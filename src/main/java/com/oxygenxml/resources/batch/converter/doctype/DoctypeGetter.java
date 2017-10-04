package com.oxygenxml.resources.batch.converter.doctype;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class DoctypeGetter {

	public static String getPublicDoctype(String converterType) {

		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_DITA.equals(converterType)){
			return Doctypes.DOCTYPE_PUBLIC_DITA;
		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return Doctypes.DOCTYPE_PUBLIC_XHTML;
		}
			return null;
	}
		
	
	public static String getSystemDoctype(String converterType){
		
		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_DITA.equals(converterType)){
			return Doctypes.DOCTYPE_SYSTEM_DITA;
		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType)){
			return Doctypes.DOCTYPE_SYSTEM_XHTML;
		}
			return null;
	}
}
