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
import com.oxygenxml.html.convertor.trasformers.OxygenTransformerFactoryCreator;
import com.oxygenxml.html.convertor.trasformers.TransformerFactoryCreator;
import com.oxygenxml.html.convertor.trasformers.XHTMLToDITATransformer;
import com.oxygenxml.html.convertor.worker.ConvertorWorkerInteractor;

import ro.sync.util.URLUtil;

/**
 * Convertor to DITA
 * @author intern4
 *
 */
public class ConvertorToDita implements Convertor{

	/**
	 * Markdown to DITA trasformer
	 */
	MarkdownToDitaTransformer markdownDitaTransformer = new MarkdownToDitaTransformer();

	/**
	 * HTML to XHTML trasformer.
	 */
	HtmlToXhtmlTransformer htmlToXhtmlTransformer = new HtmlToXhtmlTransformer();
	
	/**
	 * XHTML to DITA trasformer
	 */
	XHTMLToDITATransformer xhtmlToDITATransformer = new XHTMLToDITATransformer();
	
	/**
	 * System doctype to set in converted DITA file.
	 */
	private static final String DOCTYPE_SYSTEM = "topic.dtd";
	/**
	 * Public doctype to set in converted DITA file.
	 */
	private static final String DOCTYPE_PUBLIC = "-//OASIS//DTD DITA Topic//EN";

	/**
	 * Problem reporter
	 */
	private ProblemReporter problemReporter;

	/**
	 * Progress dialog interactor
	 */
	private ProgressDialogInteractor progressDialogInteractor;

	/**
	 * Worker interactor.
	 */
	private ConvertorWorkerInteractor workerInteractor;
	
	/**
	 * Trasformer factory  
	 */
	private TransformerFactoryCreator transformerCreator;
	
	
	/**
	 * Constructor.
	 * @param problemReporter Problem reporter.
	 * @param progressDialogInteractor Progress dialog interactor.
	 * @param workerInteractor 	Worker interactor.
	 */
	public ConvertorToDita(ProblemReporter problemReporter, ProgressDialogInteractor progressDialogInteractor, ConvertorWorkerInteractor workerInteractor) {
		this.problemReporter = problemReporter;
		this.progressDialogInteractor = progressDialogInteractor;
		this.workerInteractor = workerInteractor;
		
		transformerCreator = new OxygenTransformerFactoryCreator();
		
	}
	
	/**
	 * Convert the given file to DITA and save in given output folder.
	 * @param inputFiles The input files
	 * @param outputFolder The output folder.
	 * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
	 */
	@Override
	public boolean convertFiles(List<String> inputFiles, String outputFolder) {
		//flag to return 
		boolean isSuccessfully = true;
		//content to print
		String contentToPrint = "";
		
		//list with HTML extensions
		List<String> htmlExtensions = Arrays.asList(FileType.INPUT_HTML_EXTENSIONS);
		//list with Markdown extensions
		List<String> mdExtensions = Arrays.asList(FileType.INPUT_MD_EXTENSIONS);
		
		// iterate over files
		int size = inputFiles.size();
		for (int i = 0; i < size; i++) {
			
			//check if worker was interrupted
			if(workerInteractor.isCancelled()){
				//break the loop
				break;
			}
			
			// get the current file.
			String currentFile = inputFiles.get(i);

			//update the node in progress dialog.
			progressDialogInteractor.setNote(currentFile);
			
			// get the extension of file
			String extension = currentFile.substring(currentFile.indexOf(".") + 1);

			try {
				//convert in URL(and correct)
				URL fileUrl = URLUtil.correct(new File(currentFile));
				System.out.println("fileUrl: " + fileUrl);

				//if current file is HTML
				if (htmlExtensions.contains(extension)) {
					//convert to XHTML 
					String xhtmlDocument =  htmlToXhtmlTransformer.convert(fileUrl, null, transformerCreator);
					//convert to DITA
					contentToPrint = xhtmlToDITATransformer.convert(fileUrl, new StringReader(xhtmlDocument), transformerCreator);
				}
				//else if current file is MarkDown
				else if (mdExtensions.contains(extension)) {
					//convert to DITA	
					contentToPrint = markdownDitaTransformer.convert(fileUrl, null, transformerCreator);
				}
				
				System.out.println("s-a facut transformarea : " + contentToPrint);
				
				//print the converted content.
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
