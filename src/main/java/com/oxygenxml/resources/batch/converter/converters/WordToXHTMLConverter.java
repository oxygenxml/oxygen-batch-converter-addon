package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.w3c.dom.Document;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;
import org.zwobble.mammoth.images.ImageConverter;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.word.styles.WordStyleMapLoader;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Converter implementation for Word to HTML.
 * 
 * @author cosmin_duna
 */
public class WordToXHTMLConverter implements Converter {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(WordToXHTMLConverter.class);
	
	/**
	 * The extension of the docx files.
	 */
	private static final String DOCX_EXTENSION = "docx";

	@Override
	public ConversionResult convert(
			File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
					throws TransformerException {
		String convertedContent= "";
		
		try {
			FileImageManager imageManager = new FileImageManager(userInputsProvider.getOutputFolder());
			if (originalFile.getAbsolutePath().toLowerCase().endsWith(DOCX_EXTENSION)) {
				convertedContent = convertDocxFile(originalFile, imageManager, transformerCreator);
			} else {
				convertedContent = convertDocFile(originalFile, imageManager);
			}
		} catch (Exception e) {
			throw new TransformerException(e.getMessage() , e.getCause());
		} 
		return new ConversionResult(convertedContent);
	}

	/**
	 * Convert the given docx file to HTML. 
	 * 
	 * @param file 						The docx file to be converted.
	 * @param imagesManager		An images manager. It's used to convert images.

	 * @throws IOException 
	 */
	public String convertDocxFile(File file, ImageConverter.ImgElement imagesManager, TransformerFactoryCreator transformerFactory) throws IOException{
	  String styleMap;
    try {
      styleMap = WordStyleMapLoader.loadStyleMap();
    } catch (JAXBException e) {
      StringBuilder message = new StringBuilder();
      message.append("Invalid Word style map configuration file: ");
      if(e.getLinkedException() != null) {
        message.append(e.getLinkedException().getMessage());
      } else {
        message.append(e.getMessage());
      }
      throw new IOException(message.toString());
    }
	  DocumentConverter converter = new DocumentConverter()
	      .addStyleMap(styleMap)
				.imageConverter(imagesManager);
		
		Result<String> result = converter.convertToHtml(file);
		if(logger.isDebugEnabled()) {
		  logger.debug("Warnings from the conversion process: " + result.getWarnings());
		}
		
		String htmlContent = wrapHtmlBodyContent(result.getValue());
		
		if(htmlContent.contains("<!--<m:oMath")) {
			htmlContent = resolveMaths(htmlContent, transformerFactory);
		}
		
		return htmlContent;
	}

	/**
	 * Convert the given doc file to HTML. 
	 * 
	 * @param file 						The doc file to be converted.
	 * @param picturesManager		An images manager. It's used to convert images.

	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 */
	public String convertDocFile(File file, PicturesManager picturesManager) 
	    throws IOException, ParserConfigurationException, TransformerException {
		HWPFDocumentCore wordDocument = AbstractWordUtils.loadDoc(file);

		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()); //NOSONAR because we need an new instance here id:cwe, owasp-a4
		wordToHtmlConverter.setPicturesManager(picturesManager);

		wordToHtmlConverter.processDocument(wordDocument);
		Document htmlDocument = wordToHtmlConverter.getDocument();

		StringWriter stringWriter = new StringWriter();
		final String UTF = "UTF-8";
    try {
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(stringWriter);

			TransformerFactory tf = TransformerFactory.newInstance(); //NOSONAR because we need an new instance here id:cwe, owasp-a4
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty( OutputKeys.ENCODING, UTF );
			serializer.setOutputProperty( OutputKeys.INDENT, "yes" );
			serializer.setOutputProperty( OutputKeys.METHOD, "xhtml" );
			serializer.transform(domSource, streamResult);
		} finally {
			stringWriter.close();
		}

		String html = URLEncoder.encode(stringWriter.toString(), UTF);
		return  URLDecoder.decode(html, UTF);
	}
	
	/**
	 * Constructs a HTML structure, wrapping the given body content with body and html elements.
	 * 
	 * @param bodyContent The content of the body element.
	 * 
	 * @return A html structure that contains the given body content.
	 */
	private String wrapHtmlBodyContent(String bodyContent) {
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		htmlContent.append("<head><title/></head>\n<body>");
		htmlContent.append(bodyContent);
		htmlContent.append("\n</body>\n</html>");
		return htmlContent.toString();
	}
	
	/**
	 * Resolves the math equations from the given HTML content.
	 * 
	 * @param htmlContent					The html content.
	 * @param transformerCreator	The transformer creator.s
	 * 
	 * @return HTML content with MathML equations.
	 */
	private String resolveMaths(String htmlContent, TransformerFactoryCreator transformerCreator) {
		boolean wasXslResolved = false;
		final String teiXslPath = "http://www.tei-c.org/release/xml/tei/stylesheet/docx/from/omml2mml.xsl"; //NOSONAR

		PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
		if(pluginWorkspace != null) {
			URIResolver uriResolver = pluginWorkspace.getXMLUtilAccess().getURIResolver();
			try {
				Source resolved = uriResolver.resolve(teiXslPath, "");
				if(resolved != null) {
					wasXslResolved = true;
				}
			} catch (TransformerException e) {
				logger.debug(e.getException().getMessage(), e.getException());
			}
		}
		
		if(wasXslResolved) {
			StringBuilder xslt = new StringBuilder();
			xslt.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
					"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + 
					"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" + 
					"    xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n" + 
					"    exclude-result-prefixes=\"xs\"\n" + 
					"    version=\"3.0\">\n" + 
					"    <xsl:include href=\"");
			xslt.append(teiXslPath);
			xslt.append("\"/>\n" + 
					"    \n" + 
					"    <xsl:template match=\"node() | @*\">\n" + 
					"        <xsl:copy>\n" + 
					"            <xsl:apply-templates select=\"node() | @*\"/>\n" + 
					"        </xsl:copy>\n" + 
					"    </xsl:template>\n" + 
					"    <xsl:template match=\"m:oMath\" >\n" + 
					"        <math xmlns='http://www.w3.org/1998/Math/MathML'>\n" + 
					"            <xsl:apply-templates/>\n" + 
					"        </math>    \n" + 
					"    </xsl:template>\n" + 
					"    <xsl:template match=\"comment()[starts-with(., '&lt;m:oMath')]\">\n" + 
					"            <xsl:apply-templates select=\"parse-xml(.)\"/>\n" + 
					"    </xsl:template>\n" + 
					"</xsl:stylesheet>");

			final StreamSource src = new StreamSource(new StringReader(xslt.toString()));
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			
			try {
			  Transformer transformer = transformerCreator.createTransformer(src);
				transformer.transform(new StreamSource(new StringReader(htmlContent)), result);
				htmlContent = sw.toString();
			} catch (TransformerException e) {
				logger.debug(e.getMessage(), e);
			}
		}

		return htmlContent;
	}
}