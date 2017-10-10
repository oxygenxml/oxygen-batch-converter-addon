package com.oxygenxml.resources.batch.converter.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.extensions.ExtensionGetter;
import com.oxygenxml.resources.batch.converter.proxy.ProjectPopupMenuCustomizerInvocationHandler;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Project manager editor.
 * @author intern4
 *
 */
public class ProjectManagerEditor {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ProjectManagerEditor.class);

	/**
	 * For 19.1 oxygen version add a Menu with given actions in contextual menu
	 * of project manager. For older version than 19.1 do nothing.
	 * 
	 * @param pluginWorkspaceAccess
	 *          The StandalonePluginWorkspace.
	 * @param actions
	 *          The actions to add
	 */
	public static void addPopUpMenuCustomizer(StandalonePluginWorkspace pluginWorkspaceAccess, List<Action> actions, Translator translator) {
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
					new ProjectPopupMenuCustomizerInvocationHandler(pluginWorkspaceAccess, actions, translator));

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
	 * For 19.1 oxygen version, get the selected files from
	 * projectManager according to converterType.
	 * 
	 * @param pluginWorkspaceAccess
	 * @param converterType The type of converter.
	 * @return If oxygen version is 19.1 return a list with files paths ,
	 *         else return a empty list.
	 */
	public static List<String> getSelectedFiles(StandalonePluginWorkspace pluginWorkspaceAccess, String converterType) {
		List<String> toReturn = new ArrayList<String>();
		
		// the input types
		List<String> inputTypes = Arrays.asList(ExtensionGetter.getInputExtension(converterType) );
		
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
				toReturn.addAll(getAllFile(selectedFiles[i], inputTypes));
			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}

		return toReturn;
	}

	/**
	 * Recursive search for file according to extension list.
	 * 
	 * @param file
	 *          The file or directory.
	 * @param extensions A list with searched extensions.
	 * @return A list with found files.
	 */
	private static List<String> getAllFile(File file, List<String> extensions) {
		List<String> toReturn = new ArrayList<String>(); 
		
		if (file.isDirectory()) {
			//it's a directory
			// get the files from folder
			File[] listOfFiles = file.listFiles();

			if (listOfFiles != null) {
				// iterate over files
				int size = listOfFiles.length;
				for (int i = 0; i < size; i++) {
					toReturn.addAll(getAllFile(listOfFiles[i], extensions));
				}
			}
		}
		else{
			//it's a file
			String currentFile = file.toString();
			String extension = currentFile.substring(currentFile.lastIndexOf(".") + 1);

			if (extensions.contains(extension)) {
				toReturn.add(currentFile);
			}
		}
		return toReturn;
	}
}
