/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.LabelProvider</code> and provides
 *         labels for different elements appeared in the UI
 */
public class ElementLabelProvider extends LabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		if (obj instanceof TreeNode)
			return obj.toString();
		return ElementUIRegistry.getDefault().getLabel(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object obj) {
		if (obj instanceof TreeNode)
			return getTreeNodeImage((TreeNode) obj);
		if (obj instanceof IRodinElement)
			return EventBImage.getRodinImage((IRodinElement) obj);
		return null;
	}

	/*
	 * Getting the image corresponding to a tree node
	 * <p>
	 * 
	 * @param element
	 *            a tree node
	 * @return the image for displaying corresponding to the tree node
	 */
	private Image getTreeNodeImage(TreeNode node) {

		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();

		if (node.isType(IVariable.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_VARIABLES);
		if (node.isType(IInvariant.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_INVARIANTS);
		if (node.isType(ITheorem.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_THEOREMS);
		if (node.isType(IEvent.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_EVENTS);
		if (node.isType(ICarrierSet.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_CARRIER_SETS);
		if (node.isType(IConstant.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_CONSTANTS);
		if (node.isType(IAxiom.ELEMENT_TYPE))
			return registry.get(EventBImage.IMG_AXIOMS);

		return null;
	}

}