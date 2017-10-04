package com.oxygenxml.resources.batch.converter.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.proxy.ProjectPopupMenuCustomizerInvocationHandler;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ProjectManagerEditor {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ProjectManagerEditor.class);

	/**
	 * For 19.1 oxygen version add a MenuItem with given action in contextual menu
	 * of project manager. For older version than 19.1 do nothing.
	 * 
	 * @param pluginWorkspaceAccess
	 *          The StandalonePluginWorkspace.
	 * @param actions
	 *          The actions to add
	 */
	public static void addPopUpMenuCustomizer(StandalonePluginWorkspace pluginWorkspaceAccess, List<Action> actions) {
		// try to get method from 19.1 version
		try {
			// get the getProjectManager method
			Method getProjectManager = pluginWorkspaceAccess.getClass().getMethod("getProjectManager");

			// get the projectManager class
			Class projectManagerClass = getProjectManager.getReturnType();

			// get the projectPopupMenuCustomizer interface
			Class projectPopupMenuCustomizerClass = Class
					.forName("ro.sync.exml.workspace.api.standalone.project.ProjectPopupMenuCustomizer");

			// create a ProxyInstance of projectPopupMenuCustomizer
			Object proxyProjectPopupMenuCustomizerImpl = Proxy.newProxyInstance(
					projectPopupMenuCustomizerClass.getClassLoader(), new Class[] { projectPopupMenuCustomizerClass },
					new ProjectPopupMenuCustomizerInvocationHandler(pluginWorkspaceAccess, actions));

			// get the project manager object
			Object projectManager = getProjectManager.invoke(pluginWorkspaceAccess);

			// get the addPopUpMenuCustomizer method
			Method addPopUpMenuCustomizerMethod = projectManagerClass.getMethod("addPopUpMenuCustomizer",
					new Class[] { projectPopupMenuCustomizerClass });
			// invoke addPopUpMenuCustomizer method
			addPopUpMenuCustomizerMethod.invoke(projectManager, proxyProjectPopupMenuCustomizerImpl);

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	/**
	 * For 19.1 oxygen version, get the selected HTML and Markdown files in
	 * projectManager.
	 * 
	 * @param pluginWorkspaceAccess
	 * @return If oxygen version is 19.1 return a list with URLs in String format,
	 *         else return a empty list.
	 */
	public static List<String> getSelectedHtmlAndMdFiles(StandalonePluginWorkspace pluginWorkspaceAccess) {
		List<String> toReturn = new ArrayList<String>();
		
		// the input types
		List<String> inputTypes = Arrays.asList(FileExtensionType.INPUT_TYPES);
		
		try {
			// get the getProjectManager method
			Method getProjectManager = pluginWorkspaceAccess.getClass().getMethod("getProjectManager");

			// get the projectManager class
			Class projectManagerClass = getProjectManager.getReturnType();

			// get the projectManager
			Object projectManager = getProjectManager.invoke(pluginWorkspaceAccess);

			// get the getSelectedFiles method
			Method getSelectedFiles = projectManagerClass.getMethod("getSelectedFiles");

			// get the selected files
			File[] selectedFiles = (File[]) getSelectedFiles.invoke(projectManager);

			// iterate over files
			int size = selectedFiles.length;
			for (int i = 0; i < size; i++) {
					getAllHtmlAndMdFiles(selectedFiles[i], toReturn, inputTypes);
			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}

		return toReturn;
	}

	/**
	 * If given file is a directory, add all HTML and Markdown file from it in given list, else if it's a file, add it in list. 
	 * 
	 * @param file
	 *          The file of folder.
	 * @param listUrlFiles
	 *          The list.
	 */
	private static void getAllHtmlAndMdFiles(File file, List<String> listUrlFiles, List<String> inputTypes) {

		if (file.isDirectory()) {
			//it's a directory
			// get the files from folder
			File[] listOfFiles = file.listFiles();

			if (listOfFiles != null) {
				// iterate over files
				int size = listOfFiles.length;
				for (int i = 0; i < size; i++) {
					getAllHtmlAndMdFiles(listOfFiles[i], listUrlFiles, inputTypes);
				}
			}
		}
		else{
			//it's a file
			String currentFile = file.toString();
			String extension = currentFile.substring(currentFile.lastIndexOf(".") + 1);

			if (inputTypes.contains(extension)) {
				listUrlFiles.add(currentFile);
			}
		}
	}
}
