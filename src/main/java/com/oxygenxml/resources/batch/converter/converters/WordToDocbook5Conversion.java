package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Converter implementation for Word to DocBook5.
 * 
 * @author cosmin_duna
 */
public class WordToDocbook5Conversion extends PipelineConverter{
  
  /**
   * Get the converters used in Word to DocBook5 conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    return new Converter[] {
        new WordToXHTMLConverter(),
        new HtmlToDocbook5Converter()
    };
  }
}
