package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;

public class ClauseSimplifier {

	/**
	 * Debug flag for <code>PROVER_SIMPLIFIER_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}
	
	private List<ISimplifier> simplifiers = new ArrayList<ISimplifier>();
	
	public void addSimplifier(ISimplifier simplifier) {
		simplifiers.add(simplifier);
	}
	
	public IClause run(IClause clause) {
//		debug("Launching simplifiers");
		IClause originalClause = clause;
		for (ISimplifier simplifier : simplifiers) {
			if (simplifier.canSimplify(clause)) {
				clause = clause.simplify(simplifier);
				if (clause == null) {
					debug("Simplified: "+originalClause.toString()+" -> ⊤");
					return null;
				}
			}
		}
		debug("Simplified: "+originalClause.toString()+" -> "+clause.toString());
		return clause;
	}
	
}
