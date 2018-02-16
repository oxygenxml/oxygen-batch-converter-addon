package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.converters.Converter;
import com.oxygenxml.resources.batch.converter.converters.ConverterCreator;
import com.oxygenxml.resources.batch.converter.extensions.ExtensionGetter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinterCreater;
import com.oxygenxml.resources.batch.converter.printer.StyleSourceGetter;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.reporter.StatusReporter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.worker.ConvertorWorkerInteractor;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Batch converter implementation.
 * 
 * @author Cosmin Duna
 *
 */
public class BatchConverterImpl implements BatchConverter {

	/**
	 * Problem reporter.
	 */
	private ProblemReporter problemReporter;
	
	/**
	 * Status reporter.
	 */
	private StatusReporter statusReporter;
	
	/**
	 * Progress dialog interactor.
	 */
	private ProgressDialogInteractor progressDialogInteractor;
	/**
	 * Worker interactor.
	 */
	private ConvertorWorkerInteractor workerInteractor;
	/**
	 * Transformer creator.
	 */
	private TransformerFactoryCreator transformerFactoryCreator;
	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(BatchConverterImpl.class);
	
	 /**
	  *  The flag of conversion. <code>True</code> when conversion was successfully, <code>false</code> otherwise.
	  */
		private boolean isSuccessfully ;

		/**
		 * Number of converted files
		 */
		private int convertedFile;
		
		/**
		 * Number of files that cannot be converted.
		 */
		private int failedFile;
	 
	/**
	 * Constructor.
	 * 
	 * @param problemReporter
	 *          Problem reporter.
	 * @param progressDialogInteractor
	 *          Progress dialog interactor.
	 * @param workerInteractor
	 *          Worker interactor.
	 * @param transformerFactoryCreator
	 *          Transformer factory creator.
	 */
	public BatchConverterImpl(ProblemReporter problemReporter, StatusReporter statusReporter,
			ProgressDialogInteractor progressDialogInteractor, ConvertorWorkerInteractor workerInteractor,
			TransformerFactoryCreator transformerFactoryCreator) 
	{
		this.problemReporter = problemReporter;
		this.statusReporter = statusReporter;
		this.progressDialogInteractor = progressDialogInteractor;
		this.workerInteractor = workerInteractor;
		this.transformerFactoryCreator = transformerFactoryCreator;
	}

	
	/**
	 * Convert the given input files and write them in given output folder
	 * according to given convertorType.
	 * 
	 * @param convertorType
	 *          The converter type.
	 * @param inputFiles
	 *          The input files.
	 * @param outputFolder
	 *          The output folder.
	 * @param openConvertedFiles <code>true</code> to open the converted files in Oxygen, <code>false</code> otherwise.
	 * @return <code>true</code> if the process of conversion was finished
	 *         successfully, <code>false</code> otherwise.
	 */
	@Override
	public boolean convertFiles(String converterType, List<File> inputFiles, File outputFolder, boolean openConvertedFile) {
		
		isSuccessfully = true;
		convertedFile = 0;
		failedFile = 0;
				
		// create the converter
		Converter converter = ConverterCreator.create(converterType);

		// create a content printer
		ContentPrinter contentPrinter = ContentPrinterCreater.create(converterType);

		//make the output directory if it doesn't exist
		if(!outputFolder.exists()){
			outputFolder.mkdirs();
		}
		
		if (converter != null) {
			// iterate over files
			int size = inputFiles.size();
			for (int i = 0; i < size; i++) {

				// check if worker was interrupted
				if (workerInteractor.isCancelled()) {
					isSuccessfully = false;
					failedFile += size-i;
					// break the loop
					break;
				}

				// get the current file.
				File currentFile = inputFiles.get(i);

				// update the progress dialog note.
				progressDialogInteractor.setNote(currentFile.toString());

				//generate the output file.
				File outputFile = ConverterFileUtils.getOutputFile(currentFile, 
						ExtensionGetter.getOutputExtension(converterType), outputFolder);
				
				// create a unique file path if actual exist
				outputFile = ConverterFileUtils.getFileWithCounter(outputFile);
				
				//convert and print the current file.
				convertAndPrintFile(currentFile, outputFile, converter, contentPrinter, converterType, openConvertedFile);
				
			}

		} else {
			isSuccessfully = false;
			failedFile = inputFiles.size();
		}

		//report the finish status
		statusReporter.reportFinishStatus(convertedFile, failedFile);
		return isSuccessfully;
	}
	
	
	/**
	 * Convert the given file using the given converter and print the converted result using the given contentPrinter. 
	 * @param file The file.
	 * @param outputFile The outputFile
	 * @param converter The converter. 
	 * @param contentPrinter The contentPrinter.
	 * @param converterType The converterType.
	 * @param openConvertedFile <code>true</code>To open the converted file in Oxygen, <code>false</code> otherwise.
 	 */
	private void convertAndPrintFile(File file, File outputFile, Converter converter, ContentPrinter contentPrinter,
			String converterType, boolean openConvertedFile) {

		String convertedContent = null;
		try {
			// convert the current file
			convertedContent = converter.convert(file, null, transformerFactoryCreator);

			if (convertedContent != null) {

				// print the converted content.
				contentPrinter.print(convertedContent, transformerFactoryCreator, converterType, outputFile,
						StyleSourceGetter.getStyleSource(converterType));

				convertedFile++;

				if (openConvertedFile) {
					// open the converted file
					URL convertedFileUrl;
					convertedFileUrl = outputFile.toURI().toURL();
					PluginWorkspaceProvider.getPluginWorkspace().open(convertedFileUrl);
				}

			}

		} catch (TransformerException e) {
			problemReporter.reportProblem(e, file);
			isSuccessfully = false;
			failedFile++;
		} catch (MalformedURLException e) {
			logger.debug(e.getMessage(), e);
		}

	}
}