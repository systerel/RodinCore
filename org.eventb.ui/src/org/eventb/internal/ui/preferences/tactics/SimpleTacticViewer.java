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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author Nicolas Beauger
 *
 */
public class SimpleTacticViewer extends AbstractTacticViewer<ITacticDescriptor> {
	private Label label;
	private ITacticDescriptor desc;

	@Override
	public void createContents(Composite parent) {
		label =  new Label(parent, SWT.LEFT | SWT.WRAP);
	}
	
	@Override
	public void setInput(ITacticDescriptor desc) {
		this.desc = desc;
		if (desc == null) return;
		final StringBuilder sb = new StringBuilder();
		sb.append(desc.getTacticName());
		sb.append(":\n");
		sb.append(desc.getTacticDescription());
		label.setText(sb.toString());
		label.pack();
	}
	
	@Override
	protected Control getControl() {
		return label;
	}

	@Override
	public ITacticDescriptor getEditResult() {
		return desc;
	}
	
	@Override
	public ITacticDescriptor getInput() {
		return desc;
	}
}
