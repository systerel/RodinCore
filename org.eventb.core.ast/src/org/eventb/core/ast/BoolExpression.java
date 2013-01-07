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
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.BOOL_EXPR;

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
import org.eventb.internal.core.parser.SubParsers.KBoolParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * BoolExpression represents the bool keyword of an event-B formula.
 * <p>
 * Can only accept {KBOOL}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BoolExpression extends Expression {
	
	private static final String KBOOL_ID = "To Bool";

	private static enum Operators implements IOperatorInfo<BoolExpression> {
		OP_KBOOL("bool", KBOOL_ID, BOOL_EXPR),
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
		public IParserPrinter<BoolExpression> makeParser(int kind) {
			return new KBoolParser(kind);
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
	
	// child
	private final Predicate child;
	
	protected BoolExpression(Predicate child, int tag, SourceLocation location,
			FormulaFactory ff) {
		
		super(tag, location, child.hashCode());
		assert tag == KBOOL;
		this.child = child;
		setPredicateVariableCache(this.child);
		synthesizeType(ff, null);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;
		
		if (! child.isTypeChecked()) {
			return;
		}
		
		setFinalType(ff.makeBooleanType(), givenType);
	}

	/**
	 * Returns the predicate child of this node, that is the predicate whose
	 * truth value is transformed into a boolean expression by this operator.
	 * 
	 * @return the predicate child
	 */
	public Predicate getPredicate() {
		return child;
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		child.isLegible(result);
	}

	@Override
	boolean equalsInternalExpr(Expression expr) {
		final BoolExpression other = (BoolExpression) expr;
		return child.equals(other.child);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result, quantifiedIdentifiers);
		setTemporaryType(result.makeBooleanType(), result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final Operators operator = Operators.OP_KBOOL;
		final int kind = mediator.getKind();
		
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	private String getOperatorImage() {
		return Operators.OP_KBOOL.getImage();
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		return tabs + this.getClass().getSimpleName() + " ["
				+ getOperatorImage() + "]" + typeName + "\n"
				+ child.getSyntaxTree(boundNames, tabs + "\t");
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
		case KBOOL: goOn = visitor.enterKBOOL(this); break;
		default:    assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KBOOL: return visitor.exitKBOOL(this);
		default:    return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBoolExpression(this);
	}

	@Override
	protected Expression rewrite(ITypeCheckingRewriter rewriter) {
		final Predicate newChild = child.rewrite(rewriter);
		final BoolExpression before;
		if (newChild == child) {
			before = this;
		} else {
			before = rewriter.getFactory().makeBoolExpression(newChild,
					getSourceLocation());
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
	public Predicate getChild(int index) {
		if (index == 0)
			return child;
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
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Predicate newChild = rewriter.rewrite(child);
		return rewriter.factory.makeBoolExpression(newChild, getSourceLocation());
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
