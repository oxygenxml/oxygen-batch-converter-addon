package com.oxygenxml.resources.batch.converter.printer;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.ConverterTypes;

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
	 * The output file used to test.
	 */
	File outputFile;
	
	@Override
	protected void setUp() throws Exception {
		outputFile = new File("test-sample/outputFile.xml");
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		outputFile.delete();
		super.tearDown();
	}
	
	/**
   * <p><b>Description:</b>The content to print is pretty print and write to output file.</p>
   * <p><b>Bug ID:</b> EXM-41083</p>
   *
   * @author cosmin_duna
	 * @throws TransformerException 
	 * @throws IOException 
   *
   */
	@Test
	public void testPrettyPrint() throws TransformerException, IOException {
		String contentToPrint = ""+
				"<topic id=\"topicID\">" + 
				"<title>title</title>" + 
				"<body>" + 
				"<section>" + 
				"<title>DESCRIPTION</title>\n" + 
				" </section>" + 
				" </body>" + 
				"</topic>";
	
		PrettyContentPrinterImpl printerImpl = new PrettyContentPrinterImpl();
		printerImpl.print(contentToPrint, 
				new TransformerFactoryCreatorImpl(),
				ConverterTypes.HTML_TO_DITA,
				outputFile,
				null);
	
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"\n" + 
				"<!DOCTYPE topic\n" + 
				"  PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">\n" + 
				"<topic id=\"topicID\">    \n" + 
				"   <title>title</title>    \n" + 
				"   <body>        \n" + 
				"      <section>\n" + 
				"         <title>DESCRIPTION</title>\n" + 
				"        \n" + 
				"      </section>    \n" + 
				"   </body>\n" + 
				"</topic>", FileComparationUtil.readFile(outputFile.toString()));
	}

	
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
		String contentToPrint = ""
				+ "<p>Withour root.</p>\n" + 
				"<topic id=\"topicID\">\n" + 
				"    <title>title</title>\n" + 
				"    <body>\n" + 
				"        <section>\n" + 
				"            <title>DESCRIPTION</title>\n" + 
				"        </section>\n" + 
				"    </body>\n" + 
				"</topic>";
	
		PrettyContentPrinterImpl printerImpl = new PrettyContentPrinterImpl();
		printerImpl.print(contentToPrint, 
				new TransformerFactoryCreatorImpl(),
				ConverterTypes.HTML_TO_DITA,
				outputFile,
				null);
	
		assertEquals(contentToPrint, FileComparationUtil.readFile(outputFile.toString()));
	}
}
