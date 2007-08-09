/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;

public class ClauseSimplifier {

	/**
	 * Debug flag for <code>PROVER_SIMPLIFIER_TRACE</code>
	 */
	public static boolean DEBUG = false;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private List<ISimplifier> simplifiers = new ArrayList<ISimplifier>();
	
	public void addSimplifier(ISimplifier simplifier) {
		simplifiers.add(simplifier);
	}
	
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

	public void run(Set<Clause> clauses) {
		Set<Clause> tmp = new HashSet<Clause>();
		for (Clause clause : clauses) {
			tmp.add(run(clause));
		}
		clauses.clear();
		clauses.addAll(tmp);
	}
	
}
