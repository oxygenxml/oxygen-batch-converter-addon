package com.oxygenxml.resources.batch.converter.word.styles;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.converters.ExcelToDITAConverter;
import com.oxygenxml.resources.batch.converter.plugin.BatchConverterPlugin;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

/**
 * Loader for the style map used in conversion from Word format.
 * 
 * @author cosmin_duna
 */
public class WordStyleMapLoader {
  /**
   * Logger.
   */
  private static final Logger logger = Logger.getLogger(WordStyleMapLoader.class);
  
  /**
   * Translator
   */
  private static Translator translator = new OxygenTranslator();
  
  /**
   * Path to style map configuration file.
   */
  private static final String STYLE_MAP_PATH = "/config/wordStyleMap.xml";

  /**
   * An imposed file of style map. It's used in TCs.
   */
  private static File imposedStyleMap = null;
  
  /**
   * Cached style map.
   */
  private static String styleMap = null;
  
  /**
   * Load the style map used in conversion from Word format.
   * 
   * @return The style map in a format known by Java Mammoth.
   * 
   * @throws JAXBException When config document has problems.
   */
  public static final String loadStyleMap() throws JAXBException {
    if(styleMap == null) {
      File styleMapFile = null;
      if(imposedStyleMap == null) {
        File baseDir = BatchConverterPlugin.getInstance().getDescriptor().getBaseDir();
        styleMapFile = new File(baseDir, STYLE_MAP_PATH);
      } else {
        styleMapFile = imposedStyleMap;
      }

      if(styleMapFile != null) {
        JAXBContext context = JAXBContext.newInstance(WordStyleMap.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        WordStyleMap styleMapObj = (WordStyleMap) unmarshaller.unmarshal(styleMapFile);
        styleMap = convertToStringFormat(styleMapObj);
      } else {
        throw(new JAXBException(translator.getTranslation(Tags.CONFIG_FILE_NOT_FOUND,"")));
      }
    }
    return styleMap;
  }
  
  /**
   * Convert the style map object in the String format known by Java Mammoth.
   * 
   * @param styleMap The style map object.
   * 
   * @return The style map in a format known by Java Mammoth.
   */
  private static final String convertToStringFormat(WordStyleMap styleMap) {
    StringBuilder toRet = new StringBuilder();
    ArrayList<WordStyleToHtmlRelation> styleToHtmlList = styleMap.getStyleToHtmlList();
    for (WordStyleToHtmlRelation styleToHtmlRelation : styleToHtmlList) {
     appendStyleInfo(toRet, styleToHtmlRelation.getElement(),
         styleToHtmlRelation.getStyleName(), styleToHtmlRelation.getResultedHTML());
    }
    
    ArrayList<CustomToDefaultStyleRelation> customToDefaultList = styleMap.getCustomToDefaultList();
    for (int i = 0; i < customToDefaultList.size(); i++) {
      CustomToDefaultStyleRelation styleRelation = customToDefaultList.get(i);
      String defaultStyle = styleRelation.getDefaultStyle();
      int nuOfStyles = styleToHtmlList.size();
      for (int j = 0; j < nuOfStyles; j++) {
        WordStyleToHtmlRelation styleToHtmlRelation = styleToHtmlList.get(j);
        String styleName = styleToHtmlRelation.getStyleName();
       
        if(styleName != null && styleName.equals(defaultStyle)) {
          appendStyleInfo(toRet, styleToHtmlRelation.getElement(),
              styleRelation.getCustomStyle(), styleToHtmlRelation.getResultedHTML());
          break;
        }
      }
    }
    
    return toRet.toString();
  }
  
  /**
   * Append the style information in Java Mammoth format into the given string builder.
   * 
   * @param stringBuilder The string builder where the information will be appended.
   * @param element       The word element name.
   * @param style         The word style of the element.
   * @param htmlElement   The converted HTML element.
   */
  private static final void appendStyleInfo(
      StringBuilder stringBuilder, String element, String style, ResultedHtml htmlElement) {
    if(element != null) {
      stringBuilder.append(element);
    } else {
      stringBuilder.append("p");
    }
    
    if(style != null) {
      stringBuilder.append("[style-name='").append(style).append("']");
    }
    stringBuilder.append(" => ").append(htmlElement.getName());
    if(Boolean.parseBoolean(htmlElement.getFresh())) {
      stringBuilder.append(":fresh");
    }
    stringBuilder.append("\n");
  }
  
  /**
   * Impose a style map file. Used in TCs.
   * 
   * @param styleMapURL The style map file to be imposed.
   */
  public static void imposeStyleMapURL(File styleMapURL) {  
    imposedStyleMap = styleMapURL;
    styleMap = null;
  }
}