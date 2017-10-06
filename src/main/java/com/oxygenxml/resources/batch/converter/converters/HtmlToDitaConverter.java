package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Implementation of Converter for HTML to DITA.
 * @author intern4
 *
 */
public class HtmlToDitaConverter implements Converter{

	/**
	 * Convert the given HTML file in DITA.
	 * @param originalFile
	 *          The File to convert.
	 * @param contentReader
	 *          Reader of the document. This can be null.
	 * @param transformerCreator A transformer creator.         
	 * @return The converted DITA content in String format.
	 * @throws TransformerException 
	 */
	@Override
	public String convert(File originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		//create a HTML to XHTML converter
		HtmlToXhtmlConverter htmlToXhtmlTransformer = new HtmlToXhtmlConverter();
		
		//create a XHTML to DITA converter
		XHTMLToDITAConverter xhtmlToDITATransformer = new XHTMLToDITAConverter();
		
		String xhtmlContent;
		//convert the content to XHTML
		if(contentReader == null){
			xhtmlContent = htmlToXhtmlTransformer.convert(originalFileLocation, null, transformerCreator);
		}
		else{
			xhtmlContent = htmlToXhtmlTransformer.convert(originalFileLocation, contentReader, transformerCreator);
		}
		
		// convert the converted XHTML content in DITA 
		return  xhtmlToDITATransformer.convert(originalFileLocation, new StringReader(xhtmlContent),transformerCreator);
		
	}

}
