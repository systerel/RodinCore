/*
 * Created on 11-may-2005
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
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * AtomicExpression is the class for all atomic expressions in an event-B formula.
 * <p>
 * It is a terminal expression and therefore has no children.
 * It can only accept {INTEGER, NATURAL, NATURAL1, BOOL, TRUE, FALSE, EMPTYSET, KPRED, KSUCC}.
 * </p>
 * 
 * @author François Terrier
 */
public class AtomicExpression extends Expression {
	
	// offset of the corresponding tag-interval in Formula
	protected static final int firstTag = FIRST_ATOMIC_EXPRESSION;
	protected static final String tags[] = {
		"\u2124",  // INTEGER
		"\u2115",  // NATURAL
		"\u21151", // NATURAL1
		"BOOL",    // BOOL
		"TRUE",    // TRUE
		"FALSE",   // FALSE
		"\u2205",  // EMPTYSET
		"pred",    // KPRED
		"succ"     // KSUCC
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected AtomicExpression(int tag, SourceLocation location) {
		super(tag, location, 0);
		assert tag >= firstTag && tag < firstTag+tags.length;
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return tags[getTag()-firstTag];
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return tags[getTag()-firstTag];
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		return;
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, null);
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		return this;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		Type resultType;
		
		switch (getTag()) {
		case Formula.INTEGER:
		case Formula.NATURAL:
		case Formula.NATURAL1:
			resultType = result.makePowerSetType(result.makeIntegerType());
			break;
		case Formula.BOOL:
			resultType = result.makePowerSetType(result.makeBooleanType());
			break;
		case Formula.TRUE:
		case Formula.FALSE:
			resultType = result.makeBooleanType();
			break;
		case Formula.EMPTYSET:
			TypeVariable alpha = result.newFreshVariable(getSourceLocation());
			resultType = result.makePowerSetType(alpha);
			break;
		default:
			assert false;
			resultType = null;
		}
		setType(resultType, result);
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		return finalizeType(true, unifier);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " ["
				+ tags[getTag() - firstTag] + "]"  + typeName + "\n";
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		// Nothing to do
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		// Nothing to do
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		return this;
	}

	@Override
	public boolean accept(IVisitor visitor) {
		switch (getTag()) {
		case INTEGER:  return visitor.visitINTEGER(this);
		case NATURAL:  return visitor.visitNATURAL(this);
		case NATURAL1: return visitor.visitNATURAL1(this);
		case BOOL:     return visitor.visitBOOL(this);
		case TRUE:     return visitor.visitTRUE(this);
		case FALSE:    return visitor.visitFALSE(this);
		case EMPTYSET: return visitor.visitEMPTYSET(this);
		case KPRED:    return visitor.visitKPRED(this);
		case KSUCC:    return visitor.visitKSUCC(this);
		default:       return true;
		}
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return true;
	}

	@Override
	public AtomicExpression applySubstitution(Substitution subst, FormulaFactory ff) {
		return this;
	}

}
