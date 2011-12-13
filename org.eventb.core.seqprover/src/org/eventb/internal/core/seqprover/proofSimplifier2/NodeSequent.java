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

import java.util.Collection;

import org.eventb.core.ast.Predicate;

/**
 * 
 * Note: equals() and hashcode() must not be overridden.
 * 
 * @author Nicolas Beauger
 */
public abstract class NodeSequent extends DependSequent {

	private final DependNode node;
	protected boolean deleted = false;

	public NodeSequent(Collection<Predicate> hyps, Predicate goal, DependNode node) {
		super(hyps, goal);
		this.node = node;
	}

	public DependNode getNode() {
		return node;
	}
	
	public final void delete() {
		if (deleted) {
			return;
		}
		deleted = true;
	}
	
	protected abstract void propagateDelete();
}
