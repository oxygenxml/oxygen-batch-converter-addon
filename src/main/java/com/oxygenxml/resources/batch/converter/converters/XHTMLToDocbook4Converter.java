package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.doctype.Doctypes;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Converter implementation for XHTML to Docbook5
 * @author Cosmin Duna
 *
 */
public class XHTMLToDocbook4Converter implements Converter {
  
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
	public ConversionResult convert(File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		String docbookContent ="";
		
		// get the XSL path from oxygen
		String xslPath = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess().expandEditorVariables("${frameworks}",
				null);
		xslPath = xslPath + "/docbook/resources/xhtml2db4Driver.xsl";
		
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		final StreamSource src = new StreamSource(xslPath);
		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(src);

		// set the parameter of transformer
		transformer.setParameter("context.path.names", "article");
		transformer.setParameter("context.path.uris", "http://docbook.org/ns/docbook");
		transformer.setParameter("replace.entire.root.contents", Boolean.TRUE);
		transformer.setParameter("invokedFromBatchConverter", Boolean.TRUE);
		
		
		try {
				// convert the document
				transformer.transform(new StreamSource(contentReader, originalFile.toURI().toString()), result);
		
				docbookContent = sw.toString();
			
		}catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}
		
		final ConversionResult conversionResult;
		if (docbookContent.contains("mml:math")) {
			conversionResult = new ConversionResult(
					docbookContent, Doctypes.DOCTYPE_PUBLIC_DB4_MAHTML, Doctypes.DOCTYPE_SYSTEM_DB4_MATHML);
		} else {
			conversionResult = new ConversionResult(docbookContent);
		}
		
		return conversionResult;
	}

}
