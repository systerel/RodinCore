package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class ClauseFactory {

	private static ClauseFactory DEFAULT = new ClauseFactory();
	
	public static ClauseFactory getDefault() {
		return DEFAULT;
	}
	
	// creates a definition clause
	public IClause newDisjClauseWithCopy(IOrigin origin, List<ILiteral<?>> literals, IVariableContext context) {
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		HashMap<AbstractVariable, AbstractVariable> map = new HashMap<AbstractVariable, AbstractVariable>();
		for (ILiteral<?> literal : literals) {
			ILiteral<?> copy = literal.getCopyWithNewVariables(context, map);
			if (copy instanceof IPredicate) predicates.add((IPredicate)copy);
			else if (copy instanceof IEquality) equalities.add((IEquality)copy);
			else if (copy instanceof IArithmetic) arithmetic.add((IArithmetic)copy);
		}
		IClause clause = new PPDisjClause(origin,predicates,equalities,arithmetic);
		return clause;
	}
	
	// creates a definition clause
	public IClause newEqClauseWithCopy(IOrigin origin, List<ILiteral<?>> literals, IVariableContext context) {
		assert literals.size() > 1;
		
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		HashMap<AbstractVariable, AbstractVariable> map = new HashMap<AbstractVariable, AbstractVariable>();
		for (ILiteral<?> literal : literals) {
			ILiteral<?> copy = literal.getCopyWithNewVariables(context, map);
			if (copy instanceof IPredicate) predicates.add((IPredicate)copy);
			else if (copy instanceof IEquality) equalities.add((IEquality)copy);
			else if (copy instanceof IArithmetic) arithmetic.add((IArithmetic)copy);
		}
		IClause clause = new PPEqClause(origin,predicates,equalities,arithmetic);
		return clause;
	}
	
	
//	public IClause newDisjClause(List<ILiteral> literals) {
//		List<IPredicate> predicates = new ArrayList<IPredicate>();
//		List<IEquality> equalities = new ArrayList<IEquality>();
//		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
//		for (ILiteral literal : literals) {
//			
//			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
//			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
//			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
//		}
//		IClause clause = new PPDisjClause(Level.base,predicates,equalities,arithmetic);
//		return clause;
//	}
//	
//	public IClause newEqClause(List<ILiteral> literals) {
//		assert literals.size() > 1;
//		
//		List<IPredicate> predicates = new ArrayList<IPredicate>();
//		List<IEquality> equalities = new ArrayList<IEquality>();
//		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
//		for (ILiteral literal : literals) {
//			
//			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
//			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
//			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
//		}
//		IClause clause = new PPEqClause(Level.base,predicates,equalities,arithmetic);
//		return clause;
//	}
	
}
