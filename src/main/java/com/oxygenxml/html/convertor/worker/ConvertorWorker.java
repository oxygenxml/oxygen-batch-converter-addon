package com.oxygenxml.html.convertor.worker;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import com.oxygenxml.html.convertor.purifier.ContentPrinter;
import com.oxygenxml.html.convertor.purifier.HtmlPurifier;
import com.oxygenxml.html.convertor.purifier.PathGenerator;

public class ConvertorWorker extends SwingWorker<Void, Void> {

	
	private List<String> filesUrls;
	private String folderPath;
	private HtmlPurifier htmlPurifier = new HtmlPurifier();

	public ConvertorWorker(List<String> filesPaths, String folderPath) {
		this.filesUrls = filesPaths;
		this.folderPath = folderPath;
	}
	
	
	@Override
	protected Void doInBackground()  {
		byte purifiedData[];
		
		int size = filesUrls.size();
		
		for(int i = 0; i < size; i++){
			try {
				purifiedData = htmlPurifier.createWellFormedContent(filesUrls.get(i));
			
			
			String savePath = PathGenerator.generate(filesUrls.get(i), folderPath);
			
			
			ContentPrinter.print(purifiedData, savePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return null;
	}

}
