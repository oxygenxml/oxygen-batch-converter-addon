package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Converter implementation for Markdown to Docbook4.
 * 
 * @author cosmin_duna
 */
public class MarkdownToDocbook4Converter extends PipelineConverter {
  
  /**
   * Get the converters used in Markdown to DocBook4 conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    return new Converter[] {
        new MarkdownToXhmlConverter(),
        new XHTMLToDocbook4Converter()
    };
  }
}
