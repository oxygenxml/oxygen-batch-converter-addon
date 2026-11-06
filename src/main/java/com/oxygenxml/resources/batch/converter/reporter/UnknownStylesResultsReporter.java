/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.reporter;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zwobble.mammoth.internal.conversion.UnknownStylesReporter;
import org.zwobble.mammoth.internal.documents.Style;

import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.results.ResultsManager.ResultType;

/**
 * Reports the Word styles that have no mapping as warnings in the Results panel of the converter.
 * 
 * @author cosmin_duna
 */
public class UnknownStylesResultsReporter implements UnknownStylesReporter {
  /**
   * Logger
   */
  private static final Logger logger = LoggerFactory.getLogger(UnknownStylesResultsReporter.class);

  /**
   * The results manager the warnings are added to.
   */
  private final ResultsManager resultsManager;

  /**
   * The translator of the messages.
   */
  private final Translator translator;

  /**
   * Constructor.
   * 
   * @param resultsManager  The results manager the warnings are added to.
   * @param translator      The translator of the messages.
   */
  public UnknownStylesResultsReporter(ResultsManager resultsManager, Translator translator) {
    this.resultsManager = resultsManager;
    this.translator = translator;
  }

  /**
   * Create a reporter for the Results panel of the running Oxygen.
   * 
   * @param translator The translator of the messages.
   * 
   * @return The reporter, or <code>null</code> when there is no Results panel to report to.
   */
  public static UnknownStylesResultsReporter create(Translator translator) {
    UnknownStylesResultsReporter reporter = null;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      ResultsManager resultsManager = pluginWorkspace.getResultsManager();
      if (resultsManager != null) {
        reporter = new UnknownStylesResultsReporter(resultsManager, translator);
      }
    }
    return reporter;
  }

  @Override
  public void reportUnknownStyle(String element, Style style, Optional<Path> documentPath) {
    StringBuilder message = new StringBuilder();
    if (style.getName().isPresent()) {
      message.append(MessageFormat.format(
          translator.getTranslation(Tags.UNRECOGNIZE_STYLES_FOR_WORD_ELEMENT), style.getName().get(), element));
    } else {
      message.append(MessageFormat.format(
          translator.getTranslation(Tags.UNRECOGNIZE_STYLES_ID_FOR_WORD_ELEMENT), style.getStyleId(), element));
    }
    message.append(" ").append(translator.getTranslation(Tags.CONFIG_WORD_MAPPING_IN_PREFERENCES_PAGE));

    String location = null;
    if(documentPath.isPresent()) {
      try {
        location = documentPath.get().toUri().toURL().toExternalForm();
      } catch (MalformedURLException e) {
        logger.debug(e.getMessage(), e);
      }
    }
    resultsManager.addResult(ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY,
        new DocumentPositionedInfo(DocumentPositionedInfo.SEVERITY_WARN, message.toString(), location),
        ResultType.PROBLEM, true, true);
  }
}
