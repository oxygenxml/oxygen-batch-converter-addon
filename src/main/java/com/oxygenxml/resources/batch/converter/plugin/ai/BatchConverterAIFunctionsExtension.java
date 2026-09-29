/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.util.Arrays;
import java.util.List;

import ro.sync.exml.plugin.PluginExtension;

/**
 * Contributes the Batch Documents Converter AI functions through the "AIFunctions" extension point.
 *
 * @author vlad_greaca
 */
public class BatchConverterAIFunctionsExtension implements PluginExtension {
  /**
   * Gets the AI functions contributed by this add-on.
   * <p>
   * The returned elements implement <code>ro.sync.exml.plugin.ai.ExternalAIFunction</code>. The
   * declared type is deliberately generic so that this method can be verified on an Oxygen version
   * that does not provide that interface.
   *
   * @return The contributed AI functions. Never <code>null</code>.
   */
  public List<Object> getExternalAIFunctions() {
    return Arrays.asList(new BatchConvertorAITool());
  }
}
