package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;

public class PPEqClause extends AbstractPPClause {
	private List<IEquality> conditions = new ArrayList<IEquality>();
	
//	@Deprecated
//	public PPEqClause(int level, List<IPredicate> predicates, List<ILiteral> others) {
//		super(level, predicates, others);
//	}
	
	public PPEqClause(Level level, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		super(level, predicates, equalities, arithmetic);
		
		assert predicates.size() + equalities.size() >= 2;
	}

	public PPEqClause(Level level, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic, List<IEquality> conditions) {
		super(level, predicates, equalities, arithmetic);
		this.conditions = conditions;
		
		assert predicates.size() + equalities.size() >= 2;
	}
	
	public List<IEquality> getConditions() {
		List<IEquality> result = new ArrayList<IEquality>();
		result.addAll(conditions);
		return result;
	}
	
	@Override
	protected void computeBitSets() {
		for (IPredicate literal : predicates) {
			literal.setBit(positiveLiterals);
			literal.setBit(negativeLiterals);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPEqClause) {
			PPEqClause tmp = (PPEqClause) obj;
			HashMap<AbstractVariable, AbstractVariable> map = new HashMap<AbstractVariable, AbstractVariable>();
			return super.equalsWithDifferentVariables(tmp,map) && listEquals(conditions, tmp.conditions, map);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 31 * super.hashCodeWithDifferentVariables() + hashCode(conditions);
	}

	@Override
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		StringBuffer str = new StringBuffer();
		str.append("E"+super.toString(variableMap));
		if (conditions.size() > 0) {
			str.append(", CONDITIONS: ");
			str.append("[");
			for (ILiteral<?> literal : conditions) {
				str.append(literal.toString(variableMap));
				str.append(", ");
			}
			str.append("]");
		}
		return str.toString();
	}
	

	public IClause simplify(ISimplifier simplifier) {
		return simplifier.simplifyEquivalenceClause(this);
	}

	
	// form a new clause from an equivalence clause
	public static IClause newClause(Level level, List<IPredicate> predicate, 
			List<IEquality> equality, List<IArithmetic> arithmetic, 
			List<IEquality> conditions, IVariableContext context) {
		// we have a disjunctive clause
		if (predicate.size() + equality.size() + arithmetic.size() <= 1) {
			ILiteral<?> literal = null;
			if (predicate.size() == 1) {
				literal = predicate.remove(0);
			}
			else if (equality.size() == 1) {
				literal = equality.remove(0);
			}
			else if (arithmetic.size() == 1) {
				literal = arithmetic.remove(0);
			}
			if (literal != null) {
				List<LocalVariable> constants = new ArrayList<LocalVariable>();
				for (Term term : literal.getTerms()) {
					term.collectLocalVariables(constants);
				}
				if (!constants.isEmpty() && constants.get(0).isForall()) {
					Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
					for (LocalVariable variable : constants) {
						map.put(variable, variable.getVariable(context));
					}
					literal = literal.substitute(map);
				}
				if (literal instanceof IEquality) equality.add((IEquality)literal);
				if (literal instanceof IPredicate) predicate.add((IPredicate)literal);
				if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
			}
			
			equality.addAll(conditions);
			return new PPDisjClause(level, predicate, equality, arithmetic);
		}
		////////////////////////////////
		return new PPEqClause(level, predicate, equality, arithmetic, conditions);
	}
	
	public static void inverseOneliteral(List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		// we must inverse one sign
		if (predicates.size() > 0) {
			// we inverse a predicate literal
			IPredicate toInverse = predicates.remove(0);
			predicates.add(0, toInverse.getInverse());
		}
		else if (equalities.size() > 0) {
			// we inverse another literal
			IEquality toInverse = equalities.remove(0);
			equalities.add(0, toInverse.getInverse());
		}
		else if (arithmetic.size() > 0) {
			IArithmetic toInverse = arithmetic.remove(0);
			arithmetic.add(0, toInverse.getInverse());
		}
	}

	public void infer(IInferrer inferrer) {
		inferrer.inferFromEquivalenceClause(this);
	}
}
