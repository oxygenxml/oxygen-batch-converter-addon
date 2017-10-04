package com.oxygenxml.resources.batch.converter;

import java.net.URL;
import java.util.List;

/**
 * Batch converter.
 * @author intern4
 *
 */
public interface BatchConverter {

	/**
	 * Convert the given input files and write them in given output folder according to given convertorType.
	 * @param convertorType The converter type.
	 * @param inputFiles	The input files.
	 * @param outputFolder The output folder.
	 * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
	 */
	public boolean convertFiles(String convertorType, List<URL> inputFiles, String outputFolder); 
}
