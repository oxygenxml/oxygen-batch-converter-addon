package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * JUnit for WORD to XHTML conversion.
 *  
 * @author cosmin_duna
 *
 */
public class WordToXHtmlConverterTest extends TestCase{

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
		File expectedResultFile = new File("test-sample/wordTo/resultOfDocxToXHTML.xhtml");
		final File outputFolder  = new File(inputFile.getParentFile(), "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

	  final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(inputFile);
				
		File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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

			String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img");
			assertEquals(filterMathAttributes(expected),
					filterMathAttributes(FileComparationUtil.readFile(fileToRead.getAbsolutePath())));

			File mediaFolder = new File(outputFolder, "media");
			File[] images = mediaFolder.listFiles();
			assertEquals(1, images.length);
			
			String imageName = images[0].getName();
			assertEquals("img.tif", imageName);
			
		} finally {
			FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}

	/**
   * <p><b>Description:</b> Test conversion from a doc file to XHTML.</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
	@Test
	public void testConversionFromDoc() throws IOException {
		File inputFile  = new File("test-sample/wordTo/test.doc");		
		File expectedResultFile = new File("test-sample/wordTo/resultOfDocToXHTML.xhtml");
		final File outputFolder  = new File(inputFile.getParentFile(), "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);

		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(inputFile);
				
		File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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

			assertEquals(FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img"),
					FileComparationUtil.readFile(fileToRead.getAbsolutePath()));

			File mediaFolder = new File(outputFolder, "media");
			File[] images = mediaFolder.listFiles();
			assertEquals(2, images.length);
			assertTrue(Arrays.asList(images).toString().contains("img(1).png"));
	   	assertTrue(Arrays.asList(images).toString().contains("img.png"));
			
		} finally {
			FileComparationUtil.deleteRecursivelly(outputFolder);
		}
	}
	
	/**
	 * <p><b>Description:</b> Test conversion from a docx file with multiple equations to XHTML.</p>
	 *
	 * @author cosmin_duna
	 *
	 * @throws Exception
	 */
	@Test
	public void testConversionFromDocxWithMultipleEquation() throws IOException {
		File inputFile  = new File("test-sample/wordTo/multipleEquations.docx");		
		File expectedResultFile = new File("test-sample/wordTo/multipleEquations.xhtml");
		final File outputFolder  = new File(inputFile.getParentFile(), "output");
		
		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
		ProblemReporter problemReporter = new ProblemReporterTestImpl();
		
		BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
				new ConvertorWorkerInteractorTestImpl() , transformerCreator);
	
		final List<File> inputFiles = new ArrayList<File>();
		inputFiles.add(inputFile);
				
		File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
		
		try {
			converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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
	
			String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", File.separatorChar+"img");
			assertEquals(filterMathAttributes(expected),
					filterMathAttributes(FileComparationUtil.readFile(fileToRead.getAbsolutePath())));
	
			File mediaFolder = new File(outputFolder, "media");
			assertTrue(!mediaFolder.exists());
			
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
   * <p><b>Description:</b> Test conversion from a docx file to XHTML.
   * The docx contains embedded and linked images.
   * The embedded images should be saved on disk and referred with relative paths.
   * The linked images should be referred with absolute paths.</p>
   * <p><b>Bug ID:</b> EXM-45096</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testConversionFromDocxWithAbsoluteImages() throws IOException {
  	File inputFile  = new File("test-sample/EXM-45096/input.docx");		
  	File expectedResultFile = new File("test-sample/EXM-45096/expectedOutput.xhtml");
  	final File outputFolder  = new File(inputFile.getParentFile(), "output");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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
  
  		String expected = FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()).replace("\\img", "/img");
  		assertEquals(expected, FileComparationUtil.readFile(fileToRead.getAbsolutePath()));
  
  		File mediaFolder = new File(outputFolder, "media");
  		File[] images = mediaFolder.listFiles();
  		assertEquals("Only the embedded images should be saved in the media folder.",
  		    1, images.length);
  		
  	} finally {
  		FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  }

