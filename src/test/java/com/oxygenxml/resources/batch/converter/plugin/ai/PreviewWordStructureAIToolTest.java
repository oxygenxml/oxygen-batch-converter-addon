/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import com.oxygenxml.resources.batch.converter.persister.OptionTags;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Tests for {@link PreviewWordStructureAITool}, the AI function that previews the structure of Word documents.
 *
 * @author cosmin_duna
 */
public class PreviewWordStructureAIToolTest {

  /**
   * The function to test.
   */
  private final PreviewWordStructureAITool tool = new PreviewWordStructureAITool();

  /**
   * A document with custom styles.
   */
  private static final String CUSTOM_STYLES_DOCX = "test-sample/EXM-45677/inputCustomStyles.docx";

  @After
  public void tearDown() {
    PluginWorkspaceProvider.setPluginWorkspace(null);
  }

  /**
   * <p><b>Description:</b> Test that the function is read-only and validates its parameters.</p>
   *
   * <p><b>Bug ID:</b> BDC-163</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testDeclaration() throws Exception {
    assertEquals("preview_word_structure", tool.getName());
    assertTrue(tool.isReadOnly());
    assertTrue(tool.isSafe(null));

    JSONObject schema = new JSONObject(tool.getParameterDescriptions());
    assertEquals(Arrays.asList("input_files", "explanation"), schema.getJSONArray("required").toList());
    JSONObject properties = schema.getJSONObject("properties");
    assertTrue(properties.has("word_styles_mapping"));
    assertTrue(properties.has("max_heading_level_for_topics"));
    assertTrue(properties.has("max_depth"));
    assertFalse(schema.getBoolean("additionalProperties"));

    for (Object wrongLevel : Arrays.asList(0, -1, "not a number", JSONObject.NULL)) {
      assertRejectedParameters(new JSONObject()
          .put("input_files", new JSONArray().put(ListWordStylesAIToolTest.absolute(CUSTOM_STYLES_DOCX)))
          .put("max_heading_level_for_topics", wrongLevel).toString(), "max_heading_level_for_topics");
      assertRejectedParameters(new JSONObject()
          .put("input_files", new JSONArray().put(ListWordStylesAIToolTest.absolute(CUSTOM_STYLES_DOCX)))
          .put("max_depth", wrongLevel).toString(), "max_depth");
    }
    assertRejectedParameters(new JSONObject()
        .put("input_files", new JSONArray().put(ListWordStylesAIToolTest.absolute(CUSTOM_STYLES_DOCX)))
        .put("word_styles_mapping", new JSONArray().put(new JSONObject().put("element", "p")))
        .toString(), "Rule 1", "html");
  }

  /**
   * <p><b>Description:</b> Test that the outline follows the mapping and the heading level for topics, that
   * nothing is written and that the configured level is the default.</p>
   *
   * <p><b>Bug ID:</b> BDC-163</p>
   *
   * @author cosmin_duna
   */
  @Test
  public void testPreviewStructure() throws Exception {
    File input = new File(ListWordStylesAIToolTest.absolute(CUSTOM_STYLES_DOCX));
    long modified = input.lastModified();
    String[] siblingsBefore = input.getParentFile().list();

    JSONObject result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(input.getAbsolutePath()))
        .put("max_heading_level_for_topics", 1)
        .put("max_depth", 2)
        .toString(), null));

    assertEquals(result.toString(), 1, result.getInt("fileCount"));
    assertEquals(result.toString(), 0, result.getInt("failedCount"));
    JSONObject file = result.getJSONArray("files").getJSONObject(0);
    assertEquals(1, file.getInt("maxHeadingLevelForTopics"));
    assertEquals(3, file.getInt("topicCount"));
    assertEquals(3, file.getInt("sectionCount"));
    assertEquals(6, file.getInt("maxDepth"));
    assertEquals(1, file.getInt("omittedHeadingCount"));
    assertEquals(0, file.getInt("wordsBeforeFirstHeading"));
    assertEquals(2, file.getJSONArray("unmappedStyles").length());

    JSONArray headings = file.getJSONArray("headings");
    assertEquals(3, headings.length());
    JSONObject heading1 = headings.getJSONObject(2);
    assertEquals("Heading 1", heading1.getString("title"));
    assertEquals("topic", heading1.getString("kind"));
    assertEquals(1, heading1.getInt("level"));
    assertEquals(3, heading1.getInt("wordCount"));
    assertEquals(12, heading1.getInt("totalWordCount"));
    JSONObject heading2 = heading1.getJSONArray("children").getJSONObject(0);
    assertEquals("section", heading2.getString("kind"));
    // Heading 6 is deeper than the requested depth.
    assertFalse(heading2.has("children"));

    // Nothing was written next to the document, and the document was not touched.
    assertEquals(modified, input.lastModified());
    assertEquals(Arrays.asList(siblingsBefore), Arrays.asList(input.getParentFile().list()));

    // With a mapping, the custom title becomes a topic too, while the title mapping of the preferences applies.
    StandalonePluginWorkspace pluginWSMock = Mockito.mock(StandalonePluginWorkspace.class);
    WSOptionsStorage optionsStorageMock = Mockito.mock(WSOptionsStorage.class);
    Mockito.when(pluginWSMock.getOptionsStorage()).thenReturn(optionsStorageMock);
    Mockito.when(optionsStorageMock.getOption(OptionTags.WORD_STYLES_MAP_CONFIG, ""))
        .thenReturn("p[style-name='Title'] => p:fresh\np[style-name='Subtitle'] => p:fresh");
    Mockito.when(optionsStorageMock.getOption(OptionTags.MAX_HEADING_LEVEL_FOR_TOPICS, "5")).thenReturn("2");
    PluginWorkspaceProvider.setPluginWorkspace(pluginWSMock);

    result = new JSONObject(tool.executeFunction(new JSONObject()
        .put("input_files", new JSONArray().put(input.getAbsolutePath()))
        .put("word_styles_mapping", new JSONArray()
            .put(new JSONObject().put("element", "p").put("style", "Document Title").put("html", "h1:fresh")))
        .toString(), null));
    file = result.getJSONArray("files").getJSONObject(0);
    // The level configured by the user.
    assertEquals(2, file.getInt("maxHeadingLevelForTopics"));
    headings = file.getJSONArray("headings");
    assertEquals(3, headings.length());
    assertEquals("Heading 1", headings.getJSONObject(0).getString("title"));
    assertEquals("Document Title (custom)", headings.getJSONObject(1).getString("title"));
    assertEquals("Document Subtitle(custom)", headings.getJSONObject(2).getString("title"));
    assertEquals("topic", headings.getJSONObject(0).getJSONArray("children").getJSONObject(0).getString("kind"));
    assertEquals(5, file.getInt("wordsBeforeFirstHeading"));
    assertEquals(0, file.getJSONArray("unmappedStyles").length());
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
