package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class ConverterCreator {

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
		
		return null;
	}
}
