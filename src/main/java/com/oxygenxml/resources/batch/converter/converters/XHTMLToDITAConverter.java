package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

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
 * Converter implementation for XHTML to DITA
 * 
 * @author Cosmin Duna
 *
 */
public class XHTMLToDITAConverter implements Converter {

  /**
   * The local name of root element.
   */
  private static final String ROOT_ELEMENT_NAME = "topic";

  /**
   * The attributes from the topic root element.
   */
  private static final String TOPIC_ATTRIBUTES =  " id=\"topicID\"";
  
	/**
	 * Convert the given XHTML to DITA.
	 * 
	 * @param originalFile
	 *          The XHTML file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The conversion in DITA.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		String ditaContent ="";
		
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
		transformer.setParameter("context.path.names", ROOT_ELEMENT_NAME);

		try {
				// convert the document
				transformer.transform(new StreamSource(contentReader, originalFile.toURI().toString()), result);
		
			// add an id on root(topic)
			ditaContent = sw.toString();
			
			int indexOfTopicTag = ditaContent.indexOf(ROOT_ELEMENT_NAME);
			
			if(indexOfTopicTag != -1){
				indexOfTopicTag += ROOT_ELEMENT_NAME.length();
			  // Add the topic attributes(id).
			  StringBuilder sb = new StringBuilder();
			  sb.append(ditaContent.substring(0, indexOfTopicTag));
			  sb.append(TOPIC_ATTRIBUTES);
			  sb.append(ditaContent.substring(indexOfTopicTag));
			  ditaContent = sb.toString();
			}
			
		}catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}
		return ditaContent;
	}
	
}