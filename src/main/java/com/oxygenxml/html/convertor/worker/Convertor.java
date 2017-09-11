package com.oxygenxml.html.convertor.worker;

import java.io.File;
import java.util.Stack;

import com.oxygenxml.html.convertor.purifier.ContentPrinter;
import com.oxygenxml.html.convertor.purifier.HtmlPurifier;
import com.oxygenxml.html.convertor.purifier.PathGenerator;

public class Convertor {
	
	HtmlPurifier htmlPurifier = new HtmlPurifier();
	
	boolean containsHTML = false;

	private String baseInputFolder;
	
	public Convertor(String inputFolder) {
		this.baseInputFolder = inputFolder;
		
	}
	
	public void convertFiles(String inputFolder, String outputFolder){
		
		//input directory
		File input = new File(inputFolder);
		
		//get the files from input directory
		File[] listOfFiles = input.listFiles();

			//iterate over files 
			int size = listOfFiles.length;
			for (int i = 0; i < size; i++) {
				
				
				System.out.println(i +" "+ listOfFiles[i].getParent()  );
				//check if is a file
				if (listOfFiles[i].isFile()) {
					String filePath = listOfFiles[i].getPath();
					
					try {
						// check if is a html file
						if (filePath.substring(filePath.lastIndexOf(".")).contains("html")) {
							containsHTML = true;

							byte data[] = htmlPurifier.createWellFormedContent("file:/"+filePath); 
							
							ContentPrinter.print(data, PathGenerator.generate(baseInputFolder, filePath, outputFolder));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//check if is a directory
				} else if (listOfFiles[i].isDirectory()) {
					convertFiles(listOfFiles[i].toString(), outputFolder);
				}
				
			}

	}
	
	
	public boolean wasContainsHtml(){
		return containsHTML;
	}
}
