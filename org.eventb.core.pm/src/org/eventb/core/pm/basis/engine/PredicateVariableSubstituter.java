package org.eventb.core.pm.basis.engine;

import java.util.Map;

import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;

public class PredicateVariableSubstituter extends DefaultRewriter {

	private Map<PredicateVariable, Predicate> map;
	
	public PredicateVariableSubstituter(
			Map<PredicateVariable, Predicate> map, FormulaFactory ff){
		super(true, ff);
		this.map = map;
	}
	
	public Predicate rewrite(PredicateVariable var){
		Predicate pred = map.get(var);
		if (pred != null)
			return pred;
		return var;
	}

}
