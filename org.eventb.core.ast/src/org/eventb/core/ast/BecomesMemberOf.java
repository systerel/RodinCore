/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Implements the set-based assignment, where a set expression is given for
 * the assigned identifier.
 * 
 * @author Laurent Voisin
 */
public class BecomesMemberOf extends Assignment {

	private final Expression setExpr;
	
	public BecomesMemberOf(FreeIdentifier assignedIdent, Expression setExpr, SourceLocation location) {
		super(BECOMES_MEMBER_OF, location, setExpr.hashCode(), assignedIdent);
		this.setExpr = setExpr;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#flatten(org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	public Assignment flatten(FormulaFactory factory) {
		final Expression newSetExpr = setExpr.flatten(factory);
		if (newSetExpr == setExpr)
			return this;
		return factory.makeBecomesMemberOf(getAssignedIdentifiers()[0],
				newSetExpr, getSourceLocation());
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
		BecomesMemberOf other = (BecomesMemberOf) otherFormula;
		return this.hasSameAssignedIdentifiers(other)
				&& setExpr.equals(other.setExpr, withAlphaConversion);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#typeCheck(org.eventb.internal.core.typecheck.TypeCheckResult, org.eventb.core.ast.BoundIdentDecl[])
	 */
	@Override
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {
		// TODO Auto-generated method stub

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

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#solveType(org.eventb.internal.core.typecheck.TypeUnifier)
	 */
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames) {

		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" :∈ ");
		result.append(setExpr.toString(false, STARTTAG, boundNames));
		return result.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" :∈ (");
		result.append(setExpr.toStringFullyParenthesized(boundNames));
		result.append(')');
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
