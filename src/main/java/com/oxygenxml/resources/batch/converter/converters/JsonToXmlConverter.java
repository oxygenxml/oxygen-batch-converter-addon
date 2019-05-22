package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.utils.ConverterReaderUtils;

/**
 * Converter implementation for JSON to XML.
 * @author Cosmin Duna
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
	public String convert(File originalFileLocation, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		StringBuilder toReturn = new StringBuilder();
		String jsonContent = "";
		
		try {
			// Get the content to parse.
			if (contentReader == null) {
				jsonContent = ConverterFileUtils.readFile(originalFileLocation);
			} else {
				jsonContent = ConverterReaderUtils.getString(contentReader);
			}

			//create a JSONObject
			JSONObject jsonObject = new JSONObject(jsonContent);
			
			//convert the jsonOject in XML
			String xmlContent = XML.toString(jsonObject);

			if(jsonObject.length() != 1) {
				// Add root element.
				toReturn.append("<JSON>");
				toReturn.append(xmlContent);
				toReturn.append("</JSON>");
			} else {
				toReturn.append(xmlContent);
			}
			
		} catch (IOException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		} catch (JSONException e) {
			throw new TransformerException(e.getMessage(), e.getCause());
		}

		return toReturn.toString();

	}
}
