package com.oxygenxml.resources.batch.converter.plugin;

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
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * Plugin extension - Resources Batch Converter
 */
public class BatchConverterPluginExtension implements WorkspaceAccessPluginExtension {

	/**
	 * The id of menu where converter action will be place.
	 */
	private static final String MENU_ID = "Tools";

	/**
	 * The preceding menu item.
	 */
	private static final String PRECEDING_MENU_ITEM_ACTION_ID = "XML_to_JSON";

	/**
	 * Menu that contains items with all converter actions.
	 */
	private Menu batchConvertMenu;

	/**
	 * Translator
	 */
	private Translator translator = new OxygenTranslator();

	
	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		// List with actions.
		final Map<String, List<Action>> actions = createActionsMap(pluginWorkspaceAccess);

		// Create a menu with actions and add it to Oxygen
		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
			 */
			@Override
			public void customizeMainMenu(JMenuBar mainMenuBar) {
				addActionsInMenuBar(mainMenuBar, actions, pluginWorkspaceAccess);
			}
		});

		// add a menu with actions in contextual menu of ProjectManager
		ProjectManagerEditor.addPopUpMenuCustomizer(pluginWorkspaceAccess, actions, translator);

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
	 * Add the given Actions in the given JMenuBar.
	 *
	 * @param mainMenuBar
	 *          The menuBar
	 * @param actions
	 *          List with actions to add.
	 * @param pluginWorkspaceAccess
	 *          The oxygen PluginWorkspaceAccess.
	 */
	private void addActionsInMenuBar(JMenuBar mainMenuBar, Map<String, List<Action>> actions,
			StandalonePluginWorkspace pluginWorkspaceAccess) {

		batchConvertMenu = new Menu(translator.getTranslation(Tags.MENU_TEXT, ""));

		int indexToInsert = -1;
		
		// add actions in batch converterMenu.
		BatchConverterPluginUtil.addActionsInMenu(batchConvertMenu, actions);

		// get the number of items in MenuBar
		int menuBarSize = mainMenuBar.getMenuCount();

    // iterate over menus in menuBar
    for (int i = 0; i < menuBarSize; i++) {
      // get the menu with index i
      JMenu currentMenu = mainMenuBar.getMenu(i);
      if(currentMenu != null) {
        indexToInsert = searchForPosition(currentMenu, pluginWorkspaceAccess);
        
        if (indexToInsert != -1) {
          currentMenu.add(batchConvertMenu, indexToInsert);
          break;
        }
      }
    }
	}


/**
 * Search for position to insert the menu with actions in given menu.
 * @param menu The menu to search.
 * @param pluginWorkspaceAccess StandalonePluginWorkspace
 * @return The index of menu, or -1 if wasn't find the position.
 */
  private int searchForPosition(JMenu menu, StandalonePluginWorkspace pluginWorkspaceAccess) {
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
            if (MENU_ID.equals(actionID.substring(0, indexOfSlash))) {

              // the menuNameId is MENU_ID
              // check the menuItemActionID
              if (PRECEDING_MENU_ITEM_ACTION_ID.equals(actionID.substring(indexOfSlash + 1))) {
                // the MenuIdemActionId is PRECEDING_MENU_ITEM_ACTION_ID.
                // this is the index
                index = i + 1;
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
	private Map<String, List<Action>> createActionsMap(StandalonePluginWorkspace pluginWorkspaceAccess) {
	  Map<String, List<Action>> toReturn = new HashMap<String, List<Action>>();
		List<Action> dita = new ArrayList<Action>();
		
		dita.add(createConvertorAction(ConverterTypes.HTML_TO_DITA, pluginWorkspaceAccess));
		dita.add(createConvertorAction(ConverterTypes.MD_TO_DITA, pluginWorkspaceAccess));
		toReturn.put("ditaSection", dita);
		
		List<Action> xhtml = new ArrayList<Action>();
		xhtml.add(createConvertorAction(ConverterTypes.HTML_TO_XHTML, pluginWorkspaceAccess));
		xhtml.add(createConvertorAction(ConverterTypes.MD_TO_XHTML, pluginWorkspaceAccess));
		toReturn.put("xhtmlSection", xhtml);
		
		List<Action> json = new ArrayList<Action>();
		json.add(createConvertorAction(ConverterTypes.XML_TO_JSON, pluginWorkspaceAccess));
		json.add(createConvertorAction(ConverterTypes.JSON_TO_XML, pluginWorkspaceAccess));
		toReturn.put("jsonSection", json);
		
		float oxyVersion = Float.valueOf(pluginWorkspaceAccess.getVersion());
		if(oxyVersion > (float) 19.1) {
		  List<Action> docbook = new ArrayList<Action>();
		  docbook.add(createConvertorAction(ConverterTypes.HTML_TO_DB4, pluginWorkspaceAccess));
			docbook.add(createConvertorAction(ConverterTypes.HTML_TO_DB5, pluginWorkspaceAccess));
			docbook.add(createConvertorAction(ConverterTypes.MD_TO_DB4, pluginWorkspaceAccess));
			docbook.add(createConvertorAction(ConverterTypes.MD_TO_DB5, pluginWorkspaceAccess));
			toReturn.put("docbookSection", docbook);
		}

		return toReturn;
	}

}