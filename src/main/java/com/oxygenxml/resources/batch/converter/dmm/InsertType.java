package com.oxygenxml.resources.batch.converter.dmm;

import ro.sync.ecss.extensions.api.AuthorConstants;

/**
 * The type of insertion that can be done in DMM.
 */
public enum InsertType {
  NO_INSERT(""),
  INSERT_CHILD(AuthorConstants.POSITION_INSIDE_LAST),
  INSERT_BEFORE(AuthorConstants.POSITION_BEFORE),
  INSERT_AFTER(AuthorConstants.POSITION_AFTER);
  
  /**
   * The associated constant from Oxygen API.
   */
  private String oxyConstant;
  
  /**
   * Constuctor.
   * @param oxyConstant The asociated constant form Oxygen
   */
  private InsertType(String oxyConstant) {
    this.oxyConstant = oxyConstant;
  }
  
  /**
   * Get the associated constant from Oxygen API
   * 
   * @return the associated constant from Oxygen API.
   */
  public String getOxyConstant() {
    return oxyConstant;
  }
}
