/*
 * Copyright (c) 2018 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */
package com.oxygenxml.resources.batch.converter.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import ro.sync.basic.util.PlatformDetector;
import ro.sync.basic.util.accessibility.AccessibilityUtil;

/**
 * Link opener label.
 * 
 * Perform a custom action on mouse click on the linked label.
 */
public abstract class LinkLabel extends JLabel {
  /**
   * No focus border.
   */
  private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  /**
   * A color to be used for non-selected/non-hovered links.
   */
  private static final Color LINK_COLOR = new Color(0, 0, 178);

  /**
   * The color of a link label when hovered.
   */
  private static final Color HOVERED_LINK_COLOR = new Color(0, 0, 255);
  
  /**
   * Constructor.
   * 
   * @param text The text to be rendered.   
   */
  public LinkLabel(String text) {
    super(text);
    
    setFocusable(true);
    
    // EXM-26217 Repaint when focus gained/lost
    addFocusListener(new FocusListener() {      
      @Override
      public void focusLost(FocusEvent e) {
        updateFocusBorder();
      }
      
      @Override
      public void focusGained(FocusEvent e) {
        updateFocusBorder();
      }
    });
    //EXM-39788 Provision to avoid the label moving when focused
    setBorder(DEFAULT_NO_FOCUS_BORDER);
    
    setForeground(LINK_COLOR);
    
    Map<TextAttribute, Integer> underlinedTextAttributeMap = new HashMap<>();
    underlinedTextAttributeMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
    Font underlineFont = getFont().deriveFont(underlinedTextAttributeMap);
    
    // Create link label font
    setFont(underlineFont);
    
    addMouseListener(new MouseAdapter() {
      
      // Adds the link look of the Url label.
      @Override
      public void mouseClicked(MouseEvent e) {
        if (isEnabled() && !e.isPopupTrigger() && !SwingUtilities.isRightMouseButton(e)) {
          performAction();
        }
      }
      
      @Override
      public void mouseEntered(MouseEvent e) {
        setFont(underlineFont);
        setForeground(HOVERED_LINK_COLOR);
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        setFont(underlineFont);
        setForeground(LINK_COLOR);
      }
    });
    
    addKeyListener(new KeyAdapter() {
        
        @Override
        public void keyPressed(KeyEvent e) {
          if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) 
              && isFocusOwner() && isEnabled()) {
            e.consume();
            performAction();
          }
        }
      });
    
    // Set hand cursor for this component.
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    setHorizontalAlignment(JLabel.CENTER);
  }
  
  /**
   * Update focus border when focus gained or lost.
   */
  private void updateFocusBorder() {
    Border border = DEFAULT_NO_FOCUS_BORDER;
    if (isFocusOwner()) {
      if(PlatformDetector.isWinXPOrLater() || AccessibilityUtil.isJavaAccessBridgeEnabled()){
        //EXM-39788 Stronger focus border
        final Color focusColor = UIManager.getColor("Button.focus", this.getLocale());
        if(focusColor != null){
          //Use a dashed border
          border = BorderFactory.createDashedBorder(focusColor);
        }
      }
      if(border == null){
        // EXM-26217 Set a border to make visible the focused component.
        border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder", this.getLocale());
        if (border == null) {
          border = UIManager.getBorder("Table.focusCellHighlightBorder", this.getLocale());
        }
      }
    }
    setBorder(border);
  }
  
  /**
   * Perform the custom action on mouse click on the linked label.
   */
  protected abstract void performAction();
  
  /**
   * @see javax.swing.JLabel#getAccessibleContext()
   */
  @Override
  public AccessibleContext getAccessibleContext() {
    if (accessibleContext == null) {
      AccessibleContext accessibleContextSuper = super.getAccessibleContext();
      accessibleContext = new AccessibleContext() {
        /**
         * @see ro.sync.ui.application.AccessibleContextWrapper#getAccessibleRole()
         */
        @Override
        public AccessibleRole getAccessibleRole() {
          return AccessibleRole.HYPERLINK;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
          return accessibleContextSuper.getAccessibleStateSet();
        }

        @Override
        public int getAccessibleIndexInParent() {
          return accessibleContextSuper.getAccessibleIndexInParent();
        }

        @Override
        public int getAccessibleChildrenCount() {
          return accessibleContextSuper.getAccessibleChildrenCount();
        }

        @Override
        public Accessible getAccessibleChild(int i) {
          return accessibleContextSuper.getAccessibleChild(i);
        }

        @Override
        public Locale getLocale() throws IllegalComponentStateException {
          return accessibleContextSuper.getLocale();
        }
      };
    }
    return accessibleContext;
  }
}