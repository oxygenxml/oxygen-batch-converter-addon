package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.util.List;

/**
 * Interactor with batch converter.
 * @author Cosmin Duna
 *
 */
public interface BatchConverterInteractor {

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
	 * Set the output folder path.
	 * @param text The path of output folder.
	 */
	public void setOutputFolder(String text);
	

	/**
	 * Return if the converted file must be opened.
	 * @return <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public boolean mustOpenConvertedFiles();
	
	/**
	 * Set if the converted file must be opened.
	 * @param <code>true</code> if converted files must be opened, <code>false</code>otherwise.
	 */
	public void setOpenConvertedFiles(boolean state);
	
	/**
	 * Set enable/ disable the convert button.
	 * @param state <code>true</code> to set enable, <code>false</code> to set disable.
	 */
	public void setEnableConvert(boolean state);
}
