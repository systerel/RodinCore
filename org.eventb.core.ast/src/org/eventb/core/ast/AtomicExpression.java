/*
 * Created on 11-may-2005
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

	protected AtomicExpression(int tag, SourceLocation location, Type type,
			FormulaFactory factory) {
		super(tag, location, 0);
		assert tag >= firstTag && tag < firstTag+tags.length;

		synthesizeType(factory, type);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;

		final Type resultType;
		switch (getTag()) {
		case Formula.INTEGER:
		case Formula.NATURAL:
		case Formula.NATURAL1:
			resultType = ff.makePowerSetType(ff.makeIntegerType());
			break;
		case Formula.BOOL:
			resultType = ff.makePowerSetType(ff.makeBooleanType());
			break;
		case Formula.TRUE:
		case Formula.FALSE:
			resultType = ff.makeBooleanType();
			break;
		case Formula.EMPTYSET:
			if (givenType == null) {
				return;
			}
			assert givenType instanceof PowerSetType;
			resultType = givenType;
			break;
		case Formula.KPRED:
		case Formula.KSUCC:
			resultType = ff.makeRelationalType(
					ff.makeIntegerType(),
					ff.makeIntegerType()
			);
			break;
		default:
			assert false;
			return;
		}
		setFinalType(resultType, givenType);
	}
	
	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {
		
		final String image = tags[getTag()-firstTag];
		if (withTypes && getTag() == EMPTYSET && isTypeChecked()) {
			builder.append('(');
			builder.append(image);
			builder.append(" \u2982 ");
			builder.append(getType());
			builder.append(')');
		} else {
			builder.append(image);
		}
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {
		
		builder.append(tags[getTag()-firstTag]);
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		return;
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
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
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {

		final Type resultType;
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
		case Formula.KPRED:
		case Formula.KSUCC:
			resultType = result.makeRelationalType(
					result.makeIntegerType(),
					result.makeIntegerType()
			);
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " ["
				+ tags[getTag() - firstTag] + "]"  + typeName + "\n";
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
	public AtomicExpression applySubstitution(Substitution subst) {
		return this;
	}

	@Override
	public boolean isATypeExpression() {
		int tag = getTag();
		return tag == INTEGER || tag == BOOL;
	}

	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		switch (getTag()) {
		case INTEGER:
			return factory.makeIntegerType();
		case BOOL:
			return factory.makeBooleanType();
		default:
			throw new InvalidExpressionException();
		}
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		getType().addGivenTypes(set);
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<Position> positions) {
		
		if (filter.retainAtomicExpression(this)) {
			positions.add(new Position(indexes));
		}
	}

	@Override
	protected Formula getChild(int index) {
		return null;
	}

	@Override
	protected Position getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

}
