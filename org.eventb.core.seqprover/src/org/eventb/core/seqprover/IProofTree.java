/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;


/**
 * Common protocol for a proof tree of the sequent prover.
 * <p>
 * A proof tree is modelled, quite obviously, as a tree data structure. Its
 * nodes are instances of {@link IProofTreeNode}.
 * </p>
 * <p>
 * A proof tree is closed if, and only if, its root node is closed.
 * </p>
 * <p>
 * The proof tree implements the observer design pattern and clients can
 * register a listener to observe changes to the proof tree.
 * </p>
 * <p>
 * Proof trees are created through the prover factory using
 * {@link ProverFactory#makeProofTree(IProverSequent, Object)}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Farhad Mehta
 * @author Laurent Voisin
 * @since 1.0
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
	 * Returns whether or not this proof tree is closed.
	 * <p>
	 * This is a shortcut for <code>getConfidence() > IConfidence.PENDING</code>.
	 * </p>
	 * 
	 * @return <code>true</code> iff this proof tree is closed
	 */
	boolean isClosed();
	
	/**
	 * Returns whether or not a proof has been attempted.
	 * <p>
	 * A proof is said to be attempted iff the root node of the proof tree is not open.
	 * </p>
	 * 
	 * @return <code>true</code> iff a proof has been attempted.
	 */
	boolean proofAttempted();


	/**
	 * Returns the origin of the root node of this proof tree.
	 * 
	 * @return the origin of this proof tree, might be <code>null</code>
	 */
	Object getOrigin();

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
	
	/**
	 * Returns the dependency information for this proof tree.
	 * (see {@see IProofDependencies})
	 * <p>
	 * Note that dependencies may change after proof tree modification.
	 * To get accurate information this method needs to be re-called after 
	 * any modification of the proof tree.
	 * </p>
	 * @return the dependency information for this proof tree.
	 */
	IProofDependencies getProofDependencies();
	
	/**
	 * Returns the confidence of this proof tree.
	 * <p>
	 * A proof tree is considered unattempted if its root is open and is not 
	 * commented. Otherwise, it is the confidence of the root proof tree node. 
	 * </p>
	 * @return the confidence of this proof tree (see {@see IConfidence})
	 */
	int getConfidence();
	
	
	/**
	 * Run a batch operation as an atomic operation. There will be only one 
     * delta sent after the modification of the proof tree.
	 * <p>
	 * 
	 * @param op
	 *            a Runnable
	 */
	void run(Runnable op);
}
