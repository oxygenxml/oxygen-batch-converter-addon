/*
 * Copyright (c) 2026 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.plugin.ai;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.ConversionFormatUtil;
import com.oxygenxml.batch.converter.core.ConversionOptionTags;
import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.reporter.ProgressDialogInteractor;
import com.oxygenxml.resources.batch.converter.reporter.ResultsUtil;
import com.oxygenxml.resources.batch.converter.reporter.StatusReporter;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import ro.sync.basic.util.URLUtil;
import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.plugin.ai.ExternalAIFunction;
import ro.sync.exml.plugin.ai.ExternalServiceException;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;

/**
 * Tool that converts documents between formats using the Oxygen Batch Converter engine.
 *
 * @author vlad_greaca
 */
public class BatchConvertorAITool implements ExternalAIFunction {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(BatchConvertorAITool.class.getName());

  /**
   * <code>true</code> when the running Oxygen provides the AI document-access API.
   *
   * @see #isSandboxAccessAPIAvailable()
   */
  private static final boolean SANDBOX_ACCESS_API_AVAILABLE = isSandboxAccessAPIAvailable();

  /**
   * The name of the parameter holding the format of the input files.
   */
  private static final String PARAM_INPUT_FORMAT = "input_format";

  /**
   * The name of the parameter holding the format of the output files.
   */
  private static final String PARAM_OUTPUT_FORMAT = "output_format";

