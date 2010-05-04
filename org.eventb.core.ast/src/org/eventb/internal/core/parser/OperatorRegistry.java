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
import org.eventb.core.ast.extension.IFormulaExtension.Associativity;
import org.eventb.internal.core.parser.AbstractGrammar.SyntaxCompatibleError;

/**
 * @author Nicolas Beauger
 *  
 */
public class OperatorRegistry {

	public static final String GROUP0 = "GROUP 0";

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
	
	
	
	//	TODO refactor all these maps
	private final Map<Integer, String> groupIds = new HashMap<Integer, String>();
	private final Closure<String> groupPriority = new Closure<String>();
	
	// FIXME take group compatibility into account
	private final Relation<String> groupCompatibility = new Relation<String>();
	private final Map<String, OperatorGroup> operatorGroups = new HashMap<String, OperatorGroup>();
	private final Map<String, Integer> operatorFromId = new HashMap<String, Integer>();
	private final Map<Integer, Associativity> associativity = new HashMap<Integer, Associativity>();
	
	public void addOperator(int tag, String operatorId, String groupId) {
		operatorFromId.put(operatorId, tag);
		OperatorGroup operatorGroup = operatorGroups.get(groupId);
		if (operatorGroup == null) {
			operatorGroup = new OperatorGroup(groupId);
			operatorGroups.put(groupId, operatorGroup);
		}
		groupIds.put(tag, groupId);
	}
	
	public void addCompatibility(String leftOpId, String rightOpId) {
		final Integer leftTag = operatorFromId.get(leftOpId);
		final Integer rightTag = operatorFromId.get(rightOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftTag, rightTag);
		group.addCompatibility(leftTag, rightTag);
	}

	// lowOpId gets a lower priority than highOpId
	public void addPriority(String lowOpId, String highOpId)
			throws CycleError {
		final Integer leftTag = operatorFromId.get(lowOpId);
		final Integer rightTag = operatorFromId.get(highOpId);
		final OperatorGroup group = getAndCheckSameGroup(leftTag, rightTag);
		group.addPriority(leftTag, rightTag);
	}

	private OperatorGroup getAndCheckSameGroup(Integer leftTag, Integer rightTag) {
		final String leftGroupId = groupIds.get(leftTag);
		final String rightGroupId = groupIds.get(rightTag);
		if (!leftGroupId.equals(rightGroupId)) {
			throw new IllegalArgumentException("Operators " + leftTag + " and "
					+ rightTag + " do not belong to the same group");
		}
		return operatorGroups.get(leftGroupId);
	}
	
	/**
	 * <code>true</code> iff priority(tagLeft) < priority(tagRight) 
	 */
	public boolean hasLessPriority(int tagleft, int tagRight) throws SyntaxCompatibleError {
		// TODO right associativity
		final String gid1 = groupIds.get(tagleft);
		final String gid2 = groupIds.get(tagRight);
		
		//FIXME NPE
		if (gid1.equals(OperatorRegistry.GROUP0) && gid2.equals(OperatorRegistry.GROUP0)) {
			return false;
		// Unknown groups have a priority greater than GROUP0
		} else if (gid1.equals(OperatorRegistry.GROUP0)) {
			return true;
		} else if (gid2.equals(OperatorRegistry.GROUP0)) {
			return false;
		} else if (groupPriority.contains(gid1, gid2)) {
			return true;
		} else if (groupPriority.contains(gid2, gid1)) {
			return false;
		} else if (gid1.equals(gid2)) {
			final OperatorGroup opGroup = operatorGroups.get(gid1);
			if (opGroup.isAssociative(tagleft, tagRight)) {
				return true;
			} else if (opGroup.isAssociative(tagRight, tagleft)) {
				return false;
			} else if (opGroup.isCompatible(tagleft, tagRight)) {
				return false;
			} else throw new SyntaxCompatibleError("Incompatible symbols: "+ tagleft +" with "+tagRight);
		} else {
			return false;
		}

	}

	// lowGroupId gets a lower priority than highGroupId
	public void addGroupPriority(String lowGroupId, String highGroupId)
			throws CycleError {
		groupPriority.add(lowGroupId, highGroupId);
	}

	public void addGroupCompatibility(String leftGroupId, String rightGroupId) {
		groupCompatibility.add(leftGroupId, rightGroupId);
	}	

	
}
