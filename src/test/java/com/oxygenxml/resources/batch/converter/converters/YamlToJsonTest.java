package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;

import tests.utils.ConverterStatusReporterTestImpl;
import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;

/**
 *  Test for YAML to JSON conversion.
 */
public class YamlToJsonTest {
  @Test
  public void test() throws TransformerException, IOException {
  
    File sample  = new File("test-sample/yamlTest.yaml");   
    File goodSample = new File("test-sample/jsonTest.json");
    final File outputFolder = new File(sample.getParentFile(), "out");
    
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(sample);
        
    File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.JSON_OUTPUT_EXTENSION , outputFolder);
    
    try {
      converter.convertFiles(ConverterTypes.YAML_TO_JSON, new UserInputsProvider() {
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
      });
      assertTrue(FileComparationUtil.compareLineToLine(goodSample, convertedFile));

    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);    
    }
  }
}
