package com.oxygenxml.resources.batch.converter.view;

import java.io.File;
import java.util.List;

public class ConverterFileUtil {

	/**
	 * Get all urls of html or markdown files from given folder and add in given
	 * list
	 * 
	 * @param folder
	 *          The file of folder.
	 * @param listUrlFiles
	 *          The list.
	 */
	//TODO add javadoc ; add a return type
	public static void getFilesFromFolder(File folder, List<String> listUrlFiles , List<String> extensionsFiles) {
		// get the files from folder
		File[] listOfFiles = folder.listFiles();
		
		if (listOfFiles != null) {

			// iterate over files
			int size = listOfFiles.length;
			for (int i = 0; i < size; i++) {

				// check if is a file
				if (listOfFiles[i].isFile()) {
					String currentfile = listOfFiles[i].getPath();

					String extension = currentfile.substring(currentfile.lastIndexOf(".") + 1);
					// check the extension
					if (extensionsFiles.contains(extension)) {
						listUrlFiles.add(currentfile);
					}

					// check if is a directory
				} else if (listOfFiles[i].isDirectory()) {
					getFilesFromFolder(listOfFiles[i], listUrlFiles, extensionsFiles);
				}
			}
		}

	}
}