/*
 * Created on 10-jun-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
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
 */
public class FreeIdentifier extends Identifier {
	
	private final String name;
	
	protected FreeIdentifier(String name, int tag, SourceLocation location,
			Type type, FormulaFactory ff) {
		super(tag, location, name.hashCode());
		assert tag == Formula.FREE_IDENT;
		assert name != null;
		assert name.length() != 0;

		this.name = name;
		synthesizeType(ff, type);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = new FreeIdentifier[] {this};
		this.boundIdents = NO_BOUND_IDENTS;
		
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
	 * Returns the primed free identifier corresponding this identifier.
	 * 
	 * @param factory
	 *            a formula factory
	 * An exception is thrown if it is already primed.
	 * @return The identifier with a prime appended.
	 */
	public FreeIdentifier withPrime(FormulaFactory factory) {
		
		assert !isPrimed();
		
		FreeIdentifier primedIdentifier = factory.makeFreeIdentifier(
				name + "'",
				getSourceLocation(),
				getType());
		
		return primedIdentifier;
	}
	
	/**
	 * Returns a declaration of a bound identifier,
	 * using as model a free occurrence of the same identifier.
	 * 
	 * @param factory
	 *            a formula factory
	 * @return a bound identifier declaration
	 */
	public BoundIdentDecl asDecl(FormulaFactory factory) {
		
		BoundIdentDecl decl = factory.makeBoundIdentDecl(
				name, 
				getSourceLocation(), 
				getType());
		
		return decl;
	}

	/**
	 * Returns a primed declaration of a bound identifier,
	 * using as model a free occurrence of the same identifier.
	 * 
	 * @param factory
	 *            a formula factory
	 * @return a bound identifier declaration
	 */
	public BoundIdentDecl asPrimedDecl(FormulaFactory factory) {
		
		assert !isPrimed();
		
		BoundIdentDecl primedDecl = factory.makeBoundIdentDecl(
				name + "'", 
				getSourceLocation(), 
				getType());
		
		return primedDecl;
		
	}
	
	/**
	 * Returns the unprimed free identifier corresponding this identifier.
	 * 
	 * @param factory
	 *            a formula factory
	 * An exception is thrown if it is not primed.
	 * @return The identifier with the prime removed.
	 */
	public FreeIdentifier withoutPrime(FormulaFactory factory) {
		
		assert isPrimed();
		
		FreeIdentifier unprimedIdentifier = factory.makeFreeIdentifier(
				name.substring(0, name.length() - 1),
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
		return name.charAt(name.length()-1) == '\'';
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {
		return name;
	}
	
	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return name;
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [name: " + name + "]" 
				+ typeName + "\n";
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& name.equals(((FreeIdentifier) other).name);
	}
	
	/*
	 * A formula containing free identifiers is well-formed, only if the free identifier
	 * does not appear bound in the formula.
	 */
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
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
	public Expression flatten(FormulaFactory factory) {
		return this;
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
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Integer index = binding.get(name);
		if (index == null) {
			// Not in the binding, so should remain free, so no change.
			return this;
		}
		return factory.makeBoundIdentifier(
				index + offset, 
				getSourceLocation(),
				getType());
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitFREE_IDENT(this);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	public Expression applySubstitution(Substitution subst) {
		return subst.getReplacement(this);
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

}
