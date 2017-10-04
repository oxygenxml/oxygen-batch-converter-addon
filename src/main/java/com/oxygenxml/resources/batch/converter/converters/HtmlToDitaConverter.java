package com.oxygenxml.resources.batch.converter.converters;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public class HtmlToDitaConverter implements Converter{

	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		HtmlToXhtmlConverter htmlToXhtmlTransformer = new HtmlToXhtmlConverter();
		XHTMLToDITATransformer xhtmlToDITATransformer = new XHTMLToDITATransformer();
		
		
		String xhtmlContent = htmlToXhtmlTransformer.convert(originalFileLocation, null, transformerCreator);

		return  xhtmlToDITATransformer.convert(originalFileLocation, new StringReader(xhtmlContent),transformerCreator);
		
	}

}
