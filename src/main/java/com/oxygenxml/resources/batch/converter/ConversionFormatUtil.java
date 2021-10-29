package com.oxygenxml.resources.batch.converter;

/**
 * Utility class for working with conversion formats.
 * This class is used from the "Batch Converter" script from Oxygen. Do NOT delete it.
 * 
 * @author cosmin_duna
 */
public final class ConversionFormatUtil {

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private ConversionFormatUtil() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }
  
  /**
   * Check if the given conversion format is supported.
   * 
   * @param inputFormat  The input format to check.
   * @param outputFormat The output format to check.
   * 
   * @return <code>true</code> if it's supported, <code>false</code> otherwise.
   */
  public static boolean isSupportedConversionFormat(String inputFormat, String outputFormat) {
    return com.oxygenxml.batch.converter.core.ConversionFormatUtil.isSupportedConversionFormat(inputFormat, outputFormat);
  }
}
