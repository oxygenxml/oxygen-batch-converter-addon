package com.oxygenxml.resources.batch.converter.plugin;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import com.oxygenxml.resources.batch.converter.resources.Images;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;


/**
 * Utility class used in BatchConverterPlugin.
 *
 */
public class BatchConverterPluginUtil {

  /**
   * Private constructor.
   */
  private BatchConverterPluginUtil() {
    // Do nothing.
  }
  
  /**
   * Add the given actions in give menu.
   * @param menu The menu.
   * @param actions The actions.
   * @return The menu with actions.
   */
  public static Menu addActionsInMenu(Menu menu, Map<String, List<Action>> actions) {
    // Get the empty image for JMenuItems
    URL emptyImageToLoad = StandalonePluginWorkspace.class.getClassLoader().getResource(Images.EMPTY_IMAGE);
    
    Iterator<String> iterator = actions.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();

      List<Action> keyValue = actions.get(key);
      int size = keyValue.size();
      for (int i = 0; i < size; i++) {
        // add actions in batchConvertMenuToolbar
        JMenuItem jMenuItem = new JMenuItem(keyValue.get(i));
        if(emptyImageToLoad != null){
          jMenuItem.setIcon(getIcon(emptyImageToLoad));
        }
        menu.add(jMenuItem);
      }
      if (iterator.hasNext()) {
        //add separator
        menu.addSeparator();
      }
    }

    return menu;
  }
  
  /**
   * Get the Icon from given URL.
   * @param resource The URL
   * @return The icon.
   */
  public static Icon getIcon(URL resource) {
    Icon toReturn = null;
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (resource != null && pluginWorkspace != null) {
      toReturn = (Icon) pluginWorkspace.getImageUtilities().loadIcon(resource);
    }
    return toReturn;
  }
}
