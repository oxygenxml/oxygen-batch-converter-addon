package com.oxygenxml.html.convertor.view;

import java.io.File;
import java.util.List;

public class InputPanelUtil {

	/**
	 * Get all urls of html or markdown files from given folder and add in given
	 * list
	 * 
	 * @param folder
	 *          The file of folder.
	 * @param listUrlFiles
	 *          The list.
	 */
	public static void getFilesFromFolder(File folder, List<String> listUrlFiles) {
		// get the files from folder
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {

			// iterate over files
			int size = listOfFiles.length;
			for (int i = 0; i < size; i++) {

				System.out.println(listOfFiles[i].toString());
				
				// check if is a file
				if (listOfFiles[i].isFile()) {
					String currentfile = listOfFiles[i].getPath();
					
					try{
						String extension = currentfile.substring(currentfile.lastIndexOf(".")+1);
						// check the extension
						if ("html".equals(extension) || "md".equals(extension)) {
							System.out.println("gaseste fisier******************");
							listUrlFiles.add(currentfile);
						}
					}
					catch (Exception e) {
					}

					// check if is a directory
				} else if (listOfFiles[i].isDirectory()) {
					getFilesFromFolder(listOfFiles[i], listUrlFiles);
				}
			}
		}

	}
}