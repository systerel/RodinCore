package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;

public abstract class AbstractInferrer implements IInferrer {

	protected List<EqualityLiteral> equalities;
	protected List<PredicateLiteral> predicates;
	protected List<ArithmeticLiteral> arithmetic;
	protected List<EqualityLiteral> conditions;
	
	protected IVariableContext context;
	
	protected HashMap<SimpleTerm, SimpleTerm> substitutionsMap;
	
	public AbstractInferrer(IVariableContext context) {
		this.context = context;
	}
	
	protected void init(Clause clause, HashMap<SimpleTerm, SimpleTerm> map) {
		equalities = clause.getEqualityLiterals();
		predicates = clause.getPredicateLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
		
		initialize(clause);
		
		equalities = getListCopy(equalities,map,context);
		predicates = getListCopy(predicates,map,context);
		arithmetic = getListCopy(arithmetic,map,context);
		conditions = getListCopy(conditions,map,context);
	}
	
	protected void init(DisjunctiveClause clause) {
		substitutionsMap = new HashMap<SimpleTerm, SimpleTerm>();
		init(clause,substitutionsMap);
	}
	
	protected void init(EquivalenceClause clause) {
		substitutionsMap = new HashMap<SimpleTerm, SimpleTerm>();
		init(clause,substitutionsMap);
	}
	
	protected <T extends Literal<?,?>> List<T> getListCopy(List<T> list,
			HashMap<SimpleTerm, SimpleTerm> substitutionsMap, IVariableContext context) {
		List<T> result = new ArrayList<T>();
		for (T pred : list) {
			result.add((T)pred.getCopyWithNewVariables(context, substitutionsMap));
		}
		return result;
	}
	
	protected abstract void initialize(Clause clause) throws IllegalStateException;
	
	protected abstract void reset();

	protected abstract void inferFromDisjunctiveClauseHelper(Clause clause);

	protected abstract void inferFromEquivalenceClauseHelper(Clause clause);

	public void inferFromDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		inferFromDisjunctiveClauseHelper(clause);
//		setParents(clause);
		reset();
	}

	public void inferFromEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		inferFromEquivalenceClauseHelper(clause);
//		setParents(clause);
		reset();
	}

	protected boolean isEmpty() {
		return conditions.size() + predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
}
