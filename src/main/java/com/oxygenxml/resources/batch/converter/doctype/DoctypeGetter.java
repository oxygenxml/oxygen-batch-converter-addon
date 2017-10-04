package com.oxygenxml.resources.batch.converter.doctype;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class DoctypeGetter {

	public static String getPublicDoctype(String converterType) {

		switch (converterType) {
		case ConverterTypes.HTML_TO_DITA:
			return Doctypes.DOCTYPE_PUBLIC_DITA;

		case ConverterTypes.MD_TO_DITA:
			return Doctypes.DOCTYPE_PUBLIC_DITA;
			
		case ConverterTypes.HTML_TO_XHTML:
			return Doctypes.DOCTYPE_PUBLIC_XHTML;
			
		case ConverterTypes.MD_TO_XHTML:
			return Doctypes.DOCTYPE_PUBLIC_XHTML;
		default:
			return null;
		}
	}

	
	public static String getSystemDoctype(String converterType){
	
		switch (converterType) {
		case ConverterTypes.HTML_TO_DITA:
			return Doctypes.DOCTYPE_SYSTEM_DITA;

		case ConverterTypes.MD_TO_DITA:
			return Doctypes.DOCTYPE_SYSTEM_DITA;
			
		case ConverterTypes.HTML_TO_XHTML:
			return Doctypes.DOCTYPE_SYSTEM_XHTML;
			
		case ConverterTypes.MD_TO_XHTML:
			return Doctypes.DOCTYPE_SYSTEM_XHTML;
		default:
			return null;
		}
	}
}
