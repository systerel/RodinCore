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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.CycleError;

/**
 * @author Nicolas Beauger
 *  
 */
public class OperatorRegistry {

	public static final String GROUP0 = "GROUP 0";

	public static enum OperatorRelationship {
		LEFT_PRIORITY, RIGHT_PRIORITY, COMPATIBLE, INCOMPATIBLE
	}
	
	private static final OperatorGroup GROUP_0 = new OperatorGroup(GROUP0);
	
	private static class Relation<T> {
		private final Map<T, Set<T>> maplets = new HashMap<T, Set<T>>();

		public Relation() {
			// avoid synthetic accessor emulation
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
		private final Relation<Integer> compatibilityRelation = new Relation<Integer>();
		private final Closure<Integer> operatorPriority = new Closure<Integer>();

		private final String id;

		public OperatorGroup(String id) {
			this.id = id;
		}

		// will be needed to display current grammar to extension creator
		public String getId() {
			return id;
		}
		
		/**
		 * Adds a compatibility between a and b for all language versions.
		 * 
		 * @param a
		 *            an operator kind
		 * @param b
		 *            an operator kind
		 */
		public void addCompatibility(Integer a, Integer b) {
			compatibilityRelation.add(a, b);
		}

		public void addPriority(Integer a, Integer b)
				throws CycleError {
			operatorPriority.add(a, b);
		}

		public boolean hasLessPriority(Integer a, Integer b) {
			return operatorPriority.contains(a, b);
		}
		
		public boolean isCompatible(Integer a, Integer b) {
			return compatibilityRelation.contains(a, b)
					|| operatorPriority.contains(a, b)
					|| operatorPriority.contains(b, a);
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	private final AllInOnceMap<String, OperatorGroup> idOpGroup = new AllInOnceMap<String, OperatorGroup>();
	private final AllInOnceMap<Integer, OperatorGroup> kindOpGroup = new AllInOnceMap<Integer, OperatorGroup>();
	private final AllInOnceMap<String, Integer> idKind = new AllInOnceMap<String, Integer>();
	
	
	private final Closure<OperatorGroup> groupPriority = new Closure<OperatorGroup>();
	
	public OperatorRegistry() {
		idOpGroup.put(GROUP0, GROUP_0);
	}
	
	public void addOperator(Integer kind, String operatorId, String groupId) {
		idKind.put(operatorId, kind);
		
		OperatorGroup operatorGroup = idOpGroup.getNoCheck(groupId);
		if (operatorGroup == null) {
			operatorGroup = new OperatorGroup(groupId);
			idOpGroup.put(groupId, operatorGroup);
		}
		kindOpGroup.put(kind, operatorGroup);
	}
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		final Integer leftKind = idKind.get(leftOpId);
		final Integer rightKind = idKind.get(rightOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftKind, rightKind);
		group.addCompatibility(leftKind, rightKind);
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
	
}
