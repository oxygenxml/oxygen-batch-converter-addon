package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.extensions.FileExtensionType;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

import junit.framework.TestCase;
import tests.utils.ConverterStatusReporterTestImpl;
import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;
/**
 * Test the Html to Docbook conversion.
 * @author cosmin_duna
 *
 */
public class HtmlToDocbookConverterTest extends TestCase{

	/**
   * <p><b>Description:</b> Test a simple conversion from HTML to DB4.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testSimpleHtmlToDb4() throws IOException {
		File inputFile  = new File("test-sample/htmlTest.html");		
		File expectedResultFile = new File("test-sample/expectedDb4.xml");
		final File outputFolder  = new File(inputFile.getParentFile(), "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

	  final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(inputFile);
				
		File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XML_OUTPUT_EXTENSION, outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.HTML_TO_DB4, new UserInputsProvider() {
        @Override
        public boolean mustOpenConvertedFiles() {
          return false;
        }
        @Override
        public File getOutputFolder() {
          return outputFolder;
        }
        @Override
        public List<File> getInputFiles() {
          return inputFiles;
        }
        @Override
        public Boolean getAdditionalOptionValue(String additionalOptionId) {
          return null;
        }
        @Override
        public Integer getMaxHeadingLevelForCreatingTopics() {
          return 1;
        }
      });

			String actualResult = FileUtils.readFileToString(fileToRead);
			String expected = FileUtils.readFileToString(expectedResultFile);
      assertEquals(expected, actualResult);

		} finally {
			FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}

  /**
   * <p><b>Description:</b> Test a simple conversion from HTML to DB5.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testSimpleHtmlToDb5() throws IOException {
  	File inputFile  = new File("test-sample/htmlTest.html");		
  	File expectedResultFile = new File("test-sample/expectedDb5.xml");
  	final File outputFolder  = new File(inputFile.getParentFile(), "output");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
    final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XML_OUTPUT_EXTENSION, outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.HTML_TO_DB5, new UserInputsProvider() {
        @Override
        public boolean mustOpenConvertedFiles() {
          return false;
        }
        @Override
        public File getOutputFolder() {
          return outputFolder;
        }
        @Override
        public List<File> getInputFiles() {
          return inputFiles;
        }
        @Override
        public Boolean getAdditionalOptionValue(String additionalOptionId) {
          return null;
        }
        @Override
        public Integer getMaxHeadingLevelForCreatingTopics() {
          return 1;
        }
      });
  
  		String actualResult = FileUtils.readFileToString(fileToRead);
  		String expected = FileUtils.readFileToString(expectedResultFile);
      assertEquals(expected, actualResult);
  
  	} finally {
  		FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  }
}
