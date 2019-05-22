package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for HTML to Docbook4.
 * @author Cosmin Duna
 *
 */
public class HtmlToDocbook4Converter  implements Converter {

		/**
		 * Convert HTML to Docbook4.
		 * 
		 * @param originalFile
		 *          The HTML file.
		 * @param contentReader
		 *          Reader of the document. If the content reader isn't <code>null</code>, 
		 *          the converter will process this reader and will ignore the given file.
		 * @return The converted Docbook4 content in String format or null if conversion process failed.
		 * @throws TransformerException
		 */
		@Override
		public String convert(File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
				throws TransformerException {

			HtmlToXhtmlConverter htmlToXhtmlConverter = new HtmlToXhtmlConverter();
			XHTMLToDocbook4Converter xhtmlToDocbook4Converter = new XHTMLToDocbook4Converter();
			
			//convert the HTML to XHTML
			String xhtmlContent = htmlToXhtmlConverter.convert(originalFile, contentReader, baseDir, transformerCreator);
			
			//convert the XHTML content to Docbook4 and return
			return  xhtmlToDocbook4Converter.convert(originalFile, new StringReader(xhtmlContent), baseDir, transformerCreator);
			
		}
}

