/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.oxygenxml.batch.converter.core.word.inspect.UnmappedWordStyle;
import com.oxygenxml.batch.converter.core.word.inspect.WordHeadingNode;
import com.oxygenxml.batch.converter.core.word.inspect.WordStructurePreview;
import com.oxygenxml.batch.converter.core.word.inspect.WordStyleInfo;
import com.oxygenxml.batch.converter.core.word.inspect.WordStyleMapRules;
import com.oxygenxml.batch.converter.core.word.inspect.WordStyleRule;
import com.oxygenxml.batch.converter.core.word.inspect.WordStylesReport;

/**
 * Converts the results of the Word document inspector to the JSON returned to the AI.
 *
 * @author cosmin_duna
 */
final class WordInspectionJsonUtil {

  /**
   * The key of the location of a document in the results.
   */
  static final String KEY_LOCATION = "location";

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private WordInspectionJsonUtil() {
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }

  /**
   * Converts a styles report.
   *
   * @param location  The location of the inspected document.
   * @param report    The report.
   *
   * @return The JSON object.
   */
  static JSONObject toJson(String location, WordStylesReport report) {
    JSONArray styles = new JSONArray();
    for (WordStyleInfo style : report.getStyles()) {
      JSONObject styleJson = new JSONObject();
      styleJson.put("styleId", style.getStyleId());
      styleJson.put("name", style.getName() != null ? style.getName() : JSONObject.NULL);
      styleJson.put("type", style.getType());
      styleJson.put("basedOn", style.getBasedOn() != null ? style.getBasedOn() : JSONObject.NULL);
      styleJson.put("custom", style.isCustom());
      styleJson.put("occurrences", style.getOccurrences());
      WordStyleRule mapping = style.getMapping();
      styleJson.put("mapping", mapping != null ? mapping.getHtml() : JSONObject.NULL);
      styleJson.put("mappingRule", mapping != null ? mapping.toString() : JSONObject.NULL);
      styleJson.put("mappingSource", style.getMappingSource());
      styleJson.put("samples", new JSONArray(style.getSamples()));
      styles.put(styleJson);
    }
    JSONObject json = new JSONObject();
    json.put(KEY_LOCATION, location);
    json.put("paragraphCount", report.getParagraphCount());
    json.put("headingCount", report.getHeadingCount());
    json.put("tocLevels", report.getTocLevels() != null ? report.getTocLevels() : JSONObject.NULL);
    json.put("tocStyleLevels", report.getTocStyleLevels() != null ? report.getTocStyleLevels() : JSONObject.NULL);
    json.put("samplesTruncated", report.isSamplesTruncated());
    json.put("styles", styles);
    return json;
  }

  /**
   * Converts a structure preview.
   *
   * @param location  The location of the inspected document.
   * @param preview   The preview.
   *
   * @return The JSON object.
   */
  static JSONObject toJson(String location, WordStructurePreview preview) {
    JSONObject json = new JSONObject();
    json.put(KEY_LOCATION, location);
    json.put("maxHeadingLevelForTopics", preview.getMaxHeadingLevelForTopics());
    json.put("topicCount", preview.getTopicCount());
    json.put("sectionCount", preview.getSectionCount());
    json.put("maxDepth", preview.getMaxDepth());
    json.put("smallTopicCount", preview.getSmallTopicCount());
    json.put("smallTopicWordThreshold", preview.getSmallTopicWordThreshold());
    json.put("wordsBeforeFirstHeading", preview.getWordsBeforeFirstHeading());
    json.put("omittedHeadingCount", preview.getOmittedHeadingCount());
    json.put("unmappedStyles", toJson(preview.getUnmappedStyles()));
    json.put("headings", toJson(preview.getHeadings()));
    return json;
  }

  /**
   * Converts a headings outline.
   *
   * @param headings The headings.
   *
   * @return The JSON array.
   */
  private static JSONArray toJson(Collection<WordHeadingNode> headings) {
    JSONArray json = new JSONArray();
    for (WordHeadingNode heading : headings) {
      JSONObject headingJson = new JSONObject();
      headingJson.put("level", heading.getLevel());
      headingJson.put("title", heading.getTitle());
      headingJson.put("kind", heading.getKind());
      headingJson.put("wordCount", heading.getWordCount());
      headingJson.put("totalWordCount", heading.getTotalWordCount());
      if (!heading.getChildren().isEmpty()) {
        headingJson.put("children", toJson(heading.getChildren()));
      }
      json.put(headingJson);
    }
    return json;
  }

  /**
   * Converts the styles without a mapping.
   *
   * @param unmappedStyles The styles.
   *
   * @return The JSON array.
   */
  static JSONArray toJson(Iterable<UnmappedWordStyle> unmappedStyles) {
    JSONArray json = new JSONArray();
    for (UnmappedWordStyle style : unmappedStyles) {
      JSONObject styleJson = new JSONObject();
      styleJson.put("element", style.getElement());
      styleJson.put("styleId", style.getStyleId());
      styleJson.put("name", style.getName() != null ? style.getName() : JSONObject.NULL);
      json.put(styleJson);
    }
    return json;
  }

  /**
   * Converts the style rules of a mapping.
   *
   * @param rules The rules.
   *
   * @return The JSON array.
   */
  static JSONArray toJson(WordStyleMapRules rules) {
    JSONArray json = new JSONArray();
    for (WordStyleRule rule : rules.getStyleRules()) {
      JSONObject ruleJson = new JSONObject();
      ruleJson.put(WordStylesMappingParameter.KEY_ELEMENT, rule.getElement());
      ruleJson.put(WordStylesMappingParameter.KEY_STYLE,
          rule.getStyleName() != null ? rule.getStyleName() : JSONObject.NULL);
      ruleJson.put(WordStylesMappingParameter.KEY_HTML, rule.getHtml());
      ruleJson.put("source", rule.getSource());
      json.put(ruleJson);
    }
    return json;
  }
}
