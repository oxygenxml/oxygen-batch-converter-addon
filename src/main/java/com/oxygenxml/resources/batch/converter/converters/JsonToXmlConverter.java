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

public class JsonToXmlConverter implements Converter {

	@Override
	public String convert(File originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		String jsonContent = "";
		try {
			if (contentReader == null) {
				jsonContent = ConverterFileUtils.readFile(originalFileLocation);
			} 
			else {
				int intValueOfChar;
				while ((intValueOfChar = contentReader.read()) != -1) {
					jsonContent += (char) intValueOfChar;
				}
			}

			JSONObject jsonObject = new JSONObject(jsonContent);

			return XML.toString(jsonObject);

		} catch (IOException e) {
			// TODO poate trimit la user
			e.printStackTrace();
		} catch (JSONException e) {
			throw new TransformerException(e.getMessage());
		}

		return null;

	}
}
