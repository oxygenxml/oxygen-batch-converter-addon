package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.net.URL;

/**
 * File path generator
 * @author intern4
 *
 */
//TODO add ConverterFileUtils here.
public class FilePathGenerator {

	/**
	 * Generate a File according to output folder, extension and origin file.
	 * @param filePath
	 * @param extension
	 * @param outputFolder
	 * @return
	 */
	public static File generate(URL fileURL, String extension, String outputFolder){
		
	String fileName = fileURL.toString();

	//get the file name without extension
	fileName = fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf(".") );
	
	File toReturn = new File(outputFolder +  File.separator + fileName +"."+ extension);
	
	return toReturn;
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
		int idOfDot = filePath.indexOf(".");
		String name = filePath.substring(0, idOfDot);
		String extension = filePath.substring(idOfDot + 1);
		int counter = 1;

		while (file.exists()) {

			filePath = name + "(" + counter + ")." + extension;
			file = new File(filePath);
			counter++;
		}

		return file;
	}
}
