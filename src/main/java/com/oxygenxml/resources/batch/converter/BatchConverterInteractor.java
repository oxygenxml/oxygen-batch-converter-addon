package com.oxygenxml.resources.batch.converter;

import java.util.List;

public interface BatchConverterInteractor {

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
	
	/**
	 * Set the output folder path.
	 * @param text The path of output folder.
	 */
	public void setOutputFolder(String text);
	
	
	/**
	 * Set enable/ disable the convert button.
	 * @param state <code>true</code> to set enable, <code>false</code> to set disable.
	 */
	public void setEnableConvert(boolean state);
}
