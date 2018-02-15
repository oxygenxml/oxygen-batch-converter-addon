package com.oxygenxml.resources.batch.converter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.UtilAccess;
import ro.sync.util.URLUtil;

/**
 * Reader utilities.
 * 
 * @author Cosmin Duna
 *
 */
public class ConverterReaderUtils {

	/**
	 * Private constructor.
	 */
	private ConverterReaderUtils() {
    throw new IllegalStateException("Utility class");
  }
	
	/**
	 * Get the String from the given reader.
	 * 
	 * @param reader
	 *          The reader.
	 * @return The content in String format.
	 * @throws IOException
	 */
	public static String getString(Reader reader) throws IOException {

		char[] arr = new char[8 * 1024];
		StringBuilder buffer = new StringBuilder();
		int numCharsRead;
	
		while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
			buffer.append(arr, 0, numCharsRead);
		}
		reader.close();

		return buffer.toString();
	}
	
	
	/**
	 *  Create a reader from the given file.
	 *  
	 * @param file The file to be open.
	 * @return A reader form the given file.
	 * @throws IOException 
	 */
	public static Reader createReader(File file) throws IOException {
		Reader reader = null;
		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();

		if(pluginWorkspace != null) {
			UtilAccess utilAccess = pluginWorkspace.getUtilAccess();
			reader = utilAccess.createReader(URLUtil.correct(file), "UTF-8");
		} else {
			InputStream inputStream = new FileInputStream(file);
			 reader = new InputStreamReader(inputStream, "UTF-8");
		} 
		
		return reader;
	}
}
