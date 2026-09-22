package com.oxygenxml.resources.batch.converter.plugin.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

import com.oxygenxml.batch.converter.core.ConversionOptionTags;
import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.resources.batch.converter.InputFilesManager;

import ro.sync.exml.plugin.ai.ExternalAIFunction;
import ro.sync.exml.plugin.ai.ExternalServiceException;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;
import tests.utils.FileComparationUtil;

/**
 * Tests for {@link BatchConvertorAITool}, the AI function that converts documents.
 *
 * @author vlad_greaca
 */
public class BatchConvertorAIToolTest {

  /**
   * The converter to test.
   */
  private final BatchConvertorAITool convertor = new BatchConvertorAITool();

  /**
   * The output folder used by the tests, deleted after each test.
   */
  private File outputFolder;

  @After
  public void tearDown() throws IOException {
    if (outputFolder != null && outputFolder.exists()) {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }
  

  /**
   * <p><b>Description:</b> Test that the mandatory parameters are declared in the schema and
   * rejected when missing.</p>
   *
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testMandatoryParameters() throws Exception {
    JSONObject schema = new JSONObject(convertor.getParameterDescriptions());
    assertEquals("object", schema.getString("type"));

    List<Object> required = schema.getJSONArray("required").toList();
    assertEquals(
        Arrays.asList("input_format", "output_format", "input_files", "output_folder", "explanation"),
        required);

    JSONObject properties = schema.getJSONObject("properties");
    for (Object requiredParameter : required) {
      assertTrue("Required but not declared: " + requiredParameter,
          properties.has((String) requiredParameter));
    }

    // The formats and the input files are the ones the conversion cannot run without. The
    // explanation is required of the AI for the user to read, the conversion does not need it.
    assertRejectedParameters(new JSONObject().put("output_format", "dita").toString(), "input_format");
    assertRejectedParameters(new JSONObject()
        .put("input_format", "markdown")
        .put("output_format", "dita")
        .put("input_files", new JSONArray())
        .toString(), "input_files");
    assertRejectedParameters("not a json", "Invalid parameters");
  }

  /**
   * <p><b>Description:</b> Test that a request that cannot be carried out converts nothing.</p>
   * 
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testInvalidRequestConvertsNothing() throws Exception {
    File emptyInputFolder = new File("test-sample/ai-in-no-match");
    try {
      emptyInputFolder.mkdirs();

      // An output folder the converter cannot write into: it writes on the local filesystem only.
      assertNoConvertedDocuments(new JSONObject(convertor.executeFunction(new JSONObject()
          .put("input_format", "json")
          .put("output_format", "yaml")
          .put("input_files", new JSONArray().put("test-sample/jsonTest.json"))
          .put("output_folder", "http://www.oxygenxml.com/out")
          .toString(), null)), "http://www.oxygenxml.com/out");

      // A conversion between two formats that cannot be converted into one another. The formats the
      // caller asked for are named, it does not have to guess which of the two is the problem.
      assertNothingConverted("test-sample/ai-out-unsupported", "markdown", "excel",
          new JSONArray().put("test-sample/markdownTest.md"), "markdown", "excel");

      // A document the caller named that is not there.
      assertNothingConverted("test-sample/ai-out-no-input", "json", "yaml",
          new JSONArray().put("test-sample/this-file-does-not-exist.json"),
          "this-file-does-not-exist.json");
      
      // An existing folder that holds nothing of the input format: a good location, nothing to do.
      assertNothingConverted("test-sample/ai-out-no-match", "json", "yaml",
          new JSONArray().put(emptyInputFolder.getPath()), "No input files matching");
    } finally {
      FileComparationUtil.deleteRecursivelly(emptyInputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test that converting a folder preserves its sub-folder structure.</p>
   *
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testFolderStructureIsPreserved() throws Exception {
    File inputFolder = new File("test-sample/ai-in-structure");
    outputFolder = new File("test-sample/ai-out-folder-structure");
    try {
      // The same file name in two sub-folders: flattening the output would lose one of them.
      File firstInput = new File(inputFolder, "sub1/topic.json");
      File secondInput = new File(inputFolder, "sub2/topic.json");
      firstInput.getParentFile().mkdirs();
      secondInput.getParentFile().mkdirs();
      Files.copy(new File("test-sample/jsonTest.json").toPath(), firstInput.toPath());
      Files.copy(new File("test-sample/jsonTest.json").toPath(), secondInput.toPath());

      JSONObject result = new JSONObject(convertor.executeFunction(new JSONObject()
          .put("input_format", "json")
          .put("output_format", "yaml")
          .put("input_files", new JSONArray().put(inputFolder.getPath()))
          .put("output_folder", outputFolder.getPath())
          .toString(), null));

      assertEquals(result.toString(), 0, result.getInt("problemCount"));
      assertEquals(result.toString(), 2, result.getInt("convertedFileCount"));

      // Each document lands under its own sub-folder, holding the expected conversion of the input.
      File expected = new File("test-sample/yamlTest.yaml");
      File firstTopic = new File(outputFolder, "sub1/topic.yaml");
      File secondTopic = new File(outputFolder, "sub2/topic.yaml");
      assertTrue("sub1/topic.yaml must be the expected YAML",
          FileComparationUtil.compareLineToLine(expected, firstTopic));
      assertTrue("sub2/topic.yaml must be the expected YAML",
          FileComparationUtil.compareLineToLine(expected, secondTopic));

      // Both documents are offered to the caller, each under its own sub-folder.
      List<Object> convertedFiles = result.getJSONArray("convertedFiles").toList();
      assertTrue(convertedFiles.toString(), convertedFiles.contains(BatchConvertorAITool.toLocation(firstTopic)));
      assertTrue(convertedFiles.toString(), convertedFiles.contains(BatchConvertorAITool.toLocation(secondTopic)));
    } finally {
      FileComparationUtil.deleteRecursivelly(inputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test conversion options being used.</p>
   * 
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testConversionOptions() {
    // "Split sections" asks for a DITA Map from every input format, only the Markdown one applies.
    Map<String, Boolean> requestedOptions = new HashMap<>();
    requestedOptions.put(ConversionOptionTags.CREATE_DITA_MAP_FROM_MD, false);
    requestedOptions.put(ConversionOptionTags.CREATE_DITA_MAP_FROM_WORD, false);

    AIConversionInputsProvider markdownInputs = new AIConversionInputsProvider(new InputFilesManager(),
        new File("out"), ConverterTypes.MD_TO_DITA, requestedOptions, 3);

    assertEquals(Boolean.FALSE, markdownInputs.getAdditionalOptionValue(ConversionOptionTags.CREATE_DITA_MAP_FROM_MD));
    assertNull(markdownInputs.getAdditionalOptionValue(ConversionOptionTags.CREATE_DITA_MAP_FROM_WORD));
    assertEquals(Boolean.FALSE, markdownInputs.getAdditionalOptionValue(ConversionOptionTags.CREATE_SHORT_DESCRIPTION));
    assertEquals(Integer.valueOf(3), markdownInputs.getMaxHeadingLevelForCreatingTopics());

    // Nothing was asked for here, so the dialog defaults stand. "Preserve case" defaults to true:
    // leaving it unset would convert the names differently than the dialog does.
    AIConversionInputsProvider xsdInputs = new AIConversionInputsProvider(new InputFilesManager(),
        new File("out"), ConverterTypes.XSD_TO_JSONSCHEMA, new HashMap<>(), null);

    assertEquals(Boolean.TRUE,
        xsdInputs.getAdditionalOptionValue(ConversionOptionTags.PRESERVE_CASE_OF_NAMES_FROM_THE_XSD));
    assertEquals(Boolean.FALSE,
        xsdInputs.getAdditionalOptionValue(ConversionOptionTags.RESTRICT_ADDITIONAL_CONTENT));
  }

  /**
   * <p><b>Description:</b> Test that sandboxed and ai-ignored locations are not converted.</p>
   * 
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testDeniedLocationsAreRefused() throws Exception {
    // A document to read, denied by the sandbox and then excluded by an .ai-ignore file.
    assertAccessDenied("test-sample/ai-out-denied-input", "jsonTest.json",
        ExternalAIFunction.DOCUMENT_ACCESS_PREDICATE_KEY);
    assertAccessDenied("test-sample/ai-out-denied-input", "jsonTest.json",
        ExternalAIFunction.AI_IGNORE_PREDICATE_KEY);

    // The folder to write into: the converted documents land there, so it is off limits too.
    assertAccessDenied("test-sample/ai-out-denied-output", "ai-out-denied-output",
        ExternalAIFunction.DOCUMENT_ACCESS_PREDICATE_KEY);
    assertAccessDenied("test-sample/ai-out-denied-output", "ai-out-denied-output",
        ExternalAIFunction.AI_IGNORE_PREDICATE_KEY);
  }

  /**
   * <p><b>Description:</b> Test that ai-ignored files found in a folder are skipped, not failed on.</p>
   * 
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testInaccessibleFilesAreFiltered() throws Exception {
    File inputFolder = new File("test-sample/ai-in-filtered");
    outputFolder = new File("test-sample/ai-out-filtered");
    try {
      inputFolder.mkdirs();
      File allowed = new File(inputFolder, "allowed.json");
      File ignored = new File(inputFolder, "ignored.json");
      Files.copy(new File("test-sample/jsonTest.json").toPath(), allowed.toPath());
      Files.copy(new File("test-sample/jsonTest.json").toPath(), ignored.toPath());

      Map<String, Object> extraContext = new HashMap<>();
      extraContext.put(ExternalAIFunction.AI_IGNORE_PREDICATE_KEY,
          (Predicate<String>) url -> url.endsWith("ignored.json"));

      JSONObject result = new JSONObject(convertor.executeFunction(new JSONObject()
          .put("input_format", "json")
          .put("output_format", "yaml")
          .put("input_files", new JSONArray().put(inputFolder.getPath()))
          .put("output_folder", outputFolder.getPath())
          .toString(), extraContext));

      assertEquals(result.toString(), 0, result.getInt("problemCount"));
      assertEquals(1, result.getInt("convertedFileCount"));
      assertTrue(new File(outputFolder, "allowed.yaml").exists());
      assertTrue("The ignored file should not have been converted",
          !new File(outputFolder, "ignored.yaml").exists());
    } finally {
      FileComparationUtil.deleteRecursivelly(inputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test that an existing output file is not overwritten.</p>
   * 
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testExistingOutputFileIsNotOverwritten() throws Exception {
    outputFolder = new File("test-sample/ai-out-no-overwrite");
    String parameters = new JSONObject()
        .put("input_format", "json")
        .put("output_format", "yaml")
        .put("input_files", new JSONArray().put("test-sample/jsonTest.json"))
        .put("output_folder", outputFolder.getPath())
        .toString();

    convertor.executeFunction(parameters, null);
    File converted = new File(outputFolder, "jsonTest.yaml");
    assertTrue(converted.exists());
    long contentLength = converted.length();

    // Converting again keeps the previous result untouched and writes a second file.
    JSONObject result = new JSONObject(convertor.executeFunction(parameters, null));

    assertEquals(result.toString(), 0, result.getInt("problemCount"));
    assertEquals(1, result.getInt("convertedFileCount"));
    assertEquals("The previous result should not have been overwritten", contentLength, converted.length());
    assertEquals(2, outputFolder.listFiles().length);
  }

  /**
   * Asserts that the given parameters are rejected as invalid, naming the parameter at fault.
   *
   * @param parameters       The parameters to execute the function with.
   * @param expectedInError  The text the rejection must name.
   */
  private void assertRejectedParameters(String parameters, String expectedInError) throws Exception {
    try {
      convertor.executeFunction(parameters, null);
      fail("These parameters should have been rejected: " + parameters);
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage(), e.getMessage().contains(expectedInError));
    }
  }

