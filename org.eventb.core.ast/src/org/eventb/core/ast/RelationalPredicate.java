/*
 * Created on 11-may-2005
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
import org.eventb.internal.core.typecheck.TypeVariable;


/**
 * RelationalPredicate is the class for all relational predicates of an event-B
 * formula.
 * <p>
 * It can accept tags {EQUAL, NOTEQUAL, LT, LE, GT, GE, IN, NOTIN, SUBSET,
 * NOTSUBSET, SUBSETEQ, NOTSUBSETEQ}.
 * </p>
 * 
 * @author François Terrier
 */
public class RelationalPredicate extends Predicate {
	// children
	private final Expression left;
	private final Expression right;
	
	// offset in the corresponding tag interval
	protected final static int firstTag = FIRST_RELATIONAL_PREDICATE;
	protected final static String[] tags = {
		"=",      // EQUAL
		"\u2260", // NOTEQUAL
		"<",      // LT
		"\u2264", // LE
		">",      // GT
		"\u2265", // GE
		"\u2208", // IN
		"\u2209", // NOTIN
		"\u2282", // SUBSET
		"\u2284", // NOTSUBSET
		"\u2286", // SUBSETEQ
		"\u2288"  // NOTSUBSETEQ
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	protected RelationalPredicate(Expression left, Expression right,
			int tag, SourceLocation location) {
		super(tag, location, combineHashCodes(left.hashCode(), right.hashCode()));
		assert tag >= firstTag && tag < firstTag+tags.length;
		assert left != null;
		assert right != null;
		this.left = left;
		this.right = right;
	}
	
	/**
	 * Returns the expression on the left-hand side of this node.
	 * 
	 * @return the left-hand side of this node.
	 */
	public Expression getLeft() {
		return left;
	}
	
	/**
	 * Returns the expression on the right-hand side of this node.
	 * 
	 * @return the right-hand side of this node.
	 */
	public Expression getRight() {
		return right;
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag, String[] boundNames) {
		return left.toString(false,getTag(),boundNames)+tags[getTag()-firstTag]+right.toString(true,getTag(),boundNames);
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		return "("+left.toStringFullyParenthesized(boundNames)+")"+tags[getTag()-firstTag]+
		      "("+right.toStringFullyParenthesized(boundNames)+")";
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["
				+ tags[getTag() - firstTag] + "]\n"
				+ left.getSyntaxTree(boundNames, tabs + "\t")
				+ right.getSyntaxTree(boundNames, tabs + "\t");
	}
	
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		left.isLegible(result, quantifiedIdents);
		if (result.isSuccess()) {
			right.isLegible(result, quantifiedIdents);
		}
	}
	
	@Override
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		RelationalPredicate temp = (RelationalPredicate) other;
		return left.equals(temp.left, withAlphaConversion)
			&& right.equals(temp.right, withAlphaConversion);
	}

	@Override
	public Predicate flatten(FormulaFactory factory) {
		return factory.makeRelationalPredicate(getTag(), left.flatten(factory),right.flatten(factory),getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final SourceLocation loc = getSourceLocation();
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result,quantifiedIdentifiers);
		switch(getTag()) {
		case Formula.EQUAL:
		case Formula.NOTEQUAL:
			result.unify(left.getType(), right.getType(), loc);
			break;
		case Formula.LT:
		case Formula.LE:
		case Formula.GT:
		case Formula.GE:
			Type type = result.makeIntegerType();
			result.unify(left.getType(), type, loc);
			result.unify(right.getType(), type, loc);
			break;
		case Formula.IN:
		case Formula.NOTIN:
			result.unify(right.getType(), result.makePowerSetType(left.getType()), loc);
			break;
		case Formula.SUBSET:
		case Formula.NOTSUBSET:
		case Formula.SUBSETEQ:
		case Formula.NOTSUBSETEQ:
			TypeVariable alpha = result.newFreshVariable(null);
			type = result.makePowerSetType(alpha);
			result.unify(left.getType(), type, loc);
			result.unify(right.getType(), type, loc);
			break;
		}
	}
	
	@Override
	protected boolean solveType(TypeUnifier unifier) {
		boolean success = left.solveType(unifier) & right.solveType(unifier);
		return finalizeTypeCheck(success);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdents) {
		left.collectFreeIdentifiers(freeIdents);
		right.collectFreeIdentifiers(freeIdents);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		left.collectNamesAbove(names, boundNames, offset);
		right.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Expression newLeft = left.bindTheseIdents(binding, offset, factory);
		Expression newRight = right.bindTheseIdents(binding, offset, factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeRelationalPredicate(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case EQUAL:       goOn = visitor.enterEQUAL(this);       break;
		case NOTEQUAL:    goOn = visitor.enterNOTEQUAL(this);    break;
		case LT:          goOn = visitor.enterLT(this);          break;
		case LE:          goOn = visitor.enterLE(this);          break;
		case GT:          goOn = visitor.enterGT(this);          break;
		case GE:          goOn = visitor.enterGE(this);          break;
		case IN:          goOn = visitor.enterIN(this);          break;
		case NOTIN:       goOn = visitor.enterNOTIN(this);       break;
		case SUBSET:      goOn = visitor.enterSUBSET(this);      break;
		case NOTSUBSET:   goOn = visitor.enterNOTSUBSET(this);   break;
		case SUBSETEQ:    goOn = visitor.enterSUBSETEQ(this);    break;
		case NOTSUBSETEQ: goOn = visitor.enterNOTSUBSETEQ(this); break;
		default:          assert false;
		}

		if (goOn) goOn = left.accept(visitor);
		if (goOn) goOn = right.accept(visitor);
		
		switch (getTag()) {
		case EQUAL:       return visitor.exitEQUAL(this);
		case NOTEQUAL:    return visitor.exitNOTEQUAL(this);
		case LT:          return visitor.exitLT(this);
		case LE:          return visitor.exitLE(this);
		case GT:          return visitor.exitGT(this);
		case GE:          return visitor.exitGE(this);
		case IN:          return visitor.exitIN(this);
		case NOTIN:       return visitor.exitNOTIN(this);
		case SUBSET:      return visitor.exitSUBSET(this);
		case NOTSUBSET:   return visitor.exitNOTSUBSET(this);
		case SUBSETEQ:    return visitor.exitSUBSETEQ(this);
		case NOTSUBSETEQ: return visitor.exitNOTSUBSETEQ(this);
		default:          return true;
		}
	}

	@Override
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, left, right);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		return left.isWellFormed(noOfBoundVars) && right.isWellFormed(noOfBoundVars);
	}

	@Override
	protected Predicate substituteAll(int noOfBoundVars, Replacement replacement, FormulaFactory formulaFactory) {
		Expression newLeft = left.substituteAll(noOfBoundVars, replacement, formulaFactory);
		Expression newRight = right.substituteAll(noOfBoundVars, replacement, formulaFactory);
		if(newLeft == left && newRight == right)
			return this;
		else
			return formulaFactory.makeRelationalPredicate(getTag(), newLeft, newRight, getSourceLocation());
	}

}
