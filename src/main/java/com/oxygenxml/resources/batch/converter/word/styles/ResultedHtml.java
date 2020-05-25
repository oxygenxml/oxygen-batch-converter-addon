package com.oxygenxml.resources.batch.converter.word.styles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * The resulted html element.
 * 
 * @author cosmin_duna
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultedHtml {

  /**
   * The freshness Mammoth's notion for this element. 
   * When it's <code>true</code> it's generated this html element every time a match style will be found.
   * When it's <code>false</code>it will generate a single html element for multiple consecutive word elements with the same style.
   */
  @XmlAttribute
  protected String fresh;

  /**
   * The name of this html element
   */
  @XmlValue
  protected String name;

  /** 
   * Get the freshness notion for this element. 
   * When it's <code>true</code> it's generated a new html element every time a match style will be found.
   * When it's <code>false</code>it will generate a single html element for multiple consecutive word elements with the same style.
   * The default value is "true"
   * 
   * @return  The freshness notion for this element. 
   */
  public String getFresh() {
    String toRet = "true";
    if(fresh != null) {
      toRet = fresh;
    }
    return toRet;
  }

  /**
   * @return The name of this html element
   */
  public String getName() {
    return name;
  }

  /**
   * Set the freshness notion for this element. 
   * 
   * @param fresh The freshness notion for this element. 
   */
  public void setFresh(String fresh) {
    this.fresh = fresh;
  }

  /**
   * Set the name of this html element.
   * @param name The name of this html element.
   */
  public void setName(String name) {
    this.name = name;
  }
}
