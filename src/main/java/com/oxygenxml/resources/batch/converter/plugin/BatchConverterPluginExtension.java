package com.oxygenxml.resources.batch.converter.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.view.ConverterDialog;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.actions.MenusAndToolbarsContributorCustomizer;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * Plugin extension - Resources Batch Converter
 */
public class BatchConverterPluginExtension implements WorkspaceAccessPluginExtension {

	/**
	 * The id of Tools menu where converter action will be place.
	 */
	private static final String TOOLS_MENU_ID = "Tools";
	
	 /**
   * The id of Import sub-menu where converter action will be place.
   */
  private static final String IMPORT_MENU_ID = "Import";

	/**
	 * The preceding menu item.
	 */
	private static final String PRECEDING_TOOLS_MENU_ITEM_ACTION_ID = "XML_to_JSON";

	/**
	 * The new succeeding menu item.
	 * PRECEDING_MENU_ITEM_ACTION_ID was moved in Oxygen 22.
	 */
	private static final String NEW_SUCCEEDING_TOOLS_MENU_ITEM_ACTION_ID = "Format_and_indent_files";
	
	/**
	 * Menu that contains items with all converter actions.
	 */
	private Menu batchConvertMenu;

	/**
	 * Translator
	 */
	private Translator translator = new OxygenTranslator();
	
	/**
	 * Flag that is <code>true</code> when the actions are added in Tools toolbar.
	 */
	private boolean actionsInserted; 
	
	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
	  
	  actionsInserted = false;
	  
		// List with actions.
		final Map<String, List<Action>> convertActions = createConvertActionsMap(pluginWorkspaceAccess);

		// Create a menu with actions and add it to Oxygen
		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
			 */
			@Override
			public void customizeMainMenu(JMenuBar mainMenuBar) {
			  // Check if the actions are added. 
			  if(!actionsInserted) {
			    addConvertActionsInMenuBar(mainMenuBar, convertActions, pluginWorkspaceAccess);
			    actionsInserted = true;
			  }
			}
		});

		// add a menu with actions in contextual menu of ProjectManager
		ProjectManagerEditor.addPopUpMenuCustomizer(pluginWorkspaceAccess, convertActions, translator);

	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		// You can reject the application closing here
		return true;
	}

	/**
	 * Add the given converter actions in the given JMenuBar.
	 *
	 * @param mainMenuBar           The menuBar
	 * @param actions               Map with converter actions to add.
	 * @param pluginWorkspaceAccess The oxygen PluginWorkspaceAccess.
	 */
	private void addConvertActionsInMenuBar(JMenuBar mainMenuBar, Map<String, List<Action>> actions,
			StandalonePluginWorkspace pluginWorkspaceAccess) {

		batchConvertMenu = new Menu(translator.getTranslation(Tags.MENU_TEXT, ""));
		Menu additionalConversionsMenu = new Menu(translator.getTranslation(Tags.ADDITIONAL_CONVERSIONS, ""));
		BatchConverterPluginUtil.addActionsInMenu(batchConvertMenu, actions);
		BatchConverterPluginUtil.addActionsInMenu(additionalConversionsMenu, actions);
		
		int indexToInsertInTools = -1;
		JMenu importSubmenu = null;

		int menuBarSize = mainMenuBar.getMenuCount();
    for (int i = 0; i < menuBarSize; i++) {
      JMenu currentMenu = mainMenuBar.getMenu(i);
      if(currentMenu != null) {
        
        if(importSubmenu == null) {
          importSubmenu = searchForImportSubMenu(currentMenu, pluginWorkspaceAccess);
          if (importSubmenu != null) {
            importSubmenu.add(additionalConversionsMenu, importSubmenu.getItemCount());
          }
        }
        if (indexToInsertInTools == -1) {
          indexToInsertInTools = searchForPositionInToolsMenu(currentMenu, pluginWorkspaceAccess);
          if (indexToInsertInTools != -1) {
            currentMenu.add(batchConvertMenu, indexToInsertInTools);
          }
        }
        
        if (indexToInsertInTools != -1 && importSubmenu != null) {
          break;
        }
      }
    }
	}
	
