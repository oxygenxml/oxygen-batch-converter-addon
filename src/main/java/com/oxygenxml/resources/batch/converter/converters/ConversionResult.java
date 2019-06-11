package com.oxygenxml.resources.batch.converter.converters;

/**
 * The result of the conversion.
 * 
 * @author cosmin_duna
 *
 */
public class ConversionResult {

	/**
	 * The converted content.
	 */
	String convertedContent;
	
	/**
	 * Imposed public document type.
	 */
	String imposedPublicDoctype;
	
	/**
	 * Imposed system document type.
	 */
	String imposedSystemDoctype;

	/**
	 * Constructor.
	 * 
	 * @param convertedContent The converted content.
	 */
	public ConversionResult(String convertedContent) {
		this.convertedContent = convertedContent;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param convertedContent 				The converted content.
	 * @param imposedPublicDoctype		Imposed public document type. <code>null</code> to not impose a public document type.
	 * @param imposedSystemDoctype		Imposed system document type. <code>null</code> to not impose a system document type.
	 */
	public ConversionResult(String convertedContent, String imposedPublicDoctype, String imposedSystemDoctype) {
		this.convertedContent = convertedContent;
		this.imposedPublicDoctype = imposedPublicDoctype;
		this.imposedSystemDoctype = imposedSystemDoctype;
	}

	/**
	 * @return The converted content.
	 */
	public String getConvertedContent() {
		return convertedContent != null ? convertedContent: "";
	}

	/**
	 * @return Imposed public document type. Can be <code>null</code>.
	 */
	public String getImposedPublicDoctype() {
		return imposedPublicDoctype;
	}

	/**
	 * @return Imposed system document type. Can be <code>null</code>.
	 */
	public String getImposedSystemDoctype() {
		return imposedSystemDoctype;
	}
}
