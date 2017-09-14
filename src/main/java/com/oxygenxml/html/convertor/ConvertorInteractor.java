package com.oxygenxml.html.convertor;

import java.util.List;

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

	/**
	 * Get the input files URLs in String format.
	 * @return List with input files URLs in String format.
	 */
	public List<String> getInputFiles();
	
	/**
	 * Get the output folder path.
	 * @return The path of output folder.
	 */
	public String getOutputFolder();
}
