package com.oxygenxml.resources.batch.converter.converters;

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
  protected Converter[] getUsedConverters() {
    return new Converter[] {
        new HtmlToXhtmlConverter(),
        new HTML5Cleaner(),
        new XHTMLToDITAConverter()
    };
  }
}
