package com.oxygenxml.resources.batch.converter.printer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

/**
 * A simple content printer implementation.
 * @author intern4
 *
 */
public class ContentPrinterImpl implements ContentPrinter {

	/**
	 * Print the given content in output file(The content isn't indented).
	 * 
	 * @param contentToPrint The content to print.
	 * @param transformerCreator A transformer creator.
	 * @param converterType The type of converter.
	 * @param outputFile The output file.
	 * @throws TransformerException
	 */
	@Override
		public void print(String contentToPrint, TransformerFactoryCreator transformerCreator,String converterType, File outputFile)
				throws TransformerException {

		// create a unique file path if actual exist
		outputFile = ConverterFileUtils.getFileWithCounter(outputFile);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			out.write(contentToPrint);

			try {
				out.close();
			} catch (Exception e) {
			}
		} catch (IOException e) {
			throw new TransformerException(e.getMessage());
		}

	}

}
