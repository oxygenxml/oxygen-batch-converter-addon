package com.oxygenxml.resources.batch.converter.converters;

import javax.xml.transform.Transformer;

import com.oxygenxml.resources.batch.converter.doctype.Doctypes;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

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
    // get the XSL path from oxygen
    String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess().expandEditorVariables("${frameworks}",
        null);
    return xslPath + "/docbook/resources/xhtml2db4Driver.xsl";
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
