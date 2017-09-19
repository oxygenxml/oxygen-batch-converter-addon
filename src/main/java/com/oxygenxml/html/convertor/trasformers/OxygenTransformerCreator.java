package com.oxygenxml.html.convertor.trasformers;

import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

public class OxygenTransformerCreator implements TransformerCreator{

	@Override
	public Transformer createTransformer(StreamSource streamSource) {
		Transformer transformer = null;
		
		if(streamSource == null){
			streamSource = new StreamSource(new StringReader(
					  "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
					  + "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
					  "    exclude-result-prefixes=\"xs\"\n" + "    version=\"2.0\">\n" +
					  "    <xsl:template match=\"node() | @*\">\n" + "        <xsl:copy>\n" +
					  "            <xsl:apply-templates select=\"node() | @*\"/>\n" +
					  "        </xsl:copy>\n" + "    </xsl:template>\n" + "    \n" +
					  "</xsl:stylesheet>"));
		}
		
		
		try {
			transformer = PluginWorkspaceProvider.getPluginWorkspace().getXMLUtilAccess().
				  createXSLTTransformer(streamSource , null,
				  	  XMLUtilAccess.TRANSFORMER_SAXON_PROFESSIONAL_EDITION);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
	
		
		return transformer;
				
	}

}
