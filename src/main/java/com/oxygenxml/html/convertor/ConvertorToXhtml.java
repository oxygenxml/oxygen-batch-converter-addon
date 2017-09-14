package com.oxygenxml.html.convertor;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.transform.TransformerException;

import com.oxygenxml.html.convertor.reporter.ProblemReporter;
import com.oxygenxml.html.convertor.trasformers.ContentPrinter;
import com.oxygenxml.html.convertor.trasformers.FilePathGenerator;
import com.oxygenxml.html.convertor.trasformers.HtmlToXhtmlTransformer;
import com.oxygenxml.html.convertor.trasformers.MarkdownToHtmlTransformer;

import ro.sync.util.URLUtil;

public class ConvertorToXhtml implements Convertor {

	private HtmlToXhtmlTransformer htmlToXhtmlTransformer = new HtmlToXhtmlTransformer();
	private MarkdownToHtmlTransformer markdownToXHtmlTransformer = new MarkdownToHtmlTransformer();

	private static final String DOCTYPE_SYSTEM = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
	private static final String DOCTYPE_PUBLIC = "-//W3C//DTD XHTML 1.0 Strict//EN";
	private ProblemReporter problemReporter;

	public ConvertorToXhtml(ProblemReporter problemReporter) {
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
					// It's html file

					String document = htmlToXhtmlTransformer.convert(fileUrl, null);

					System.out.println("s-a facut conversia: " + document);

					ContentPrinter.prettifyAndPrint("xml", new StringReader(document),
							FilePathGenerator.generate(currentFile, "xhtml", outputFolder), DOCTYPE_SYSTEM, DOCTYPE_PUBLIC);
				}

				else if ("md".equals(extension)) {
					// it's markdown file

					String htmlContent = markdownToXHtmlTransformer.convert(fileUrl, null);
					String xhtmlContent = htmlToXhtmlTransformer.convert(fileUrl, new StringReader(htmlContent));

					ContentPrinter.prettifyAndPrint("xml", new StringReader(xhtmlContent),
							FilePathGenerator.generate(currentFile, "xhtml", outputFolder), DOCTYPE_SYSTEM, DOCTYPE_PUBLIC);
				}

			} catch (MalformedURLException e) {
				isSuccessfully = false;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				isSuccessfully = false;
				problemReporter.reportProblem(e, currentFile);
			}
		}
		return isSuccessfully;
	}
}
