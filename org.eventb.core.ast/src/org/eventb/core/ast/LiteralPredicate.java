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

import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_PRED;

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
import org.eventb.internal.core.parser.SubParsers.LiteralPredicateParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * This class represents a literal predicate in an event-B formula.
 * <p>
 * Can take value {BTRUE} or {BFALSE}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class LiteralPredicate extends Predicate {

	// offset of the corresponding tag-interval in Formula
	private static final int FIRST_TAG = FIRST_LITERAL_PREDICATE;
	
	private static final String BTRUE_ID = "B True";

	private static final String BFALSE_ID = "B False";

	private static enum Operators implements IOperatorInfo<LiteralPredicate> {
		OP_BTRUE("\u22a4", BTRUE_ID, ATOMIC_PRED, BTRUE),
		OP_BFALSE("\u22a5", BFALSE_ID, ATOMIC_PRED, BFALSE),
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
		public IParserPrinter<LiteralPredicate> makeParser(int kind) {
			return new LiteralPredicateParser(kind, tag);
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
	
	protected LiteralPredicate(int tag, SourceLocation location,
			FormulaFactory ff) {
		
		super(tag, location, 0);
		assert tag >= FIRST_TAG && tag < FIRST_TAG+TAGS_LENGTH;
		
		setPredicateVariableCache();
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;
		typeChecked = true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		// Nothing to do, this subformula is always well-formed.
	}
	
	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		return true;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		// Nothing to do
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return true;
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
	protected void toString(IToStringMediator mediator) {
		final Operators operator = getOperator();
		final int kind = mediator.getKind();
		
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " ["+ getOperatorImage() + "]" + "\n";
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
	public boolean accept(IVisitor visitor) {
		switch (getTag()) {
		case BTRUE:  return visitor.visitBTRUE(this);
		case BFALSE: return visitor.visitBFALSE(this);
		default:     return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitLiteralPredicate(this);
	}

	@Override
	protected Predicate rewrite(ITypeCheckingRewriter rewriter) {
		return rewriter.rewrite(this);
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		// Nothing to do
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.childrenSkipped();
	}

	@Override
	public Formula<?> getChild(int index) {
		throw invalidIndex(index);
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		return new Position(indexes);
	}

	@Override
	protected Predicate rewriteChild(int index, SingleRewriter rewriter) {
		throw new IllegalArgumentException("Position is outside the formula");
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
