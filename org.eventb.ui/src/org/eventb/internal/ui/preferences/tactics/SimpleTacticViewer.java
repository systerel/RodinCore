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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author Nicolas Beauger
 *
 */
public class SimpleTacticViewer {
	private Label label;

	public void createContents(Composite parent) {
		label =  new Label(parent, SWT.LEFT | SWT.WRAP);
	}
	
	public void setInput(ITacticDescriptor desc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(desc.getTacticName());
		sb.append(":\n");
		sb.append(desc.getTacticDescription());
		label.setText(sb.toString());
		label.pack();
	}
	
	public void dispose() {
		label.dispose();
	}

	public void show() {
		label.setVisible(true);		
	}
}
