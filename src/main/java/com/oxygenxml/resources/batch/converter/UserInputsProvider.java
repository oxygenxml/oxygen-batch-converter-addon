package com.oxygenxml.resources.batch.converter;

import java.io.File;

import com.oxygenxml.batch.converter.core.ConversionInputsProvider;

/**
 * Provider for the user inputs like input files, output directory and another options.
 * 
 * @author cosmin_duna
 *
 */
public abstract class UserInputsProvider implements ConversionInputsProvider {

	/**
	 * Return if the converted file must be opened.
	 * @return <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public abstract boolean mustOpenConvertedFiles();
	
  /**
   * The media output folder
   * @return The path of the media output folder.
   */
  @Override
  public File getMediaOutputFolder() {
    return new File(getOutputFolder(), "media/");
  }
}
