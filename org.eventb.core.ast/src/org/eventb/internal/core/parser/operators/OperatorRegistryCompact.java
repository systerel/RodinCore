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

import static org.eventb.internal.core.parser.operators.OperatorRelationship.COMPATIBLE;
import static org.eventb.internal.core.parser.operators.OperatorRelationship.INCOMPATIBLE;
import static org.eventb.internal.core.parser.operators.OperatorRelationship.LEFT_PRIORITY;
import static org.eventb.internal.core.parser.operators.OperatorRelationship.RIGHT_PRIORITY;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IGrammar;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.internal.core.parser.operators.ExternalViewUtils.ExternalGrammar;
import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class OperatorRegistryCompact {

	private final OperatorGroupCompact[] groups;

	// first kind of each group, in ascending order
	// last element is 1 + last kind of last group
	private final int[] firstKinds;
	private final AllInOnceMap<String, Integer> idKind;
	private final Matrix groupPriority;

	public OperatorRegistryCompact(OperatorGroupCompact[] groups,
			int[] firstKinds, AllInOnceMap<String, Integer> idKind,
			Matrix groupPriority) {
		this.groups = groups;
		this.firstKinds = firstKinds;
		this.idKind = idKind;
		this.groupPriority = groupPriority;
	}

	private int getGroupIndex(int opKind) {
		for (int index = 0; index < firstKinds.length; index++) {
			if (opKind < firstKinds[index]) {
				return index - 1;
			}
		}
		return -1;
	}

	private OperatorGroupCompact getGroup(int opKind) {
		final int groupIndex = getGroupIndex(opKind);
		if (groupIndex < 0) {
			return null;
		}
		return groups[groupIndex];
	}

	public Map<Integer, String> getKindIds() {
		// not a bijective map because: { = Set Extension & Comprehension Set
		// but it is in a single group: Brace Sets
		// so one id or the other is OK
		return idKind.invert();
	}

	/**
	 * Computes operator relationship between given operator kinds.
	 * <p>
	 * Given kinds MUST be checked to be operators before calling this method.
	 * </p>
	 * 
	 * @param leftKind
	 *            the kind of the left operator
	 * @param rightKind
	 *            the kind of the right operator
	 * @return an operator relationship
	 */
	public OperatorRelationship getOperatorRelationship(int leftKind,
			int rightKind) {
		final int leftGroupIndex = getGroupIndex(leftKind);
		final int rightGroupIndex = getGroupIndex(rightKind);

		// index == 0 means GROUP0
		if (leftGroupIndex == 0 && rightGroupIndex == 0) {
			return LEFT_PRIORITY;
			// Unknown groups have a priority greater than GROUP0
		} else if (leftGroupIndex == 0) {
			return RIGHT_PRIORITY;
		} else if (rightGroupIndex == 0) {
			return LEFT_PRIORITY;
		} else if (groupPriority.get(leftGroupIndex, rightGroupIndex)) {
			return RIGHT_PRIORITY;
		} else if (groupPriority.get(rightGroupIndex, leftGroupIndex)) {
			return LEFT_PRIORITY;
		} else if (leftGroupIndex == rightGroupIndex) {
			final OperatorGroupCompact group = groups[leftGroupIndex];
			if (group.hasLessPriority(leftKind, rightKind)) {
				return RIGHT_PRIORITY;
			} else if (group.hasLessPriority(rightKind, leftKind)) {
				return LEFT_PRIORITY;
			} else if (group.isCompatible(leftKind, rightKind)) {
				return COMPATIBLE;
			} else {
				return INCOMPATIBLE;
			}
		} else {
			return LEFT_PRIORITY;
		}

	}

	public boolean hasGroup(int kind) {
		return getGroupIndex(kind) > 0;
	}

	public boolean isAssociative(int kind) {
		final OperatorGroupCompact group = getGroup(kind);
		return group.isAssociative(kind);
	}

	public boolean isSpaced(int kind) {
		final OperatorGroupCompact group = getGroup(kind);
		return group.isSpaced(kind);
	}

	public boolean isDeclared(String operatorId) {
		return idKind.containsKey(operatorId);
	}

	public IGrammar asExternalView(Instantiator<Integer, IOperator> instantiator) {
		final Instantiator<Integer, IOperatorGroup> groupInst = new Instantiator<Integer, IOperatorGroup>();
		final Set<IOperatorGroup> extGroups = new HashSet<IOperatorGroup>();
		for (int i = 0; i < groups.length; i++) {
			final IOperatorGroup groupView = groups[i]
					.asExternalView(instantiator);
			extGroups.add(groupView);
			groupInst.setInst(i, groupView);
		}
		final Map<IOperatorGroup, Set<IOperatorGroup>> extGroupPrios = groupPriority
				.toRelationMap(groupInst);
		return new ExternalGrammar(extGroups, extGroupPrios);
	}

}
