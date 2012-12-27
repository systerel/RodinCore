/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;

/**
 * This class is responsible for calling several simplifiers in a 
 * row on a clause.
 * <p>
 * Simplifiers (instances of {@link ISimplifier}) are run on a clause using the
 * order in which they were added using {@link #addSimplifier(ISimplifier)}.
 *
 * @author François Terrier
 *
 */
public final class ClauseSimplifier {

	/**
	 * Debug flag for <code>PROVER_SIMPLIFIER_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private final List<ISimplifier> simplifiers = new ArrayList<ISimplifier>();
	
	/**
	 * Adds a simplifier to the list of simplifiers.
	 * 
	 * @param simplifier the simplifier to be added
	 */
	public void addSimplifier(ISimplifier simplifier) {
		simplifiers.add(simplifier);
	}
	
	/**
	 * Runs the simplifiers on the clause.
	 * 
	 * @param clause the clause on which to run the simplifiers
	 * @return the simplified clause
	 */
	public Clause run(Clause clause) {
		Clause tmp = clause;
		for (ISimplifier simplifier : simplifiers) {
			if (simplifier.canSimplify(clause)) {
				tmp = tmp.simplify(simplifier);
			}
		}
		if (DEBUG) debug("Simplified: "+clause.toString()+" -> "+tmp.toString());
		return tmp;
	}

	/**
	 * Runs the simplifiers on the specified set of clauses and 
	 * puts back the simplified clauses in the given set.
	 * 
	 * @param clauses the clauses to be simplified
	 */
	public void run(Set<Clause> clauses) {
		Set<Clause> tmp = new HashSet<Clause>();
		for (Clause clause : clauses) {
			tmp.add(run(clause));
		}
		clauses.clear();
		clauses.addAll(tmp);
	}
	
}
