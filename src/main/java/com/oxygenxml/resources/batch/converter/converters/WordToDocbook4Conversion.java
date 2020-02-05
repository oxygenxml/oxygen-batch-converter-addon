package com.oxygenxml.resources.batch.converter.converters;

/**
 * Converter implementation for Word to DocBook4.
 * 
 * @author cosmin_duna
 */
public class WordToDocbook4Conversion extends PipelineConverter{

  /**
   * Get the converters used in Word to DocBook4 conversion.
   */
  @Override
  protected Converter[] getUsedConverters() {
    return new Converter[] {
        new WordToXHTMLConverter(),
        new HtmlToDocbook4Converter()
    };
  }
}
