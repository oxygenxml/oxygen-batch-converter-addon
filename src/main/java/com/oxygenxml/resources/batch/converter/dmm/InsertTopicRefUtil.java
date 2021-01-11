package com.oxygenxml.resources.batch.converter.dmm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.reporter.OxygenProblemReporter;

import ro.sync.ecss.dita.DITAAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * This class contains utility methods used to insert topicrefs in the map opened in DMM.
 * 
 * @author cosmin_duna
 */
public final class InsertTopicRefUtil {
  /**
   * Logger
   */
   private static final Logger logger = Logger.getLogger(InsertTopicRefUtil.class);
   
  /**
   * Private constructor.
   */
  private InsertTopicRefUtil() {
    // Avoid instantiation
  }
  
  
  /**
   * Insert topic references at the given converted files into the page opened in DMM.
   * 
   * @param convertedFiles     The converted files
   * @param insertType         The type of insertion to be made.
   */
  public static void insertTopicReferencesInDMM(List<File> convertedFiles, InsertType insertType, WSEditor dmmEditor) {
    if(dmmEditor != null) {
      WSEditorPage currentPage = dmmEditor.getCurrentPage();
      if(currentPage instanceof WSDITAMapEditorPage) {
        WSDITAMapEditorPage dmmPage = (WSDITAMapEditorPage) currentPage;
        final AuthorDocumentController documentController = dmmPage.getDocumentController();
        AuthorNode[] selectedNodes = dmmPage.getSelectedNodes(true);
        if(selectedNodes.length > 0) {
          final AuthorNode targetNode = selectedNodes[0];
          
          URL baseURL = targetNode.getXMLBaseURL();
          if(insertType == InsertType.INSERT_BEFORE || insertType == InsertType.INSERT_AFTER) {
            AuthorNode parent = targetNode.getParent();
            if(parent != null) {
              baseURL = parent.getXMLBaseURL();
            }
          }
          try {
            final int offset = getOffsetRelativeTo(targetNode, insertType);
            String topicRefElementName = getTopicRefElementName(documentController, offset);
            final String xmlFragment = createTopicReferencesFragment(topicRefElementName, convertedFiles, baseURL);
            SwingUtilities.invokeAndWait(new Runnable() {
              @Override
              public void run() {
                try {
                  documentController.insertXMLFragmentSchemaAware(xmlFragment, offset);
                } catch (AuthorOperationException e) {
                  new OxygenProblemReporter().reportProblem(e, null);
                }
              }
            });
          } catch (InvocationTargetException e) {
            logger.debug(e.getMessage(), e);
          } catch (InterruptedException e) {
            logger.debug(e.getMessage(), e);
          }
        }
      }
    }
  }

  /**
   * Get the offset to insert relative to target node and insert position.
   * 
   * @param targetNode        The target node.
   * @param insertPosition    The insert position relative to target node.
   * 
   * @return The offset to insert.
   */
  private static final int getOffsetRelativeTo(AuthorNode targetNode, InsertType insertPosition){
    int offset = -1;
    if (InsertType.INSERT_AFTER == insertPosition) {
      offset = targetNode.getEndOffset() + 1;
    } else if ( InsertType.INSERT_BEFORE == insertPosition) {
      offset = targetNode.getStartOffset();
    } else if (InsertType.INSERT_CHILD == insertPosition) {
      // Insert as last child of node
      offset = targetNode.getEndOffset();
    }
    return offset;
  }
  
  /**
   * Get the name of the topic ref element to insert
   * 
   * @param authorAccess The author document controller
   * @param offset  The position to insert.
   * 
   * @return A name for the topic ref to insert
   */
  private static final String getTopicRefElementName(AuthorDocumentController controller, int offset) {
    String topicRefElementName = "topicref";
    try {
      Class<DITAAccess> ditaAccessClass = DITAAccess.class;
      Method getInsertTopicRefElementNameMethod = ditaAccessClass.getMethod(
          "getAutoInsertTopicRefElementName", AuthorDocumentController.class, int.class);
      topicRefElementName = (String)getInsertTopicRefElementNameMethod.invoke(null, controller, offset);
    } catch (NoSuchMethodException e) {
      // Ignore. We have this method starting with 23.1. We will use the default in the older versions.
    } catch (IllegalAccessException e) {
      // Ignore. We have this method starting with 23.1. We will use the default in the older versions.
    } catch (IllegalArgumentException e) {
      // Ignore. We have this method starting with 23.1. We will use the default in the older versions.
    } catch (InvocationTargetException e) {
      // Ignore. We have this method starting with 23.1. We will use the default in the older versions.
    }
    
    return topicRefElementName;
  } 
  
  /**
   * Create a XML fragment with topic references at the given files.
   * @param files     The files to be refered.
   * @param baseURL   The base URL.
   * 
   * @return The XML fragment with topic references at the given files.
   */
  private static final String createTopicReferencesFragment(String topicRefElementName, List<File> files, URL baseURL) {
    StringBuilder topicRefsFragment = new StringBuilder();
    for (File file : files) {
      String relativePath = file.getAbsolutePath();
      try {
        UtilAccess utilAccess = PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess();
        relativePath = utilAccess.makeRelative(baseURL, utilAccess.convertFileToURL(file));
      } catch (MalformedURLException e) {
        // We will keep the absolute path
        logger.debug(e.getMessage(), e);
      }
      topicRefsFragment.append("<").append(topicRefElementName).append(" href=\"").append(relativePath).append("\"");
      if(relativePath.endsWith("ditamap")) {
        topicRefsFragment.append(" format=\"ditamap\"");
      }
      topicRefsFragment.append("/>");
    }
    return topicRefsFragment.toString();
  }
  
}
