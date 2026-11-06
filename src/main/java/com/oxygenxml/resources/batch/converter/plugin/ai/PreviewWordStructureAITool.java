/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.word.inspect.WordDocumentInspector;
import com.oxygenxml.batch.converter.core.word.inspect.WordStructurePreview;
import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.UserInputsProviderUtil;

import ro.sync.exml.plugin.ai.ExternalServiceException;

/**
 * Tool that previews the structure (headings outline, topics and sections) Word documents would produce with a
 * given styles mapping, without writing any file.
 *
 * @author cosmin_duna
 */
public class PreviewWordStructureAITool extends AbstractBatchConverterAITool {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PreviewWordStructureAITool.class.getName());

  /**
   * The name of the parameter holding the maximum heading level for creating topics.
   */
  static final String PARAM_MAX_HEADING_LEVEL_FOR_TOPICS = "max_heading_level_for_topics";

  /**
   * The name of the parameter holding the deepest heading level to include in the outline.
   */
  static final String PARAM_MAX_DEPTH = "max_depth";

  /**
   * The default depth of the outline.
   */
  static final int DEFAULT_MAX_DEPTH = 3;

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getName()
   */
  @Override
  public String getName() {
    return "preview_word_structure";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getDescription()
   */
  @Override
  public String getDescription() {
    return "Preview the structure that Word .docx documents would get when converted to DITA with "
        + "\"convert_documents\", using the given \"word_styles_mapping\" on top of the user's preferences: the "
        + "outline of headings with their level, title and word counts, which headings become separate DITA "
        + "topics and which become sections according to \"max_heading_level_for_topics\", the totals (topics, "
        + "sections, deepest level, small topics) and the Word styles that are still unmapped. Use it to check a "
        + "mapping and to choose the heading level for topics before converting. Converts in memory only, writes "
        + "nothing.";
  }

  /**
   * @see AbstractBatchConverterAITool#getUIDescription()
   */
  @Override
  public String getUIDescription() {
    return "Preview Word structure";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getParameterDescriptions()
   */
  @Override
  public String getParameterDescriptions() {
    JSONObject properties = new JSONObject();
    properties.put(PARAM_INPUT_FILES, inputFilesParameter(
        "The Word .docx documents or directories to preview, as filesystem paths or file URLs. "
            + "Directories are searched recursively for .docx files."));
    properties.put(WordStylesMappingParameter.PARAM_WORD_STYLES_MAPPING, WordStylesMappingParameter.schema(
        "Optional. Word styles mapping rules to apply on top of the user's preferences, the same as for "
            + "\"convert_documents\". Use \"list_word_styles\" to find the styles to map."));
    properties.put(PARAM_MAX_HEADING_LEVEL_FOR_TOPICS, new JSONObject()
        .put("type", "integer")
        .put("description",
            "Optional. The maximum heading level that produces a separate DITA topic; deeper headings become "
                + "sections. Defaults to the level configured by the user."));
    properties.put(PARAM_MAX_DEPTH, new JSONObject()
        .put("type", "integer")
        .put("description",
            "Optional. The deepest heading level included in the returned outline, to keep it short. Deeper "
                + "headings are only counted. Defaults to " + DEFAULT_MAX_DEPTH + "."));
    properties.put(PARAM_EXPLANATION, explanationParameter());

    JSONObject schema = new JSONObject();
    schema.put("type", "object");
    schema.put("properties", properties);
    schema.put("required", new JSONArray(Arrays.asList(PARAM_INPUT_FILES, PARAM_EXPLANATION)));
    schema.put("additionalProperties", false);
    return schema.toString();
  }

  /**
   * Previews the structure of the documents.
   *
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#executeFunction(java.lang.String, java.util.Map)
   */
  @Override
  public String executeFunction(String parameters, Map<String, Object> extraContext)
      throws IllegalArgumentException, ExternalServiceException {
    JSONObject params = parseParameters(parameters);
    int maxHeadingLevel = readPositiveInteger(params, PARAM_MAX_HEADING_LEVEL_FOR_TOPICS,
        UserInputsProviderUtil.getMaxHeadingLevelForCreatingTopicsOption());
    int maxDepth = readPositiveInteger(params, PARAM_MAX_DEPTH, DEFAULT_MAX_DEPTH);
    String aiRules = WordStylesMappingParameter.readRules(params);
    InputFilesManager inputFiles = resolveInputFiles(params, ListWordStylesAITool.DOCX_EXTENSIONS, extraContext,
        ListWordStylesAITool.DOCX_FORMAT_NAME);

    String styleMap;
    try {
      styleMap = WordStylesMappingParameter.buildStyleMap(aiRules);
    } catch (IOException e) {
      throw new ExternalServiceException(e.getMessage(), false);
    }

    JSONArray files = new JSONArray();
    JSONArray failed = new JSONArray();
    WordDocumentInspector inspector = new WordDocumentInspector();
    for (File file : inputFiles.getInputFiles()) {
      String location = toLocation(file);
      try {
        WordStructurePreview preview = inspector.previewStructure(file, styleMap, maxHeadingLevel, maxDepth);
        files.put(WordInspectionJsonUtil.toJson(location, preview));
      } catch (IOException | IllegalArgumentException e) {
        LOGGER.debug(e.getMessage(), e);
        failed.put(new JSONObject().put(WordInspectionJsonUtil.KEY_LOCATION, location).put("reason", e.getMessage()));
      }
    }

    JSONObject result = new JSONObject();
    result.put("fileCount", files.length());
    result.put("files", files);
    result.put("failedCount", failed.length());
    result.put("failed", failed);
    return result.toString();
  }

  /**
   * Reads a positive integer parameter.
   *
   * @param params        The parameters given by the AI.
   * @param parameterName The name of the parameter.
   * @param defaultValue  The value when the parameter is absent.
   *
   * @return The value.
   *
   * @throws IllegalArgumentException If the given value isn't a positive integer.
   */
  private static int readPositiveInteger(JSONObject params, String parameterName, int defaultValue) {
    int value = defaultValue;
    if (params.has(parameterName)) {
      // A value that isn't a number at all reads as the fallback and is rejected together with the
      // numbers that make no sense.
      value = params.optInt(parameterName, -1);
      if (value <= 0) {
        throw new IllegalArgumentException(
            "The '" + parameterName + "' must be a positive integer, but it was: " + params.opt(parameterName));
      }
    }
    return value;
  }

  /**
   * The function only reads the documents, the preview is computed in memory.
   * <p>
   * No {@code @Override}: the {@code ExternalAIFunction#isReadOnly()} API was added in Oxygen 29.0,
   * and this add-on must also compile against older Oxygen versions.
   * </p>
   *
   * @return <code>true</code>.
   */
  public boolean isReadOnly() {
    return true;
  }
}
