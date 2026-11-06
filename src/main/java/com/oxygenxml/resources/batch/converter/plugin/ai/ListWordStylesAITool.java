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
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.word.inspect.WordDocumentInspector;
import com.oxygenxml.batch.converter.core.word.inspect.WordStyleMapRules;
import com.oxygenxml.batch.converter.core.word.inspect.WordStylesReport;
import com.oxygenxml.resources.batch.converter.InputFilesManager;

import ro.sync.exml.plugin.ai.ExternalServiceException;

/**
 * Tool that lists the styles used by Word documents, with text samples and their current mapping, so that the AI
 * can build a styles mapping before converting them.
 *
 * @author cosmin_duna
 */
public class ListWordStylesAITool extends AbstractBatchConverterAITool {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ListWordStylesAITool.class.getName());

  /**
   * The name of the parameter holding the number of text samples per style.
   */
  static final String PARAM_SAMPLES_PER_STYLE = "samples_per_style";

  /**
   * The name of the parameter asking for samples of the mapped styles as well.
   */
  static final String PARAM_INCLUDE_MAPPED = "include_mapped";

  /**
   * The default number of text samples per style.
   */
  static final int DEFAULT_SAMPLES_PER_STYLE = 2;

  /**
   * The extensions of the documents that can be inspected.
   */
  static final List<String> DOCX_EXTENSIONS = Arrays.asList("docx");

  /**
   * The name of the inspected format, for the messages.
   */
  static final String DOCX_FORMAT_NAME = "Word (.docx)";

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getName()
   */
  @Override
  public String getName() {
    return "list_word_styles";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getDescription()
   */
  @Override
  public String getDescription() {
    return "List the Word styles used by .docx documents before converting them to DITA/XHTML/DocBook with "
        + "\"convert_documents\": for each style its ID, name, type (\"p\" paragraph, \"r\" run, \"table\"), "
        + "the style it is based on, whether it is custom, how many times it is used, text samples and the HTML "
        + "it is currently mapped to (\"mappingSource\": \"default\", \"preference\", \"inherited:<styleId>\" or "
        + "\"none\" when it is unmapped and would be converted as a plain paragraph/text). Also returns the number "
        + "of paragraphs mapped to headings (\"headingCount\"), the heading levels of the table of contents "
        + "(\"tocLevels\") when the document has one, and the current mapping rules (\"currentMapping\"). "
        + "Use it to decide the \"word_styles_mapping\" of \"preview_word_structure\" and \"convert_documents\". "
        + "Reads the documents only, writes nothing.";
  }

  /**
   * @see AbstractBatchConverterAITool#getUIDescription()
   */
  @Override
  public String getUIDescription() {
    return "List Word styles";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getParameterDescriptions()
   */
  @Override
  public String getParameterDescriptions() {
    JSONObject properties = new JSONObject();
    properties.put(PARAM_INPUT_FILES, inputFilesParameter(
        "The Word .docx documents or directories to inspect, as filesystem paths or file URLs. "
            + "Directories are searched recursively for .docx files."));
    properties.put(PARAM_SAMPLES_PER_STYLE, new JSONObject()
        .put("type", "integer")
        .put("description",
            "Optional. The maximum number of text samples returned for each style, 0 for none. Defaults to "
                + DEFAULT_SAMPLES_PER_STYLE + "."));
    properties.put(PARAM_INCLUDE_MAPPED, new JSONObject()
        .put("type", "boolean")
        .put("description",
            "Optional. Return text samples for the styles that already have a mapping as well. Defaults to "
                + "false: only the custom and the unmapped styles get samples, the counts are always returned."));
    properties.put(PARAM_EXPLANATION, explanationParameter());

    JSONObject schema = new JSONObject();
    schema.put("type", "object");
    schema.put("properties", properties);
    schema.put("required", new JSONArray(Arrays.asList(PARAM_INPUT_FILES, PARAM_EXPLANATION)));
    schema.put("additionalProperties", false);
    return schema.toString();
  }

  /**
   * Lists the styles of the documents.
   *
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#executeFunction(java.lang.String, java.util.Map)
   */
  @Override
  public String executeFunction(String parameters, Map<String, Object> extraContext)
      throws IllegalArgumentException, ExternalServiceException {
    JSONObject params = parseParameters(parameters);
    int samplesPerStyle = DEFAULT_SAMPLES_PER_STYLE;
    if (params.has(PARAM_SAMPLES_PER_STYLE)) {
      samplesPerStyle = params.optInt(PARAM_SAMPLES_PER_STYLE, -1);
      if (samplesPerStyle < 0) {
        throw new IllegalArgumentException("The '" + PARAM_SAMPLES_PER_STYLE
            + "' must be a positive integer or 0, but it was: " + params.opt(PARAM_SAMPLES_PER_STYLE));
      }
    }
    boolean includeMapped = params.optBoolean(PARAM_INCLUDE_MAPPED, false);
    InputFilesManager inputFiles = resolveInputFiles(params, DOCX_EXTENSIONS, extraContext, DOCX_FORMAT_NAME);

    WordStyleMapRules currentRules;
    String preferenceStyleMap;
    try {
      currentRules = WordStylesMappingParameter.getCurrentRules();
      preferenceStyleMap = WordStylesMappingParameter.getPreferenceStyleMap();
    } catch (IOException e) {
      throw new ExternalServiceException(e.getMessage(), false);
    }

    JSONArray files = new JSONArray();
    JSONArray failed = new JSONArray();
    WordDocumentInspector inspector = new WordDocumentInspector();
    for (File file : inputFiles.getInputFiles()) {
      String location = toLocation(file);
      try {
        WordStylesReport report = inspector.inspectStyles(file, preferenceStyleMap,
            WordStylesMappingParameter.SOURCE_PREFERENCE, samplesPerStyle, includeMapped);
        files.put(WordInspectionJsonUtil.toJson(location, report));
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
    result.put("currentMapping", WordInspectionJsonUtil.toJson(currentRules));
    result.put("mappingSyntax", WordStylesMappingParameter.MAPPING_SYNTAX);
    return result.toString();
  }

  /**
   * The function only reads the documents.
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
