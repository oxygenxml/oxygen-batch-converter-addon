/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.view.ConverterAdditionalOptionsProvider;

/**
 * {@link UserInputsProvider} backed by the parameters of the {@link AIConvertor} AI function.
 * <p>
 * The options the AI does not name keep the value the conversion dialog presents by default, so that
 * a conversion run by the AI matches the one the user would get from the interface.
 *
 * @author vlad_greaca
 */
class AIConversionInputsProvider implements UserInputsProvider {

  /**
   * The manager holding the input files and the directory each one was collected from.
   */
  private final InputFilesManager inputFilesManager;

  /**
   * The output folder.
   */
  private final File outputFolder;

  /**
   * The additional boolean options, keyed by option id.
   */
  private final Map<String, Boolean> additionalOptions = new HashMap<>();

  /**
   * The maximum heading level for creating topics, or <code>null</code> to use the configured one.
   */
  private final Integer maxHeadingLevelForCreatingTopics;

  /**
   * Constructor.
   *
   * @param inputFilesManager                 The manager holding the input files.
   * @param outputFolder                      The output folder.
   * @param converterType                     The converter type, it decides which options apply.
   * @param requestedOptions                  The options named by the AI, keyed by option id. The
   *                                          ones that do not apply to this conversion are ignored.
   * @param maxHeadingLevelForCreatingTopics  The maximum heading level for creating topics, or
   *                                          <code>null</code> to use the configured one.
   */
  AIConversionInputsProvider(InputFilesManager inputFilesManager, File outputFolder, String converterType,
      Map<String, Boolean> requestedOptions, Integer maxHeadingLevelForCreatingTopics) {
    this.inputFilesManager = inputFilesManager;
    this.outputFolder = outputFolder;
    this.maxHeadingLevelForCreatingTopics = maxHeadingLevelForCreatingTopics;

    // Only the options this conversion accepts are set, each one on the value the AI asked for or,
    // when it didn't ask, on the default the conversion dialog presents. The dialog list carries the
    // separators it lays the options out with, those are not options and are skipped.
    for (String option : ConverterAdditionalOptionsProvider.getImposedAdditionalOptions(converterType)) {
      if (!ConverterAdditionalOptionsProvider.ADDITIONAL_OPTIONS_SEPARATOR.equals(option)) {
        Boolean requestedValue = requestedOptions.get(option);
        additionalOptions.put(option, requestedValue != null ? requestedValue
            : ConverterAdditionalOptionsProvider.getDefaultValueFor(option));
      }
    }
  }

  /**
   * @see UserInputsProvider#getInputFiles()
   */
  @Override
  public List<File> getInputFiles() {
    return inputFilesManager.getInputFiles();
  }

  /**
   * @see UserInputsProvider#getOutputFolder()
   */
  @Override
  public File getOutputFolder() {
    return outputFolder;
  }

  /**
   * @see UserInputsProvider#getAdditionalOptionValue(String)
   */
  @Override
  public Boolean getAdditionalOptionValue(String additionalOptionId) {
    return additionalOptions.get(additionalOptionId);
  }

  /**
   * The level named by the AI or, when it didn't name one, the level configured by the user, which
   * the {@link UserInputsProvider} default reads from the options storage.
   *
   * @see UserInputsProvider#getMaxHeadingLevelForCreatingTopics()
   */
  @Override
  public Integer getMaxHeadingLevelForCreatingTopics() {
    return maxHeadingLevelForCreatingTopics != null ? maxHeadingLevelForCreatingTopics
        : UserInputsProvider.super.getMaxHeadingLevelForCreatingTopics();
  }

  /**
   * @see UserInputsProvider#getRootInputDirectoryForFile(File)
   */
  @Override
  public File getRootInputDirectoryForFile(File inputFile) {
    return inputFilesManager.getRootDirectoryForFile(inputFile);
  }

  /**
   * @see UserInputsProvider#mustOpenConvertedFiles()
   */
  @Override
  public boolean mustOpenConvertedFiles() {
    // The AI function only reports the converted files, it doesn't open them in the editor.
    return false;
  }
}
