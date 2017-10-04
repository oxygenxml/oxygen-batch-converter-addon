package com.oxygenxml.resources.batch.converter.extensions;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class ExtensionGetter {

	public static String[] getInputExtension(String converterType) {

		switch (converterType) {
		case ConverterTypes.HTML_TO_DITA:
			return FileExtensionType.INPUT_HTML_EXTENSIONS;

		case ConverterTypes.HTML_TO_XHTML:
			return FileExtensionType.INPUT_HTML_EXTENSIONS;
			
		case ConverterTypes.MD_TO_DITA:
			return FileExtensionType.INPUT_MD_EXTENSIONS;
			
		case ConverterTypes.MD_TO_XHTML:
			return FileExtensionType.INPUT_MD_EXTENSIONS;
		default:
			return null;
		}
	}

	
	public static String getOutputExtension(String converterType){
	
		switch (converterType) {
		case ConverterTypes.HTML_TO_DITA:
			return FileExtensionType.DITA_OUTPUT_EXTENSION;

		case ConverterTypes.MD_TO_DITA:
			return FileExtensionType.DITA_OUTPUT_EXTENSION;
			
		case ConverterTypes.HTML_TO_XHTML:
			return FileExtensionType.XHTML_OUTPUT_EXTENSION;
			
		case ConverterTypes.MD_TO_XHTML:
			return FileExtensionType.XHTML_OUTPUT_EXTENSION;
		default:
			return null;
		}
	}
}
