package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Converter implementation for Markdown to Docbook5.
 * 
 * @author cosmin_duna
 */
public class MarkdownToDocbook5Converter extends PipelineConverter {
	
  /**
   * Get the converters used in Markdown to DocBook5 conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    return new Converter[] {
        new MarkdownToXhmlConverter(),
        new XHTMLToDocbook5Converter()
    };
  }
}
