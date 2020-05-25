package com.oxygenxml.resources.batch.converter.word.styles;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Relation between a word style and a HTML element.
 * 
 * @author cosmin_duna
 */
@XmlRootElement(name = "relation")
public class WordStyleToHtmlRelation {

  /**
   * The word element.
   */
  private String element;
  /**
   * The style of the word element.
   */
  private String styleName;
  /**
   * The converted HTML element.
   */
  private ResultedHtml resultedHTML;
  
  
  /**
   * @return The word element.
   */
  public String getElement() {
    return element;
  }
  
  /**
   * @return The style of the word element.
   */
  public String getStyleName() {
    return styleName;
  }
  
  /**
   * @return The converted HTML element.
   */
  public ResultedHtml getResultedHTML() {
    return resultedHTML;
  }
  
  /**
   * Set the word element.
   * @param element The word element.
   */
  public void setElement(String element) {
    this.element = element;
  }
  
  /**
   * Set the style of word element.
   * @param styleName The style of word element.
   */
  public void setStyleName(String styleName) {
    this.styleName = styleName;
  }
  
  /**
   * Set the converted HTML element.
   * @param resultedHTML The converted HTML element.
   */
  public void setResultedHTML(ResultedHtml resultedHTML) {
    this.resultedHTML = resultedHTML;
  }
}