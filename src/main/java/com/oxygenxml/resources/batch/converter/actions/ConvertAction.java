package com.oxygenxml.resources.batch.converter.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.oxygenxml.resources.batch.converter.plugin.PluginMenusInteractor;
import com.oxygenxml.resources.batch.converter.plugin.ProjectManagerEditor;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.view.ConverterDialog;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;

/**
 * Action for converting documents.
 * 
 * @author cosmin_duna
 */
public class ConvertAction extends ConvertActionBase {
  
  /**
   * The type of converter.
   */
  private String converterType;
  
  /**
   * Interactor for menus added by add-on.
   */
  private PluginMenusInteractor menuInteractor;
  
  /**
   * <code>true</code> when action is invoked from the contextul menu of the project view,
   *  <code>false</code> when is invoked from other places 
   */
  private boolean isInvokedFromProject = false;
  
  /**
   * Constructor.
   * 
   * @param converterType   The type of converter.
   * @param translator      Translator for messages
   * @param menuInteractor  Interactor for menus added by add-on.
   */ 
  public ConvertAction(final String converterType,
      Translator translator, PluginMenusInteractor menuInteractor) {
    super(converterType, translator);
        this.converterType = converterType;
        this.menuInteractor = menuInteractor;
  }

  /**
   * Get the imposed input files.
   * 
   * @param actionEvent The action event
   * 
   * @return the imposed input files
   */
  @Override
  public List<File> getImposedInputFiles(ActionEvent event) {
    List<File> selectedFile = new ArrayList<File>();
    if (menuInteractor.isInvokedFromProjectMenu(event)) {
      isInvokedFromProject = true;
      // get the selectedFile from ProjectManager
      selectedFile = ProjectManagerEditor.getSelectedFiles(converterType);
    } else {
      isInvokedFromProject = false;
    }
    return selectedFile;
  }
  
  /**
   * Get the imposed input files.
   * 
   * @param actionEvent The action event
   * 
   * @return the imposed input files
   */
  @Override
  public File getImposedOutputDirectory() {
    return null;
  }
  
  /**
   * Execute the conversion post-processing
   * 
   * @param convertedDocuments The converted documents.
   * @param outputDir          The output directory that contains the converted documents.
   */
  @Override
  public void conversionFinished(List<File> convertedDocuments, final File outputDir) {
    if(isInvokedFromProject) {
      //refresh the output folder from the project manager.
      PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();   
      final ProjectController projectManager = ((StandalonePluginWorkspace)pluginWorkspace).getProjectManager();
      
      //refresh the project manager parent directory
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          projectManager.refreshFolders(new File[]{outputDir.getParentFile()});
          
        }
      });
    }
  }

  /**
   * Customize the converter dialog.
   * 
   * @param converterDialog The converter dialog.
   */
  @Override
  public void customizeDialog(ConverterDialog converterDialog) {
    //  Do nothing
  }
}
