package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.utils.ConverterReaderUtils;

/**
 * Converter implementation for XML to JSON.
 * @author Cosmin Duna
 *
 */
public class XmlToJsonConverter implements Converter {
	/**
	 * Convert XML to JSON.
	 * 
	 * @param originalFile
	 *          The XML file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @param transformerCreator  A transformer creator.
	 * @param userInputsProvider  Provider for the options set by user.
	 * @return The converted JSON content in String format or null if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
			throws TransformerException {

		String convertedContent = null;
		
		String contentToParse = "";

		try {
			// Get the content to parse.
			if (contentReader == null) {
				contentToParse = ConverterFileUtils.readFile(originalFile);
			} else {
				contentToParse = ConverterReaderUtils.getString(contentReader);
			}

			JSONObject jsonObj = XML.toJSONObject(contentToParse);

			convertedContent = jsonObj.toString(4, false);

		}
		catch (JSONException | IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		}

		return new ConversionResult(convertedContent);
	}
}
