package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;

/**
 * Additional processing of the XHTML content before to apply conversion to DITA or DocBook.
 * Some unknown HTML5 elements are deleted in this.
 * 
 * @author cosmin_duna
 */
public class AdditionalXHTMLProcessor implements Converter {

  /**
   * Process the given XHTML content.
   * 
   * @param originalFile
   *          The XHTML file.
   * @param contentReader
   *          Reader of the document. If the content reader isn't <code>null</code>, 
   *          the converter will process this reader and will ignore the given file.
   *          
   * @return The processed XHTML content.
   * @throws TransformerException
   */
  @Override
  public ConversionResult convert(File originalFile, Reader contentReader, File baseDir, TransformerFactoryCreator transformerCreator)
      throws TransformerException {

    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);

    final StreamSource src = new StreamSource(new StringReader(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n" + 
            "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" + 
            "    exclude-result-prefixes=\"xs\"\n" + 
            "    version=\"1.0\">\n" + 
            "    <xsl:template match=\"node() | @*\">\n" + 
            "        <xsl:copy>\n" + 
            "            <xsl:apply-templates select=\"node() | @*\"/>\n" + 
            "        </xsl:copy>\n" + 
            "    </xsl:template>\n" + 
            "    \n" + 
            "    <xsl:template match=\"*[local-name()='article'] | *[local-name()='main']\">\n" + 
            "        <xsl:apply-templates/>\n" + 
            "    </xsl:template>\n" + 
        "</xsl:stylesheet>"));

    // create the transformer
    Transformer transformer = transformerCreator.createTransformer(src);

    final ConversionResult conversionResult;
    try {
      // convert the document
      transformer.transform(new StreamSource(contentReader, originalFile.getAbsolutePath()), result);
      conversionResult = new ConversionResult(sw.toString());


    }catch (TransformerException e) {
      throw new TransformerException(e.getException().getMessage() , e.getException().getCause());
    }

    return conversionResult;
  }
}