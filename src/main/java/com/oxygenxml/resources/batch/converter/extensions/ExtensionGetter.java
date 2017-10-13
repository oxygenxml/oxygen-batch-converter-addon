package com.oxygenxml.resources.batch.converter.extensions;

import com.oxygenxml.resources.batch.converter.ConverterTypes;
/**
 * Getter for extensions.
 * @author intern4
 *
 */
public class ExtensionGetter {

	/**
	 * Get the input extensions according to given converter type.
	 * @param converterType The converter type.
	 * @return A vector with extensions or <code>null</code> if isn't declared a extension for given converter type.
	 */
	public static String[] getInputExtension(String converterType) {

		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.HTML_TO_XHTML.equals(converterType)){
			return FileExtensionType.INPUT_HTML_EXTENSIONS;

		}
		else if(ConverterTypes.MD_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType) 
				|| ConverterTypes.MD_TO_DB5.equals(converterType)){
			return FileExtensionType.INPUT_MD_EXTENSIONS;
		}
		else if(ConverterTypes.XML_TO_JSON.equals(converterType)) {
			return FileExtensionType.INPUT_XML_EXTENSIONS;
		}
		else if(ConverterTypes.JSON_TO_XML.equals(converterType)){
			return FileExtensionType.INPUT_JSON_EXTENSIONS;
		}
		
			return null;
		
	}

	

	/**
	 * Get the output extension according to given converter type.
	 * @param converterType The converter type.
	 * @return The extension in String format or <code>null</code> if isn't declared a extension for given converter type.
	 */
	public static String getOutputExtension(String converterType){
	
		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_DITA.equals(converterType)){
			return FileExtensionType.DITA_OUTPUT_EXTENSION;
		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return FileExtensionType.XHTML_OUTPUT_EXTENSION;
		}
		else if(ConverterTypes.XML_TO_JSON.equals(converterType)) {
			return FileExtensionType.JSON_OUTPUT_EXTENSION;
		}
		else if(ConverterTypes.JSON_TO_XML.equals(converterType) || ConverterTypes.MD_TO_DB5.equals(converterType)){
			return FileExtensionType.XML_OUTPUT_EXTENSION;
		}
			return null;
	}
		
}
