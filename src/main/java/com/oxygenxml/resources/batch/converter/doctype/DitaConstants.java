package com.oxygenxml.resources.batch.converter.doctype;

/**
 * Constants user in DITA documents.
 * 
 * @author cosmin_duna
 */
public class DitaConstants {

	/**
	 * Private constructor.
	 */
  private DitaConstants() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * The line of XML encoding declaration.
   */
  public static final String ENCODING_DECLARATION_LINE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
  
  /**
   * The root element of composite.
   */
  public static final String COMPOSITE_ROOT_ELEMENT = "<dita>";
}
