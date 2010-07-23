/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.extensionality;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.tracing.ExtensionalityOrigin;

public class ExtensionalityProver implements IProverModule {
	
	private PredicateTable predicateTable;
	private VariableContext variableContext;
	private ClauseFactory cf = ClauseFactory.getDefault();
	
	public ExtensionalityProver(PredicateTable predicateTable,
			VariableContext variableContext) {
		this.predicateTable = predicateTable;
		this.variableContext = variableContext;
	}
	
	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		Clause derivedClause = null;
		if (clause.getOrigin() instanceof ExtensionalityOrigin) return ProverResult.EMPTY_RESULT;
		
		if (isUnitSetEqualityLiteral(clause)) {
			derivedClause = getEquivalence(clause, getUnitEqualityLiteral(clause));
		}
		else if (isEquivalenceCandidate(clause)) {
			PredicateLiteral literal1 = clause.getPredicateLiteral(0);
			PredicateLiteral literal2 = clause.getPredicateLiteral(1);
			boolean sameSign = haveSameSign(literal1, literal2);
			if (	(sameSign && haveSameVariables(literal1, literal2))
				|| 	(!sameSign && haveSameConstants(literal1, literal2))) {
				SimpleTerm term1 = getLastTerm(literal1);
				SimpleTerm term2 = getLastTerm(literal2);
				assert term1.getSort().equals(term2.getSort()) && predicateTable.getDescriptor(term1.getSort()) != null;
				if (isRealConstant(term1) && isRealConstant(term2)) {
					derivedClause = getUnitEqualityClause(term1, term2, sameSign, clause);
				}
			}
		}
		else if (isDisjunctiveCandidate(clause)) {
			// TODO code case where we need to search for the complementary clause
		}
		if (derivedClause != null) return new ProverResult(derivedClause);
		return ProverResult.EMPTY_RESULT;
	}

	private boolean isRealConstant(SimpleTerm term) {
		return term.isConstant() && !term.isQuantified();
	}
	
	private Clause getUnitEqualityClause(SimpleTerm term1, SimpleTerm term2, boolean isPositive, Clause clause) {
		EqualityLiteral equality = new EqualityLiteral(term1, term2, isPositive);
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		equalities.add(equality);
		return cf.makeDisjunctiveClause(new ExtensionalityOrigin(clause), new ArrayList<PredicateLiteral>(),
				equalities, new ArrayList<ArithmeticLiteral>(), new ArrayList<EqualityLiteral>());
	}
	
	private SimpleTerm getLastTerm(PredicateLiteral literal) {
		return literal.getTerm(literal.getDescriptor().getArity()-1);
	}
	
	private boolean haveSameConstants(PredicateLiteral literal1, PredicateLiteral literal2) {
		for (int i = 0;i<literal1.getDescriptor().getArity()-1;i++) {
			Term term1 = literal1.getTerm(i);
			Term term2 = literal2.getTerm(i);
			if (!term1.isConstant()) return false;
			if (!term1.equals(term2)) return false;
		}
		return true;
	}
	
	/**
	 * Returns <code>true</code> if both literals are of the form PxyzA where xyz are
	 * the same variables for both literals and A can be either a constant or a variable 
	 * and not necessarily the same for both literals.
	 * <p>
	 * Also checks that variables xyz are pairwise distinct.
	 * </p>
	 * 
	 * @param literal1
	 * @param literal2
	 * @return
	 */
	private boolean haveSameVariables(PredicateLiteral literal1, PredicateLiteral literal2) {
		final Set<Term> set = new HashSet<Term>();
		final int lengthMinusOne = literal1.getDescriptor().getArity()-1;
		for (int i = 0;i<lengthMinusOne;i++) {
			Term term1 = literal1.getTerm(i);
			Term term2 = literal2.getTerm(i);
			if (term1.isConstant() || term1 != term2 || set.contains(term1))
				return false;
			set.add(term1);
		}
		return true;
	}
	
	private boolean haveSameSign(PredicateLiteral literal1, PredicateLiteral literal2) {
		return literal1.isPositive() == literal2.isPositive();
	}

	private boolean isCandidate(Clause clause) {
		if (clause.hasConditions()) return false;
		if (clause.sizeWithoutConditions() != 2) return false;
		if (clause.getPredicateLiteralsSize() != 2) return false;
		PredicateLiteral literal1 = clause.getPredicateLiteral(0);
		PredicateLiteral literal2 = clause.getPredicateLiteral(1);
		if (!literal1.getDescriptor().equals(literal2.getDescriptor())) return false;
		if (!literal1.getDescriptor().isGenuineMembership()) return false;
		return true;
	}
	
	private boolean isDisjunctiveCandidate(Clause clause) {
		if (clause.isEquivalence()) return false;
		return isCandidate(clause);
	}
	
	private boolean isEquivalenceCandidate(Clause clause) {
		if (!clause.isEquivalence()) return false;
		return isCandidate(clause);
	}

	private Clause getEquivalence(Clause clause, EqualityLiteral equality) {
		final boolean positive = equality.isPositive();
		final Sort sort = equality.getSort();
		final PredicateLiteralDescriptor descriptor = predicateTable
				.getDescriptor(sort);
		final List<SimpleTerm> terms1 = new ArrayList<SimpleTerm>();
		final List<SimpleTerm> terms2 = new ArrayList<SimpleTerm>();
		final List<Sort> sorts = descriptor.getSortList();
		final List<Sort> leftSorts = sorts.subList(0, sorts.size() - 1);
		addLeftTerms(leftSorts, terms1, terms2, positive);
		terms1.add(equality.getTerm1());
		terms2.add(equality.getTerm2());

		final PredicateLiteral literal1 = new ComplexPredicateLiteral(
				descriptor, positive, terms1);
		final PredicateLiteral literal2 = new ComplexPredicateLiteral(
				descriptor, true, terms2);
		return cf.makeEquivalenceClause(new ExtensionalityOrigin(clause),
				getPredicateLiterals(literal1, literal2),
				new ArrayList<EqualityLiteral>(),
				new ArrayList<ArithmeticLiteral>(),
				new ArrayList<EqualityLiteral>());
	}

	private void addLeftTerms(List<Sort> sorts, List<SimpleTerm> terms1,
			List<SimpleTerm> terms2, boolean positive) {

		for (final Sort sort : sorts) {
			final SimpleTerm term;
			if (positive) {
				term = variableContext.getNextVariable(sort);
			} else {
				term = variableContext.getNextFreshConstant(sort);
			}
			terms1.add(term);
			terms2.add(term);
		}
	}
	
	private List<PredicateLiteral> getPredicateLiterals(PredicateLiteral literal1, PredicateLiteral literal2) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		predicates.add(literal1);
		predicates.add(literal2);
		return predicates;
	}
	
	private boolean isUnitSetEqualityLiteral(Clause clause) {
		if (!clause.isUnit()) return false;
		if (clause.getEqualityLiteralsSize() == 1) {
			if (isSetEquality(clause.getEqualityLiteral(0))) return true;
		}
		return false;
	}
	
	private boolean isSetEquality(EqualityLiteral equality) {
		if (!equality.getSort().isSetSort()) return false;
		PredicateLiteralDescriptor descriptor = predicateTable.getDescriptor(equality.getSort());
		if (descriptor == null) return false;
		if (!descriptor.isGenuineMembership()) return false;
		return true;
	}
	
	private EqualityLiteral getUnitEqualityLiteral(Clause clause) {
		return clause.getEqualityLiteral(0);
	}

	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// do nothing
		return ProverResult.EMPTY_RESULT;
	}

	@Override
	public ProverResult next(boolean force) {
		return ProverResult.EMPTY_RESULT;
	}

	@Override
	public void registerDumper(Dumper dumper) {
		// do nothing
	}

	@Override
	public void removeClause(Clause clause) {
		// do nothing
	}
	
	@Override
	public String toString() {
		return "ExtensionalityProver";
	}


}
