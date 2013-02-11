/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *     Systerel - store factory used to build a formula
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.INFIX_PRED;
import static org.eventb.internal.core.ast.FormulaChecks.ensureTagInRange;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
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
import org.eventb.internal.core.parser.SubParsers.BinaryPredicateParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * BinaryPredicate is the base class for all binary predicates in an event-B
 * formula.
 * <p>
 * It can only accept {LIMP, LEQV}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BinaryPredicate extends Predicate {
	
	// offset of the corresponding tag-interval in Formula
	private final static int FIRST_TAG = FIRST_BINARY_PREDICATE;

	private static final String LIMP_ID = "Logical Implication";
	private static final String LEQV_ID = "Equivalent";
	
	private static enum Operators implements IOperatorInfo<BinaryPredicate> {
		OP_LIMP("\u21d2", LIMP_ID, INFIX_PRED, LIMP),
		OP_LEQV("\u21d4", LEQV_ID, INFIX_PRED, LEQV),
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
		public IParserPrinter<BinaryPredicate> makeParser(int kind) {
			return new BinaryPredicateParser(kind, tag);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
	}

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

	// Left and right children.
	// Are never null by construction.
	private final Predicate left;
	private final Predicate right;
	
	// For testing purposes
	public static final int TAGS_LENGTH = Operators.values().length;
	
	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeBinaryPredicate(int, Predicate, Predicate,
	 *      SourceLocation)
	 */
	protected BinaryPredicate(Predicate left, Predicate right, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, ff, location, combineHashCodes(left.hashCode(), right.hashCode()));
		this.left = left;
		this.right = right;
		ensureTagInRange(tag, FIRST_TAG, TAGS_LENGTH);
		ensureSameFactory(this.left, this.right);
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
		
		if (left.isTypeChecked() && right.isTypeChecked()) {
			typeChecked = true;
		}
	}

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-FIRST_TAG];
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}
	
	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		final BinaryPredicate other = (BinaryPredicate) formula;
		return left.equals(other.left) && right.equals(other.right);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result, quantifiedIdentifiers);
	}
	
	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		left.solveType(unifier);
		right.solveType(unifier);
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final Operators operator = getOperator();
		final int kind = mediator.getKind();
		
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getOperatorImage() + "]\n"
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

	/**
	 * Returns the predicate on the left-hand side of this node.
	 * 
	 * @return the predicate on the left-hand side
	 */
	public Predicate getLeft() {
		return left;
	}

	/**
	 * Returns the predicate on the right-hand side of this node.
	 * 
	 * @return the predicate on the right-hand side
	 */
	public Predicate getRight() {
		return right;
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
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case LIMP: goOn = visitor.enterLIMP(this); break;
		case LEQV: goOn = visitor.enterLEQV(this); break;
		default:   assert false;
		}

		if (goOn) goOn = left.accept(visitor);
		
		if (goOn) {
			switch (getTag()) {
			case LIMP: goOn = visitor.continueLIMP(this); break;
			case LEQV: goOn = visitor.continueLEQV(this); break;
			default:   assert false;
			}
		}
		
		if (goOn) goOn = right.accept(visitor);
		
		switch (getTag()) {
		case LIMP: return visitor.exitLIMP(this);
		case LEQV: return visitor.exitLEQV(this);
		default:   return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBinaryPredicate(this);
	}

	@Override
	protected Predicate rewrite(ITypeCheckingRewriter rewriter) {
		final Predicate newLeft = left.rewrite(rewriter);
		final Predicate newRight = right.rewrite(rewriter);
		final BinaryPredicate before;
		if (newLeft == left && newRight == right) {
			before = this;
		} else {
			before = rewriter.getFactory().makeBinaryPredicate(getTag(),
					newLeft, newRight, getSourceLocation());
		}
		return rewriter.rewrite(this, before);
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
	public Predicate getChild(int index) {
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
		Predicate newLeft = left;
		Predicate newRight = right;
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
		return rewriter.factory.makeBinaryPredicate(getTag(), newLeft, newRight,
				getSourceLocation());
	}

	@Override
	public boolean isWDStrict() {
		return getTag() == LEQV;
	}

}
