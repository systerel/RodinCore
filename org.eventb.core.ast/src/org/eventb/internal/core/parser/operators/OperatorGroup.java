/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser.operators;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.extension.CycleError;

public class OperatorGroup {
	// TODO use BitSet for matrices
	// idea: have 2 kinds of data structures:
	// - 1 for initialization, with resizable objects
	// - 1 for usage, with optimized data structures
	// After initialization, compute fixed data structures
	// reordering operator kinds so that those of a given group are contiguous
	// having a constant offset per group to translate global kind to local
	// group arrays use local indexes (starting from 0) 
	// Relation: int + BitSet (group offset + relation)
	// the BitSet uses x+size*y
	private final Set<Integer> allOperators = new HashSet<Integer>();
	private final Relation<Integer> compatibilityRelation = new Relation<Integer>();
	private final Closure<Integer> operatorPriority = new Closure<Integer>();
	private final Set<Integer> associativeOperators = new HashSet<Integer>();
	private final Set<Integer> spacedOperators = new HashSet<Integer>();

	private final String id;

	public OperatorGroup(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Set<Integer> getAllOperators() {
		return allOperators;
	}

	public Relation<Integer> getCompatibilityRelation() {
		return compatibilityRelation;
	}
	
	public Closure<Integer> getOperatorPriority() {
		return operatorPriority;
	}
	
	public Set<Integer> getAssociativeOperators() {
		return associativeOperators;
	}
	
	public Set<Integer> getSpacedOperators() {
		return spacedOperators;
	}
	
	public void add(Integer a) {
		allOperators.add(a);
	}

	private void checkKnown(Integer... ops) {
		for (Integer op : ops) {
			if (!allOperators.contains(op)) {
				throw new IllegalArgumentException("unknown operator " + op);
			}
		}
	}
	
	/**
	 * Adds a compatibility between a and b.
	 * 
	 * @param a
	 *            an operator kind
	 * @param b
	 *            an operator kind
	 */
	public void addCompatibility(Integer a, Integer b) {
		checkKnown(a, b);
		compatibilityRelation.add(a, b);
	}

	/**
	 * Adds a self compatibility for the given operator and records it as
	 * associative.
	 * 
	 * @param a
	 *            an operator kind
	 */
	public void addAssociativity(Integer a) {
		checkKnown(a);
		compatibilityRelation.add(a, a);
		associativeOperators.add(a);
	}

	public void addPriority(Integer a, Integer b)
			throws CycleError {
		checkKnown(a, b);
		operatorPriority.add(a, b);
	}
	
	@Override
	public String toString() {
		return id;
	}

	public void setSpaced(Integer kind) {
		checkKnown(kind);
		spacedOperators.add(kind);
	}

}