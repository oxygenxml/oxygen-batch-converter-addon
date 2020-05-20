package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.util.PrettyPrintException;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;

/**
 * Convert a DITA Topic with multiple topics or sections into a DITA Map with referred DITA Topics.
 * 
 * @author cosmin_duna
 */
public class MapWithTopicsConverter extends StylesheetConverter{
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(MapWithTopicsConverter.class);

  
  @Override
  public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator,
      UserInputsProvider userInputsProvider) throws TransformerException {
    
    XMLUtilAccess xmlUtilAccess = PluginWorkspaceProvider.getPluginWorkspace().getXMLUtilAccess();
    try {
      String prettyPrintedContent = xmlUtilAccess.prettyPrint(
          contentReader, originalFile.toURI().toURL().toExternalForm());
      contentReader = new StringReader(prettyPrintedContent);
    } catch (PrettyPrintException e) {
      logger.debug(e.getMessage(), e);
    } catch (MalformedURLException e) {
      logger.debug(e.getMessage(), e);
    }
    return super.convert(originalFile, contentReader, transformerCreator, userInputsProvider);
  }
  
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
