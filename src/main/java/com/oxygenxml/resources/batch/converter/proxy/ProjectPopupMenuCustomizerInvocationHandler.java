package com.oxygenxml.resources.batch.converter.proxy;

import java.lang.reflect.Method;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
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

	private List<Action> actions;
	
	/**
	 * Constructor
	 * 
	 * @param checkerDocBook
	 *          The action that open the DocBook checker.
	 */
	public ProjectPopupMenuCustomizerInvocationHandler(StandalonePluginWorkspace pluginWorkspaceAccess,
			List<Action> actions) {

		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.actions = actions;
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
				
				JMenu batchConvertMenu = new JMenu("Batch Convert");

				int sizeList = actions.size();

				for (int i = 0; i < sizeList; i++) {
					batchConvertMenu.add(new JMenuItem(actions.get(i)));
				}

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