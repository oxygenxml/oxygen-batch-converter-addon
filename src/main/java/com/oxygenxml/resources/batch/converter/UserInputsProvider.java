package com.oxygenxml.resources.batch.converter;

import com.oxygenxml.batch.converter.core.ConversionInputsProvider;

/**
 * Provider for the user inputs like input files, output directory and another options.
 * 
 * @author cosmin_duna
 *
 */
public interface UserInputsProvider extends ConversionInputsProvider {

	/**
	 * Return if the converted file must be opened.
	 * @return <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public boolean mustOpenConvertedFiles();
	
}
