package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.converters.Converter;
import com.oxygenxml.resources.batch.converter.converters.ConverterCreator;
import com.oxygenxml.resources.batch.converter.extensions.ExtensionGetter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinter;
import com.oxygenxml.resources.batch.converter.printer.ContentPrinterCreater;
import com.oxygenxml.resources.batch.converter.reporter.OxygenStatusReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.worker.ConvertorWorkerInteractor;

/**
 * Batch converter implementation.
 * 
 * @author intern4
 *
 */
public class BatchConverterImpl implements BatchConverter {

	/**
	 * Problem reporter.
	 */
	private ProblemReporter problemReporter;
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
	 private static final Logger logger = Logger.getLogger(OxygenStatusReporter.class);
	
	
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
	public BatchConverterImpl(ProblemReporter problemReporter, ProgressDialogInteractor progressDialogInteractor,
			ConvertorWorkerInteractor workerInteractor, TransformerFactoryCreator transformerFactoryCreator) 
	{
		this.problemReporter = problemReporter;
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
	 * @return <code>true</code> if the process of conversion was finished
	 *         successfully, <code>false</code> otherwise.
	 */
	@Override
	public boolean convertFiles(String converterType, List<File> inputFiles, File outputFolder) {
		// flag to return
		boolean isSuccessfully = true;

		// converted content
		String convertedContent = "";

		// create the converter
		Converter converter = ConverterCreator.create(converterType);

		// create a content printer
		ContentPrinter contentPrinter = ContentPrinterCreater.create(converterType);

		if (converter != null) {
			// iterate over files
			int size = inputFiles.size();
			for (int i = 0; i < size; i++) {

				// check if worker was interrupted
				if (workerInteractor.isCancelled()) {
					isSuccessfully = false;
					// break the loop
					break;
				}

				// get the current file.
				File currentFile = inputFiles.get(i);

				// update the progress dialog note.
				progressDialogInteractor.setNote(currentFile.toString());

				try {
					// convert the current file
					convertedContent = converter.convert(currentFile, null, transformerFactoryCreator);
					
					if (convertedContent != null) {
						//generate the output file.
						File outputFile = ConverterFileUtils.generateOutputFile(currentFile, 
								ExtensionGetter.getOutputExtension(converterType), outputFolder);
						
						// print the converted content.
						contentPrinter.print(convertedContent, transformerFactoryCreator, converterType, outputFile);
					}

				} catch (TransformerException e) {
					problemReporter.reportProblem(e, currentFile);
					isSuccessfully = false;
					
				} catch (Throwable e) {
					logger.debug(e.getMessage(), e);
					//TODO delete printStackTrace();
					e.printStackTrace();
				}
			}

		} else {
			isSuccessfully = false;
		}

		return isSuccessfully;
	}
}