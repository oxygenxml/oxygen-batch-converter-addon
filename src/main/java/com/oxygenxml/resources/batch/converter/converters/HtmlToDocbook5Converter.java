package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for HTML to Docbook5.
 * @author Cosmin Duna
 *
 */
public class HtmlToDocbook5Converter  implements Converter {

		/**
		 * Convert HTML to Docbook5.
		 * 
		 * @param originalFile
		 *          The HTML file.
		 * @param contentReader
		 *          Reader of the document. If the content reader isn't <code>null</code>, 
		 *          the converter will process this reader and will ignore the given file.
		 * @return The converted Docbook5 content in String format or null if conversion process failed.
		 * @throws TransformerException
		 */
		@Override
		public ConversionResult convert(File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
				throws TransformerException {

			HtmlToXhtmlConverter htmlToXhtmlConverter = new HtmlToXhtmlConverter();
			
	    // Additional processing of XHTML content
      AdditionalXHTMLProcessor additionalXHTMLProcessor = new AdditionalXHTMLProcessor();
			
			XHTMLToDocbook5Converter xhtmlToDocbook5Converter = new XHTMLToDocbook5Converter();
			
			//convert the HTML to XHTML
			String xhtmlContent = htmlToXhtmlConverter.convert(
					originalFile, contentReader, baseDir, transformerCreator).getConvertedContent();
			
			String processedXhtml = additionalXHTMLProcessor.convert(
			    originalFile, new StringReader(xhtmlContent), baseDir, transformerCreator).getConvertedContent();

			if (!processedXhtml.isEmpty()) {
			  xhtmlContent = processedXhtml;
			}
			
			//convert the XHTML content to Docbook5 and return
			return  xhtmlToDocbook5Converter.convert(originalFile, new StringReader(xhtmlContent), baseDir, transformerCreator);
			
		}
}
