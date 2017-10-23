package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.reporter.OxygenStatusReporter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

/**
 * A simple content printer implementation.
 * 
 * @author intern4
 *
 */
public class SimpleContentPrinterImpl implements ContentPrinter {
	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(OxygenStatusReporter.class);
	
	/**
	 * Print the given content in output file(The content isn't indented).
	 * 
	 * @param contentToPrint
	 *          The content to print.
	 * @param transformerCreator
	 *          A transformer creator.
	 * @param converterType
	 *          The type of converter.
	 * @param outputFile
	 *          The output file.
	 * @throws TransformerException
	 */
	@Override
	public void print(String contentToPrint, TransformerFactoryCreator transformerCreator, String converterType,
			File outputFile) throws TransformerException {

		// create a unique file path if actual exist
		outputFile = ConverterFileUtils.getFileWithCounter(outputFile);
		
		OutputStream outputStream = null;
		OutputStreamWriter writer = null;

		try {
			outputStream = new FileOutputStream(outputFile);
			writer = new OutputStreamWriter(outputStream, "UTF-8");
			writer.write(contentToPrint);
			
		}
		catch (IOException e) {
			throw new TransformerException(e.getMessage());
		} 
		finally {
			try {
				writer.close();
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}

	}
}
