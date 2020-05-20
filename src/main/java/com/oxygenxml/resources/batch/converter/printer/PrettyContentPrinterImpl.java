package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.oxygenxml.resources.batch.converter.converters.ConversionResult;
import com.oxygenxml.resources.batch.converter.doctype.DoctypeGetter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.PrettyPrintException;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

/**
 * Content pretty printer implementation.
 * 
 * @author Cosmin Duna
 *
 */
public class PrettyContentPrinterImpl implements ContentPrinter {
	
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(PrettyContentPrinterImpl.class);
	
	/**
	 * Prettify the given content and write in given output file.
	 * 
	 * @param conversionResult The conversion result.
	 * @param transformerCreator A transformer creator.
	 * @param converterType The type of converter.
	 * @param outputFile The output file.
	 * @param styleSource The source XSL, or <code>null</code> 
	 * @throws TransformerException
	 */
		public void print(ConversionResult conversionResult, TransformerFactoryCreator transformerCreator, String converterType,
				File outputFile,  StreamSource styleSource)
				throws TransformerException {

		// create the transformer
		Transformer transformer = transformerCreator.createTransformer(styleSource);

		// set the output properties
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		final String systemDocType = DoctypeGetter.getSystemDoctype(conversionResult, converterType);
		if(!systemDocType.isEmpty()){
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemDocType);
		}
		
		final String publicDocType = DoctypeGetter.getPublicDoctype(conversionResult, converterType);
		if(!publicDocType.isEmpty()){
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicDocType);
		}
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		
		StringReader convertedContent = new StringReader(conversionResult.getConvertedContent());
		try {
		  PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
		  if(pluginWorkspace != null) {
		    XMLUtilAccess xmlUtilAccess = pluginWorkspace.getXMLUtilAccess();
		    try {
		      String prettyPrintedContent = xmlUtilAccess.prettyPrint(
		          convertedContent, outputFile.toURI().toURL().toExternalForm());
		      convertedContent = new StringReader(prettyPrintedContent);
		    } catch (PrettyPrintException e ) {
		      logger.debug(e.getMessage(), e);
		    } catch (MalformedURLException e) {
		      logger.debug(e.getMessage(), e);
        }
		  } else {
		    // For TCs.
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		  }

		  InputSource inputSource = new InputSource(convertedContent);
		  transformer.transform(new SAXSource(inputSource), new StreamResult(outputFile));
		} catch (TransformerException e) {
			logger.debug(e.getMessage(), e);
			// Stop indenting and create the output file.
			SimpleContentPrinterImpl simpleContentPrinter = new SimpleContentPrinterImpl();
			simpleContentPrinter.print(
					conversionResult, transformerCreator, converterType, outputFile, styleSource);
		} finally {
      convertedContent.close();
    }
	}
}
