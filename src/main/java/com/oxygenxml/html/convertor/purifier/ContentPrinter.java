package com.oxygenxml.html.convertor.purifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

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
	public static void print(byte[] content, String outputPath) throws IOException{
	
		File outputFile = new File(outputPath);
		
		if(!outputFile.getParentFile().exists()){
			outputFile.getParentFile().mkdirs();
		}
		
		FileOutputStream out = new FileOutputStream(outputPath);
		
		out.write(content);
		
		out.close();
	}
}
