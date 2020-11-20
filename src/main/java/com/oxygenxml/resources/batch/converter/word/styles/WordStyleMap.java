package com.oxygenxml.resources.batch.converter.word.styles;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The style map used to configure conversions from Word format.
 * 
 * @author cosmin_duna
 */
@XmlRootElement(name = "styleMap")
@XmlAccessorType(XmlAccessType.FIELD)
public class WordStyleMap {
  
  /**
   * A mapping of word styles to HTML elements
   */
  @XmlElementWrapper(name = "toHTML")
  @XmlElement(name = "relation")
  private List<WordStyleToHtmlRelation> styleToHtmlList;
  
  
  /**
   * A mapping between custom style and default style.
   */
  @XmlElementWrapper(name = "customToDefault")
  @XmlElement(name = "styleRelation")
  private List<CustomToDefaultStyleRelation> customToDefaultList;


  /**
   * @return The relations between word styles and HTML elements
   */
  public List<WordStyleToHtmlRelation> getStyleToHtmlList() {
    return styleToHtmlList;
  }
  
  /**
   * Set the relations between word styles and HTML elements
   * 
   * @param styleToHtmlList The relations between word styles and HTML elements
   */
  public void setStyleToHtmlList(List<WordStyleToHtmlRelation> styleToHtmlList) {
    this.styleToHtmlList = styleToHtmlList;
  }
  
  /**
   * @return The relations between custom styles and default elements
   */
  public List<CustomToDefaultStyleRelation> getCustomToDefaultList() {
    return customToDefaultList;
  }
  
  /**
   * Set the relations between custom styles and default elements
   * 
   * @param customToDefaultList The relations between custom styles and default elements
   */
  public void setCustomToDefaultList(List<CustomToDefaultStyleRelation> customToDefaultList) {
    this.customToDefaultList = customToDefaultList;
  }
}
