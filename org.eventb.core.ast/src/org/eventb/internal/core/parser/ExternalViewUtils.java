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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.extension.IGrammar;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExternalViewUtils {

	public static class ExternalOperator implements IOperator {

		private final String id;
		private final String syntaxSymbol;

		public ExternalOperator(String id, String syntaxSymbol) {
			this.id = id;
			this.syntaxSymbol = syntaxSymbol;
		}

		@Override
		public String getSyntaxSymbol() {
			return syntaxSymbol;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return syntaxSymbol;
		}
	}

	public static class ExternalOpGroup implements IOperatorGroup {

		private final String id;
		private final Set<IOperator> operators;
		private final Map<IOperator, Set<IOperator>> priorities;
		private final Map<IOperator, Set<IOperator>> compatibilities;
		private final Set<IOperator> associativeOperators;

		public ExternalOpGroup(String id, Set<IOperator> operators,
				Map<IOperator, Set<IOperator>> priorities,
				Map<IOperator, Set<IOperator>> compatibilities,
				Set<IOperator> associativeOperators) {
			this.id = id;
			this.operators = operators;
			this.priorities = priorities;
			this.compatibilities = compatibilities;
			this.associativeOperators = associativeOperators;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public Set<IOperator> getOperators() {
			return operators;
		}

		@Override
		public Map<IOperator, Set<IOperator>> getPriorities() {
			return priorities;
		}

		@Override
		public Map<IOperator, Set<IOperator>> getCompatibilities() {
			return compatibilities;
		}

		@Override
		public Set<IOperator> getAssociativeOperators() {
			return associativeOperators;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("group id=");
			sb.append(id);
			sb.append("\nall operators=\n");
			sb.append(operators);
			sb.append("\npriorities=\n");
			sb.append(priorities);
			sb.append("\ncompatibilities=\n");
			sb.append(compatibilities);
			sb.append("\nassociative operators=\n");
			sb.append(associativeOperators);
			return sb.toString();
		}
	}

	public static class ExternalGrammar implements IGrammar {

		private final Set<IOperatorGroup> groups;
		private Map<IOperatorGroup, Set<IOperatorGroup>> groupPrios;

		public ExternalGrammar(Set<IOperatorGroup> groups,
				Map<IOperatorGroup, Set<IOperatorGroup>> groupPrios) {
			this.groups = groups;
			this.groupPrios = groupPrios;
		}

		@Override
		public Set<IOperatorGroup> getGroups() {
			return groups;
		}

		@Override
		public Map<IOperatorGroup, Set<IOperatorGroup>> getGroupPriorities() {
			return groupPrios;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("\n===========  Grammar:  ===========\n");
			for (IOperatorGroup group : groups) {
				sb.append(group);
				sb.append("\n*********************\n");
			}
			sb.append("\n++++++++++  Priorities:  +++++++++\n");
			for (Entry<IOperatorGroup, Set<IOperatorGroup>> prio : groupPrios
					.entrySet()) {
				final List<String> ids = new ArrayList<String>();
				for (IOperatorGroup group : prio.getValue()) {
					ids.add(group.getId());
				}
				sb.append(prio.getKey().getId());
				sb.append(" < ");
				sb.append(ids);
				sb.append('\n');
			}
			sb.append("\n==================================\n");
			return sb.toString();
		}
	}

	// S: source
	// T: target
	public static class Instantiator<S, T> {
		private final Map<S, T> instantiation = new HashMap<S, T>();

		public void setInst(S source, T target) {
			final T old = instantiation.put(source, target);
			if (old != null) {
				throw new IllegalArgumentException("source " + source
						+ " already is given several targets: " + old + " and "
						+ target);
			}
		}

		public boolean hasInst(S source) {
			return instantiation.containsKey(source);
		}
		
		public Collection<T> values() {
			return instantiation.values();
		}
		
		public T instantiate(S source) {
			final T target = instantiation.get(source);
			if (target == null) {
				throw new IllegalStateException("no target for source "
						+ source);
			}
			return target;
		}
	}

}
