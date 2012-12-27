/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.markers;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IElementType;
import org.rodinp.core.IParent;

public interface IMarkerUIRegistry extends IMarkerRegistry {
	
	/**
	 * Get the maximum marker severity for children of the input type of the
	 * input parent.
	 * 
	 * @param parent
	 *            a Tree Node element, this must not be <code>null</code>.
	 * @param childType type of the children
	 * 
	 * @return Return the maximum severity of the markers found. Return -1 if no
	 *         markers found.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract int getMaxMarkerSeverity(IParent parent,
			IElementType<?> childType) throws CoreException;

}
