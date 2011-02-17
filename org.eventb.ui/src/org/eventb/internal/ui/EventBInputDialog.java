/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import static org.eventb.internal.ui.autocompletion.ContentProposalFactory.makeContentProposal;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.pm.IUserSupport;

/**
 * Input dialog class which provides math translation and content proposal.
 * 
 * @author "Thomas Muller"
 */
public class EventBInputDialog extends InputDialog {

	private final IUserSupport us;

	public EventBInputDialog(Shell parentShell, String title, String message,
			String initialValue, IUserSupport us) {
		super(parentShell, title, message, initialValue, null);
		this.us = us;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Control control = super.createDialogArea(parent);
		final Text text = getText();
		new EventBMath(text);
		makeContentProposal(text, us);
		return control;
	}

}
