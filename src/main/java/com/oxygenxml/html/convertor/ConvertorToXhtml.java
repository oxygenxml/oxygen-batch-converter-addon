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
import com.oxygenxml.html.convertor.trasformers.OxygenTransformerFactoryCreator;
import com.oxygenxml.html.convertor.trasformers.TransformerFactoryCreator;
import com.oxygenxml.html.convertor.worker.ConvertorWorkerInteractor;

import ro.sync.util.URLUtil;

/**
 * Convertor to XHTML
 * @author intern4
 *
 */
public class ConvertorToXhtml implements Convertor {

	/**
	 * HTML to XHTML transformer 
	 */
	private HtmlToXhtmlTransformer htmlToXhtmlTransformer = new HtmlToXhtmlTransformer();
	
	/**
	 * Markdown to HTML trasformer
	 */
	private MarkdownToHtmlTransformer markdownToXHtmlTransformer = new MarkdownToHtmlTransformer();
	
	/**
	 * System doctype to set in converted XHTML file.
	 */
	private static final String DOCTYPE_SYSTEM = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
	/**
	 * Public doctype to set in converted XHTML file.
	 */
	private static final String DOCTYPE_PUBLIC = "-//W3C//DTD XHTML 1.0 Strict//EN";
	
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
	public ConvertorToXhtml(ProblemReporter problemReporter, ProgressDialogInteractor progressDialogInteractor,
			ConvertorWorkerInteractor workerInteractor) {

		this.problemReporter = problemReporter;
		this.progressDialogInteractor = progressDialogInteractor;
		this.workerInteractor = workerInteractor;

		transformerCreator = new OxygenTransformerFactoryCreator();
	}

	
	/**
	 * Convert the given file to XHTML and save in given output folder.
	 * @param inputFiles The input files
	 * @param outputFolder The output folder.
	 * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
	 */
	@Override
	public boolean convertFiles(List<String> inputFiles, String outputFolder) {
		//flag to return
		boolean isSuccessfully = true;
		//content to print
		String contentToPrint= "";
		
		//list with HTML extensions
		List<String> htmlExtensions = Arrays.asList(FileType.INPUT_HTML_EXTENSIONS);
		
		//list with Markdown extensions
		List<String> mdExtensions = Arrays.asList(FileType.INPUT_MD_EXTENSIONS);

		// iterate over files
		int size = inputFiles.size();
		
		//check if worker was interrupted
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
