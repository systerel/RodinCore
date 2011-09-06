/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractTacticViewer<T extends ITacticDescriptor> extends ContentViewer {

	public abstract void createContents(Composite parent);
	
	public abstract void setInput(T desc);
	
	/**
	 * Returns the value of the descriptor with currently displayed state.
	 * <p>
	 * If there are changes, a new descriptor is returned. If there are no
	 * changes, the input instance is returned. Therefore, it is possible to
	 * determine if changes occurred by comparing (==) input and edit result.
	 * </p>
	 * 
	 * @return a tactic descriptor
	 */
	public abstract T getEditResult();
	
	private static boolean checkControl(Control control) {
		return control != null && !control.isDisposed();
	}

	public void dispose() {
		final Control control = getControl();
		if (checkControl(control)) {
			control.dispose();
		}
	}

	public void show() {
		final Control control = getControl();
		if (checkControl(control)) {
			control.setVisible(true);
		}
	}

	public void hide() {
		final Control control = getControl();
		if (checkControl(control)) {
			control.setVisible(false);
		}
	}
}
