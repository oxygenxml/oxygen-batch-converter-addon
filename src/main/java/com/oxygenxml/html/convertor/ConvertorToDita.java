package com.oxygenxml.html.convertor;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.oxygenxml.html.convertor.reporter.ProblemReporter;
import com.oxygenxml.html.convertor.trasformers.ContentPrinter;
import com.oxygenxml.html.convertor.trasformers.HtmlToXhtmlTransformer;
import com.oxygenxml.html.convertor.trasformers.MarkdownToDitaTransformer;
import com.oxygenxml.html.convertor.trasformers.FilePathGenerator;
import com.oxygenxml.html.convertor.trasformers.XHTMLToDITATransformer;

import ro.sync.util.URLUtil;

public class ConvertorToDita implements Convertor{

	MarkdownToDitaTransformer markdownDitaTransformer = new MarkdownToDitaTransformer();

	private static final String DOCTYPE_SYSTEM = "topic.dtd";
	private static final String DOCTYPE_PUBLIC = "-//OASIS//DTD DITA Topic//EN";

	private ProblemReporter problemReporter;
	
	public ConvertorToDita(ProblemReporter problemReporter) {
		this.problemReporter = problemReporter;
		
	}
	
	@Override
	public boolean convertFiles(List<String> inputFiles, String outputFolder) {
		boolean isSuccessfully = true;
		
		// iterate over files
		int size = inputFiles.size();
		for (int i = 0; i < size; i++) {

			// get the current file
			String currentFile = inputFiles.get(i);

			// get extension
			String extension = currentFile.substring(currentFile.indexOf(".") + 1);

			try {
				URL fileUrl = URLUtil.correct(new File(currentFile));

				if ("html".equals(extension)) {
					System.out.println("fileUrl: " + fileUrl);
					String xhtmlDocument =  new HtmlToXhtmlTransformer().convert(fileUrl, null);
					System.out.println("xhtml: "+ xhtmlDocument);
					String document = new XHTMLToDITATransformer().convert(fileUrl, new StringReader(xhtmlDocument));
					System.out.println("s-a facut transformarea : " + document);

					ContentPrinter.prettifyAndPrint("xml", new StringReader(document), FilePathGenerator.generate(currentFile, "dita", outputFolder), DOCTYPE_SYSTEM, DOCTYPE_PUBLIC);
					//					ContentPrinter.addDitaTagAndPrint(document, PathGenerator.generate(currentFile, "dita", outputFolder));
					
				}

				else if ("md".equals(extension)) {
					System.out.println("fileUrl: " + fileUrl);
						String document = markdownDitaTransformer.convert(fileUrl, null);
						System.out.println("s-a facut transformarea : " + document);
						ContentPrinter.prettifyAndPrint("xml", new StringReader(document), FilePathGenerator.generate(currentFile, "dita", outputFolder), DOCTYPE_SYSTEM, DOCTYPE_PUBLIC);
				}
			} catch (MalformedURLException e1) {
				isSuccessfully = false;
				e1.printStackTrace();
			} catch (TransformerException e) {
				problemReporter.reportProblem(e, currentFile);
				isSuccessfully = false;
			}
		}
		return isSuccessfully;
	}
}
