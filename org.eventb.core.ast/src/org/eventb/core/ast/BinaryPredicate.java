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
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.BMath.INFIX_PRED;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.GenParser.OverrideException;
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
		
		private Operators(String image, String id, String groupId, int tag) {
			this.image = image;
			this.id = id;
			this.groupId = groupId;
			this.tag = tag;
		}

		public String getImage() {
			return image;
		}
		
		public String getId() {
			return id;
		}
		
		public String getGroupId() {
			return groupId;
		}

		public IParserPrinter<BinaryPredicate> makeParser(int kind) {
			return new BinaryPredicateParser(kind, tag);
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
	
	// offset of the corresponding tag-interval in Formula
	protected final static int firstTag = FIRST_BINARY_PREDICATE;
	protected final static String[] tags = {
		"\u21d2", // LIMP
		"\u21d4"  // LEQV
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;
	
	protected BinaryPredicate(Predicate left, Predicate right, int tag,
			SourceLocation location, FormulaFactory ff) {
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
		
		if (left.isTypeChecked() && right.isTypeChecked()) {
			typeChecked = true;
		}
	}

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-firstTag];
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		BinaryPredicate temp = (BinaryPredicate) other;
		return left.equals(temp.left, withAlphaConversion)
				&& right.equals(temp.right, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result, quantifiedIdentifiers);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return left.solveType(unifier) & right.solveType(unifier);
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

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		left.isLegible(result, quantifiedIdents);
		if (result.isSuccess()) {
			right.isLegible(result, quantifiedIdents);
		}
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
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Predicate newLeft = left.bindTheseIdents(binding, offset, factory);
		Predicate newRight = right.bindTheseIdents(binding, offset, factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeBinaryPredicate(getTag(), newLeft, newRight, getSourceLocation());
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

	private Predicate getWDPredicateLIMP(FormulaFactory formulaFactory) {
		Predicate conj0 = left.getWDPredicateRaw(formulaFactory);
		Predicate conj1 = getWDSimplifyI(formulaFactory, left, right.getWDPredicateRaw(formulaFactory));
		return getWDSimplifyC(formulaFactory, conj0, conj1);
	}
	
	private Predicate getWDPredicateLEQV(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, left, right);
	}
	
	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		switch (getTag()) {
		case LIMP: return getWDPredicateLIMP(formulaFactory);
		case LEQV: return getWDPredicateLEQV(formulaFactory);
		default:   assert false; return null;
		}
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final Predicate newLeft = left.rewrite(rewriter);
		final Predicate newRight = right.rewrite(rewriter);
		final BinaryPredicate before;
		if (newLeft == left && newRight == right) {
			before = this;
		} else {
			before = rewriter.getFactory().makeBinaryPredicate(getTag(),
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
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		left.getPositions(filter, indexes, positions);
		indexes.incrementTop();
		right.getPositions(filter, indexes, positions);
		indexes.pop();
	}

	@Override
	protected Formula<Predicate> getChild(int index) {
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

}
