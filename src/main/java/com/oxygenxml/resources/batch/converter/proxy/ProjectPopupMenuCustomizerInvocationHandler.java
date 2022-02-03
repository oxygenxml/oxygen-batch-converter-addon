package com.oxygenxml.resources.batch.converter.proxy;

import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger logger = LoggerFactory.getLogger(ProjectPopupMenuCustomizerInvocationHandler.class);

	/**
	 * PluginWorkspaceAccess
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * The action id of predecessor item.
	 */
	private static final String PREDECESSOR_ITEM_ACTION_ID = "Project/Compare";

	/**
	 * The menu to be add.
	 */
	private Menu menuToAdd;

	/**
	 * Constructor
	 * 
	 * @param checkerDocBook
	 *          The action that open the DocBook checker.
	 */
	public ProjectPopupMenuCustomizerInvocationHandler(StandalonePluginWorkspace pluginWorkspaceAccess,
	    Menu menuToAdd) {

		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.menuToAdd = menuToAdd;
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
				popupMenu.add(menuToAdd, index+1);

			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return result;
	}
}