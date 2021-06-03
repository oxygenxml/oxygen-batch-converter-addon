package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;

import tests.utils.TransformerFactoryCreatorImpl;

/**
 * Test HTML5Cleaner.
 * 
 * @author cosmin_duna
 *
 */
public class HTML5CleanerTest {

  /**
   * <p><b>Description:</b> Test unknown HTML 5 elements are removed when XHTML content is processed.< </p>
   * <p><b>Bug ID:</b> EXM-44890</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testRemoveHtml5Elements() throws Exception {
    File sample  = new File("test-sample/EXM-44976/html5.html");   
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    
    //create a HTML to XHTML converter
    HtmlToXhtmlConverter htmlToXhtmlTransformer = new HtmlToXhtmlConverter();
    // Additional processing of XHTML content
    HTML5Cleaner additionalXHTMLProcessor = new HTML5Cleaner();
    
    //convert the content to XHTML
    String xhtmlContent = htmlToXhtmlTransformer.convert(
        sample, null, transformerCreator, null).getConvertedContent();
    
    String processedXhtml = additionalXHTMLProcessor.convert(
        sample, new StringReader(xhtmlContent), transformerCreator, null).getConvertedContent();
    
    assertEquals("The procesed XHTML should not containt 'main' and 'article' elements.",
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n" + 
        "<head>\n" + 
        "<title>Title</title>\n" + 
        "</head>\n" + 
        "<body>\n" + 
        "\n" + 
        "\n" + 
        "<h1 class=\"title topictitle1\" id=\"ariaid-title1\">Title</h1>\n" + 
        "<section class=\"section\">\n" + 
        "<p class=\"p\">Bla bla bla.</p>\n" + 
        "</section>\n" +
        "\n" + 
        "</body>\n" + 
        "</html>",
        processedXhtml);
  }
}
