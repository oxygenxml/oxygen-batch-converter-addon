package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for Markdown to Docbook5.
 * @author intern4
 *
 */
public class MarkdownToDocbook5Converter implements Converter {

	/**
	 * Convert Markdown to Docbook5.
	 * 
	 * @param originalFile
	 *          The markdown file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted Docbook5 content in String format or null if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		MarkdownToXhmlConverter markdownToXhmlConverter = new MarkdownToXhmlConverter();
		XHTMLToDocbook5Converter xhtmlToDocbook5Converter = new XHTMLToDocbook5Converter();
		
		//convert the markdown file to XHTML
		String xhtmlContent = markdownToXhmlConverter.convert(originalFile, contentReader, transformerCreator);

		//convert the XHTML content to Docbook and return
		return  xhtmlToDocbook5Converter.convert(originalFile, new StringReader(xhtmlContent),transformerCreator);
		
	}

}
