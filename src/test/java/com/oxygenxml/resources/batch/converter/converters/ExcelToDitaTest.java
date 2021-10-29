package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

import tests.utils.ConverterStatusReporterTestImpl;
import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;

/**
 * JUnit for Excel to DITA conversion.
 * @author cosmin_duna
 */
public class ExcelToDitaTest {

	@Test
	public void testConversion() throws Exception {
		File sample  = new File("test-sample/excel.xls");		
		File expectedOutput = new File("test-sample/excelToDITA.dita");
		final File outputFolder = new File(sample.getParentFile(), "out");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.EXCEL_TO_DITA, new UserInputsProvider() {
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
        public int getMaxHeadingLevelForCreatingTopics() {
          return 1;
        }
      });
      assertEquals(FileUtils.readFileToString(expectedOutput), FileUtils.readFileToString(convertedFile));

		} finally {
		  FileComparationUtil.deleteRecursivelly(outputFolder); 
		}
	}

	/**
   * <p><b>Description:</b> Test invalid characters are escaped in the resulted DITA document.< </p>
   * <p><b>Bug ID:</b> EXM-47523</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testInvalidCharsAreEscaped() throws Exception {
  	File inputFile  = new File("test-sample/EXM-47523/_sample#@.xlsx");		
  	File expectedOutput = new File("test-sample/EXM-47523/expected.dita");
  	final File outputFolder = new File(inputFile.getParentFile(), "out");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.EXCEL_TO_DITA, new UserInputsProvider() {
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
        public int getMaxHeadingLevelForCreatingTopics() {
          return 1;
        }
      });
  		assertTrue(FileComparationUtil.compareLineToLine(expectedOutput, convertedFile));
  	} finally {
  	  FileComparationUtil.deleteRecursivelly(outputFolder); 
  	}
  }
}