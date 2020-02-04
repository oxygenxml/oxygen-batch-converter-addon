package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Implementation of Converter for HTML to DITA.
 * @author Cosmin Duna
 *
 */
public class HtmlToDitaConverter implements Converter{

	/**
	 * Convert the given HTML to DITA.
	 * @param originalFile
	 *          The File to convert.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @param transformerCreator A transformer creator.         
	 * @return The converted DITA content in String format.
	 * @throws TransformerException 
	 */
	@Override
	public ConversionResult convert(File originalFileLocation, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		//create a HTML to XHTML converter
		HtmlToXhtmlConverter htmlToXhtmlTransformer = new HtmlToXhtmlConverter();
		
		// Additional processing of XHTML content
		AdditionalXHTMLProcessor additionalXHTMLProcessor = new AdditionalXHTMLProcessor();
		
		//create a XHTML to DITA converter
		XHTMLToDITAConverter xhtmlToDITATransformer = new XHTMLToDITAConverter();
		
		//convert the content to XHTML
		String xhtmlContent = htmlToXhtmlTransformer.convert(
				originalFileLocation, contentReader, baseDir, transformerCreator).getConvertedContent();
		
		String processedXhtml = additionalXHTMLProcessor.convert(
        originalFileLocation, new StringReader(xhtmlContent), baseDir, transformerCreator).getConvertedContent();
		
		if (!processedXhtml.isEmpty()) {
		  xhtmlContent = processedXhtml;
		}
		
		// convert the converted XHTML content in DITA 
		return  xhtmlToDITATransformer.convert(originalFileLocation, new StringReader(xhtmlContent), baseDir, transformerCreator);
		
	}

}
