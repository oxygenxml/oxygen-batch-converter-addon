package com.oxygenxml.resources.batch.converter.converters;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.extensions.FileExtensionType;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;
import com.oxygenxml.batch.converter.core.utils.ConverterFileUtils;
import com.oxygenxml.batch.converter.core.word.styles.WordStyleMapLoader;
import com.oxygenxml.resources.batch.converter.BatchConverter;
import com.oxygenxml.resources.batch.converter.BatchConverterImpl;
import com.oxygenxml.resources.batch.converter.UserInputsProvider;
import com.oxygenxml.resources.batch.converter.persister.OptionTags;
import com.oxygenxml.resources.batch.converter.reporter.ProblemReporter;
import com.oxygenxml.resources.batch.converter.translator.Tags;

import junit.framework.TestCase;
import ro.sync.basic.io.IOUtil;
import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.results.ResultsManager.ResultType;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.PrettyPrintException;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;
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
public class WordToXHtmlConverter2Test extends TestCase{

  @Override
  protected void tearDown() throws Exception {
    WordStyleMapLoader.imposeStyleMapURL(null);
    super.tearDown();
  }

  /**
   * <p><b>Description:</b> Test conversion from a docx file to XHTML using a word styles configuration set in options</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testConversionFromDocxUsingConfigFromOptions() throws Exception {
    setStylesMapOption(
            "p[style-name='Title'] => h1\n" + 
            "p[style-name='Subtitle'] => p.subtitle\n" + 
            "p[style-name='Heading 1'] => h2:fresh\n"+
            "p[style-name='Heading 2'] => h2:fresh\n"+
            "p[style-name='Heading 6'] => h2:fresh\n"+
            "r[style-name='Strong'] => b\n"+
            "b => b\n"+
            "u => p.underline\n"+
            "p[style-name='Document Title'] => h1.document:fresh\n" + 
            "p[style-name='Document Subtitle'] => h2.document:fresh\n"+
            "p:unordered-list(1) => ul.mylist > li:fresh > p"
        );
    File inputFile  = new File("test-sample/EXM-45677/inputCustomStyles.docx");   
    final File outputFolder  = new File(inputFile.getParentFile(), "output");

    try {
      TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
      ProblemReporter problemReporter = new ProblemReporterTestImpl();

      BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
          new ConvertorWorkerInteractorTestImpl() , transformerCreator);

      final List<File> inputFiles = new ArrayList<File>();
      inputFiles.add(inputFile);

      File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);

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

      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE html\n" + 
          "  SYSTEM \"about:legacy-compat\">\n" + 
          "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
          "<head><title/></head>\n" + 
          "<body><h1>Title styleTitle style2</h1><p class=\"subtitle\">Subtitle</p><h2>Heading 1</h2>"
          + "<p>Some <p class=\"underline\">underlined text</p></p><h2>Heading 2</h2><p><b>Strong text</b></p>"
          + "<h2>Heading 6</h2><ul class=\"mylist\"><li><p>Li1</p></li><li><p>Li2</p></li></ul>"
          + "<h1 class=\"document\">Document Title (custom)</h1><h2 class=\"document\">Document Subtitle(custom)</h2>\n" + 
          "</body>\n" + 
          "</html>",
          FileUtils.readFileToString(fileToRead));
    } finally {
      PluginWorkspaceProvider.setPluginWorkspace(null);
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }

  /**
   * Mock the pluginWorkspace and set the given styles map in options.
   * 
   * @param toSet The word style map to set.
   * @throws PrettyPrintException 
   */
  private void setStylesMapOption(String toSet) throws PrettyPrintException {
    StandalonePluginWorkspace pluginWSMock = Mockito.mock(StandalonePluginWorkspace.class);
    PluginWorkspaceProvider.setPluginWorkspace(pluginWSMock);

    WSOptionsStorage wsOptions = Mockito.mock(WSOptionsStorage.class);
    Mockito.when(wsOptions.getOption(Mockito.anyString(), Mockito.anyString())).then(
        new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            String toRet = null;
            String key = invocation.getArgumentAt(0, String.class);
            if(key.equals(OptionTags.WORD_STYLES_MAP_CONFIG)) {
              toRet = toSet;
            }
            return toRet;
          }
        });
    Mockito.when(pluginWSMock.getOptionsStorage()).thenReturn(wsOptions);

    XMLUtilAccess xmlUtilAccess = Mockito.mock(XMLUtilAccess.class);
    Mockito.when(xmlUtilAccess.prettyPrint(Mockito.any(Reader.class), Mockito.anyString())).then(
        new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            Reader reader = invocation.getArgumentAt(0, Reader.class);
            return IOUtil.readSimple(reader);
          }
        });
    Mockito.when(pluginWSMock.getXMLUtilAccess()).thenReturn(xmlUtilAccess);
  }

  /**
   * <p><b>Description:</b> Test warnings are presented when unrecognized styles are found in conversion process.</p>
   * <p><b>Bug ID:</b> EXM-48983</p>
   * @author cosmin_duna
   *
   * @throws Exception
   */
  @Test
  public void testPresentUnrecognizedStylesWarnings() throws Exception {
    File inputFile  = new File("test-sample/EXM-48983/sample.docx");   
    final File outputFolder  = new File(inputFile.getParentFile(), "output");
  
    try {
      StandalonePluginWorkspace pluginWSMock = Mockito.mock(StandalonePluginWorkspace.class);
      PluginWorkspaceProvider.setPluginWorkspace(pluginWSMock);
      
      PluginResourceBundle pluginResourceBundle = Mockito.mock(PluginResourceBundle.class); 
      Mockito.when(pluginResourceBundle.getMessage(Mockito.anyString())).thenAnswer(new Answer<String>() {

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
          if (invocation.getArgumentAt(0, String.class).equals(Tags.UNRECOGNIZE_STYLES_FOR_WORD_ELEMENT)) {
            return  "Unrecognized \"{0}\" style ID for \"{1}\" Word element.";
          } else if (invocation.getArgumentAt(0, String.class).equals(Tags.CONFIG_WORD_MAPPING_IN_PREFERENCES_PAGE)) {
            return "You can configure the mapping between styles and elements in the Batch Documents Converter preferences page.";
          } else {
            return "";
          }
        }
      });
      Mockito.when(pluginWSMock.getResourceBundle()).thenReturn(pluginResourceBundle);
      
      XMLUtilAccess xmlUtilAccess = Mockito.mock(XMLUtilAccess.class);
      Mockito.when(xmlUtilAccess.prettyPrint(Mockito.any(Reader.class), Mockito.anyString())).then(
          new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
              Reader reader = invocation.getArgumentAt(0, Reader.class);
              return IOUtil.readSimple(reader);
            }
          });
      Mockito.when(pluginWSMock.getXMLUtilAccess()).thenReturn(xmlUtilAccess);
      
      WSOptionsStorage wsOptions = Mockito.mock(WSOptionsStorage.class);
      Mockito.when(wsOptions.getOption(Mockito.anyString(), Mockito.anyString())).thenReturn("");
      Mockito.when(pluginWSMock.getOptionsStorage()).thenReturn(wsOptions);

      ResultsManager resultsManager = Mockito.mock(ResultsManager.class);
      Mockito.when(pluginWSMock.getResultsManager()).thenReturn(resultsManager);
      ArgumentCaptor<DocumentPositionedInfo> dpiCaptor = ArgumentCaptor.forClass(DocumentPositionedInfo.class);

      
      TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
      ProblemReporter problemReporter = new ProblemReporterTestImpl();
  
      BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
          new ConvertorWorkerInteractorTestImpl() , transformerCreator);
  
      final List<File> inputFiles = new ArrayList<File>();
      inputFiles.add(inputFile);
  
      File fileToRead = ConverterFileUtils.getOutputFile(inputFile, FileExtensionType.XHTML_OUTPUT_EXTENSION , outputFolder);
  
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
  
      assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<!DOCTYPE html\n" + 
          "  SYSTEM \"about:legacy-compat\">\n" + 
          "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + 
          "<head><title/></head>\n" + 
          "<body><p>Title</p><p>Some content</p><p>Heading1</p><p>Some text with <strong>bold</strong> <em>italic</em> <u>underline</u>.</p><ul><li>Li1</li><li>li2</li></ul>\n" + 
          "</body>\n" + 
          "</html>",
          FileUtils.readFileToString(fileToRead));
      
      Mockito.verify(resultsManager, Mockito.times(2)).addResult(Mockito.any(String.class), dpiCaptor.capture(), Mockito.any(ResultType.class), Mockito.anyBoolean(), Mockito.anyBoolean());

      List<DocumentPositionedInfo> capturedPeople = dpiCaptor.getAllValues();
      assertEquals("Unrecognized \"MyTitle\" style ID for \"p\" Word element."
          + " You can configure the mapping between styles and elements in the Batch Documents Converter preferences page.",
          capturedPeople.get(0).getMessage());
      assertTrue(capturedPeople.get(0).getSystemID().contains("sample.docx"));
      assertEquals("Unrecognized \"MyHeading\" style ID for \"p\" Word element."
          + " You can configure the mapping between styles and elements in the Batch Documents Converter preferences page.",
          capturedPeople.get(1).getMessage());
      assertTrue(capturedPeople.get(1).getSystemID().contains("sample.docx"));
    } finally {
      PluginWorkspaceProvider.setPluginWorkspace(null);
      FileComparationUtil.deleteRecursivelly(outputFolder);
    }
  }
}
