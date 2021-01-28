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

public class JsonToYamlConverter implements Converter {

  /**
   * Convert JSON to YAML.
   * 
   * @param originalFile
   *          The JSON file.
   * @param contentReader
   *          Reader of the document. If the content reader isn't <code>null</code>, 
   *          the converter will process this reader and will ignore the given file.
   * @return The converted YAML content in String format or null if conversion process failed.
   * @throws TransformerException
   */
  @Override
  public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
      throws TransformerException {

    String convertedContent = "";
    try {
      String jsonContent = "";
      // Get the content to parse.
      if (contentReader == null) {
        jsonContent = ConverterFileUtils.readFile(originalFile);
      } else {
        jsonContent = ConverterReaderUtils.getString(contentReader);
      }
      convertedContent = new YamlJsonConverter().convertJsonToYaml(jsonContent);
      
    } catch (IOException e) {
      throw new TransformerException(e.getMessage(), e.getCause());
    }
    return new ConversionResult(convertedContent);
  }
}