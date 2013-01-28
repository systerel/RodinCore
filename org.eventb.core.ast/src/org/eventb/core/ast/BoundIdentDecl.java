/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.sameType;
import static org.eventb.internal.core.ast.FormulaChecks.ensureValidIdentifierName;
import static org.eventb.internal.core.ast.GivenTypeHelper.getGivenTypeIdentifiers;
import static org.eventb.internal.core.parser.SubParsers.BOUND_IDENT_DECL_SUBPARSER;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
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
	
	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeBoundIdentDecl(String, SourceLocation)
	 * @see FormulaFactory#makeBoundIdentDecl(String, SourceLocation, Type)
	 * @since 3.0
	 */
	protected BoundIdentDecl(String name, SourceLocation location,
			Type givenType, FormulaFactory ff) {
		super(BOUND_IDENT_DECL, location, name.hashCode());
		ensureValidIdentifierName(name, ff);
		this.name = name;
		setPredicateVariableCache();
		synthesizeType(ff, givenType);
		// ensures that type was coherent (final type cannot be null if given
		// type was not)
		assert givenType == null || givenType == this.type;
	}

	private void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;

		if (givenType == null)
			return;
		
		assert givenType.isSolved();
		this.freeIdents = getGivenTypeIdentifiers(givenType, ff);
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
	protected boolean equalsInternal(Formula<?> formula) {
		final BoundIdentDecl other = (BoundIdentDecl) formula;
		return name.equals(other.name) && equalsWithAlphaConversion(other);
	}
	
	boolean equalsWithAlphaConversion(BoundIdentDecl other) {
		return sameType(type, other.type);
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
		result.analyzeType(type, this);
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
			synthesizeType(unifier.getFormulaFactory(), inferredType);
		} else {
			synthesizeType(unifier.getFormulaFactory(), null);
		}
		return isTypeChecked();
	}

	/**
	 * Returns the type of this declaration if it is type-checked, or
	 * <code>null</code> otherwise. Once the type of this declaration is known,
	 * it will never change.
	 * 
	 * @return the type of this declaration or <code>null</code>
	 * @see #isTypeChecked()
	 */
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
	protected BoundIdentDecl rewrite(ITypeCheckingRewriter rewriter) {
		return rewriter.rewrite(this);
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
		return rewriter.getBoundIdentDecl(this);
	}
	
	@Override
	public boolean isWDStrict() {
		return true;
	}

}
