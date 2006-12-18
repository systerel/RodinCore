/*
 * Created on 20-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * This class represents a literal predicate in an event-B formula.
 * <p>
 * Can take value {BTRUE} or {BFALSE}.
 * </p>
 * 
 * @author François Terrier
 */
public class LiteralPredicate extends Predicate {

	// offset of the corresponding tag-interval in Formula
	protected static final int firstTag = FIRST_LITERAL_PREDICATE;
	protected static final String[] tags = {
		"\u22a4", // BTRUE
		"\u22a5"  // BFALSE
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	
	protected LiteralPredicate(int tag, SourceLocation location,
			FormulaFactory ff) {
		
		super(tag, location, 0);
		assert tag >= firstTag && tag < firstTag+tags.length;
		
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;
		typeChecked = true;
	}

	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		builder.append(tags[getTag() - firstTag]);
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		builder.append(tags[getTag() - firstTag]);
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		// Nothing to do, this subformula is always well-formed.
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return true;
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		// Nothing to do
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["+tags[getTag()-firstTag] + "]" + "\n";
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		// Nothing to do
	}
	
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		return this;
	}

	@Override
	public boolean accept(IVisitor visitor) {
		switch (getTag()) {
		case BTRUE:  return visitor.visitBTRUE(this);
		case BFALSE: return visitor.visitBFALSE(this);
		default:     return true;
		}
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}

	@Override
	public LiteralPredicate applySubstitution(Substitution subst) {
		return this;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		// Nothing to do
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<Position> positions) {
		
		if (filter.retainLiteralPredicate(this)) {
			positions.add(new Position(indexes));
		}
	}

	@Override
	protected Formula getChild(int index) {
		return null;
	}

}
