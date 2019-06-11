package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for Word to DocBook4.
 * 
 * @author Cosmin Duna
 *
 */
public class WordToDocbook4Conversion implements Converter{

	@Override
	public ConversionResult convert(File originalFile, Reader contentReader, File baseDir,
			TransformerFactoryCreator transformerCreator) throws TransformerException {
		// Create a WORD to HTML converter
		WordToXHTMLConverter wordToHTMLConverter = new WordToXHTMLConverter();
		
		// Create a HTML to DB4 converter
		HtmlToDocbook4Converter htmlToDb4Converter = new HtmlToDocbook4Converter();
			
		// Convert the WORD content to HTML
		String htmlContent = wordToHTMLConverter.convert(
				originalFile, contentReader, baseDir, transformerCreator).getConvertedContent();
		
		// Convert the converted HTML content to DB4 
		return  htmlToDb4Converter.convert(originalFile, new StringReader(htmlContent), baseDir, transformerCreator);
	}

}
