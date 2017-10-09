package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.oxygenxml.resources.batch.converter.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Converter implementation for JSON to XML.
 * @author intern4
 *
 */
public class JsonToXmlConverter implements Converter {

	/**
	 * Convert JSON to XML.
	 * 
	 * @param originalFile
	 *          The JSON file.
	 * @param contentReader
	 *          Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @return The converted XML content in String format or null if conversion process failed.
	 * @throws TransformerException
	 */
	@Override
	public String convert(File originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		String toReturn = null;
		String jsonContent = "";
		
		try {
			// Get the content to parse.
			if (contentReader == null) {
				jsonContent = ConverterFileUtils.readFile(originalFileLocation);
			} else {
				int intValueOfChar;
				while ((intValueOfChar = contentReader.read()) != -1) {
					jsonContent += (char) intValueOfChar;
				}
			}

			JSONObject jsonObject = new JSONObject(jsonContent);
			toReturn = XML.toString(jsonObject);

		} catch (IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		} catch (JSONException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		}

		return toReturn;

	}
}
