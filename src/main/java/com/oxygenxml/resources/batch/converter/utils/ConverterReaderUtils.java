package com.oxygenxml.resources.batch.converter.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * Reader utilities.
 * @author intern4
 *
 */
public class ConverterReaderUtils {

	/**
	 * Get the String from the given reader.
	 * @param reader The reader.
	 * @return The content in String format.
	 * @throws IOException
	 */
	public static String getString(Reader reader) throws IOException{
		String toReturn = ""; 
		
		int intValueOfChar;
		while ((intValueOfChar = reader.read()) != -1) {
			toReturn += (char) intValueOfChar;
		}
		
		return toReturn;
	}
}
