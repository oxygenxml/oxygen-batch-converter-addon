package com.oxygenxml.resources.batch.converter.view;

/**
 * Info about converter additional option
 * 
 * @author cosmin_duna
 *
 */
public class AdditionalOptionsInfo {
  /**
   * The presented message for the additional option.
   */
  private String message; 
  
  /**
   * The default value of the addtional option
   */
  private boolean defaultValue;

  /**
   * Constructor
   * 
   * @param message       the presented message for the additional option.
   * @param defaultValue  the default value of the addtional option
   */
  public AdditionalOptionsInfo(String message, boolean defaultValue) {
    this.message = message;
    this.defaultValue = defaultValue;
  }

  /**
   * Get the presented message for the additional option.
   * @return the presented message for the additional option.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the default value of the addtional option.
   * @return the default value of the addtional option.
   */
  public boolean getDefaultValue() {
    return defaultValue;
  }
  
  
}
