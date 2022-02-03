package com.oxygenxml.resources.batch.converter.view;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.batch.converter.core.word.styles.CustomToDefaultStyleRelation;
import com.oxygenxml.batch.converter.core.word.styles.ResultedHtml;
import com.oxygenxml.batch.converter.core.word.styles.WordStyleMap;
import com.oxygenxml.batch.converter.core.word.styles.WordStyleToHtmlRelation;

import ro.sync.basic.util.URLUtil;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Utilty class used to work with word styles configuration.
 * 
 * @author cosmin_duna
 *
 */
public class WordStylesConfigUtil {
  /**
   * Logger
   */
  private static final Logger logger = LoggerFactory.getLogger(WordStylesConfigUtil.class);

  /**
   * Pattern used to extract Word style rules from text format
   */
  private static final Pattern WORD_STYLE_RULE = Pattern.compile("([^\\[]+)(\\[\\s*style-name\\s*=\\s*'([^']+)?'\\s*\\])?\\s*=>\\s*(.*)");
  
  /**
   * Private constructor
   */
  private WordStylesConfigUtil() {
    // Avoid instantiation
  }
  
  /**
   * Export the word styles mapping from table in xml format into the given output file.
   * 
   * @param wordMappingtableModel The model that contains styles mapping
   * @param outputFile The output file.
   */
  public static void exportWordStylesMapConfiguration(DefaultTableModel tabelModelWithConfig, File outputFile) {
    if (outputFile != null) {
      WordStyleMap stylesMapping = new WordStyleMap();
      stylesMapping.setCustomToDefaultList(new ArrayList<CustomToDefaultStyleRelation>());
      List<WordStyleToHtmlRelation> stylesToHtmlList = new ArrayList<WordStyleToHtmlRelation>();

      int rowCount = tabelModelWithConfig.getRowCount();
      for (int i = 0; i < rowCount; i++) {
        WordStyleToHtmlRelation wordStyleToHtmlRelation = new WordStyleToHtmlRelation();
        wordStyleToHtmlRelation.setElement((String)tabelModelWithConfig.getValueAt(i, 0));
        wordStyleToHtmlRelation.setStyleName((String) tabelModelWithConfig.getValueAt(i, 1));
        String htmlElement = (String)tabelModelWithConfig.getValueAt(i, 2);
        ResultedHtml resultedHTML = new ResultedHtml();
        if(htmlElement.endsWith(":fresh")) {
          resultedHTML.setFresh("true");
          resultedHTML.setName(htmlElement.substring(0, htmlElement.indexOf(":fresh")));
        } else {
          resultedHTML.setName(htmlElement);
          resultedHTML.setFresh("false");
        }
        wordStyleToHtmlRelation.setResultedHTML(resultedHTML);
        stylesToHtmlList.add(wordStyleToHtmlRelation);
      }
      
      stylesMapping.setStyleToHtmlList(stylesToHtmlList);

      try {
        JAXBContext context = JAXBContext.newInstance(WordStyleMap.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.marshal(stylesMapping, outputFile);
        PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
        if (pluginWorkspace != null) {
          pluginWorkspace.open(URLUtil.correct(outputFile));
        }
      } catch (JAXBException e1) {
        logger.debug(e1.getMessage(), e1);
      } catch (MalformedURLException e) {
        logger.debug(e.getMessage(), e);
      }
    }
  }
  
  /**
   * Import the word styles mapping into the given table in xml format into the given output file.
   * 
   * @param wordMappingtableModel The model that contains styles mapping
   * @param outputFile The output file.
   */
  public static void importWordStylesMapConfiguration(DefaultTableModel tabelModelWithConfig, File configFile) {
    if (configFile != null) {
      try {
        JAXBContext context = JAXBContext.newInstance(WordStyleMap.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        WordStyleMap stylesMap = (WordStyleMap) unmarshaller.unmarshal(configFile);
        
        List<WordStyleToHtmlRelation> styleToHtmlList = stylesMap.getStyleToHtmlList();
        if(styleToHtmlList != null) {
          for (WordStyleToHtmlRelation styleToHtmlRelation : styleToHtmlList) {
            tabelModelWithConfig.addRow(new String[] {
                styleToHtmlRelation.getElement() != null ? styleToHtmlRelation.getElement() : "",
                styleToHtmlRelation.getStyleName() != null ? styleToHtmlRelation.getStyleName() : "",
                getHTMLElement(styleToHtmlRelation.getResultedHTML())});
          }

          List<CustomToDefaultStyleRelation> customToDefaultList = stylesMap.getCustomToDefaultList();
          if(customToDefaultList != null) {
            for (int i = 0; i < customToDefaultList.size(); i++) {
              CustomToDefaultStyleRelation styleRelation = customToDefaultList.get(i);
              String defaultStyle = styleRelation.getDefaultStyle();
              int nuOfStyles = styleToHtmlList.size();
              for (int j = 0; j < nuOfStyles; j++) {
                WordStyleToHtmlRelation styleToHtmlRelation = styleToHtmlList.get(j);
                String styleName = styleToHtmlRelation.getStyleName();

                if(styleName != null && styleName.equals(defaultStyle)) {
                  tabelModelWithConfig.addRow(new String[] {
                      styleToHtmlRelation.getElement() != null ? styleToHtmlRelation.getElement() : "",
                          styleRelation.getCustomStyle() != null ? styleRelation.getCustomStyle() : "",
                      getHTMLElement(styleToHtmlRelation.getResultedHTML())});
                  break;
                }
              }
            }
          }
        }
      } catch (JAXBException e1) {
        logger.debug(e1.getMessage(), e1);
      }
    }
  }
  
  /**
   * Get the html element in string format from the given resultedHTML object.
   * 
   * @param resultedHTML The resultedHTML object to be processed.
   * 
   * @return The html element in string format
   */
  private static String getHTMLElement (ResultedHtml resultedHTML) {
    String element = "";
    if (resultedHTML != null && resultedHTML.getName() != null) {
      element =  resultedHTML.getName();
      if (Boolean.toString(true).equals(resultedHTML.getFresh())) {
        element += ":fresh";
      }
    }
    return element;
  }
  
  /**
   * Set the given word styles mapping in given table model.
   * 
   * @param wordStylesMappingInText The word styles mapping in text format.
   * @param wordMappingtableModel The model that contains styles mapping
   */
  public static void setWordStylesMappingInTable(String wordStylesMappingInText, DefaultTableModel wordMappingtableModel) {
    // Remove old config from table.
    int rowCount = wordMappingtableModel.getRowCount();
    for (int i = rowCount - 1; i >= 0; i--) {
      wordMappingtableModel.removeRow(i);
    }
    
    String[] rules = wordStylesMappingInText.split("\n");
    int nuOfRules = rules.length;
    for (int i = 0; i < nuOfRules; i++) {
      String rule = rules[i];
      if (!rule.isEmpty()) {
        Matcher matcher = WORD_STYLE_RULE.matcher(rule);
        if(matcher.matches()) {
          int nuOfGroups = matcher.groupCount();
          if(nuOfGroups == 4) {
            wordMappingtableModel.addRow(new String[] {
                matcher.group(1).trim(),  matcher.group(3) != null ? matcher.group(3).trim() : "", matcher.group(4).trim()});
          }
        }
      }
    }
  }
  
  /**
   * Serialiaze word styles mapping from table in text format.
   * @param wordMappingtableModel The model that contains styles mapping
   * 
   * @return The Word styles mapping in text format.
   */ 
  public static String serializeWordStylesMapping(DefaultTableModel wordMappingtableModel) {
    StringBuilder toRet = new StringBuilder();
    int rowCount = wordMappingtableModel.getRowCount();
    for (int i = 0; i < rowCount; i++) {
      toRet.append(((String)wordMappingtableModel.getValueAt(i, 0)).trim());
      String style = (String) wordMappingtableModel.getValueAt(i, 1);
      if (!style.isEmpty()) {
        toRet.append("[style-name='").append(style.trim()).append("']");
      }
      toRet.append(" => ");
      toRet.append(((String)wordMappingtableModel.getValueAt(i, 2)).trim());
      if (i < rowCount - 1) {
        toRet.append("\n");
      }
    }
    return toRet.toString();
  }
}
