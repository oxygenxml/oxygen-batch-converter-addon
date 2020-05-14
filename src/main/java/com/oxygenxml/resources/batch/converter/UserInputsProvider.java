package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.util.List;

/**
 * Provider for the user inputs like input files, output directory and another options.
 * 
 * @author cosmin_duna
 *
 */
public interface UserInputsProvider {

	/**
	 * Get the input files URLs in String format.
	 * @return List with input files URLs in String format.
	 */
	public List<File> getInputFiles();
	
	/**
	 * Get the output folder path.
	 * @return The path of output folder.
	 */
	public File getOutputFolder();

	/**
	 * Return if the converted file must be opened.
	 * @return <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public boolean mustOpenConvertedFiles();
	
	/**
	 * Get the boolean value chose by user for the given additional option.
	 * 
	 * @param additionalOptionId The id of the additional option.
	 * 
	 * @return <code>true</code> of <code>false</code> according to user decision. 
	 *         <code>null</code> if the option is not configured in the dialog.
	 */
	public Boolean getAdditionalOptionValue(String additionalOptionId);
	
}
