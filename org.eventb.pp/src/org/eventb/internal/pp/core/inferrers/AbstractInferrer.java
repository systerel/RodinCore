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
		initialize(clause);
		
		equalities = getListCopy(clause.getEqualityLiterals(),map,context);
		predicates = getListCopy(clause.getPredicateLiterals(),map,context);
		arithmetic = getListCopy(clause.getArithmeticLiterals(),map,context);
	}
	
	protected void init(PPDisjClause clause) {
		substitutionsMap = new HashMap<AbstractVariable, AbstractVariable>();
		init(clause,substitutionsMap);
		conditions = new ArrayList<IEquality>();
	}
	
	protected void init(PPEqClause clause) {
		substitutionsMap = new HashMap<AbstractVariable, AbstractVariable>();
		init(clause,substitutionsMap);
		conditions = getListCopy(clause.getConditions(),substitutionsMap,context);
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

	protected abstract void inferFromDisjunctiveClauseHelper();

	protected abstract void inferFromEquivalenceClauseHelper();

	public void inferFromDisjunctiveClause(PPDisjClause clause) {
		init(clause);
		inferFromDisjunctiveClauseHelper();
		setParents(clause);
		reset();
	}

	public void inferFromEquivalenceClause(PPEqClause clause) {
		init(clause);
		inferFromEquivalenceClauseHelper();
		setParents(clause);
		reset();
	}

	protected abstract void setParents(IClause clause);
	
}
