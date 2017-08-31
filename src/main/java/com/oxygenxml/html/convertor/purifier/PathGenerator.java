package com.oxygenxml.html.convertor.purifier;

public class PathGenerator {

	public static String generate(String filePath, String folder){
		
	return folder + "/XHTML"+ filePath.substring(filePath.lastIndexOf("/")+1);
	}

}