  /**
   * <p><b>Description:</b> Test conversion from a docx file to XHTML.
   * The docx contains "none" value for the "w:val" attributes.
   * For example: "<w:u w:val="none" />". We test it is correctly interpreted 
   * and the converted text in correctly underlined. 
   * "</p>
   * <p><b>Bug ID:</b> EXM-45460</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testConversionFromDocxWithNoneStyleValue() throws IOException {
  	File inputFile  = new File("test-sample/EXM-45460/input.docx");		
  	File expectedResultFile = new File("test-sample/EXM-45460/expectedOutput.xhtml");
  	final File outputFolder  = new File(inputFile.getParentFile(), "output");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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
  		assertEquals(FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()),
  		    FileComparationUtil.readFile(fileToRead.getAbsolutePath()));
  	} finally {
  		FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  }

  /**
   * <p><b>Description:</b> Test conversion from a docx file to XHTML.
   * A h1 should be created for an element with the "Document Title" style.
   * <p><b>Bug ID:</b> EXM-44654</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testConversionFromDocx_EXM_44654() throws IOException {
  	File inputFile  = new File("test-sample/EXM-44654/input.docx");		
  	File expectedResultFile = new File("test-sample/EXM-44654/expectedOutput.xhtml");
  	final File outputFolder  = new File(inputFile.getParentFile(), "output");
  	File customStyleMap = new File("test-sample/EXM-44654/wordStyleMap.xml");
  	WordStyleMapLoader.imposeStyleMapFile(customStyleMap);
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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
  		assertEquals(FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()),
  		    FileComparationUtil.readFile(fileToRead.getAbsolutePath()));
  	} finally {
  		FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  }

  /**
   * <p><b>Description:</b> Test conversion from a docx file to XHTML 
   * takes account of default stylemap.
   * <p><b>Bug ID:</b> EXM-44654</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testConversionFromDocx_defaultStyleMap() throws IOException {
  	File inputFile  = new File("test-sample/EXM-45677/input.docx");		
  	File expectedResultFile = new File("test-sample/EXM-45677/expectedOutput.xhtml");
  	final File outputFolder  = new File(inputFile.getParentFile(), "output");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  	
  	try {
  		converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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
  		assertEquals(FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()),
  		    FileComparationUtil.readFile(fileToRead.getAbsolutePath()));
  	} finally {
  	  WordStyleMapLoader.imposeStyleMapFile(null);
  		FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  }

  /**
   * <p><b>Description:</b> Test conversion from a docx file to XHTML 
   * takes account of a custom stylemap.
   * <p><b>Bug ID:</b> EXM-44654</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testConversionFromDocx_customStyleMap() throws IOException {
  	File inputFile  = new File("test-sample/EXM-45677/inputCustomStyles.docx");		
  	File expectedResultFile = new File("test-sample/EXM-45677/expectedCustomOutput.xhtml");
  	final File outputFolder  = new File(inputFile.getParentFile(), "output");
  	File customStyleMap = new File("test-sample/EXM-45677/wordStyleMap.xml");
  	
  	TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
  	ProblemReporter problemReporter = new ProblemReporterTestImpl();
  	
  	BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
  			new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
  	final List<File> inputFiles = new ArrayList<File>();
  	inputFiles.add(inputFile);
  			
  	File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  	
  	WordStyleMapLoader.imposeStyleMapFile(customStyleMap);
  	try {
  		converter.convertFiles(ConverterTypes.WORD_TO_XHTML, new UserInputsProvider() {
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
  		assertEquals(FileComparationUtil.readFile(expectedResultFile.getAbsolutePath()),
  		    FileComparationUtil.readFile(fileToRead.getAbsolutePath()));
  	} finally {
  	  WordStyleMapLoader.imposeStyleMapFile(null);
  		FileComparationUtil.deleteRecursivelly(outputFolder);
  	}
  }
}
