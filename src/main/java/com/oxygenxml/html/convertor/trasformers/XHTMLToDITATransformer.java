package com.oxygenxml.html.convertor.trasformers;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.common.Heuristics;
import nu.validator.htmlparser.sax.HtmlParser;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Class for convert XHTML to DITA.
 * @author intern4
 *
 */
public class XHTMLToDITATransformer implements com.oxygenxml.html.convertor.trasformers.Transformer{

	/**
	 * Convert the XHTML document from the given URL in DITA.
	 * 
	 * @param originalFileLocation
	 *          The URL location of document.
	 * @param contentReader
	 *          Reader of the document.
	 * @return The conversion in DITA.
	 * @throws TransformerException
	 */
	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerCreator transformerCreator) throws TransformerException{
		
		//get the htmlParser
		HtmlParser parser = new HtmlParser();
		
		parser.setDoctypeExpectation(DoctypeExpectation.AUTO);
		parser.setHeuristics(Heuristics.ICU);

		// get the xsl path from oxygen
		String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess()
				.expandEditorVariables("${frameworks}", null);
		xslPath = xslPath + "/dita/resources/xhtml2ditaDriver.xsl";

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);

		//get the trasformFactory property 
		String property = System.getProperty("javax.xml.transform.TransformerFactory");
		
		//set the trasformFactory property to "com.saxonica.config.EnterpriseTransformerFactory"	
		System.setProperty("javax.xml.transform.TransformerFactory", "com.saxonica.config.EnterpriseTransformerFactory");
		
		
		try{
			final StreamSource src = new StreamSource(xslPath);
		
			//create the transformer
			Transformer transformer = transformerCreator.createTransformer(src);
			
			//set the parameter of transformer
			transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
			transformer.setParameter("context.path.names", "topic");
	
			//convert the document
			transformer.transform(new StreamSource(contentReader, originalFileLocation.toString()), result);
		
		} finally {
			//return the initial property of trasformerFactory
			if (property == null) {
				System.getProperties().remove("javax.xml.transform.TransformerFactory");
			} else {
				System.setProperty("javax.xml.transform.TransformerFactory", property);
			}
		}
		
		//add an id on root(topic)
		String ditaContent = sw.toString();
		int indexOfTopicTag = ditaContent.indexOf("topic") + 5;

		ditaContent = ditaContent.substring(0, indexOfTopicTag) + " id=\"topicID\"" + ditaContent.substring(indexOfTopicTag);  
		
		return ditaContent;
	}
	
}
