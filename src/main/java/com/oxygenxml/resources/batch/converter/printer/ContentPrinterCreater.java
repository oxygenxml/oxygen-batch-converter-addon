package com.oxygenxml.resources.batch.converter.printer;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

public class ContentPrinterCreater {

	public static ContentPrinter create(String converterType){
		if(ConverterTypes.XML_TO_JSON.equals(converterType)){
			return new ContentPrinterImpl();
		}
		else return new ContentPrinterPrettyXmlImpl();
	}
}
