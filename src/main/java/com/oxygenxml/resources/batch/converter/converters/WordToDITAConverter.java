package com.oxygenxml.resources.batch.converter.converters;

/**
 * Converter implementation for Word to DITA.
 * 
 * @author cosmin_duna
 */
public class WordToDITAConverter extends PipelineConverter {
	
  /**
   * Get the converters used in Word to DITA conversion.
   */
  @Override
  protected Converter[] getUsedConverters() {
    return new Converter[] {
        new WordToXHTMLConverter(),
        new HtmlToDitaConverter()
    };
  }
}