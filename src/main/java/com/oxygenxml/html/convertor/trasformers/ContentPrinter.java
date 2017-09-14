package com.oxygenxml.html.convertor.trasformers;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

/**
 * Content printer
 * 
 * @author intern4
 *
 */
public class ContentPrinter {


	public static void prettifyAndPrint(String method, Reader contentReader, File out, String systemDoctype, String publicDoctype) {
		 
		try {
				Transformer transformer = PluginWorkspaceProvider.getPluginWorkspace().getXMLUtilAccess().
				  createXSLTTransformer( new StreamSource(new StringReader(
				  "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
				  + "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
				  "    exclude-result-prefixes=\"xs\"\n" + "    version=\"2.0\">\n" +
				  "    <xsl:template match=\"node() | @*\">\n" + "        <xsl:copy>\n" +
				  "            <xsl:apply-templates select=\"node() | @*\"/>\n" +
				  "        </xsl:copy>\n" + "    </xsl:template>\n" + "    \n" +
				  "</xsl:stylesheet>")), null,
				  XMLUtilAccess.TRANSFORMER_SAXON_PROFESSIONAL_EDITION);
			
				
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemDoctype);
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicDoctype);
				transformer.setOutputProperty(OutputKeys.METHOD, method);
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			InputSource inputSource = new InputSource(contentReader);

			out = FilePathGenerator.getFileWithCounter(out);
			
			transformer.transform(new SAXSource(inputSource), new StreamResult(out));

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	

}
