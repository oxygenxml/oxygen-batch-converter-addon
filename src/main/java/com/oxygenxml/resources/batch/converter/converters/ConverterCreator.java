package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

/**
 * Creator of Converter
 * @author intern4
 *
 */
public class ConverterCreator {

	/**
	 * Create a converter according to given converter type.
	 * @param converterType The type of converter {@link ConverterTypes}
	 * @return
	 */
	public static Converter create(String converterType){
		
		
		if(ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return new MarkdownToXhmlConverter();
		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType)){
			return new HtmlToXhtmlConverter();
		}
		else if(ConverterTypes.HTML_TO_DITA.equals(converterType)){
			return new HtmlToDitaConverter();
		}
		else if(ConverterTypes.MD_TO_DITA.equals(converterType)){
			return new MarkdownToDitaTransformer();
		}
		else if(ConverterTypes.XML_TO_JSON.equals(converterType)){
			return new XmlToJsonConverter();
		}
		else if(ConverterTypes.JSON_TO_XML.equals(converterType)){
			return new JsonToXmlConverter();
		}
		return null;
	}
}