  /**
   * The name of the parameter holding the files and directories to convert.
   */
  private static final String PARAM_INPUT_FILES = "input_files";

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
   * The name of the parameter holding the reason the AI gives for the conversion.
   */
  private static final String PARAM_EXPLANATION = "explanation";

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
        + "between XML, JSON, YAML and XSD to JSON Schema. Returns the produced output files and the "
        + "conversions that failed, in JSON format. When the conversion also raises warnings, like the "
        + "unrecognized Word styles, these are reported in the Results panel and the result carries "
        + "their \"warningCount\" and the \"warningsResultsTab\" holding them: read that tab when they "
        + "matter. A result without those fields raised no warnings, so there is nothing to read.";
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#getUIDecription()
   */
  @Override
  public String getUIDecription() {
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
    properties.put(PARAM_INPUT_FILES, new JSONObject()
        .put("type", "array")
        .put("items", new JSONObject().put("type", "string"))
        .put("description",
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
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#executeFunction(java.lang.String, java.util.Map)
   */
  @Override
  public String executeFunction(String parameters, Map<String, Object> extraContext)
      throws IllegalArgumentException, ExternalServiceException {
    JSONObject params;
    try {
      params = parameters != null && !parameters.isEmpty() ? new JSONObject(parameters) : new JSONObject();
    } catch (JSONException e) {
      throw new IllegalArgumentException("Invalid parameters: " + e.getMessage(), e);
    }

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

    String converterType = ConversionFormatUtil.getConverterType(inputFormat, outputFormat);
    if (converterType == null) {
      return error("The \"" + inputFormat + "\" to \"" + outputFormat + "\" conversion is not supported.");
    }

    File outputFolder = toFile(outputFolderPath);
    if (outputFolder == null) {
      return error("The output folder could not be resolved to a local folder: " + outputFolderPath
          + ". Provide an absolute path or a file URL; a relative path is resolved against the opened project.");
    }
    // The converted documents are written here, so the output folder must be accessible as well.
    checkAccessAllowed(outputFolder, extraContext);

    // The parameters are valid: resolve the inputs and expand the directories to the files matching
    // the input format, remembering the directory each file was collected from, so that the folder
    // structure is recreated in the output.
    List<String> nonLocalEntries = new ArrayList<>();
    List<String> missingEntries = new ArrayList<>();
    InputFilesManager inputFilesManager =
        collectInputFiles(inputFilesArray, converterType, extraContext, nonLocalEntries, missingEntries);
    if (!nonLocalEntries.isEmpty()) {
      return error("The following input files could not be resolved to local files: "
          + String.join(", ", nonLocalEntries)
          + ". Provide absolute paths or file URLs; a relative path is resolved against the opened project.");
    }
    if (!missingEntries.isEmpty()) {
      return error("The following input files do not exist: " + String.join(", ", missingEntries));
    }
    if (inputFilesManager.isEmpty()) {
      return error("No input files matching the \"" + inputFormat + "\" format were found.");
    }

    // Nothing is created until everything the conversion needs has been validated.
    if (!outputFolder.isDirectory() && !outputFolder.mkdirs()) {
      return error("Could not create the output folder: " + outputFolder.getAbsolutePath());
    }

    return convertFiles(converterType, new AIConversionInputsProvider(inputFilesManager, outputFolder,
        converterType, readRequestedOptions(params), maxHeadingLevelForTopics,
        params.optBoolean(PARAM_OPEN_CONVERTED_FILES, true)));
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
   * @param converterType  The converter type resolved from the conversion formats.
   * @param inputsProvider The conversion inputs provider.
   *
   * @return The JSON result describing the produced files and the conversion problems.
   */
  private String convertFiles(String converterType, UserInputsProvider inputsProvider) {
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
        convertedFiles.put(toLocation(outputFile));
      }
    }

    JSONArray problems = problemReporter.getProblems();
    JSONObject result = new JSONObject();
    result.put("convertedFileCount", convertedFiles.length());
    result.put("convertedFiles", convertedFiles);
    result.put("problemCount", problems.length());
    result.put("problems", problems);
    if (raisedWarnings > 0) {
      // The warnings are not failures, so they are not repeated here, only counted and the tab
      // holding them named, so that they are read when there are any and there is something to read.
      result.put("warningCount", raisedWarnings);
      result.put("warningsResultsTab", ResultsUtil.BATCH_CONVERTER_RESULTS_TAB_KEY);
    }
    return result.toString();
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
   * Resolves the input locations given by the AI and expands the directories among them to the files
   * matching the input format, keeping the files named individually and recording the root directory
   * of the expanded ones, so that the folder structure is recreated in the output.
   *
   * @param inputFilesArray  The input locations given by the AI.
   * @param converterType    The converter type.
   * @param extraContext     The extra context, used to enforce the AI sandbox / ai-ignore access rules.
   * @param nonLocalEntries  Where to collect the entries that are not on the local filesystem.
   * @param missingEntries   Where to collect the locations of the entries that do not exist.
   *
   * @return The manager holding the files to convert and their root directories.
   *
   * @throws ExternalServiceException If access to one of the entries is denied.
   */
  private InputFilesManager collectInputFiles(JSONArray inputFilesArray, String converterType,
      Map<String, Object> extraContext, List<String> nonLocalEntries, List<String> missingEntries)
      throws ExternalServiceException {
    List<String> inputExtensions = Arrays.asList(ExtensionGetter.getInputExtension(converterType));
    InputFilesManager inputFilesManager = new InputFilesManager();
    Set<File> processedEntries = new HashSet<>();
    for (int i = 0; i < inputFilesArray.length(); i++) {
      String path = inputFilesArray.optString(i, null);
      if (!StringUtils.isBlank(path)) {
        File entry = toFile(path);
        // Verify the entry the AI named: an unusable one is collected and reported here, so that the
        // collection below is the same one the conversion dialog performs on entries the user picked.
        if (entry == null) {
          nonLocalEntries.add(path.trim());
        } else if (!processedEntries.add(entry)) {
          LOGGER.debug("Input was already collected and is ignored: {}", entry);
        } else if (!entry.exists()) {
          missingEntries.add(toLocation(entry));
        } else {
          checkAccessAllowed(entry, extraContext);
          if (entry.isDirectory()) {
            inputFilesManager.addFilesFromFolder(
                filterAccessible(ConverterFileUtils.getAllFiles(entry, inputExtensions), extraContext), entry);
          } else {
            inputFilesManager.addFiles(Arrays.asList(entry));
          }
        }
      }
    }
    return inputFilesManager;
  }

  /**
   * Keeps only the files the AI is allowed to access.
   *
   * @param files        The files to filter.
   * @param extraContext The extra context, used to enforce the AI sandbox / ai-ignore access rules.
   *
   * @return The accessible files.
   */
  private List<File> filterAccessible(List<File> files, Map<String, Object> extraContext) {
    if (!SANDBOX_ACCESS_API_AVAILABLE) {
      return files;
    }
    List<File> accessibleFiles = new ArrayList<>();
    for (File file : files) {
      String location = toLocation(file);
      if (isDocumentAccessAllowed(location, extraContext) && !isIgnoredFromAiIgnoreFile(location, extraContext)) {
        accessibleFiles.add(file);
      } else {
        LOGGER.debug("Input is not accessible to the AI and is ignored: {}", file);
      }
    }
    return accessibleFiles;
  }

  /**
   * Verifies that the AI is allowed to access the given file, both by the AI project sandbox and by
   * the <code>.ai-ignore</code> rules.
   *
   * @param file         The file to check.
   * @param extraContext The extra context holding the access predicates.
   *
   * @throws ExternalServiceException If access to the file is denied.
   */
  private void checkAccessAllowed(File file, Map<String, Object> extraContext) throws ExternalServiceException {
    if (SANDBOX_ACCESS_API_AVAILABLE) {
      String location = toLocation(file);
      checkDocumentAccessPermissions(location, extraContext);
      checkNotIgnoredFromAiIgnoreFile(location, extraContext);
    }
  }

  /**
   * Checks whether the document-access API is available in the running Oxygen version.
   * <p>
   * The add-on supports Oxygen 26.0 and newer, but the access helpers were only added to
   * {@link ExternalAIFunction} in Oxygen 29.0. They are therefore called only after this probe
   * confirms they exist, so that on an older Oxygen the calls are never linked. On those versions
   * there is no AI project sandbox to enforce either, and no access predicate is passed in the
   * extra context.
   *
   * @return <code>true</code> when the access helpers can be called.
   */
  private static boolean isSandboxAccessAPIAvailable() {
    boolean available = false;
    try {
      // Every helper called on the access path, both the ones that throw and the ones that filter.
      ExternalAIFunction.class.getMethod("checkDocumentAccessPermissions", String.class, Map.class);
      ExternalAIFunction.class.getMethod("checkNotIgnoredFromAiIgnoreFile", String.class, Map.class);
      ExternalAIFunction.class.getMethod("isDocumentAccessAllowed", String.class, Map.class);
      ExternalAIFunction.class.getMethod("isIgnoredFromAiIgnoreFile", String.class, Map.class);
      available = true;
    } catch (NoSuchMethodException e) { // NOSONAR - an older Oxygen, without the AI project sandbox.
      LOGGER.debug("The AI document access API is not available in this Oxygen version.");
    }
    return available;
  }

  /**
   * Converts a location received from the AI to a file. A relative path is resolved against the
   * project currently opened in Oxygen, see {@link #getCurrentProjectURL()}. An absolute path, with
   * either slash, and a file URL are locations on their own and are converted as they are.
   *
   * @param location The location to convert. Not blank.
   *
   * @return The corresponding file, or <code>null</code> if the location isn't a local one.
   */
  static File toFile(String location) {
    String trimmedLocation = location.trim();
    File file = null;
    // A URL, of any protocol, and an absolute path are locations on their own: they are never
    // resolved against the project, so that a remote one is still reported as not being local.
    if (!URLUtil.isRelativePath(trimmedLocation) || new File(trimmedLocation).isAbsolute()) {
      URL url = URLUtil.convertToURL(trimmedLocation);
      file = url != null ? URLUtil.getCanonicalFileFromFileUrl(url) : null;
    } else {
      URL projectURL = getCurrentProjectURL();
      if (projectURL != null) {
        try {
          // Resolves the path against the folder holding the project file.
          file = URLUtil.computeCanonicalFile(projectURL, trimmedLocation);
        } catch (IOException e) {
          LOGGER.debug(e.getMessage(), e);
        }
      }
      // With no opened project the path is left unresolved: the working directory of the
      // application is the installation folder, which is never what the AI meant.
    }
    return file;
  }

  /**
   * Gets the location of the project currently opened in Oxygen.
   *
   * @return The URL of the project file, or <code>null</code> when Oxygen does not run standalone or
   *         no project is opened.
   */
  private static URL getCurrentProjectURL() {
    URL projectURL = null;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace instanceof StandalonePluginWorkspace) {
      ProjectController projectManager = ((StandalonePluginWorkspace) pluginWorkspace).getProjectManager();
      if (projectManager != null) {
        projectURL = projectManager.getCurrentProjectURL();
      }
    }
    return projectURL;
  }

  /**
   * Computes the location to report for the given file, as a URL when possible.
   *
   * @param file The file.
   *
   * @return The location of the file.
   */
  static String toLocation(File file) {
    try {
      return URLUtil.correct(file).toExternalForm();
    } catch (MalformedURLException e) {
      LOGGER.debug(e.getMessage(), e);
      return file.getAbsolutePath();
    }
  }

  /**
   * Builds the JSON response for an operation that could not be carried out.
   *
   * @param message The error message.
   *
   * @return The JSON error response.
   */
  private static String error(String message) {
    return new JSONObject().put("error", message).toString();
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#isSafe(java.lang.String)
   */
  @Override
  public boolean isSafe(String parameters) {
    // The conversion only adds files to the output folder: an existing file is never overwritten,
    // a counter is added to the name instead, and nothing is removed. That alone makes it safe only
    // as long as the AI cannot choose freely where to read from and write to, which is what the
    // sandbox and the ai-ignore rules decide. On an Oxygen that doesn't provide them there is
    // nothing to keep the conversion inside the project, so the user is asked to confirm it.
    return SANDBOX_ACCESS_API_AVAILABLE;
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
