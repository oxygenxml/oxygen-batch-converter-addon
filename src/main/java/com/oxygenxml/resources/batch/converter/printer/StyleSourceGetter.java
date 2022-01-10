package com.oxygenxml.resources.batch.converter.printer;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.batch.converter.core.ConverterTypes;

/**
 * Getter for the style source.
 * @author Cosmin Duna
 *
 */
public class StyleSourceGetter {
  /**
   * Private constructor.
   */
  private StyleSourceGetter() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * XSL for MD to DITA.
   */
  private static final String MD_TO_DITA_XSL= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
      "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + 
      "    xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\"\n" + 
      "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" + 
      "    exclude-result-prefixes=\"xs\"\n" + 
      "    version=\"2.0\">\n" + 
      "\n" + 
      "    <xsl:template match=\"node()|@*\">\n" + 
      "        <xsl:copy>\n" + 
      "            <xsl:apply-templates select=\"node()|@*\"/>\n" + 
      "        </xsl:copy>\n" + 
      "    </xsl:template>\n" + 
      "    <xsl:template match=\"topic/@class\" />\n" + 
      "    <xsl:template match=\"topic/@ditaarch:DITAArchVersion\" />\n" + 
      "    <xsl:template match=\"topic/@domains\" />\n" + 
      "</xsl:stylesheet>";
  
  /**
   * Get the style source according to given converter type.
   * @param converterType The type of converter.
   * @return the style source
   */
  public static StreamSource getStyleSource(String converterType){
    if(ConverterTypes.MD_TO_DITA.equals(converterType) ){
      //return a style source
      return  new StreamSource(new StringReader(MD_TO_DITA_XSL));
    } else{
      return null;
    }
  }
}