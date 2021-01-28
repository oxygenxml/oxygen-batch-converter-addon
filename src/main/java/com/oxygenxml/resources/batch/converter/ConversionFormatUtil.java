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
   * HTML document name for conversion
   */
  private static final String HTML = "html";
  
  /**
   * XHTML document name for conversion
   */
  private static final String XHTML = "xhtml";
  
  /**
   * DITA document name for conversion
   */
  private static final String DITA = "dita";
  
  /**
   * Excel document name for conversion
   */
  private static final String EXCEL = "excel";
  
  /**
   * Word document name for conversion
   */
  private static final String WORD = "word";
  
  /**
   * XML document name for conversion
   */
  private static final String XML = "xml";
  
  /**
   * JSON document name for conversion
   */
  private static final String JSON = "json";
  
  /**
   * YAML document name for conversion
   */
  private static final String YAML = "yaml";
  
  /**
   * MARKDOWN document name for conversion
   */
  private static final String MARKDOWN = "markdown";
  
  /**
   * MD document name for conversion
   */
  private static final String MD = "md";
  
  /**
   * DB5 document name for conversion
   */
  private static final String DB5 = "db5";
  
  /**
   * DB4 document name for conversion
   */
  private static final String DB4 = "db4";
  
  /**
   * DOCBOOK4 document name for conversion
   */
  private static final String DOCBOOK4 = "docbook4";
  
  /**
   * DOCBOOK5 document name for conversion
   */
  private static final String DOCBOOK5 = "docbook5";
  
  /**
   * Private constructor.
   */
  private ConversionFormatUtil() {
    // Avoid instantiation
  }
  
  /**
   * A map between conversion formats and converter types
   */
  private static final Map<ConversionFormat, String> CONVERSION_FORMATS_TO_CONVERTER_TYPES = new HashMap<>();
  static {
   
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(HTML, DITA), ConverterTypes.HTML_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MD, DITA), ConverterTypes.MD_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MARKDOWN, DITA), ConverterTypes.MD_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(EXCEL, DITA), ConverterTypes.EXCEL_TO_DITA);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(WORD, DITA), ConverterTypes.WORD_TO_DITA);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(XML, JSON), ConverterTypes.XML_TO_JSON);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(JSON, XML), ConverterTypes.JSON_TO_XML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(YAML, JSON), ConverterTypes.YAML_TO_JSON);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(JSON, YAML), ConverterTypes.JSON_TO_YAML);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(HTML, XHTML), ConverterTypes.HTML_TO_XHTML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MD, XHTML), ConverterTypes.MD_TO_XHTML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MARKDOWN, XHTML), ConverterTypes.MD_TO_XHTML);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(WORD, XHTML), ConverterTypes.WORD_TO_XHTML);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(HTML, DB4), ConverterTypes.HTML_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(HTML, DOCBOOK4), ConverterTypes.HTML_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MD, DB4), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MD, DOCBOOK4), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MARKDOWN, DB4), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MARKDOWN, DOCBOOK4), ConverterTypes.MD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(WORD, DB4), ConverterTypes.WORD_TO_DB4);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(WORD, DOCBOOK4), ConverterTypes.WORD_TO_DB4);
    
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(HTML, DB5), ConverterTypes.HTML_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(HTML, DOCBOOK5), ConverterTypes.HTML_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MD, DB5), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MD, DOCBOOK5), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MARKDOWN, DB5), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(MARKDOWN, DOCBOOK5), ConverterTypes.MD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(WORD, DB5), ConverterTypes.WORD_TO_DB5);
    CONVERSION_FORMATS_TO_CONVERTER_TYPES.put(new ConversionFormat(WORD, DOCBOOK5), ConverterTypes.WORD_TO_DB5);
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
