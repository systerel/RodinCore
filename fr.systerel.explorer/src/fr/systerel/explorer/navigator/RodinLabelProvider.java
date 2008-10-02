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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.model.ModelElementNode;
import fr.systerel.explorer.model.ModelPOContainer;

/**
 * This class provides labels to all <code>ContentProvider</code> classes.
 * @author Maria Husmann
 * 
 *
 */
public class RodinLabelProvider implements
		ILabelProvider, IFontProvider {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
        if (element instanceof  IWorkingSet) {
        	return EventBImage.getImage(((IWorkingSet) element).getImageDescriptor(), 0);
        }
		if (element instanceof IPSStatus) {
			IPSStatus status = ((IPSStatus) element);
			return EventBImage.getPRSequentImage(status);
		}
		if (element instanceof IRodinElement) {
			return EventBImage.getRodinImage((IRodinElement) element);
			
		} else if (element instanceof TreeNode ) {
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
		} else if (element instanceof ModelElementNode) {
			ModelElementNode node = (ModelElementNode) element;
			
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
		
		} else if (element instanceof ModelPOContainer) {
			boolean discharged = !((ModelPOContainer) element).hasUndischargedPOs();
			
			if (discharged) {
				return EventBImage.getImage(IEventBSharedImages.IMG_DISCHARGED);
			} else{
				return EventBImage.getImage(IEventBSharedImages.IMG_PENDING);
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
        if (obj instanceof  IWorkingSet) {
            return ((IWorkingSet) obj).getName();
        }
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
		
	
		} else if (obj instanceof ModelPOContainer) {
			return ModelPOContainer.DISPLAY_NAME;
			
		} else if (obj instanceof ModelElementNode) {
			return ((ModelElementNode) obj).getName();
		
		} else if (obj instanceof IContainer) {
			return ((IContainer) obj).getName();
		}
		return obj.toString();
	}

	public Font getFont(Object element) {
		return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
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
