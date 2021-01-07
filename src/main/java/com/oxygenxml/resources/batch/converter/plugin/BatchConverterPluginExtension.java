package com.oxygenxml.resources.batch.converter.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;
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
import com.oxygenxml.resources.batch.converter.dmm.InsertType;
import com.oxygenxml.resources.batch.converter.dmm.InsertTypeProvider;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;
import com.oxygenxml.resources.batch.converter.view.ConverterDialog;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
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
	 * The Id of the "Reference" action from the contextual menu of DMM.
	 */
	private static final String REFERENCE_ACTION_ID = "Reference";
	/**
   * The Id of the "Reference to currently edited file" action from the contextual menu of DMM.
   */
	private static final String REFERENCE_TO_CURRENT_EDITED_FILE_ACTION_ID = "Reference_to_current_edited_file";
	
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
		
		pluginWorkspaceAccess.addMenusAndToolbarsContributorCustomizer(new MenusAndToolbarsContributorCustomizer() {
		  @Override
		  public void customizeDITAMapPopUpMenu(JPopupMenu popUp, WSDITAMapEditorPage ditaMapEditorPage) {
		    enrichDmmContextualMenu(popUp, pluginWorkspaceAccess);
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
          indexToInsertInTools = searchLocationInToolsMenu(currentMenu, pluginWorkspaceAccess);
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
   * Enrich contextul menu from DMM with plugin actions.
   *
   * @param popUpMenu             The contextual menu from DMM.
   * @param pluginWorkspaceAccess Access for the plugin workspace
   */
  private void enrichDmmContextualMenu(JPopupMenu popUpMenu, StandalonePluginWorkspace pluginWorkspaceAccess) {
    int menuComponentsCount = popUpMenu.getComponentCount();
    int menuIndex = -1;
    for (int i = 0; i < menuComponentsCount; i++) {
      Component component = popUpMenu.getComponent(i);
      if(component instanceof JMenu) {
        JMenu currentMenu = (JMenu)component;
        
        String menuId = pluginWorkspaceAccess.getActionsProvider().getActionID(currentMenu);
        if(InsertTypeProvider.isInsertDmmMenuId(menuId)) {
          menuIndex = searchLocationInDmmSubmenu(currentMenu, pluginWorkspaceAccess);
          if(menuIndex != -1) {
            Menu importMenu = new Menu(translator.getTranslation(Tags.IMPORT, ""));
            InsertType insertType = InsertTypeProvider.getInsertTypeFor(menuId);
            importMenu.add(createImportActionsList(insertType, pluginWorkspaceAccess));
            currentMenu.add(importMenu, menuIndex);
          }
        }
      }
    }
  }

  /**
   * Search for actions location in the given submenu from the contextul menu of DMM.
   * 
   * @param dmmSubmenu   The submenu from DMM contextul menu.
   * @param pluginWorkspaceAccess Access to plugin workspace.
   * 
   * @return Index where the actions can be inserted, or -1 when it canot be found.
   */
  private int searchLocationInDmmSubmenu(JMenu dmmSubmenu, StandalonePluginWorkspace pluginWorkspaceAccess) {
    int index = -1;
    int sizeMenu = dmmSubmenu.getItemCount();
    for (int j = 0; j < sizeMenu; j++) {
      JMenuItem menuItem = dmmSubmenu.getItem(j);

      if (menuItem != null) {
        Action action = menuItem.getAction();
        if (action != null && action.isEnabled()) {
          // get the actionID
          String actionID = pluginWorkspaceAccess.getOxygenActionID(action);
          if (actionID != null) {
            // The actionId is in format: menuNameId/menuItemActionID
            int indexOfSlash = actionID.indexOf('/');

            if (indexOfSlash != -1 
                && REFERENCE_TO_CURRENT_EDITED_FILE_ACTION_ID.equals(actionID.substring(indexOfSlash + 1))) {
              index = j + 1;
              // This is the perfect position. Break here.
              break;
            } else if (indexOfSlash != -1 
                && REFERENCE_ACTION_ID.equals(actionID.substring(indexOfSlash + 1))) {
              index = j + 1;
            }
          }
        }
      }
    }
    return index;
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
  private int searchLocationInToolsMenu(JMenu menu, StandalonePluginWorkspace pluginWorkspaceAccess) {
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

			new ConverterDialog(converterType, InsertType.NO_INSERT, selectedFile, null,
						(JFrame) pluginWorkspaceAccess.getParentFrame(), translator);

			}
		};
	}
	
	 /**
   * Create the Swing action which shows the converter according to converter
   * type.
   * 
   * @param convertorType          The type of converter.
   * @param pluginWorkspaceAccess  The plugin workspace access.
   * 
   * @return The converter action
   */
  @SuppressWarnings("serial")
  private AbstractAction createImportAction(final String converterType, final InsertType insertType,
      final StandalonePluginWorkspace pluginWorkspaceAccess) {

    return new AbstractAction(translator.getTranslation(Tags.MENU_ITEM_TEXT, converterType)+ "...") {
      @Override
      public void actionPerformed(ActionEvent actionevent) {
        List<File> inputFiles = new ArrayList<File>();
        File outputdir = null;
        
        WSEditor currentEditorAccess = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.DITA_MAPS_EDITING_AREA);
        if(currentEditorAccess != null) {
          WSEditorPage currentPage = currentEditorAccess.getCurrentPage();
          if(currentPage instanceof WSDITAMapEditorPage) {
            WSDITAMapEditorPage dmmPage = (WSDITAMapEditorPage) currentPage;
            AuthorNode[] selectedNodes = dmmPage.getSelectedNodes(true);
            if(selectedNodes.length > 0) {
              try {
                outputdir = new File(selectedNodes[0].getXMLBaseURL().toURI()).getParentFile();
              } catch (URISyntaxException e) {
                // Do nothing
              }
            }
          }
        }
        new ConverterDialog(converterType, insertType, inputFiles, outputdir,
            (JFrame) pluginWorkspaceAccess.getParentFrame(), translator);

      }
    };
  }

  /**
   * Create the array with import actions for the DMM contextual menu.
   * 
   * @param insertType            The insert location according to selection from the DMM.
   * @param pluginWorkspaceAccess Access for the plugin workspace.
   * 
   * @return the array with import actions for the DMM contextual menu.
   */
  private Action[] createImportActionsList(InsertType insertType, StandalonePluginWorkspace pluginWorkspaceAccess) {
    Action[] importActionList = new Action[4];
    importActionList[0] = createImportAction(ConverterTypes.HTML_TO_DITA, insertType, pluginWorkspaceAccess);
    importActionList[1] = createImportAction(ConverterTypes.MD_TO_DITA, insertType, pluginWorkspaceAccess);
    importActionList[2] = createImportAction(ConverterTypes.EXCEL_TO_DITA, insertType, pluginWorkspaceAccess);
    importActionList[3] = createImportAction(ConverterTypes.WORD_TO_DITA, insertType, pluginWorkspaceAccess);
    return importActionList;
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