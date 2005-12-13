/*
 * Created on 10-jun-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
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
 */
public class BoundIdentDecl extends Formula<BoundIdentDecl> {
	
	private final String name;
	private Type type;
	
	protected BoundIdentDecl(String name, int tag, SourceLocation location) {
		super(tag, location, name.hashCode());
		this.name = name;
		assert tag == Formula.BOUND_IDENT_DECL;
		assert name != null;
		assert name.length() != 0;
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
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
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
		BoundIdentDecl otherDecl = (BoundIdentDecl) other;
		boolean result = type == null ? otherDecl.type == null : type.equals(otherDecl.type);
		return result && name.equals(((BoundIdentDecl) other).name);
	}
	
	/*
	 * A formula containing free identifiers is well-formed, only if the free identifier
	 * does not appear bound in the formula.
	 */
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
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
	public BoundIdentDecl flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		if (type == null) {
			type = result.newFreshVariable(getSourceLocation());
		}
	}
	
	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		// this method should never be called
		assert false;
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		type = unifier.solve(type);
		if (type.isSolved()) {
			return true;
		}
		type = null;
		return false;
	}

	public Type getType() {
		return type;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
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
	protected boolean isWellFormed(int noOfBoundVars) {
		return true;
	}

	@Override
	protected BoundIdentDecl substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		// this method should never be called
		assert false;
		return null;
	}

	@Override
	public boolean isTypeChecked() {
		return type != null;
	}

}
