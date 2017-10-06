package com.oxygenxml.resources.batch.converter.converters;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

public class JsonToXmlConverter implements Converter {

	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		String jsonContent;
		try {
			jsonContent = ConverterUtils.getUrlContents(originalFileLocation);

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
