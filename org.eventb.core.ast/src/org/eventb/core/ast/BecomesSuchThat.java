/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.getBoundIdentsAbove;
import static org.eventb.core.ast.QuantifiedHelper.getSyntaxTreeQuantifiers;
import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.core.ast.BoundIdentSubstitution;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Implements the most general assignment, where a predicate is given on the
 * before and after values of assigned identifiers.
 * <p>
 * In event-B, the after values of the assigned identifiers are denoted by a
 * primed identifier whose prefix is the assigned identifier. These primed
 * identifiers are implemented as bound identifiers, whose (implicit)
 * declaration is stored in instances of this class.
 * </p>
 * <p>
 * For instance, the assignment <code>x :| x &lt; x'</code> is represented in the
 * following way:
 * <ul>
 * <li>the assigned identifier is <code>x</code>.</li>
 * <li>the bound primed identifier is <code>x'</code>.</li>
 * <li>the condition is <code>x &lt; [[0]]</code>, where <code>[[0]]</code>
 * denotes an occurrence of the bound identifier <code>x'</code>.</li>
 * </ul>
 * 
 * @author Laurent Voisin
 */
public class BecomesSuchThat extends Assignment {

	// Quantified primed identifiers
	private BoundIdentDecl[] primedIdents;
	
	// Post-condition of this assignment
	private final Predicate condition;
	
	protected BecomesSuchThat(FreeIdentifier assignedIdent,
			BoundIdentDecl primedIdent, Predicate condition,
			SourceLocation location) {
		
		super(Formula.BECOMES_SUCH_THAT, location, condition.hashCode(), assignedIdent);
		this.condition = condition;
		this.primedIdents = new BoundIdentDecl[] {primedIdent};
		checkPreconditions();
		synthesizeType();
	}

	protected BecomesSuchThat(FreeIdentifier[] assignedIdents,
			BoundIdentDecl[] primedIdents, Predicate condition,
			SourceLocation location) {
		
		super(Formula.BECOMES_SUCH_THAT, location, condition.hashCode(), assignedIdents);
		this.condition = condition;
		this.primedIdents = new BoundIdentDecl[primedIdents.length];
		System.arraycopy(primedIdents, 0, this.primedIdents, 0, primedIdents.length);
		checkPreconditions();
		synthesizeType();
	}

	protected BecomesSuchThat(List<FreeIdentifier> assignedIdents,
			List<BoundIdentDecl> primedIdents, Predicate condition,
			SourceLocation location) {

		super(Formula.BECOMES_SUCH_THAT, location, condition.hashCode(), assignedIdents);
		this.condition = condition;
		this.primedIdents = primedIdents.toArray(new BoundIdentDecl[primedIdents.size()]);
		checkPreconditions();
		synthesizeType();
	}
	
	private void checkPreconditions() {
		assert this.primedIdents.length == assignedIdents.length;
	}
	
	private void synthesizeType() {
		final int length = assignedIdents.length;
		final Formula[] children = new Formula[length + 1];
		System.arraycopy(assignedIdents, 0, children, 0, length);
		children[length] = condition;
		
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		final BoundIdentifier[] boundIdentsBelow = 
			boundIdentMerger.getBoundMergedArray(); 
		this.boundIdents = 
			getBoundIdentsAbove(boundIdentsBelow, primedIdents);

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		if (! condition.isTypeChecked()) {
			return;
		}

		// Check equality of types
		for (int i = 0; i < length; i++) {
			final Type type = assignedIdents[i].getType();
			if (type == null || ! type.equals(primedIdents[i].getType())) {
				return;
			}
		}
		finalizeTypeCheck(true, null);
	}

	/**
	 * Returns the declaration of the primed identifiers created for each
	 * assigned identifier.
	 * 
	 * @return an array of bound identifier declaration
	 */
	public BoundIdentDecl[] getPrimedIdents() {
		final int length = this.primedIdents.length;
		final BoundIdentDecl[] result = new BoundIdentDecl[length];
		System.arraycopy(this.primedIdents, 0, result, 0, length);
		return result;
	}

	/**
	 * Returns the post-condition that appears in the right-hand side of this
	 * assignment.
	 * 
	 * @return the post-condition of this assignment
	 */
	public Predicate getCondition() {
		return condition;
	}

