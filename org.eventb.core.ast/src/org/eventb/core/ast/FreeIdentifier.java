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
 *     Systerel - added support for specialization
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
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
	
	protected FreeIdentifier(String name, int tag, SourceLocation location,
			Type type, FormulaFactory ff) {
		super(tag, location, name.hashCode());
		assert tag == Formula.FREE_IDENT;
		assert name != null;
		assert name.length() != 0;
		assert ff.isValidIdentifierName(name);

		this.name = name;
		setPredicateVariableCache();
		synthesizeType(ff, type);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = new FreeIdentifier[] {this};
		this.boundIdents = NO_BOUND_IDENT;
		
		if (givenType == null) {
			return;
		}
		setFinalType(givenType, givenType);
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
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return hasSameType(other)
				&& name.equals(((FreeIdentifier) other).name);
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
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
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