  /**
   * Builds an extra context whose predicate denies the documents whose location contains the given
   * text, and allows everything else.
   *
   * @param predicateKey    The key of the predicate to install.
   * @param deniedLocation  The text identifying the denied documents.
   *
   * @return The extra context.
   */
  private static Map<String, Object> deny(String predicateKey, String deniedLocation) {
    Map<String, Object> extraContext = new HashMap<>();
    // The sandbox predicate answers "is allowed", the ai-ignore one answers "is ignored", so the
    // denied answer is the opposite one for each of them.
    boolean isAiIgnorePredicate = ExternalAIFunction.AI_IGNORE_PREDICATE_KEY.equals(predicateKey);
    extraContext.put(predicateKey,
        (Predicate<String>) url -> url.contains(deniedLocation) == isAiIgnorePredicate);
    return extraContext;
  }

  /**
   * Runs a conversion that is expected to be refused and asserts that no document was converted, the
   * output folder was not created, and the caller was told what to correct.
   *
   * @param outputFolderPath  The output folder to convert into, it must not exist afterwards.
   * @param inputFormat       The input format to ask for.
   * @param outputFormat      The output format to ask for.
   * @param inputFiles        The input locations to convert.
   * @param expectedInReason  The parts of the request the reason must name.
   */
  private void assertNothingConverted(String outputFolderPath, String inputFormat, String outputFormat,
      JSONArray inputFiles, String... expectedInReason) throws Exception {
    outputFolder = new File(outputFolderPath);

    JSONObject result = new JSONObject(convertor.executeFunction(new JSONObject()
        .put("input_format", inputFormat)
        .put("output_format", outputFormat)
        .put("input_files", inputFiles)
        .put("output_folder", outputFolder.getPath())
        .toString(), null));

    assertNoConvertedDocuments(result, expectedInReason);
    assertFalse("Nothing should have been written for " + inputFiles, outputFolder.exists());
  }

