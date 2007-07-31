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
	public static boolean DEBUG;
	public static void debug(String message){
		System.out.println(message);
	}
	
	private List<ISimplifier> simplifiers = new ArrayList<ISimplifier>();
	
	public void addSimplifier(ISimplifier simplifier) {
		simplifiers.add(simplifier);
	}
	
	public Clause run(Clause clause) {
//		debug("Launching simplifiers");
		Clause originalClause = clause;
		for (ISimplifier simplifier : simplifiers) {
			if (simplifier.canSimplify(clause)) {
				clause = clause.simplify(simplifier);
			}
		}
		if (DEBUG) debug("Simplified: "+originalClause.toString()+" -> "+clause.toString());
		return clause;
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
