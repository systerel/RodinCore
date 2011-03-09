/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
@Deprecated
class CommonAttributesUtil {
	
	public static String getLabel(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getAttributeValue(EventBAttributes.LABEL_ATTRIBUTE);
	}
	
	public static void setLabel(InternalElement element, String label, IProgressMonitor monitor) throws RodinDBException {
		element.setAttributeValue(EventBAttributes.LABEL_ATTRIBUTE, label, monitor);
	}

	public static IRodinElement getSource(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE);
	}
	
	public static void setSource(InternalElement element, IRodinElement source, IProgressMonitor monitor) throws RodinDBException {
		element.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE, source, monitor);
	}
	
	public static String getComment(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE);
	}
	
	public static void setComment(InternalElement element, String label, IProgressMonitor monitor) throws RodinDBException {
		element.setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, label, monitor);
	}
	
//	public static int getSignature(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
//		return element.getAttributeValue(EventBAttributes.SIGNATURE_ATTRIBUTE, monitor);
//	}
//	
//	public static void setSignature(InternalElement element, int signature, IProgressMonitor monitor) throws RodinDBException {
//		element.setAttributeValue(EventBAttributes.SIGNATURE_ATTRIBUTE, signature, monitor);
//	}
	
	//	Attributes related to the PR and PS files

	public static int getConfidence(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE);
	}
	
	public static void setConfidence(InternalElement element, int confidence, IProgressMonitor monitor) throws RodinDBException {
		element.setAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE, confidence, monitor);
	}
	
}
