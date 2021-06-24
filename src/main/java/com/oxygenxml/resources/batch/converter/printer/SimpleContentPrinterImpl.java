package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.converters.ConversionResult;
import com.oxygenxml.batch.converter.core.doctype.DoctypeGetter;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;

/**
 * A simple content printer implementation.
 * 
 * @author Cosmin Duna
 *
 */
public class SimpleContentPrinterImpl implements ContentPrinter {
	/**
	 * Logger
	 */
	 private static final Logger logger = Logger.getLogger(SimpleContentPrinterImpl.class);
	 
	 /**
	  * Pattern from extract the encoding from a xml file.
	  */
	 private static final Pattern ENCODING_PATTERN = Pattern.compile(
				"^[\\s]*<\\?xml[\\s]+version[\\s]*=[\\s]*\"1\\.0\"[\\s]+encoding=[\\s]*\"[\\s]*([^\\s\"]*)[\\s]*\"[\\s]*\\?>");
	 
	/**
	 * Print the given content in output file(The content isn't indented).
	 * 
	 * @param conversionResult
	 *          The conversion result.
	 * @param transformerCreator
	 *          A transformer creator.
	 * @param converterType
	 *          The type of converter.
	 * @param outputFile
	 *          The output file.
	 * @param styleSource This is not used.
	 * @throws TransformerException
	 */
	@Override
	public void print(ConversionResult conversionResult, TransformerFactoryCreator transformerCreator, String converterType,
			File outputFile, StreamSource styleSource) throws TransformerException {

		String encoding = "UTF-8";
		
		String contentToPrint = conversionResult.getConvertedContent();
		if(!(ConverterTypes.XML_TO_JSON.equals(converterType) ||
		    ConverterTypes.JSON_TO_YAML.equals(converterType) ||
		    ConverterTypes.YAML_TO_JSON.equals(converterType))) {
			String encodingLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			// Get the encoding from content to print.
			Matcher matcher = ENCODING_PATTERN.matcher(contentToPrint);
			if(matcher.find()) {
				encoding = matcher.group(1);
				encodingLine = matcher.group(0);
				//Delete encoding line.
				contentToPrint = matcher.replaceFirst("");
				contentToPrint = contentToPrint.replaceAll("^\\s", "");
			}
			// EXM-41083 -- Add the document type.
			contentToPrint = addEncodingAndDoctype(contentToPrint, encodingLine, converterType);
		}
		
		OutputStream outputStream = null;
		OutputStreamWriter writer = null;

		try {
			outputStream = new FileOutputStream(outputFile);
			writer = new OutputStreamWriter(outputStream, encoding);
			writer.write(contentToPrint);
			
		}
		catch (IOException e) {
			throw new TransformerException(e.getMessage());
		} 
		finally {
			try {
				if(writer != null){
					writer.close();
				}else{
					if(outputStream != null){
						outputStream.close();
					}
				}
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
			}
		}

	}
	
	/**
	 * Add the doctype and the encoding at the given content.
	 * @param content Content to modify.
	 * @param encodingLine The encoding line that should be add.
	 * @param convertedType The type of conversion.
	 * @return The content with doctype and encoding.
	 */
	private String addEncodingAndDoctype(String content, String encodingLine, String convertedType) {
		StringBuilder sb = new StringBuilder();
		// Add encoding.
		sb.append(encodingLine).append("\n");
		
		if(!content.contains("<!DOCTYPE")) {
			// Add doctype.
			String doctype = DoctypeGetter.getDoctype(convertedType);
			if(!doctype.isEmpty()) {
				sb.append(DoctypeGetter.getDoctype(convertedType)).append("\n");
			}
		}
		sb.append(content);

		return sb.toString();
	}
}
