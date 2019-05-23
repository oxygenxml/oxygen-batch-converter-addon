package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.w3c.dom.Document;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;
import org.zwobble.mammoth.images.ImageConverter;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

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
	public String convert(
			File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
					throws TransformerException {
		String convertedContent= "";
		
		try {
			FileImageManager imageManager = new FileImageManager(baseDir);
			if (originalFile.getAbsolutePath().toLowerCase().endsWith(DOCX_EXTENSION)) {
				convertedContent = convertDocxFile(originalFile, imageManager);
			} else {
				convertedContent = convertDocFile(originalFile, imageManager);
			}
		} catch (Exception e) {
			throw new TransformerException(e.getMessage() , e.getCause());
		} 
		return convertedContent;
	}

	/**
	 * Convert the given docx file to HTML. 
	 * 
	 * @param file 						The docx file to be converted.
	 * @param imagesManager		An images manager. It's used to convert images.

	 * @throws IOException 
	 */
	public String convertDocxFile(File file, ImageConverter.ImgElement imagesManager) throws IOException{
		DocumentConverter converter = new DocumentConverter()
				.addStyleMap("p[style-name='Title'] => h1:fresh\n" +
						"p[style-name='Heading 1'] => h2:fresh\n" +
						"p[style-name='Heading 2'] => h3:fresh\n" +
						"p[style-name='Heading 3'] => h4:fresh\n" +
						"p[style-name='Heading 4'] => h5:fresh\n" +
						"p[style-name='Heading 5'] => h6:fresh\n" +
						"p[style-name='Heading 6'] => p:fresh\n" +
						"u => u:fresh\n")
				.imageConverter(imagesManager);
		
		Result<String> result = converter.convertToHtml(file);
		if(logger.isDebugEnabled()) {
			logger.debug("Warnings from the conversion process: " + result.getWarnings());
		}
		
		return wrapHtmlBodyContent(result.getValue());
	}

	/**
	 * Convert the given doc file to HTML. 
	 * 
	 * @param file 						The doc file to be converted.
	 * @param picturesManager		An images manager. It's used to convert images.

	 * @throws IOException 
	 */
	public String convertDocFile(File file, PicturesManager picturesManager) throws Exception{
		HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(file);

		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		wordToHtmlConverter.setPicturesManager(picturesManager);

		wordToHtmlConverter.processDocument(wordDocument);
		Document htmlDocument = wordToHtmlConverter.getDocument();

		StringWriter stringWriter = new StringWriter();
		String html = "";
		try {
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(stringWriter);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
			serializer.setOutputProperty( OutputKeys.INDENT, "yes" );
			serializer.setOutputProperty( OutputKeys.METHOD, "xhtml" );
			serializer.transform(domSource, streamResult);
			html = URLEncoder.encode(stringWriter.toString(), "UTF-8");
		} finally {
			stringWriter.close();
		}

		return  URLDecoder.decode(html, "UTF-8");
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
}