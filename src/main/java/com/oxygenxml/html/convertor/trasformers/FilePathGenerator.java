package com.oxygenxml.html.convertor.trasformers;

import java.io.File;

public class FilePathGenerator {

	public static File generate(String filePath, String extension, String outputFolder){
		
	String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1, filePath.lastIndexOf("."));
		
	File toReturn = new File(outputFolder + "/" + fileName +"."+ extension);
	
	return toReturn;
	}

	
	/**
	 * Add a counter on the given file if exists.
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
