package com.oxygenxml.html.convertor.purifier;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Content printer
 * @author intern4
 *
 */
public class ContentPrinter {

	/**
	 * Print the given content at the given path 
	 * @param content
	 * @param path
	 * @throws IOException
	 */
	public static void print(byte[] content, String path) throws IOException{
		FileOutputStream out = new FileOutputStream(path);
		
		out.write(content);
		
		out.close();
	}
}
