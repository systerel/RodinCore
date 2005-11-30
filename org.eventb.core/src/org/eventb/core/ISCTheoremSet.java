/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 * An axiom set contains theorems of abstractions of a context, or
 * of seen contexts (and their abstractions. Element names of theorems
 * are not unique in a theorem set. The origin of a theorem can be determined
 * by a corresponding attribute.
 */
public interface ISCTheoremSet extends IUnnamedInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scTheoremSet"; //$NON-NLS-1$
	
	public ITheorem[] getTheorems() throws RodinDBException;
}
