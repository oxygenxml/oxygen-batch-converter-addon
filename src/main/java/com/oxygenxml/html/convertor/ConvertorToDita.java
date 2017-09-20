package com.oxygenxml.html.convertor;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.oxygenxml.html.convertor.reporter.ProblemReporter;
import com.oxygenxml.html.convertor.reporter.ProgressDialogInteractor;
import com.oxygenxml.html.convertor.trasformers.ContentPrinter;
import com.oxygenxml.html.convertor.trasformers.FilePathGenerator;
import com.oxygenxml.html.convertor.trasformers.HtmlToXhtmlTransformer;
import com.oxygenxml.html.convertor.trasformers.MarkdownToDitaTransformer;
import com.oxygenxml.html.convertor.trasformers.OxygenTransformerCreator;
import com.oxygenxml.html.convertor.trasformers.TransformerCreator;
import com.oxygenxml.html.convertor.trasformers.XHTMLToDITATransformer;
import com.oxygenxml.html.convertor.worker.ConvertorWorkerInteractor;

import ro.sync.util.URLUtil;

public class ConvertorToDita implements Convertor{

	MarkdownToDitaTransformer markdownDitaTransformer = new MarkdownToDitaTransformer();

	private static final String DOCTYPE_SYSTEM = "topic.dtd";
	private static final String DOCTYPE_PUBLIC = "-//OASIS//DTD DITA Topic//EN";

	private ProblemReporter problemReporter;

	private ProgressDialogInteractor progressDialogInteractor;

	private ConvertorWorkerInteractor workerInteractor;
	
	private TransformerCreator transformerCreator;
	
	public ConvertorToDita(ProblemReporter problemReporter, ProgressDialogInteractor progressDialogInteractor, ConvertorWorkerInteractor workerInteractor) {
		this.problemReporter = problemReporter;
		this.progressDialogInteractor = progressDialogInteractor;
		this.workerInteractor = workerInteractor;
		
		transformerCreator = new OxygenTransformerCreator();
		
		
	}
	
	@Override
	public boolean convertFiles(List<String> inputFiles, String outputFolder) {
		boolean isSuccessfully = true;
		
		String contentToPrint = "";
		
		List<String> htmlExtensions = Arrays.asList(FileType.INPUT_HTML_EXTENSIONS);
		List<String> mdExtensions = Arrays.asList(FileType.INPUT_MD_EXTENSIONS);
		
		// iterate over files
		int size = inputFiles.size();
		for (int i = 0; i < size; i++) {
			if(workerInteractor.isCancelled()){
				break;
			}
			
			
			// get the current file
			String currentFile = inputFiles.get(i);

			System.out.println(currentFile);
			progressDialogInteractor.setNote(currentFile);
			
			// get extension
			String extension = currentFile.substring(currentFile.indexOf(".") + 1);

			try {
				URL fileUrl = URLUtil.correct(new File(currentFile));

				if (htmlExtensions.contains(extension)) {
					System.out.println("fileUrl: " + fileUrl);
					String xhtmlDocument =  new HtmlToXhtmlTransformer().convert(fileUrl, null, transformerCreator);
					System.out.println("xhtml: "+ xhtmlDocument);
					 contentToPrint = new XHTMLToDITATransformer().convert(fileUrl, new StringReader(xhtmlDocument), transformerCreator);
				}
				else if (mdExtensions.contains(extension)) {
					System.out.println("fileUrl: " + fileUrl);
						 contentToPrint = markdownDitaTransformer.convert(fileUrl, null, transformerCreator);
				}
				
				System.out.println("s-a facut transformarea : " + contentToPrint);
				ContentPrinter.prettifyAndPrint( new StringReader(contentToPrint), 
						FilePathGenerator.generate(currentFile, FileType.DITA_TYPE_AND_EXTENSION, outputFolder), 
						DOCTYPE_SYSTEM, DOCTYPE_PUBLIC, transformerCreator);
				
				
			} catch (MalformedURLException e1) {
				//TODO poate trimit la user
				isSuccessfully = false;
				e1.printStackTrace();
				problemReporter.reportProblem(e1, "file:" + File.separator+ currentFile);
				
				
			} catch (TransformerException e) {
				e.printStackTrace();
				problemReporter.reportProblem(e, "file:" + File.separator+ currentFile);
				isSuccessfully = false;
			}
		}
		return isSuccessfully;
	}
	
	
}
