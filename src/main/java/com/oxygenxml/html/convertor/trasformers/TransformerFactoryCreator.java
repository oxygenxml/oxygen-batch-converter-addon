package com.oxygenxml.html.convertor.trasformers;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

/**
 * Trasformer factory
 * @author intern4
 *
 */
public interface TransformerFactoryCreator {

	/**
	 * Create a trasformer according to given stream source.
	 * @param streamSource
	 * @return
	 */
	public Transformer createTransformer(StreamSource streamSource);

}