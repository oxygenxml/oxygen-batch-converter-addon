package com.oxygenxml.html.convertor;

public interface ConvertorInteractor {

	/**
	 * Get the type of input.
	 * @return The type of input.
	 */
	public String getInputType();
	
	/**
	 * Set the type of input.
	 * @param type The type of input.
	 */
	public void setInputType(String type);


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
