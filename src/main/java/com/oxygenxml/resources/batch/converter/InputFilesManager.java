package com.oxygenxml.resources.batch.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the list of input files and tracks which root directory each file
 * was collected from (when added via "Add Folder"). This allows the converter
 * to recreate the source folder hierarchy in the output directory.
 *
 * @author cosmin_duna
 */
public class InputFilesManager {

  /**
   * The ordered set of input files. LinkedHashSet gives O(1) duplicate checks
   * while preserving insertion order.
   */
  private final Set<File> inputFiles = new LinkedHashSet<>();

  /**
   * Maps each input file to the root directory it was collected from when added
   * via "Add Folder". Files added individually are not present in this map.
   */
  private final Map<File, File> fileToRootDirectory = new HashMap<>();

  /**
   * Add files collected from a folder, associating them with the given root directory.
   * If <code>rootDirectory</code> is <code>null</code>, the files are treated as
   * individually added (no structure to preserve).
   * Files already registered (e.g. added individually before this call) are not
   * overwritten — their existing state is preserved.
   *
   * @param files         The files to add.
   * @param rootDirectory The folder from which they were collected.
   */
  public void addFilesFromFolder(List<File> files, File rootDirectory) {
    if (rootDirectory == null) {
      addFiles(files);
      return;
    }
    for (File file : files) {
      if (inputFiles.add(file)) {
        fileToRootDirectory.put(file, rootDirectory);
      }
      // File already present: keep its current state (may have been added individually)
    }
  }

  /**
   * Add individually selected files (no root directory association).
   * If a file was previously added from a folder, its root mapping is removed
   * since the user is now explicitly treating it as an individual file.
   *
   * @param files The files to add.
   */
  public void addFiles(List<File> files) {
    for (File file : files) {
      if (!inputFiles.add(file)) {
        // File already present — remove any folder association.
        fileToRootDirectory.remove(file);
      }
    }
  }

  /**
   * Remove a file.
   *
   * @param file The file to remove.
   */
  public void removeFile(File file) {
    inputFiles.remove(file);
    fileToRootDirectory.remove(file);
  }

  /**
   * Remove all files.
   */
  public void clear() {
    inputFiles.clear();
    fileToRootDirectory.clear();
  }

  /**
   * @return <code>true</code> if no files are registered.
   */
  public boolean isEmpty() {
    return inputFiles.isEmpty();
  }

  /**
   * @return <code>true</code> if at least one file was added from a folder
   *         and has a root directory association.
   */
  public boolean hasFolderMappings() {
    return !fileToRootDirectory.isEmpty();
  }

  /**
   * Get all registered input files in insertion order.
   *
   * @return Unmodifiable list of input files.
   */
  public List<File> getInputFiles() {
    return Collections.unmodifiableList(new ArrayList<>(inputFiles));
  }

  /**
   * Get the root directory from which the given file was collected, or
   * <code>null</code> if it was added individually.
   *
   * @param inputFile The input file.
   * @return The root directory, or <code>null</code>.
   */
  public File getRootDirectoryForFile(File inputFile) {
    return fileToRootDirectory.get(inputFile);
  }
}
