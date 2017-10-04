package com.oxygenxml.resources.batch.converter.converters;

import java.io.Reader;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Interface for convert a document in other type.
 * 
 * @author intern4
 *
 */
public interface Converter {

	/**
	 * Convert the document from the given URL in other type.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document. This can be null.
	 * @param transformerCreator A trasformer creator.         
	 * @return The conversion result content.
	 * @throws TransformerException
	 */
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator) throws TransformerException;
}
