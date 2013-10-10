/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * Updates editor actions according to edition mode and selection.
 */
class EditorActionUpdater implements FocusListener, SelectionListener {
	
	/**
	 * The parent Rodin Editor
	 */
	private final RodinEditor editor;

	public EditorActionUpdater(RodinEditor parent) {
		this.editor = parent;
	}

	@Override
	public void focusGained(FocusEvent e) {
		editor.clearNavigationActions();
		editor.updateActions();
	}

	@Override
	public void focusLost(FocusEvent e) {
		editor.createNavigationActions();
		editor.updateActions();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		editor.updateSelectionDependentActions();
	}
}