/**
 * Search for position to insert the menu with actions in given menu.
 * 
 * @param menu                    The menu to search.
 * @param pluginWorkspaceAccess   Access to plugin workspace.
 * 
 * @return The index of menu, or 0 if the menu is good, but a position cannot be found
 *  or -1 if wasn't find the position.
 */
  private int searchForPositionInToolsMenu(JMenu menu, StandalonePluginWorkspace pluginWorkspaceAccess) {
    int index = -1;
    // iterate over menu items in currentMenu
    int sizeMenu = menu.getItemCount();
    for (int i = 0; i < sizeMenu; i++) {
      JMenuItem menuItem = menu.getItem(i);

      if (menuItem != null) {
        Action action = menuItem.getAction();
        if (action != null) {
          // get the actionID
          String actionID = pluginWorkspaceAccess.getOxygenActionID(action);

          if (actionID != null) {
            // The actionId is in format: menuNameId/menuItemActionID
            int indexOfSlash = actionID.indexOf('/');

            // check the menuNameId
            if (TOOLS_MENU_ID.equals(actionID.substring(0, indexOfSlash))) {
            	index = 0;
              // the menuNameId is MENU_ID
              // check the menuItemActionID
              if (PRECEDING_TOOLS_MENU_ITEM_ACTION_ID.equals(actionID.substring(indexOfSlash + 1))) {
                // the MenuIdemActionId is PRECEDING_MENU_ITEM_ACTION_ID.
                // this is the index
                index = i + 1;
                break;
              }
              // In Oxygen 22, the PRECEDING_MENU_ITEM_ACTION_ID was moved from this menu.
              // We need a new reference action id.
              if (NEW_SUCCEEDING_TOOLS_MENU_ITEM_ACTION_ID.equals(actionID.substring(indexOfSlash + 1))) {
                index = i - 1;
                break;
              }
              
            } else {
              // the menuNameId is not MENU_NAME
              break;
            }
          }
        }
      }
    }
    return index;
  }

  /**
   * Search for the "Import" submenu into the given menu.
   * 
   * @param menu                  The menu to search.
   * @param pluginWorkspaceAccess StandalonePluginWorkspace
   * 
   * @return The "Import" submenu, or <code>null</code> if it cannot be found.
   */
  private JMenu searchForImportSubMenu(JMenu menu, StandalonePluginWorkspace pluginWorkspaceAccess) {
    JMenu importSubmenu = null;
    Component[] menuComponents = menu.getMenuComponents();
    for (int i = 0; i < menuComponents.length; i++) {
      Component currrentComponent = menuComponents[i];
      if(currrentComponent instanceof JMenu) {
        JMenu submenu = (JMenu) currrentComponent;
        if(submenu.getItemCount() > 0) {
          Action action = submenu.getItem(0).getAction();
          if (action != null) {
            String actionID = pluginWorkspaceAccess.getOxygenActionID(action);
            // The actionId is in format: menuNameId/menuItemActionID
            int indexOfSlash = actionID.indexOf('/');

            // check the menuNameId
            if (indexOfSlash != -1 && IMPORT_MENU_ID.equals(actionID.substring(0, indexOfSlash))) {
              importSubmenu = submenu;
              break;
            }
          }
        }
      }
    }
    return importSubmenu;
  }
	
	/**
	 * Create the Swing action which shows the converter according to converter
	 * type.
	 * 
	 * @param convertorType
	 *          The type of converter.
	 * @param pluginWorkspaceAccess
	 *          The plugin workspace access.
	 * @return The converter action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createConvertorAction(final String converterType,
			final StandalonePluginWorkspace pluginWorkspaceAccess) {

		return new AbstractAction(translator.getTranslation(Tags.MENU_ITEM_TEXT, converterType)+ "...") {
			@Override
			public void actionPerformed(ActionEvent actionevent) {

				List<File> selectedFile = new ArrayList<File>();

				JMenuItem menuItemAction = (JMenuItem) (actionevent.getSource());

				// if is not JMenu from Toolbar
				if (!batchConvertMenu.equals(((JPopupMenu) menuItemAction.getParent()).getInvoker())) {
					// get the selectedFile from ProjectManager
					selectedFile = ProjectManagerEditor.getSelectedFiles(pluginWorkspaceAccess, converterType);
				}

			new ConverterDialog(converterType, selectedFile,
						(JFrame) pluginWorkspaceAccess.getParentFrame(), translator);

			}
		};
	}

	/**
	 * Create a list with Swing actions for all converters.
	 * 
	 * @param pluginWorkspaceAccess
	 * @return
	 */
	private Map<String, List<Action>> createConvertActionsMap(StandalonePluginWorkspace pluginWorkspaceAccess) {
	  Map<String, List<Action>> toReturn = new HashMap<String, List<Action>>();
		List<Action> dita = new ArrayList<Action>();
		
		dita.add(createConvertorAction(ConverterTypes.HTML_TO_DITA, pluginWorkspaceAccess));
		dita.add(createConvertorAction(ConverterTypes.MD_TO_DITA, pluginWorkspaceAccess));
		dita.add(createConvertorAction(ConverterTypes.EXCEL_TO_DITA, pluginWorkspaceAccess));
		dita.add(createConvertorAction(ConverterTypes.WORD_TO_DITA, pluginWorkspaceAccess));
		toReturn.put("ditaSection", dita);
		
		List<Action> xhtml = new ArrayList<Action>();
		xhtml.add(createConvertorAction(ConverterTypes.HTML_TO_XHTML, pluginWorkspaceAccess));
		xhtml.add(createConvertorAction(ConverterTypes.MD_TO_XHTML, pluginWorkspaceAccess));
		xhtml.add(createConvertorAction(ConverterTypes.WORD_TO_XHTML, pluginWorkspaceAccess));
		toReturn.put("xhtmlSection", xhtml);
		
		List<Action> json = new ArrayList<Action>();
		json.add(createConvertorAction(ConverterTypes.XML_TO_JSON, pluginWorkspaceAccess));
		json.add(createConvertorAction(ConverterTypes.JSON_TO_XML, pluginWorkspaceAccess));
		toReturn.put("jsonSection", json);
		
		List<Action> docbook = new ArrayList<Action>();
		docbook.add(createConvertorAction(ConverterTypes.HTML_TO_DB4, pluginWorkspaceAccess));
		docbook.add(createConvertorAction(ConverterTypes.HTML_TO_DB5, pluginWorkspaceAccess));
		docbook.add(createConvertorAction(ConverterTypes.MD_TO_DB4, pluginWorkspaceAccess));
		docbook.add(createConvertorAction(ConverterTypes.MD_TO_DB5, pluginWorkspaceAccess));
		docbook.add(createConvertorAction(ConverterTypes.WORD_TO_DB4, pluginWorkspaceAccess));
		docbook.add(createConvertorAction(ConverterTypes.WORD_TO_DB5, pluginWorkspaceAccess));
		toReturn.put("docbookSection", docbook);

		return toReturn;
	}

}