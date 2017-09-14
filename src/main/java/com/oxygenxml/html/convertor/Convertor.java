package com.oxygenxml.html.convertor;

import java.util.List;

/**
 * Make the conversion.
 * @author intern4
 *
 */
public interface Convertor {

	/**
	 * Convert the given input files and write them in given output folder.
	 * @param inputFiles	The input files.
	 * @param outputFolder The output folder.
	 * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
	 */
	public boolean convertFiles(List<String> inputFiles, String outputFolder); 
}
