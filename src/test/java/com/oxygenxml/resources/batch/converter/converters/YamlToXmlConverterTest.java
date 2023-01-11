package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.extensions.FileExtensionType;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

import ro.sync.basic.io.FileSystemUtil;
import ro.sync.basic.io.IOUtil;
import tests.utils.ConverterStatusReporterTestImpl;
import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;

public class YamlToXmlConverterTest {

  /**
   * <p><b>Description:</b> Test YAML to XML conversion.</p>
   * <p><b>Bug ID:</b> EXM-47435</p>
   *
   * @author andrei_pomacu
   *
   * @throws Exception
   */
  @Test
  public void testYamlToXmlConverter() throws Exception {
    File inputFile  = new File("test-sample/EXM-47435/personal-schema.yaml"); 
    File outputDir = new File("test-sample/EXM-47435/output");
      
      TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
      ProblemReporter problemReporter = new ProblemReporterTestImpl();
      
      BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
          new ConvertorWorkerInteractorTestImpl() , transformerCreator);

      final List<File> inputFiles = new ArrayList<File>();
      inputFiles.add(inputFile);
      File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XML_OUTPUT_EXTENSION , outputDir);
      
      try {
        converter.convertFiles(ConverterTypes.YAML_TO_XML, new UserInputsProvider() {
          @Override
          public boolean mustOpenConvertedFiles() {
            return false;
          }
          @Override
          public File getOutputFolder() {
            return outputDir;
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
      
     assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><JSON>\n"
         + "  <_X24_schema>http://json-schema.org/draft-07/schema#</_X24_schema>\n"
         + "  <title>Personal schema.</title>\n"
         + "  <type>object</type>\n"
         + "  <properties>\n"
         + "    <personnel>\n"
         + "      <title>The 'personnel' property</title>\n"
         + "      <description>Defines the personnel as a collection of person entries.</description>\n"
         + "      <type>object</type>\n"
         + "      <properties>\n"
         + "        <person>\n"
         + "          <_X24_ref>#/definitions/personType</_X24_ref>\n"
         + "        </person>\n"
         + "      </properties>\n"
         + "      <additionalProperties>false</additionalProperties>\n"
         + "    </personnel>\n"
         + "  </properties>\n"
         + "  <definitions>\n"
         + "    <personType>\n"
         + "      <title>The 'person' property</title>\n"
         + "      <description>Specifies information about a person.</description>\n"
         + "      <type>array</type>\n"
         + "      <uniqueItems>true</uniqueItems>\n"
         + "    </personType>\n"
         + "  </definitions>\n"
         + "</JSON>",
             IOUtil.readFile(convertedFile));
      
    } finally {
      FileSystemUtil.deleteRecursivelly(outputDir);
    }
  }
  
  /**
   * <p><b>Description:</b> Test XML to YAML conversion.</p>
   * <p><b>Bug ID:</b> EXM-47435</p>
   *
   * @author andrei_pomacu
   *
   * @throws Exception
   */
  @Test
  public void testXmlToYamlConverter() throws Exception {
    File inputFile  = new File("test-sample/EXM-47435/personal.xml"); 
    File outputDir = new File("test-sample/EXM-47435/output");

    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();

    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
    File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.YAML_OUTPUT_EXTENSION , outputDir);

    try {
      converter.convertFiles(ConverterTypes.XML_TO_YAML, new UserInputsProvider() {
        @Override
        public boolean mustOpenConvertedFiles() {
          return false;
        }
        @Override
        public File getOutputFolder() {
          return outputDir;
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

      assertEquals("---\n"
          + "personnel:\n"
          + "  person:\n"
          + "  - id: harris.anderson\n"
          + "    photo: personal-images/harris.anderson.jpg\n"
          + "    name:\n"
          + "      given: Harris\n"
          + "      family: Anderson\n"
          + "    email: harris.anderson@example.com\n"
          + "    link:\n"
          + "      subordinates: robert.taylor helen.jackson michelle.taylor jason.chen harris.anderson brian.carter\n"
          + "    url:\n"
          + "      href: http://www.example.com/na/harris-anderson.html\n"
          + "  - id: robert.taylor\n"
          + "    photo: personal-images/robert.taylor.jpg\n"
          + "    name:\n"
          + "      given: Robert\n"
          + "      family: Taylor\n"
          + "    email: robert.taylor@example.com\n"
          + "    link:\n"
          + "      manager: harris.anderson\n"
          + "    url:\n"
          + "      href: http://www.example.com/na/robert-taylor.html\n"
          + "  - id: helen.jackson\n"
          + "    photo: personal-images/helen.jackson.jpg\n"
          + "    name:\n"
          + "      given: Helen\n"
          + "      family: Jackson\n"
          + "    email: hellen.jackson@example.com\n"
          + "    link:\n"
          + "      manager: harris.anderson\n"
          + "    url:\n"
          + "      href: http://www.example.com/na/hellen-jackson.html\n"
          + "  - id: michelle.taylor\n"
          + "    photo: personal-images/michelle.taylor.jpg\n"
          + "    name:\n"
          + "      given: Michelle\n"
          + "      family: Taylor\n"
          + "    email: michelle.taylor@example.com\n"
          + "    link:\n"
          + "      manager: harris.anderson\n"
          + "    url:\n"
          + "      href: http://www.example.com/na/michelle-taylor.html\n"
          + "  - id: jason.chen\n"
          + "    photo: personal-images/jason.chen.jpg\n"
          + "    name:\n"
          + "      given: Jason\n"
          + "      family: Chen\n"
          + "    email: jason.chen@example.com\n"
          + "    link:\n"
          + "      manager: harris.anderson\n"
          + "    url:\n"
          + "      href: http://www.example.com/na/jason-chen.html\n"
          + "  - id: brian.carter\n"
          + "    photo: personal-images/brian.carter.jpg\n"
          + "    name:\n"
          + "      given: Brian\n"
          + "      family: Carter\n"
          + "    email: brian.carter@example.com\n"
          + "    link:\n"
          + "      manager: harris.anderson\n"
          + "    url:\n"
          + "      href: http://www.example.com/na/brian-carter.html",
          IOUtil.readFile(convertedFile));

    } finally {
      FileSystemUtil.deleteRecursivelly(outputDir);
    }
  }

}
