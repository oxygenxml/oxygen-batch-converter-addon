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
import com.oxygenxml.resources.batch.converter.reporter.StatusReporter;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import ro.sync.basic.util.URLUtil;
import ro.sync.exml.plugin.ai.ExternalAIFunction;
import ro.sync.exml.plugin.ai.ExternalServiceException;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
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
        + "between XML, JSON, YAML and XSD to JSON Schema. Returns the produced output files and any conversion "
        + "problems in JSON format.";
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
    properties.put("input_format", new JSONObject()
        .put("type", "string")
        .put("description",
            "The format of the input files. Possible values: \"html\", \"markdown\", \"word\", \"excel\", "
                + "\"confluence\", \"docbook\", \"openapi\", \"json\", \"yaml\", \"xml\" or \"xsd\"."));
    properties.put("output_format", new JSONObject()
        .put("type", "string")
        .put("description",
            "The format of the output files. Possible values: \"xhtml\", \"dita\", \"docbook4\", \"docbook5\", "
                + "\"json\", \"yaml\" or \"xml\"."));
    properties.put("input_files", new JSONObject()
        .put("type", "array")
        .put("items", new JSONObject().put("type", "string"))
        .put("description",
            "The input files or directories to convert, as filesystem paths or file URLs."
                + "Directories are searched recursively for files matching the input format and their "
                + "folder structure is recreated in the output folder."));
    properties.put("output_folder", new JSONObject()
        .put("type", "string")
        .put("description",
            "The output folder where the converted files are written, as a filesystem path or file URL. "
                + "It is created if it does not exist."));
    properties.put("split_sections", new JSONObject()
        .put("type", "boolean")
        .put("description",
            "Optional. Split sections marked by titles or headings into separate files and create a DITA Map "
                + "(only for Word, HTML, Markdown, DocBook and OpenAPI to DITA conversions). Defaults to true."));
    properties.put("create_short_description", new JSONObject()
        .put("type", "boolean")
        .put("description",
            "Optional. Create short description elements from the first paragraph after headings (only for "
                + "Markdown to DITA conversions). Defaults to false."));
    properties.put("max_heading_level_for_topics", new JSONObject()
        .put("type", "integer")
        .put("description",
            "Optional. The maximum heading level used to create separate DITA topics when converting to DITA."));
    properties.put("explanation", new JSONObject()
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
        Arrays.asList("input_format", "output_format", "input_files", "output_folder", "explanation")));
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

    String inputFormat = params.optString("input_format", null);
    String outputFormat = params.optString("output_format", null);
    if (StringUtils.isBlank(inputFormat) || StringUtils.isBlank(outputFormat)) {
      throw new IllegalArgumentException("Both 'input_format' and 'output_format' must be provided.");
    }
    inputFormat = inputFormat.trim().toLowerCase(Locale.ROOT);
    outputFormat = outputFormat.trim().toLowerCase(Locale.ROOT);

    JSONArray inputFilesArray = params.optJSONArray("input_files");
    if (inputFilesArray == null || inputFilesArray.length() == 0) {
      throw new IllegalArgumentException("At least one input file must be provided in 'input_files'.");
    }

    String outputFolderPath = params.optString("output_folder", null);
    if (StringUtils.isBlank(outputFolderPath)) {
      throw new IllegalArgumentException("The 'output_folder' must be provided.");  
    }

    String converterType = ConversionFormatUtil.getConverterType(inputFormat, outputFormat);
    if (converterType == null) {
      return error("The \"" + inputFormat + "\" to \"" + outputFormat + "\" conversion is not supported.");
    }

    File outputFolder = toFile(outputFolderPath);
    if (outputFolder == null) {
      return error("The output folder is not a local folder: " + outputFolderPath);
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
      return error("The following input files are not on the local filesystem: "
          + String.join(", ", nonLocalEntries));
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
        converterType, readRequestedOptions(params),
        params.has("max_heading_level_for_topics") ? params.optInt("max_heading_level_for_topics") : null));
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
    putIfPresent(params, "split_sections", requestedOptions,
        ConversionOptionTags.CREATE_DITA_MAP_FROM_WORD, ConversionOptionTags.CREATE_DITA_MAP_FROM_HTML,
        ConversionOptionTags.CREATE_DITA_MAP_FROM_MD, ConversionOptionTags.CREATE_DITA_MAP_FROM_DOCBOOK,
        ConversionOptionTags.CREATE_DITA_MAP_FROM_OPEN_API);
    putIfPresent(params, "create_short_description", requestedOptions,
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

    // The constructor used by the command line script: it already runs the conversion without a
    // worker and without a progress dialog, using the Oxygen transformer factory.
    List<File> outputFiles = new BatchConverterImpl(problemReporter, NO_STATUS_REPORTING, NO_PROGRESS_DIALOG)
        .convertFiles(converterType, inputsProvider);

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
    return result.toString();
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
      ExternalAIFunction.class.getMethod("checkDocumentAccessPermissions", String.class, Map.class);
      ExternalAIFunction.class.getMethod("checkNotIgnoredFromAiIgnoreFile", String.class, Map.class);
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
    // A URL, of any protocol, is a location on its own: it is never resolved against the project,
    // so that a remote one is still reported as not being on the local filesystem.
    URL projectURL = URLUtil.isRelativePath(trimmedLocation) ? getCurrentProjectURL() : null;
    if (projectURL != null) {
      try {
        // Resolves the path against the folder holding the project file, keeping an absolute one.
        file = URLUtil.computeCanonicalFile(projectURL, trimmedLocation);
      } catch (IOException e) {
        LOGGER.debug(e.getMessage(), e);
      }
    }
    if (file == null) {
      // A URL, or a path with no project to resolve it against: the working directory of the
      // application is then all there is to resolve it against.
      URL url = URLUtil.convertToURL(trimmedLocation);
      file = url != null ? URLUtil.getCanonicalFileFromFileUrl(url) : null;
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
    // a counter is added to the name instead, and nothing is removed. Sandbox and ai-ignore access
    // is enforced in executeFunction via the predicates from the extra context.
    return true;
  }
}
