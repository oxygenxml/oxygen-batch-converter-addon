package com.oxygenxml.resources.batch.converter.extensions;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class ExtensionGetter {

	public static String[] getInputExtension(String converterType) {

		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.HTML_TO_XHTML.equals(converterType)){
			return FileExtensionType.INPUT_HTML_EXTENSIONS;

		}
		else if(ConverterTypes.MD_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return FileExtensionType.INPUT_MD_EXTENSIONS;
		}
			return null;
		
	}

	
	public static String getOutputExtension(String converterType){
	
		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_DITA.equals(converterType)){
			return FileExtensionType.DITA_OUTPUT_EXTENSION;

		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return FileExtensionType.XHTML_OUTPUT_EXTENSION;
		}
			return null;
	}
		
}
