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

package fr.systerel.explorer.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.navigator.CommonNavigator;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.RodinCore;

import fr.systerel.explorer.poModel.ModelFactory;

/**
 * @author Maria Husmann
 *
 */
public class RodinNavigator extends CommonNavigator implements IElementChangedListener{

	/**
	 * Observe the database.
	 *
	 */
	public RodinNavigator(){
		RodinCore.addElementChangedListener(this);
	}
	
	/**
	 * Take the <code>RodinDB</code> as InitialInput and not the <code>Workspace</code>.
	 *
	 */
	@Override
	protected IAdaptable getInitialInput() {
		this.getCommonViewer().refresh();
		return RodinCore.getRodinDB();
	}

	/**
	 * React to changes in the database.
	 *
	 */
	public void elementChanged(ElementChangedEvent event) {	
		getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
			public void run() {
				if (getViewSite().getShell() != null) {
					ModelFactory.clearAll();
					getCommonViewer().refresh();
				
				}
			}});
	}

}
