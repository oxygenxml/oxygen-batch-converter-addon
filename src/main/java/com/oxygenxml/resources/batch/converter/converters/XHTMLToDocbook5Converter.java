package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Converter implementation for XHTML to Docbook5
 * @author Cosmin Duna
 *
 */
public class XHTMLToDocbook5Converter implements Converter {
  /**
   * The NAMESPACE attribute and value from the article root element.
   */
  private static final String  NAMESPACE  = "xmlns=\"http://docbook.org/ns/docbook\"";
  /**
   * The SCHEMA attribute and value from the article root element.
   */
  private static final String  SCHEMA  = "xmlns:xlink=\"http://www.w3.org/1999/xlink\"";
	
  /**
   * The VERSION attribute and value from the article root element.
   */
  private static final String  VERSION  = "version=\"5.0\"";
  
  /**
   * The local name of root element.
   */
  private static final String ROOT_ELEMENT = "article";
  
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
		
		// get the XSL path from oxygen
		String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess().expandEditorVariables("${frameworks}",
				null);
		xslPath = xslPath + "/docbook/resources/xhtml2db5Driver.xsl";

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);

		final StreamSource src = new StreamSource(xslPath);

		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(src);

		// set the parameter of transformer
		transformer.setParameter("context.path.names", ROOT_ELEMENT);
		transformer.setParameter("context.path.uris", "http://docbook.org/ns/docbook");
		transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
		
		try {
				// convert the document
				transformer.transform(new StreamSource(contentReader, originalFile.toURI().toString()), result);
		
				docbookContent = sw.toString();
			
		}catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}

		docbookContent = updateArticleRootAttributes(docbookContent);
		return docbookContent;
	}

	/**
	 * Add the namespace, schema and version if those don't exist.
	 * @param documentContent The document content to be updated.
	 * @return A document with namespace, schema and version.
	 */
	private String updateArticleRootAttributes(String documentContent) {
		int indexOfRootTag = documentContent.indexOf(ROOT_ELEMENT);
		if(indexOfRootTag != -1){
			int rootCloseTag = documentContent.indexOf('>');
			String rootContent = documentContent.substring(0, rootCloseTag);
			if(!rootContent.contains(NAMESPACE)) {
				indexOfRootTag += ROOT_ELEMENT.length();
				StringBuilder sb = new StringBuilder();
				sb.append(documentContent.substring(0, indexOfRootTag ));
				sb.append(NAMESPACE).append(" ");
				sb.append(documentContent.substring(indexOfRootTag));
				documentContent = sb.toString();
			}
			if(!rootContent.contains(SCHEMA)) {
				int newRootCloseTag = documentContent.indexOf('>'); 
				StringBuilder sb = new StringBuilder();
				sb.append(documentContent.substring(0, newRootCloseTag));
				sb.append(" ").append(SCHEMA);
				sb.append(documentContent.substring(newRootCloseTag));
				documentContent = sb.toString();
			}
			if(!rootContent.contains(VERSION)) {
				int newRootCloseTag = documentContent.indexOf('>'); 
				StringBuilder sb = new StringBuilder();
				sb.append(documentContent.substring(0, newRootCloseTag));
				sb.append(" ").append(VERSION);
				sb.append(documentContent.substring(newRootCloseTag));
				documentContent = sb.toString();
			}
		}
		return documentContent;
	}
}
