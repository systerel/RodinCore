package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.Level;

public class ClauseFactory {

	private static ClauseFactory DEFAULT = new ClauseFactory();
	
	public static ClauseFactory getDefault() {
		return DEFAULT;
	}
	
	public IClause newDisjClause(List<ILiteral> literals) {
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		for (ILiteral literal : literals) {
			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
		}
		IClause clause = new PPDisjClause(Level.base,predicates,equalities,arithmetic);
		return clause;
	}
	
	public IClause newEqClause(List<ILiteral> literals) {
		assert literals.size() > 1;
		
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		for (ILiteral literal : literals) {
			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
		}
		IClause clause = new PPEqClause(Level.base,predicates,equalities,arithmetic);
		return clause;
	}
	
	public IClause newDisjClause(Level level, List<ILiteral> literals) {
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		for (ILiteral literal : literals) {
			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
		}
		return new PPDisjClause(level,predicates,equalities,arithmetic);
	}
	
	public IClause newEqClause(Level level, List<ILiteral> literals) {
		assert literals.size() > 1;
		
		List<IPredicate> predicates = new ArrayList<IPredicate>();
		List<IEquality> equalities = new ArrayList<IEquality>();
		List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
		for (ILiteral literal : literals) {
			if (literal instanceof IPredicate) predicates.add((IPredicate)literal);
			else if (literal instanceof IEquality) equalities.add((IEquality)literal);
			else if (literal instanceof IArithmetic) arithmetic.add((IArithmetic)literal);
		}
		return new PPEqClause(level,predicates,equalities,arithmetic);
	}
	
	
}
