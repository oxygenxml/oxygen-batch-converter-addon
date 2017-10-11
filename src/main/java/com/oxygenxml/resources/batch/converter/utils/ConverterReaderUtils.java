package com.oxygenxml.resources.batch.converter.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * Reader utilities.
 * 
 * @author intern4
 *
 */
public class ConverterReaderUtils {

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
}
