package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

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
 * JUnit for Html to Xhtml conversion.
 * @author Cosmin Duna
 *
 */
public class HtmlToXhtmlTest {

	@Test
	public void testConversion() throws TransformerException, IOException {
		File sample  = new File("test-sample/htmlTest.html");		
		File expectedResult = new File("test-sample/expected/HtmlToXhtml.xhtml");
		final File outputFolder = new File(sample.getParentFile(), "out");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.HTML_TO_XHTML, new UserInputsProvider() {
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

			assertEquals(FileUtils.readFileToString(expectedResult), FileUtils.readFileToString(convertedFile));

		} finally {
		  FileComparationUtil.deleteRecursivelly(outputFolder);
		}

	}

  /**
   * <p><b>Description:</b> Test unknown tags are not ignored in HTML to XHTML conversion.</p>
   * <p><b>Bug ID:</b> EXM-43766</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testUnknownTagsAreNotRemoved() throws Exception {
		File sample  = new File("test-sample/htmlWithUnknownTags.html");		
		File expectedResult = new File("test-sample/expected/XHtmlWithUnknownTags.xhtml");
		final File outputFolder = new File(sample.getParentFile(), "out");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);
	
		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.HTML_TO_XHTML, new UserInputsProvider() {
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
	
			assertEquals(FileUtils.readFileToString(expectedResult), FileUtils.readFileToString(convertedFile));
	
		} finally {
		  FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	
	}

  /**
   * <p><b>Description:</b> Test "pre" element content is preserved and no extra new lines are added.</p>
   * <p><b>Bug ID:</b> EXM-47898</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testPreservePreContent() throws Exception {
  	File sample  = new File("test-sample/EXM-47898/samplePre.html");		
  	File expectedResult = new File("test-sample/EXM-47898/expected.xhtml");
  	final File outputFolder = new File(sample.getParentFile(), "out");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(sample);
  			
  	File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.HTML_TO_XHTML, new UserInputsProvider() {
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
  
  		assertEquals(FileUtils.readFileToString(expectedResult), FileUtils.readFileToString(convertedFile));
  
  	} finally {
  	  FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  
  }
}
