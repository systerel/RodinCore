/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for axiom sets in Event-B statically checked (SC) files.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * An axiom set contains axioms of abstractions of a context, or
 * of seen contexts (and their abstractions). Element names of axioms
 * are not unique in an axiom set. The origin of an axiom can be determined
 * by a corresponding attribute.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface ISCAxiomSet extends IInternalElement {
	public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scAxiomSet"; //$NON-NLS-1$
	
	public IAxiom[] getAxioms() throws RodinDBException;
	
}
