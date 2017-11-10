package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.util.List;

/**
 * Batch converter.
 * @author Cosmin Duna
 *
 */
public interface BatchConverter {

	/**
	 * Convert the given input files and write them in given output folder according to given convertorType.
	 * @param convertorType The converter type.
	 * @param inputFiles	The input files.
	 * @param outputFolder The output folder.
	 * @param openConvertedFiles <code>true</code> to open the converted files in Oxygen, <code>false</code> otherwise.
	 * @return <code>true</code> if the process of conversion was finished successfully, <code>false</code> otherwise.
	 */
	public boolean convertFiles(String convertorType, List<File> inputFiles, File outputFolder, boolean openConvertedFiles); 
}
