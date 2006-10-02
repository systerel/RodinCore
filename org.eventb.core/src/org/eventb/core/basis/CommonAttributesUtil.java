/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
class CommonAttributesUtil {
	
	public static String LABEL_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".label";
	public static String SOURCE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".source";
	public static String BAG_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".bag";
	public static String COMMENT_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".comment";
	public static String SIGNATURE_ATTRIBUTE = EventBPlugin.PLUGIN_ID + ".signature";
	
	public static String getLabel(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getStringAttribute(LABEL_ATTRIBUTE, monitor);
	}
	
	public static void setLabel(InternalElement element, String label, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(LABEL_ATTRIBUTE, label, monitor);
	}

	public static IRodinElement getSource(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		String handleID = element.getStringAttribute(SOURCE_ATTRIBUTE, monitor);
		return RodinCore.create(handleID);
	}
	
	public static void setSource(InternalElement element, IRodinElement source, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(SOURCE_ATTRIBUTE, source.getHandleIdentifier(), monitor);
	}
	
	public static String getBag(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getStringAttribute(BAG_ATTRIBUTE, monitor);
	}
	
	public static void setBag(InternalElement element, String bag, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(BAG_ATTRIBUTE, bag, monitor);
	}

	public static String getComment(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getStringAttribute(COMMENT_ATTRIBUTE, monitor);
	}
	
	public static void setComment(InternalElement element, String label, IProgressMonitor monitor) throws RodinDBException {
		element.setStringAttribute(COMMENT_ATTRIBUTE, label, monitor);
	}
	
	public static int getSignature(InternalElement element, IProgressMonitor monitor) throws RodinDBException {
		return element.getIntegerAttribute(SIGNATURE_ATTRIBUTE, monitor);
	}
	
	public static void setSignature(InternalElement element, int signature, IProgressMonitor monitor) throws RodinDBException {
		element.setIntegerAttribute(SIGNATURE_ATTRIBUTE, signature, monitor);
	}

}
