package com.oxygenxml.html.convertor;

public interface ConvertorInteractor {

	/**
	 * Get the type of output.
	 * @return The type of output.
	 */
	public String getOutputType();
	
	/**
	 * Set the type of output.
	 * @param type The type of output.
	 */
	public void setOutputType(String type);

}
