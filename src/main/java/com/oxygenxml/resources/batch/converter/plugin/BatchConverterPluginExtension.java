package com.oxygenxml.resources.batch.converter.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.oxygenxml.resources.batch.converter.ConverterTypes;
import com.oxygenxml.resources.batch.converter.actions.ConvertAction;
import com.oxygenxml.resources.batch.converter.actions.ImportAction;
import com.oxygenxml.resources.batch.converter.dmm.InsertType;
import com.oxygenxml.resources.batch.converter.dmm.InsertTypeProvider;
import com.oxygenxml.resources.batch.converter.translator.OxygenTranslator;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.actions.MenusAndToolbarsContributorCustomizer;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * Plugin extension - Resources Batch Converter
 */
public class BatchConverterPluginExtension implements WorkspaceAccessPluginExtension, PluginMenusInteractor{

	/**
	 * The id of Tools menu where converter action will be place.
	 */
	private static final String TOOLS_MENU_ID = "Tools";
	
	/**
   * The id of File menu where converter action will be place.
	 */
	private static final String FILE_MENU_ID = "File";
	
	 /**
   * The id of Import sub-menu where converter action will be place.
   */
  private static final String IMPORT_MENU_ID = "File/Import";

	/**
	 * The preceding menu item.
	 */
	private static final String PRECEDING_TOOLS_MENU_ITEM_ACTION_ID = "Tools/XML_to_JSON";

	/**
	 * The new succeeding menu item.
	 * PRECEDING_MENU_ITEM_ACTION_ID was moved in Oxygen 22.
	 */
	private static final String NEW_SUCCEEDING_TOOLS_MENU_ITEM_ACTION_ID = "Tools/Format_and_indent_files";
 
	/**
	 * The Id of the "Reference" action from the contextual menu of DMM.
	 */
	private static final String REFERENCE_ACTION_ID = "ACTION_WITH_NO_SHORTCUT/Reference";
	/**
   * The Id of the "Reference to currently edited file" action from the contextual menu of DMM.
   */
	private static final String REFERENCE_TO_CURRENT_EDITED_FILE_ACTION_ID = "ACTION_WITH_NO_SHORTCUT/Reference_to_current_edited_file";
	
	/**
	 * The Batch converter menu from the Project view.
	 */
	private Menu batchConvertMenuFromProject;

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

		
		batchConvertMenuFromProject = new Menu(translator.getTranslation(Tags.MENU_TEXT));
		BatchConverterPluginUtil.addActionsInMenu(batchConvertMenuFromProject, convertActions);
		// add a menu with actions in contextual menu of ProjectManager
		ProjectManagerEditor.addPopUpMenuCustomizer(pluginWorkspaceAccess, batchConvertMenuFromProject, translator);

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

	  Menu batchConvertMenuForTools = new Menu(translator.getTranslation(Tags.MENU_TEXT));
		Menu additionalConversionsMenu = new Menu(translator.getTranslation(Tags.ADDITIONAL_CONVERSIONS));
		BatchConverterPluginUtil.addActionsInMenu(batchConvertMenuForTools, actions);
		BatchConverterPluginUtil.addActionsInMenu(additionalConversionsMenu, actions);
		
		boolean foundFileMenu = false;
	  boolean foundToolsMenu = false;

		int menuBarSize = mainMenuBar.getMenuCount();
    for (int i = 0; i < menuBarSize; i++) {
      JMenu currentMenu = mainMenuBar.getMenu(i);
      if(currentMenu != null) {
        String currentMenuId = pluginWorkspaceAccess.getActionsProvider().getActionID(currentMenu);

        if (TOOLS_MENU_ID.equals(currentMenuId)) {
          foundToolsMenu = true;
          int indexToInsertInTools = searchLocationInToolsMenu(currentMenu, pluginWorkspaceAccess);
          if(indexToInsertInTools == -1) {
            // Fallback: Add in the last position.
            indexToInsertInTools = currentMenu.getItemCount();
          }
          currentMenu.add(batchConvertMenuForTools, indexToInsertInTools);
        }

        if (FILE_MENU_ID.equals(currentMenuId)) {
          foundFileMenu = true;
          JMenu importSubmenu = BatchConverterPluginUtil.searchForSubMenu(currentMenu, IMPORT_MENU_ID, pluginWorkspaceAccess);
          if (importSubmenu != null) {
            importSubmenu.add(additionalConversionsMenu, importSubmenu.getItemCount());
          }

        }

        if (foundToolsMenu && foundFileMenu) {
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
          menuIndex = BatchConverterPluginUtil.searchForActionInMenu(currentMenu,
              REFERENCE_TO_CURRENT_EDITED_FILE_ACTION_ID, REFERENCE_ACTION_ID, true, pluginWorkspaceAccess);
          if(menuIndex != -1) {
            // Position after this action.
            menuIndex++;
            Menu importMenu = new Menu(translator.getTranslation(Tags.IMPORT));
            InsertType insertType = InsertTypeProvider.getInsertTypeFor(menuId);
            importMenu.add(createImportActionsList(insertType, pluginWorkspaceAccess));
            currentMenu.add(importMenu, menuIndex);
          }
        }
      }
    }
  }

  
