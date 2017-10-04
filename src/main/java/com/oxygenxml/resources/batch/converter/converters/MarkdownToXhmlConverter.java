package com.oxygenxml.resources.batch.converter.converters;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public class MarkdownToXhmlConverter implements Converter{

	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		MarkdownToHtmlConverter markdownToHtmlTransformer = new MarkdownToHtmlConverter();
		HtmlToXhtmlConverter htmlToXhtmlTransformer = new HtmlToXhtmlConverter();
		
		
		String htmlContent = markdownToHtmlTransformer.convert(originalFileLocation, null, transformerCreator);

		return  htmlToXhtmlTransformer.convert(originalFileLocation, new StringReader(htmlContent),transformerCreator);
		
	}

}
