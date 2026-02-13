package com.oxygenxml.batch.converter.cli;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.batch.converter.core.ConversionInputsProvider;
import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.converters.ConversionResult;
import com.oxygenxml.batch.converter.core.converters.Converter;
import com.oxygenxml.batch.converter.core.converters.ConverterCreator;
import com.oxygenxml.batch.converter.core.extensions.ExtensionGetter;
import com.oxygenxml.batch.converter.core.printers.ContentPrinter;
import com.oxygenxml.batch.converter.core.printers.ContentPrinterCreater;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;

/**
 * Command-line interface for the Oxygen Batch Converter.
 * Converts files between formats (Word, HTML, Markdown, JSON, YAML, XML, etc.)
 */
public class BatchConverterCli {

  private static final Map<String, String> CONVERTER_TYPE_MAP = getConverterTypes();

  public static void main(String[] args) {
    if (args.length == 0 || "--help".equals(args[0]) || "-h".equals(args[0])) {
      printUsage();
      System.exit(args.length == 0 ? 1 : 0);
      return;
    }

    if ("--list".equals(args[0]) || "-l".equals(args[0])) {
      listConversions();
      System.exit(0);
      return;
    }

    String inputFormat = null;
    String outputFormat = null;
    String outputDir = null;
    List<String> inputPaths = new ArrayList<>();
    int maxHeadingLevel = 5;

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "-i":
        case "--input-format":
          if (++i < args.length) inputFormat = args[i];
          break;
        case "-o":
        case "--output-format":
          if (++i < args.length) outputFormat = args[i];
          break;
        case "-d":
        case "--output-dir":
          if (++i < args.length) outputDir = args[i];
          break;
        case "--max-heading-level":
          if (++i < args.length) maxHeadingLevel = Integer.parseInt(args[i]);
          break;
        default:
          // Treat as input file
          if (!args[i].startsWith("-")) {
            inputPaths.add(args[i]);
          } else {
            System.err.println("Unknown option: " + args[i]);
            printUsage();
            System.exit(1);
          }
          break;
      }
    }

    if (inputFormat == null || outputFormat == null) {
      System.err.println("Error: Both --input-format and --output-format are required.");
      printUsage();
      System.exit(1);
      return;
    }

    if (inputPaths.isEmpty()) {
      System.err.println("Error: At least one input file is required.");
      printUsage();
      System.exit(1);
      return;
    }

    // Resolve the converter type from format names
    String converterType = resolveConverterType(inputFormat, outputFormat);
    if (converterType == null) {
      System.err.println("Error: Unsupported conversion: " + inputFormat + " -> " + outputFormat);
      System.err.println("Use --list to see available conversions.");
      System.exit(1);
      return;
    }

    // Resolve input files (expand directories)
    List<File> inputFiles = resolveInputFiles(inputPaths);
    if (inputFiles.isEmpty()) {
      System.err.println("Error: No input files found.");
      System.exit(1);
      return;
    }

    // Determine output directory
    File outputFolder;
    if (outputDir != null) {
      outputFolder = new File(outputDir);
    } else {
      outputFolder = new File("output");
    }
    outputFolder.mkdirs();

    // Run conversion
    int exitCode = convert(converterType, inputFiles, outputFolder, maxHeadingLevel);
    System.exit(exitCode);
  }

  /**
   * Run the conversion.
   */
  private static int convert(String converterType, List<File> inputFiles, File outputFolder, int maxHeadingLevel) {
    Converter converter = ConverterCreator.create(converterType);
    if (converter == null) {
      System.err.println("Error: Could not create converter for type: " + converterType);
      return 1;
    }

    ContentPrinter contentPrinter = ContentPrinterCreater.create(converterType);
    TransformerFactoryCreator transformerCreator = new SaxonTransformerFactoryCreator();
    String outputExtension = ExtensionGetter.getOutputExtension(converterType);

    ConversionInputsProvider inputsProvider = new ConversionInputsProvider() {
      @Override
      public List<File> getInputFiles() { return inputFiles; }
      @Override
      public File getOutputFolder() { return outputFolder; }
      @Override
      public Boolean getAdditionalOptionValue(String additionalOptionId) { return null; }
      @Override
      public Integer getMaxHeadingLevelForCreatingTopics() { return maxHeadingLevel; }
      @Override
      public String getFormatForSameTypeReferences() { return null; }
    };

    int converted = 0;
    int failed = 0;

    for (File inputFile : inputFiles) {
      System.out.println("Converting: " + inputFile.getPath());
      try {
        File outputFile = ConverterFileUtils.getUniqueOutputFile(inputFile, outputExtension, outputFolder);
        ConversionResult result = converter.convert(inputFile, null, transformerCreator, inputsProvider);

        if (!result.shouldSkipPrinting()) {
          String content = result.getConvertedContent();
          if (content != null) {
            if (result.getImposedOutputFileExtension() != null) {
              outputFile = ConverterFileUtils.getUniqueOutputFile(
                  inputFile, result.getImposedOutputFileExtension(), outputFolder);
            }
            contentPrinter.print(result, transformerCreator, converterType, outputFile);
            System.out.println("  -> " + outputFile.getPath());
            converted++;
          } else {
            System.err.println("  Error: No content produced for " + inputFile.getPath());
            failed++;
          }
        } else {
          System.out.println("  -> Skipped (no output needed)");
          converted++;
        }

        // Report conversion problems
        List<String> problems = result.getConversionProblems();
        for (String problem : problems) {
          System.err.println("  Warning: " + problem);
        }

      } catch (Exception e) {
        System.err.println("  Error converting " + inputFile.getPath() + ": " + e.getMessage());
        failed++;
      }
    }

    System.out.println();
    System.out.println("Conversion complete: " + converted + " succeeded, " + failed + " failed.");
    return failed > 0 ? 1 : 0;
  }

  /**
   * Resolve the converter type from input and output format names.
   */
  private static String resolveConverterType(String inputFormat, String outputFormat) {
    String key = inputFormat.toLowerCase() + "_to_" + outputFormat.toLowerCase();
    // Try exact match first
    for (Map.Entry<String, String> entry : CONVERTER_TYPE_MAP.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(key)) {
        return entry.getValue();
      }
    }
    // Try with common aliases
    String normalizedInput = normalizeFormat(inputFormat);
    String normalizedOutput = normalizeFormat(outputFormat);
    key = normalizedInput + "_to_" + normalizedOutput;
    for (Map.Entry<String, String> entry : CONVERTER_TYPE_MAP.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(key)) {
        return entry.getValue();
      }
    }
    return null;
  }

  private static String normalizeFormat(String format) {
    switch (format.toLowerCase()) {
      case "docx": case "word": return "word";
      case "md": case "markdown": return "md";
      case "htm": case "html": return "html";
      case "xhtml": return "xhtml";
      case "dita": return "dita";
      case "xml": return "xml";
      case "json": return "json";
      case "yaml": case "yml": return "yaml";
      case "xls": case "xlsx": case "excel": return "excel";
      case "docbook": case "db": return "docbook";
      case "docbook4": case "db4": return "db4";
      case "docbook5": case "db5": return "db5";
      case "confluence": return "confluence";
      case "openapi": return "openapi";
      case "xsd": return "xsd";
      case "json-schema": case "jsonschema": return "jsonschema";
      case "ditamap": return "ditamap";
      default: return format.toLowerCase();
    }
  }

  /**
   * Resolve input files: expand directories to their files.
   */
  private static List<File> resolveInputFiles(List<String> inputPaths) {
    List<File> files = new ArrayList<>();
    for (String path : inputPaths) {
      File f = new File(path);
      if (f.isDirectory()) {
        try (Stream<Path> stream = Files.list(f.toPath())) {
          stream.filter(p -> Files.isRegularFile(p))
                .map(Path::toFile)
                .forEach(files::add);
        } catch (IOException e) {
          System.err.println("Warning: Could not list directory: " + path);
        }
      } else if (f.isFile()) {
        files.add(f);
      } else {
        System.err.println("Warning: File not found: " + path);
      }
    }
    return files;
  }

  /**
   * Get all converter types via reflection on ConverterTypes fields.
   */
  private static Map<String, String> getConverterTypes() {
    Map<String, String> types = new LinkedHashMap<>();
    try {
      for (Field field : ConverterTypes.class.getDeclaredFields()) {
        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
            && field.getType() == String.class) {
          field.setAccessible(true);
          String value = (String) field.get(null);
          if (value != null) {
            types.put(field.getName().toLowerCase(), value);
          }
        }
      }
    } catch (Exception e) {
      // Fallback: add known types manually
      types.put("html_to_xhtml", "html-to-xhtml");
      types.put("html_to_dita", "html-to-dita");
      types.put("md_to_xhtml", "md-to-xhtml");
      types.put("md_to_dita", "md-to-dita");
      types.put("word_to_xhtml", "word-to-xhtml");
      types.put("word_to_dita", "word-to-dita");
      types.put("xml_to_json", "xml-to-json");
      types.put("json_to_xml", "json-to-xml");
      types.put("yaml_to_json", "yaml-to-json");
      types.put("json_to_yaml", "json-to-yaml");
      types.put("yaml_to_xml", "yaml-to-xml");
      types.put("xml_to_yaml", "xml-to-yaml");
      types.put("excel_to_dita", "excel-to-dita");
    }
    return types;
  }

  private static void listConversions() {
    System.out.println("Available conversions:");
    System.out.println();
    for (Map.Entry<String, String> entry : CONVERTER_TYPE_MAP.entrySet()) {
      String name = entry.getKey().replace("_to_", " -> ");
      System.out.println("  " + name);
    }
  }

  private static void printUsage() {
    System.out.println("Oxygen Batch Converter CLI");
    System.out.println();
    System.out.println("Usage:");
    System.out.println("  batch-converter -i <input-format> -o <output-format> [-d <output-dir>] <input-files...>");
    System.out.println();
    System.out.println("Options:");
    System.out.println("  -i, --input-format <format>    Input format (e.g., word, html, md, json, xml, yaml, excel)");
    System.out.println("  -o, --output-format <format>   Output format (e.g., dita, xhtml, json, xml, yaml, db4, db5)");
    System.out.println("  -d, --output-dir <dir>         Output directory (default: ./output)");
    System.out.println("  --max-heading-level <n>         Max heading level for DITA topic creation (default: 5)");
    System.out.println("  -l, --list                     List available conversions");
    System.out.println("  -h, --help                     Show this help message");
    System.out.println();
    System.out.println("Examples:");
    System.out.println("  batch-converter -i word -o dita -d output/ document.docx");
    System.out.println("  batch-converter -i md -o xhtml -d output/ *.md");
    System.out.println("  batch-converter -i json -o xml -d output/ data.json");
    System.out.println("  batch-converter -i html -o dita -d output/ page.html");
  }

  /**
   * Saxon-based TransformerFactoryCreator for standalone usage.
   */
  static class SaxonTransformerFactoryCreator implements TransformerFactoryCreator {
    @Override
    public Transformer createTransformer(StreamSource streamSource) throws TransformerConfigurationException {
      TransformerFactory factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
      if (streamSource != null) {
        return factory.newTransformer(streamSource);
      } else {
        return factory.newTransformer();
      }
    }
  }
}
