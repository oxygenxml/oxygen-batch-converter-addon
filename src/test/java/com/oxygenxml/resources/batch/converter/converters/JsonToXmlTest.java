package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

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
 * JUnit for JSON to XML conversion.
 * @author Cosmin Duna
 *
 */
public class JsonToXmlTest extends TestCase{

	@Test
	public void test() throws TransformerException, IOException {
	
		File sample  = new File("test-sample/jsonTest.json");		
		File goodSample = new File("test-sample/xmlTest.xml");
		final File outputFolder = new File(sample.getParentFile(), "out");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.XML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.JSON_TO_XML, new UserInputsProvider() {
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

			assertEquals(FileComparationUtil.readFile(goodSample.getAbsolutePath()),
          FileComparationUtil.readFile(convertedFile.getAbsolutePath()));
		} finally {
		  FileComparationUtil.deleteRecursivelly(outputFolder);    
		}
	}
}