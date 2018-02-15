package com.oxygenxml.resources.batch.converter.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * File utilities. 
 * @author Cosmin Duna
 *
 */
public class ConverterFileUtils {
	
	/**
	 * Private constructor.
	 */
	 private ConverterFileUtils() {
	    throw new IllegalStateException("Utility class");
	  }
	
	/**
	 * Read the content of given file.
	 * 
	 * @param file
	 *          The file.
	 * @return The content in String format.
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		String toReturn = "";
		Reader reader = ConverterReaderUtils.createReader(file);
		toReturn = ConverterReaderUtils.getString(reader);

		return toReturn;
	}


	/**
	 * Recursive search for file according to extension list.
	 * 
	 * @param file
	 *          The file or directory.
	 * 
	 * @param extensionsFiles
	 *          The extensions.
	 */ 
	public static List<File> getAllFiles(File file, List<String> extensionsFiles) {

		List<File> toReturn = new ArrayList<File>();

		if (file.isDirectory()) {
			// the given file is a directory.
			// get the files from folder
			File[] listOfFiles = file.listFiles();
			
			if (listOfFiles != null) {
				// iterate over files
				int size = listOfFiles.length;
				for (int i = 0; i < size; i++) {
					toReturn.addAll(getAllFiles(listOfFiles[i], extensionsFiles));
				}
			}
			
		} else {
			// get the fileName
			String fileName = file.getName();
			// get the extension
			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

			// check the extension
			if (extensionsFiles.contains(extension)) {
				toReturn.add(file);
			}
		}

		return toReturn;
	}

	/**
	 * Generate a File according to output folder, extension and origin file.
	 * 
	 * @param filePath
	 * @param extension
	 * @param outputFolder
	 * @return The generated file.
	 */
	public static File generateOutputFile(File originalFile, String extension, File outputFolder) {
		String fileName = originalFile.getName();

		// get the file name without extension
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));

		return  new File(outputFolder.getAbsolutePath() + File.separator + fileName + '.' + extension);
	}

	/**
	 * Add a counter on the given file if it exists.
	 * 
	 * @param file
	 *          The file.
	 * @return A unique file.
	 */
	public static File getFileWithCounter(File file) {

		String filePath = file.getAbsolutePath();
		int idOfDot = filePath.indexOf('.');
		String name = filePath.substring(0, idOfDot);
		String extension = filePath.substring(idOfDot + 1);
		int counter = 1;

		while (file.exists()) {

			filePath = name + '(' + counter + ")." + extension;
			file = new File(filePath);
			counter++;
		}

		return file;
	}

}
