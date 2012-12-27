/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class EventBPOElement extends EventBElement {

	public EventBPOElement(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	private boolean isPlainPOFile(IPORoot root) {
		return root.getPORoot().equals(root);
	}

	protected IRodinElement getTranslatedAttributeValue(IAttributeType.Handle attrType) 
	throws RodinDBException{
		IRodinElement element = getAttributeValue(attrType);
		
		if (element instanceof IInternalElement) {
			
			IInternalElement iElement = (IInternalElement) element;
			
			IRodinFile file = getRodinFile();
			IPORoot root = (IPORoot) getRoot();
		
			if (isPlainPOFile(root)) {
				return element;
			} else {
				return iElement.getSimilarElement(file);
			}
				
		} else
			throw Util.newRodinDBException(
					"Internal PO handle translation only possible for internal elements");
	}
	
	protected void setTranslatedAttributeValue(
			IAttributeType.Handle attrType, 
			IRodinElement element, 
			IProgressMonitor monitor)
	throws RodinDBException {
		
		if (element instanceof IInternalElement) {
		
			IInternalElement iElement = (IInternalElement) element;
			
			IPORoot iRoot= (IPORoot) iElement.getRoot();
			
			if (isPlainPOFile(iRoot)) {
				setAttributeValue(attrType, element, monitor);
			} else {
				setAttributeValue(attrType, iElement.getSimilarElement(iRoot
						.getPORoot().getRodinFile()), monitor);
			}
			
		} else
			throw Util.newRodinDBException(
					"Internal PO handle translation only possible for internal elements");
	}

}
