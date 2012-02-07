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
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.RELOP_PRED;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.RelationalPredicateInfix;
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
 * @noextend This class is not intended to be subclassed by clients.
 */
public class RelationalPredicate extends Predicate {
	// children
	private final Expression left;
	private final Expression right;
	
	// offset in the corresponding tag interval
	private final static int FIRST_TAG = FIRST_RELATIONAL_PREDICATE;
	
	private static final String EQUAL_ID = "equal";
	private static final String NOTEQUAL_ID = "Not Equal";
	private static final String LT_ID = "Lower Than";
	private static final String LE_ID = "lower or equal";
	private static final String GT_ID = "greater than";
	private static final String GE_ID = "Greater or Equal";
	private static final String IN_ID = "In";
	private static final String NOTIN_ID = "Not In";
	private static final String SUBSET_ID = "Subset";
	private static final String NOTSUBSET_ID = "Not Subset";
	private static final String SUBSETEQ_ID = "Subset or Equal";
	private static final String NOTSUBSETEQ_ID = "Not Subset or Equal";
	
	private static enum Operators implements IOperatorInfo<RelationalPredicate> {
		OP_EQUAL("=", EQUAL_ID, RELOP_PRED, EQUAL),
		OP_NOTEQUAL("≠", NOTEQUAL_ID, RELOP_PRED, NOTEQUAL),
		OP_LT("<", LT_ID, RELOP_PRED, LT),
		OP_LE("≤", LE_ID, RELOP_PRED, LE),
		OP_GT(">", GT_ID, RELOP_PRED, GT),
		OP_GE("\u2265", GE_ID, RELOP_PRED, GE),
		OP_IN("\u2208", IN_ID, RELOP_PRED, IN),
		OP_NOTIN("\u2209", NOTIN_ID, RELOP_PRED, NOTIN),
		OP_SUBSET("\u2282", SUBSET_ID, RELOP_PRED, SUBSET),
		OP_NOTSUBSET("\u2284", NOTSUBSET_ID, RELOP_PRED, NOTSUBSET),
		OP_SUBSETEQ("\u2286", SUBSETEQ_ID, RELOP_PRED, SUBSETEQ),
		OP_NOTSUBSETEQ("\u2288", NOTSUBSETEQ_ID, RELOP_PRED, NOTSUBSETEQ),
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		private final int tag;
		
		private Operators(String image, String id, StandardGroup group, int tag) {
			this.image = image;
			this.id = id;
			this.groupId = group.getId();
			this.tag = tag;
		}

		@Override
		public String getImage() {
			return image;
		}
		
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public String getGroupId() {
			return groupId;
		}

		@Override
		public IParserPrinter<RelationalPredicate> makeParser(int kind) {
			return new RelationalPredicateInfix(kind, tag);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
	}
	
	// For testing purposes
	public static final int TAGS_LENGTH = Operators.values().length;
	
	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {		
			for(Operators operInfo: Operators.values()) {
				grammar.addOperator(operInfo);
			}
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected RelationalPredicate(Expression left, Expression right,
			int tag, SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, combineHashCodes(left.hashCode(), right.hashCode()));
		this.left = left;
		this.right = right;

		assert tag >= FIRST_TAG && tag < FIRST_TAG+TAGS_LENGTH;
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

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-FIRST_TAG];
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final Operators operator = getOperator();
		final int kind = mediator.getKind();
		
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["
				+ getOperatorImage() + "]\n"
				+ left.getSyntaxTree(boundNames, tabs + "\t")
				+ right.getSyntaxTree(boundNames, tabs + "\t");
	}
	
	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		left.isLegible(result);
		right.isLegible(result);
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
		if (acc.childrenSkipped()) {
			return;
		}
		acc.enterChildren();
		left.inspect(acc);
		if (acc.allSkipped()) {
			return;
		}
		acc.nextChild();
		right.inspect(acc);
		acc.leaveChildren();
	}

	@Override
	public Expression getChild(int index) {
		switch (index) {
		case 0:
			return left;
		case 1:
			return right;
		default:
			throw invalidIndex(index);
		}
	}

	@Override
	public int getChildCount() {
		return 2;
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

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
