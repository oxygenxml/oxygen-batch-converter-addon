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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.resources.batch.converter.InputFilesManager;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import ro.sync.basic.util.URLUtil;
import ro.sync.exml.plugin.ai.ExternalAIFunction;
import ro.sync.exml.plugin.ai.ExternalServiceException;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;

/**
 * Base of the AI functions contributed by the Batch Documents Converter: resolves the locations given by the AI,
 * enforces the AI sandbox and the <code>.ai-ignore</code> rules, and collects the input files.
 *
 * @author cosmin_duna
 */
public abstract class AbstractBatchConverterAITool implements ExternalAIFunction {

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBatchConverterAITool.class.getName());

  /**
   * <code>true</code> when the running Oxygen provides the AI document-access API.
   *
   * @see #isSandboxAccessAPIAvailable()
   */
  protected static final boolean SANDBOX_ACCESS_API_AVAILABLE = isSandboxAccessAPIAvailable();

  /**
   * The name of the parameter holding the files and directories to process.
   */
  protected static final String PARAM_INPUT_FILES = "input_files";

  /**
   * The name of the parameter holding the reason the AI gives for the call.
   */
  protected static final String PARAM_EXPLANATION = "explanation";

  /**
   * The category used to group the functions of the add-on in the UI.
   */
  private static final String CATEGORY = "Conversion";

  /**
   * Returns a short description displayed in the UI when the function is executed.
   * <p>
   * Implements <code>ro.sync.exml.plugin.ai.ExternalAIFunction#getUIDescription()</code>, added in
   * Oxygen 29.0. No <code>@Override</code> because the plugin compiles against an older SDK.
   * </p>
   *
   * @return The UI description.
   */
  public abstract String getUIDescription();

  /**
   * Returns the category used to group this function with other tools in the UI.
   * <p>
   * Implements <code>ro.sync.exml.plugin.ai.ExternalAIFunction#getCategory()</code>, added in
   * Oxygen 29.0. No <code>@Override</code> because the plugin compiles against an older SDK.
   * </p>
   *
   * @return The category.
   */
  public String getCategory() {
    return CATEGORY;
  }

  /**
   * Kept for compatibility with Oxygen versions older than 29.0, where the API method
   * was named <code>getUIDecription()</code>.
   *
   * @return The same value as {@link #getUIDescription()}.
   */
  public String getUIDecription() {
    return getUIDescription();
  }

  /**
   * @see ro.sync.exml.plugin.ai.ExternalAIFunction#isSafe(java.lang.String)
   */
  @Override
  public boolean isSafe(String parameters) {
    // The functions only read the documents the AI names and only add files to the output folder: an
    // existing file is never overwritten, nothing is removed. That alone makes them safe only as long
    // as the AI cannot choose freely where to read from and write to, which is what the sandbox and
    // the ai-ignore rules decide. On an Oxygen that doesn't provide them there is nothing to keep the
    // function inside the project, so the user is asked to confirm it.
    return SANDBOX_ACCESS_API_AVAILABLE;
  }

  /**
   * Parses the parameters given by the AI.
   *
   * @param parameters The parameters, as a JSON object.
   *
   * @return The parameters. Never <code>null</code>.
   *
   * @throws IllegalArgumentException If the parameters are not a JSON object.
   */
  protected static JSONObject parseParameters(String parameters) {
    JSONObject params;
    try {
      params = parameters != null && !parameters.isEmpty() ? new JSONObject(parameters) : new JSONObject();
    } catch (JSONException e) {
      throw new IllegalArgumentException("Invalid parameters: " + e.getMessage(), e);
    }
    return params;
  }

  /**
   * Builds the schema of the <code>explanation</code> parameter.
   *
   * @return The schema of the parameter.
   */
  protected static JSONObject explanationParameter() {
    return new JSONObject()
        .put("type", "string")
        .put("description",
            "One sentence, addressed to the user, explaining why you are calling this tool now and how "
                + "you chose the other parameter values. This is shown to the user for transparency/debugging; "
                + "it is not used by the tool itself.");
  }

  /**
   * Builds the schema of the <code>input_files</code> parameter.
   *
   * @param description The description of the parameter.
   *
   * @return The schema of the parameter.
   */
  protected static JSONObject inputFilesParameter(String description) {
    return new JSONObject()
        .put("type", "array")
        .put("items", new JSONObject().put("type", "string"))
        .put("description", description);
  }

  /**
   * Resolves the input locations given by the AI and expands the directories among them to the files with
   * the given extensions, refusing the request when a location cannot be used.
   *
   * @param params           The parameters given by the AI.
   * @param inputExtensions  The extensions of the files to collect from the directories.
   * @param extraContext     The extra context, used to enforce the AI sandbox / ai-ignore access rules.
   * @param formatName       The name of the input format, for the messages.
   *
   * @return The manager holding the files to process and their root directories. Never empty.
   *
   * @throws IllegalArgumentException If a location is missing, is not local or does not exist, or no file is found.
   * @throws ExternalServiceException If access to one of the entries is denied.
   */
  protected InputFilesManager resolveInputFiles(JSONObject params, List<String> inputExtensions,
      Map<String, Object> extraContext, String formatName) throws ExternalServiceException {
    JSONArray inputFilesArray = params.optJSONArray(PARAM_INPUT_FILES);
    if (inputFilesArray == null || inputFilesArray.length() == 0) {
      throw new IllegalArgumentException(
          "At least one input file must be provided in '" + PARAM_INPUT_FILES + "'.");
    }
    List<String> nonLocalEntries = new ArrayList<>();
    List<String> missingEntries = new ArrayList<>();
    InputFilesManager inputFilesManager =
        collectInputFiles(inputFilesArray, inputExtensions, extraContext, nonLocalEntries, missingEntries);
    if (!nonLocalEntries.isEmpty()) {
      throw new IllegalArgumentException(
          "The following input files could not be resolved to local files: "
          + String.join(", ", nonLocalEntries)
          + ". Provide absolute paths or file URLs; a relative path is resolved against the opened project.");
    }
    if (!missingEntries.isEmpty()) {
      throw new IllegalArgumentException(
          "The following input files do not exist: " + String.join(", ", missingEntries));
    }
    if (inputFilesManager.isEmpty()) {
      throw new IllegalArgumentException(
          "No input files matching the \"" + formatName + "\" format were found.");
    }
    return inputFilesManager;
  }

  /**
   * Resolves the input locations given by the AI and expands the directories among them to the files
   * with the given extensions, keeping the files named individually and recording the root directory
   * of the expanded ones, so that the folder structure can be recreated in the output.
   *
   * @param inputFilesArray  The input locations given by the AI.
   * @param inputExtensions  The extensions of the files to collect from the directories.
   * @param extraContext     The extra context, used to enforce the AI sandbox / ai-ignore access rules.
   * @param nonLocalEntries  Where to collect the entries that are not on the local filesystem.
   * @param missingEntries   Where to collect the locations of the entries that do not exist.
   *
   * @return The manager holding the files to process and their root directories.
   *
   * @throws ExternalServiceException If access to one of the entries is denied.
   */
  protected InputFilesManager collectInputFiles(JSONArray inputFilesArray, List<String> inputExtensions,
      Map<String, Object> extraContext, List<String> nonLocalEntries, List<String> missingEntries)
      throws ExternalServiceException {
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
  protected List<File> filterAccessible(List<File> files, Map<String, Object> extraContext) {
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
  protected void checkAccessAllowed(File file, Map<String, Object> extraContext) throws ExternalServiceException {
    if (SANDBOX_ACCESS_API_AVAILABLE) {
      String location = toLocation(file);
      checkDocumentAccessPermissions(location, extraContext);
      checkNotIgnoredFromAiIgnoreFile(location, extraContext);
    }
  }

  /**
   * Checks whether the document-access API is available in the running Oxygen version.
   * <p>
   * The add-on supports Oxygen 28.0 and newer, but the access helpers were only added to
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
}
