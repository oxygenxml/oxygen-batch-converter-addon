/*
 * Copyright (c) 2022 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */

package com.oxygenxml.resources.batch.converter.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oxygenxml.RetinaDetector;

import ro.sync.basic.util.URLUtil;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.ColorTheme;

public class IconsLoader {
  /**
   * Dark icon marker
   */
  private static final String DARK_ICON_MARKER = "_dark";
  /**
   * Marks retina icons
   */
  private static final String RETINA_ICON_PATH_MARKER = "@2x";
  /**
   * Fallback icon, for when a specialized interactor does not
   * define one, but it should. 
   */
  private static ImageIcon FALLBACK_ICON; // NOSONAR java:S3008
  
  static {
    FALLBACK_ICON = new ImageIcon();
    BufferedImage noIconImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = noIconImage.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 16, 16);
    FALLBACK_ICON.setImage(noIconImage);
  }
  
  /**
    * Constructor.
    */
  private IconsLoader() {
    // Private to avoid instantiations
  }

  /**
   * Logger for logging.
   */
  private static final Logger logger = LoggerFactory.getLogger(IconsLoader.class.getName());
  
  /**
   * Load an icon using the Oxygen API.
   * 
   * @param icon The system id of the icon.
   * @return The icon or null.
   */
  public static ImageIcon loadIcon(String icon) {
    ImageIcon imgIcon = FALLBACK_ICON;
    if (icon != null) {
      URL resource = IconsLoader.class.getResource(icon);
      if (resource != null) {
        imgIcon = loadIcon(resource);
      }
    }
    return imgIcon;
  }
  
  /**
   * Load an icon using the Oxygen API.
   * 
   * @param iconLocation the location of the icon.
   * @return The icon
   */
  public static ImageIcon loadIcon(URL iconLocation) {
    ImageIcon imgIcon = FALLBACK_ICON;
    if (iconLocation != null) {
      PluginWorkspace pw = PluginWorkspaceProvider.getPluginWorkspace();
      if(pw != null) {
        ImageUtilities imageUtilities = pw.getImageUtilities();
        if(imageUtilities != null) {
          imgIcon = (ImageIcon) imageUtilities.loadIcon(iconLocation);
        }
      }
    }
    return imgIcon;
  }
  
  /**
   * Load animated icon.
   * 
   * @param iconPath The path to the icon.
   * 
   * @return the icon or <code>null</code>.
   */
  public static ImageIcon loadAnimatedIcon(String iconPath) {
    ImageIcon icon = FALLBACK_ICON;
    if (iconPath != null) {
      PluginWorkspace pw = PluginWorkspaceProvider.getPluginWorkspace();
      if (pw instanceof StandalonePluginWorkspace) {
        StandalonePluginWorkspace saPluginWS = (StandalonePluginWorkspace) pw;
        ColorTheme colorTheme = saPluginWS.getColorTheme();
        boolean darkTheme = colorTheme != null && colorTheme.isDarkTheme();
        //4 cases with fallbacks
        //Retina + dark, retina, dark, default
        URL iconResource = null;
        String retinaIconPath = getRetinaIconPath(iconPath);
        boolean foundRetina = false;
        if(retinaIconPath != null) {
          //We are on retina display
          if(darkTheme) {
            //Maybe dark theme
            String darkRetinaPath = getDarkIconPath(retinaIconPath);
            if(darkRetinaPath != null) {
              iconResource = getResource(darkRetinaPath);
            }
          }
          if(iconResource == null) {
            //Fallback to plain retina path
            iconResource = getResource(retinaIconPath);
          }
          if(iconResource != null) {
            foundRetina = true;
          } 
        }
        //Non retina
        if(iconResource == null && darkTheme) {
          //Try to load the dark icon
          String darkPath = getDarkIconPath(iconPath);
          if(darkPath != null) {
            iconResource = getResource(darkPath);
          }
        }
        if(iconResource == null) {
          //Load the plain icon oath
          iconResource = getResource(iconPath);
        }
        if (iconResource != null) {
          if(foundRetina) {
            icon = new RetinaIcon(iconResource);
          } else {
            icon = new ImageIcon(iconResource);
          }
        } else {
          logger.error("Wrong path for icon: {}", iconPath);
        }
      }
    }
    return icon;
  }

  /**
   * Convert the icon path to an URL
   * @param iconPath The icon path
   * @return The URL
   */
  private static URL getResource(String iconPath) {
    return IconsLoader.class.getResource(iconPath);
  }

  /**
   * Get the icon path for retina screens. 
   * @param iconPath The icon path
   * @return the icon path for retina screens. 
   */
  private static String getRetinaIconPath(String iconPath) {
    float iconScalingFactor = RetinaDetector.getIconScalingFactor();
    if(iconScalingFactor >= 2) {
      int indexSlash = iconPath.lastIndexOf('/');
      if(indexSlash != -1) {
        String parentPath = iconPath.substring(0, indexSlash + 1);
        String iconName = iconPath.substring(indexSlash + 1); 
        String extension = URLUtil.getExtension(iconName);
        String name = URLUtil.removeExtension(iconName);
        if(name != null && ! name.endsWith(RETINA_ICON_PATH_MARKER)) {
          return parentPath + name + RETINA_ICON_PATH_MARKER + "." + extension;
        }
      }
    }
    return null;
  }
  
  /**
   * Get the icon path for dark theme. 
   * @param iconPath The icon path
   * @return the icon path for dark theme. 
   */
  private static String getDarkIconPath(String iconPath) {
    int indexSlash = iconPath.lastIndexOf('/');
    if(indexSlash != -1) {
      String parentPath = iconPath.substring(0, indexSlash + 1);
      String iconName = iconPath.substring(indexSlash + 1); 
      String extension = URLUtil.getExtension(iconName);
      String name = URLUtil.removeExtension(iconName);
      if(name.endsWith(RETINA_ICON_PATH_MARKER)) {
        //Add "_dark" before @2x
        return parentPath 
            + name.substring(0, name.length() - RETINA_ICON_PATH_MARKER.length()) 
            + DARK_ICON_MARKER + RETINA_ICON_PATH_MARKER + "." + extension;
      } else {
        return parentPath + name + DARK_ICON_MARKER + "." + extension;
      }
    }
    return null;
  }
}
