package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * A converter based on a xslt stylesheet.
 * 
 * @author cosmin_duna
 *
 */
abstract class StylesheetConverter implements Converter {

	/**
	 * Convert the given file.
	 * 
	 * @param originalFile        File to be converted
	 * @param contentReader       Reader of the document. If the content reader isn't <code>null</code>, 
	 *          the converter will process this reader and will ignore the given file.
	 * @param transformerCreator  A transformer creator.
   * @param userInputsProvider  Provider for the options set by user.
	 *          
	 * @return The conversion result.
	 * @throws TransformerException
	 */
	@Override
	public ConversionResult convert(File originalFile, Reader contentReader, TransformerFactoryCreator transformerCreator, UserInputsProvider userInputsProvider)
			throws TransformerException {
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		final StreamSource src = new StreamSource(getStylesheetPath());

		Transformer transformer = transformerCreator.createTransformer(src);
		setTransformationParam(transformer);
		
		final ConversionResult conversionResult;
		try {
		  // convert the document
		  transformer.transform(createTransformationSource(originalFile, contentReader, userInputsProvider),
		      result);
		  conversionResult = processConversionResult(sw.toString());
		}catch (TransformerException e) {
			throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
		}
		
		return conversionResult;
	}
	
	/**
	 * Get the path to stylesheet applied in conversion.
	 * 
	 * @return The path to stylesheet applied in conversion.
	 */
  public abstract String getStylesheetPath();

	
  /**
   * Create the transformation source.
   * 
   * @param originalFile        File to be converted
   * @param contentReader       Reader of the document. If the content reader isn't <code>null</code>, 
   *          the converter will process this reader and will ignore the given file.
   * @param userInputsProvider  Provider for the options set by user.
   * 
   * @return The transformation source.
   */
	public Source createTransformationSource(File originalFile, Reader contentReader, UserInputsProvider userInputsProvider){
	  return new StreamSource(contentReader, originalFile.toURI().toString());
	}
	
	/**
	 * Set parameter before to execute the transformation.
	 * 
	 * @param transformer The transformer.
	 */
	public void setTransformationParam(Transformer transformer) {
	  // Nothing
	}
	
	/**
	 * Process the conversion result.
	 * 
	 * @param conversionResult The conversion result in String format.
	 * 
	 * @return The processed conversion result.
	 */
	public ConversionResult processConversionResult(String conversionResult) {
	  return new ConversionResult(conversionResult); 
	} 
}