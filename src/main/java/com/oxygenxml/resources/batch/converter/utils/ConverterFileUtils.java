package com.oxygenxml.resources.batch.converter.utils;

import java.io.File;
import java.util.List;

/**
 * Utility class for working with files in converter.
 * This class is used from the "Batch Converter" script from Oxygen. Do NOT delete it.
 * 
 * @author cosmin_duna
 */
public final class ConverterFileUtils  {
    
  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private ConverterFileUtils() {
    // Private to avoid instantiations
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }
  
  /**
   * Recursive search for file according to extension list.
   * 
   * @param file
   *          The file or directory.
   * 
   * @param extensionsFiles
   *          The extensions.
   */ 
  public static List<File> getAllFiles(File file, List<String> extensionsFiles) {
    return com.oxygenxml.batch.converter.core.utils.ConverterFileUtils.getAllFiles(file, extensionsFiles);
  }
}
