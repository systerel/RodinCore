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
 *     Systerel - bug #3574162: AST does not compare bound ident decl types
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedHelper.areEqualDecls;
import static org.eventb.core.ast.QuantifiedHelper.getBoundIdentsAbove;
import static org.eventb.core.ast.QuantifiedHelper.getSyntaxTreeQuantifiers;
import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;
import static org.eventb.core.ast.extension.StandardGroup.INFIX_SUBST;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.internal.core.ast.BoundIdentSubstitution;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
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
 * Implements the most general assignment, where a predicate is given on the
 * before and after values of assigned identifiers.
 * <p>
 * In event-B, the after values of the assigned identifiers are denoted by a
 * primed identifier whose prefix is the assigned identifier. These primed
 * identifiers are implemented as bound identifiers, whose (implicit)
 * declaration is stored in instances of this class.
 * </p>
 * <p>
 * For instance, the assignment <code>x :| x &lt; x'</code> is represented in the
 * following way:
 * <ul>
 * <li>the assigned identifier is <code>x</code>.</li>
 * <li>the bound primed identifier is <code>x'</code>.</li>
 * <li>the condition is <code>x &lt; [[0]]</code>, where <code>[[0]]</code>
 * denotes an occurrence of the bound identifier <code>x'</code>.</li>
 * </ul>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BecomesSuchThat extends Assignment {

	private static final String BECST_ID = "Becomes Such That";
	
	/**
	 * @since 2.0
	 */
	public static final IOperatorInfo<BecomesSuchThat> OP_BECST = new IOperatorInfo<BecomesSuchThat>() {
		
		@Override
		public IParserPrinter<BecomesSuchThat> makeParser(int kind) {
			return new MainParsers.BecomesSuchThatParser(kind);
		}

		@Override
		public String getImage() {
			return ":\u2223";
		}
		
		@Override
		public String getId() {
			return BECST_ID;
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
			grammar.addOperator(OP_BECST);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Quantified primed identifiers
	private BoundIdentDecl[] primedIdents;
	
	// Post-condition of this assignment
	private final Predicate condition;
	
	protected BecomesSuchThat(FreeIdentifier[] assignedIdents,
			BoundIdentDecl[] primedIdents, Predicate condition,
			SourceLocation location,
			FormulaFactory ff) {
		// Note: primedIdents must not be part of hash-code as their name is
		// not relevant for equality
		super(Formula.BECOMES_SUCH_THAT, location, condition.hashCode(), assignedIdents);
		this.condition = condition;
		this.primedIdents = primedIdents;
		checkPreconditions();
		setPredicateVariableCache(this.condition);
		synthesizeType(ff);
	}

	private void checkPreconditions() {
		assert this.primedIdents.length == assignedIdents.length;
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff) {
		final int length = assignedIdents.length;
		final Formula<?>[] children = new Formula[2 * length + 1];
		System.arraycopy(assignedIdents, 0, children, 0, length);
		System.arraycopy(primedIdents, 0, children, length, length);
		children[2 * length] = condition;
		
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(children);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		final BoundIdentifier[] boundIdentsBelow = condition.boundIdents; 
		this.boundIdents = 
			getBoundIdentsAbove(boundIdentsBelow, primedIdents, ff);

		if (freeIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		if (! condition.isTypeChecked()) {
			return;
		}

		// Check equality of types
		for (int i = 0; i < length; i++) {
			final Type type = assignedIdents[i].getType();
			if (type == null || ! type.equals(primedIdents[i].getType())) {
				return;
			}
		}
		typeChecked = true;
	}

	/**
	 * Returns the declaration of the primed identifiers created for each
	 * assigned identifier.
	 * 
	 * @return an array of bound identifier declaration
	 */
	public BoundIdentDecl[] getPrimedIdents() {
		return primedIdents.clone();
	}

	/**
	 * Returns the post-condition that appears in the right-hand side of this
	 * assignment.
	 * 
	 * @return the post-condition of this assignment
	 */
	public Predicate getCondition() {
		return condition;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectFreeIdentifiers(freeIdentSet);
		}
		condition.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.collectNamesAbove(names, boundNames, offset);
		}
		final int newOffset = offset + primedIdents.length;
		condition.collectNamesAbove(names, boundNames, newOffset);
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final int kind = mediator.getKind();
		OP_BECST.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(OP_BECST.getImage());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		final String childTabs = tabs + '\t';
		final String[] boundNamesBelow = catenateBoundIdentLists(boundNames, primedIdents);
		return tabs
			+ this.getClass().getSimpleName()
			+ " [:\u2223]\n"
		    + getSyntaxTreeLHS(boundNames, childTabs)
		    + getSyntaxTreeQuantifiers(boundNamesBelow, childTabs, primedIdents)
		    + condition.getSyntaxTree(boundNamesBelow, childTabs);
	}

	@Override
	protected boolean equalsInternal(Formula<?> formula) {
		final BecomesSuchThat other = (BecomesSuchThat) formula;
		return hasSameAssignedIdentifiers(other)
				&& areEqualDecls(primedIdents, other.primedIdents)
				&& condition.equals(other.condition);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] boundAbove) {
		for (int i = 0; i < primedIdents.length; i++) {
			assignedIdents[i].typeCheck(result, boundAbove);
			primedIdents[i].typeCheck(result, boundAbove);
			result.unify(assignedIdents[i].getType(), primedIdents[i].getType(), this);
		}
		BoundIdentDecl[] boundBelow = catenateBoundIdentLists(boundAbove, primedIdents);
		condition.typeCheck(result, boundBelow);
	}

	@Override
	protected void isLegible(LegibilityResult result) {
		for (FreeIdentifier ident: assignedIdents) {
			ident.isLegible(result);
		}
		for (BoundIdentDecl decl: primedIdents) {
			decl.isLegible(result);
		}
		condition.isLegible(result);
	}

	@Override
	protected void solveChildrenTypes(TypeUnifier unifier) {
		for (BoundIdentDecl ident : primedIdents) {
			ident.solveType(unifier);
		}
		condition.solveType(unifier);
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterBECOMES_SUCH_THAT(this);

		for (int i = 0; goOn && i < assignedIdents.length; ++i) {
			goOn = assignedIdents[i].accept(visitor);
			if (goOn) {
				goOn = visitor.continueBECOMES_SUCH_THAT(this);
			}
		}
		
		for (int i = 0; goOn && i < primedIdents.length; ++i) {
			goOn = primedIdents[i].accept(visitor);
			if (goOn) {
				goOn = visitor.continueBECOMES_SUCH_THAT(this);
			}
		}
		
		if (goOn) {
			goOn = condition.accept(visitor);
		}

		return visitor.exitBECOMES_SUCH_THAT(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBecomesSuchThat(this);
	}

	@Override
	protected Predicate getFISPredicateRaw(FormulaFactory ff) {
		final SourceLocation loc = getSourceLocation();
		return QuantifiedHelper.getWDSimplifyQ(ff, EXISTS, primedIdents,
				condition, loc);
	}

	@Override
	protected Predicate getBAPredicateRaw(FormulaFactory ff) {
		ITypeEnvironmentBuilder typeEnvironment = ff.makeTypeEnvironment();
		FreeIdentifier[] freshIdents = 
				typeEnvironment.makeFreshIdentifiers(primedIdents);
		Substitution subst = 
			new BoundIdentSubstitution(primedIdents, freshIdents, ff);
		return condition.rewrite(subst);
	}

	@Override
	public FreeIdentifier[] getUsedIdentifiers() {
		return condition.getFreeIdentifiers();
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}
