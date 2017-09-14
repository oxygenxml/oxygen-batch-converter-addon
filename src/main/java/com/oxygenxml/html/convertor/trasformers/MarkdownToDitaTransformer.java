package com.oxygenxml.html.convertor.trasformers;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.elovirta.dita.markdown.MarkdownReader;

/**
 * Class that use com.elovirta.dita.markdown.MarkdownReader for convert Markdown to DITA.
 * @author intern4
 *
 */
public class MarkdownToDitaTransformer implements com.oxygenxml.html.convertor.trasformers.Transformer{

	/**
	 * Convert the markdown document from the given URL in DITA.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document.
	 * @return The conversion in DITA.
	 * @throws TransformerException
	 */
	@Override
	public String convert(URL originalFileLocation, Reader contentReader) throws TransformerException  {
		// content to return
		String toReturn = null;
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer trasformer = transformerFactory.newTransformer();
	
		/*
		 * Transformer createXSLTTransformer =
		 * PluginWorkspaceProvider.getPluginWorkspace().getXMLUtilAccess().
		 * createXSLTTransformer( new StreamSource(new StringReader(
		 * "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
		 * + "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
		 * "    exclude-result-prefixes=\"xs\"\n" + "    version=\"2.0\">\n" +
		 * "    <xsl:template match=\"node() | @*\">\n" + "        <xsl:copy>\n" +
		 * "            <xsl:apply-templates select=\"node() | @*\"/>\n" +
		 * "        </xsl:copy>\n" + "    </xsl:template>\n" + "    \n" +
		 * "</xsl:stylesheet>")), null,
		 * XMLUtilAccess.TRANSFORMER_SAXON_PROFESSIONAL_EDITION);
		 */

		//get the trasformFactory property 
		String	property = 	System.getProperty("javax.xml.transform.TransformerFactory");
		
		//set the trasformFactory property to "com.saxonica.config.EnterpriseTransformerFactory"
		System.setProperty("javax.xml.transform.TransformerFactory", "com.saxonica.config.EnterpriseTransformerFactory");
		
		try {
			//reader for markdown document
			final MarkdownReader r = new MarkdownReader();
			
			//input source of document to convert
			final InputSource i = new InputSource(originalFileLocation.toString());
			
			StringWriter sw = new StringWriter();
			StreamResult res = new StreamResult(sw);
			
			//convert the document
			trasformer.transform(new SAXSource(r, i), res);
			
			//get the converted content
			toReturn = sw.toString();

		} finally {
			//return the initial property of trasformerFactory
			if(property == null){
				System.getProperties().remove("javax.xml.transform.TransformerFactory");
			}
			else{
				System.setProperty("javax.xml.transform.TransformerFactory", property);
			}
		}
		
		
		return toReturn;
	}
	
	
}

