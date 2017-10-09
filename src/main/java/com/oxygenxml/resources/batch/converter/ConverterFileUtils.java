package com.oxygenxml.resources.batch.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConverterFileUtils {

	/**
	 * Read the content of given file.
	 * 
	 * @param file
	 *          The file.
	 * @return The content in String format.
	 * @throws IOException
	 */
	public static String readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	/**
	 * Cast a list of String to a list of File.
	 * 
	 * @param list
	 * @return
	 */
	public static List<File> convertToFile(List<String> list) {
		List<File> toReturn = new ArrayList<File>();
		String currentElement;
		File currentFile;

		int size = list.size();
		for (int i = 0; i < size; i++) {
			currentElement = list.get(i);

			currentFile = new File(currentElement);
			toReturn.add(currentFile);

		}
		return toReturn;
	}

	/**
	 * Get all urls of html or markdown files from given folder and add in given
	 * list
	 * 
	 * @param folder
	 *          The file of folder.
	 * @param listUrlFiles
	 *          The list.
	 */
	// TODO add javadoc ; add a return type
	public static void getFilesFromFolder(File folder, List<String> listUrlFiles, List<String> extensionsFiles) {
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

	/**
	 * Generate a File according to output folder, extension and origin file.
	 * 
	 * @param filePath
	 * @param extension
	 * @param outputFolder
	 * @return
	 */
	public static File generateOutputFile(File originalFile, String extension, File outputFolder) {
		String fileName = originalFile.getName();

		// get the file name without extension
		fileName = fileName.substring(0, fileName.lastIndexOf("."));

		File toReturn = new File(outputFolder.getAbsolutePath() + File.separator + fileName + "." + extension);

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
