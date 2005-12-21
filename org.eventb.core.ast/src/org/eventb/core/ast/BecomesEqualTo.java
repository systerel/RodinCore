/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Implements the deterministic assignment, where an expression is given for
 * each assigned identifier.
 * 
 * @author Laurent Voisin
 */
public class BecomesEqualTo extends Assignment {

	private final Expression[] values;
	
	public BecomesEqualTo(FreeIdentifier assignedIdent, Expression value,
			SourceLocation location) {
		super(BECOMES_EQUAL_TO, location, value.hashCode(), assignedIdent);
		this.values = new Expression[] {value};
		checkPreconditions();
	}

	public BecomesEqualTo(FreeIdentifier[] assignedIdents, Expression[] values,
			SourceLocation location) {
		super(BECOMES_EQUAL_TO, location, combineHashCodes(values), assignedIdents);
		this.values = new Expression[values.length];
		System.arraycopy(values, 0, this.values, 0, values.length);
		checkPreconditions();
	}

	public BecomesEqualTo(List<FreeIdentifier> assignedIdents, List<Expression> values,
			SourceLocation location) {
		super(BECOMES_EQUAL_TO, location, combineHashCodes(values), assignedIdents);
		this.values = values.toArray(new Expression[values.size()]);
		checkPreconditions();
	}


	private void checkPreconditions() {
		assert assignedIdents.length == values.length;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#flatten(org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	public Assignment flatten(FormulaFactory factory) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#collectFreeIdentifiers(java.util.LinkedHashSet)
	 */
	@Override
	protected void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdents) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#collectNamesAbove(java.util.Set, java.lang.String[], int)
	 */
	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#getSyntaxTree(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#isWellFormed(int)
	 */
	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean equals(Formula otherFormula, boolean withAlphaConversion) {
		BecomesEqualTo other = (BecomesEqualTo) otherFormula;
		if (! this.hasSameAssignedIdentifiers(other))
			return false;
		for (int i = 0; i < values.length; i++) {
			if (! values[i].equals(other.values[i], withAlphaConversion))
				return false;
		}
		return true;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		final SourceLocation loc = getSourceLocation();
		for (int i = 0; i < values.length; i++) {
			assignedIdents[i].typeCheck(result, boundAbove);
			values[i].typeCheck(result, boundAbove);
			result.unify(assignedIdents[i].getType(), values[i].getType(), loc);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#isLegible(org.eventb.internal.core.ast.LegibilityResult, org.eventb.core.ast.BoundIdentDecl[])
	 */
	@Override
	protected void isLegible(LegibilityResult result,
			BoundIdentDecl[] quantifiedIdents) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#getWDPredicateRaw(org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean result = true;
		for (Expression value: values) {
			result &= value.solveType(unifier);
		}
		return finalizeTypeCheck(result, unifier);
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames) {
		
		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" ≔ ");
		boolean comma = false;
		for (Expression value: values) {
			if (comma) result.append(", ");
			result.append(value.toString(false, STARTTAG, boundNames));
			comma = true;
		}
		return result.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" ≔ ");
		boolean comma = false;
		for (Expression value: values) {
			if (comma) result.append(", ");
			result.append('(');
			result.append(value.toStringFullyParenthesized(boundNames));
			result.append(')');
			comma = true;
		}
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#accept(org.eventb.core.ast.IVisitor)
	 */
	@Override
	public boolean accept(IVisitor visitor) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#isTypeChecked()
	 */
	@Override
	public boolean isTypeChecked() {
		// TODO Auto-generated method stub
		return false;
	}

}
