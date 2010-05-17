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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.CycleError;
import org.eventb.internal.core.parser.GenParser.SyntaxCompatibleError;

/**
 * @author Nicolas Beauger
 *  
 */
public class OperatorRegistry {

	public static final String GROUP0 = "GROUP 0";

	private static final OperatorGroup GROUP_0 = new OperatorGroup(GROUP0);
	
	private static class Relation<T> {
		private final Map<T, Set<T>> maplets = new HashMap<T, Set<T>>();

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

	}
	
	private static class Closure<T> {// TODO extends Relation<T> ?
		private final Map<T, Set<T>> reachable = new HashMap<T, Set<T>>();
		private final Map<T, Set<T>> reachableReverse = new HashMap<T, Set<T>>();

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
	}
	
	private static class OperatorGroup {
		private final Set<Integer> operators = new HashSet<Integer>();
		private final Relation<Integer> compatibilityRelation = new Relation<Integer>();
		private final Closure<Integer> operatorPriority = new Closure<Integer>();

		private final String id;

		public OperatorGroup(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
		
		public void addCompatibility(Integer a, Integer b) {
			operators.add(a);
			operators.add(b);
			compatibilityRelation.add(a, b);
		}

		public void addPriority(Integer a, Integer b)
				throws CycleError {
			operatorPriority.add(a, b);
		}

		public boolean contains(Integer a) {
			return operators.contains(a);
		}

		public boolean isAssociative(Integer a, Integer b) {
			return operatorPriority.contains(a, b);
		}
		
		public boolean isCompatible(Integer a, Integer b) {
			return compatibilityRelation.contains(a, b);
		}
	}
	
	private final Map<String, OperatorGroup> idOpGroup = new HashMap<String, OperatorGroup>();
	// refactored
	private final Map<Integer, OperatorGroup> kindOpGroup = new HashMap<Integer, OperatorGroup>();
	private final Map<String, Integer> idKind = new HashMap<String, Integer>();
	
	
	private final Closure<OperatorGroup> groupPriority = new Closure<OperatorGroup>();
	// FIXME take group compatibility into account
	private final Relation<OperatorGroup> groupCompatibility = new Relation<OperatorGroup>();
	
	public OperatorRegistry() {
		idOpGroup.put(GROUP0, GROUP_0);
	}
	
	public void addOperator(Integer kind, String operatorId, String groupId) {
		idKind.put(operatorId, kind);
		
		OperatorGroup operatorGroup = idOpGroup.get(groupId);
		if (operatorGroup == null) {
			operatorGroup = new OperatorGroup(groupId);
			idOpGroup.put(groupId, operatorGroup);
		}
		final OperatorGroup oldGroup = kindOpGroup.put(kind, operatorGroup);
		if (oldGroup != null && oldGroup != operatorGroup) {
			kindOpGroup.put(kind, oldGroup);
			throw new IllegalArgumentException("when attempting to add group "
					+ groupId + " for token kind " + kind
					+ ", found another group already set for this kind: "
					+ oldGroup.getId());
		}
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

	private OperatorGroup getAndCheckSameGroup(Integer leftTag, Integer rightTag) {
		final OperatorGroup leftGroup = kindOpGroup.get(leftTag);
		final OperatorGroup rightGroup = kindOpGroup.get(rightTag);
		if (leftGroup != rightGroup) {
			throw new IllegalArgumentException("Operators " + leftTag + " and "
					+ rightTag + " do not belong to the same group");
		}
		return leftGroup;
	}
	
	/**
	 * <code>true</code> iff priority(tagLeft) < priority(tagRight) 
	 */
	public boolean hasLessPriority(int leftKind, int rightKind) throws SyntaxCompatibleError {
		// TODO right associativity
		// TODO encapsulate access to opGroup, returning constant default group0 when none is found
		final OperatorGroup leftGroup = kindOpGroup.get(leftKind);
		final OperatorGroup rightGroup = kindOpGroup.get(rightKind);
		
		// TODO have the  group0 constant available
		// TODO compare group references instead of ids
		// TODO group priority and compatibility should reference groups instead of their ids
		if (leftGroup == GROUP_0 && rightGroup == GROUP_0) {
			return false;
		// Unknown groups have a priority greater than GROUP0
		} else if (leftGroup == GROUP_0) {
			return true;
		} else if (rightGroup == GROUP_0) {
			return false;
		} else if (groupPriority.contains(leftGroup, rightGroup)) {
			return true;
		} else if (groupPriority.contains(rightGroup, leftGroup)) {
			return false;
		} else if (leftGroup == rightGroup) {
			final OperatorGroup group = leftGroup;
			if (group.isAssociative(leftKind, rightKind)) {
				return true;
			} else if (group.isAssociative(rightKind, leftKind)) {
				return false;
			} else if (group.isCompatible(leftKind, rightKind)) {
				return false;
			} else throw new SyntaxCompatibleError("Incompatible symbols: "+ leftKind +" with "+rightKind);
		} else {
			return false;
		}

	}

	private OperatorGroup getAndCheckExists(String groupId) {
		final OperatorGroup group = idOpGroup.get(groupId);
		if (group == null) {
			throw new IllegalArgumentException("unknown group: "+groupId);
		}
		return group;
	}
	
	// lowGroupId gets a lower priority than highGroupId
	public void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError {
		final OperatorGroup lowGroup = getAndCheckExists(lowGroupId);
		final OperatorGroup highGroup = getAndCheckExists(highGroupId);
		if (lowGroup == null || highGroup == null) {
			throw new IllegalArgumentException("unknown group ");
		}
		groupPriority.add(lowGroup, highGroup);
	}

	public void addGroupCompatibility(String leftGroupId, String rightGroupId) {
		final OperatorGroup leftGroup = getAndCheckExists(leftGroupId);
		final OperatorGroup rightGroup = getAndCheckExists(rightGroupId);
		groupCompatibility.add(leftGroup, rightGroup);
	}	

	
}
