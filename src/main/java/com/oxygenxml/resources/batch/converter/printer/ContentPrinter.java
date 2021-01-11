package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.converters.ConversionResult;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;

/**
 * Interface for print converted content.
 * @author Cosmin Duna
 *
 */
public interface ContentPrinter {

	/**
	 * Print the given conversion result in the given output file.
	 * @param contentToPrint The conversion result.
	 * @param transformerCreator A transformer creator.
	 * @param converterType The type of converter.
	 * @param outputFile The output file.
	 * @param styleSource The source XSL, or <code>null</code> 
	 * @throws TransformerException
	 */
	public void print(ConversionResult conversionResult, TransformerFactoryCreator transformerCreator, String converterType,
			File outputFile, StreamSource styleSource) throws TransformerException;
}
