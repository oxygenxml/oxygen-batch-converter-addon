package com.oxygenxml.resources.batch.converter.printer;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

/**
 * Class for create the printer that write the converted content.
 * @author Cosmin Duna
 *
 */
public class ContentPrinterCreater {

	/**
	 * Private constructor.
	 */
  private ContentPrinterCreater() {
    throw new IllegalStateException("Utility class");
  }
	
	/**
	 * Create a ContentPrinter according to given converter type.
	 * @param converterType The type of converter.
	 * @return The contentPrinter
	 */
	public static ContentPrinter create(String converterType){
		if(ConverterTypes.XML_TO_JSON.equals(converterType) || ConverterTypes.MD_TO_DB5.equals(converterType)){
			//return a printer that does't edit(indent) the content of conversion.
			return new SimpleContentPrinterImpl();
		}
			//return a printer that prettify the content.
		else return new PrettyContentPrinterImpl();
	}
}
