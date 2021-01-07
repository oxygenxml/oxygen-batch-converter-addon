package com.oxygenxml.resources.batch.converter.worker;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.dmm.InsertType;
import com.oxygenxml.resources.batch.converter.reporter.OxygenProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.OxygenStatusReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.reporter.ResultsUtil;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.trasformer.OxygenTransformerFactoryCreator;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;
import ro.sync.util.URLUtil;

/**
 * Worker that execute the conversion.
 * @author Cosmin Duna
 *
 */
public class ConverterWorker extends SwingWorker<Void, Void> implements ConvertorWorkerInteractor {
  /**
   * Logger
   */
   private static final Logger logger = Logger.getLogger(ConverterWorker.class);
   
  /**
	 * Batch converter interactor.
	 */
	private BatchConverterInteractor convertorInteractor;
	/**
	 * Status reporter.
	 */
	private OxygenStatusReporter oxygenStatusReporter;
	/**
	 * Translator
	 */
	private Translator translator;
	
	/**
	 * Progress dialog interactor.
	 */
	private ProgressDialogInteractor progressDialogInteractor;
	/**
	 * Problem reporter.
	 */
	private OxygenProblemReporter oxygenProblemReporter;
	/**
	 * Converter type
	 */
	private String converterType;
	
	/**
   * The type of insertion to be made in DMM after conversion.
   */
  private InsertType dmmInsertType;
	
	/**
	 * Constructor
	 * 
	 * @param converterType The type of converter.
	 * @param dmmInsertType The type of insertion to be made in DMM after conversion.
	 * @param convertorInteractor A converter interactor.
	 * @param progressDialogInteractor A progress dialog interactor.
	 */
	public ConverterWorker(String converterType, InsertType dmmInsertType, BatchConverterInteractor convertorInteractor,
			ProgressDialogInteractor progressDialogInteractor) {
		this.converterType = converterType;
    this.dmmInsertType = dmmInsertType;
		this.convertorInteractor = convertorInteractor;
		this.progressDialogInteractor = progressDialogInteractor;
		oxygenStatusReporter = new OxygenStatusReporter();
		oxygenProblemReporter = new OxygenProblemReporter();
		translator = new OxygenTranslator();
	}


	/**
	 * Convert the files.
	 * Note: this method is executed in a background thread.
	 */
	@Override
	protected Void doInBackground() {
		//report the progress status
		oxygenStatusReporter.setStatusMessage(translator.getTranslation(Tags.PROGRESS_STATUS, ""));
		try {
		  //set the progress dialog visible 
		  progressDialogInteractor.setDialogVisible(true);

		  //delete reported problems from other conversion
		  ResultsUtil.deleteReportedProblems();

		  //create the converter
		  BatchConverter convertor = new BatchConverterImpl(oxygenProblemReporter, oxygenStatusReporter, progressDialogInteractor, this,
		      new OxygenTransformerFactoryCreator());

		  //convert the files
		  List<File> convertedFiles = convertor.convertFiles(converterType,	convertorInteractor);

		  //refresh the output folder from the project manager.
		  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();		
		  final ProjectController projectManager = ((StandalonePluginWorkspace)pluginWorkspace).getProjectManager();

		  //refresh the project manager parent directory
		  SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		      projectManager.refreshFolders(new File[]{convertorInteractor.getOutputFolder().getParentFile()});

		    }
		  });
		  
		  if(dmmInsertType != InsertType.NO_INSERT && !convertedFiles.isEmpty()) {
		    insertTopicReferencesInDMM(convertedFiles, pluginWorkspace);
		  }

		} finally {
		  //close the progress dialog
		  progressDialogInteractor.close();
    }

		return null;
	}


	/**
	 * Insert topic references at the given converted files into the page opened in DMM.
	 * 
	 * @param convertedFiles     The converted files
	 * @param pluginWorkspace    The plugin workspace
	 */
  private void insertTopicReferencesInDMM(List<File> convertedFiles, PluginWorkspace pluginWorkspace) {
    WSEditor currentEditorAccess = pluginWorkspace.getCurrentEditorAccess(PluginWorkspace.DITA_MAPS_EDITING_AREA);
    if(currentEditorAccess != null) {
      WSEditorPage currentPage = currentEditorAccess.getCurrentPage();
      if(currentPage instanceof WSDITAMapEditorPage) {
        WSDITAMapEditorPage dmmPage = (WSDITAMapEditorPage) currentPage;
        AuthorDocumentController documentController = dmmPage.getDocumentController();
        AuthorNode[] selectedNodes = dmmPage.getSelectedNodes(true);
        if(selectedNodes.length > 0) {
          AuthorNode targetNode = selectedNodes[0];
          
          URL baseURL = targetNode.getXMLBaseURL();
          if(dmmInsertType == InsertType.INSERT_BEFORE || dmmInsertType == InsertType.INSERT_AFTER) {
            AuthorNode parent = targetNode.getParent();
            if(parent != null) {
              baseURL = parent.getXMLBaseURL();
            }
          }
          try {
            documentController.beginCompoundEdit();
            String xmlFragment = createTopicReferencesFragment(convertedFiles, baseURL);
            documentController.insertXMLFragment(xmlFragment, targetNode, dmmInsertType.getOxyConstant());
          } catch (AuthorOperationException e) {
            oxygenProblemReporter.reportProblem(e, null);
          } finally {
            documentController.endCompoundEdit();
          }
        }
      }
    }
  }

  /**
   * Create a XML fragment with topic references at the given files.
   * @param files     The files to be refered.
   * @param baseURL   The base URL.
   * 
   * @return The XML fragment with topic references at the given files.
   */
  private String createTopicReferencesFragment(List<File> files, URL baseURL) {
    StringBuilder topicRefsFragment = new StringBuilder();
    for (File file : files) {
      String relativePath = file.getAbsolutePath();
      try {
        relativePath = URLUtil.makeRelative(baseURL, file.toURI().toURL());
      } catch (MalformedURLException e) {
        // We will keep the absolute path
        logger.debug(e.getMessage(), e);
      }
      topicRefsFragment.append("<topicref href=\"").append(relativePath).append("\"");
      if(relativePath.endsWith("ditamap")) {
        topicRefsFragment.append(" format=\"ditamap\"");
      }
      topicRefsFragment.append("/>");
    }
    return topicRefsFragment.toString();
  }
}
