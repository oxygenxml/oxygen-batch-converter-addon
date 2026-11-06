/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.oxygenxml.batch.converter.core.converters.word.WordToXHTMLConverter;
import com.oxygenxml.batch.converter.core.word.inspect.WordStyleMapRules;
import com.oxygenxml.batch.converter.core.word.inspect.WordStyleRule;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;

/**
 * The <code>word_styles_mapping</code> parameter of the AI functions: Word styles mapping rules given by the AI,
 * applied on top of the mapping configured by the user in the preferences (or the default one).
 *
 * @author cosmin_duna
 */
final class WordStylesMappingParameter {

  /**
   * The name of the parameter holding the Word styles mapping rules.
   */
  static final String PARAM_WORD_STYLES_MAPPING = "word_styles_mapping";

  /**
   * The key of the Word element of a rule.
   */
  static final String KEY_ELEMENT = "element";

  /**
   * The key of the Word style name of a rule.
   */
  static final String KEY_STYLE = "style";

  /**
   * The key of the resulted HTML of a rule.
   */
  static final String KEY_HTML = "html";

  /**
   * The source reported for the rules configured by the user in the preferences.
   */
  static final String SOURCE_PREFERENCE = "preference";

  /**
   * The Word elements a rule can match.
   */
  private static final Pattern ELEMENT_PATTERN = Pattern.compile(
      "^(p|r|b|i|u|strike|table|p:(?:un)?ordered-list\\(\\d+\\)|(?:p|r|table)\\.[A-Za-z0-9_-]+)$");

  /**
   * A summary of the mapping syntax, for the AI.
   */
  static final String MAPPING_SYNTAX = "A mapping rule has an \"element\" (the Word element: \"p\" for paragraphs, "
      + "\"r\" for runs/character styles, \"b\", \"i\", \"u\", \"strike\" for bold/italic/underline/strikethrough "
      + "runs, \"table\", \"p:unordered-list(n)\" and \"p:ordered-list(n)\" for list items of level n, or "
      + "\"p.StyleId\" / \"r.StyleId\" to match a style by its ID), an optional \"style\" (the Word style name, "
      + "matched ignoring case) and the \"html\" produced for it (which the DITA conversion then turns into DITA "
      + "structure). The \"html\" can be: an element name (\"h1\"..\"h6\" headings, \"p\", \"pre\", \"blockquote\", "
      + "\"kbd\", \"code\", \"i\", \"b\", \"cite\", \"div\"...), with \":fresh\" to start a new element for each "
      + "Word element instead of merging consecutive ones (use it for headings), with \".class\" to add a class "
      + "that becomes the DITA @outputclass attribute (e.g. \"i.filepath\", \"pre.codeblock\"), with "
      + "\":separator('\\n')\" to join consecutive paragraphs with new lines (e.g. "
      + "\"pre.codeblock:separator('\\n')\" for code snippets), nested elements with \">\" (e.g. \"ul > li\"), "
      + "or \"!\" to drop the element from the output. A custom Word style without a rule inherits the rule of "
      + "the style it is based on (basedOn), unless that is Normal. The rules given here take precedence over "
      + "the user's preferences; the preferences are not modified.";

  /**
   * Constructor.
   *
   * @throws UnsupportedOperationException when invoked.
   */
  private WordStylesMappingParameter() {
    throw new UnsupportedOperationException("Instantiation of this utility class is not allowed!");
  }

  /**
   * Builds the schema of the parameter.
   *
   * @param description The description of the parameter.
   *
   * @return The schema of the parameter.
   */
  static JSONObject schema(String description) {
    JSONObject rule = new JSONObject();
    rule.put("type", "object");
    rule.put("properties", new JSONObject()
        .put(KEY_ELEMENT, new JSONObject()
            .put("type", "string")
            .put("description", "The Word element: \"p\", \"r\", \"b\", \"i\", \"u\", \"strike\", \"table\", "
                + "\"p:unordered-list(n)\", \"p:ordered-list(n)\" or \"p.StyleId\" / \"r.StyleId\"."))
        .put(KEY_STYLE, new JSONObject()
            .put("type", "string")
            .put("description", "Optional. The Word style name the rule applies to, e.g. \"Document Title\"."))
        .put(KEY_HTML, new JSONObject()
            .put("type", "string")
            .put("description", "The HTML to produce, e.g. \"h1:fresh\", \"kbd\", \"i.filepath\", "
                + "\"pre.codeblock:separator('\\n')\", \"ul > li\" or \"!\" to drop the element.")));
    rule.put("required", new JSONArray(Arrays.asList(KEY_ELEMENT, KEY_HTML)));
    rule.put("additionalProperties", false);

    return new JSONObject()
        .put("type", "array")
        .put("items", rule)
        .put("description", description);
  }

