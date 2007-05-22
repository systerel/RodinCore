package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;

public abstract class AbstractInferrer implements IInferrer {

	protected List<IEquality> equalities;
	protected List<IPredicate> predicates;
	protected List<IArithmetic> arithmetic;
	protected List<IEquality> conditions;
	
	protected IVariableContext context;
	
	protected HashMap<AbstractVariable, AbstractVariable> substitutionsMap;
	
	public AbstractInferrer(IVariableContext context) {
		this.context = context;
	}
	
	protected void init(IClause clause, HashMap<AbstractVariable, AbstractVariable> map) {
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
	
	protected void init(PPDisjClause clause) {
		substitutionsMap = new HashMap<AbstractVariable, AbstractVariable>();
		init(clause,substitutionsMap);
	}
	
	protected void init(PPEqClause clause) {
		substitutionsMap = new HashMap<AbstractVariable, AbstractVariable>();
		init(clause,substitutionsMap);
	}
	
	protected <T extends ILiteral<T>> List<T> getListCopy(List<T> list,
			HashMap<AbstractVariable, AbstractVariable> substitutionsMap, IVariableContext context) {
		List<T> result = new ArrayList<T>();
		for (T pred : list) {
			result.add(pred.getCopyWithNewVariables(context, substitutionsMap));
		}
		return result;
	}
	
	protected abstract void initialize(IClause clause) throws IllegalStateException;
	
	protected abstract void reset();

	protected abstract void inferFromDisjunctiveClauseHelper(IClause clause);

	protected abstract void inferFromEquivalenceClauseHelper(IClause clause);

	public void inferFromDisjunctiveClause(PPDisjClause clause) {
		init(clause);
		inferFromDisjunctiveClauseHelper(clause);
//		setParents(clause);
		reset();
	}

	public void inferFromEquivalenceClause(PPEqClause clause) {
		init(clause);
		inferFromEquivalenceClauseHelper(clause);
//		setParents(clause);
		reset();
	}

	protected boolean isEmpty() {
		return conditions.size() + predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
}
