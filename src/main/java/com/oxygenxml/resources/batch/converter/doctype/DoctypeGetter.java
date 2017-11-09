package com.oxygenxml.resources.batch.converter.doctype;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

/**
 * Getter for document type.
 * @author intern4
 *
 */
public class DoctypeGetter {

	/**
	 * Get the public document type according to given converter type.
	 * @param converterType The converter type.
	 * @return The public document type or a empty String if isn't declared a doctype for given converter type.
	 */
	public static String getPublicDoctype(String converterType) {

		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_DITA.equals(converterType)){
			return Doctypes.DOCTYPE_PUBLIC_DITA;
		}
			return "";
	}
		
	
	/**
	 * Get the System document type according to given converter type.
	 * @param converterType The converter type.
	 * @return The system document type or a empty String if isn't declared a doctype for given converter type..
	 */
	public static String getSystemDoctype(String converterType){
		
		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_DITA.equals(converterType)){
			return Doctypes.DOCTYPE_SYSTEM_DITA;
		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return Doctypes.DOCTYPE_SYSTEM_XHTML;
		}
			return "";
	}
}