	@Override
	public BecomesSuchThat flatten(FormulaFactory factory) {
		Predicate newCondition = condition.flatten(factory);
		if (newCondition == condition)
			return this;
		return factory.makeBecomesSuchThat(assignedIdents, primedIdents,
				newCondition, getSourceLocation());
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdentSet);
		}
		condition.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		final int newOffset = offset + primedIdents.length;
		condition.collectNamesAbove(names, boundNames, newOffset);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		final String[] boundNamesBelow = catenateBoundIdentLists(boundNames, primedIdents);
		return tabs
			+ this.getClass().getSimpleName()
			+ " [:\u2223]\n"
		    + getSyntaxTreeLHS(boundNames, childTabs)
		    + getSyntaxTreeQuantifiers(boundNamesBelow, childTabs, primedIdents)
		    + condition.getSyntaxTree(boundNames, childTabs);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		int newNoOfBoundVars = noOfBoundVars + primedIdents.length;
		return condition.isWellFormed(newNoOfBoundVars);
	}

	@Override
	protected boolean equals(Formula otherFormula, boolean withAlphaConversion) {
		BecomesSuchThat other = (BecomesSuchThat) otherFormula;
		return hasSameAssignedIdentifiers(other)
				&& condition.equals(other.condition, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		final SourceLocation loc = getSourceLocation();
		for (int i = 0; i < primedIdents.length; i++) {
			assignedIdents[i].typeCheck(result, boundAbove);
			primedIdents[i].typeCheck(result, boundAbove);
			result.unify(assignedIdents[i].getType(), primedIdents[i].getType(), loc);
		}
		
		BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, primedIdents);
		condition.typeCheck(result, boundBelow);
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] boundAbove) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result, boundAbove);
			if (! result.isSuccess())
				return;
		}
		for (BoundIdentDecl decl: primedIdents) {
			decl.isLegible(result, boundAbove);
			if (! result.isSuccess()) {
				return;
			}
		}
		final BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, primedIdents);
		condition.isLegible(result, boundBelow);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#getWDPredicateRaw(org.eventb.core.ast.FormulaFactory)
	 */
	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		Predicate wdCondition = condition.getWDPredicateRaw(formulaFactory);
		return getWDSimplifyQ(formulaFactory, FORALL, primedIdents, wdCondition);
	}

	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean result = true;
		for (BoundIdentDecl ident : primedIdents) {
			result &= ident.solveType(unifier);
		}
		result &= condition.solveType(unifier);
		return finalizeTypeCheck(result, unifier);
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames) {

		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" :\u2223 ");
		
		final String[] localNames = getLocalNames();
		final String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);
		result.append(condition.toString(false, STARTTAG, newBoundNames));
		
		return result.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		StringBuilder result = new StringBuilder();
		appendAssignedIdents(result);
		result.append(" :\u2223 (");

		final String[] localNames = getLocalNames();
		final String[] newBoundNames = catenateBoundIdentLists(boundNames, localNames);
		result.append(condition.toStringFullyParenthesized(newBoundNames));
		
		result.append(')');
		return result.toString();
	}

	// TODO check for other uses of primed variables.
	private String[] getLocalNames() {
		final int length = primedIdents.length;
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = primedIdents[i].getName();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#accept(org.eventb.core.ast.IVisitor)
	 */
	@Override
	public boolean accept(IVisitor visitor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Predicate getFISPredicateRaw(FormulaFactory ff) {		
		if (condition.getTag() == BTRUE)
			return condition;
		else
			return ff.makeQuantifiedPredicate(EXISTS, 
					primedIdents, condition, getSourceLocation()); 
	}

	@Override
	protected Predicate getBAPredicateRaw(FormulaFactory ff) {
		ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
		FreeIdentifier[] freshIdents = 
			ff.makeFreshIdentifiers(primedIdents, typeEnvironment);
		Substitution subst = 
			new BoundIdentSubstitution(primedIdents, freshIdents, ff);
		return condition.applySubstitution(subst, ff);
	}

	@Override
	public FreeIdentifier[] getUsedIdentifiers() {
		return condition.getFreeIdentifiers();
	}

}
