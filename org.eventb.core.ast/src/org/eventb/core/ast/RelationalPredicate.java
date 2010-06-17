/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
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
 * @since 1.0
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
			int tag, SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, combineHashCodes(left.hashCode(), right.hashCode()));
		this.left = left;
		this.right = right;

		assert tag >= firstTag && tag < firstTag+tags.length;
		assert left != null;
		assert right != null;
		
		setPredicateVariableCache(this.left, this.right);
		synthesizeType(ff);
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff) {
		IdentListMerger freeIdentMerger = 
			IdentListMerger.makeMerger(left.freeIdents, right.freeIdents);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = 
			IdentListMerger.makeMerger(left.boundIdents, right.boundIdents);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		// Fast exit if children are not typed
		// (the most common case where type synthesis can't be done)
		if (! left.isTypeChecked() || ! right.isTypeChecked()) {
			return;
		}
		Type leftType = left.getType();
		Type rightType = right.getType();
		
		final Type alpha;
		switch(getTag()) {
		case Formula.EQUAL:
		case Formula.NOTEQUAL:
			if (! leftType.equals(rightType)) {
				return;
			}
			break;
		case Formula.LT:
		case Formula.LE:
		case Formula.GT:
		case Formula.GE:
			if (! (leftType instanceof IntegerType) ||
					! (rightType instanceof IntegerType)) {
				return;
			}
			break;
		case Formula.IN:
		case Formula.NOTIN:
			alpha = rightType.getBaseType();
			if (alpha == null || ! alpha.equals(leftType)) {
				return;
			}
			break;
		case Formula.SUBSET:
		case Formula.NOTSUBSET:
		case Formula.SUBSETEQ:
		case Formula.NOTSUBSETEQ:
			alpha = leftType.getBaseType();
			if (alpha == null || ! alpha.equals(rightType.getBaseType())) {
				return;
			}
			break;
		default:
			assert false;
			return;
		}
		typeChecked = true;
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
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		left.toString(builder, false, getTag(), boundNames, withTypes);
		builder.append(tags[getTag() - firstTag]);
		right.toString(builder, true, getTag(), boundNames, withTypes);
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		builder.append('(');
		left.toStringFullyParenthesized(builder, boundNames);
		builder.append(')');
		builder.append(tags[getTag() - firstTag]);
		builder.append('(');
		right.toStringFullyParenthesized(builder, boundNames);
		builder.append(')');
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
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		RelationalPredicate temp = (RelationalPredicate) other;
		return left.equals(temp.left, withAlphaConversion)
			&& right.equals(temp.right, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result,quantifiedIdentifiers);
		switch(getTag()) {
		case Formula.EQUAL:
		case Formula.NOTEQUAL:
			result.unify(left.getType(), right.getType(), this);
			break;
		case Formula.LT:
		case Formula.LE:
		case Formula.GT:
		case Formula.GE:
			Type type = result.makeIntegerType();
			result.unify(left.getType(), type, this);
			result.unify(right.getType(), type, this);
			break;
		case Formula.IN:
		case Formula.NOTIN:
			result.unify(right.getType(), result.makePowerSetType(left.getType()), this);
			break;
		case Formula.SUBSET:
		case Formula.NOTSUBSET:
		case Formula.SUBSETEQ:
		case Formula.NOTSUBSETEQ:
			TypeVariable alpha = result.newFreshVariable(null);
			type = result.makePowerSetType(alpha);
			result.unify(left.getType(), type, this);
			result.unify(right.getType(), type, this);
			break;
		default:
			assert false;
		}
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return left.solveType(unifier) & right.solveType(unifier);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		left.collectFreeIdentifiers(freeIdentSet);
		right.collectFreeIdentifiers(freeIdentSet);
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

		if (goOn) {
			switch (getTag()) {
			case EQUAL:       goOn = visitor.continueEQUAL(this);       break;
			case NOTEQUAL:    goOn = visitor.continueNOTEQUAL(this);    break;
			case LT:          goOn = visitor.continueLT(this);          break;
			case LE:          goOn = visitor.continueLE(this);          break;
			case GT:          goOn = visitor.continueGT(this);          break;
			case GE:          goOn = visitor.continueGE(this);          break;
			case IN:          goOn = visitor.continueIN(this);          break;
			case NOTIN:       goOn = visitor.continueNOTIN(this);       break;
			case SUBSET:      goOn = visitor.continueSUBSET(this);      break;
			case NOTSUBSET:   goOn = visitor.continueNOTSUBSET(this);   break;
			case SUBSETEQ:    goOn = visitor.continueSUBSETEQ(this);    break;
			case NOTSUBSETEQ: goOn = visitor.continueNOTSUBSETEQ(this); break;
			default:          assert false;
			}
		}
		
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
	public void accept(ISimpleVisitor visitor) {
		visitor.visitRelationalPredicate(this);		
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, left, right);
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final Expression newLeft = left.rewrite(rewriter);
		final Expression newRight = right.rewrite(rewriter);
		final RelationalPredicate before;
		if (newLeft == left && newRight == right) {
			before = this;
		} else {
			before = rewriter.getFactory().makeRelationalPredicate(getTag(),
					newLeft, newRight, getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		left.addGivenTypes(set);
		right.addGivenTypes(set);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.enterChildren();
		left.inspect(acc);
		acc.nextChild();
		right.inspect(acc);
		acc.leaveChildren();
	}

	@Override
	protected Formula<Expression> getChild(int index) {
		switch (index) {
		case 0:
			return left;
		case 1:
			return right;
		default:
			return null;
		}
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		IPosition pos;
		indexes.push(0);
		pos = left.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.incrementTop();
		pos = right.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		Expression newLeft = left;
		Expression newRight = right;
		switch (index) {
		case 0:
			newLeft = rewriter.rewrite(left);
			break;
		case 1:
			newRight = rewriter.rewrite(right);
			break;
		default:
			throw new IllegalArgumentException("Position is outside the formula");
		}
		return rewriter.factory.makeRelationalPredicate(getTag(), newLeft,
				newRight, getSourceLocation());
	}

}
