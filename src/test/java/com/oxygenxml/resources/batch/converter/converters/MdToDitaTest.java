package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
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
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
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
			Files.delete(convertedFile.toPath());
			Files.delete(outputFolder.toPath());
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
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
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
			if(convertedFile.exists()) {
				Files.delete(convertedFile.toPath());
				Files.delete(outputFolder.toPath());
			}
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
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
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
      if(convertedFile.exists()) {
        Files.delete(convertedFile.toPath());
        Files.delete(outputFolder.toPath());
      }
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
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
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
      if(convertedFile.exists()) {
        Files.delete(convertedFile.toPath());
        Files.delete(outputFolder.toPath());
      }
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
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
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
      if(convertedFile.exists()) {
        Files.delete(convertedFile.toPath());
        Files.delete(outputFolder.toPath());
      }
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
    File inputFile  = new File("test-sample/EXM-45707/testShortdesc.md");
    File expectedOutputFile  = new File("test-sample/EXM-45707/testShortdescOutput.dita");
    final File outputFolder = new File(inputFile.getParentFile(), "output");
    
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporterTestImpl problemReporter = new ProblemReporterTestImpl();
    
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ProgressDialogInteractorTestImpl(),
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
      if(convertedFile.exists()) {
        Files.delete(convertedFile.toPath());
        Files.delete(outputFolder.toPath());
      }
    }
  }
}