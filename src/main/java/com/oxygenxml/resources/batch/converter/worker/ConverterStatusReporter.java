package com.oxygenxml.resources.batch.converter.worker;

import java.io.File;
import java.util.List;

/**
 * Report the status of the conversion process.
 * 
 * @author cosmin_duna
 */
public interface ConverterStatusReporter {
  
  /**
   * Notify that the conversion process starts.
   */
  public void conversionStarted();
  
  /**
   * Notify that the conversion process starts for the given input file.
   * 
   * @param inputFile The file foreach the conversion process starts
   */
  public void conversionInProgress(File inputFile);
  
  /**
   * Notify that the conversion process has finished.
   * 
   * @param resultedDocuments The documents resulted in conversion process.
   * @param outputDirectory The output dir that contains resulted documents.
   */
  public void conversionFinished(List<File> resultedDocuments, File outputDirectory);
}
