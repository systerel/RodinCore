/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pm;

import java.util.ArrayList;
import java.util.Arrays;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Helper class for sorting a type environment into carrier sets and regular
 * variables.
 * 
 * @author Laurent Voisin
 */
public class TypeEnvironmentSorter {

	/**
	 * Helper class for holding the result of this sorter.
	 */
	public static final class Entry implements Comparable<Entry> {
		public final String name;
		public final Type type;
		
		public Entry(final String name, final Type type) {
			this.name = name;
			this.type = type;
		}

		public int compareTo(Entry o) {
			return name.compareTo(o.name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof Entry) {
				Entry other = (Entry) obj;
				return name.equals(other.name);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public String toString() {
			return name + " \u2982 " + type;
		}

	}

	/**
	 * The names of all given sets from the type environment passed to the
	 * constructor, sorted in increasing order.
	 */
	public final String[] givenSets;
	
	/**
	 * All entries from the type environment passed to the constructor, sorted
	 * in increasing order of names.
	 */
	public final Entry[] variables;

	public TypeEnvironmentSorter(ITypeEnvironment typEnv) {
		ArrayList<String> sets = new ArrayList<String>();
		ArrayList<Entry> vars = new ArrayList<Entry>();
		ITypeEnvironment.IIterator iter = typEnv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String name = iter.getName();
			if (iter.isGivenSet()) {
				sets.add(name);
			} else {
				vars.add(new Entry(name, iter.getType()));
			}
		}
		givenSets = sets.toArray(new String[sets.size()]);
		Arrays.sort(givenSets);
		variables = vars.toArray(new Entry[vars.size()]);
		Arrays.sort(variables);
	}

}
