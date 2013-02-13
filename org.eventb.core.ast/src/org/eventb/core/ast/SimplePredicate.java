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
 *     Systerel - fixed bug in synthesizeType()
 *     Systerel - added support for predicate variables
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *     Systerel - store factory used to build a formula
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_PRED;
import static org.eventb.internal.core.ast.FormulaChecks.ensureTagInRange;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.FiniteParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * SimplePredicate represents a predicate builds from a single expression in an
 * event-B formula.
 * <p>
 * It can accept tag {KFINITE}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SimplePredicate extends Predicate {
	
	private static enum Operators implements IOperatorInfo<SimplePredicate> {
		OP_KFINITE("finite", KFINITE_ID, ATOMIC_PRED),
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
		public IParserPrinter<SimplePredicate> makeParser(int kind) {
			return new FiniteParser(kind);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}

	}

	// child
	private final Expression child;
	
	// offset in the corresponding tag interval
	private static final int FIRST_TAG = FIRST_SIMPLE_PREDICATE;
	
	private static final String KFINITE_ID = "Finite";

	private static final int TAGS_LENGTH = Operators.values().length;

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {		
			grammar.addOperator(Operators.OP_KFINITE);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeSimplePredicate(int, Expression, SourceLocation)
	 */
	protected SimplePredicate(Expression child, int tag,
			SourceLocation location, FormulaFactory ff) {
		super(tag, ff, location, child.hashCode());
		this.child = child;
		ensureTagInRange(tag, FIRST_TAG, TAGS_LENGTH);
		ensureSameFactory(this.child);
		setPredicateVariableCache(this.child);
		synthesizeType();
	}

	@Override
	protected void synthesizeType() {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		
		if (! child.isTypeChecked()) {
			return;
		}
		final Type type = child.getType();
		if (type == null || type.getBaseType() == null) {
			return;
		}
		typeChecked = true;
	}
	
	/**
	 * Returns the child expression of this node.
	 * 
	 * @return the expression of this node 
	 */
	public Expression getExpression() {
		return child;
	}

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	private Operators getOperator() {
		return Operators.OP_KFINITE;
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
	protected boolean equalsInternal(Formula<?> formula) {
		final SimplePredicate other = (SimplePredicate) formula;
		return child.equals(other.child);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		TypeVariable alpha = result.newFreshVariable(null);
		child.typeCheck(result, quantifiedIdentifiers);
		result.unify(child.getType(), result.makePowerSetType(alpha), this);
	}
	
	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		child.solveType(unifier);
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
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KFINITE: goOn = visitor.enterKFINITE(this); break;
		default:      assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KFINITE: return visitor.exitKFINITE(this);
		default:      return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitSimplePredicate(this);
	}

	@Override
	protected Predicate rewrite(ITypeCheckingRewriter rewriter) {
		final Expression newChild = child.rewrite(rewriter);
		final SimplePredicate before;
		if (newChild == child) {
			before = this;
		} else {
			before = rewriter.getFactory().makeSimplePredicate(getTag(),
					newChild, getSourceLocation());
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
		child.inspect(acc);
		acc.leaveChildren();
	}

	@Override
	public Expression getChild(int index) {
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
		assert (rewriter.factory == getFactory());

		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Expression newChild = rewriter.rewrite(child);
		return rewriter.factory.makeSimplePredicate(getTag(), newChild,
				getSourceLocation());
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
