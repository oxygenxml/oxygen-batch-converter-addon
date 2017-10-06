package com.oxygenxml.resources.batch.converter;

import java.net.URL;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.converters.Converter;
import com.oxygenxml.resources.batch.converter.converters.ConverterCreator;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinterCreater;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinterPrettyXmlImpl;
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

				System.out.println("batch conv impl: curentUrlFile: " + currentFileUrl.toString());
				
				//update the node in progress dialog.
				progressDialogInteractor.setNote(currentFileUrl.toString());
				
				try {
					
					System.out.println("inainte de conversie");
					//convert 
					 contentToPrint =  transformer.convert(currentFileUrl, null, transformerFactoryCreator);
				
					 if(contentToPrint != null){
						 System.out.println(contentToPrint);
						 ContentPrinter contentPrinter = ContentPrinterCreater.create(converterType);
						 
						 //print the converted content.
						 contentPrinter.print(contentToPrint, transformerFactoryCreator, currentFileUrl, outputFolder, converterType);
					 }
					
				} catch (TransformerException e) {
					problemReporter.reportProblem(e, currentFileUrl.toString());
					isSuccessfully = false;
				}
				catch( Throwable e) {
					e.printStackTrace();
				}
			}
			return isSuccessfully;
		
	}

}
