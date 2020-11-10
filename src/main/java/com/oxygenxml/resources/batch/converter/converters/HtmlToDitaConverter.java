package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Implementation of Converter for HTML to DITA.
 * 
 * @author cosmin_duna
 *
 */
public class HtmlToDitaConverter extends PipelineConverter{

  /**
   * Get the converters used in HTML to DITA conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    Converter ditaConverter = createDitaMapConverter(userInputsProvider);
    Converter[] converters = new Converter[] {
        new HtmlToXhtmlConverter(),
        new HTML5Cleaner(),
        new XHTMLToDITAConverter(),
        ditaConverter
    };
    return converters;
  }
}
