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
 *     Systerel - added support for specialization
 *     Systerel - add given sets to free identifier cache
 *     Systerel - store factory used to build a formula
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.ast.FormulaChecks.ensureHasType;
import static org.eventb.internal.core.ast.FormulaChecks.ensureValidIdentifierName;
import static org.eventb.internal.core.ast.GivenTypeHelper.getGivenTypeIdentifiers;
import static org.eventb.internal.core.ast.GivenTypeHelper.isGivenSet;
import static org.eventb.internal.core.ast.IdentListMerger.makeMerger;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * This class represents either identifiers occurring free in an event-B
 * formula, or a bound identifier declaration.
 * <p>
 * Identifiers which are bound by a quantifier are instance of the class
 * {@link org.eventb.core.ast.BoundIdentifier} instead.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class FreeIdentifier extends Identifier {

	// Suffix to be used for primed identifiers (after values of assigned variables)
	private static String primeSuffix = "'";
	
	private final String name;
	

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeFreeIdentifier(String, SourceLocation)
	 * @see FormulaFactory#makeFreeIdentifier(String, SourceLocation, Type)
	 * 
	 * @since 3.0
	 */
	protected FreeIdentifier(String name, SourceLocation location, Type type,
			FormulaFactory ff) {
		super(FREE_IDENT, ff, location, name.hashCode());
		ensureValidIdentifierName(name, ff);
		this.name = name;
		ensureSameFactory(type);
		setPredicateVariableCache();
		synthesizeType(ff, type);
		ensureHasType(this, type);
	}

	/*
	 * We must proceed in two steps for constructing the cache of free
	 * identifiers. This is because this identifier is not typed initially and
	 * therefore cannot be merged successfully. So we split the test in two
	 * parts: we verify that this identifier name does not occur in the proposed
	 * type BEFORE setting the type; once the type has been set, we compute the
	 * free identifier cache.
	 */
	@Override
	protected void synthesizeType(FormulaFactory ff, Type proposedType) {
		this.freeIdents = new FreeIdentifier[] {this};
		this.boundIdents = NO_BOUND_IDENT;
		
		if (proposedType == null) {
			return;
		}

		final FreeIdentifier[] givenTypeIdents;
		if (!isGivenSet(name, proposedType)) {
			// Check there is no occurrence of this identifier in given types
			givenTypeIdents = getGivenTypeIdentifiers(proposedType, ff);
			for (final FreeIdentifier givenTypeIdent : givenTypeIdents) {
				if (name.equals(givenTypeIdent.getName())) {
					return;
				}
			}
		} else {
			givenTypeIdents = null;
		}

		setFinalType(proposedType, proposedType);

		if (givenTypeIdents != null) {
			final IdentListMerger merger = makeMerger(freeIdents, givenTypeIdents);
			this.freeIdents = merger.getFreeMergedArray();
			assert !merger.containsError();
		}
	}
	
	/**
	 * Returns the name of this identifier.
	 * 
	 * @return the name of this identifier
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the primed free identifier corresponding to this unprimed
	 * identifier.
	 * <p>
	 * This identifier <b>must not</b> be primed.
	 * </p>
	 * 
	 * @param factory
	 *            a formula factory
	 * @return a copy of this identifier with a prime added
	 */
	public FreeIdentifier withPrime(FormulaFactory factory) {
		
		assert !isPrimed();
		
		FreeIdentifier primedIdentifier = factory.makeFreeIdentifier(
				name + primeSuffix,
				getSourceLocation(),
				getType());
		
		return primedIdentifier;
	}
	
	/**
	 * Returns a declaration of a bound identifier, using this identifier as
	 * model.
	 * 
	 * @param factory
	 *            a formula factory
	 * @return a bound identifier declaration with the same properties as this
	 *         identifier
	 */
	public BoundIdentDecl asDecl(FormulaFactory factory) {
		
		BoundIdentDecl decl = factory.makeBoundIdentDecl(
				name, 
				getSourceLocation(), 
				getType());
		
		return decl;
	}

	/**
	 * Returns a primed declaration of a bound identifier, using this identifier
	 * as model.
	 * <p>
	 * This is a short-hand for <code>ident.withPrime().asDecl()</code>, 
	 * implemented in a more efficient way.
	 * </p>
	 * 
	 * @param factory
	 *            a formula factory
	 * @return a bound identifier declaration with the same properties as this
	 *         identifier, except for the name which is primed
	 */
	public BoundIdentDecl asPrimedDecl(FormulaFactory factory) {
		
		assert !isPrimed();
		
		BoundIdentDecl primedDecl = factory.makeBoundIdentDecl(
				name + primeSuffix, 
				getSourceLocation(), 
				getType());
		
		return primedDecl;
		
	}
	
	/**
	 * Returns the unprimed free identifier corresponding to this primed
	 * identifier.
	 * <p>
	 * This identifier <b>must</b> be primed.
	 * </p>
	 * 
	 * @param factory
	 *            a formula factory
	 * @return a copy of this identifier with the prime removed
	 */
	public FreeIdentifier withoutPrime(FormulaFactory factory) {
		
		assert isPrimed();
		
		FreeIdentifier unprimedIdentifier = factory.makeFreeIdentifier(
				name.substring(0, name.length() - primeSuffix.length()),
				getSourceLocation(),
				getType());
		
		return unprimedIdentifier;
	}
	
	/**
	 * Returns whether this identifier is primed.
	 * 
	 * @return whether this identifier is primed
	 */
	public boolean isPrimed() {
		return name.endsWith(primeSuffix);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [name: " + name + "]" 
				+ typeName + "\n";
	}
	
	@Override
	boolean equalsInternalExpr(Expression expr) {
		final FreeIdentifier other = (FreeIdentifier) expr;
		return name.equals(other.name);
	}
	
	/**
	 * A formula containing free identifiers is well-formed, only if the free identifier
	 * does not appear bound in the formula.
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		if (result.hasBoundIdentDecl(this.name)) {
			result.addProblem(new ASTProblem(this.getSourceLocation(),
					ProblemKind.FreeIdentifierHasBoundOccurences,
					ProblemSeverities.Error, this.name));
			BoundIdentDecl temp = result.getExistingBoundIdentDecl(this.name);
			result.addProblem(new ASTProblem(temp.getSourceLocation(),
					ProblemKind.BoundIdentifierHasFreeOccurences,
					ProblemSeverities.Error, this.name));
		}
		else if (! result.hasFreeIdent(this.name)) {
			result.addFreeIdent(this);
		}
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		setTemporaryType(result.getIdentType(this), result);
		result.analyzeExpression(this);
	}
	
	/**
	 * @since 3.0
	 */
	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		// No child
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		freeIdentSet.add(this);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		names.add(name);
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitFREE_IDENT(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitFreeIdentifier(this);
	}
	
	@Override
	protected Expression rewrite(ITypeCheckingRewriter rewriter) {
		return rewriter.rewrite(this);
	}

	@Override
	public boolean isATypeExpression() {
		Type myType = getType();
		if (myType instanceof PowerSetType) {
			PowerSetType powerSetType = (PowerSetType) myType;
			Type baseType = powerSetType.getBaseType();
			if (baseType instanceof GivenType) {
				GivenType givenType = (GivenType) baseType;
				return givenType.getName().equals(name);
			}
		}
		return false;
	}

	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		return factory.makeGivenType(getName());
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
