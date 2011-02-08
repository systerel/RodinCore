/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others. 
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

import static org.rodinp.keyboard.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
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

public class RuleDetailsView extends ViewPart implements ISelectionListener, IPropertyChangeListener {

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
	private Font font;

	@Override
	public void createPartControl(final Composite parent) {
		final IWorkbenchWindow workbench = getSite().getWorkbenchWindow();
		final ISelectionService selectionService = workbench.getSelectionService();
		// add myself as a global selection listener
        selectionService.addSelectionListener(this);

		initializeControl(parent);

		font = JFaceResources.getFont(RODIN_MATH_FONT);
		JFaceResources.getFontRegistry().addListener(this);
		// prime the selection to display contents
		refreshOnSelectionChanged(getSite().getPage().getSelection());
	}

	private void initializeControl(final Composite parent) {
		final GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = ZERO;
		gl.marginWidth = ZERO;
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayout(gl);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
	}

	@Override
	public void setFocus() {
		// Nothing to do.
	}

	public void refreshContents(IProofTreeNode node) {
		if (rdp == null) {
			rdp = new RuleDetailsProvider(sc, font);
		}
		final IProofRule rule = node.getRule();
		if (rule == null) {
			sc.setVisible(false);
			return;
		}
		sc.setVisible(true);
		sc.setRedraw(false);
		final Control details = rdp.getRuleDetailsPresentation(rule);
		sc.setContent(details);
		sc.setMinSize(details.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setRedraw(true);
	}
	
	private void refreshOnSelectionChanged(ISelection selection) {		
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

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof ProofTreeUI)) {
			return;
		}
		refreshOnSelectionChanged(selection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		final IWorkbenchWindow workbench = getSite().getWorkbenchWindow();
		final ISelectionService selectionService = workbench.getSelectionService();
        selectionService.removeSelectionListener(this);
        JFaceResources.getFontRegistry().removeListener(this);
		super.dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			font = JFaceResources.getFont(RODIN_MATH_FONT);
			if (rdp == null) {
				return;
			}
			rdp.setFont(font);
			if (currentNode != null) {
				refreshContents(currentNode);
			}
		}
	}

}
