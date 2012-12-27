/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
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
		holder.addListener(new YellowBoxKeyListener(text, holder));
		ContentProposalFactory.makeContentProposal(provider, text);
		return text;
	}

	private static class YellowBoxKeyListener extends KeyAdapter {

		private final ControlHolder holder;
		private final Text owner;

		public YellowBoxKeyListener(Text owner, ControlHolder holder) {
			this.holder = holder;
			this.owner = owner;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.character == SWT.CR && owner.isFocusControl()) {
				final PredicateRow row = holder.getRow();
				row.instantiate();
			} else {
				FormToolkit.ensureVisible(owner);
				owner.showSelection();
			}
		}
	}

}
