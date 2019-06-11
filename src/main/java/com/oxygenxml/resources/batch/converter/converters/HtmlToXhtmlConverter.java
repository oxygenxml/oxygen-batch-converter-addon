package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;

import org.w3c.tidy.Tidy;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterReaderUtils;

/**
 * Converter implementation for HTML to XHTML.
 * @author Cosmin Duna
 *
 */
public class HtmlToXhtmlConverter implements Converter {

	/**
	 * Convert HTML to XHTML.
	 *
	 * @param originalFileLocation The HTML file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted XHTML content..        
	 */
	public ConversionResult convert(File originalFileLocation, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator) throws TransformerException {
		//XHTML content to be return
		String convertedContent = null;

			try {
				StringWriter sw = new StringWriter();

				/* Transforms to well-formed XHTML */
				Tidy t = new Tidy();

				// set the Tidy parser
				t.setXHTML(true);
				t.setQuiet(true);
				t.setDocType("omit");
				t.setShowWarnings(false);
				t.setTidyMark(false);
				t.setForceOutput(true);
				t.setXmlTags(true);
				
				if (contentReader == null) {
					contentReader = ConverterReaderUtils.createReader(originalFileLocation);
				}
				
				// Read the content from the readear to catch the encoding problems.
				StringBuilder sb = new StringBuilder();
				int len = -1;
				char[] cbuf = new char[1024];
				while((len = contentReader.read(cbuf)) != -1) {
					sb.append(cbuf, 0, len);
				}
				
				// parse the content
				t.parse(new StringReader(sb.toString()), sw);

				// convert to String
				convertedContent = sw.toString();

			} catch (IOException e1) {
				throw new TransformerException(e1.getMessage(), e1);
			
			} finally {
				//Close the reader.
				if(contentReader != null) {
					try {
						contentReader.close();
					} catch (IOException e) {
						// Do nothing.
					}
				}
			}
			
			return new ConversionResult(convertedContent);
		}

}