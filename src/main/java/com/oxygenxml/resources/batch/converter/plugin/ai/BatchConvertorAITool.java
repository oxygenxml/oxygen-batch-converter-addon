/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.ConversionFormatUtil;
import com.oxygenxml.batch.converter.core.ConversionOptionTags;
import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.batch.converter.core.word.inspect.WordDocumentInspector;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.reporter.ResultsUtil;
import com.oxygenxml.resources.batch.converter.reporter.StatusReporter;

import ro.sync.basic.util.URLUtil;
import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.plugin.ai.ExternalServiceException;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.results.ResultsManager;

/**
 * Tool that converts documents between formats using the Oxygen Batch Converter engine.
 *
 * @author vlad_greaca
 */
public class BatchConvertorAITool extends AbstractBatchConverterAITool {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(BatchConvertorAITool.class.getName());

  /**
   * The name of the parameter holding the format of the input files.
   */
  private static final String PARAM_INPUT_FORMAT = "input_format";

  /**
   * The name of the parameter holding the format of the output files.
   */
  private static final String PARAM_OUTPUT_FORMAT = "output_format";

  /**
   * The name of the parameter holding the folder the converted files are written in.
   */
  private static final String PARAM_OUTPUT_FOLDER = "output_folder";

  /**
   * The name of the parameter asking for the sections to be split into separate files.
   */
  private static final String PARAM_SPLIT_SECTIONS = "split_sections";

  /**
   * The name of the parameter asking for short descriptions to be created.
   */
  private static final String PARAM_CREATE_SHORT_DESCRIPTION = "create_short_description";

  /**
   * The name of the parameter holding the maximum heading level for creating topics.
   */
  private static final String PARAM_MAX_HEADING_LEVEL_FOR_TOPICS = "max_heading_level_for_topics";

  /**
   * The name of the parameter asking for the converted files to be opened.
   */
  private static final String PARAM_OPEN_CONVERTED_FILES = "open_converted_files";

  /**
   * The Word input format.
   */
  private static final String WORD_INPUT_FORMAT = "word";

  /**
   * The extensions of the output files whose words are counted.
   */
  private static final List<String> COUNTED_OUTPUT_EXTENSIONS = Arrays.asList("dita", "ditamap", "xhtml", "html");

  /**
   * The AI function has no status bar and no progress dialog, the outcome of the conversion is
   * returned to the AI instead.
   */
  private static final StatusReporter NO_STATUS_REPORTING = new StatusReporter() {
    @Override
    public void setStatusMessage(String message) {
      // Nothing to report.
    }

    @Override
    public void conversionFinished(int convertedCnt, int failedCnt) {
      // Nothing to report.
    }
  };

