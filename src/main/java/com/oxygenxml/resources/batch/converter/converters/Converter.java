package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;

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
	 * Convert the given file in other type.
	 * 
	 * @param originalFile
	 *          The File to convert.
	 * @param contentReader
	 *          Reader of the document. This can be null.
	 * @param transformerCreator
	 *          A transformer creator.
	 * @return The converted content in String format.
	 * @throws TransformerException
	 */
	public String convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException;
}
