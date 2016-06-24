/*******************************************************************************
 * Copyright (c) 2011, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * Abstract class for views that display information about currently selected
 * proof tree node.
 * 
 * @author Nicolas Beauger
 * 
 */
public abstract class AbstractProofNodeView extends ViewPart implements IPropertyChangeListener, IProofTreeSelectionListener {

	private Composite parentComp = null;
	private Font currentFont = null;

	/**
	 * Creates the initial control of the view.
	 * 
	 * @param parent
	 *            the parent control
	 * @param font
	 *            the mathematical font to use
	 */
	protected abstract void initializeControl(final Composite parent, Font font);

	/**
	 * Refreshes the control because a new proof tree node has been selected.
	 * 
	 * @param node
	 *            the new proof tree node to use for the display
	 */
	protected abstract void refreshContents(IProofTreeNode node);

	/**
	 * Refreshes the control with a new mathematical font.
	 * 
	 * @param font
	 *            the mathematical font to use
	 */
	protected abstract void fontChanged(Font font);
	
	@Override
	public void createPartControl(final Composite parent) {
		this.parentComp = parent;
		
		currentFont = JFaceResources.getFont(RODIN_MATH_FONT);

		initializeControl(parent, currentFont);

		JFaceResources.getFontRegistry().addListener(this);
		final ProofTreeSelectionService treeSelService = ProofTreeSelectionService.getInstance();
		treeSelService.addListener(this);
		// prime the selection to display contents
		final IProofTreeNode currentNode = treeSelService.getCurrentNode();
		if (currentNode != null) {
			refreshOnSelectionChanged(currentNode);
		}
	}

	@Override
	public void setFocus() {
		// Nothing to do.
	}

	private void refreshOnSelectionChanged(IProofTreeNode newNode) {
		if (parentComp == null || parentComp.isDisposed()) {
			return;
		}
		refreshContents(newNode);
	}

	@Override
	public void nodeChanged(IProofTreeNode newNode) {
		refreshOnSelectionChanged(newNode);
	}
	
	@Override
	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		super.dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (parentComp == null || parentComp.isDisposed()) {
			return;
		}
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			currentFont = JFaceResources.getFont(RODIN_MATH_FONT);
			fontChanged(currentFont);
		}
	}

}
