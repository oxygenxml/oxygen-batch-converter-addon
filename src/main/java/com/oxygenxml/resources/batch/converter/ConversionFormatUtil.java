package com.oxygenxml.resources.batch.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to work with conversion formats.
 * 
 * @author cosmin_duna
 *
 */
public class ConversionFormatUtil {

  /**
   * Private constructor.
   */
  private ConversionFormatUtil() {
    // Avoid instantiation
  }
  
  /**
   * A map between conversion formats and converter types
   */
  private static final Map<ConversionFormat, String> CONVERSION_FORMATS_TO_CONVERTER_TYPES = new HashMap<ConversionFormat, String>();
  static {
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("html", "dita"), ConverterTypes.HTML_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("md", "dita"), ConverterTypes.MD_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("markdown", "dita"), ConverterTypes.MD_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("excel", "dita"), ConverterTypes.EXCEL_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("word", "dita"), ConverterTypes.WORD_TO_DITA);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("xml", "json"), ConverterTypes.XML_TO_JSON);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("json", "xml"), ConverterTypes.JSON_TO_XML);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("html", "xhtml"), ConverterTypes.HTML_TO_XHTML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("md", "xhtml"), ConverterTypes.MD_TO_XHTML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("markdown", "xhtml"), ConverterTypes.MD_TO_XHTML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("word", "xhtml"), ConverterTypes.WORD_TO_XHTML);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("html", "db4"), ConverterTypes.HTML_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("html", "docbook4"), ConverterTypes.HTML_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("md", "db4"), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("md", "docbook4"), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("markdown", "db4"), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("markdown", "docbook4"), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("word", "db4"), ConverterTypes.WORD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("word", "docbook4"), ConverterTypes.WORD_TO_DB4);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("html", "db5"), ConverterTypes.HTML_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("html", "docbook5"), ConverterTypes.HTML_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("md", "db5"), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("md", "docbook5"), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("markdown", "db5"), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("markdown", "docbook5"), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("word", "db5"), ConverterTypes.WORD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat("word", "docbook5"), ConverterTypes.WORD_TO_DB5);
  }
  
  /**
   * Get the converter type for the given conversion format.
   * 
   * @param inputFormat  The input format to check.
   * @param outputFormat The output format to check.
   * 
   * @return The converter type for the given conversion format,
   *       <code>null</code> when conversion format in not supported or is incorrect.
   */
 public static String getConverterType(String inputFormat, String outputFormat) {
   return CONVERSION_FORMATS_TO_CONVERTER_TYPES.get(
       new ConversionFormat(inputFormat.toLowerCase(), outputFormat.toLowerCase()));
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
   return CONVERSION_FORMATS_TO_CONVERTER_TYPES.containsKey(
       new ConversionFormat(inputFormat.toLowerCase(), outputFormat.toLowerCase()));
 }
}
