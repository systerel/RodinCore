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
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
class CommonAttributesUtil {
	
	public static String getLabel(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getStringAttribute(EventBAttributes.LABEL_ATTRIBUTE, monitor);
	}
	
	public static void setLabel(InternalElement element, String label, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(EventBAttributes.LABEL_ATTRIBUTE, label, monitor);
	}

	public static IRodinElement getSource(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		String handleID = element.getStringAttribute(EventBAttributes.SOURCE_ATTRIBUTE, monitor);
		return RodinCore.create(handleID);
	}
	
	public static void setSource(InternalElement element, IRodinElement source, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(EventBAttributes.SOURCE_ATTRIBUTE, source.getHandleIdentifier(), monitor);
	}
	
	public static String getComment(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getStringAttribute(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
	}
	
	public static void setComment(InternalElement element, String label, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(EventBAttributes.COMMENT_ATTRIBUTE, label, monitor);
	}
	
	public static int getSignature(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getIntegerAttribute(EventBAttributes.SIGNATURE_ATTRIBUTE, monitor);
	}
	
	public static void setSignature(InternalElement element, int signature, IProgressMonitor monitor) throws RodinDBException {
		element.setIntegerAttribute(EventBAttributes.SIGNATURE_ATTRIBUTE, signature, monitor);
	}

}
