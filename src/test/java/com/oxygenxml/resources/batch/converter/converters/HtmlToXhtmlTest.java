package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

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
import tests.utils.ProblemReporterTestImpl;
import tests.utils.ProgressDialogInteractorTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;

/**
 * JUnit for Html to Xhtml conversion.
 * @author Cosmin Duna
 *
 */
public class HtmlToXhtmlTest {

	@Test
	public void testConversion() throws TransformerException, IOException {
	
		File sample  = new File("test-sample/htmlTest.html");		
		File expectedResult = new File("test-sample/expected/HtmlToXhtml.xhtml");
		File outputFolder = sample.getParentFile();
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.HTML_TO_XHTML, inputFiles, outputFolder, false);

			assertEquals(FileUtils.readFileToString(expectedResult), FileUtils.readFileToString(convertedFile));

		} finally {
			Files.delete(convertedFile.toPath());
		}

	}

  /**
   * <p><b>Description:</b> Test unknown tags are not ignored in HTML to XHTML conversion.</p>
   * <p><b>Bug ID:</b> EXM-20562</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testUnknownTagsAreNotRemoved() throws Exception {
	
		File sample  = new File("test-sample/htmlWithUnknownTags.html");		
		File expectedResult = new File("test-sample/expected/XHtmlWithUnknownTags.xhtml");
		File outputFolder = sample.getParentFile();
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);
	
		List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.HTML_TO_XHTML, inputFiles, outputFolder, false);
	
			assertEquals(FileUtils.readFileToString(expectedResult), FileUtils.readFileToString(convertedFile));
	
		} finally {
			Files.delete(convertedFile.toPath());
		}
	
	}
}
