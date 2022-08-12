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
	
  /**
   * Get the maximum heading level for creating DITA Topics when converting to DITA.
   * 
   * @return the maximum heading level for creating DITA Topics when converting to DITA.
   */
  @Override
  default Integer getMaxHeadingLevelForCreatingTopics() {
    return UserInputsProviderUtil.getMaxHeadingLevelForCreatingTopicsOption();
  }
  
  /**
   * The format shouldn't be added on Batch Converter add-on
   *  
   * @return <code>null</code> because the format shouldn't be added on Batch Converter add-on .
   */
  @Override
  default String getFormatForSameTypeReferences() {
    return null;
  }
}
