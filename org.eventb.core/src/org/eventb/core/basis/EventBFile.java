/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBFile extends RodinFile {

	protected EventBFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	protected IRodinElement getSingletonChild(String elementType, String message) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(elementType);
		if(list.size() == 1)
			return list.get(0);
		else if (list.size() == 0)
			return null;
		else
			throw Util.newRodinDBException(message, this);
	}


}
