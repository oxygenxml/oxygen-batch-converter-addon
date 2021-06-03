package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.extensions.FileExtensionType;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.transformer.TransformerFactoryCreator;
import com.oxygenxml.resources.batch.converter.utils.ConverterFileUtils;
import com.oxygenxml.resources.batch.converter.word.styles.WordStyleMapLoader;

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
  protected void setUp() throws Exception {
    super.setUp();
    WordStyleMapLoader.imposeStyleMapURL(new File("config/wordStyleMap.xml"));
  }
  
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
      });

			String actualResult = FileUtils.readFileToString(fileToRead);
			String expected = FileUtils.readFileToString(expectedResultFile).replace("\\img", "/img");
			assertEquals(filterMathAttributes(expected),
          filterMathAttributes(actualResult));

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
}
