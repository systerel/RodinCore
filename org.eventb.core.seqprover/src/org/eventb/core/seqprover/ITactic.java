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

import org.eventb.core.seqprover.tactics.BasicTactics;


/**
 * Interface for tactics on proof trees.
 * 
 * <p>
 * Tactics are wrappers for proof tree modifications. Their purpose is to make operations
 * on proof trees convenient to implement and combine.
 * </p>
 * 
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * @see BasicTactics
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface ITactic {
	
	/**
	 * Applies this tactic to a proof tree at a specific node.
	 * 
	 * <p>
	 * The convention is that a tactic applied at a particular node is allowed to
	 * modify only the subtree rooted at that node, but this is not checked.
	 * </p>
	 * 
	 * @param ptNode
	 * 		The proof tree node at which this tactic should be applied
	 * 		
	 * @param pm
	 * 		The proof monitor to monitor the progress of the tactic
	 * 
	 * @return
	 * 		<code>null</code> iff the application was successfull.
	 * 
	 * <p>
	 * Note : The current convention for the return type is that in case the tactic 
	 * was not successful, the proof tree is not modified, and the reason for tactic
	 * failure can be read from the <code>toString()</code> method from the object
	 * returned. In the near future the return type for the tactic will be refined
	 * to return more information useful for composing tactics. Implementors should
	 * therefore not rely on the return type at this time.
	 * </p>
	 * 
	 * @see IProofMonitor
	 */
	Object apply(IProofTreeNode ptNode, IProofMonitor pm);
	
}
