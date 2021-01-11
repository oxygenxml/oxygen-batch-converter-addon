package com.oxygenxml.resources.batch.converter.plugin;

import java.awt.Component;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.oxygenxml.resources.batch.converter.resources.Images;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
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
   * Search for the submenu with the given id into the given menu.
   * 
   * @param menu                  The menu
   * @param submenuId             The id of the submenu to search
   * @param pluginWorkspaceAccess StandalonePluginWorkspace
   * 
   * @return The submenu, or <code>null</code> if it cannot be found.
   */
  public static JMenu searchForSubMenu(JMenu menu, String submenuId, StandalonePluginWorkspace pluginWorkspaceAccess) {
    JMenu menuToRet = null;
    Component[] menuComponents = menu.getMenuComponents();
    for (int i = 0; i < menuComponents.length; i++) {
      Component currrentComponent = menuComponents[i];
      if(currrentComponent instanceof JMenu) {
        String currentId = pluginWorkspaceAccess.getActionsProvider().getActionID(currrentComponent);
        if(submenuId.equals(currentId)) {
          menuToRet = (JMenu)currrentComponent;
        }
      }
    }
    return menuToRet;
  }
  
  /**
   * Search for actions into the given menu.
   * 
   * @param menu                    The menu to search.
   * @param perfectOptionActionId   The id of the perfect action to find.
   * @param secondOptionActionId    The action id of the second option, if the perfect one cannot be found.
   * @param searchOnlyInEnableActions To search only in enable actions, <code>false</code> to search in all action
   * @param pluginWorkspaceAccess   Access to plugin workspace.
   * 
   * @return The index of action in menu, 
   */
 public static int searchForActionInMenu(JMenu menu, String perfectOptionActionId, String secondOptionActionId,
     boolean searchOnlyInEnableActions, StandalonePluginWorkspace pluginWorkspaceAccess) {
   int index = -1;
   // iterate over menu items in currentMenu
   int sizeMenu = menu.getItemCount();
   for (int i = 0; i < sizeMenu; i++) {
     JMenuItem menuItem = menu.getItem(i);

     if (menuItem != null) {
       Action action = menuItem.getAction();
       if (action != null 
           && (!searchOnlyInEnableActions || action.isEnabled())) {
         // get the actionID
         String actionID = pluginWorkspaceAccess.getOxygenActionID(action);
         if (actionID != null) {
           if (perfectOptionActionId != null && perfectOptionActionId.equals(actionID)) {
             index = i;
             // The perfect option was found. Break here
             break;
           } else if (secondOptionActionId != null && secondOptionActionId.equals(actionID)) {
             // Save this position but continue to search for the perfect options
             index = i;
           }
         }
       }
     }
   }
   return index;
 
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
