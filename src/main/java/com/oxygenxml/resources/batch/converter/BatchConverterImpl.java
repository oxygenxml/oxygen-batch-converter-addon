package com.oxygenxml.resources.batch.converter;

import java.net.URL;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.converters.Converter;
import com.oxygenxml.resources.batch.converter.converters.ConverterCreator;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinterXhtmlAndDita;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.worker.ConvertorWorkerInteractor;

public class BatchConverterImpl implements BatchConverter{

	private ProblemReporter problemReporter;
	private ProgressDialogInteractor progressDialogInteractor;
	private ConvertorWorkerInteractor workerInteractor;
	private TransformerFactoryCreator transformerFactoryCreator;

	public BatchConverterImpl(ProblemReporter problemReporter, ProgressDialogInteractor progressDialogInteractor, ConvertorWorkerInteractor workerInteractor, TransformerFactoryCreator transformerFactoryCreator) {
		this.problemReporter = problemReporter;
		this.progressDialogInteractor = progressDialogInteractor;
		this.workerInteractor = workerInteractor;
		this.transformerFactoryCreator = transformerFactoryCreator;
	}
	
	@Override
	public boolean convertFiles(String converterType, List<URL> inputFiles, String outputFolder) {
	//flag to return 
			boolean isSuccessfully = true;
			//content to print
			String contentToPrint = "";
			
			Converter transformer = ConverterCreator.create(converterType);
			
			// iterate over files
			int size = inputFiles.size();
			for (int i = 0; i < size; i++) {
				
				//check if worker was interrupted
				if(workerInteractor.isCancelled()){
					//break the loop
					break;
				}
				
				// get the current file.
				URL currentFileUrl = inputFiles.get(i);

				//update the node in progress dialog.
				progressDialogInteractor.setNote(currentFileUrl.toString());
				
				try {
					
					//convert 
					 contentToPrint =  transformer.convert(currentFileUrl, null, transformerFactoryCreator);
				
					//TODO make a contentPrinterCreater;
					ContentPrinter contentPrinter = new ContentPrinterXhtmlAndDita();
					
					//print the converted content.
					contentPrinter.prettifyAndPrint(contentToPrint, transformerFactoryCreator, currentFileUrl, outputFolder, converterType);
					
					
				} catch (TransformerException e) {
					e.printStackTrace();
					problemReporter.reportProblem(e, currentFileUrl.toString());
					isSuccessfully = false;
				}
			}
			return isSuccessfully;
		
	}

}
