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
 *     Systerel - externalized wd lemmas generation
 *     Systerel - add given sets to free identifier cache
 *     Systerel - added child indexes
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.INFIX_SUBST;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.MainParsers;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * Implements the set-based assignment, where a set expression is given for
 * the assigned identifier.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BecomesMemberOf extends Assignment {

	private static final String BECMO_ID = "Becomes Member Of";

	/**
	 * @since 2.0
	 */
	public static final IOperatorInfo<BecomesMemberOf> OP_BECMO = new IOperatorInfo<BecomesMemberOf>() {
		
		@Override
		public IParserPrinter<BecomesMemberOf> makeParser(int kind) {
			return new MainParsers.BecomesMemberOfParser(kind);
		}

		@Override
		public String getImage() {
			return ":\u2208";
		}
		
		@Override
		public String getId() {
			return BECMO_ID;
		}
		
		@Override
		public String getGroupId() {
			return INFIX_SUBST.getId();
		}

		@Override
		public boolean isSpaced() {
			return true;
		}
	};

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {		
			grammar.addOperator(OP_BECMO);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final Expression setExpr;
	
	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeBecomesMemberOf(FreeIdentifier, Expression,
	 *      SourceLocation)
	 */
	protected BecomesMemberOf(FreeIdentifier assignedIdent, Expression setExpr,
			SourceLocation location, FormulaFactory ff) {
		super(BECOMES_MEMBER_OF, location, setExpr.hashCode(),
				new FreeIdentifier[] { assignedIdent });
		this.setExpr = setExpr;
		setPredicateVariableCache(this.setExpr);
		synthesizeType(ff);
	}

	@Override
	protected void synthesizeType(FormulaFactory ff) {
		// assignedIdents contains only one element since it is guaranteed by
		// constructor
		IdentListMerger freeIdentMerger = IdentListMerger.makeMerger(
					assignedIdents[0].freeIdents, setExpr.freeIdents);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = IdentListMerger.makeMerger(
					assignedIdents[0].boundIdents, setExpr.boundIdents);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		if (! setExpr.isTypeChecked())
			return;

		// Check equality of types
		final Type type = assignedIdents[0].getType();
		if (type == null || ! type.equals(setExpr.getType().getBaseType())) {
			return;
		}
		typeChecked = true;
	}

	/**
	 * Returns the set that occurs in the right-hand side of this assignment.
	 * 
	 * @return the set on the right-hand side of this assignment
	 */
	public Expression getSet() {
		return setExpr;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdentSet);
		}
		setExpr.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {

		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		setExpr.collectNamesAbove(names, boundNames, offset);
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final int kind = mediator.getKind();
		OP_BECMO.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(OP_BECMO.getImage());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		
		final StringBuilder result = new StringBuilder();
		result.append(tabs);
		result.append(this.getClass().getSimpleName());
		result.append(" [:\u2208]\n");
		for (FreeIdentifier ident: assignedIdents) {
			result.append(ident.getSyntaxTree(boundNames, childTabs));
		}
		result.append(setExpr.getSyntaxTree(boundNames, childTabs));
		return result.toString();
	}

	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		final BecomesMemberOf other = (BecomesMemberOf) formula;
		return this.hasSameAssignedIdentifiers(other)
				&& setExpr.equals(other.setExpr);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		final FreeIdentifier lhs = assignedIdents[0];
		lhs.typeCheck(result, boundAbove);
		setExpr.typeCheck(result, boundAbove);
		result.unify(setExpr.getType(), result.makePowerSetType(lhs.getType()), this);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#isLegible(org.eventb.internal.core.ast.LegibilityResult, org.eventb.core.ast.BoundIdentDecl[])
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result);
		}
		setExpr.isLegible(result);
	}

	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		setExpr.solveType(unifier);
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterBECOMES_MEMBER_OF(this);

		if (goOn) {
			goOn = assignedIdents[0].accept(visitor);
		}
		if (goOn) {
			goOn = visitor.continueBECOMES_MEMBER_OF(this);
		}
		if (goOn) {
			goOn = setExpr.accept(visitor);
		}
		return visitor.exitBECOMES_MEMBER_OF(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBecomesMemberOf(this);
	}


	@Override
	protected Predicate getFISPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		final Expression emptySet = ff.makeEmptySet(setExpr.getType(), null);
		return ff.makeRelationalPredicate(NOTEQUAL, setExpr, emptySet, loc);
	}

	@Override
	protected Predicate getBAPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		final FreeIdentifier primedIdentifier = 
			assignedIdents[0].withPrime(ff);
		return ff.makeRelationalPredicate(IN, primedIdentifier, setExpr, loc);
	}

	@Override
	public FreeIdentifier[] getUsedIdentifiers() {
		return setExpr.getFreeIdentifiers();
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

	@Override
	public Formula<?> getChild(int index) {
		checkChildIndex(index);
		if (index < assignedIdents.length) {
			return assignedIdents[index];
		}
		return setExpr;
	}

	@Override
	public int getChildCount() {
		return assignedIdents.length + 1;
	}

}
