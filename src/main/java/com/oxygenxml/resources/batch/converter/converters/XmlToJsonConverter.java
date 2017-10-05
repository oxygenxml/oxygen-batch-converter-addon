package com.oxygenxml.resources.batch.converter.converters;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.json.JSONObject;
import org.json.XML;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;


public class XmlToJsonConverter implements Converter{

	@Override
	public String convert(URL originalFileLocation, Reader contentReader, TransformerFactoryCreator transformerCreator)
			throws TransformerException {

		Path path = Paths.get(originalFileLocation.getPath().substring(1));
		
		// Get the content to parse.
		try {
			String contentToParse;
			contentToParse = new String(Files.readAllBytes(path), "UTF-8");
			JSONObject jsonObj = XML.toJSONObject(contentToParse);
		
			return jsonObj.toString();
			
		} catch (UnsupportedEncodingException e) {
			//TODO trateaza exceptii
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
