package com.oxygenxml.resources.batch.converter.extensions;

/**
 * Provider for file extensions suported by the converter.
 * This class is used from the "Batch Converter" script from Oxygen. Do NOT delete it.
 * 
 * @author cosmin_duna
 */
public class ExtensionGetter { 

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private ExtensionGetter() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }

  /**
   * Get the input extensions according to given conversion formats.
   * @param inputFormat    The input format
   * @param outputFormat   The output format.
   * @return A vector with extensions or <code>null</code> if isn't declared a extension for given conversion formats.
   */
  public static String[] getInputExtension(String inputFormat, String outputFormat) {
    return com.oxygenxml.batch.converter.core.extensions.ExtensionGetter.getInputExtension(inputFormat, outputFormat);
  }

  /**
   * Get the input extensions according to given converter type.
   * @param converterType The converter type.
   * @return A vector with extensions or <code>null</code> if isn't declared a extension for given converter type.
   */
  public static String[] getInputExtension(String converterType) {
    return  com.oxygenxml.batch.converter.core.extensions.ExtensionGetter.getInputExtension(converterType);
  }
}
