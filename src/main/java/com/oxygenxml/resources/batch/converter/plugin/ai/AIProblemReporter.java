/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.resources.batch.converter.reporter.OxygenProblemReporter;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

/**
 * {@link ProblemReporter} that collects the reported problems in JSON format.
 * <p>
 * The {@link AIConvertor} AI function returns the problems to the AI as part of its result, instead
 * of presenting them in the interface the way {@link OxygenProblemReporter} does.
 *
 * @author vlad_greaca
 */
class AIProblemReporter implements ProblemReporter {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(AIProblemReporter.class.getName());

  /**
   * The collected problems.
   */
  private final JSONArray problems = new JSONArray();

  /**
   * @see ProblemReporter#reportProblem(Exception, File)
   */
  @Override
  public void reportProblem(Exception ex, File docFile) {
    LOGGER.debug(ex.getMessage(), ex);

    JSONObject problem = new JSONObject();
    if (docFile != null) {
      problem.put("input", AIConvertor.toLocation(docFile));
    }
    problem.put("message", ex.getMessage() != null ? ex.getMessage() : ex.toString());
    problems.put(problem);
  }

  /**
   * @return The problems reported so far. Never <code>null</code>.
   */
  JSONArray getProblems() {
    return problems;
  }
}
