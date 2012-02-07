/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
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

import static org.eventb.core.ast.extension.StandardGroup.NOT_PRED;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.UnaryPredicateParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * UnaryPredicate is the base class for all unary predicates in an event-B formula.
 * <p>
 * It can accept tags {NOT}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class UnaryPredicate extends Predicate {
	
	// offset in the corresponding tag interval
	private static final int firstTag = FIRST_UNARY_PREDICATE;
	
	/**
	 * @since 2.0
	 */
	public static final String NOT_ID = "Not";

	private static enum Operators implements IOperatorInfo<UnaryPredicate> {
		OP_NOT("\u00ac", NOT_ID, NOT_PRED, NOT);
		
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
		public IParserPrinter<UnaryPredicate> makeParser(int kind) {
			return new UnaryPredicateParser(kind, tag, false);
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

	protected final Predicate child;

	protected UnaryPredicate(Predicate child, int tag, SourceLocation location,
			FormulaFactory ff) {

		super(tag, location, child.hashCode());
		this.child = child;
		
		assert tag >= firstTag && tag < firstTag + TAGS_LENGTH;
		assert child != null;
		
		setPredicateVariableCache(this.child);
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		
		if (! child.isTypeChecked())
			return;
		typeChecked = true;
	}
	
	/**
	 * Returns the child of this node.
	 * 
	 * @return the child predicate of this node
	 */
	public Predicate getChild() {
		return child;
	}
	
	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-firstTag];
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		UnaryPredicate temp = (UnaryPredicate) other;
		return child.equals(temp.child, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result, quantifiedIdentifiers);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
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
		return tabs + this.getClass().getSimpleName() + " [" + getOperatorImage()	+ "]\n"
		+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		child.isLegible(result);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		child.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		child.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Predicate newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeUnaryPredicate(getTag(), newChild, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case NOT: goOn = visitor.enterNOT(this); break;
		default:  assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case NOT: return visitor.exitNOT(this);
		default:  return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitUnaryPredicate(this);		
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final Predicate newChild = child.rewrite(rewriter);
		final UnaryPredicate before;
		if (newChild == child) {
			before = this;
		} else {
			before = rewriter.getFactory().makeUnaryPredicate(getTag(),
					newChild, getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		child.addGivenTypes(set);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
		acc.enterChildren();
		child.inspect(acc);
		acc.leaveChildren();
	}

	@Override
	public Predicate getChild(int index) {
		if (index == 0) {
			return child;
		}
		throw invalidIndex(index);
	}

	@Override
	public int getChildCount() {
		return 1;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		IPosition pos = child.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Predicate newChild = rewriter.rewrite(child);
		return rewriter.factory.makeUnaryPredicate(getTag(), newChild,
				getSourceLocation());
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
