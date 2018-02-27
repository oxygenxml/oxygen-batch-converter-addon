package com.oxygenxml.resources.batch.converter.proxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import com.oxygenxml.resources.batch.converter.plugin.BatchConverterPluginUtil;
import com.oxygenxml.resources.batch.converter.translator.Tags;
import com.oxygenxml.resources.batch.converter.translator.Translator;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * InvocationHandler for ProjectPopupMenuCustomizer
 * 
 * @author Cosmin Duna
 *
 */
public class ProjectPopupMenuCustomizerInvocationHandler implements java.lang.reflect.InvocationHandler {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ProjectPopupMenuCustomizerInvocationHandler.class);

	/**
	 * PluginWorkspaceAccess
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * The action id of predecessor item.
	 */
	private static final String PREDECESSOR_ITEM_ACTION_ID = "Project/Compare";

	/**
	 * Actions to be add.
	 */
	private Map<String, List<Action>> actions;

	/**
	 * Translator
	 */
	private Translator translator;
	
	/**
	 * Constructor
	 * 
	 * @param checkerDocBook
	 *          The action that open the DocBook checker.
	 */
	public ProjectPopupMenuCustomizerInvocationHandler(StandalonePluginWorkspace pluginWorkspaceAccess,
			Map<String, List<Action>> actions, Translator translator) {

		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.actions = actions;
		this.translator = translator;
	}

	/**
	 * Processes a "customizePopUpMenu" method invocation on a proxy instance and
	 * returns the result.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;

		try {

			// if the method name equals with "customizePopUpMenu"
			if (method.getName().equals("customizePopUpMenu")) {
				
				// create a menu
				Menu batchConvertMenu = new Menu(translator.getTranslation(Tags.MENU_TEXT, ""));

				// Add actions in menu.
				batchConvertMenu = BatchConverterPluginUtil.addActionsInMenu(batchConvertMenu, actions);
				
				// cast the args[0] at JPopupMenu
				JPopupMenu popupMenu = (JPopupMenu) args[0];

				//get the component count
				int size = popupMenu.getComponentCount();

				//get the index of predecessor item.
				int index = 0;
				for (index = 0; index < size; index++) {

					// get the current element
					Object currentElement = popupMenu.getComponent(index);

					if (currentElement instanceof JMenuItem) {
						JMenuItem item = (JMenuItem) currentElement;

						if (PREDECESSOR_ITEM_ACTION_ID.equals(pluginWorkspaceAccess.getOxygenActionID(item.getAction()))) {
							// the predecessor index was found.
							break;
						}
					}
				}
				
				// add a separator
				popupMenu.addSeparator();

				// add menuItem at popupMenu
				popupMenu.add(batchConvertMenu, index+1);

			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return result;
	}
}