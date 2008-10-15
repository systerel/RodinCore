/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.masterDetails;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This interface needs to be implemented by all clients who wish to
 * add content to the Details part of the Navigator's Master/Details pattern.
 *
 */
public interface INavigatorDetailsTab {

	/**
	 * Create a TabItem inside <code>tabFolder</code> and return it.
	 * In the NavigatorMasterDetails framework this allows you to add
	 * a tab to the Details page.
	 * 
	 * Example implementation:
	 * <pre>
	 *	TabItem item = new TabItem (tabFolder, SWT.NONE);
	 *	//add some content to tabFolder
	 *	item.setControl(...)
	 *	item.setText ("Example");
	 *	return item;
	 * </pre>
	 * 
	 * @param tabFolder The tabFolder the tabItem should be created with.
	 * @return A TabItem that has been created with tabFolder
	 */
	public TabItem createTabItem(TabFolder tabFolder);
	
	/**
	 * Allows you to register with a selectionProvider.
	 * In the NavigatorMasterDetails framework this allows you to register
	 * for the selection in the master part.
	 * The selections provided by the master part have the interface
	 * ITreeSelection.
	 * If you are not interested in the selection, do nothing here.
	 * If you register with the <code>selectionProvider</code> 
	 * you'll need to implement a ISelectionChangedListener.
	 * Suggested implementation:
	 * <pre>
	 * 	public void registerAsListener(ISelectionProvider selectionProvider) {
	 *		selectionProvider.addSelectionChangedListener(this);
	 *  } 
	 * </pre>
	 * @param selectionProvider The selectionProvider that you can register with.
	 */
	public void registerAsListener(ISelectionProvider selectionProvider);
}
