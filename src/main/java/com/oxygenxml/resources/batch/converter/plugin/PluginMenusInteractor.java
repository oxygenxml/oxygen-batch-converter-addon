package com.oxygenxml.resources.batch.converter.plugin;

import java.awt.event.ActionEvent;

/**
 * Interactor for the menus contributed by add-on
 * 
 * @author cosmin_duna
 */
public interface PluginMenusInteractor {
  
  /**
   * Check if the action with the given event is invoked from the contextual menu of the Project view.
   * 
   * @param event The action event.
   * 
   * @return <code>true</code> if the action is invoked from the contextual menu of the Project view,
   *  <code>false</code> when is invoked from other places 
   */
  public boolean isInvokedFromProjectMenu(ActionEvent event);
}
