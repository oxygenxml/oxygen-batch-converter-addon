package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for Word to DITA.
 * 
 * @author Cosmin Duna
 *
 */
public class WordToDITAConverter implements Converter {

	@Override
	public String convert(File originalFile, Reader contentReader, File baseDir,
			TransformerFactoryCreator transformerCreator) throws TransformerException {
		// Create a WORD to HTML converter
		WordToHTMLConverter wordToHTMLConverter = new WordToHTMLConverter();
		
		// Create a HTML to DITA converter
		HtmlToDitaConverter htmlToDITATransformer = new HtmlToDitaConverter();
			
		// Convert the WORD content to HTML
		String htmlContent = wordToHTMLConverter.convert(originalFile, contentReader, baseDir, transformerCreator);
		
		// Convert the converted HTML content to DITA 
		return  htmlToDITATransformer.convert(originalFile, new StringReader(htmlContent), baseDir, transformerCreator);
	}
}