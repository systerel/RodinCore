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
package org.eventb.internal.ui.prover;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;

/**
 * Class creating instantiation boxes.
 * 
 * @author "Thomas Muller"
 */
public class YellowBoxMaker extends ControlMaker {

	private static final Color YELLOW = EventBSharedColor
			.getSystemColor(SWT.COLOR_YELLOW);
	private final IContentProposalProvider provider;

	public YellowBoxMaker(Composite parent, IContentProposalProvider provider) {
		super(parent);
		this.provider = provider;
	}

	@Override
	public Control makeControl(ControlHolder holder) {
		final Text text = new Text(getParent(), SWT.SINGLE);
		text.setText("     ");
		text.setBackground(YELLOW);
		ContentProposalFactory.makeContentProposal(provider, text);
		return text;
	}

}
