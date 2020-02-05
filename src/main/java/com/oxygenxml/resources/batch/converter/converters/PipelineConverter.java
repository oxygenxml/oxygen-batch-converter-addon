package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * A complex converter with uses a pipeline of converters
 * 
 * @author cosmin_duna
 */
public abstract class PipelineConverter implements Converter{

  /**
   * Get the converters that will be used in conversion.
   * 
   * @return The used converters in order.
   */
  protected abstract Converter[] getUsedConverters();
  
  @Override
  public ConversionResult convert(File originalFile, Reader contentReader, File baseDir,
      TransformerFactoryCreator transformerCreator) throws TransformerException {

    ConversionResult result = new ConversionResult("");
    Converter[] converters = getUsedConverters();
    
    int noOfCoverters = converters.length;
    for (int i = 0; i < noOfCoverters; i++) {
      if(i != 0) {
        contentReader = new StringReader(result.getConvertedContent());
      }
      result = converters[i].convert(originalFile, contentReader, baseDir, transformerCreator);
    }
    
    return result;
  }
}
