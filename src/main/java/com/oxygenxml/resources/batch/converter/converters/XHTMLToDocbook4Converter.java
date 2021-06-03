package com.oxygenxml.resources.batch.converter.converters;

import java.net.URL;

import javax.xml.transform.Transformer;

import com.oxygenxml.resources.batch.converter.doctype.Doctypes;

/**
 * Converter implementation for XHTML to Docbook5
 * @author Cosmin Duna
 *
 */
public class XHTMLToDocbook4Converter extends StylesheetConverter {
  
  /**
   * @see StylesheetConverter#getStylesheetPath()
   */
  @Override
  public String getStylesheetPath() {
    URL xsltURL = getClass().getClassLoader().getResource("stylesheets/docbook/xhtml2db4Driver.xsl");
    return xsltURL.toExternalForm();
  }
  
  /**
   * @see StylesheetConverter#setTransformationParam(Transformer)
   */
  @Override
  public void setTransformationParam(Transformer transformer) {
    transformer.setParameter("context.path.names", "article");
    transformer.setParameter("context.path.uris", "http://docbook.org/ns/docbook");
    transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
    transformer.setParameter("wrapMultipleSectionsInARoot", Boolean.TRUE);
  }
  
  /**
   * @see StylesheetConverter#processConversionResult(String)
   */
  @Override
  public ConversionResult processConversionResult(String dbContent) {
    final ConversionResult conversionResult;
    if (dbContent.contains("mml:math")) {
      conversionResult = new ConversionResult(
          dbContent, Doctypes.DOCTYPE_PUBLIC_DB4_MAHTML, Doctypes.DOCTYPE_SYSTEM_DB4_MATHML);
    } else {
      conversionResult = new ConversionResult(dbContent);
    }
    return conversionResult;
    
  }
}
