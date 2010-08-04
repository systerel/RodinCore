/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.COMPATIBLE;
import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.INCOMPATIBLE;
import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.LEFT_PRIORITY;
import static org.eventb.internal.core.parser.OperatorRegistry.OperatorRelationship.RIGHT_PRIORITY;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.CycleError;
import org.eventb.core.ast.extension.IGrammar;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.internal.core.parser.ExternalViewUtils.ExternalGrammar;
import org.eventb.internal.core.parser.ExternalViewUtils.ExternalOpGroup;
import org.eventb.internal.core.parser.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 *  
 */
public class OperatorRegistry {

	public static final String GROUP0 = "GROUP 0";

	/**
	 * Describes the relationship between two operators: left (on the left) and
	 * right (on the right).
	 */
	public static enum OperatorRelationship {
		LEFT_PRIORITY,       // priority(left)  > priority(right)
		RIGHT_PRIORITY,      // priority(right) > priority(left)
		COMPATIBLE,          // left then right is allowed w/o parentheses
		INCOMPATIBLE,        // no combination is allowed
	}
	
	private static final OperatorGroup GROUP_0 = new OperatorGroup(GROUP0);
	
	private static class Relation<T> {
		private final Map<T, Set<T>> maplets = new HashMap<T, Set<T>>();

		public Relation() {
			// avoid synthetic accessor emulation
		}
		
		public Map<T, Set<T>> getRelationMap() {
			return Collections.unmodifiableMap(maplets);
		}
		
		public void add(T a, T b) {
			Set<T> set = maplets.get(a);
			if (set == null) {
				set = new HashSet<T>();
				maplets.put(a, set);
			}
			set.add(b);
		}

		public boolean contains(T a, T b) {
			Set<T> set = maplets.get(a);
			if (set == null) {
				return false;
			}
			return set.contains(b);
		}

		@Override
		public String toString() {
			return maplets.toString();
		}
	}
	
	private static class Closure<T> {// TODO extends Relation<T> ?
		private final Map<T, Set<T>> reachable = new HashMap<T, Set<T>>();
		private final Map<T, Set<T>> reachableReverse = new HashMap<T, Set<T>>();

		public Closure() {
			// avoid synthetic accessor emulation
		}
		
		public Map<T, Set<T>> getRelationMap() {
			return Collections.unmodifiableMap(reachable);
		}
		
		public boolean contains(T a, T b) {
			return contains(reachable, a, b);
		}

		public void add(T a, T b) throws CycleError {
			add(reachable, a, b);
			addAll(reachable, a, get(reachable, b));
			add(reachableReverse, b, a);
			addAll(reachableReverse, b, get(reachableReverse, a));
			if (!a.equals(b) && contains(reachableReverse, a, b)) {
				throw new CycleError("Adding " + a + "|->" + b
						+ " makes a cycle.");
			}
			for (T e : get(reachableReverse, a)) {
				addAll(reachable, e, get(reachable, a));
			}
			for (T e : get(reachable, b)) {
				addAll(reachableReverse, e, get(reachableReverse, b));
			}
		}

		private static <T> void add(Map<T, Set<T>> map, T a, T b) {
			final Set<T> set = get(map, a, true);
			set.add(b);
		}

		private static <T> Set<T> get(Map<T, Set<T>> map, T a, boolean addIfNeeded) {
			Set<T> set = map.get(a);
			if (set == null) {
				set = new HashSet<T>();
				if (addIfNeeded) {
					map.put(a, set);
				}
			}
			return set;
		}

		private static <T> void addAll(Map<T, Set<T>> map, T a, Set<T> s) {
			final Set<T> set = get(map, a, true);
			set.addAll(s);
		}

		private static <T> Set<T> get(Map<T, Set<T>> map, T a) {
			return get(map, a, false);
		}

		private static <T> boolean contains(Map<T, Set<T>> map, T a, T b) {
			return get(map, a).contains(b);
		}
		
		@Override
		public String toString() {
			return reachable.toString();
		}
	}
	
	private static class OperatorGroup {
		
		private final Set<Integer> allOperators = new HashSet<Integer>();
		private final Relation<Integer> compatibilityRelation = new Relation<Integer>();
		private final Closure<Integer> operatorPriority = new Closure<Integer>();
		private final Set<Integer> associativeOperators = new HashSet<Integer>();
		private final Set<Integer> spacedOperators = new HashSet<Integer>();

		private final String id;

		public OperatorGroup(String id) {
			this.id = id;
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

		public boolean hasLessPriority(Integer a, Integer b) {
			checkKnown(a, b);
			return operatorPriority.contains(a, b);
		}
		
		public boolean isCompatible(Integer a, Integer b) {
			checkKnown(a, b);
			return compatibilityRelation.contains(a, b)
					|| operatorPriority.contains(a, b)
					|| operatorPriority.contains(b, a);
		}
		
		public boolean isAssociative(Integer a) {
			checkKnown(a);
			return associativeOperators.contains(a);
		}
		
		@Override
		public String toString() {
			return id;
		}

		public void setSpaced(Integer kind) {
			checkKnown(kind);
			spacedOperators.add(kind);
		}
		
		public boolean isSpaced(Integer kind) {
			checkKnown(kind);
			return spacedOperators.contains(kind);
		}

		public IOperatorGroup asExternalView(
				Instantiator<Integer, IOperator> instantiator) {
			final Set<IOperator> extOpers = instantiator
					.instantiate(allOperators);
			final Map<IOperator, Set<IOperator>> extPrio = instantiator
					.instantiate(operatorPriority.getRelationMap());
			final Map<IOperator, Set<IOperator>> extCompat = instantiator
					.instantiate(compatibilityRelation.getRelationMap());
			final Set<IOperator> extAssoc = instantiator
					.instantiate(associativeOperators);
			return new ExternalOpGroup(id, extOpers, extPrio, extCompat,
					extAssoc);
		}
	}
	
