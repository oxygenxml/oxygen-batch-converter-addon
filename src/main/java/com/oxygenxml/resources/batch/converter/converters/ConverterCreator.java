package com.oxygenxml.resources.batch.converter.converters;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class ConverterCreator {

	public static Converter create(String converterType){
		
		switch (converterType) {
		case ConverterTypes.MD_TO_XHTML:
			return new MarkdownToXhmlConverter();
		case ConverterTypes.HTML_TO_XHTML:
			return new HtmlToXhtmlConverter();
		case ConverterTypes.HTML_TO_DITA:
			return new HtmlToDitaConverter();
		case ConverterTypes.MD_TO_DITA:
			return new MarkdownToDitaTransformer();
			
		default:
			return null;
		}
	}
}
