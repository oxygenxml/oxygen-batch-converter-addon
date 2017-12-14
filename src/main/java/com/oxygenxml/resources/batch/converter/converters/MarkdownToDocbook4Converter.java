package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for Markdown to Docbook4.
 * @author Cosmin Duna
 *
 */
public class MarkdownToDocbook4Converter implements Converter {
  
  
	/**
	 * Convert Markdown to Docbook4.
	 * 
	 * @param originalFile
	 *          The markdown file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted Docbook4 content in String format or null if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {
	  
		MarkdownToXhmlConverter generatedXhtml = new MarkdownToXhmlConverter();
		
		XHTMLToDocbook4Converter generatedDB4 = new XHTMLToDocbook4Converter();
		
		// Convert the markdown file to XHTML.
		String xhtmlContent = generatedXhtml.convert(originalFile, contentReader, transformerCreator);
		
		StringReader readerOverXHTML = new StringReader(xhtmlContent);
    return generatedDB4.convert(originalFile, readerOverXHTML, transformerCreator);
	}

}
