/*
 * Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter;

/**
 * The conversion format. It's a pair of input format and output format.
 * 
 * @author cosmin_duna
 */
public class ConversionFormat {
  /**
   * The conversion input format
   */
  private String inputFormat;
  /**
   * The conversion output format
   */
  private String outputFormat;

  /**
   * Constructor
   */
  public ConversionFormat() { 
  }
  
  /**
   * Constructor.
   * 
   * @param inputFormat  The conversion input format
   * @param outputFormat The conversion output format
   */
  public ConversionFormat(String inputFormat, String outputFormat) {
    this.inputFormat = inputFormat;
    this.outputFormat = outputFormat;
  }
  
  /**
   * Get the conversion input format
   * 
   * @return The conversion input format
   */
  public String getInputFormat() {
    return inputFormat;
  }
  
  /**
   * Get the conversion output format
   * 
   * @return The conversion output format
   */
  public String getOutputFormat() {
    return outputFormat;
  }

  /**
   * Set the conversion input format
   * 
   * @param inputFormat The conversion input format
   */
  public void setInputFormat(String inputFormat) {
    this.inputFormat = inputFormat;
  }

  /**
   * Set the conversion output format
   * 
   * @param outputFormat The conversion output format
   */
  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((inputFormat == null) ? 0 : inputFormat.hashCode());
    result = prime * result + ((outputFormat == null) ? 0 : outputFormat.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConversionFormat other = (ConversionFormat) obj;
    if (inputFormat == null) {
      if (other.inputFormat != null) {
        return false;
      }
    } else if (!inputFormat.equals(other.inputFormat)) {
      return false;
    }
    if (outputFormat == null) {
      if (other.outputFormat != null) {
        return false;
      }
    } else if (!outputFormat.equals(other.outputFormat)) {
      return false;
    }
    return true;
  } 
}
