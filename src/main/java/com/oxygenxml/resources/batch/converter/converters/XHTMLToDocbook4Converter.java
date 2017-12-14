package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

import nu.validator.htmlparser.common.DoctypeExpectation;
import nu.validator.htmlparser.common.Heuristics;
import nu.validator.htmlparser.sax.HtmlParser;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Converter implementation for XHTML to Docbook5
 * @author Cosmin Duna
 *
 */
public class XHTMLToDocbook4Converter implements Converter {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(XHTMLToDocbook4Converter.class.getName());

	/**
	 * Convert the given XHTML to Docbook5.
	 * 
	 * @param originalFile
	 *          The XHTML file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The conversion in Docbook5.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {
	  
	  
	  

		String docbookContent ="";
		
		// get the htmlParser
		HtmlParser parser = new HtmlParser();

		//configure the parser
		parser.setDoctypeExpectation(DoctypeExpectation.AUTO);
		parser.setHeuristics(Heuristics.ICU);

		// get the XSL path from oxygen
		String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess().expandEditorVariables("${frameworks}",
				null);
		xslPath = xslPath + "/docbook/resources/xhtml2db4Driver.xsl";
		
		System.out.println("DOCBOOK 4 Driver:" + xslPath);

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		final StreamSource src = new StreamSource(xslPath);
		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(src);

		// set the parameter of transformer
		transformer.setParameter("context.path.names", "article");
		transformer.setParameter("context.path.uris", "http://docbook.org/ns/docbook");
		transformer.setParameter("context.item.separator", ",");
		transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
		
		
		
		try {
				// convert the document
				transformer.transform(new StreamSource(contentReader, originalFile.toURI().toString()), result);
		
				docbookContent = sw.toString();
			
		}catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}

		return docbookContent;
	}

}
