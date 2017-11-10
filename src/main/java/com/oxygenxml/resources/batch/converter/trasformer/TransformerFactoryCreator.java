package com.oxygenxml.resources.batch.converter.trasformer;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

/**
 * Transformer factory
 * @author Cosmin Duna
 *
 */
public interface TransformerFactoryCreator {

	/**
	 * Create a transformer according to given stream source.
	 * @param styleSource The source XSL, or <code>null</code> 
	 * @return
	 */
	public Transformer createTransformer(StreamSource styleSource);

}