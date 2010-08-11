/*******************************************************************************
 * Copyright (c) 2010 Systerel and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.EventBUIPlugin;

/**
 * The rule details view provides informations about the rule which was applied
 * on a given proof tree node.
 * 
 * @author "Thomas Muller"
 */

public class RuleDetailsView extends ViewPart implements ISelectionListener {

	/**
	 * The identifier of the Rule Details View (value
	 * <code>"org.eventb.ui.views.RuleDetails"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.RuleDetails";

	public static final int ZERO = 0;

	private ScrolledComposite sc = null;
	private IProofTreeNode currentNode = null;
	private RuleDetailsProvider rdp = null;

	@Override
	public void createPartControl(final Composite parent) {
		// add myself as a global selection listener
		getSite().getPage().addSelectionListener(this);

		initializeControl(parent);

		// prime the selection to display contents
		selectionChanged(null, getSite().getPage().getSelection());
	}

	private void initializeControl(final Composite parent) {
		final GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = ZERO;
		gl.marginWidth = ZERO;
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayout(gl);
	}

	@Override
	public void setFocus() {
		// Nothing to do.
	}

	public void refreshContents(IProofTreeNode node) {
		if (rdp == null) {
			rdp = new RuleDetailsProvider(sc);
		}
		final IProofRule rule = node.getRule();
		if (rule == null) {
			sc.setVisible(false);
			return;
		}
		sc.setVisible(true);
		final Control details = rdp.getRuleDetailsPresentation(rule);
		sc.setContent(details);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setMinSize(details.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = ((IStructuredSelection) selection);
			final Object element = ssel.getFirstElement();
			if (element instanceof IProofTreeNode) {
				if (currentNode == element || sc.isDisposed()) {
					return;
				}
				refreshContents((IProofTreeNode) element);
			}
		}
	}

}
