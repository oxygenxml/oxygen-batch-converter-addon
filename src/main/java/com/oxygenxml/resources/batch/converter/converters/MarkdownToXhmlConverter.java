package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
/**
 * Converter implementation for Markdown to XHTML
 * @author intern4
 *
 */
public class MarkdownToXhmlConverter implements Converter{

	/**
	 * Convert Markdown to XHTML.
	 * 
	 * @param originalFile
	 *          The markdown file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted XHTML content in String format or null if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		MarkdownToHtmlConverter markdownToHtmlTransformer = new MarkdownToHtmlConverter();
		HtmlToXhtmlConverter htmlToXhtmlTransformer = new HtmlToXhtmlConverter();
		
		//convert the markdown file to HTML
		String htmlContent = markdownToHtmlTransformer.convert(originalFile, contentReader, transformerCreator);

		//convert the HTML content to XHTML and return
		return  htmlToXhtmlTransformer.convert(originalFile, new StringReader(htmlContent),transformerCreator);
		
	}

}
