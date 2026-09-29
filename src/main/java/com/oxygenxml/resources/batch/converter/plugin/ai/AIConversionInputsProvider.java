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

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;


/**
 * {@link UserInputsProvider} backed by the parameters of the {@link BatchConvertorAITool} AI function.
 * <p>
 * The options the AI does not name keep the default of the conversion, not the value the user last
 * chose in the conversion dialog: a conversion run by the AI depends only on what it was asked for.
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
   * <code>true</code> to open the converted documents once the conversion is done.
   */
  private final boolean openConvertedFiles;

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
   * @param openConvertedFiles                <code>true</code> to open the converted documents.
   */
  AIConversionInputsProvider(InputFilesManager inputFilesManager, File outputFolder, String converterType,
      Map<String, Boolean> requestedOptions, Integer maxHeadingLevelForCreatingTopics,
      boolean openConvertedFiles) {
    this.inputFilesManager = inputFilesManager;
    this.outputFolder = outputFolder;
    this.maxHeadingLevelForCreatingTopics = maxHeadingLevelForCreatingTopics;
    this.openConvertedFiles = openConvertedFiles;

    // Only the options this conversion accepts are set, each one on the value the AI asked for or,
    // when it didn't ask, on the default of the option. The dialog list carries the separators it
    // lays the options out with, those are not options and are skipped.
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
   * The converted documents are opened so that the user doesn't have to look for them, unless the
   * AI asked otherwise. There is nothing to open them in when Oxygen doesn't run with an interface.
   *
   * @see UserInputsProvider#mustOpenConvertedFiles()
   */
  @Override
  public boolean mustOpenConvertedFiles() {
    return openConvertedFiles && PluginWorkspaceProvider.getPluginWorkspace() != null;
  }
}
