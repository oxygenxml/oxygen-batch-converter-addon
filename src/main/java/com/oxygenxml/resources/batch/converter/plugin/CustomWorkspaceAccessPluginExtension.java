package com.oxygenxml.resources.batch.converter.plugin;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

/**
 * Plugin extension - workspace access extension.
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {

	/**
	 * The id of menu where converter action will be place.
	 */
	private final static String MENU_ID = "Tools";

	/**
	 * The preceding menu item.
	 */
	private final static String PRECEDING_MENU_ITEM_ACTION_ID = "XML_to_JSON";

	JMenu batchConvertMenuToolbar;
	private Translator translator = new OxygenTranslator();

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {

		// List with actions.
		final List<Action> actions = createActionsList(pluginWorkspaceAccess);

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
	 * @param actionsToAdd
	 *          List with actions to add.
	 * @param pluginWorkspaceAccess
	 *          The oxygen PluginWorkspaceAccess.
	 */
	private void addActionsInMenuBar(JMenuBar mainMenuBar, List<Action> actionsToAdd,
			StandalonePluginWorkspace pluginWorkspaceAccess) {

		batchConvertMenuToolbar = new JMenu(translator.getTranslation(Tags.MENU_TEXT, ""));

		//add actions in batchConvertMenuToolbar 
		int size = actionsToAdd.size();
		for (int i = 0; i < size; i++) {
			batchConvertMenuToolbar.add(new JMenuItem(actionsToAdd.get(i)));
		}

		// get the number of items in MenuBar
		int menuBarSize = mainMenuBar.getMenuCount();

		// iterate over menus in menuBar
		for (int j = 0; j < menuBarSize; j++) {

			// get the menu with index j
			JMenu currentMenu = mainMenuBar.getMenu(j);

			if (currentMenu != null) {
				//iterate over menu items in currentMenu 
				int sizeMenu = currentMenu.getItemCount();
				for (int i = 0; i < sizeMenu; i++) {

					JMenuItem menuItem = currentMenu.getItem(i);

					if (menuItem != null) {
						Action action = menuItem.getAction();
						if (action != null) {
							// get the actionID
							String actionID = pluginWorkspaceAccess.getOxygenActionID(action);

							// The actionId is in format: menuNameId/menuItemActionID
							int indexOfSlash = actionID.indexOf("/");

							// check the menuNameId
							if (MENU_ID.equals(actionID.substring(0, indexOfSlash))) {

								// the menuNameId is MENU_ID
								// check the menuItemActionID
								if (PRECEDING_MENU_ITEM_ACTION_ID.equals(actionID.substring(indexOfSlash + 1))) {
									// the MenuIdemActionId is PRECEDING_MENU_ITEM_ACTION_ID.
									// add the action after this index.
									currentMenu.add(batchConvertMenuToolbar, i + 1);

									// break the loops.
									j = menuBarSize;
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

		}
	}

	/**
	 * Create the Swing action which shows the converter according to converter type.
	 * 
	 * @param convertorType The type of converter.
	 * @param pluginWorkspaceAccess
	 *          The plugin workspace access.
	 * @return The converter action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createConvertorAction(final String converterType, 
			final StandalonePluginWorkspace pluginWorkspaceAccess) {
		
		return new AbstractAction(translator.getTranslation(Tags.MENU_ITEM_TEXT, converterType)) {
			@Override
			public void actionPerformed(ActionEvent actionevent) {

				List<File> selectedFile = new ArrayList<File>();

				JMenuItem menuItemAction = (JMenuItem) (actionevent.getSource());

				// if is not JMenu from Toolbar
				if (!batchConvertMenuToolbar.equals(((JPopupMenu) menuItemAction.getParent()).getInvoker())) {
					// get the selectedFile from ProjectManager
					selectedFile = ProjectManagerEditor.getSelectedFiles(pluginWorkspaceAccess, converterType);
				}

				ConverterDialog convertorDialog = new ConverterDialog(converterType, selectedFile,
						(JFrame) pluginWorkspaceAccess.getParentFrame(), translator);

			}
		};
	}

	/**
	 * Create a list with Swing actions for all converters.
	 * @param pluginWorkspaceAccess
	 * @return
	 */
	private List<Action> createActionsList(StandalonePluginWorkspace pluginWorkspaceAccess) {
		List<Action> toReturn = new ArrayList<Action>();
		toReturn.add(createConvertorAction(ConverterTypes.HTML_TO_DITA, pluginWorkspaceAccess));
		toReturn.add(createConvertorAction(ConverterTypes.HTML_TO_XHTML, pluginWorkspaceAccess));
		toReturn.add(createConvertorAction(ConverterTypes.MD_TO_XHTML, pluginWorkspaceAccess));
		toReturn.add(createConvertorAction(ConverterTypes.MD_TO_DITA, pluginWorkspaceAccess));
		toReturn.add(createConvertorAction(ConverterTypes.XML_TO_JSON, pluginWorkspaceAccess));
		toReturn.add(createConvertorAction(ConverterTypes.JSON_TO_XML, pluginWorkspaceAccess));
		//root problem 
		//toReturn.add(createConvertorAction(ConverterTypes.MD_TO_DB5, pluginWorkspaceAccess));
		
		return toReturn;
	}

}