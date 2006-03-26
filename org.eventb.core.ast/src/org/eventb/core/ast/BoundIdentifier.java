/*
 * Created on 03-jun-2005
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
 * Represents a bound identifier inside an event-B formula.
 * <p>
 * A bound identifier is encoded using the De Bruijn notation. The corresponding
 * quantifier (which is a {@link BoundIdentDecl}) is retrieved using the index
 * of the bound identifier. Index 0 represents the nearest quantifier up in the
 * formula.
 * </p>
 * 
 * TODO: give examples and a better specification.
 * 
 * @author François Terrier
 */
public class BoundIdentifier extends Identifier {
	
	// index of this bound identifier
	// helps find its corresponding declaration in the formula
	private final int boundIndex;

	protected BoundIdentifier(int boundIndex, int tag, SourceLocation location,
			Type type) {

		super(tag, location, boundIndex);
		assert tag == Formula.BOUND_IDENT;
		assert 0 <= boundIndex;
		
		this.boundIndex = boundIndex;
		
		this.freeIdents = NO_FREE_IDENTS;
		this.boundIdents = new BoundIdentifier[] {this};
		this.setType(type, null);
	}

	/**
	 * Returns the De Bruijn index of this identifier.
	 * 
	 * @return the index of this bound identifier
	 */
	public int getBoundIndex() {
		return boundIndex;
	}

	/**
	 * Returns the declaration of this identifier.
	 * 
	 * @param boundIdentDecls
	 *            declarations of bound identifier above this node
	 * @return the declaration of this bound identifier
	 */
	public BoundIdentDecl getDeclaration(BoundIdentDecl[] boundIdentDecls) {
		return boundIdentDecls[boundIdentDecls.length - boundIndex - 1];
	}

	private static String resolveIndex(int index, String[] boundIdents) {
		if (index < boundIdents.length) {
			return boundIdents[boundIdents.length - index - 1];
		}
		return null;
	}
	
	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {
		return toStringFullyParenthesized(boundNames);
	}
	
	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		String result = resolveIndex(boundIndex, boundNames);
		if (result == null) {
			// Fallback default in case this can not be resolved.
			result = "[[" + boundIndex + "]]";
		}
		return result;
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " [name: "
				+ toStringFullyParenthesized(boundNames) + "] [index: "
				+ boundIndex + "]" + typeName + "\n";
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		// this has now been moved to isWellFormed because the user cannot cause this problem!
//		if (boundIndex >= quantifiedIdents.length) {
//			result.addProblem(new LegibilityProblem(getSourceLocation(),Problem.BoundIdentifierIndexOutOfBounds,new String[]{""},ProblemSeverities.Error));
//		}
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& boundIndex == ((BoundIdentifier) other).boundIndex;
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final BoundIdentDecl decl = getDeclaration(quantifiedIdentifiers);
		assert decl != null : "Bound variable without a declaration";
		setType(decl.getType(), result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		return finalizeType(true, unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		if (boundIndex < offset) {
			// Locally bound, nothing to do
		}
		else {
			names.add(resolveIndex(boundIndex - offset, boundNames));
		}
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		if (boundIndex < offset) {
			//  Tightly bound so not changed
			return this;
		}
		return factory.makeBoundIdentifier(
				boundIndex + binding.size(), 
				getSourceLocation(),
				getType());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitBOUND_IDENT(this);
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return boundIndex < noOfBoundVars;
	}

	@Override
	public Expression applySubstitution(Substitution subst) {
		return subst.getReplacement(this);
	}

}
