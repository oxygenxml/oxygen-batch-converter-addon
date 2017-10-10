package com.oxygenxml.resources.batch.converter.trasformer;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

/**
 * Transformer factory
 * @author intern4
 *
 */
public interface TransformerFactoryCreator {

	/**
	 * Create a transformer according to given stream source.
	 * @param streamSource
	 * @return
	 */
	public Transformer createTransformer(StreamSource streamSource);

}