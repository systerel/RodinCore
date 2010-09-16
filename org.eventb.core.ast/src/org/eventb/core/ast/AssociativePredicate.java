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
 *******************************************************************************/ 
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.internal.core.parser.BMath.StandardGroup.LOGIC_PRED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.BMath.StandardGroup;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.AssociativePredicateInfix;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * AssociativePredicate is the AST class for the associative predicates in an
 * event-B formula.
 * <p>
 * It can have several children which can only be Predicate objects. Can only
 * accept {LAND, LOR}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class AssociativePredicate extends Predicate {
	
	// The children of this associative predicate.
	// Is never null and contains at least two elements by construction.
	private final Predicate[] children;
	
	// offset of the corresponding interval in Formula
	private static int FIRST_TAG = FIRST_ASSOCIATIVE_PREDICATE;

	/**
	 * @since 2.0
	 */
	public static final String LOR_ID = "lor";

	/**
	 * @since 2.0
	 */
	public static final String LAND_ID = "land";

	private static enum Operators implements IOperatorInfo<AssociativePredicate> {
		OP_LAND("\u2227", LAND_ID, LOGIC_PRED, LAND),
		OP_LOR("\u2228", LOR_ID, LOGIC_PRED, LOR),
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
		public IParserPrinter<AssociativePredicate> makeParser(int kind) {
			return new AssociativePredicateInfix(kind, tag);
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
	
	protected AssociativePredicate(Predicate[] children, int tag,
			SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, combineHashCodes(children));
		this.children = children.clone();
		
		checkPreconditions();
		setPredicateVariableCache(this.children);
		synthesizeType(ff);
	}

	protected AssociativePredicate(Collection<Predicate> children, int tag,
			SourceLocation location, FormulaFactory ff) {
		
		super(tag, location, combineHashCodes(children));
		Predicate[] model = new Predicate[children.size()];
		this.children = children.toArray(model);

		checkPreconditions();
		setPredicateVariableCache(this.children);
		synthesizeType(ff);
	}

	// Common initialization.
	private void checkPreconditions() {
		assert getTag() >= FIRST_TAG && getTag() < FIRST_TAG+TAGS_LENGTH;
		assert children != null;
		assert children.length >= 2;
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
		
		for (Predicate child: children) {
			if (! child.isTypeChecked()) {
				return;
			}
		}
		typeChecked = true;
	}
	
	/**
	 * Returns the children of this node.
	 * 
	 * @return the children of this node. Can never be <code>null</code> or
	 *         empty.
	 */
	public Predicate[] getChildren() {
		return children.clone();
	}

	private Operators getOperator() {
		return Operators.values()[getTag()-FIRST_TAG];
	}

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return equalsHelper(children, ((AssociativePredicate) other).children,
				withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		for (Predicate child: children) {
			child.typeCheck(result,quantifiedIdentifiers);
		}
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Predicate child: children) {
			success &= child.solveType(unifier);
		}
		return success;
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
		return AssociativeHelper
				.getSyntaxTreeHelper(boundNames, tabs, children,
						getOperatorImage(), "", this.getClass().getSimpleName());
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		AssociativeHelper.isLegibleList(children, result);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (Predicate child: children) {
			child.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		for (Predicate child: children) {
			child.collectNamesAbove(names, boundNames, offset);
		}
	}

	@Override
	protected Predicate bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		boolean changed = false;
		Predicate[] newChildren = new Predicate[children.length];
		for (int i = 0; i < children.length; i++) {
			newChildren[i] = children[i].bindTheseIdents(binding, offset, factory);
			changed |= newChildren[i] != children[i];
		}
		if (! changed) {
			return this;
		}
		return factory.makeAssociativePredicate(getTag(), newChildren, getSourceLocation());
	}
	

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case LAND: goOn = visitor.enterLAND(this); break;
		case LOR:  goOn = visitor.enterLOR(this);  break;
		default:   assert false;
		}

		for (int i = 0; goOn && i < children.length; i++) {
			if (i != 0) {
				switch (getTag()) {
				case LAND: goOn = visitor.continueLAND(this); break;
				case LOR:  goOn = visitor.continueLOR(this);  break;
				default:     assert false;
				}
			}
			if (goOn) {
				goOn = children[i].accept(visitor);
			}
		}
		
		switch (getTag()) {
		case LAND: return visitor.exitLAND(this);
		case LOR:  return visitor.exitLOR(this);
		default:   assert false; return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitAssociativePredicate(this);
	}

	@Override
	public Predicate rewrite(IFormulaRewriter rewriter) {
		final boolean flatten = rewriter.autoFlatteningMode();
		final ArrayList<Predicate> newChildren =
			new ArrayList<Predicate>(children.length + 11); 
		boolean changed = false;
		for (Predicate child: children) {
			Predicate newChild = child.rewrite(rewriter);
			if (flatten && getTag() == newChild.getTag()) {
				final Predicate[] grandChildren =
					((AssociativePredicate) newChild).children;
				newChildren.addAll(Arrays.asList(grandChildren));
				changed = true;
			} else {
				newChildren.add(newChild);
				changed |= newChild != child;
			}
		}
		final AssociativePredicate before;
		if (! changed) {
			before = this;
		} else {
			before = rewriter.getFactory().makeAssociativePredicate(getTag(),
					newChildren, getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (Predicate child: children) {
			child.addGivenTypes(set);
		}
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.enterChildren();
		for (Predicate child: children) {
			child.inspect(acc);
			acc.nextChild();
		}
		acc.leaveChildren();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index < children.length) {
			return children[index];
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		for (Predicate child: children) {
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
			throw new IllegalArgumentException("Position is outside the formula");
		Predicate[] newChildren = children.clone();
		newChildren[index] = rewriter.rewrite(children[index]);
		return rewriter.factory.makeAssociativePredicate(
				getTag(), newChildren, getSourceLocation());
	}

}
