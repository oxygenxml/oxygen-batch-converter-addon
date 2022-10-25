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
import com.oxygenxml.batch.converter.core.word.styles.WordStyleMapLoader;
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
 * Test the WORD to Dita conversion.
 * @author cosmin_duna
 *
 */
public class WordToDitaConverterTest extends TestCase{

  @Override
  protected void tearDown() throws Exception {
    WordStyleMapLoader.imposeStyleMapURL(null);
    super.tearDown();
  }

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
    File expectedResultFile = new File("test-sample/wordTo/resultOfDocxToDita.dita");
    final File outputFolder  = new File(inputFile.getParentFile(), "output");

    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();

    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);

    File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);

    try {
      converter.convertFiles(ConverterTypes.WORD_TO_DITA, new UserInputsProvider() {
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
          return 5;
        }
      });

      String actualResult = FileComparationUtil.readFile(fileToRead.getAbsolutePath());
      String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\image", "/image");
      assertEquals(filterMathAttributes(expected),
          filterMathAttributes(actualResult));

      File mediaFolder = new File(outputFolder, "media");
      File[] images = mediaFolder.listFiles();
      assertEquals(1, images.length);

      String imageName = images[0].getName();
      assertEquals("image1.tif", imageName);

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

  /**
   * <p><b>Description:</b> Test index entries are processed properly.</p>
   * <p><b>Bug ID:</b> EXM-47545</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testProcessIndexTerms() throws IOException {
    File inputFile  = new File("test-sample/EXM-47545/indexTerms.docx");		
    File expectedResultFile = new File("test-sample/EXM-47545/resultIndexTerms.dita");
    final File outputFolder  = new File(inputFile.getParentFile(), "output");

    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();

    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);

    File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);

    try {
      converter.convertFiles(ConverterTypes.WORD_TO_DITA, new UserInputsProvider() {
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
          return 0;
        }
      });

      String actualResult = FileComparationUtil.readFile(fileToRead.getAbsolutePath());
      String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img");
      assertEquals(expected, actualResult);
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test index entries with cross references are processed properly.</p>
   * <p><b>Bug ID:</b> EXM-47545</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testProcessIndexTermsWithCrossRefs() throws Exception {
    File inputFile  = new File("test-sample/EXM-47545/indexTerms2.docx");		
    File expectedResultFile = new File("test-sample/EXM-47545/resultIndexTerms2.dita");
    final File outputFolder  = new File(inputFile.getParentFile(), "output");

    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();

    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);

    File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);

    try {
      converter.convertFiles(ConverterTypes.WORD_TO_DITA, new UserInputsProvider() {
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
          return 0;
        }
      });

      String actualResult = FileComparationUtil.readFile(fileToRead.getAbsolutePath());
      String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img");
      assertEquals(expected, actualResult);
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test that index section from Word is ignored.</p>
   * <p><b>Bug ID:</b> EXM-47545</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testIndexIsIgnored() throws Exception {
    File inputFile  = new File("test-sample/EXM-47545/index.docx");		
    File expectedResultFile = new File("test-sample/EXM-47545/index.dita");
    final File outputFolder  = new File(inputFile.getParentFile(), "output");
  
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();
  
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
  
    File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
  
    try {
      converter.convertFiles(ConverterTypes.WORD_TO_DITA, new UserInputsProvider() {
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
          return 0;
        }
      });
  
      String actualResult = FileComparationUtil.readFile(fileToRead.getAbsolutePath());
      String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img");
      assertEquals(expected, actualResult);
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }

  /**
   * <p><b>Description:</b> Test that footnotes from Word are properly converted in DITA.</p>
   * <p><b>Bug ID:</b> EXM-47800</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testProcessFootnotes() throws Exception {
    File inputFile  = new File("test-sample/EXM-47800/sampleFootnote.docx");		
    File expectedResultFile = new File("test-sample/EXM-47800/sampleFootnote.dita");
    final File outputFolder  = new File(inputFile.getParentFile(), "output");
  
    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();
  
    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(inputFile);
  
    File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.DITA_OUTPUT_EXTENSION , outputFolder);
  
    try {
      converter.convertFiles(ConverterTypes.WORD_TO_DITA, new UserInputsProvider() {
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
          return 0;
        }
      });
  
      String actualResult = FileComparationUtil.readFile(fileToRead.getAbsolutePath());
      String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img");
      assertEquals(expected, actualResult);
    } finally {
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }
}
