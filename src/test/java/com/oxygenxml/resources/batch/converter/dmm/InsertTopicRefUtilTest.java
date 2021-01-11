package com.oxygenxml.resources.batch.converter.dmm;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.css.sac.InputSource;

import junit.framework.TestCase;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacade;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacadeFactory;
import ro.sync.ecss.css.csstopdf.facade.CatalogResolverFacade;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * Test  {@link InsertTopicRefUtil}
 */
@RunWith(PowerMockRunner.class)
public class InsertTopicRefUtilTest extends TestCase{

  /**
   * The relative path to catalogs.
   */
  private static final String CATALOG = "config/catalogs/catalog.xml";
  /**
   * The author document controller.
   */
  private AuthorDocumentController controller;

  /**
   * Set up.
   */
  @Override
  protected void setUp() throws Exception {
    PluginWorkspace pluginWorkspace = Mockito.mock(PluginWorkspace.class);
    UtilAccess utilAccess = Mockito.mock(UtilAccess.class);
    Mockito.when(utilAccess.convertFileToURL(Mockito.any(File.class))).then(
        new Answer<URL>() {
          @Override
          public URL answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            File file = (File) args[0];
            return file.toURI().toURL();
          }
        });
    
    Mockito.when(utilAccess.makeRelative(Mockito.any(URL.class), Mockito.any(URL.class))).then(
        new Answer<String>() {
          @Override
          public String answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            return new File(((URL) args[1]).toURI()).getName();
          }
        });

    Mockito.when(pluginWorkspace.getUtilAccess()).thenReturn(utilAccess);
    PowerMockito.mockStatic(PluginWorkspaceProvider.class);
    PowerMockito.when(PluginWorkspaceProvider.getPluginWorkspace()).thenReturn(pluginWorkspace);
  }

  @Test
  @PrepareForTest({ PluginWorkspaceProvider.class })
  public void testInsertTopicReferencesInDMM() throws Exception {
    WSEditor editor = createEditor(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">\n" + 
        "<map>\n" + 
        "    <topicref href=\"topic.dita\"></topicref>\n" + 
        "</map>\n" + 
        "");
    
    WSDITAMapEditorPage wsEditorPage = (WSDITAMapEditorPage) editor.getCurrentPage();
    AuthorElement rootElement = controller.getAuthorDocumentNode().getRootElement();
    AuthorNode topicRefNode = rootElement.getContentNodes().get(0);
    assertEquals("topicref", topicRefNode.getName());
    Mockito.when(wsEditorPage.getSelectedNodes(Mockito.anyBoolean())).thenReturn(new AuthorNode[] {topicRefNode});
    
    List<File> listWithFiles = new ArrayList<File>();
    File file1 = new File("test/test1.dita");
    File file2 = new File("test/test2.dita");
    listWithFiles.add(file1);
    listWithFiles.add(file2);
    
    // Insert before
    InsertTopicRefUtil.insertTopicReferencesInDMM(listWithFiles, InsertType.INSERT_BEFORE, editor);
    AuthorDocument documentNode = controller.getAuthorDocumentNode();
    AuthorDocumentFragment fragment = controller.createDocumentFragment(documentNode, true);
    String xmlContent = controller.serializeFragmentToXML(fragment);
    assertEquals("<map>"
        + "<topicref href=\"test1.dita\"/>"
        + "<topicref href=\"test2.dita\"/>"
        + "<topicref href=\"topic.dita\"/></map>", xmlContent);
    
    // Insert after
    InsertTopicRefUtil.insertTopicReferencesInDMM(listWithFiles, InsertType.INSERT_AFTER, editor);
    documentNode = controller.getAuthorDocumentNode();
    fragment = controller.createDocumentFragment(documentNode, true);
    xmlContent = controller.serializeFragmentToXML(fragment);
    assertEquals("<map>"
        + "<topicref href=\"test1.dita\"/>"
        + "<topicref href=\"test2.dita\"/>"
        + "<topicref href=\"topic.dita\"/>"
        + "<topicref href=\"test1.dita\"/>"
        + "<topicref href=\"test2.dita\"/>"
        + "</map>", xmlContent);
    
    // Insert as child
    InsertTopicRefUtil.insertTopicReferencesInDMM(listWithFiles, InsertType.INSERT_CHILD, editor);
    documentNode = controller.getAuthorDocumentNode();
    fragment = controller.createDocumentFragment(documentNode, true);
    xmlContent = controller.serializeFragmentToXML(fragment);
    assertEquals("<map>"
        + "<topicref href=\"test1.dita\"/><"
        + "topicref href=\"test2.dita\"/>"
        + "<topicref href=\"topic.dita\">"
              + "<topicref href=\"test1.dita\"/>"
              + "<topicref href=\"test2.dita\"/>"
        + "</topicref>"
        + "<topicref href=\"test1.dita\"/>"
        + "<topicref href=\"test2.dita\"/></map>", xmlContent);
  }

  
  /**
   * Create a WSEditor with the given xml content.
   * 
   * @param inputXML The input document content.
   * @throws Exception
   */
  private WSEditor createEditor(String inputXML)  throws Exception {
    String defaultCatalog = new File(CATALOG).toURI().toString();

    // Sets the catalogs
    String[] catalogURIs = new String[] { defaultCatalog };
    CatalogResolverFacade.setCatalogs(catalogURIs, "public");

    // Create a AuthorDocumentController
    AuthorDocumentFacadeFactory facadeFactory = new AuthorDocumentFacadeFactory();
    InputSource[] cssInputSources = new InputSource[] { new InputSource(new StringReader("* {display: block;}")) };
    StringReader reader = new StringReader(inputXML);
    AuthorDocumentFacade facade = facadeFactory.createFacade(new StreamSource(reader), cssInputSources, null,
        new File("."));
    controller = facade.getController();

    // Create mocks.
    WSEditor wsEditor = Mockito.mock(WSEditor.class);
    WSDITAMapEditorPage wsEditorPage = Mockito.mock(WSDITAMapEditorPage.class);

    Mockito.when(wsEditor.getCurrentPage()).thenReturn(wsEditorPage);
    Mockito.when(wsEditorPage.getDocumentController()).thenReturn(controller);
    return wsEditor;
  }
}
