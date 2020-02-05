package com.oxygenxml.resources.batch.converter.converters;

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
  protected Converter[] getUsedConverters() {
    return new Converter[] {
        new HtmlToXhtmlConverter(),
        new HTML5Cleaner(),
        new XHTMLToDocbook5Converter()
    };
  }
}
