package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.elovirta.dita.markdown.MarkdownReader;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for Markdown to DITA.
 * 
 * @author intern4
 *
 */
public class MarkdownToDitaTransformer implements com.oxygenxml.resources.batch.converter.converters.Converter {

	/**
	 * Convert Markdown to DITA.
	 * 
	 * @param originalFile
	 *          The Markdowm file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted DITA content in String format or <code>null</code> if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator) throws TransformerException {
		// content to return
		String toReturn = null;

		//create the transformer
		Transformer transformer = transformerCreator.createTransformer(null);

		// get the trasformFactory property
		String property = System.getProperty("javax.xml.transform.TransformerFactory");

		// set the trasformFactory property to
		// "net.sf.saxon.TransformerFactoryImpl"
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
		// reader for markdown document
		final MarkdownReader r = new MarkdownReader();

		try {
			// input source of document to convert
			InputSource inputSource;
			if (contentReader == null) {
				inputSource = new InputSource(originalFileLocation.toURI().toString());
			}
			else{
				inputSource = new InputSource(contentReader);
			}
			
			StringWriter sw = new StringWriter();
			StreamResult res = new StreamResult(sw);

			// convert the document
			transformer.transform(new SAXSource(r, inputSource), res);

			// get the converted content
			toReturn = sw.toString();

		}catch (TransformerException e) {
				throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}catch (Throwable e) {
			throw new TransformerException(e.getMessage() , e.getCause());
		} 
		finally {
			// return the initial property of trasformerFactory
			if (property == null) {
				System.getProperties().remove("javax.xml.transform.TransformerFactory");
			} else {
				System.setProperty("javax.xml.transform.TransformerFactory", property);
			}
		}

		return toReturn;
	}

}
