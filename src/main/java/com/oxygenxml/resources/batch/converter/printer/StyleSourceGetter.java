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
	 * XSL for ignoring extra attributes when printing.
	 */
	private static final String IGNORE_EXTRA_ATTRS_XSL= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + 
			"    xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\"\n" + 
			"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" + 
			"    exclude-result-prefixes=\"xs ditaarch\"\n" + 
			"    version=\"2.0\">\n" + 
			"\n" + 
			"    <xsl:template match=\"node()|@*\">\n" + 
			"        <xsl:copy>\n" + 
			"            <xsl:apply-templates select=\"node()|@*\"/>\n" + 
			"        </xsl:copy>\n" + 
			"    </xsl:template>\n" + 
			"    <xsl:template match=\"*/@class\" />\n" + 
			"    <xsl:template match=\"*/@ditaarch:DITAArchVersion\" />\n" + 
			"    <xsl:template match=\"*/@*[local-name() = 'ditaarch']\" />\n" + 
			"    <xsl:template match=\"*/@domains\" />\n" + 
			"</xsl:stylesheet>";
	
	/**
	 * Get the style source according to given converter type.
	 * @param converterType The type of converter.
	 * @return the style source
	 */
	public static StreamSource getStyleSource(String converterType){
		if(ConverterTypes.MD_TO_DITA.equals(converterType) 
		    || ConverterTypes.DOCBOOK_TO_DITA.equals(converterType)){
			//return a style source
		  System.out.println("IGNORE_EXTRA_ATTRS_XSL: " + IGNORE_EXTRA_ATTRS_XSL);
			return  new StreamSource(new StringReader(IGNORE_EXTRA_ATTRS_XSL));
		}	else{
			return null;
		}
	}
}