/**
 * Search for position to insert the menu with actions in given Tools menu.
 * 
 * @param menu                    The Tools menu.
 * @param pluginWorkspaceAccess   Access to plugin workspace.
 * 
 * @return The index of menu, or 0 if the menu is good, but a position cannot be found
 *  or -1 if wasn't find the position.
 */
  private int searchLocationInToolsMenu(JMenu menu, StandalonePluginWorkspace pluginWorkspaceAccess) {
    int index = -1;
    index = BatchConverterPluginUtil.searchForActionInMenu(
        menu, PRECEDING_TOOLS_MENU_ITEM_ACTION_ID, null, false, pluginWorkspaceAccess);
    if(index != -1) {
      index++;
    } else {
      // In Oxygen 22, the PRECEDING_TOOLS_MENU_ITEM_ACTION_ID was moved from this menu.
      // We need a new reference action id.
      index = BatchConverterPluginUtil.searchForActionInMenu(
          menu, NEW_SUCCEEDING_TOOLS_MENU_ITEM_ACTION_ID, null, false, pluginWorkspaceAccess);
      if(index != -1) {
        index--;
      }              
    }
    return index;
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
    importActionList[0] = new ImportAction(ConverterTypes.HTML_TO_DITA, insertType, translator);
    importActionList[1] = new ImportAction(ConverterTypes.MD_TO_DITA, insertType, translator);
    importActionList[2] = new ImportAction(ConverterTypes.EXCEL_TO_DITA, insertType, translator);
    importActionList[3] = new ImportAction(ConverterTypes.WORD_TO_DITA, insertType, translator);
    return importActionList;
  }
  
	/**
	 * Create a list with Swing actions for all converters.
	 * 
	 * @param pluginWorkspaceAccess
	 * @return
	 */
	private Map<String, List<Action>> createConvertActionsMap(StandalonePluginWorkspace pluginWorkspaceAccess) {

    Map<String, List<Action>> toReturn = new LinkedHashMap<String, List<Action>>();
    List<Action> markdown = new ArrayList<Action>();
    List<Action> html = new ArrayList<Action>();
    List<Action> word = new ArrayList<Action>();
    List<Action> xmlAndJson = new ArrayList<Action>();
    
    html.add(new ConvertAction(ConverterTypes.HTML_TO_XHTML, translator, this));
    html.add(new ConvertAction(ConverterTypes.HTML_TO_DITA, translator, this));
    html.add(new ConvertAction(ConverterTypes.HTML_TO_DB4, translator, this));
    html.add(new ConvertAction(ConverterTypes.HTML_TO_DB5, translator, this));
    toReturn.put("htmlSection", html);
    
    markdown.add(new ConvertAction(ConverterTypes.MD_TO_XHTML, translator, this));
    markdown.add(new ConvertAction(ConverterTypes.MD_TO_DITA, translator, this));
    markdown.add(new ConvertAction(ConverterTypes.MD_TO_DB4, translator, this));
    markdown.add(new ConvertAction(ConverterTypes.MD_TO_DB5, translator, this));
    toReturn.put("markdownSection", markdown);
    
    word.add(new ConvertAction(ConverterTypes.WORD_TO_XHTML, translator, this));
    word.add(new ConvertAction(ConverterTypes.WORD_TO_DITA, translator, this));
    word.add(new ConvertAction(ConverterTypes.WORD_TO_DB4, translator, this));
    word.add(new ConvertAction(ConverterTypes.WORD_TO_DB5, translator, this));
    word.add(new ConvertAction(ConverterTypes.EXCEL_TO_DITA, translator, this));
    toReturn.put("wordSection", word);
    
    xmlAndJson.add(new ConvertAction(ConverterTypes.JSON_TO_XML, translator, this));
    xmlAndJson.add(new ConvertAction(ConverterTypes.XML_TO_JSON, translator, this));
    toReturn.put("xmlAndJsonSection", xmlAndJson);

    return toReturn;
	}

  /**
   * Check if the action with the given event is invoked from the contextual menu of the Project view.
   * 
   * @param event The action event.
   * 
   * @return <code>true</code> if the action is invoked from the contextual menu of the Project view,
   *  <code>false</code> when is invoked from other places 
   */
  @Override
  public boolean isInvokedFromProjectMenu(ActionEvent e) {
    boolean isInvokedFromProjectMenu = false;
    Object source = e.getSource();
    if(source instanceof JMenuItem) {
      JMenuItem menuItemAction = (JMenuItem) source;
      // if is not JMenu from Toolbar
      isInvokedFromProjectMenu = batchConvertMenuFromProject.equals(((JPopupMenu) menuItemAction.getParent()).getInvoker());
    }

    return isInvokedFromProjectMenu;
  }

}