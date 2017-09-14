package com.oxygenxml.html.convertor.trasformers;

import java.io.Reader;
import java.net.URL;

import javax.xml.transform.TransformerException;

/**
 * Interface for convert a document in other type.
 * 
 * @author intern4
 *
 */
public interface Transformer {

	/**
	 * Convert the document from the given URL in other type.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document. This can be null.
	 * @return The conversion result content.
	 * @throws TransformerException
	 */
	public String convert(URL originalFileLocation, Reader contentReader) throws TransformerException;
}
