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

	/**
	 * Prettify the given content and write in given file.
	 * @param contentReader The content.
	 * @param out The output file.
	 * @param systemDoctype System doctype to be add in document.
	 * @param publicDoctype Public doctype to be add in document.	
	 * @param transformerCreator Trasformer factory.
	 * @throws TransformerException
	 */
	public static void prettifyAndPrint(Reader contentReader, File out, String systemDoctype, String publicDoctype,
			TransformerFactoryCreator transformerCreator) throws TransformerException  {

			//create the trasformer
			Transformer transformer = transformerCreator.createTransformer(null);

			//set the output properties
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemDoctype);
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicDoctype);
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			//get the input source
			InputSource inputSource = new InputSource(contentReader);

			//create a unique file path if actual exist 
			out = FilePathGenerator.getFileWithCounter(out);

			try {
				//prettify and print 
				transformer.transform(new SAXSource(inputSource), new StreamResult(out));
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
			}
	}

}
