package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public interface ContentPrinter {

	/**
	 * Print the given content in the given output file.
	 * @param contentToPrint The content to print.
	 * @param transformerCreator A transformer creator.
	 * @param converterType The type of converter.
	 * @param outputFile The output file.
	 * @param styleSource The source XSL, or <code>null</code> 
	 * @throws TransformerException
	 */
	public void print(String contentToPrint, TransformerFactoryCreator transformerCreator, String converterType,
			File outputFile, StreamSource styleSource) throws TransformerException;
}
