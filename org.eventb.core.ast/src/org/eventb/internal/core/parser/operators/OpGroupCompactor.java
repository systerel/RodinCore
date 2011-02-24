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
package org.eventb.internal.core.parser.operators;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class OpGroupCompactor {

	private final OperatorGroup initGroup;
	private final int firstKind;

	private final Instantiator<Integer, Integer> kindInst;

	public OpGroupCompactor(OperatorGroup initGroup, int firstKind,
			Instantiator<Integer, Integer> kindInst) {
		this.initGroup = initGroup;
		this.firstKind = firstKind;
		this.kindInst = kindInst;
	}

	public OperatorGroupCompact compact() {
		final Set<Integer> allOperators = initGroup.getAllOperators();
		final int opCount = allOperators.size();
		final int lastKind = firstKind + opCount - 1;

		final List<Integer> ops = new ArrayList<Integer>(allOperators);
		Collections.sort(ops);

		for (int i = 0; i < ops.size(); i++) {
			final Integer initKind = ops.get(i);
			final Integer newKind = firstKind + i;
			kindInst.setInst(initKind, newKind);
		}

		final Matrix compatibilityRelation = convert(initGroup.getCompatibilityRelation().getRelationMap(), opCount);
		final Matrix operatorPriority = convert(initGroup.getOperatorPriority().getRelationMap(), opCount);
		final BitSet associativeOperators = convert(initGroup.getAssociativeOperators(), opCount);
		final BitSet spacedOperators = convert(initGroup.getSpacedOperators(), opCount);
		
		return new OperatorGroupCompact(initGroup.getId(), firstKind, lastKind,
				compatibilityRelation, operatorPriority, associativeOperators,
				spacedOperators);
	}

	private BitSet convert(Set<Integer> set, int opCount) {
		final BitSet result = new BitSet(opCount);
		for (Integer i : set) {
			final int local = localKind(i);
			result.set(local);
		}
		return result;
	}

	private Matrix convert(Map<Integer, Set<Integer>> relationMap, int opCount) {
		final Matrix result = new Matrix(opCount, firstKind);
		for (Entry<Integer, Set<Integer>> rel : relationMap.entrySet()) {
			final int left = localKind(rel.getKey());
			for (Integer relRight : rel.getValue()) {
				final int right = localKind(relRight);
				result.set(left, right);
			}
		}
		return result;
	}

	private int localKind(int initKind) {
		return kindInst.instantiate(initKind);
	}

}
