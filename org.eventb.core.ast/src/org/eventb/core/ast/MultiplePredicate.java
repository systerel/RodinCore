/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.getSyntaxTreeHelper;
import static org.eventb.core.ast.AssociativeHelper.isLegibleList;
import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_PRED;
import static org.eventb.internal.core.ast.FormulaChecks.ensureMinLength;
import static org.eventb.internal.core.ast.FormulaChecks.ensureTagInRange;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.PARTITION;

import java.util.Arrays;
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
import org.eventb.internal.core.parser.SubParsers.MultiplePredicateParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * MultiplePredicate is the class for all predicates taking a variable number of
 * expression arguments in an event-B formula.
 * <p>
 * It can have several children which can only be Expression objects. Can only
 * accept {KPARTITION}.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class MultiplePredicate extends Predicate {

	// The children of this multiple predicate.
	// Is never null and contains at least one element by construction.
	protected final Expression[] children;

	// offset of the corresponding interval in Formula
	private static final int FIRST_TAG = FIRST_MULTIPLE_PREDICATE;

	private static enum Operators implements IOperatorInfo<MultiplePredicate> {
		OP_KPARTITION(PARTITION.getImage(), KPARTITION_ID, ATOMIC_PRED),
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		
		private Operators(String image, String id, StandardGroup group) {
			this.image = image;
			this.id = id;
			this.groupId = group.getId();
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
		public IParserPrinter<MultiplePredicate> makeParser(int kind) {
			return new MultiplePredicateParser(kind);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}

	}

	// For testing purposes
	public static final int TAGS_LENGTH = Operators.values().length;

	private static final String KPARTITION_ID = "Partition";

	/**
	 * @since 2.0
	 */
	public static void initV2(BMath grammar) {
		try {
			for(Operators operInfo: Operators.values()) {
				grammar.addOperator(operInfo);
			}
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeMultiplePredicate(int, Expression[],
	 *      SourceLocation)
	 * @see FormulaFactory#makeMultiplePredicate(int, java.util.Collection,
	 *      SourceLocation)
	 */
	protected MultiplePredicate(Expression[] children, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, ff, location, combineHashCodes(children));
		this.children = children;
		ensureTagInRange(tag, FIRST_TAG, TAGS_LENGTH);
		ensureMinLength(children, 1);
		ensureSameFactory(this.children);
		setPredicateVariableCache(this.children);
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();
	
		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();
	
		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
	
		final Type childType = children[0].getType();
		if (!(childType instanceof PowerSetType)) {
			return;
		}
		for (int i = 1; i < children.length; i++) {
			if (!childType.equals(children[i].getType())) {
				return;
			}
		}
		typeChecked = true;
	}

	/**
	 * Returns the children of this node.
	 * 
	 * @return the children of this node, neither <code>null</code> nor empty
	 */
	public Expression[] getChildren() {
		return children.clone();
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
	
	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-FIRST_TAG];
	}
	
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return getSyntaxTreeHelper(boundNames, tabs, children,
				getOperatorImage(), "", this.getClass().getSimpleName());
	}

	@Override
	protected void isLegible(LegibilityResult result) {
		isLegibleList(children, result);
	}

	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		final MultiplePredicate other = (MultiplePredicate) formula;
		return Arrays.equals(children, other.children);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final TypeVariable alpha = result.newFreshVariable(null);
		final Type childType = result.makePowerSetType(alpha);
		for (Expression child : children) {
			child.typeCheck(result, quantifiedIdentifiers);
			result.unify(child.getType(), childType, this);
		}
	}

	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		for (Expression child: children) {
			child.solveType(unifier);
		}
	}

	@Override
	protected void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (Expression child: children) {
			child.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		for (Expression child: children) {
			child.collectNamesAbove(names, boundNames, offset);
		}
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterKPARTITION(this);
	
		for (int i = 0; goOn && i < children.length; i++) {
			if (i != 0) {
				goOn = visitor.continueKPARTITION(this);
			}
			if (goOn) {
				goOn = children[i].accept(visitor);
			}
		}
		
		return visitor.exitKPARTITION(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitMultiplePredicate(this);
	}
	
	@Override
	protected Predicate rewrite(ITypeCheckingRewriter rewriter) {
		final int length = children.length;
		final FormulaFactory ff = rewriter.getFactory();
		final SourceLocation sloc = getSourceLocation();

		final Expression[] newChildren = new Expression[length];
		boolean changed = false;
		for (int i = 0; i < length; i++) {
			final Expression child = children[i];
			final Expression newChild = child.rewrite(rewriter);
			newChildren[i] = newChild;
			changed |= newChild != child;
		}
		final MultiplePredicate before;
		if (!changed) {
			before = this;
		} else {
			before = ff.makeMultiplePredicate(getTag(), newChildren, sloc);
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
		for (Expression child: children) {
			child.inspect(acc);
			if (acc.allSkipped()) {
				break;
			}
			acc.nextChild();
		}
		acc.leaveChildren();
	}

	@Override
	public Expression getChild(int index) {
		checkChildIndex(index);
		return children[index];
	}

	@Override
	public int getChildCount() {
		return children.length;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		for (Expression child: children) {
			IPosition pos = child.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		if (index < 0 || children.length <= index)
			throw new IllegalArgumentException(
					"Position is outside the formula");
		Expression[] newChildren = children.clone();
		newChildren[index] = rewriter.rewrite(children[index]);
		return rewriter.factory.makeMultiplePredicate(getTag(), newChildren,
				getSourceLocation());
	}
	
	@Override
	public boolean isWDStrict() {
		return true;
	}

}
