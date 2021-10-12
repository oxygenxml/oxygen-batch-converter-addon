/*
 * Copyright (c) 2018 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextArea;

import ro.sync.basic.util.LFDetector;

/**
 * A text section that stands out.
 * 
 */
public class MultilineLabel extends JTextArea {
  
  /**
  * Default width for a text area.
  */
 private final static int DEFAULT_WIDTH = 100;
  
  /**
   * The approximative width for this area.
   */
  private int aproxWidth;

  /**
   * Constructor.
   * 
   * @param warning The warning message
   */
  public MultilineLabel(String warning) {    
    this(warning, DEFAULT_WIDTH);
  }

  /**
   * Constructor.
   * 
   * @param warning The warning message
   * @param boldText <code>true</code> if the text must be bold, <code>false</code> otherwise
   * @param aproxWidth The approximative width for this text area.
   */
  public MultilineLabel(String warning, int aproxWidth) {    
    super(warning);
    this.aproxWidth = aproxWidth;

    setLineWrap(true);
    setWrapStyleWord(true);
    setEditable(false);
    setOpaque(false);
    if (LFDetector.isNimbusLF()) {
      setBackground(new Color(0, 0, 0, 0));
    }
  }
  
  /**
   * EXM-26470 - Modifies the preferred size's width according to the approximative width specified for this area.
   * If the actual width is greater than the width limit then the new width becomes the width limit (prevent 
   * unnecessary horizontal scroll bars in option panes after dialog resize)
   * 
   * @see javax.swing.JTextArea#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {
    Dimension preferredSize = super.getPreferredSize();
    if (preferredSize.width > aproxWidth) {
      preferredSize.width = aproxWidth;
    }
    return preferredSize;
  }
  
}