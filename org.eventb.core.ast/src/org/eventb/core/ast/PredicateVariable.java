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

import static org.eventb.core.ast.extension.StandardGroup.GROUP_0;
import static org.eventb.internal.core.ast.FormulaChecks.ensureValidIdentifierName;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.PRED_VAR;
import static org.eventb.internal.core.parser.SubParsers.PRED_VAR_SUBPARSER;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * This class represents a predicate meta-variable in an event-B formula. Only
 * one AST tag corresponds to this node. For technical reasons (predicates and
 * expressions are distinct syntactic categories), predicate meta-variable's
 * attribute <code>name</code> starts with a leading symbol '$'.
 * 
 * @author Thomas Muller
 * @since 1.2
 * @noextend This class is not intended to be subclassed by clients.
 */
public class PredicateVariable extends Predicate {

	/**
	 * Unique tag identifying a predicate variable AST node.
	 * 
	 * @see Formula#PREDICATE_VARIABLE
	 */
	public static final int tag = PREDICATE_VARIABLE;

	/**
	 * Leading symbol used to distinguish predicate meta-variables from
	 * predicates and expressions.
	 */
	public static final String LEADING_SYMBOL = "$";

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {
			grammar.addOperator(PRED_VAR, PRED_VAR.getImage(), GROUP_0.getId(),
					PRED_VAR_SUBPARSER, false);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// The name of the PredicateVariable including the leading symbol '$'
	private final String name;

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makePredicateVariable(String, SourceLocation)
	 */
	protected PredicateVariable(String name, SourceLocation location,
			FormulaFactory ff) {
		super(tag, ff, location, name.hashCode());
		if (!name.startsWith(LEADING_SYMBOL)) {
			throw new IllegalArgumentException("Name " + name
					+ " does not start with " + LEADING_SYMBOL);
		}
		final String suffix = name.substring(LEADING_SYMBOL.length());
		ensureValidIdentifierName(suffix, ff);
		this.name = name;
		setPredicateVariableCache(this);
		synthesizeType();
	}

	public String getName() {
		return this.name;
	}

	@Override
	protected void synthesizeType() {
		this.freeIdents = NO_FREE_IDENT;
		this.boundIdents = NO_BOUND_IDENT;
		typeChecked = true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		// Nothing to do, this sub-formula is always legible.
	}

	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		final PredicateVariable other = (PredicateVariable) formula;
		return name.equals(other.name);
	}

	@Override
	protected void typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers) {
		// Always well-typed
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		PRED_VAR_SUBPARSER.toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getPREDVAR();
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + this.name + "]"
				+ "\n";
	}

	@Override
	protected void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet) {
		// Nothing to collect
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		// Nothing to collect
	}

	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		// No child
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
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
	public boolean accept(IVisitor visitor) {
		if (!(visitor instanceof IVisitor2)) {
			throw new IllegalArgumentException(
					"The given visitor shall support predicate variables");
		}
		return ((IVisitor2) visitor).visitPREDICATE_VARIABLE(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		if (!(visitor instanceof ISimpleVisitor2)) {
			throw new IllegalArgumentException(
					"The given visitor shall support predicate variables");
		}
		((ISimpleVisitor2) visitor).visitPredicateVariable(this);
	}

	@Override
	protected Predicate rewrite(ITypeCheckingRewriter rewriter) {
		return rewriter.rewrite(this);
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
