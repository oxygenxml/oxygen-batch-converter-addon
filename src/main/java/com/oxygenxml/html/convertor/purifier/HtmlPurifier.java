package com.oxygenxml.html.convertor.purifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.w3c.tidy.Tidy;

/**
 * Class which uses HTML Tidy to create a well-formed XHTML.
 */
public class HtmlPurifier {

	
	/**
	 * Create a well-formed XHTML from the HTML at a given path.
	 *
	 * @param filePath The path of file
	 * @return byteArray The well-formed XHTML
	 * @throws IOException
	 */
	public byte[] createWellFormedContent(String filePath) throws IOException {

		URL url  = new URL(filePath);
		
		InputStream stream = url.openStream();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/* Transforms to well-formed XHTML */
		Tidy t = new Tidy();

		// set the Tidy parser
		t.setXHTML(true);
		t.setQuiet(true);
		t.setShowWarnings(false);
		t.setTidyMark(false);
		t.setForceOutput(true);

		// parse the inputStream
		t.parse(stream, baos);

		// convert to byteArray
		byte data[] = baos.toByteArray();

		// close the streams
		try {
			stream.close();
			baos.close();
		} catch (Exception e) {
		}

		return data;

	}
}