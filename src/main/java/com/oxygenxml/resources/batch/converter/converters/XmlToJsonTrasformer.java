package com.oxygenxml.resources.batch.converter.converters;

import java.io.Reader;
import java.net.URL;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public class XmlToJsonTrasformer implements Converter {

	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		
		return null;
	}

}
