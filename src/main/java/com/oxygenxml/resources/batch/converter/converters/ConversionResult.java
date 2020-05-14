package com.oxygenxml.resources.batch.converter.converters;

/**
 * The result of the conversion.
 * 
 * @author cosmin_duna
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
	 * The imposed extension for the output file.
	 */
	String imposedOutputFileExtension;
	
	/**
	 * This flag is used by {@link PipelineConverter}.
	 * When it's <code>true</code> this conversion result 
	 * doesn't change the processed content and it's better to let this as it is.
	 * It's <code>false</code> when we should take account of this result.
	 */
	private boolean shouldKeepTheLastResult = false;
	
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
	 * @param imposedOutputFileExtension The imposed extension for the output file. <code>null</code> to not impose a system document type.
	 */
	public ConversionResult(String convertedContent, String imposedPublicDoctype,
	    String imposedSystemDoctype, String imposedOutputFileExtension) {
		this.convertedContent = convertedContent;
		this.imposedPublicDoctype = imposedPublicDoctype;
		this.imposedSystemDoctype = imposedSystemDoctype;
		this.imposedOutputFileExtension = imposedOutputFileExtension;
	}

	/**
   * Constructor.
   * 
   * @param convertedContent        The converted content.
   * @param imposedPublicDoctype    Imposed public document type. <code>null</code> to not impose a public document type.
   * @param imposedSystemDoctype    Imposed system document type. <code>null</code> to not impose a system document type.
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
	
  /**
   * @return Imposed extension of the output file. Can be <code>null</code> in a extention was not imposed.
   */
	public String getImposedOutputFileExtension() {
    return imposedOutputFileExtension;
  }
	
	/**
	 * Set to keep the last conversion result, because this conversion result 
   * doesn't change the processed content.
	 */
	public void setToKeepTheLastResult() {
	  shouldKeepTheLastResult = true;
	}
	
	/**
   * This flag is used by {@link PipelineConverter}.
   * @return When return <code>true</code> this conversion result 
   * doesn't change the processed content and it's better to let this as it is.
   * When it's <code>false</code> we should take account of this result.
   */
	public boolean shouldKeepTheLastResult() {
    return shouldKeepTheLastResult;
  }
}
