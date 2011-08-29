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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractTacticViewer<T extends ITacticDescriptor> {

	protected abstract Control getControl();

	public abstract void createContents(Composite parent);
	
	public abstract void setInput(T desc);
	
	public void dispose() {
		final Control control = getControl();
		if (control != null) {
			control.dispose();
		}
	}

	public void show() {
		final Control control = getControl();
		if (control != null) {
			control.setVisible(true);
		}
	}

	public void hide() {
		final Control control = getControl();
		if (control != null) {
			control.setVisible(false);
		}
	}
}
