package com.oxygenxml.resources.batch.converter.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oxygenxml.resources.batch.converter.dmm.InsertTopicRefUtil;
import com.oxygenxml.resources.batch.converter.dmm.InsertType;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.view.ConverterDialog;

import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;

/**
 * Action that converts documents and import the resulted files in DMM.
 * 
 * @author cosmin_duna
 */
public class ImportAction extends ConvertActionBase{
  /**
   * The type of insertion to be made in DMM after conversion.
   */
  private InsertType dmmInsertType;
  
  /**
   * Translator for messages
   */
  private Translator translator;

  /**
   * Constructor.
   * 
   * @param converterType The type of converter
   * @param dmmInsertType The type of insertion to be made in dmm after conversion.
   * @param translator    Translator for messages
   */
  public ImportAction(final String converterType, final InsertType insertType, Translator translator) {
    super(converterType, translator);
        this.dmmInsertType = insertType;
        this.translator = translator;
  }
  
  /**
   * Get the imposed input files.
   * 
   * @param actionEvent The action event
   * 
   * @return the imposed input files
   */
  @Override
  public List<File> getImposedInputFiles(ActionEvent actionEvent) {
    return new ArrayList<File>();
  }
  
  /**
   * Get the imposed output directory.
   * 
   * @return the imposed output directory.
   */
  @Override
  public File getImposedOutputDirectory() {
    File outputdir = null;
    WSEditor currentEditorAccess = PluginWorkspaceProvider.getPluginWorkspace().getCurrentEditorAccess(PluginWorkspace.DITA_MAPS_EDITING_AREA);
    if(currentEditorAccess != null) {
      WSEditorPage currentPage = currentEditorAccess.getCurrentPage();
      if(currentPage instanceof WSDITAMapEditorPage) {
        WSDITAMapEditorPage dmmPage = (WSDITAMapEditorPage) currentPage;
        AuthorNode[] selectedNodes = dmmPage.getSelectedNodes(true);
        if(selectedNodes.length > 0) {
          try {
            outputdir = new File(selectedNodes[0].getXMLBaseURL().toURI()).getParentFile();
          } catch (URISyntaxException e) {
            // Do nothing
          }
        }
      }
    }
    return outputdir;
  }
  
  /**
   * Customize the converter dialog.
   * 
   * @param converterDialog The converter dialog.
   */
  @Override
  public void customizeDialog(ConverterDialog converterDialog) {
    converterDialog.setOkButtonText(translator.getTranslation(Tags.IMPORT));
  }
  
  /**
   * Execute the conversion post-processing
   * 
   * @param convertedDocuments The converted documents.
   * @param outputDir          The output directory that contains the converted documents.
   */
  @Override
  public void conversionFinished(List<File> convertedDocuments, File outputDir) {
    if(dmmInsertType != InsertType.NO_INSERT && !convertedDocuments.isEmpty()) {
      WSEditor dmmEditor = PluginWorkspaceProvider.getPluginWorkspace().getCurrentEditorAccess(PluginWorkspace.DITA_MAPS_EDITING_AREA);
      InsertTopicRefUtil.insertTopicReferencesInDMM(convertedDocuments, dmmInsertType, dmmEditor);
    }
  }

}