	private final AllInOnceMap<String, OperatorGroup> idOpGroup = new AllInOnceMap<String, OperatorGroup>();
	private final AllInOnceMap<Integer, OperatorGroup> kindOpGroup = new AllInOnceMap<Integer, OperatorGroup>();
	private final AllInOnceMap<String, Integer> idKind = new AllInOnceMap<String, Integer>();
	
	
	private final Closure<OperatorGroup> groupPriority = new Closure<OperatorGroup>();
	
	public OperatorRegistry() {
		idOpGroup.put(GROUP0, GROUP_0);
	}
	
	public Map<Integer, String> getKindIds() {
		// not a bijective map because: { = Set Extension & Comprehension Set
		// but it is in a single group: Brace Sets
		// so one id or the other is OK
		return idKind.invert();
	}
	
	public IGrammar asExternalView(Instantiator<Integer, IOperator> instantiator) {
		final Instantiator<OperatorGroup, IOperatorGroup> groupInst = new Instantiator<OperatorGroup, IOperatorGroup>();
		final Set<IOperatorGroup> extGroups = new HashSet<IOperatorGroup>();
		for (OperatorGroup opGroup : idOpGroup.values()) {
			final IOperatorGroup groupView = opGroup
					.asExternalView(instantiator);
			extGroups.add(groupView);
			groupInst.setInst(opGroup, groupView);
		}
		final Map<IOperatorGroup, Set<IOperatorGroup>> extGroupPrios = groupInst
				.instantiate(groupPriority.getRelationMap());
		return new ExternalGrammar(extGroups, extGroupPrios);
	}

	public void addOperator(Integer kind, String operatorId, String groupId, boolean isSpaced) {
		idKind.put(operatorId, kind);
		
		OperatorGroup operatorGroup = idOpGroup.getNoCheck(groupId);
		if (operatorGroup == null) {
			operatorGroup = new OperatorGroup(groupId);
			idOpGroup.put(groupId, operatorGroup);
		}
		operatorGroup.add(kind);
		
		kindOpGroup.put(kind, operatorGroup);
		
		if (isSpaced) {
			operatorGroup.setSpaced(kind);
		}
	}
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		final Integer leftKind = idKind.get(leftOpId);
		final Integer rightKind = idKind.get(rightOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addCompatibility(leftKind, rightKind);
	}

	public void addAssociativity(String opId) {
		final Integer opKind = idKind.get(opId);
		final OperatorGroup group = kindOpGroup.get(opKind);
		group.addAssociativity(opKind);
	}

	// lowOpId gets a lower priority than highOpId
	public void addPriority(String lowOpId, String highOpId)
			throws CycleError {
		final Integer leftKind = idKind.get(lowOpId);
		final Integer rightKind = idKind.get(highOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addPriority(leftKind, rightKind);
	}

	// FIXME public operations that call this method should throw a caught exception
	private OperatorGroup getAndCheckSameGroup(Integer leftKind, Integer rightKind) {
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		if (leftGroup != rightGroup) {
			throw new IllegalArgumentException("Operators " + leftKind + " and "
					+ rightKind + " do not belong to the same group");
		}
		return leftGroup;
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
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		
		if (leftGroup == GROUP_0 && rightGroup == GROUP_0) {
			return LEFT_PRIORITY;
		// Unknown groups have a priority greater than GROUP0
		} else if (leftGroup == GROUP_0) {
			return RIGHT_PRIORITY;
		} else if (rightGroup == GROUP_0) {
			return LEFT_PRIORITY;
		} else if (groupPriority.contains(leftGroup, rightGroup)) {
			return RIGHT_PRIORITY;
		} else if (groupPriority.contains(rightGroup, leftGroup)) {
			return LEFT_PRIORITY;
		} else if (leftGroup == rightGroup) {
			final OperatorGroup group = leftGroup;
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

	// lowGroupId gets a lower priority than highGroupId
	public void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError {
		final OperatorGroup lowGroup = idOpGroup.get(lowGroupId);
		final OperatorGroup highGroup = idOpGroup.get(highGroupId);
		groupPriority.add(lowGroup, highGroup);
	}

	public boolean hasGroup(int kind) {
		return kindOpGroup.containsKey(kind);
	}

	public boolean isAssociative(int kind) {
		final OperatorGroup group = kindOpGroup.get(kind);
		return group.isAssociative(kind);
	}	
	
	public boolean isSpaced(int kind) {
		final OperatorGroup group = kindOpGroup.get(kind);
		return group.isSpaced(kind);
	}	
	
}
