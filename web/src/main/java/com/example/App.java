package com.example;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.batch.converter.core.ConversionInputsProvider;
import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.converters.ConversionResult;
import com.oxygenxml.batch.converter.core.converters.Converter;
import com.oxygenxml.batch.converter.core.converters.ConverterCreator;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class App {
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

    Javalin app = Javalin.create(config -> {
      // Fly proxy reaches your service via a private IPv4 address, so bind to 0.0.0.0
      config.jetty.defaultHost = "0.0.0.0";
      config.jetty.defaultPort = port;

      // Serve files from src/main/resources/public/*
      config.staticFiles.add("/public", Location.CLASSPATH);
    });

    // Basic health check endpoint for Fly
    app.get("/health", ctx -> ctx.result("ok"));

    // Optional: make "/" go to your static index
    app.get("/", ctx -> ctx.redirect("/index.html"));

    // POST: XML -> JSON using the batch converter core
    app.post("/api/xml-to-json", ctx -> {
      String xml = ctx.body();

      // Write input XML to a temp file (the converter core works with files)
      File tempInput = File.createTempFile("convert-input-", ".xml");
      File tempOutputDir = Files.createTempDirectory("convert-output-").toFile();

      try {
        Files.writeString(tempInput.toPath(), xml);

        // Create converter — same pattern as BatchConverterCli
        Converter converter = ConverterCreator.create(ConverterTypes.XML_TO_JSON);
        TransformerFactoryCreator transformerCreator = new SaxonTransformerFactoryCreator();

        ConversionInputsProvider inputsProvider = new ConversionInputsProvider() {
          @Override public List<File> getInputFiles() { return Collections.singletonList(tempInput); }
          @Override public File getOutputFolder() { return tempOutputDir; }
          @Override public Boolean getAdditionalOptionValue(String additionalOptionId) { return null; }
          @Override public Integer getMaxHeadingLevelForCreatingTopics() { return 5; }
          @Override public String getFormatForSameTypeReferences() { return null; }
        };

        ConversionResult result = converter.convert(tempInput, null, transformerCreator, inputsProvider);
        String content = result.getConvertedContent();

        if (content != null) {
          ctx.contentType("application/json").result(content);
        } else {
          ctx.status(500).result("Conversion produced no output");
        }
      } finally {
        // Clean up temp files
        tempInput.delete();
        File[] outputFiles = tempOutputDir.listFiles();
        if (outputFiles != null) {
          for (File f : outputFiles) f.delete();
        }
        tempOutputDir.delete();
      }
    });

    app.start(port);
  }

  /**
   * Saxon-based TransformerFactoryCreator for standalone usage.
   * Same as in BatchConverterCli.
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
