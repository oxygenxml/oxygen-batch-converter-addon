package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;

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
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;

import tests.utils.ConverterStatusReporterTestImpl;
import tests.utils.ConvertorWorkerInteractorTestImpl;
import tests.utils.FileComparationUtil;
import tests.utils.ProblemReporterTestImpl;
import tests.utils.StatusReporterImpl;
import tests.utils.TransformerFactoryCreatorImpl;

/**
 * JUnit test for the Markdown to DITA conversion.
 * 
 * @author Cosmin Duna
 */
public class MdToDitaTest {

  /**
   * Test Markdown to DITA conversion
   * @throws Exception
   */
	@Test
	public void testConversion() throws Exception {
		File sample  = new File("test-sample/markdownTest.md");		
		File goodSample = new File("test-sample/goodMdToDita.dita");
    final File outputFolder = new File(sample.getParentFile(), "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
		try {
			converter.convertFiles(ConverterTypes.MD_TO_DITA, 
			    new UserInputsProvider() {
            
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

		  String expected = FileComparationUtil.readFile(goodSample.getAbsolutePath()).toString();
      String actual = FileComparationUtil.readFile(convertedFile.getAbsolutePath()).toString();
      assertEquals(expected, actual);
		} finally {
		  FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}
	
	/**
	 * <p><b>Description:</b> Test conversion of MD to DITA Composite.</p>
	 * <p><b>Bug ID:</b> EXM-44491</p>
	 * 
	 * @author cosmin_duna
	 */
	@Test
	public void testMarkdownToComposite_EXM_44491() throws TransformerException, IOException {
		
		File sample  = new File("test-sample/EXM-44491/sample.md");
		File goodOutput  = new File("test-sample/EXM-44491/outputFile.dita");
		final File outputFolder = new File(sample.getParentFile(), "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(sample);
				
		File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.MD_TO_DITA, new UserInputsProvider() {
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
			
			assertEquals(FileComparationUtil.readFile(goodOutput.getAbsolutePath()),
					FileComparationUtil.readFile(convertedFile.getAbsolutePath()));
		} finally {
		  FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}
	
	 /**
   * <p><b>Description:</b> Test that conversion of MD to DITA is more relaxed and accepts files 
   * that starts with a high heading level or the level is increased with more than one unit.</p>
   * <p><b>Bug ID:</b> EXM-45707</p>
   * 
   * @author cosmin_duna
   */
  @Test
  public void testStartWithHighHeadingLevel() throws TransformerException, IOException {
    File inputFile  = new File("test-sample/EXM-45707/start_with_high_level.md");
    File expectedOutputFile  = new File("test-sample/EXM-45707/start_with_high_level.dita");
    final File outputFolder = new File(inputFile.getParentFile(), "output");
    
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
        
    File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
    
    try {
      converter.convertFiles(ConverterTypes.MD_TO_DITA, new UserInputsProvider() {
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
      
      assertEquals(FileComparationUtil.readFile(expectedOutputFile.getAbsolutePath()),
          FileComparationUtil.readFile(convertedFile.getAbsolutePath()));
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test that conversion of MD to DITA is more relaxed and accepts files 
   * that starts with a high heading level or the level is increased with more than one unit.</p>
   * <p><b>Bug ID:</b> EXM-45707</p>
   * 
   * @author cosmin_duna
   */
  @Test
  public void testStartWithHighHeadingLevel2() throws TransformerException, IOException {
    File inputFile  = new File("test-sample/EXM-45707/start_with_high_level2.md");
    File expectedOutputFile  = new File("test-sample/EXM-45707/start_with_high_level2.dita");
    final File outputFolder = new File(inputFile.getParentFile(), "output");
    
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
        
    File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
    
    try {
      converter.convertFiles(ConverterTypes.MD_TO_DITA, new UserInputsProvider() {
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
      
      assertEquals(FileComparationUtil.readFile(expectedOutputFile.getAbsolutePath()),
          FileComparationUtil.readFile(convertedFile.getAbsolutePath()));
    } finally {
//      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }
  
  /**
   * <p><b>Description:</b> Test that conversion of MD to DITA is more relaxed and accepts sections that don't have level higher than heading.</p>
   * <p><b>Bug ID:</b> EXM-45707</p>
   * 
   * @author cosmin_duna
   */
  @Test
  public void testLevelOfSection() throws TransformerException, IOException {
    File inputFile  = new File("test-sample/EXM-45707/start_with_high_level3.md");
    File expectedOutputFile  = new File("test-sample/EXM-45707/start_with_high_level3.dita");
    final File outputFolder = new File(inputFile.getParentFile(), "output");
    
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
        
    File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
    
    try {
      converter.convertFiles(ConverterTypes.MD_TO_DITA, new UserInputsProvider() {
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
      
      assertEquals(FileComparationUtil.readFile(expectedOutputFile.getAbsolutePath()),
          FileComparationUtil.readFile(convertedFile.getAbsolutePath()));
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }
  
  /**
   * <p><b>Description:</b> Test if first paragraph is added as shortdesc.</p>
   * <p><b>Bug ID:</b> EXM-46055</p>
   * 
   * @author mircea_badoi
   */
  @Test
  public void testFirstParagraphAsShortdesc() throws TransformerException, IOException {
    File inputFile  = new File("test-sample/EXM-46055/testShortdesc.md");
    File expectedOutputFile  = new File("test-sample/EXM-46055/testShortdescOutput.dita");
    final File outputFolder = new File(inputFile.getParentFile(), "output");
    
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
        
    File convertedFile = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
    
    try {
      converter.convertFiles(ConverterTypes.MD_TO_DITA, new UserInputsProvider() {
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
          if(additionalOptionId.equals(OptionTags.CREATE_SHORT_DESCRIPTION)){
            return true;
          };
          return false;
        }
      });
      
      assertEquals(FileComparationUtil.readFile(expectedOutputFile.getAbsolutePath()),
          FileComparationUtil.readFile(convertedFile.getAbsolutePath()));
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test extract sections in topics.</p>
   * <p><b>Bug ID:</b> EXM-48113</p>
   * 
   * @author cosmin_duna
   */
  @Test
  public void testExtractSectionsInTopics() throws Exception {
    File sample  = new File("test-sample/markdownTest.md");		
    final File outputFolder = new File(sample.getParentFile(), "output");

    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();

    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(sample);

    File convertedFile = ConverterFileUtils.getOutputFile(sample, FileExtensionType.DITA_MAP_OUTPUT_EXTENSION , outputFolder);
    try {
      converter.convertFiles(ConverterTypes.MD_TO_DITA, 
          new UserInputsProvider() {

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
          Boolean toRet = null;
          if(OptionTags.CREATE_DITA_MAP_FROM_MD.equals(additionalOptionId)) {
            toRet = Boolean.TRUE;
          }
          return toRet;
        }
      });

      String actual = FileComparationUtil.readFile(convertedFile.getAbsolutePath());
      File expectedMap  = new File("test-sample/EXM-48113/expectedMap.ditamap");   
      String expected = FileComparationUtil.readFile(expectedMap.getAbsolutePath());
      assertEquals(expected, actual);

      File firstTopic = new File(outputFolder, "markdownTest_oxygen_batch_converter_add-on_for_oxygen_xml_editor.dita");
      actual = FileComparationUtil.readFile(firstTopic.getAbsolutePath());
      File expectedTopic = new File("test-sample/EXM-48113/topic3.dita");   
      expected = FileComparationUtil.readFile(expectedTopic.getAbsolutePath());
      assertEquals(expected, actual);

      File secondTopic = new File(outputFolder, "markdownTest_installation.dita");
      actual = FileComparationUtil.readFile(secondTopic.getAbsolutePath());
      expectedTopic = new File("test-sample/EXM-48113/topic2.dita");   
      expected = FileComparationUtil.readFile(expectedTopic.getAbsolutePath());
      assertEquals(expected, actual);

      File thirdTopic = new File(outputFolder, "markdownTest_converting_a_document.dita");
      actual = FileComparationUtil.readFile(thirdTopic.getAbsolutePath());
      expectedTopic = new File("test-sample/EXM-48113/topic1.dita");   
      expected = FileComparationUtil.readFile(expectedTopic.getAbsolutePath());
      assertEquals(expected, actual);
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }
}