/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IElementType;
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

	@Deprecated
	protected IRodinElement getSingletonChild(IElementType elementType,
			String message) throws RodinDBException {

		return EventBUtil.getSingletonChild(this, elementType, message);
	}

}
