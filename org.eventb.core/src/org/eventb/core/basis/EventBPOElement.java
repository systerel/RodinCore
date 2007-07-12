/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.internal.core.Util;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBPOElement extends EventBElement {

	public EventBPOElement(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	private boolean isPlainPOFile(IPOFile file) {
		return file.getPOFile().equals(file);
	}

	protected IRodinElement getTranslatedAttributeValue(IAttributeType.Handle attrType) 
	throws RodinDBException{
		IRodinElement element = getAttributeValue(attrType);
		
		if (element instanceof IInternalElement) {
			
			IInternalElement iElement = (IInternalElement) element;
			
			IPOFile file = (IPOFile) getRodinFile();
		
			if (isPlainPOFile(file)) {
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
			
			IPOFile iFile = (IPOFile) iElement.getRodinFile();
			
			if (isPlainPOFile(iFile)) {
				setAttributeValue(attrType, element, monitor);
			} else {
				setAttributeValue(attrType, iElement.getSimilarElement(iFile.getPOFile()), monitor);
			}
			
		} else
			throw Util.newRodinDBException(
					"Internal PO handle translation only possible for internal elements");
	}

}
