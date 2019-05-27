package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.trasformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.ProgressDialogInteractorTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;
/**
 * JUnit for WORD to XHTML conversion.
 *  
 * @author cosmin_duna
 *
 */
public class WordToXHtmlConverterTest {

	/**
   * <p><b>Description:</b> Test conversion from a docx file to XHTML.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testConversionFromDocx() throws IOException {
		File inputFile  = new File("test-sample/wordTo/test.docx");		
		File expectedResultFile = new File("test-sample/wordTo/resultOfDocxToXHTML.xhtml");
		File outputFolder = inputFile.getParentFile();
		outputFolder = new File(outputFolder, "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(inputFile);
				
		File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.WORD_TO_XHTML, inputFiles, outputFolder, false);

			String expected = FileUtils.readFileToString(expectedResultFile).replace("\\img", File.separatorChar+"img");
			assertEquals(filterMathAttributes(expected),
					filterMathAttributes(FileUtils.readFileToString(fileToRead)));

			File mediaFolder = new File(outputFolder, "media");
			File[] images = mediaFolder.listFiles();
			assertEquals(1, images.length);
			
			String imageName = images[0].getName();
			assertEquals("img.tif", imageName);
			
		} finally {
			FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}

	/**
   * <p><b>Description:</b> Test conversion from a doc file to XHTML.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testConversionFromDoc() throws IOException {
		File inputFile  = new File("test-sample/wordTo/test.doc");		
		File expectedResultFile = new File("test-sample/wordTo/resultOfDocToXHTML.xhtml");
		File outputFolder = inputFile.getParentFile();
		outputFolder = new File(outputFolder, "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(inputFile);
				
		File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.WORD_TO_XHTML, inputFiles, outputFolder, false);

			assertEquals(FileUtils.readFileToString(expectedResultFile).replace("\\img", File.separatorChar+"img"),
					FileUtils.readFileToString(fileToRead));

			File mediaFolder = new File(outputFolder, "media");
			File[] images = mediaFolder.listFiles();
			assertEquals(2, images.length);
			assertTrue(Arrays.asList(images).toString().contains("img(1).png"));
	   	assertTrue(Arrays.asList(images).toString().contains("img.png"));
			
		} finally {
			FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}
	
	/**
	 * Filter some math attributes in the html content.
	 * 
	 * @param html The html content.
	 * 
	 * @return
	 */
	private String filterMathAttributes(String html) {
		html = html.replace("w:ascii=\"Cambria Math\"", "");
		return html.replace("w:cs=\"Cambria Math\"", "");
	}
}
