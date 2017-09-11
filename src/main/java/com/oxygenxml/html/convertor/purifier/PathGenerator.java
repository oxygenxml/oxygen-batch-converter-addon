package com.oxygenxml.html.convertor.purifier;


public class PathGenerator {

	public static String generate(String baseInputFolder ,String filePath, String outputFolder){
		
		System.out.println(baseInputFolder + " base");
		System.out.println(filePath+ " path");
		
		System.out.println("index: " + filePath.contains(baseInputFolder));
		
		System.out.println("path gen: "+ outputFolder + filePath.substring(filePath.lastIndexOf(baseInputFolder)) );
		
	return outputFolder + filePath.substring(filePath.lastIndexOf(baseInputFolder));
	}

}