  /**
   * @see #NO_STATUS_REPORTING
   */
  private static final ProgressDialogInteractor NO_PROGRESS_DIALOG = new ProgressDialogInteractor() {
    @Override
    public void setDialogVisible(boolean state) {
      // No dialog to show.
    }

    @Override
    public void conversionInProgress(File file) {
      // No dialog to update.
    }

    @Override
    public void close() {
      // No dialog to close.
    }
  };

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getName()
   */
  @Override
  public String getName() {
    return "convert_documents";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getDescription()
   */
  @Override
  public String getDescription() {
    return "Convert documents from one format to another."
        + "Provide the input format, the output format, the input files (or directories) and the output "
        + "folder where the converted files are written. Supported conversions include HTML/Markdown/Word/"
        + "Excel/Confluence/DocBook/OpenAPI to DITA, HTML/Markdown/Word to XHTML or DocBook, and conversions "
        + "between XML, JSON, YAML and XSD to JSON Schema. Returns the produced output files (with their size "
        + "and word count) and the conversions that failed, in JSON format. For Word documents, custom styles "
        + "can be mapped to HTML/DITA structure with \"word_styles_mapping\": use \"list_word_styles\" to see "
        + "the styles and \"preview_word_structure\" to check the mapping first; the result then lists the "
        + "styles that remained unmapped (\"unmappedStyles\"). When the conversion also raises warnings, like "
        + "the unrecognized Word styles, these are reported in the Results panel and the result carries "
        + "their \"warningCount\" and the \"warningsResultsTab\" holding them: read that tab when they "
        + "matter. A result without those fields raised no warnings, so there is nothing to read.";
  }

  /**
   * @see AbstractBatchConverterAITool#getUIDescription()
   */
  @Override
  public String getUIDescription() {
    return "Convert documents";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getParameterDescriptions()
   */
  @Override
  public String getParameterDescriptions() {
    JSONObject properties = new JSONObject();
    properties.put(PARAM_INPUT_FORMAT, new JSONObject()
        .put("type", "string")
        .put("description",
            "The format of the input files. Possible values: \"html\", \"markdown\", \"word\", \"excel\", "
                + "\"confluence\", \"docbook\", \"openapi\", \"json\", \"yaml\", \"xml\" or \"xsd\"."));
    properties.put(PARAM_OUTPUT_FORMAT, new JSONObject()
        .put("type", "string")
        .put("description",
            "The format of the output files. Possible values: \"xhtml\", \"dita\", \"docbook4\", \"docbook5\", "
                + "\"json\", \"yaml\" or \"xml\"."));
    properties.put(PARAM_INPUT_FILES, inputFilesParameter(
        "The input files or directories to convert, as filesystem paths or file URLs."
            + "Directories are searched recursively for files matching the input format and their "
            + "folder structure is recreated in the output folder."));
    properties.put(PARAM_OUTPUT_FOLDER, new JSONObject()
        .put("type", "string")
        .put("description",
            "The output folder where the converted files are written, as a filesystem path or file URL. "
                + "It is created if it does not exist."));
    properties.put(PARAM_SPLIT_SECTIONS, new JSONObject()
        .put("type", "boolean")
        .put("description",
            "Optional. Split sections marked by titles or headings into separate files and create a DITA Map "
                + "(only for Word, HTML, Markdown, DocBook and OpenAPI to DITA conversions). Defaults to true."));
    properties.put(PARAM_CREATE_SHORT_DESCRIPTION, new JSONObject()
        .put("type", "boolean")
        .put("description",
            "Optional. Create short description elements from the first paragraph after headings (only for "
                + "Markdown to DITA conversions). Defaults to false."));
    properties.put(PARAM_MAX_HEADING_LEVEL_FOR_TOPICS, new JSONObject()
        .put("type", "integer")
        .put("description",
            "Optional. The maximum heading level used to create separate DITA topics when converting to DITA."));
    properties.put(WordStylesMappingParameter.PARAM_WORD_STYLES_MAPPING, WordStylesMappingParameter.schema(
        "Optional, only for the \"word\" input format. Rules mapping Word styles to the HTML elements the "
            + "conversion is based on, applied on top of the mapping configured by the user in the preferences "
            + "(which is not modified). Each rule is an object with \"element\" (\"p\", \"r\", \"b\", \"i\", "
            + "\"u\", \"strike\", \"table\", \"p:unordered-list(n)\", \"p:ordered-list(n)\" or \"p.StyleId\"), "
            + "an optional \"style\" (the Word style name) and \"html\" (e.g. \"h1:fresh\" for a title, "
            + "\"h2:fresh\" for a heading, \"kbd\", \"i.filepath\", \"pre.codeblock:separator('\\n')\", "
            + "\"blockquote\", \"div.note\", or \"!\" to drop the element). Use \"list_word_styles\" to see the "
            + "styles of the documents and \"preview_word_structure\" to check the resulting outline."));
    properties.put(PARAM_OPEN_CONVERTED_FILES, new JSONObject()
        .put("type", "boolean")
        .put("description",
            "Optional. Open the converted documents in Oxygen once the conversion is done, so that the "
                + "user doesn't have to look for them. Defaults to true. Set it to false when converting "
                + "many documents, or when the user only wants the files written."));
    properties.put(PARAM_EXPLANATION, new JSONObject()
        .put("type", "string")
        .put("description",
            "One sentence, addressed to the user, explaining why you are calling this tool now and how "
                + "you chose the other parameter values (e.g. why these input files, this output format or "
                + "this output folder). This is shown to the user for transparency/debugging; it is not "
                + "used by the tool itself."));

    JSONObject schema = new JSONObject();
    schema.put("type", "object");
    schema.put("properties", properties);
    schema.put("required", new JSONArray(
        Arrays.asList(PARAM_INPUT_FORMAT, PARAM_OUTPUT_FORMAT, PARAM_INPUT_FILES, PARAM_OUTPUT_FOLDER,
            PARAM_EXPLANATION)));
    schema.put("additionalProperties", false);
    return schema.toString();
  }

  /**
   * Converts the documents.
   * <p>
   * A request that cannot be carried out throws, so that AI Positron reports it as a failed tool
   * call rather than as an answer: an {@link IllegalArgumentException} for a parameter that cannot
   * be used - a conversion that is not supported, a location that cannot be resolved or does not
   * exist - and a non-fatal {@link ExternalServiceException} when access to a location is denied or
   * the output folder cannot be created. Nothing is converted in either case. Documents that fail to
   * convert once the conversion runs are reported in its answer, next to the ones that were
   * converted.
   * </p>
   *
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#executeFunction(java.lang.String, java.util.Map)
   */
  @Override
  public String executeFunction(String parameters, Map<String, Object> extraContext)
      throws IllegalArgumentException, ExternalServiceException {
    JSONObject params = parseParameters(parameters);

    String inputFormat = params.optString(PARAM_INPUT_FORMAT, null);
    String outputFormat = params.optString(PARAM_OUTPUT_FORMAT, null);
    if (StringUtils.isBlank(inputFormat) || StringUtils.isBlank(outputFormat)) {
      throw new IllegalArgumentException(
          "Both '" + PARAM_INPUT_FORMAT + "' and '" + PARAM_OUTPUT_FORMAT + "' must be provided.");
    }
    inputFormat = inputFormat.trim().toLowerCase(Locale.ROOT);
    outputFormat = outputFormat.trim().toLowerCase(Locale.ROOT);

    JSONArray inputFilesArray = params.optJSONArray(PARAM_INPUT_FILES);
    if (inputFilesArray == null || inputFilesArray.length() == 0) {
      throw new IllegalArgumentException(
          "At least one input file must be provided in '" + PARAM_INPUT_FILES + "'.");
    }

    String outputFolderPath = params.optString(PARAM_OUTPUT_FOLDER, null);
    if (StringUtils.isBlank(outputFolderPath)) {
      throw new IllegalArgumentException("The '" + PARAM_OUTPUT_FOLDER + "' must be provided.");
    }

    Integer maxHeadingLevelForTopics = readMaxHeadingLevelForTopics(params);

    String wordStylesRules = WordStylesMappingParameter.readRules(params);
    if (wordStylesRules != null && !WORD_INPUT_FORMAT.equals(inputFormat)) {
      throw new IllegalArgumentException("The '" + WordStylesMappingParameter.PARAM_WORD_STYLES_MAPPING
          + "' only applies to the \"" + WORD_INPUT_FORMAT + "\" input format, not to \"" + inputFormat + "\".");
    }

    String converterType = ConversionFormatUtil.getConverterType(inputFormat, outputFormat);
    if (converterType == null) {
      throw new IllegalArgumentException(
          "The \"" + inputFormat + "\" to \"" + outputFormat + "\" conversion is not supported.");
    }

    File outputFolder = toFile(outputFolderPath);
    if (outputFolder == null) {
      throw new IllegalArgumentException(
          "The output folder could not be resolved to a local folder: " + outputFolderPath
          + ". Provide an absolute path or a file URL; a relative path is resolved against the opened project.");
    }
    // The converted documents are written here, so the output folder must be accessible as well.
    checkAccessAllowed(outputFolder, extraContext);

    // The parameters are valid: resolve the inputs and expand the directories to the files matching
    // the input format, remembering the directory each file was collected from, so that the folder
    // structure is recreated in the output.
    InputFilesManager inputFilesManager = resolveInputFiles(params,
        Arrays.asList(ExtensionGetter.getInputExtension(converterType)), extraContext, inputFormat);

    // Nothing is created until everything the conversion needs has been validated.
    if (!outputFolder.isDirectory() && !outputFolder.mkdirs()) {
      // Not a parameter error, but no reason to stop the rest of what the AI is doing either.
      throw new ExternalServiceException(
          "Could not create the output folder: " + outputFolder.getAbsolutePath(), false);
    }

    // A Word conversion gets a styles map of its own: the rules given by the AI on top of the user's
    // preferences, applied to this conversion only, and a reporter collecting the unmapped styles.
    String wordStylesMap = null;
    AIUnknownStylesReporter unknownStylesReporter = null;
    if (isWordConverter(converterType)) {
      try {
        wordStylesMap = WordStylesMappingParameter.buildStyleMap(wordStylesRules);
      } catch (IOException e) {
        throw new ExternalServiceException(e.getMessage(), false);
      }
      unknownStylesReporter = new AIUnknownStylesReporter();
    }

    AIConversionInputsProvider inputsProvider = new AIConversionInputsProvider(inputFilesManager, outputFolder,
        converterType, readRequestedOptions(params), maxHeadingLevelForTopics,
        params.optBoolean(PARAM_OPEN_CONVERTED_FILES, true), wordStylesMap, unknownStylesReporter);
    return convertFiles(converterType, inputsProvider, unknownStylesReporter);
  }

  /**
   * Checks whether the given converter reads Word documents with the Word styles mapping.
   *
   * @param converterType The converter type.
   *
   * @return <code>true</code> for the Word to XHTML and Word to DITA converters.
   */
  private static boolean isWordConverter(String converterType) {
    return ConverterTypes.WORD_TO_XHTML.equals(converterType) || ConverterTypes.WORD_TO_DITA.equals(converterType)
        || ConverterTypes.WORD_TO_DB4.equals(converterType) || ConverterTypes.WORD_TO_DB5.equals(converterType);
  }

  /**
   * Reads the maximum heading level for creating topics named by the AI.
   *
   * @param params The parameters given by the AI.
   *
   * @return The level, or <code>null</code> when the AI didn't name one, in which case the level
   *         configured by the user is used.
   *
   * @throws IllegalArgumentException If the given level isn't a positive integer.
   */
  private static Integer readMaxHeadingLevelForTopics(JSONObject params) {
    Integer maxHeadingLevel = null;
    if (params.has(PARAM_MAX_HEADING_LEVEL_FOR_TOPICS)) {
      // A value that isn't a number at all reads as the fallback and is rejected together with the
      // numbers that make no sense as a heading level.
      int level = params.optInt(PARAM_MAX_HEADING_LEVEL_FOR_TOPICS, -1);
      if (level <= 0) {
        throw new IllegalArgumentException(
            "The '" + PARAM_MAX_HEADING_LEVEL_FOR_TOPICS + "' must be a positive integer, but it was: "
                + params.opt(PARAM_MAX_HEADING_LEVEL_FOR_TOPICS));
      }
      maxHeadingLevel = level;
    }
    return maxHeadingLevel;
  }

  /**
   * Reads the conversion options named by the AI and maps them to the option ids of the engine.
   * <p>
   * Only the parameters actually present are collected: an absent one is left to the default of the
   * conversion dialog, see {@link AIConversionInputsProvider}.
   *
   * @param params The parameters given by the AI.
   *
   * @return The requested options, keyed by option id. Never <code>null</code>.
   */
  private static Map<String, Boolean> readRequestedOptions(JSONObject params) {
    Map<String, Boolean> requestedOptions = new HashMap<>();
    // "Split sections" maps to creating a DITA Map. Each converter reads the option matching its own
    // input format and only one of them applies to a given conversion, so they are all requested.
    putIfPresent(params, PARAM_SPLIT_SECTIONS, requestedOptions,
        ConversionOptionTags.CREATE_DITA_MAP_FROM_WORD, ConversionOptionTags.CREATE_DITA_MAP_FROM_HTML,
        ConversionOptionTags.CREATE_DITA_MAP_FROM_MD, ConversionOptionTags.CREATE_DITA_MAP_FROM_DOCBOOK,
        ConversionOptionTags.CREATE_DITA_MAP_FROM_OPEN_API);
    putIfPresent(params, PARAM_CREATE_SHORT_DESCRIPTION, requestedOptions,
        ConversionOptionTags.CREATE_SHORT_DESCRIPTION);
    return requestedOptions;
  }

  /**
   * Records the value of the given parameter for each of the given options, when the AI provided it.
   *
   * @param params           The parameters given by the AI.
   * @param parameterName    The name of the parameter to read.
   * @param requestedOptions Where to collect the requested options.
   * @param optionIds        The ids of the options the parameter sets.
   */
  private static void putIfPresent(JSONObject params, String parameterName,
      Map<String, Boolean> requestedOptions, String... optionIds) {
    if (params.has(parameterName)) {
      boolean value = params.optBoolean(parameterName);
      for (String optionId : optionIds) {
        requestedOptions.put(optionId, value);
      }
    }
  }

  /**
   * Converts the input files through the batch converter engine.
   *
   * @param converterType          The converter type resolved from the conversion formats.
   * @param inputsProvider         The conversion inputs provider.
   * @param unknownStylesReporter  The reporter collecting the unmapped Word styles, or <code>null</code>
   *                               when the conversion does not read Word documents.
   *
   * @return The JSON result describing the produced files and the conversion problems.
   */
  private String convertFiles(String converterType, UserInputsProvider inputsProvider,
      AIUnknownStylesReporter unknownStylesReporter) {
    AIProblemReporter problemReporter = new AIProblemReporter();

    // The conversion adds the warnings it encounters to the Results panel, which is never cleared
    // here, so only the ones this conversion adds count as its own.
    int messagesBefore = countMessagesInConverterTab();

    // The constructor used by the command line script: it already runs the conversion without a
    // worker and without a progress dialog, using the Oxygen transformer factory.
    List<File> outputFiles = new BatchConverterImpl(problemReporter, NO_STATUS_REPORTING, NO_PROGRESS_DIALOG)
        .convertFiles(converterType, inputsProvider);

    int raisedWarnings = countMessagesInConverterTab() - messagesBefore;

    JSONArray convertedFiles = new JSONArray();
    for (File outputFile : outputFiles) {
      // A file the engine did not produce is recorded as a null output, the failure itself is
      // reported through the problem reporter.
      if (outputFile != null) {
        convertedFiles.put(describeOutputFile(outputFile));
      }
    }

    JSONArray problems = problemReporter.getProblems();
    JSONObject result = new JSONObject();
    result.put("convertedFileCount", convertedFiles.length());
    result.put("convertedFiles", convertedFiles);
    result.put("problemCount", problems.length());
    result.put("problems", problems);
    if (unknownStylesReporter != null) {
      result.put("unmappedStyles", WordInspectionJsonUtil.toJson(unknownStylesReporter.getUnmappedStyles()));
    }
    if (raisedWarnings > 0) {
      // The warnings are not failures, so they are not repeated here, only counted and the tab
      // holding them named, so that they are read when there are any and there is something to read.
      result.put("warningCount", raisedWarnings);
      result.put("warningsResultsTab", ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY);
    }
    return result.toString();
  }

  /**
   * Describes a produced file: its location, its size and, for the DITA and XHTML files, its word count,
   * so that the AI can tell small or empty topics without reading every file.
   *
   * @param outputFile The produced file.
   *
   * @return The description.
   */
  static JSONObject describeOutputFile(File outputFile) {
    JSONObject description = new JSONObject();
    description.put(WordInspectionJsonUtil.KEY_LOCATION, toLocation(outputFile));
    description.put("sizeInBytes", outputFile.length());
    String extension = URLUtil.getExtension(outputFile.getName());
    if (extension != null && COUNTED_OUTPUT_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
      try {
        String content = new String(Files.readAllBytes(outputFile.toPath()), StandardCharsets.UTF_8);
        description.put("wordCount", WordDocumentInspector.countWords(content));
      } catch (IOException e) {
        LOGGER.debug(e.getMessage(), e);
      }
    }
    return description;
  }

  /**
   * Counts the messages currently held by the Results panel tab of the converter. A conversion that
   * makes this number grow raised warnings, which is how the tab is only named in the result when
   * there is something to read there.
   *
   * @return The number of messages, or <code>0</code> when there is no Results panel holding them,
   *         in which case there is nothing to read from it either.
   */
  private static int countMessagesInConverterTab() {
    int count = 0;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      ResultsManager resultsManager = pluginWorkspace.getResultsManager();
      if (resultsManager != null) {
        List<DocumentPositionedInfo> results =
            resultsManager.getAllResults(ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY);
        count = results != null ? results.size() : 0;
      }
    }
    return count;
  }

  /**
   * The function writes the converted files to the output folder, so it is not read-only.
   * <p>
   * No {@code @Override}: the {@code ExternalAIFunction#isReadOnly()} API was added in Oxygen 29.0,
   * and this add-on must also compile against older Oxygen versions.
   * </p>
   *
   * @return <code>false</code>.
   */
  public boolean isReadOnly() {
    return false;
  }
}
