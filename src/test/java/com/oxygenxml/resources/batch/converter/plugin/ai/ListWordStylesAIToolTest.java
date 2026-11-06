/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import com.oxygenxml.resources.batch.converter.persister.OptionTags;

import ro.sync.exml.plugin.ai.ExternalAIFunction;
import ro.sync.exml.plugin.ai.ExternalServiceException;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Tests for {@link ListWordStylesAITool}, the AI function that lists the styles of Word documents.
 *
 * @author cosmin_duna
 */
public class ListWordStylesAIToolTest {

  /**
   * The function to test.
   */
  private final ListWordStylesAITool tool = new ListWordStylesAITool();

  /**
   * A document with custom styles.
   */
  private static final String CUSTOM_STYLES_DOCX = "test-sample/EXM-45677/inputCustomStyles.docx";

  @After
  public void tearDown() {
    PluginWorkspaceProvider.setPluginWorkspace(null);
  }

  /**
   * <p><b>Description:</b> Test that the function is read-only, safe under the sandbox, and declares its
   * parameters.</p>
   *
   * <p><b>Bug ID:</b> BDC-163</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testDeclaration() throws Exception {
    assertEquals("list_word_styles", tool.getName());
    assertTrue(tool.isReadOnly());
    assertTrue(tool.isSafe(null));
    assertEquals("Conversion", tool.getCategory());
    assertEquals(tool.getUIDescription(), tool.getUIDecription());

    JSONObject schema = new JSONObject(tool.getParameterDescriptions());
    assertEquals(Arrays.asList("input_files", "explanation"), schema.getJSONArray("required").toList());
    JSONObject properties = schema.getJSONObject("properties");
    assertTrue(properties.has("samples_per_style"));
    assertTrue(properties.has("include_mapped"));
    assertFalse(schema.getBoolean("additionalProperties"));

    assertRejectedParameters(new JSONObject().toString(), "input_files");
    assertRejectedParameters(new JSONObject()
        .put("input_files", new JSONArray().put(absolute(CUSTOM_STYLES_DOCX)))
        .put("samples_per_style", -1).toString(), "samples_per_style");
    assertRejectedParameters(new JSONObject()
        .put("input_files", new JSONArray().put(absolute("test-sample/this-file-does-not-exist.docx")))
        .toString(), "this-file-does-not-exist.docx");
  }

  /**
   * <p><b>Description:</b> Test that the styles of a document are listed with their mapping and samples, and
   * that the mapping configured by the user is the one reported.</p>
   *
   * <p><b>Bug ID:</b> BDC-163</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testListStyles() throws Exception {
    JSONObject result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(absolute(CUSTOM_STYLES_DOCX)))
        .toString(), null));

    assertEquals(result.toString(), 1, result.getInt("fileCount"));
    assertEquals(result.toString(), 0, result.getInt("failedCount"));
    assertTrue(result.getString("mappingSyntax").contains(":fresh"));

    JSONObject file = result.getJSONArray("files").getJSONObject(0);
    assertEquals(BatchConvertorAITool.toLocation(new File(absolute(CUSTOM_STYLES_DOCX))), file.getString("location"));
    assertEquals(14, file.getInt("paragraphCount"));
    assertEquals(6, file.getInt("headingCount"));
    assertTrue(file.isNull("tocLevels"));

    JSONObject documentTitle = findStyle(file, "p", "DocumentTitle0");
    assertEquals("Document Title", documentTitle.getString("name"));
    assertTrue(documentTitle.getBoolean("custom"));
    assertEquals("Documenttitle", documentTitle.getString("basedOn"));
    assertEquals(1, documentTitle.getInt("occurrences"));
    assertTrue(documentTitle.isNull("mapping"));
    assertEquals("none", documentTitle.getString("mappingSource"));
    assertEquals(Arrays.asList("Document Title (custom)"), documentTitle.getJSONArray("samples").toList());

    // A mapped default style: counted, but not sampled unless asked.
    JSONObject title = findStyle(file, "p", "Title");
    assertEquals("h1:fresh", title.getString("mapping"));
    assertEquals("p[style-name='Title'] => h1:fresh", title.getString("mappingRule"));
    assertEquals("default", title.getString("mappingSource"));
    assertEquals(2, title.getInt("occurrences"));
    assertEquals(0, title.getJSONArray("samples").length());

    // The current mapping is the default one.
    JSONArray currentMapping = result.getJSONArray("currentMapping");
    JSONObject titleRule = findRule(currentMapping, "Title");
    assertEquals("p", titleRule.getString("element"));
    assertEquals("h1:fresh", titleRule.getString("html"));
    assertEquals("default", titleRule.getString("source"));

    // With the samples of the mapped styles too.
    result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(absolute(CUSTOM_STYLES_DOCX)))
        .put("include_mapped", true)
        .put("samples_per_style", 1)
        .toString(), null));
    title = findStyle(result.getJSONArray("files").getJSONObject(0), "p", "Title");
    assertEquals(Arrays.asList("Title style"), title.getJSONArray("samples").toList());

    // The mapping configured by the user replaces the default one.
    StandalonePluginWorkspace pluginWSMock = Mockito.mock(StandalonePluginWorkspace.class);
    WSOptionsStorage optionsStorageMock = Mockito.mock(WSOptionsStorage.class);
    Mockito.when(pluginWSMock.getOptionsStorage()).thenReturn(optionsStorageMock);
    Mockito.when(optionsStorageMock.getOption(OptionTags.WORD_STYLES_MAP_CONFIG, ""))
        .thenReturn("p[style-name='Document Title'] => h1:fresh\np[style-name='Title'] => p:fresh");
    PluginWorkspaceProvider.setPluginWorkspace(pluginWSMock);

    result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(absolute(CUSTOM_STYLES_DOCX)))
        .toString(), null));
    file = result.getJSONArray("files").getJSONObject(0);
    documentTitle = findStyle(file, "p", "DocumentTitle0");
    assertEquals("h1:fresh", documentTitle.getString("mapping"));
    assertEquals("preference", documentTitle.getString("mappingSource"));
    // The subtitle, based on the custom title, inherits its mapping.
    assertEquals("inherited:DocumentTitle0", findStyle(file, "p", "DocumentSubtitle").getString("mappingSource"));
    assertEquals("p:fresh", findStyle(file, "p", "Title").getString("mapping"));
    // Heading 1, Heading 2, Heading 6, Document Title, Document Subtitle.
    assertEquals(5, file.getInt("headingCount"));
    currentMapping = result.getJSONArray("currentMapping");
    assertEquals("preference", findRule(currentMapping, "Title").getString("source"));
    assertEquals("p:fresh", findRule(currentMapping, "Title").getString("html"));
  }

  /**
   * <p><b>Description:</b> Test that the .docx documents of a folder are inspected, that a document that
   * cannot be inspected is reported without failing the call, and that the access rules are enforced.</p>
   *
   * <p><b>Bug ID:</b> BDC-163</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testFoldersAndDeniedLocations() throws Exception {
    File folder = new File("test-sample/EXM-45677").getCanonicalFile();
    JSONObject result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(folder.getAbsolutePath()))
        .put("samples_per_style", 0)
        .toString(), null));
    assertEquals(result.toString(), 2, result.getInt("fileCount"));
    assertEquals(result.toString(), 0, result.getInt("failedCount"));

    // A document named directly that is not a .docx one is reported, the others are still inspected.
    result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray()
            .put(absolute(CUSTOM_STYLES_DOCX))
            .put(absolute("test-sample/markdownTest.md")))
        .toString(), null));
    assertEquals(result.toString(), 1, result.getInt("fileCount"));
    assertEquals(result.toString(), 1, result.getInt("failedCount"));
    JSONObject failed = result.getJSONArray("failed").getJSONObject(0);
    assertTrue(failed.getString("location").endsWith("markdownTest.md"));
    assertTrue(failed.getString("reason"), failed.getString("reason").contains(".doc format is not supported"));

    // An ignored document in a folder is skipped, one named directly is refused.
    Map<String, Object> extraContext = new HashMap<>();
    extraContext.put(ExternalAIFunction.AI_IGNORE_PREDICATE_KEY,
        (Predicate<String>) url -> url.endsWith("inputCustomStyles.docx"));
    result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(folder.getAbsolutePath()))
        .put("samples_per_style", 0)
        .toString(), extraContext));
    assertEquals(result.toString(), 1, result.getInt("fileCount"));
    assertTrue(result.getJSONArray("files").getJSONObject(0).getString("location").endsWith("/input.docx"));
    try {
      tool.executeFunction(new JSONObject()
          .put("input_files", new JSONArray().put(absolute(CUSTOM_STYLES_DOCX)))
          .toString(), extraContext);
      fail("The ignored document should have been refused");
    } catch (ExternalServiceException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("inputCustomStyles.docx"));
    }
  }

  /**
   * Finds a style in the result of a file.
   *
   * @param file     The file result.
   * @param type     The style type.
   * @param styleId  The style ID.
   *
   * @return The style.
   */
  static JSONObject findStyle(JSONObject file, String type, String styleId) {
    JSONObject found = null;
    JSONArray styles = file.getJSONArray("styles");
    for (int i = 0; i < styles.length(); i++) {
      JSONObject style = styles.getJSONObject(i);
      if (type.equals(style.getString("type")) && styleId.equals(style.getString("styleId"))) {
        found = style;
      }
    }
    assertNotNull("Style not found: " + type + ":" + styleId + " in " + styles, found);
    return found;
  }

