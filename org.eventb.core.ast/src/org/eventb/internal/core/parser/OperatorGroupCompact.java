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
package org.eventb.internal.core.parser;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.internal.core.parser.ExternalViewUtils.ExternalOpGroup;
import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;

/**
 * An optimized implementation of operator group. This type is used after
 * grammar initialization, for parsing.
 * 
 * @author Nicolas Beauger
 * 
 */
public class OperatorGroupCompact {

	private final String id;
	private final int firstKind;
	private final int lastKind;

	private final Matrix compatibilityRelation;
	private final Matrix operatorPriority;
	private final BitSet associativeOperators;
	private final BitSet spacedOperators;

	public OperatorGroupCompact(String id, int firstKind, int lastKind,
			Matrix compatibilityRelation, Matrix operatorPriority,
			BitSet associativeOperators, BitSet spacedOperators) {
		this.id = id;
		this.firstKind = firstKind;
		this.lastKind = lastKind;
		this.compatibilityRelation = compatibilityRelation;
		this.operatorPriority = operatorPriority;
		this.associativeOperators = associativeOperators;
		this.spacedOperators = spacedOperators;
	}

	public String getId() {
		return id;
	}

	public int getFirstKind() {
		return firstKind;
	}

	public int getLastKind() {
		return lastKind;
	}

	private void checkKnown(int... ops) {
		for (int op : ops) {
			if (op < firstKind || op > lastKind) {
				throw new IllegalArgumentException("unknown operator " + op
						+ " in group " + id);
			}
		}
	}

	public boolean hasLessPriority(int a, int b) {
		checkKnown(a, b);
		return operatorPriority.get(a, b);
	}

	public boolean isCompatible(int a, int b) {
		checkKnown(a, b);
		return compatibilityRelation.get(a, b) || operatorPriority.get(a, b)
				|| operatorPriority.get(b, a);
	}

	public boolean isAssociative(int a) {
		checkKnown(a);
		return associativeOperators.get(a);
	}

	public boolean isSpaced(int kind) {
		checkKnown(kind);
		return spacedOperators.get(kind);
	}

	public IOperatorGroup asExternalView(Instantiator<Integer, IOperator> inst) {

		final Set<IOperator> extOpers = new HashSet<IOperator>();
		for (int i = firstKind; i <= lastKind; i++) {
			extOpers.add(inst.instantiate(i));

		}
		final Map<IOperator, Set<IOperator>> extPrio = operatorPriority
				.toRelationMap(inst);
		final Map<IOperator, Set<IOperator>> extCompat = compatibilityRelation
				.toRelationMap(inst);

		final Set<IOperator> extAssoc = toSet(associativeOperators, inst);
		return new ExternalOpGroup(id, extOpers, extPrio, extCompat, extAssoc);
	}

	private Set<IOperator> toSet(BitSet bitSet,
			Instantiator<Integer, IOperator> inst) {
		final Set<IOperator> set = new HashSet<IOperator>();
		for (int i = 0; i < bitSet.size(); i++) {
			if (bitSet.get(i)) {
				final IOperator op = inst.instantiate(i + firstKind);
				set.add(op);
			}
		}
		return set;
	}

	@Override
	public String toString() {
		return id;
	}

}
