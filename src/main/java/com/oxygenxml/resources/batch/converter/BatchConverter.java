package com.oxygenxml.resources.batch.converter;

/**
 * Batch converter.
 * @author Cosmin Duna
 *
 */
public interface BatchConverter {

	/**
	 * Convert the given input files and write them in given output folder according to given convertorType.
	 * @param convertorType        The converter type.
	 * @param inputsProvider	     Provider for the user inputs like input files, output directory and another options.
	 * 
	 * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
	 */
	public boolean convertFiles(String convertorType, UserInputsProvider inputsProvider); 
	
	 /**
   * Convert the given input files and write them in given output folder according to given convertorType.
   * @param inputFormat     The input format to check.
   * @param outputFormat    The output format to check.
   * @param inputsProvider  Provider for the user inputs like input files, output directory and another options.
   * 
   * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
   */
  public boolean convertFiles(String inputFormat, String outputFormat, UserInputsProvider inputsProvider); 
}
