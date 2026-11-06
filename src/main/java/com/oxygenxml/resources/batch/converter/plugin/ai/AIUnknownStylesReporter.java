/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.zwobble.mammoth.internal.conversion.UnknownStylesReporter;
import org.zwobble.mammoth.internal.documents.Style;

import com.oxygenxml.batch.converter.core.word.inspect.UnmappedWordStyle;
import com.oxygenxml.resources.batch.converter.reporter.UnknownStylesResultsReporter;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;

/**
 * Collects the Word styles without a mapping met by a conversion run by the AI, so that they are returned
 * in the result of the function, and still reports them in the Results panel, as the conversion dialog does.
 *
 * @author cosmin_duna
 */
class AIUnknownStylesReporter implements UnknownStylesReporter {

  /**
   * The styles without a mapping, in the order they were met.
   */
  private final Set<UnmappedWordStyle> unmappedStyles = new LinkedHashSet<>();

  /**
   * The reporter of the Results panel, or <code>null</code> when there is none.
   */
  private final UnknownStylesReporter resultsPanelReporter;

  /**
   * Constructor.
   */
  AIUnknownStylesReporter() {
    this(UnknownStylesResultsReporter.create(new OxygenTranslator()));
  }

  /**
   * Constructor.
   *
   * @param resultsPanelReporter The reporter of the Results panel, or <code>null</code> when there is none.
   */
  AIUnknownStylesReporter(UnknownStylesReporter resultsPanelReporter) {
    this.resultsPanelReporter = resultsPanelReporter;
  }

  @Override
  public void reportUnknownStyle(String element, Style style, Optional<Path> documentPath) {
    unmappedStyles.add(new UnmappedWordStyle(element, style.getStyleId(), style.getName().orElse(null)));
    if (resultsPanelReporter != null) {
      resultsPanelReporter.reportUnknownStyle(element, style, documentPath);
    }
  }

  /**
   * @return The styles without a mapping, in the order they were met.
   */
  List<UnmappedWordStyle> getUnmappedStyles() {
    return new ArrayList<>(unmappedStyles);
  }
}
