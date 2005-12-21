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

	@Override
	public Assignment flatten(FormulaFactory factory) {
		final Expression newSetExpr = setExpr.flatten(factory);
		if (newSetExpr == setExpr)
			return this;
		return factory.makeBecomesMemberOf(assignedIdents[0],
				newSetExpr, getSourceLocation());
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdents);
		}
		setExpr.collectFreeIdentifiers(freeIdents);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {

		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		setExpr.collectNamesAbove(names, boundNames, offset);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		
		final StringBuilder result = new StringBuilder();
		result.append(tabs);
		result.append(this.getClass().getSimpleName());
		result.append(" [:∈]\n");
		for (FreeIdentifier ident: assignedIdents) {
			result.append(ident.getSyntaxTree(boundNames, childTabs));
		}
		result.append(setExpr.getSyntaxTree(boundNames, childTabs));
		return result.toString();
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return setExpr.isWellFormed(noOfBoundVars);
	}

	@Override
	protected boolean equals(Formula otherFormula, boolean withAlphaConversion) {
		BecomesMemberOf other = (BecomesMemberOf) otherFormula;
		return this.hasSameAssignedIdentifiers(other)
				&& setExpr.equals(other.setExpr, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		final FreeIdentifier lhs = assignedIdents[0];
		lhs.typeCheck(result, boundAbove);
		setExpr.typeCheck(result, boundAbove);

		final SourceLocation loc = getSourceLocation();
		result.unify(setExpr.getType(), result.makePowerSetType(lhs.getType()), loc);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#isLegible(org.eventb.internal.core.ast.LegibilityResult, org.eventb.core.ast.BoundIdentDecl[])
	 */
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result, quantifiedIdents);
			if (! result.isSuccess())
				return;
		}
		setExpr.isLegible(result, quantifiedIdents);
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
		boolean result = setExpr.solveType(unifier);
		return finalizeTypeCheck(result, unifier);
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
