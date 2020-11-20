package com.oxygenxml.resources.batch.converter.extensions;

import com.oxygenxml.resources.batch.converter.ConversionFormatUtil;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
/**
 * Getter for extensions.
 * @author Cosmin Duna
 *
 */
public class ExtensionGetter {

	/**
	 * Private constructor.
	 */
	 private ExtensionGetter() {
	    throw new IllegalStateException("Utility class");
	  }
	
	 /**
	   * Get the input extensions according to given conversion formats.
	   * @param inputFormat    The input format
	   * @param outputFormat   The output format.
	   * @return A vector with extensions or <code>null</code> if isn't declared a extension for given conversion formats.
	   */
	  public static String[] getInputExtension(String inputFormat, String outputFormat) {
	    String[] toRet = null;
	    String converterType = ConversionFormatUtil.getConverterType(inputFormat, outputFormat);
	    if(converterType != null) {
	      toRet = getInputExtension(converterType);
	    }
	    return toRet;
	  }
	 
	/**
	 * Get the input extensions according to given converter type.
	 * @param converterType The converter type.
	 * @return A vector with extensions or <code>null</code> if isn't declared a extension for given converter type.
	 */
	public static String[] getInputExtension(String converterType) {

		if(ConverterTypes.HTML_TO_DITA.equals(converterType) || ConverterTypes.HTML_TO_XHTML.equals(converterType)
				|| ConverterTypes.HTML_TO_DB4.equals(converterType) || ConverterTypes.HTML_TO_DB5.equals(converterType)){
			return FileExtensionType.INPUT_HTML_EXTENSIONS;

		}
		else if(ConverterTypes.MD_TO_DITA.equals(converterType) || ConverterTypes.MD_TO_XHTML.equals(converterType) 
				|| ConverterTypes.MD_TO_DB5.equals(converterType) || ConverterTypes.MD_TO_DB4.equals(converterType)){
			return FileExtensionType.INPUT_MD_EXTENSIONS;
		}
		else if(ConverterTypes.XML_TO_JSON.equals(converterType)) {
			return FileExtensionType.INPUT_XML_EXTENSIONS;
		}
		else if(ConverterTypes.JSON_TO_XML.equals(converterType)){
			return FileExtensionType.INPUT_JSON_EXTENSIONS;
		}
		else if(ConverterTypes.EXCEL_TO_DITA.equals(converterType)) {
			return FileExtensionType.INPUT_EXCEL_EXTENSIONS;
		}
		else if(ConverterTypes.WORD_TO_XHTML.equals(converterType) 
				|| ConverterTypes.WORD_TO_DITA.equals(converterType) 
				|| ConverterTypes.WORD_TO_DB4.equals(converterType) 
				|| ConverterTypes.WORD_TO_DB5.equals(converterType) ) {
			return FileExtensionType.INPUT_WORD_EXTENSIONS;
		}
		return new String[]{};
		
	}

	

	/**
	 * Get the output extension according to given converter type.
	 * @param converterType The converter type.
	 * @return The extension in String format or <code>null</code> if isn't declared a extension for given converter type.
	 */
	public static String getOutputExtension(String converterType){
	
		if(ConverterTypes.HTML_TO_DITA.equals(converterType) 
				|| ConverterTypes.MD_TO_DITA.equals(converterType)
				|| ConverterTypes.EXCEL_TO_DITA.equals(converterType) 
				|| ConverterTypes.WORD_TO_DITA.equals(converterType) ){
			return FileExtensionType.DITA_OUTPUT_EXTENSION;
		}
		else if(ConverterTypes.HTML_TO_XHTML.equals(converterType) 
				|| ConverterTypes.MD_TO_XHTML.equals(converterType) 
				|| ConverterTypes.WORD_TO_XHTML.equals(converterType)){
			return FileExtensionType.XHTML_OUTPUT_EXTENSION;
		}
		else if(ConverterTypes.XML_TO_JSON.equals(converterType)) {
			return FileExtensionType.JSON_OUTPUT_EXTENSION;
		}
		else if(ConverterTypes.JSON_TO_XML.equals(converterType) 
				|| ConverterTypes.MD_TO_DB5.equals(converterType) 
				|| ConverterTypes.MD_TO_DB4.equals(converterType) 
				|| ConverterTypes.HTML_TO_DB4.equals(converterType) 
				|| ConverterTypes.HTML_TO_DB5.equals(converterType)
				|| ConverterTypes.WORD_TO_DB4.equals(converterType)
				|| ConverterTypes.WORD_TO_DB5.equals(converterType)){
			return FileExtensionType.XML_OUTPUT_EXTENSION;
		} 
			return null;
	}
		
}
