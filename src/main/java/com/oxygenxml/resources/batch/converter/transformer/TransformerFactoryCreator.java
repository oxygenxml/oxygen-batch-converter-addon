package com.oxygenxml.resources.batch.converter.transformer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
   * @throws TransformerConfigurationException 
   */
  public Transformer createTransformer(StreamSource styleSource) throws TransformerConfigurationException;

}