  /**
   * Finds the first rule for a style name in the current mapping.
   *
   * @param currentMapping  The current mapping.
   * @param styleName       The style name.
   *
   * @return The rule.
   */
  private static JSONObject findRule(JSONArray currentMapping, String styleName) {
    JSONObject found = null;
    for (int i = 0; i < currentMapping.length() && found == null; i++) {
      JSONObject rule = currentMapping.getJSONObject(i);
      if (styleName.equals(rule.optString("style", null))) {
        found = rule;
      }
    }
    assertNotNull("Rule not found for: " + styleName + " in " + currentMapping, found);
    return found;
  }

  /**
   * The absolute path of a file from the test samples.
   *
   * @param testSamplePath The path of the file, relative to the project.
   *
   * @return The absolute path of the file.
   */
  static String absolute(String testSamplePath) throws IOException {
    return new File(testSamplePath).getCanonicalPath();
  }

  /**
   * Asserts that the given parameters are rejected as invalid, naming the parts of the request to correct.
   *
   * @param parameters       The parameters to execute the function with.
   * @param expectedInError  The parts of the request the rejection must name.
   */
  private void assertRejectedParameters(String parameters, String... expectedInError) throws Exception {
    try {
      String result = tool.executeFunction(parameters, null);
      fail("These parameters should have been rejected: " + parameters + ", the answer was: " + result);
    } catch (IllegalArgumentException e) {
      for (String expected : expectedInError) {
        assertTrue("The rejection must name \"" + expected + "\", it was: " + e.getMessage(),
            e.getMessage().contains(expected));
      }
    }
  }
}
