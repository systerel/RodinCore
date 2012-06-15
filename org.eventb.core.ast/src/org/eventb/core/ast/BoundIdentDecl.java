/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - added support for predicate variables
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.SubParsers.BOUND_IDENT_DECL_SUBPARSER;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * This class represents a declaration of a bound identifier in a quantified
 * formula.
 * <p>
 * For instance, in the formula
 * 
 * <pre>
 *   ∀x,y·x ∈ ℕ ∧ y ∈ ℕ ⇒ x + y ∈ ℕ
 * </pre>
 * 
 * the first occurrences of "x" and "y" are represented by instances of this
 * class. The other occurrences are represented by
 * {@link org.eventb.core.ast.BoundIdentifier} instances.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BoundIdentDecl extends Formula<BoundIdentDecl> {
	
	private final String name;
	private Type type;
	
	protected BoundIdentDecl(String name, int tag, SourceLocation location, Type givenType) {
		super(tag, location, name.hashCode());
		assert tag == Formula.BOUND_IDENT_DECL;
		assert name != null;
		assert name.length() != 0;
		assert !name.contains(PredicateVariable.LEADING_SYMBOL);
		this.name = name;

		setPredicateVariableCache();
		synthesizeType(givenType);
	}

	private void synthesizeType(Type givenType) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;

		if (givenType == null)
			return;
		
		assert givenType.isSolved();
		this.type = givenType;
		this.typeChecked = true;
	}
	
	/**
	 * Returns the name of this identifier.
	 * 
	 * @return the name of this identifier
	 */
	public String getName() {
		return name;
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		BOUND_IDENT_DECL_SUBPARSER.toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getIDENT();
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [name: " + name + "]" 
				+ typeName + "\n";
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		final BoundIdentDecl otherDecl = (BoundIdentDecl) other;
		final boolean sameType = type == null ? otherDecl.type == null :
			type.equals(otherDecl.type);
		return sameType && name.equals(otherDecl.name);
	}
	
	/*
	 * A formula containing free identifiers is well-formed, only if the free identifier
	 * does not appear bound in the formula.
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		if (result.hasFreeIdent(name)) {
			result.addProblem(new ASTProblem(
					this.getSourceLocation(),
					ProblemKind.BoundIdentifierHasFreeOccurences,
					ProblemSeverities.Error, name));
			FreeIdentifier other = result.getExistingFreeIdentifier(name);
			result.addProblem(new ASTProblem(
					other.getSourceLocation(),
					ProblemKind.FreeIdentifierHasBoundOccurences,
					ProblemSeverities.Error, name));
		} else if (result.hasBoundIdentDecl(name)) {
			result.addProblem(new ASTProblem(
					this.getSourceLocation(),
					ProblemKind.BoundIdentifierIsAlreadyBound,
					ProblemSeverities.Error, name));
			BoundIdentDecl other = result.getExistingBoundIdentDecl(name);
			result.addProblem(new ASTProblem(
					other.getSourceLocation(),
					ProblemKind.BoundIdentifierIsAlreadyBound,
					ProblemSeverities.Error, name));
		} else {
			result.addBoundIdentDecl(this);
		}
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		if (type == null) {
			type = result.newFreshVariable(getSourceLocation());
		}
	}

	@Override
	protected boolean solveType(TypeUnifier unifier) {
		if (isTypeChecked()) {
			return true;
		}
		if (type == null) {
			// Shared node, already solved (and failed).
			return false;
		}
		Type inferredType = unifier.solve(type);
		type = null;
		if (inferredType != null && inferredType.isSolved()) {
			synthesizeType(inferredType);
		}
		return isTypeChecked();
	}

	public Type getType() {
		return type;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to do.
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		names.add(name);
	}
	
	@Override
	protected BoundIdentDecl bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		return this;
	}

	@Override
	protected BoundIdentDecl getTypedThis() {
		return this;
	}

	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitBOUND_IDENT_DECL(this);
	}
	
	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBoundIdentDecl(this);
	}

	@Override
	public BoundIdentDecl rewrite(IFormulaRewriter rewriter) {
		throw new UnsupportedOperationException(
				"Bound identifier declarations cannot be rewritten");
	}

	@Override
	protected BoundIdentDecl rewrite(ITypedFormulaRewriter rewriter) {
		throw new UnsupportedOperationException(
				"Bound identifier declarations cannot be rewritten");
	}

	/**
	 * @since 2.6
	 */
	@Override
	public BoundIdentDecl specialize(ISpecialization specialization) {
		return ((Specialization)specialization).rewrite(this);
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		type.addGivenTypes(set);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
	}

	@Override
	public Formula<?> getChild(int index) {
		throw invalidIndex(index);
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

	@Override
	protected BoundIdentDecl rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

	@Override
	protected BoundIdentDecl getCheckedReplacement(SingleRewriter rewriter) {
		return TypedFormulaRewriter.getDefault().checkReplacement(this,
				rewriter.getBoundIdentDecl());
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
