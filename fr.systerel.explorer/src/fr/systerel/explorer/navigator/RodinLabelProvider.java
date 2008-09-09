/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.navigator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.*;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * This class provides labels to all <code>ContentProvider</code> classes.
 * @author Maria Husmann
 * 
 *
 */
public class RodinLabelProvider implements
		ILabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof IRodinElement) {
			return EventBImage.getRodinImage((IRodinElement) element);
			
		} else if (element instanceof TreeNode) {
			TreeNode<?> node = (TreeNode<?>) element;
			
			if (node.getType().equals(IInvariant.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_INVARIANT_PATH).createImage();
			}
			if (node.getType().equals(ITheorem.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_THEOREM_PATH).createImage();
			}
			if (node.getType().equals(IEvent.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_EVENT_PATH).createImage();
			}
			if (node.getType().equals(IVariable.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_VARIABLE_PATH).createImage();
			}
			if (node.getType().equals(IAxiom.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_AXIOM_PATH).createImage();
			}
			if (node.getType().equals(ICarrierSet.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_CARRIER_SET_PATH).createImage();
			}
			if (node.getType().equals(IConstant.ELEMENT_TYPE)) {
				return EventBImage.getImageDescriptor(IEventBSharedImages.IMG_CONSTANT_PATH).createImage();
			}
			
			return null;
		} else if (element instanceof IContainer) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		if (obj instanceof IRodinProject) {
			return ((IRodinProject) obj).getElementName();
			
		} else if (obj instanceof IEventBFile) {
			return ((IEventBFile) obj).getBareName();
		
		} else if (obj instanceof TreeNode) {
				return ((TreeNode<?>) obj).toString();
		
		} else if (obj instanceof ILabeledElement) {
			try {
				return ((ILabeledElement) obj).getLabel();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (obj instanceof IIdentifierElement) {
			try {
				return ((IIdentifierElement) obj).getIdentifierString();
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (obj instanceof IRodinElement) {
			return ((IRodinElement) obj).getElementName();
		
	
		} else if (obj instanceof IContainer) {
			return ((IContainer) obj).getName();
		}
		return new String();
	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing
		
	}

	public void dispose() {
		// do nothing
		
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing
		
	}

}
