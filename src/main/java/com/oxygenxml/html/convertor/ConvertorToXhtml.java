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
import com.oxygenxml.html.convertor.trasformers.MarkdownToHtmlTransformer;
import com.oxygenxml.html.convertor.trasformers.OxygenTransformerCreator;
import com.oxygenxml.html.convertor.trasformers.TransformerCreator;
import com.oxygenxml.html.convertor.worker.ConvertorWorkerInteractor;

import ro.sync.util.URLUtil;

public class ConvertorToXhtml implements Convertor {

	private HtmlToXhtmlTransformer htmlToXhtmlTransformer = new HtmlToXhtmlTransformer();
	private MarkdownToHtmlTransformer markdownToXHtmlTransformer = new MarkdownToHtmlTransformer();

	private static final String DOCTYPE_SYSTEM = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
	private static final String DOCTYPE_PUBLIC = "-//W3C//DTD XHTML 1.0 Strict//EN";
	private ProblemReporter problemReporter;
	private ProgressDialogInteractor progressDialogInteractor;
	private ConvertorWorkerInteractor workerInteractor;

	private TransformerCreator transformerCreator;

	public ConvertorToXhtml(ProblemReporter problemReporter, ProgressDialogInteractor progressDialogInteractor,
			ConvertorWorkerInteractor workerInteractor) {

		this.problemReporter = problemReporter;
		this.progressDialogInteractor = progressDialogInteractor;
		this.workerInteractor = workerInteractor;

		transformerCreator = new OxygenTransformerCreator();
	}

	@Override
	public boolean convertFiles(List<String> inputFiles, String outputFolder) {
		boolean isSuccessfully = true;

		String contentToPrint= "";
		
		List<String> htmlExtensions = Arrays.asList(FileType.INPUT_HTML_EXTENSIONS);
		List<String> mdExtensions = Arrays.asList(FileType.INPUT_MD_EXTENSIONS);

		// iterate over files
		int size = inputFiles.size();
		for (int i = 0; i < size; i++) {
			if (workerInteractor.isCancelled()) {
				break;
			}

			// get the current file
			String currentFile = inputFiles.get(i);

			progressDialogInteractor.setNote(currentFile);

			// get extension
			String extension = currentFile.substring(currentFile.indexOf(".") + 1);

			try {
				URL fileUrl;

				if (htmlExtensions.contains(extension)) {
					// It's html file

					fileUrl = URLUtil.correct(new File(currentFile));

					contentToPrint = htmlToXhtmlTransformer.convert(fileUrl, null, transformerCreator);

					
				}

				else if (mdExtensions.contains(extension)) {
					// it's markdown file

					fileUrl = new URL("file:/" + currentFile);

					String htmlContent = markdownToXHtmlTransformer.convert(fileUrl, null, transformerCreator);

					System.out.println("html: " + htmlContent);

					contentToPrint = htmlToXhtmlTransformer.convert(fileUrl, new StringReader(htmlContent),
							transformerCreator);
				}
				
				
				ContentPrinter.prettifyAndPrint(new StringReader(contentToPrint),
						FilePathGenerator.generate(currentFile, FileType.XHTML_TYPE_AND_EXTENSION, outputFolder), DOCTYPE_SYSTEM,
						DOCTYPE_PUBLIC, transformerCreator);
				

			} catch (MalformedURLException e) {
				isSuccessfully = false;
				// TODO Auto-generated catch block
				e.printStackTrace();
				problemReporter.reportProblem(e, "file:" + File.separator+ currentFile);
			} 
			catch (TransformerException e) {
				isSuccessfully = false;
				problemReporter.reportProblem(e, "file:" + File.separator+ currentFile);
			}
		}
		return isSuccessfully;
	}
}
