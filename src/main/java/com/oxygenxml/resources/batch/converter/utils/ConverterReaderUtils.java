package com.oxygenxml.resources.batch.converter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

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
	 *  Logger.
	 */
	private static final Logger logger = Logger.getLogger(ConverterReaderUtils.class);
	
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
	
		try {
			while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
				buffer.append(arr, 0, numCharsRead);
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
			}
		}

		return buffer.toString();
	}
	
	
	/**
	 *  Create a reader from the given file.
	 *  
	 * @param file The file to be open.
	 * @return A reader form the given file.
	 * @throws MalformedURLException 
	 * @throws IOException 
	 */
	public static Reader createReader(File file) throws IOException {
		Reader reader = null;
		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();

		if(pluginWorkspace != null) {
			UtilAccess utilAccess = pluginWorkspace.getUtilAccess();
			reader = utilAccess.createReader(URLUtil.correct(file), "UTF-8"); //NOSONAR
		} else {
			InputStream inputStream = new FileInputStream(file);
			 try {
				reader = new InputStreamReader(inputStream, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					logger.debug(e1.getMessage(), e1);
				}
				throw e;
			}
		} 
		
		return reader;
	}
}
