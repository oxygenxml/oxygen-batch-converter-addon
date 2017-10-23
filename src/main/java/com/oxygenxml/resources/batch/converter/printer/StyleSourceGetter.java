package com.oxygenxml.resources.batch.converter.printer;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

/**
 * Getter for the style source.
 * @author intern4
 *
 */
public class StyleSourceGetter {

	/**
	 * Xsl for MD to DITA.
	 */
	private static String MD_TO_DITA_XSL= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\r\n" + 
			"    xmlns:ditaarch=\"http://dita.oasis-open.org/architecture/2005/\"\r\n" + 
			"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + 
			"    exclude-result-prefixes=\"xs\"\r\n" + 
			"    version=\"2.0\">\r\n" + 
			"\r\n" + 
			"    <xsl:template match=\"node()|@*\">\r\n" + 
			"        <xsl:copy>\r\n" + 
			"            <xsl:apply-templates select=\"node()|@*\"/>\r\n" + 
			"        </xsl:copy>\r\n" + 
			"    </xsl:template>\r\n" + 
			"    \r\n" + 
			"    <xsl:template match=\"topic/@class\" />\r\n" + 
			"    <xsl:template match=\"topic/@ditaarch:DITAArchVersion\" />\r\n" + 
			"    <xsl:template match=\"topic/@domains\" />\r\n" + 
			"    \r\n" + 
			"    \r\n" + 
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
		}
			//return null
		else return null;
	}
}
