/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier2;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A proof tree node type to use for dependence computation and manipulation.
 * <p>
 * Instances of this class are compared using == operator.
 * </p>
 * @param <T> type of node elements
 * 
 * @author Nicolas Beauger
 */
public class DependNode<T> {

	// nodes on which this node depends
	private final Collection<DependNode<T>> neededNodes = new ArrayList<DependNode<T>>();

	// nodes that depend on this node
	// FIXME split between the various children
	private final Collection<DependNode<T>> dependentNodes = new ArrayList<DependNode<T>>();

	// the boolean is set to true when the node is deleted
	private boolean isDeleted = false;

	public DependNode() {
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	private void dependentDeleted(DependNode<T> node) {
		dependentNodes.remove(node);
		deleteIfUnneeded();
	}

	public void deleteIfUnneeded() {
		if (dependentNodes.isEmpty()) {
			delete();
		}
	}
	
	private void delete() {
		if (isDeleted) {
			return;
		}
		// mark deleted (before propagating)
		isDeleted = true;

		// propagate upwards
		for (DependNode<T> needed : neededNodes) {
			needed.dependentDeleted(this);
		}
		// propagate downwards
		for (DependNode<T> dependent : dependentNodes) {
			dependent.delete();
		}
	}

}
