package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.util.Arrays;
import java.util.List;

import ro.sync.exml.plugin.PluginExtension;

/**
 * Contributes the Batch Documents Converter AI functions through the "AIFunctions" extension point.
 */
public class BatchConverterAIFunctionsExtension implements PluginExtension {
  /**
   * Gets the AI functions contributed by this add-on.
   * <p>
   *
   * @return The contributed AI functions, or an empty list when the AI functions API is not
   *         available in the current Oxygen version.
   */
  public List<Object> getExternalAIFunctions() {
    return Arrays.asList(new BatchConvertorAITool());
  }
}
