package com.oxygenxml.resources.batch.converter;

/**
 * The types of converter.
 * @author Cosmin Duna
 *
 */
public class ConverterTypes {
	
	/**
	 * Private constructor.
	 */
  private ConverterTypes() {
    throw new IllegalStateException("Utility class");
  }
	
	/**
	 * HTML -> XHTML
	 */
	public static final String HTML_TO_XHTML = "_html.to.xhtml";
	/**
	 * HTML -> DITA
	 */
	public static final String HTML_TO_DITA = "_html.to.dita";
	/**
	 * MARKDOWN -> XHTML
	 */
	public static final String MD_TO_XHTML = "_md.to.xhtml";
	/**
	 * MARKDOWN -> DITA
	 */
	public static final String MD_TO_DITA = "_md.to.dita";
	/**
	 * XML -> JSON
	 */
	public static final String XML_TO_JSON = "_xml.to.json";
	/**
	 * JSON -> XML
	 */
	public static final String JSON_TO_XML = "_json.to.xml";
	
	/**
	 * MARKDOWN -> Docbook4
	 */
	public static final String MD_TO_DB4 = "_md.to.db4";
	
	 /**
   * MARKDOWN -> Docbook5
   */
  public static final String MD_TO_DB5 = "_md.to.db5";

}
