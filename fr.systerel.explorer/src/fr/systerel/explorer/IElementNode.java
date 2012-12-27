/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer;

import org.eventb.core.IEventBRoot;
import org.rodinp.core.IInternalElementType;

/**
 * An element node is a node in the navigator tree that is in between a
 * machine/context and its invariants, theorems, proof obligations etc.
 * @since 1.0
 * 
 */
public interface IElementNode {
	/**
	 * Returns the parent node of this node in the navigator tree. The result is
	 * either of type IMachineRoot or IContextRoot.
	 * 
	 * @return The parent of this node in the navigator tree.
	 */
	public IEventBRoot getParent();
	
	/**
	 * Returns the type of the children of this node. This describes what
	 * children are attached to this node. For Proof Obligations this is
	 * <code>IPSStatus.ELEMENT_TYPE</code>. For Invariants it is
	 * <code>IInvariant.ELEMENT_TYPE</code> and similarly for the other
	 * elements (theorems, events etc.).
	 * 
	 * @return the type of the children of this node.
	 */
	public IInternalElementType<?> getChildrenType();

	/**
	 * 
	 * @return The label for displaying this node.
	 */
	public String getLabel();
}
