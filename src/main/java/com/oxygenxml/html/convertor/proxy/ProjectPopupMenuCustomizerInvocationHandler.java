package com.oxygenxml.html.convertor.proxy;

import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * InvocationHandler for ProjectPopupMenuCustomizer
 * 
 * @author intern4
 *
 */
public class ProjectPopupMenuCustomizerInvocationHandler implements java.lang.reflect.InvocationHandler {
	/**
	 * The action that open the DocBook checker.
	 */
	private Action checkerDocBook;

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
	 * Constructor
	 * 
	 * @param checkerDocBook
	 *          The action that open the DocBook checker.
	 */
	public ProjectPopupMenuCustomizerInvocationHandler(StandalonePluginWorkspace pluginWorkspaceAccess,
			Action checkerDocBook) {

		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.checkerDocBook = checkerDocBook;
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
				for ( index = 0; index < size; index++) {
					try {
						JMenuItem item = (JMenuItem) popupMenu.getComponent(index);
						
						if(PREDECESSOR_ITEM_ACTION_ID.equals(pluginWorkspaceAccess.getOxygenActionID(item.getAction()))){
							break;
						}
					} catch (Exception e) {
					}
				}

				// item to add in popupMenu
				JMenuItem projectMenuItem = new JMenuItem();

				// set action on MenuItem
				projectMenuItem.setAction(checkerDocBook);

				// add a separator
				popupMenu.addSeparator();

				// add menuItem at popupMenu
				popupMenu.add(projectMenuItem, index+1);

			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return result;
	}
}