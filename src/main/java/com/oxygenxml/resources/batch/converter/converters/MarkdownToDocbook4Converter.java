package com.oxygenxml.resources.batch.converter.converters;

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
  protected Converter[] getUsedConverters() {
    return new Converter[] {
        new MarkdownToXhmlConverter(),
        new XHTMLToDocbook4Converter()
    };
  }
}