  /**
   * Asserts that the given outcome holds no converted document and gives a reason naming the parts
   * of the request the caller has to correct.
   *
   * @param result            The outcome of the conversion.
   * @param expectedInReason  The parts of the request the reason must name.
   */
  private static void assertNoConvertedDocuments(JSONObject result, String... expectedInReason) {
    assertEquals("No document should have been converted: " + result, 0,
        result.optJSONArray("convertedFiles") != null ? result.getJSONArray("convertedFiles").length() : 0);

    String reason = result.optString("error", null);
    assertNotNull("The caller must be told why nothing was converted, got: " + result, reason);
    for (String expected : expectedInReason) {
      assertTrue("The reason must name \"" + expected + "\", it was: " + reason,
          reason.contains(expected));
    }
  }

  /**
   * Runs a conversion in which the given location is off limits and asserts that it is refused
   * naming that location, without writing anything.
   *
   * @param outputFolderPath  The output folder to convert into, it must not exist afterwards.
   * @param deniedLocation    The text identifying the location that is off limits.
   * @param predicateKey      The key of the access predicate that denies it.
   */
  private void assertAccessDenied(String outputFolderPath, String deniedLocation, String predicateKey)
      throws Exception {
    outputFolder = new File(outputFolderPath);

    try {
      convertor.executeFunction(new JSONObject()
          .put("input_format", "json")
          .put("output_format", "yaml")
          .put("input_files", new JSONArray().put("test-sample/jsonTest.json"))
          .put("output_folder", outputFolder.getPath())
          .toString(), deny(predicateKey, deniedLocation));
      fail("The conversion should have been refused for " + deniedLocation);
    } catch (ExternalServiceException e) {
      assertTrue(e.getMessage(), e.getMessage().contains(deniedLocation));
    }

    assertFalse("Nothing should have been written for " + deniedLocation, outputFolder.exists());
  }