  /**
   * Reads and validates the rules given by the AI.
   *
   * @param params The parameters given by the AI.
   *
   * @return The rules, in the Java Mammoth syntax, one per line, or <code>null</code> when the AI gave none.
   *
   * @throws IllegalArgumentException If a rule is not valid.
   */
  static String readRules(JSONObject params) {
    String rules = null;
    if (params.has(PARAM_WORD_STYLES_MAPPING)) {
      JSONArray rulesArray = params.optJSONArray(PARAM_WORD_STYLES_MAPPING);
      if (rulesArray == null) {
        throw new IllegalArgumentException("The '" + PARAM_WORD_STYLES_MAPPING
            + "' must be an array of {\"element\", \"style\", \"html\"} objects, but it was: "
            + params.opt(PARAM_WORD_STYLES_MAPPING));
      }
      StringBuilder rulesText = new StringBuilder();
      for (int i = 0; i < rulesArray.length(); i++) {
        JSONObject rule = rulesArray.optJSONObject(i);
        if (rule == null) {
          throw new IllegalArgumentException("Rule " + (i + 1) + " of '" + PARAM_WORD_STYLES_MAPPING
              + "' must be an object with \"element\", \"style\" and \"html\", but it was: " + rulesArray.opt(i));
        }
        if (i > 0) {
          rulesText.append('\n');
        }
        rulesText.append(toRule(rule, i + 1));
      }
      rules = rulesText.toString();
    }
    return rules;
  }

  /**
   * Converts a rule given by the AI to the Java Mammoth syntax.
   *
   * @param rule   The rule.
   * @param index  The 1-based index of the rule, for the messages.
   *
   * @return The rule in the Java Mammoth syntax.
   *
   * @throws IllegalArgumentException If the rule is not valid.
   */
  private static String toRule(JSONObject rule, int index) {
    String element = rule.optString(KEY_ELEMENT, "").trim();
    String style = rule.optString(KEY_STYLE, "").trim();
    String html = rule.optString(KEY_HTML, "").trim();
    if (!ELEMENT_PATTERN.matcher(element).matches()) {
      throw new IllegalArgumentException("Rule " + index + " of '" + PARAM_WORD_STYLES_MAPPING
          + "' has an invalid \"" + KEY_ELEMENT + "\": \"" + element + "\". Use \"p\", \"r\", \"b\", \"i\", "
          + "\"u\", \"strike\", \"table\", \"p:unordered-list(n)\", \"p:ordered-list(n)\" or \"p.StyleId\".");
    }
    if (style.indexOf('\'') != -1 || style.indexOf('\n') != -1) {
      throw new IllegalArgumentException("Rule " + index + " of '" + PARAM_WORD_STYLES_MAPPING
          + "' has an invalid \"" + KEY_STYLE + "\": \"" + style + "\". The style name cannot contain quotes.");
    }
    if (StringUtils.isBlank(html) || html.indexOf('\n') != -1) {
      throw new IllegalArgumentException("Rule " + index + " of '" + PARAM_WORD_STYLES_MAPPING
          + "' must have the \"" + KEY_HTML + "\" to produce, e.g. \"h1:fresh\", or \"!\" to drop the element.");
    }
    // The same syntax the preferences page serializes the rules with.
    StringBuilder ruleText = new StringBuilder(element);
    if (!style.isEmpty()) {
      ruleText.append("[style-name='").append(style).append("']");
    }
    return ruleText.append(" => ").append(html).toString();
  }

  /**
   * Gets the mapping configured by the user in the preferences.
   *
   * @return The mapping, in the Java Mammoth syntax, or <code>null</code> when the user configured none,
   *         in which case the default mapping of the converter applies.
   */
  static String getPreferenceStyleMap() {
    String styleMap = null;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      WSOptionsStorage optionsStorage = pluginWorkspace.getOptionsStorage();
      if (optionsStorage != null) {
        String configured = optionsStorage.getOption(OptionTags.WORD_STYLES_MAP_CONFIG, "");
        if (!configured.isEmpty()) {
          styleMap = configured;
        }
      }
    }
    return styleMap;
  }

  /**
   * Gets the rules currently applied to the conversions: the ones configured by the user in the preferences
   * or, when there are none, the default ones of the converter.
   *
   * @return The rules.
   *
   * @throws IOException If the default mapping cannot be loaded.
   */
  static WordStyleMapRules getCurrentRules() throws IOException {
    String preferenceStyleMap = getPreferenceStyleMap();
    WordStyleMapRules rules;
    if (preferenceStyleMap != null) {
      rules = new WordStyleMapRules(preferenceStyleMap, SOURCE_PREFERENCE);
    } else {
      rules = new WordStyleMapRules(WordToXHTMLConverter.loadDefaultStyleMap(), WordStyleRule.SOURCE_DEFAULT);
    }
    return rules;
  }

  /**
   * Builds the mapping to use for a conversion: the rules given by the AI, when any, on top of the mapping
   * configured by the user (or the default one).
   *
   * @param aiRules The rules given by the AI, in the Java Mammoth syntax, or <code>null</code>.
   *
   * @return The mapping, in the Java Mammoth syntax.
   *
   * @throws IOException If the default mapping cannot be loaded.
   */
  static String buildStyleMap(String aiRules) throws IOException {
    String baseStyleMap = getPreferenceStyleMap();
    if (baseStyleMap == null) {
      baseStyleMap = WordToXHTMLConverter.loadDefaultStyleMap();
    }
    String styleMap;
    if (aiRules == null || aiRules.isEmpty()) {
      styleMap = baseStyleMap;
    } else if (baseStyleMap == null || baseStyleMap.isEmpty()) {
      styleMap = aiRules;
    } else {
      // The first matching rule wins, so the ones given by the AI come first.
      styleMap = aiRules + "\n" + baseStyleMap;
    }
    return styleMap;
  }
}
