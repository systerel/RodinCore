/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.core.provers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.d2A;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.internal.pp.CancellationChecker;
import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.equality.EqualityProver;
import org.eventb.internal.pp.core.provers.extensionality.ExtensionalityProver;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.internal.pp.core.simplifiers.LiteralSimplifier;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;
import org.junit.Test;

public class TestConditions extends AbstractPPTest {

    @Test
	public void testConditions() {
		List<Clause> clauses = mList(
				cClause(cPred(d0A, a), cPred(d1A, a), cPred(d2A, a)),
				cClause(cProp(3), Util.cEqual(a, b)),
				cClause(cNotPred(d0A, b)),
				cClause(cNotPred(d1A, b)),
				cClause(cNotPred(d2A, b)),
				cClause(cNotProp(3))
		);
		
		ClauseDispatcher prover = initProver();
		prover.setClauses(clauses);
		prover.mainLoop(-1);
		PPResult result = prover.getResult();
		assertTrue(result.getResult() == Result.valid);
	}
	
	private ClauseDispatcher initProver() {
		final CancellationChecker cancellation = CancellationChecker.newChecker(null);
		ClauseDispatcher proofStrategy = new ClauseDispatcher(cancellation);
		VariableContext context = new VariableContext();
		PredicateTable table = new PredicateTable();
		
		PredicateProver prover = new PredicateProver(context);
		CaseSplitter casesplitter = new CaseSplitter(context, proofStrategy.getLevelController());
		SeedSearchProver seedsearch = new SeedSearchProver(context, proofStrategy.getLevelController());
		EqualityProver equalityprover = new EqualityProver(context);
		ExtensionalityProver extensionalityProver = new ExtensionalityProver(table, context);
		proofStrategy.addProverModule(prover);
		proofStrategy.addProverModule(casesplitter);
		proofStrategy.addProverModule(seedsearch);
		proofStrategy.addProverModule(equalityprover);
		proofStrategy.addProverModule(extensionalityProver);
		
		OnePointRule onepoint = new OnePointRule();
		ExistentialSimplifier existential = new ExistentialSimplifier(context);
		LiteralSimplifier literal = new LiteralSimplifier(context);
		EqualitySimplifier equality = new EqualitySimplifier(context);
		proofStrategy.addSimplifier(onepoint);
		proofStrategy.addSimplifier(equality);
		proofStrategy.addSimplifier(existential);
		proofStrategy.addSimplifier(literal);
		
		return proofStrategy;
	}
}
