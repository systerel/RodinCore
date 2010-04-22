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

import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	protected static class CycleError extends Exception {

		private static final long serialVersionUID = 3961303994056706546L;

		public CycleError(String reason) {
			super(reason);
		}
	}
	
	protected static class SyntaxCompatibleError extends SyntaxError {

		private static final long serialVersionUID = -6230478311681172354L;

		public SyntaxCompatibleError(String reason) {
			super(reason);
		}
	}

	protected static class Relation<T> {
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

	protected static class Closure<T> {// TODO extends Relation<T> ?
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

	protected static class OperatorGroup {
		private final Set<Integer> operators = new HashSet<Integer>();
		private final Relation<Integer> compatibilityRelation = new Relation<Integer>();
		private final Closure<Integer> operatorAssociativity = new Closure<Integer>();

		private final String id;

		public OperatorGroup(String id) {
			this.id = id;
		}

		public void addCompatibility(Integer a, Integer b) {
			operators.add(a);
			operators.add(b);
			compatibilityRelation.add(a, b);
		}

		public void addAssociativity(Integer a, Integer b)
				throws CycleError {
			operatorAssociativity.add(a, b);
		}

		public boolean contains(Integer a) {
			return operators.contains(a);
		}

		public boolean isAssociative(Integer a, Integer b) {
			return operatorAssociativity.contains(a, b);
		}
		
		public boolean isCompatible(Integer a, Integer b) {
			return compatibilityRelation.contains(a, b);
		}
	}
	
	protected static final String GROUP0 = "GROUP 0";
	
	protected final IndexedSet<String> tokens = new IndexedSet<String>();
	protected final Map<Integer, String> groupIds = new HashMap<Integer, String>();
	protected final Closure<String> groupAssociativity = new Closure<String>();
	protected final Map<String, OperatorGroup> operatorGroups = new HashMap<String, OperatorGroup>();
	protected final Map<Integer, ISubParser> subParsers = new HashMap<Integer, ISubParser>();
	protected final Map<Integer, Integer> operatorTag = new HashMap<Integer, Integer>();
	
	// TODO fill for BMath
	protected final Map<String, Integer> operatorFromId = new HashMap<String, Integer>();
	
	public int getOperatorTag(Token token) throws SyntaxError {
		final Integer tag = operatorTag.get(token.kind);
		if (tag == null) {
			throw new SyntaxError("not an operator: " + token.val);
		}
		return tag;
	}
	
	public IndexedSet<String> getTokens() {
		return tokens;
	}

	public abstract void init();

	public ISubParser getSubParser(int kind) {
		return subParsers.get(kind);
	}
	

	/**
	 * priority(tagLeft) < priority(tagRight) 
	 */
	public boolean hasLessPriority(int tagleft, int tagRight) throws SyntaxCompatibleError {
		// TODO right associativity
		final String gid1 = groupIds.get(tagleft);
		final String gid2 = groupIds.get(tagRight);
		
		//FIXME NPE
		if (gid1.equals(GROUP0) && gid2.equals(GROUP0)) {
			return false;
		} else if (gid1.equals(GROUP0)) {
			return true;
		} else if (gid2.equals(GROUP0)) {
			return false;
		} else if (groupAssociativity.contains(gid1, gid2)) {
			return true;
		} else if (groupAssociativity.contains(gid2, gid1)) {
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
	


}
