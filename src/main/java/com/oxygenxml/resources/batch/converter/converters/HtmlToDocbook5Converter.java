package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Converter implementation for HTML to Docbook5.
 * 
 * @author cosmin_duna
 */
public class HtmlToDocbook5Converter extends PipelineConverter {

  /**
   * Get the converters used in HTML to DocBook5 conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    return new Converter[] {
        new HtmlToXhtmlConverter(),
        new HTML5Cleaner(),
        new XHTMLToDocbook5Converter()
    };
  }
}
