/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;

/**
 * This class manages a given context menu through its manager and simplifies it
 * by removing a list of contribution with defined IDs. This class locally
 * removes some unwanted items and is an alternative to eclipse activities, as
 * activities are unable to filter contribution provided with the old action
 * mechanism and activities run furthermore in the application scope.
 * 
 * @author Thomas Muller
 */
public class ContextMenuSimplifier implements IMenuListener2 {

	/** Retrieved from the {@link MenuManager} class */
	private static final String MENUMANAGER_KEY = "org.eclipse.jface.action.MenuManager.managerKey";

	private static final String[] filteredIdParts = { "debug", "team",
			"compareWithMenu", "replaceWithMenu", "settings" };

	private MenuManager menuManager;

	private ContextMenuSimplifier(Menu menu) {
		final Object data = menu.getData(MENUMANAGER_KEY);
		assert data instanceof MenuManager;
		menuManager = (MenuManager) data;
	}
	
	public static ContextMenuSimplifier startSimplifying(Menu menu) {
		final ContextMenuSimplifier m = new ContextMenuSimplifier(menu);
		m.simplifyMenu();
		return m;
	}

	public void simplifyMenu() {
		menuManager.addMenuListener(this);
	}

	public void finishSimplifying() {
		menuManager.removeMenuListener(this);
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		final IContributionItem[] items = manager.getItems();
		for (IContributionItem i : items) {
			final String id = i.getId();
			if (id != null && containsFilteredPart(id)) {
				manager.remove(id);
			}
		}
	}

	public boolean containsFilteredPart(String id) {
		for (String part : filteredIdParts) {
			if (id.contains(part))
				return true;
		}
		return false;
	}

	@Override
	public void menuAboutToHide(IMenuManager manager) {
		// Nothing to do
	}

}
