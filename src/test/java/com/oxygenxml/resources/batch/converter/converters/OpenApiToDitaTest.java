package com.oxygenxml.resources.batch.converter.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.oxygenxml.batch.converter.core.ConverterTypes;
import com.oxygenxml.batch.converter.core.transformer.TransformerFactoryCreator;
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

/**
 * Test OpenAPI conversion
 * 
 * @author cosmin_duna
 *
 */
public class OpenApiToDitaTest {

  /**
   * The output directory of the conversion
   */
  private File outputDir;

  /**
   * Setup
   * @throws Exception
   */
  @Before 
  public void before() throws Exception {
    outputDir = null;
  }

  /**
   * TearDown
   * @throws Exception
   */
  @After
  public void after() throws Exception {
    if(outputDir != null) {
      FileSystemUtil.deleteRecursivelly(outputDir);
    }
  }
  
  /**
   * <p><b>Description:</b> Test that external OpenAPI referenced document are not converted twice when they are also set as input files.</p>
   * <p><b>Bug ID:</b> EXM-50253</p>
   *
   * @author cosmin_duna
   *
   * @throws Exception If it fails
   */
  @Test
  public void testReferencedOpenApiAreNotConvertedTwice() throws Exception {
    outputDir = new File("test-sample/EXM-50253/output");

    TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();
    ProblemReporter problemReporter = new ProblemReporterTestImpl();

    BatchConverter converter = new BatchConverterImpl(problemReporter, new StatusReporterImpl(), new ConverterStatusReporterTestImpl(),
        new ConvertorWorkerInteractorTestImpl() , transformerCreator);

    final List<File> inputFiles = new ArrayList<File>();
    inputFiles.add(new File("test-sample/EXM-50253/externalReference.json"));
    inputFiles.add(new File("test-sample/EXM-50253/definitions.json"));

    converter.convertFiles(ConverterTypes.OPENAPI_TO_DITA, new UserInputsProvider() {
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
        return 0;
      }
    });
    
    File[] outputFiles = outputDir.listFiles();
    assertEquals("Only 2 files should be created in the output folder."
        + " The 'definitions.json' shouldn't be converted twice. Files: " + Arrays.asList(outputFiles),
        2, outputFiles.length);
    assertTrue(Arrays.toString(outputFiles).contains("definitions.dita"));
    assertTrue(Arrays.toString(outputFiles).contains("externalReference.dita"));
    
    File outputFile = new File(outputDir, "externalReference.dita");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE dita\n" + 
        "  PUBLIC \"-//OASIS//DTD DITA Composite//EN\" \"ditabase.dtd\">\n" + 
        "<dita>\n" + 
        "   <topic id=\"introTopic\" outputclass=\"openapi-introduction\">\n" + 
        "      <title outputclass=\"openapi-title\">Swagger Petstore - OpenAPI 3.0</title>\n" + 
        "      <body>\n" + 
        "         <p>\n" + 
        "            <ph>Version:</ph>\n" + 
        "            <b outputclass=\"openapi-version\">1.0.11</b>\n" + 
        "         </p>\n" + 
        "      </body>\n" + 
        "   </topic>\n" + 
        "   <topic outputclass=\"openapi-tag\" id=\"defaultTag\">\n" + 
        "      <title outputclass=\"openapi-tag-title\">API methods: default</title>\n" + 
        "      <body/>\n" + 
        "      <topic id=\"get_pets\" outputclass=\"openapi-operation\">\n" + 
        "         <title outputclass=\"openapi-operation-title openapi-method-get\">\n" + 
        "            <ph id=\"get_pets-operationName\">\n" + 
        "               <ph outputclass=\"openapi-operation-method-get\">GET</ph>\n" + 
        "               <ph outputclass=\"openapi-operation-path\">/pets</ph>\n" + 
        "            </ph>\n" + 
        "         </title>\n" + 
        "         <body>\n" + 
        "            <bodydiv outputclass=\"openapi-operation-description\" id=\"get_pets-description\">\n" + 
        "               <p>Returns all pets from the system that the user has access to</p>\n" + 
        "            </bodydiv>\n" + 
        "            <section outputclass=\"openapi-responses\">\n" + 
        "               <title>Responses</title>\n" + 
        "               <dl outputclass=\"openapi-responses-list\">\n" + 
        "                  <dlentry outputclass=\"openapi-response\">\n" + 
        "                     <dt outputclass=\"openapi-response-key\">200</dt>\n" + 
        "                     <dd>\n" + 
        "                        <div outputclass=\"openapi-response-description\">\n" + 
        "                           <p>A list of pets.</p>\n" + 
        "                        </div>\n" + 
        "                        <dl outputclass=\"openapi-response-properties\">\n" + 
        "                           <dlentry outputclass=\"openapi-response-content\">\n" + 
        "                              <dt>Media types</dt>\n" + 
        "                              <dd outputclass=\"openapi-mediaTypes-data\">\n" + 
        "                                 <dl outputclass=\"openapi-mediaTypes\">\n" + 
        "                                    <dlentry outputclass=\"openapi-mediaType\">\n" + 
        "                                       <dt outputclass=\"openapi-mediaType-key\">application/json</dt>\n" + 
        "                                       <dd>\n" + 
        "                                          <dl outputclass=\"openapi-mediaType-fields-list\">\n" + 
        "                                             <dlentry outputclass=\"openapi-mediaType-schema\">\n" + 
        "                                                <dt>Schema</dt>\n" + 
        "                                                <dd>\n" + 
        "                                                   <dl outputclass=\"openapi-schema-values\">\n" + 
        "                                                      <dlentry>\n" + 
        "                                                         <dt>type</dt>\n" + 
        "                                                         <dd>array</dd>\n" + 
        "                                                      </dlentry>\n" + 
        "                                                      <dlentry>\n" + 
        "                                                         <dt>items</dt>\n" + 
        "                                                         <dd>\n" + 
        "                                                            <dl outputclass=\"openapi-schema-values\">\n" + 
        "                                                               <dlentry>\n" + 
        "                                                                  <dt>$ref</dt>\n" + 
        "                                                                  <dd>\n" + 
        "                                                                     <xref outputclass=\"openapi-reference\" href=\"definitions.dita#schemas/pet\"/>\n" + 
        "                                                                  </dd>\n" + 
        "                                                               </dlentry>\n" + 
        "                                                            </dl>\n" + 
        "                                                         </dd>\n" + 
        "                                                      </dlentry>\n" + 
        "                                                   </dl>\n" + 
        "                                                </dd>\n" + 
        "                                             </dlentry>\n" + 
        "                                          </dl>\n" + 
        "                                       </dd>\n" + 
        "                                    </dlentry>\n" + 
        "                                 </dl>\n" + 
        "                              </dd>\n" + 
        "                           </dlentry>\n" + 
        "                        </dl>\n" + 
        "                     </dd>\n" + 
        "                  </dlentry>\n" + 
        "                  <dlentry outputclass=\"openapi-response\">\n" + 
        "                     <dt outputclass=\"openapi-response-key\">400</dt>\n" + 
        "                     <dd>\n" + 
        "                        <xref outputclass=\"openapi-reference\"\n" + 
        "                             href=\"definitions2.dita#responses/error400\"/>\n" + 
        "                     </dd>\n" + 
        "                  </dlentry>\n" + 
        "               </dl>\n" + 
        "            </section>\n" + 
        "         </body>\n" + 
        "      </topic>\n" + 
        "   </topic>\n" + 
        "</dita>", IOUtil.readFile(outputFile));
  }
      
}
