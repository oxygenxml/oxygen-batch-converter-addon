package com.oxygenxml.html.convertor.proxy;

import java.lang.reflect.Method;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;


/**
 * InvocationHandler for ProjectPopupMenuCustomizer
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
	 * Constructor
	 * @param checkerDocBook The action that open the DocBook checker.
	 */
	public ProjectPopupMenuCustomizerInvocationHandler(Action checkerDocBook) {
		this.checkerDocBook = checkerDocBook;
	}

	/**
	 * Processes a "customizePopUpMenu" method invocation on a proxy instance and returns the result.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			
			// if the method name equals with "customizePopUpMenu"
			if (method.getName().equals("customizePopUpMenu")) {
				//cast the args[0] at JPopupMenu
				JPopupMenu popupMenu = (JPopupMenu) args[0];
				
				//item to add in popupMenu
				JMenuItem projectMenuItem = new JMenuItem();

				//set action on MenuItem
				projectMenuItem.setAction(checkerDocBook);
				
				//add a separator
				popupMenu.addSeparator();

				//add menuItem at popupMenu
				popupMenu.add(projectMenuItem);
			}

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return result;
	}
}