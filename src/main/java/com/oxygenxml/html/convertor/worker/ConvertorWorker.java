package com.oxygenxml.html.convertor.worker;

import javax.swing.SwingWorker;

public class ConvertorWorker extends SwingWorker<Void, Void> {

	private String inputFolder;
	private String outputFolder;
	private Convertor convertor;
	
	
	public ConvertorWorker(String inputFolder, String outputFolder) {
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
		convertor = new Convertor(inputFolder);
	}
	
	@Override
	protected Void doInBackground()  {
		
		convertor.convertFiles(inputFolder, outputFolder);
		
		return null;
	}

}
