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

package fr.systerel.explorer.navigator.contentProviders;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelElementNode;
import fr.systerel.explorer.model.ModelPOContainer;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * This class provides labels to all <code>ContentProvider</code> classes.
 * @author Maria Husmann
 * 
 *
 */
public class RodinLabelProvider implements
		ILabelProvider{

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {

		if (element instanceof IPSStatus) {
			IPSStatus status = ((IPSStatus) element);
			return EventBImage.getPRSequentImage(status);
		}
		if (element instanceof IRodinElement) {
			return EventBImage.getRodinImage((IRodinElement) element);
			
		} else if (element instanceof IElementNode) {
			IElementNode node = (IElementNode) element;
			
			if (node.getChildrenType().equals(IInvariant.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_INVARIANT);
			}
			if (node.getChildrenType().equals(ITheorem.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_THEOREM);
			}
			if (node.getChildrenType().equals(IEvent.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_EVENT);
			}
			if (node.getChildrenType().equals(IVariable.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_VARIABLE);
			}
			if (node.getChildrenType().equals(IAxiom.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_AXIOM);
			}
			if (node.getChildrenType().equals(ICarrierSet.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_CARRIER_SET);
			}
			if (node.getChildrenType().equals(IConstant.ELEMENT_TYPE)) {
				return EventBImage.getImage(IEventBSharedImages.IMG_CONSTANT);
			}
			if (node.getChildrenType().equals(IPSStatus.ELEMENT_TYPE)) {
				ModelPOContainer parent = (ModelPOContainer) ((ModelElementNode) node).getModelParent();
				boolean discharged = !parent.hasUndischargedPOs();
				
				if (discharged) {
					return EventBImage.getImage(IEventBSharedImages.IMG_DISCHARGED);
				} else{
					return EventBImage.getImage(IEventBSharedImages.IMG_PENDING);
				}
			}
		
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
		if (obj instanceof ILabeledElement) {
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
		
	
		} else if (obj instanceof ModelPOContainer) {
			return ModelPOContainer.DISPLAY_NAME;
			
		} else if (obj instanceof IElementNode) {
			return ((IElementNode) obj).getLabel();
		
		} else if (obj instanceof IContainer) {
			return ((IContainer) obj).getName();
		}
		return obj.toString();
	}

//	public Font getFont(Object element) {
//		return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
//	}
	
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
