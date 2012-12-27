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
 * A proof tree delta describes changes in a proof tree between two discrete
 * points in time. Given a delta, clients can access all proof tree node that
 * have changed.
 * <p>
 * Deltas have different flags depending on the kind of change they represent.
 * The list below summarizes each flag (as returned by <code>getFlags</code>)
 * and its meaning (see individual constants for a more detailed description):
 * <ul>
 * <li><code>COMMENT</code> - The comment of the node described by the delta 
 * has a changed. 
 * <li><code>CONFIDENCE</code> - The node described by the delta has changed
 * confidence level.
 * <li><code>CHILDREN</code> - The children of the node described by the
 * delta have changed. </li>
 * <li><code>RULE</code> - The rule associated to this node has changed. </li>
 * </ul>
 * </p>
 * <p>
 * A proof tree delta tree always starts at the root node of a proof tree.
 * </p>
 * <p>
 * <code>IProofTreeDelta</code> objects are not valid outside the dynamic
 * scope of the notification.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IProofTreeDelta {

	/**
	 * Flag indicating that the rule associated with the proof node has changed
	 * <p>
	 * The new rule of the node can be read directly from the proof tree node
	 * described by this delta.
	 * </p>
	 */
	int RULE = 0x1;
	
	/**
	 * Flag indicating that the element has changed its list of children.
	 * <p>
	 * A delta of this kind doesn't have any child (as the children either
	 * were pruned or are brand new). The new list of children should then be
	 * read directly from the proof tree node described by this delta.
	 * </p>
	 */
	int CHILDREN = 0x2;

	/**
	 * Flag indicating that the proof node has a changed confidence level.
	 * <p>
	 * The new confidence level of the node can be read directly from the proof tree 
	 * node described by this delta.
	 * </p>
	 */
	int CONFIDENCE = 0x4;
	
	
	/**
	 * Flag indicating that the comment associated with the proof node has changed
	 * <p>
	 * The new comment of the node can be read directly from the proof tree node
	 * described by this delta.
	 * </p>
	 */
	int COMMENT = 0x8;
	
	
	/**
	 * Returns the node that this delta describes a change to.
	 * 
	 * @return the node that this delta describes a change to
	 */
	IProofTreeNode getProofTreeNode();
	
	/**
	 * Returns the flags of this delta: a combination of <code>STATUS</code>
	 * and <code>CHILDREN</code>.
	 * 
	 * @return the flags of this delta
	 */
	int getFlags();

	/**
	 * Returns deltas for the children which have changed.
	 * <p>
	 * Note that this method returns an empty array if this delta has flag
	 * <code>CHILDREN</code> or <code>CONTENTS</code> set.
	 * </p>
	 * 
	 * @return deltas for the children which have changed
	 */
	public IProofTreeDelta[] getChildren();

}
