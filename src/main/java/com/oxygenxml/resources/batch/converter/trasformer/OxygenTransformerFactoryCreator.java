package com.oxygenxml.resources.batch.converter.trasformer;

import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.reporter.OxygenStatusReporter;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

/**
 * Oxygen transformer factory.
 * @author intern4
 *
 */
public class OxygenTransformerFactoryCreator implements TransformerFactoryCreator{

	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(OxygenStatusReporter.class);
	
	/**
	 * Create a transformer using  XMLUtilAccess.
	 */
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
			logger.debug(e.getMessage(), e);
		}
	
		
		return transformer;
				
	}

}
