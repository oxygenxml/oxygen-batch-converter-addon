package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.transform.TransformerException;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.utils.ConverterReaderUtils;

import ro.sync.yaml.converter.YamlJsonConverter;

public class YamlToJsonConverter implements Converter {

  /**
   * Convert YAML to JSON.
   * 
   * @param originalFile
   *          The YAML file.
   * @param contentReader
   *          Reader of the document. If the content reader isn't <code>null</code>, 
   *          the converter will process this reader and will ignore the given file.
   * @return The converted JSON content in String format or null if conversion process failed.
   * @throws TransformerException
   */
  @Override
  public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
      throws TransformerException {
    String convertedContent = "";
    try {
      String yamlContent = "";
      // Get the content to parse.
      if (contentReader == null) {
        yamlContent = ConverterFileUtils.readFile(originalFile);
      } else {
        yamlContent = ConverterReaderUtils.getString(contentReader);
      }
      convertedContent = new YamlJsonConverter().convertYamlToJson(yamlContent, 4, false);
      
    } catch (IOException e) {
      throw new TransformerException(e.getMessage(), e.getCause());
    }

    return new ConversionResult(convertedContent);
  }
}