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
 *     Systerel - externalized wd lemmas generation
 *******************************************************************************/ 
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.BMath.StandardGroup.INFIX_SUBST;

import java.util.Collection;
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
 * Implements the deterministic assignment, where an expression is given for
 * each assigned identifier.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BecomesEqualTo extends Assignment {

	private static final String BECEQ_ID = "Becomes Equal To";
	
	/**
	 * @since 2.0
	 */
	public static final IOperatorInfo<BecomesEqualTo> OP_BECEQ = new IOperatorInfo<BecomesEqualTo>() {
		
		@Override
		public IParserPrinter<BecomesEqualTo> makeParser(int kind) {
			return new MainParsers.BecomesEqualToParser(kind);
		}

		@Override
		public String getImage() {
			return "\u2254";
		}
		
		@Override
		public String getId() {
			return BECEQ_ID;
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
			grammar.addOperator(OP_BECEQ);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final Expression[] values;
	
	protected BecomesEqualTo(FreeIdentifier assignedIdent, Expression value,
			SourceLocation location, FormulaFactory ff) {
		super(BECOMES_EQUAL_TO, location, value.hashCode(), assignedIdent);
		this.values = new Expression[] {value};
		checkPreconditions();
		setPredicateVariableCache(this.values);
		synthesizeType(ff);
	}

	protected BecomesEqualTo(FreeIdentifier[] assignedIdents, Expression[] values,
			SourceLocation location, FormulaFactory ff) {
		super(BECOMES_EQUAL_TO, location, combineHashCodes(values), assignedIdents);
		this.values = values.clone();
		checkPreconditions();
		setPredicateVariableCache(this.values);
		synthesizeType(ff);
	}

	protected BecomesEqualTo(Collection<FreeIdentifier> assignedIdents,
			Collection<Expression> values, SourceLocation location,
			FormulaFactory ff) {
		super(BECOMES_EQUAL_TO, location, combineHashCodes(values), assignedIdents);
		this.values = values.toArray(new Expression[values.size()]);
		checkPreconditions();
		setPredicateVariableCache(this.values);
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		assert assignedIdents.length != 0;
		assert assignedIdents.length == values.length;
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff) {
		final int length = assignedIdents.length;
		final Expression[] children = new Expression[length * 2];
		System.arraycopy(assignedIdents, 0, children, 0, length);
		System.arraycopy(values, 0, children, length, length);
		
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(children);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		// Check equality of types
		for (int i = 0; i < length; i++) {
			final Type type = assignedIdents[i].getType();
			if (type == null || ! type.equals(values[i].getType())) {
				return;
			}
		}
		typeChecked = true;
	}
	
	/**
	 * Returns the expressions that occur in the right-hand side of this
	 * assignment.
	 * 
	 * @return an array containing the expressions on the right-hand side of
	 *         this assignment
	 */
	public Expression[] getExpressions() {
		return values.clone();
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdentSet);
		}
		for (Expression value: values) {
			value.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames,
			int offset) {
		
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		for (Expression value: values) {
			value.collectNamesAbove(names, boundNames, offset);
		}
	}
	
	private String getOperatorImage() {
		return OP_BECEQ.getImage();
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final int kind = mediator.getKind();
		OP_BECEQ.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ast.Formula#getSyntaxTree(java.lang.String[], java.lang.String)
	 */
	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		
		final StringBuilder result = new StringBuilder();
		result.append(tabs);
		result.append(this.getClass().getSimpleName());
		result.append(" [:=]\n");
		for (FreeIdentifier ident: assignedIdents) {
			result.append(ident.getSyntaxTree(boundNames, childTabs));
		}
		for (Expression value: values) {
			result.append(value.getSyntaxTree(boundNames, childTabs));
		}
		return result.toString();
	}

	@Override
	protected boolean equals(Formula<?> otherFormula, boolean withAlphaConversion) {
		if (this.getTag() != otherFormula.getTag()) {
			return false;
		}
		BecomesEqualTo other = (BecomesEqualTo) otherFormula;
		if (! this.hasSameAssignedIdentifiers(other))
			return false;
		for (int i = 0; i < values.length; i++) {
			if (! values[i].equals(other.values[i], withAlphaConversion))
				return false;
		}
		return true;
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		for (int i = 0; i < values.length; i++) {
			assignedIdents[i].typeCheck(result, boundAbove);
			values[i].typeCheck(result, boundAbove);
			result.unify(assignedIdents[i].getType(), values[i].getType(), this);
		}
	}

	@Override
	protected void isLegible(LegibilityResult result) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result);
		}
		for (Expression value: values) {
			value.isLegible(result);
		}
	}

	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Expression value: values) {
			success &= value.solveType(unifier);
		}
		return success;
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterBECOMES_EQUAL_TO(this);

		for (int i = 0; goOn && i < assignedIdents.length; ++i) {
			goOn = assignedIdents[i].accept(visitor);
			if (goOn) {
				goOn = visitor.continueBECOMES_EQUAL_TO(this);
			}
		}
		
		for (int i = 0; goOn && i < values.length; i++) {
			if (i != 0) {
				goOn = visitor.continueBECOMES_EQUAL_TO(this);
			}
			if (goOn) {
				goOn = values[i].accept(visitor);
			}
		}

		return visitor.exitBECOMES_EQUAL_TO(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBecomesEqualTo(this);
	}

	@Override
	protected Predicate getFISPredicateRaw(FormulaFactory formulaFactory) {
		return formulaFactory.makeLiteralPredicate(BTRUE, getSourceLocation());
	}

	@Override
	protected Predicate getBAPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		final int length = assignedIdents.length;
		final Predicate[] predicates = new Predicate[length];
		for (int i=0; i<length; i++) {
			predicates[i] = 
				ff.makeRelationalPredicate(EQUAL, 
						assignedIdents[i].withPrime(ff),
						values[i], 
						loc);
		}
		if (predicates.length > 1)
			return ff.makeAssociativePredicate(LAND, predicates, loc);
		else
			return predicates[0];
	}

	@Override
	public FreeIdentifier[] getUsedIdentifiers() {
		if (values.length == 1) {
			return values[0].getFreeIdentifiers();
		}

		// More than one value, we need to merge the free identifiers of every
		// child
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(values);
		FreeIdentifier[] idents = freeIdentMerger.getFreeMergedArray();

		// Need to clone the array, as it can be maximal for one child (and then
		// we would expose an internal array to clients)
		return idents.clone();
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (Expression value: values) {
			value.addGivenTypes(set);
		}
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
