package com.oxygenxml.html.convertor.trasformers;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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


	public static void prettifyAndPrint( Reader contentReader, File out, String systemDoctype, String publicDoctype, TransformerCreator transformerCreator) {
		 
		
		System.out.println("out " + out.toString());
		
		String property = null;
		
		try {
		
			Transformer transformer = transformerCreator.createTransformer(null);
				
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemDoctype);
				transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicDoctype);
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			InputSource inputSource = new InputSource(contentReader);

			out = FilePathGenerator.getFileWithCounter(out);
			
			System.out.println("new out: " + out.toString());
		
			//get the trasformFactory property 
			 property = System.getProperty("javax.xml.transform.TransformerFactory");
			
			//set the trasformFactory property to "com.saxonica.config.EnterpriseTransformerFactory"	
			System.setProperty("javax.xml.transform.TransformerFactory", "com.saxonica.config.EnterpriseTransformerFactory");
			
			
			transformer.transform(new SAXSource(inputSource), new StreamResult(out));

			System.out.println("dupa trasform");
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			//TODO send to user
			e.printStackTrace();
		}
		finally {
			//return the initial property of trasformerFactory
			if (property == null) {
				System.getProperties().remove("javax.xml.transform.TransformerFactory");
			} else {
				System.setProperty("javax.xml.transform.TransformerFactory", property);
			}
		}
		
	}
	
	

}
