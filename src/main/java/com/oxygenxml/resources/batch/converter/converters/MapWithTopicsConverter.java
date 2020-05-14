package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

/**
 * Convert a DITA Topic with multiple topics or sections into a DITA Map with referred DITA Topics.
 * 
 * @author cosmin_duna
 */
public class MapWithTopicsConverter extends StylesheetConverter{
  
  /**
   * @see StylesheetConverter#getStylesheetPath()
   */
  @Override
  public String getStylesheetPath() {
    URL resource = getClass().getClassLoader().getResource(
        "stylesheets/convert-sections-to-map-with-topics.xsl");
    return resource.toExternalForm();
  }
  
  /**
   * @see StylesheetConverter#createTransformationSource(File, Reader, UserInputsProvider)
   */
  @Override
  public Source createTransformationSource(File originalFile, Reader contentReader, UserInputsProvider userInputsProvider) {
    return new StreamSource(contentReader, 
        // The refactoring stylesheet uses this to create the output topics 
        ConverterFileUtils.getUniqueOutputFile(originalFile, 
            FileExtensionType.DITA_OUTPUT_EXTENSION, userInputsProvider.getOutputFolder()).getAbsolutePath());
  }
  
  /**
   * @see StylesheetConverter#processConversionResult(String)
   */
  @Override
  public ConversionResult processConversionResult(String conversionResult) {
    final ConversionResult result;
    if( conversionResult.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><map")) {
      result = new ConversionResult(
          conversionResult,
          "-//OASIS//DTD DITA Map//EN",
          "map.dtd",
          FileExtensionType.DITA_MAP_OUTPUT_EXTENSION);
    } else {
      result = super.processConversionResult(conversionResult);
      result.setToKeepTheLastResult();
    }
    return result;
  }
}
