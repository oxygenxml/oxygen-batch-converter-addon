package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.converters.ConversionResult;

import junit.framework.TestCase;
import tests.utils.FileComparationUtil;
import tests.utils.TransformerFactoryCreatorImpl;

/**
 * Test the PrettyContentPrinterImpl.
 * @author cosmin_duna
 *
 */
public class PrettyContentPrinterTest extends TestCase {
	/**
	 * <p><b>Description:</b>The content to print is write in the output file
	 *  even if the indent fails.</p>
	 * <p><b>Bug ID:</b> EXM-41083</p>
	 *
	 * @author cosmin_duna
	 * @throws TransformerException 
	 * @throws IOException 
	 *
	 */
	@Test
	public void testPrettyPrintFallback() throws TransformerException, IOException {
	  File outputFile = new File("test-sample/prettyPrintTest/outputFile.xml");
	  try {
	    String contentToPrint = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	        "<p>Withour root.</p>\n" + 
	        "<topic id=\"topicID\">\n" + 
	        "    <title>title</title>\n" + 
	        "    <body>\n" + 
	        "        <section>\n" + 
	        "            <title>DESCRIPTION</title>\n" + 
	        "        </section>\n" + 
	        "    </body>\n" + 
	        "</topic>";

	    PrettyContentPrinterImpl printerImpl = new PrettyContentPrinterImpl();
	    printerImpl.print(new ConversionResult(contentToPrint), 
	        new TransformerFactoryCreatorImpl(),
	        ConverterTypes.HTML_TO_DITA,
	        outputFile,
	        null);

	    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
	        "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
	        "<p>Withour root.</p>\n" + 
	        "<topic id=\"topicID\">\n" + 
	        "    <title>title</title>\n" + 
	        "    <body>\n" + 
	        "        <section>\n" + 
	        "            <title>DESCRIPTION</title>\n" + 
	        "        </section>\n" + 
	        "    </body>\n" + 
	        "</topic>", FileComparationUtil.readFile(outputFile.toString()));
	  } finally {
	    FileComparationUtil.deleteRecursivelly(outputFile.getParentFile());
	  }
	}
}
