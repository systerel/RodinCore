/*
 * Created on 10-jun-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.BindReplacement;
import org.eventb.internal.core.ast.FreeReplacement;
import org.eventb.internal.core.ast.Info;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Replacement;
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
	
	// Name without trailing prime
	private final String bareName;
	
	// Full name
	private final String name;
	
	protected FreeIdentifier(String name, int tag, SourceLocation location) {
		super(tag, location, name.hashCode());
		this.name = name;
		final int endIndex = name.length() - 1;
		if (name.charAt(endIndex) == '\'') {
			this.bareName = name.substring(0, endIndex);
		} else {
			this.bareName = name;
		}
		assert tag == Formula.FREE_IDENT;
		assert name != null;
		assert name.length() != 0;
	}
	
	/**
	 * Returns the bare name of this identifier, that is its name with a
	 * trailing prime removed.
	 * 
	 * @return the bare name of this identifier
	 */
	public String getBareName() {
		return bareName;
	}

	/**
	 * Returns the full name of this identifier.
	 * 
	 * @return the full name of this identifier
	 */
	public String getName() {
		return name;
	}

	/**
	 * Tells whether this identifier is primed.
	 * 
	 * @return <code>true</code> iff this identifier is primed
	 */
	public boolean isPrimed() {
		return name != bareName;
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
		setType(result.getIdentType(this), result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		return finalizeType(true, unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		freeIdents.add(this);
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
		return factory.makeBoundIdentifier(index + offset, getSourceLocation());
	}
	
	@Override
	public boolean accept(IVisitor visitor) {
		return visitor.visitFREE_IDENT(this);
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return true;
	}

	@Override
	protected Expression substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		if (replacement instanceof FreeReplacement) {
			Info info = ((FreeReplacement) replacement).getInfo(this);
			if (info == null)
				return this;
			else {
				Expression expr = info.getExpression();
				
				// the types must correspond!
				assert this.getType() == null || this.getType().equals(expr.getType());
				
				if (info.isIndexClosed())
					return expr;
				else
					return expr.adjustIndicesAbsolute(noOfBoundVars, formulaFactory);
			}
		} else if (replacement instanceof BindReplacement) {
			Integer index = ((BindReplacement) replacement).getIndex(this);
			
			if(index == null) 
				return this;
			else {
				BoundIdentifier newBound = formulaFactory.makeBoundIdentifier(index + noOfBoundVars, getSourceLocation());
				newBound.setType(getType(), null);
				return newBound;
			}
			
		} else
			return this;
	}

}
