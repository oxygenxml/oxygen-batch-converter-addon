package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.common.Heuristics;
import nu.validator.htmlparser.sax.HtmlParser;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Class for convert XHTML to DITA.
 * 
 * @author intern4
 *
 */
public class XHTMLToDITAConverter implements com.oxygenxml.resources.batch.converter.converters.Converter {

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
	public String convert(File originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		System.out.println("xhtml -> dita");
		String ditaContent ="";
		
		// get the htmlParser
		HtmlParser parser = new HtmlParser();

		parser.setDoctypeExpectation(DoctypeExpectation.AUTO);
		parser.setHeuristics(Heuristics.ICU);

		// get the xsl path from oxygen
		String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess().expandEditorVariables("${frameworks}",
				null);
		
		xslPath = xslPath + "/dita/resources/xhtml2ditaDriver.xsl";

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);

		final StreamSource src = new StreamSource(xslPath);

		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(src);

		// set the parameter of transformer
		transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
		transformer.setParameter("context.path.names", "topic");

		System.out.println("aici");
		try {
				// convert the document
				transformer.transform(new StreamSource(contentReader, originalFileLocation.toURI().toString()), result);
		
			// add an id on root(topic)
			ditaContent = sw.toString();
			
			int indexOfTopicTag = ditaContent.indexOf("topic");
			
			if(indexOfTopicTag != -1){
				ditaContent = ditaContent.substring(0, indexOfTopicTag + 5) + " id=\"topicID\""
						+ ditaContent.substring(indexOfTopicTag + 5);
			}
			
			
		}catch (TransformerException e) {
			e.printStackTrace();
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}
		

		return ditaContent;
	}
}