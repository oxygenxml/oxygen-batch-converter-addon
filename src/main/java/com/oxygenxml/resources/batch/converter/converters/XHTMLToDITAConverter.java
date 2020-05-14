package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.doctype.DitaConstants;
import com.oxygenxml.resources.batch.converter.doctype.Doctypes;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.common.Heuristics;
import nu.validator.htmlparser.sax.HtmlParser;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Converter implementation for XHTML to DITA
 * 
 * @author Cosmin Duna
 *
 */
public class XHTMLToDITAConverter implements Converter {

  /**
   * The local name of root element.
   */
  private static final String TOPIC_ROOT_ELEMENT_NAME = "topic";

  /**
   * The attributes from the topic root element.
   */
  private static final String TOPIC_ATTRIBUTES =  " id=\"topicID";
  
	/**
	 * Convert the given XHTML to DITA.
	 * 
	 * @param originalFile
	 *          The XHTML file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @param transformerCreator  A transformer creator.
	 * @param userInputsProvider  Provider for the options set by user.          
	 * @return The conversion in DITA.
	 * @throws TransformerException
	 */
	@Override
	public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
			throws TransformerException {

		// get the htmlParser
		HtmlParser parser = new HtmlParser();

		//configure the parser
		parser.setDoctypeExpectation(DoctypeExpectation.AUTO);
		parser.setHeuristics(Heuristics.ICU);

		// get the XSL path from oxygen
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
		transformer.setParameter("context.path.names", TOPIC_ROOT_ELEMENT_NAME);
		transformer.setParameter("wrapMultipleSectionsInARoot", Boolean.TRUE);

		final ConversionResult conversionResult;
		try {
				// convert the document
				transformer.transform(new StreamSource(contentReader, originalFile.toURI().toString()), result);
		
			// add an id on root(topic)
			String ditaContent = sw.toString();
			
			ditaContent = addIdForTopics(ditaContent);
			
			if(ditaContent.startsWith(DitaConstants.COMPOSITE_ROOT_ELEMENT)) {
				conversionResult = new ConversionResult(
						ditaContent, Doctypes.DOCTYPE_PUBLIC_DITA_COMPOSITE, Doctypes.DOCTYPE_SYSTEM_DITA_COMPOSITE);
			} else {
				conversionResult = new ConversionResult(ditaContent);
			}
			
		}catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}
		
		return conversionResult;
	}
	
	/**
	 * Add id attribute on topic elements.
	 * 
	 * @param ditaContent The DITA content.
	 * 
	 * @return The updated DITA content.
	 */
	private String addIdForTopics(String ditaContent) {
		String topicStartElement = '<' + TOPIC_ROOT_ELEMENT_NAME;
		int topicStartElementLenght = topicStartElement.length();
		StringBuilder contentToReturn = new StringBuilder();
		
 		int indexOfTopicTag = ditaContent.indexOf(topicStartElement);
		int beginIndex = 0;
		int topicsCounter = 1;
		while (indexOfTopicTag != -1) {
			indexOfTopicTag += topicStartElementLenght;
			contentToReturn.append(ditaContent.substring(beginIndex, indexOfTopicTag));
			// Add the id attribute.
			contentToReturn.append(TOPIC_ATTRIBUTES).append(topicsCounter).append("\"");
			beginIndex = indexOfTopicTag;
			indexOfTopicTag = ditaContent.indexOf(topicStartElement, indexOfTopicTag);
			topicsCounter++;
		}
		
		contentToReturn.append(ditaContent.substring(beginIndex));
		return contentToReturn.toString();
	}
	
}