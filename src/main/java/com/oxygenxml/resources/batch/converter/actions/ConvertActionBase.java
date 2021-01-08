package com.oxygenxml.resources.batch.converter.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.view.ConverterDialog;
import com.oxygenxml.resources.batch.converter.view.ProgressDialog;
import com.oxygenxml.resources.batch.converter.worker.ConverterStatusReporter;
import com.oxygenxml.resources.batch.converter.worker.ConverterWorker;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Base the action that convert documents.
 * 
 * @author cosmin_duna
 */
public abstract class ConvertActionBase extends AbstractAction {

  /**
   * The type of converter
   */
  private String converterType;
  
  /**
   * Translator for messages
   */
  private Translator translator;

  /**
   * Constructor
   * 
   * @param converterType The type of converter
   * @param translator    Translator for messages
   */
  public ConvertActionBase(final String converterType, Translator translator) {
    super(translator.getTranslation(Tags.MENU_ITEM_TEXT + converterType)+ "...");
        this.converterType = converterType;
        this.translator = translator;
  }
  
  /**
   * Perform the conversion process
   * @param e The event that perform the action
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    List<File> imposedInputFile = getImposedInputFiles(e);
    File imposedOutputDirectory = getImposedOutputDirectory();

    JFrame parentFrame = (JFrame) PluginWorkspaceProvider.getPluginWorkspace().getParentFrame();
    final ConverterDialog converterDialog = new ConverterDialog(
        converterType, imposedInputFile, imposedOutputDirectory,
        parentFrame, translator);

    customizeDialog(converterDialog);
    converterDialog.showDialog();
    
    //create a progress dialog
    final ProgressDialog progressDialog = new ProgressDialog(parentFrame, translator, converterType);

    //create a converter worker.
    final ConverterWorker converterWorker = new ConverterWorker(converterType,  converterDialog,  new ConverterStatusReporter() {
      @Override
      public void conversionStarts() {
        //set the progress dialog visible 
        progressDialog.setDialogVisible(true);
      }
      
      @Override
      public void conversionStartsFor(File inputFile) {
        progressDialog.conversionInProgress(inputFile);
      }
      
      @Override
      public void conversionHasFinished(List<File> resultedDocuments, File outputDir) {
        progressDialog.close();
        executeConversionPostprocessing(resultedDocuments, outputDir);
      }
    });

    //add a action listener on cancel button for progress dialog
    progressDialog.addCancelActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        converterWorker.cancel(true);
        progressDialog.dispose();
      }
    });

    //start the worker.
    converterWorker.execute();
  }
  
 /**
  * Get the imposed input files.
  * 
  * @param actionEvent The action event
  * 
  * @return the imposed input files
  */
 public abstract List<File> getImposedInputFiles(ActionEvent actionEvent);

 /**
  * Get the imposed output directory.
  * 
  * @return the imposed output directory.
  */
 public abstract File getImposedOutputDirectory();
 
 /**
  * Customize the converter dialog.
  * 
  * @param converterDialog The converter dialog.
  */
 public abstract void customizeDialog(ConverterDialog converterDialog);
 
 /**
  * Execute the conversion post-processing
  * 
  * @param convertedDocuments The converted documents.
  * @param outputDir          The output directory that contains the converted documents.
  */
 public abstract void executeConversionPostprocessing(List<File> convertedDocuments, File outputDir);
}