  /**
   * <p><b>Description:</b> Test that a relative location is resolved against the current project.</p>
   *
   * <p><b>Bug ID:</b> EXM-57614</p>
   *
   * @author vlad_greaca
   */
  @Test
  public void testRelativeLocationsAreProjectRelative() throws Exception {
    File projectFolder = new File("test-sample/ai-project").getCanonicalFile();
    try {
      projectFolder.mkdirs();
      StandalonePluginWorkspace pluginWSMock = Mockito.mock(StandalonePluginWorkspace.class);
      ProjectController projectManagerMock = Mockito.mock(ProjectController.class);
      Mockito.when(pluginWSMock.getProjectManager()).thenReturn(projectManagerMock);
      Mockito.when(projectManagerMock.getCurrentProjectURL())
          .thenReturn(new File(projectFolder, "project.xpr").toURI().toURL());
      PluginWorkspaceProvider.setPluginWorkspace(pluginWSMock);

      // A relative location lands in the project, not in the working directory of the application.
      File inProject = new File(projectFolder, "docs/topic.md");
      assertEquals(inProject, BatchConvertorAITool.toFile("docs/topic.md"));
      assertEquals(projectFolder, BatchConvertorAITool.toFile("."));

      // An absolute path and a file URL are locations on their own, the project does not apply.
      assertEquals(inProject, BatchConvertorAITool.toFile(inProject.getAbsolutePath()));
      assertEquals(inProject, BatchConvertorAITool.toFile(inProject.toURI().toURL().toExternalForm()));

      // A remote location is not a local one, whatever project is opened.
      assertNull(BatchConvertorAITool.toFile("http://www.oxygenxml.com/docs"));
    } finally {
      PluginWorkspaceProvider.setPluginWorkspace(null);
      FileComparationUtil.deleteRecursivelly(projectFolder);
    }
  }
}
