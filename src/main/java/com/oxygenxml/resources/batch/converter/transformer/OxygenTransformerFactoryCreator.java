package com.oxygenxml.resources.batch.converter.transformer;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

/**
 * Oxygen transformer factory.
 * @author Cosmin Duna
 *
 */
public class OxygenTransformerFactoryCreator implements TransformerFactoryCreator{

  /**
   * Create a transformer using  XMLUtilAccess.
   * @throws TransformerConfigurationException 
   */
  @Override
  public Transformer createTransformer(StreamSource styleSource) throws TransformerConfigurationException {
    if(styleSource == null){
      styleSource = new StreamSource(new StringReader(
            "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
            + "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "    exclude-result-prefixes=\"xs\"\n" + "    version=\"2.0\">\n" +
            "    <xsl:template match=\"node() | @*\">\n" + "        <xsl:copy>\n" +
            "            <xsl:apply-templates select=\"node() | @*\"/>\n" +
            "        </xsl:copy>\n" + "    </xsl:template>\n" + "    \n" +
            "</xsl:stylesheet>"));
    }
    
    String version = PluginWorkspaceProvider.getPluginWorkspace().getVersion();
    boolean shouldUseHE = false;
    if(version != null) {
      String[] versionItems = version.split("\\.");
      if(versionItems.length > 0 && Integer.valueOf(versionItems[0]) >= 23) {
        shouldUseHE = true;
      }
    }
      
    return PluginWorkspaceProvider.getPluginWorkspace().getXMLUtilAccess().
        createXSLTTransformer(styleSource , null,
            shouldUseHE ? XMLUtilAccess.TRANSFORMER_SAXON_HOME_EDITION
                : XMLUtilAccess.TRANSFORMER_SAXON_PROFESSIONAL_EDITION);
  }

}