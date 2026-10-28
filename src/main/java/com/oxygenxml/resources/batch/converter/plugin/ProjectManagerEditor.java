package com.oxygenxml.resources.batch.converter.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.proxy.ProjectPopupMenuCustomizerInvocationHandler;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * Project manager editor.
 * @author Cosmin Duna
 *
 */
public class ProjectManagerEditor {

	/**
	 * Private constructor.
	 */
  private ProjectManagerEditor() {
    throw new IllegalStateException("Utility class");
  }
	
	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProjectManagerEditor.class);

	/**
	 * For 19.1 oxygen version add a Menu with given actions in contextual menu
	 * of project manager. For older version than 19.1 do nothing.
	 * 
	 * @param pluginWorkspaceAccess
	 *          The StandalonePluginWorkspace.
	 * @param menuToAdd
	 *          The menu to add
	 */
	public static void addPopUpMenuCustomizer(StandalonePluginWorkspace pluginWorkspaceAccess, Menu menuToAdd, Translator translator) {
		// try to get method from 19.1 version
		try {
			// get the getProjectManager method
			Method getProjectManager = pluginWorkspaceAccess.getClass().getMethod("getProjectManager");

			// get the projectManager class
			Class<?> projectManagerClass = getProjectManager.getReturnType();

			// get the projectPopupMenuCustomizer interface
			Class<?> projectPopupMenuCustomizerClass = Class
					.forName("ro.sync.exml.workspace.api.standalone.project.ProjectPopupMenuCustomizer");

			// create a ProxyInstance of projectPopupMenuCustomizer
			Object proxyProjectPopupMenuCustomizerImpl = Proxy.newProxyInstance(
					projectPopupMenuCustomizerClass.getClassLoader(), new Class[] { projectPopupMenuCustomizerClass },
					new ProjectPopupMenuCustomizerInvocationHandler(pluginWorkspaceAccess, menuToAdd));

			// get the project manager object
			Object projectManager = getProjectManager.invoke(pluginWorkspaceAccess);

			// get the addPopUpMenuCustomizer method
			Method addPopUpMenuCustomizerMethod = projectManagerClass.getMethod("addPopUpMenuCustomizer",
					projectPopupMenuCustomizerClass );
			// invoke addPopUpMenuCustomizer method
			addPopUpMenuCustomizerMethod.invoke(projectManager, proxyProjectPopupMenuCustomizerImpl);

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	/**
	 * For 19.1 oxygen version, get the selected files from
	 * projectManager according to converterType.
	 *
	 * @param converterType The type of converter.
	 * @return The manager with the files to convert and the folders they were collected from.
	 *         It is empty for older versions than 19.1.
	 */
	public static InputFilesManager getSelectedFiles(String converterType) {
		InputFilesManager toReturn = new InputFilesManager();
		try {
		  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
			// get the getProjectManager method
			Method getProjectManager = pluginWorkspace.getClass().getMethod("getProjectManager");

			// get the projectManager class
			Class<?> projectManagerClass = getProjectManager.getReturnType();

			// get the projectManager
			Object projectManager = getProjectManager.invoke(pluginWorkspace);

			// get the getSelectedFiles method
			Method getSelectedFiles = projectManagerClass.getMethod("getSelectedFiles");

			// get the selected files
			File[] selectedFiles = (File[]) getSelectedFiles.invoke(projectManager);
			toReturn = collectInputFiles(selectedFiles, converterType);

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return toReturn;
	}

	/**
	 * Collect the files to convert from the given selected resources. The files from a selected folder
	 * are associated with that folder, so its structure is preserved in the output.
	 *
	 * @param selectedFiles The selected files and folders.
	 * @param converterType The type of converter.
	 * @return The manager with the collected files.
	 */
	static InputFilesManager collectInputFiles(File[] selectedFiles, String converterType) {
		List<String> inputTypes = Arrays.asList(ExtensionGetter.getInputExtension(converterType));
		InputFilesManager inputFilesManager = new InputFilesManager();
		for (File selectedFile : selectedFiles) {
			List<File> files = ConverterFileUtils.getAllFiles(selectedFile, inputTypes);
			if (selectedFile.isDirectory()) {
				inputFilesManager.addFilesFromFolder(files, selectedFile);
			} else {
				inputFilesManager.addFiles(files);
			}
		}
		return inputFilesManager;
	}

}
