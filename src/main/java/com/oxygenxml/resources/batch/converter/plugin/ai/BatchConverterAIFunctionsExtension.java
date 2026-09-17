package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.exml.plugin.PluginExtension;

/**
 * Contributes the Batch Documents Converter AI functions through the "AIFunctions" extension point.
 * <p>
 * The add-on supports Oxygen 26.0 and newer, but the AI functions API
 * (<code>ro.sync.exml.plugin.ai.ExternalAIFunction</code>) was only added in Oxygen 27.1. This class
 * is therefore kept free of any reference to that API: it implements only the {@link PluginExtension}
 * marker interface and it does not mention the AI types in any signature, so it can always be loaded.
 * <p>
 * The actual function is created reflectively, so that on an older Oxygen the missing API is reported
 * as an empty list of functions instead of failing the load of the entire add-on.
 * <p>
 * Oxygen invokes <code>getExternalAIFunctions()</code> reflectively as well, see
 * <code>ExternalAIFunctionsProviderImpl</code>, so this class does not need to implement
 * <code>AIFunctionsPluginExtension</code>.
 */
public class BatchConverterAIFunctionsExtension implements PluginExtension {
  /**
   * Gets the AI functions contributed by this add-on.
   * <p>
   * The returned elements implement <code>ro.sync.exml.plugin.ai.ExternalAIFunction</code>. The
   * declared type is deliberately generic so that this method can be verified and invoked on an
   * Oxygen version that does not provide that interface.
   *
   * @return The contributed AI functions, or an empty list when the AI functions API is not
   *         available in the current Oxygen version. Never <code>null</code>.
   */
  public List<Object> getExternalAIFunctions() {
    return Arrays.asList(new AIConvertor());
  }
}
