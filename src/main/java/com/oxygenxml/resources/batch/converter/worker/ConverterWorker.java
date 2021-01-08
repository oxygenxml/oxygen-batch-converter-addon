package com.oxygenxml.resources.batch.converter.worker;

import java.io.File;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.BatchConverterInteractor;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.reporter.OxygenProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.OxygenStatusReporter;
import com.oxygenxml.resources.batch.converter.reporter.ResultsUtil;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.trasformer.OxygenTransformerFactoryCreator;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;

/**
 * Worker that execute the conversion.
 * @author Cosmin Duna
 *
 */
public class ConverterWorker extends SwingWorker<Void, Void> implements ConvertorWorkerInteractor {
  /**
	 * Provider for the inputs data.
	 */
	private UserInputsProvider inputsProvider;
	/**
	 * Status reporter.
	 */
	private OxygenStatusReporter oxygenStatusReporter;
	/**
	 * Translator
	 */
	private Translator translator;
	
	/**
	 * Report for the status of the conversion process.
	 */
	private ConverterStatusReporter converterStatusReporter;
	/**
	 * Problem reporter.
	 */
	private OxygenProblemReporter oxygenProblemReporter;
	/**
	 * Converter type
	 */
	private String converterType;
	
	/**
	 * Constructor
	 * 
	 * @param converterType            The type of converter.
	 * @param inputsProvider           Provider for the inputs data
	 * @param progressDialogInteractor A progress dialog interactor.
	 */
	public ConverterWorker(String converterType, UserInputsProvider inputsProvider,
			ConverterStatusReporter converterStatusReporter) {
		this.converterType = converterType;
		this.inputsProvider = inputsProvider;
		this.converterStatusReporter = converterStatusReporter;
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
		oxygenStatusReporter.setStatusMessage(translator.getTranslation(Tags.PROGRESS_STATUS));
		List<File> convertedFiles = null;
		try {
		  converterStatusReporter.conversionStarts();

		  //delete reported problems from other conversion
		  ResultsUtil.deleteReportedProblems();

		  //create the converter
		  BatchConverter convertor = new BatchConverterImpl(oxygenProblemReporter, oxygenStatusReporter, converterStatusReporter, this,
		      new OxygenTransformerFactoryCreator());

		  //convert the files
		  convertedFiles = convertor.convertFiles(converterType, inputsProvider);

		  //refresh the output folder from the project manager.
		  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();		
		  final ProjectController projectManager = ((StandalonePluginWorkspace)pluginWorkspace).getProjectManager();

		  //refresh the project manager parent directory
		  SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		      projectManager.refreshFolders(new File[]{inputsProvider.getOutputFolder().getParentFile()});

		    }
		  });
		  
		} finally {
		  // Notify that the conversion has finished
		  converterStatusReporter.conversionHasFinished(convertedFiles, inputsProvider.getOutputFolder());
    }

		return null;
	}
}
