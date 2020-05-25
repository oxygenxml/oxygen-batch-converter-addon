package com.oxygenxml.resources.batch.converter.word.styles;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Relation between a custom style and a default style.
 * 
 * @author cosmin_duna
 */
@XmlRootElement(name = "styleRelation")
public class CustomToDefaultStyleRelation {
  /**
   * The custom style name.
   */
  String customStyle;
  /**
   * The default style name.
   */
  String defaultStyle;
  
  /**
   * @return The custom style name.
   */
  public String getCustomStyle() {
    return customStyle;
  }
  /**
   * @return The default style name.
   */
  public String getDefaultStyle() {
    return defaultStyle;
  }
  /**
   * Set the custom style name.
   * @param customStyle The custom style name.
   */
  public void setCustomStyle(String customStyle) {
    this.customStyle = customStyle;
  }
  /**
   * Set the default style name.
   * @param defaultStyle The default style name.
   */
  public void setDefaultStyle(String defaultStyle) {
    this.defaultStyle = defaultStyle;
  }
}
