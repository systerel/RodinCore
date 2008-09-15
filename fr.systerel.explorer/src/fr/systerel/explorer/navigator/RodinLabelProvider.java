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
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.*;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.poModel.Machine;
import fr.systerel.explorer.poModel.POContainer;
import fr.systerel.explorer.poModel.ProofObligation;

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
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		if (element instanceof IPSStatus) {
			IPSStatus status = ((IPSStatus) element);
			return EventBImage.getPRSequentImage(status);
		}
		if (element instanceof IRodinElement) {
			return EventBImage.getRodinImage((IRodinElement) element);
			
		} else if (element instanceof TreeNode) {
			TreeNode<?> node = (TreeNode<?>) element;
			
			if (node.getType().equals(IInvariant.ELEMENT_TYPE)) {
				
				return EventBImage.getImage(IEventBSharedImages.IMG_INVARIANT);
			}
			if (node.getType().equals(ITheorem.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_THEOREM);
			}
			if (node.getType().equals(IEvent.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_EVENT);
			}
			if (node.getType().equals(IVariable.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_VARIABLE);
			}
			if (node.getType().equals(IAxiom.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_AXIOM);
			}
			if (node.getType().equals(ICarrierSet.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_CARRIER_SET);
			}
			if (node.getType().equals(IConstant.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_CONSTANT);
			}
			
			return null;
		} else if (element instanceof ProofObligation) {
			IPSStatus status = ((ProofObligation) element).getIPSStatus();
			if (status != null) {
				return EventBImage.getPRSequentImage(status);
			}
		} else if (element instanceof POContainer) {
			return EventBImage.getImage(IEventBSharedImages.IMG_DISCHARGED);
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
		
	
		} else if (obj instanceof ProofObligation) {
			return ((ProofObligation) obj).getName();
		
	
		} else if (obj instanceof POContainer) {
			return POContainer.DISPLAY_NAME;
			
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
