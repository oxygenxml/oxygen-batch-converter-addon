package com.oxygenxml.html.convertor.trasformers;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.elovirta.dita.markdown.MarkdownReader;

/**
 * Class that use com.elovirta.dita.markdown.MarkdownReader for convert Markdown
 * to DITA.
 * 
 * @author intern4
 *
 */
public class MarkdownToDitaTransformer implements com.oxygenxml.html.convertor.trasformers.Transformer {

	/**
	 * Convert the markdown document from the given URL in DITA.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document.
	 * @return The conversion in DITA.
	 * @throws TransformerException
	 */
	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerCreator transformerCreator)
			throws TransformerException {
		// content to return
		String toReturn = null;

		Transformer transformer = transformerCreator.createTransformer(null);

		// reader for markdown document
		final MarkdownReader r = new MarkdownReader();

		// get the trasformFactory property
		String property = System.getProperty("javax.xml.transform.TransformerFactory");

		System.out.println("property " + property);

		// set the trasformFactory property to
		// "com.saxonica.config.EnterpriseTransformerFactory"
		System.setProperty("javax.xml.transform.TransformerFactory", "com.saxonica.config.EnterpriseTransformerFactory");

		try {

			System.out.println("aici1");

			// input source of document to convert
			final InputSource i = new InputSource(originalFileLocation.toString());

			System.out.println("aici2");

			StringWriter sw = new StringWriter();
			StreamResult res = new StreamResult(sw);

			// convert the document
			transformer.transform(new SAXSource(r, i), res);

			// get the converted content
			toReturn = sw.toString();

		} finally {
			// return the initial property of trasformerFactory
			if (property == null) {
				System.getProperties().remove("javax.xml.transform.TransformerFactory");
			} else {
				System.setProperty("javax.xml.transform.TransformerFactory", property);
			}
		}

		return toReturn;
	}

	public static void main(String[] args) {
		MarkdownToDitaTransformer ditaTransformer = new MarkdownToDitaTransformer();

		try {
			String ditaContent = ditaTransformer.convert(new URL("file:/C:\\Users\\intern4\\Desktop\\test\\table.md"), null,
					new TransformerCreatorImpl());

			System.out.println(ditaContent);
			ContentPrinter.prettifyAndPrint(new StringReader(ditaContent),
					new File("C:\\Users\\intern4\\Desktop\\test\\table.dita"), "dita", "", new TransformerCreatorImpl());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
