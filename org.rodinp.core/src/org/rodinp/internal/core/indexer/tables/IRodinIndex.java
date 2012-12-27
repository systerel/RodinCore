/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer.tables;

import java.util.Collection;

import org.rodinp.core.IInternalElement;
import org.rodinp.internal.core.indexer.Descriptor;

public interface IRodinIndex {

	/**
	 * Gets the Descriptor corresponding to the given element. Returns
	 * <code>null</code> if such a Descriptor does not exist.
	 * 
	 * @param element
	 * @return the Descriptor of the given element, or <code>null</code> if it
	 *         does not exist.
	 */
	Descriptor getDescriptor(IInternalElement element);

	Collection<Descriptor> getDescriptors();

}