package com.oxygenxml.resources.batch.converter.converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.input.ReaderInputStream;
import org.w3c.tidy.Tidy;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Class which uses HTML Tidy to create a well-formed XHTML.
 */
public class HtmlToXhtmlConverter implements Converter {

	/**
	 * Convert the HTML document from the given URL in XHTML.
	 *
	 * @param originalFileLocation The URL location of document.
	 * @param contentReader Reader of the document.
	 * @return The conversion in XHTML.        
	 */
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator) throws TransformerException {
		//XHTML content to be return
		String toReturn = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			/* Transforms to well-formed XHTML */
			Tidy t = new Tidy();

			// set the Tidy parser
			t.setXHTML(true);
			t.setQuiet(true);
			t.setShowWarnings(false);
			t.setTidyMark(false);
			t.setForceOutput(true);

			if (contentReader != null) {
				// parse the content
				t.parse(new ReaderInputStream(contentReader, "UTF-8"), baos);
			}
			else {
				InputStream inputStream = originalFileLocation.openStream();
				// parse the inputStream
				t.parse(inputStream, baos);
				
				//close the inputStream
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}

			// convert to String
			toReturn = baos.toString("UTF-8");

			// close the streams
			try {
				baos.close();
			} catch (Exception e) {
			}

		} catch (IOException e1) {
			throw new TransformerException(e1.getMessage(), e1.getCause());
		}

		return toReturn;
	}

}