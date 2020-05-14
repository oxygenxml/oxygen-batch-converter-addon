package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;

/**
 * Converter implementation for Markdown to XHTML
 * 
 * @author cosmin_duna
 */
public class MarkdownToXhmlConverter extends PipelineConverter{

  /**
   * Get the converters used in Markdown to XHTML conversion.
   */
  @Override
  protected Converter[] getUsedConverters(UserInputsProvider userInputsProvider) {
    return new Converter[] {
        new MarkdownToHtmlConverter(),
        new HtmlToXhtmlConverter()
    };
  }
}
