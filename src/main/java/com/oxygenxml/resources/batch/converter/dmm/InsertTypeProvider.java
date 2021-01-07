package com.oxygenxml.resources.batch.converter.dmm;

import java.util.HashMap;
import java.util.Map;

/**
 * Provider for the insert types according to ID of the insertion menu from DMM.
 * 
 * @author cosmin_duna
 */
public class InsertTypeProvider {
  
  /**
   * The id of the "Append child" submenu from DMM.
   */
  private static final String INSERT_CHILD_MENU_ID = "DITA_MAPS_MANAGER_APPEND_POP_UP";
  /**
   * The id of the "Insert before" submenu from DMM.
   */
  private static final String INSERT_BEFORE_MENU_ID = "DITA_MAPS_MANAGER_INSERT_BEFORE_POP_UP";
  /**
   * The id of the "Insert after" submenu from DMM.
   */
  private static final String INSERT_AFTER_MENU_ID = "DITA_MAPS_MANAGER_INSERT_AFTER_POP_UP";
  
  /**
   * Mapping between the ids of the submenu from DMM and the insertion types
   */
  private static Map<String, InsertType> menuIdToInsertType = new HashMap<>();
  static {
    menuIdToInsertType.put(INSERT_CHILD_MENU_ID, InsertType.INSERT_CHILD);
    menuIdToInsertType.put(INSERT_BEFORE_MENU_ID, InsertType.INSERT_BEFORE);
    menuIdToInsertType.put(INSERT_AFTER_MENU_ID, InsertType.INSERT_AFTER);
  }

  /**
   * Private constructor
   */
  private InsertTypeProvider() {
    // Avoid instantiation.
  }
  
  /**
   * Get the insert type for the given ID of the DMM menu.
   * 
   * @param dmmMenuId The ID of the DMM menu.
   * 
   * @return the insert type for the given ID of the DMM menu.
   */
  public static InsertType getInsertTypeFor(String dmmMenuId) {
    InsertType type = InsertType.NO_INSERT;
    if(menuIdToInsertType.containsKey(dmmMenuId)) {
      type = menuIdToInsertType.get(dmmMenuId);
    }
    return type;
  }
  
  /**
   * Check if the menu with the given id is an insert menu from DMM.
   *  
   * @param menuId
   * 
   * @return <code>true</code> if the menu with the given id is an insert menu from DMM,
   *          <code>false</code> otherwise.
   */
  public static boolean isInsertDmmMenuId(String menuId) {
    return menuId != null && menuIdToInsertType.containsKey(menuId);
  }
}
