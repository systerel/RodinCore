/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover;

import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

/**
 * Common protocol for a proof tree of the sequent prover.
 * <p>
 * A proof tree is modelled, quite obviously, as a tree data structure. Its
 * nodes are instances of {@link IProofTreeNode}.
 * </p>
 * <p>
 * A proof tree is discharged if, and only if, its root node is discharged.
 * </p>
 * <p>
 * The proof tree implements the observer design pattern and clients can
 * register a listener to observe changes to the proof tree.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IProofTree {

	/**
	 * Returns the sequent that this proof tree attempts to prove.
	 * <p>
	 * This is a shortcut for <code>getRoot().getSequent()</code>.
	 * </p>
	 * 
	 * @return the sequent of this proof tree
	 */
	IProverSequent getSequent();

	/**
	 * Returns whether this proof tree has been discharged.
	 * <p>
	 * This is a shortcut for <code>getRoot().isDischarged()</code>.
	 * </p>
	 * 
	 * @return <code>true</code> iff this proof tree has been discharged
	 */
	boolean isDischarged();

	/**
	 * Returns the root node of this proof tree.
	 * 
	 * @return the root node of this proof tree
	 */
	IProofTreeNode getRoot();

	/**
	 * Adds the given listener for changes to this proof tree. Has no effect if
	 * an identical listener is already registered.
	 * <p>
	 * This listener will be notified when a change is applied to this tree.
	 * </p>
	 * 
	 * @param listener
	 *            the listener to register
	 * @see IProofTreeChangedListener
	 */
	void addChangeListener(IProofTreeChangedListener listener);

	/**
	 * Removes the given listener for changes to this proof tree. Has no effect
	 * if this listener is not registered.
	 * <p>
	 * This listener will be notified anymore when a change is applied to this
	 * tree.
	 * </p>
	 * 
	 * @param listener
	 *            the listener to unregister
	 * @see IProofTreeChangedListener
	 */
	void removeChangeListener(IProofTreeChangedListener listener);
	
	Set<Hypothesis> getUsedHypotheses();
	Set<FreeIdentifier> getUsedFreeIdents();
	
	int getConfidence();
}
