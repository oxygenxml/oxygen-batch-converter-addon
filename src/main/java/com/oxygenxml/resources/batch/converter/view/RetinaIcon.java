package com.oxygenxml.resources.batch.converter.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Retina icon which pains at half the size.
 * @author radu_coravu
 */
public class RetinaIcon extends ImageIcon {

  /**
   * Constructor
   * @param iconPath Icon path
   */
  public RetinaIcon(URL iconPath) {
    super(iconPath);
  }
  
  /**
   * Half the original icon width
   */
  @Override
  public int getIconWidth() {
    return super.getIconWidth()/2;
  }
  
  /**
   * Half the original icon height
   */
  @Override
  public int getIconHeight() {
    return super.getIconHeight()/2;
  }
  /**
   * Draw scaled
   */
  @Override
  public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
    if (g instanceof Graphics2D) {
      final Graphics2D g2d = (Graphics2D) g;
      //Scale the image half it size.
      g2d.scale(1 / 2.0f, 1 / 2.0f);
    }
    super.paintIcon(c, g, x, y);
  }
}
