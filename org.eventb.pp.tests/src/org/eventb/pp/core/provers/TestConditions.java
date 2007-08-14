package org.eventb.pp.core.provers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.List;

import org.eventb.internal.pp.core.ClauseDispatcher;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.PredicateTable;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.provers.equality.EqualityProver;
import org.eventb.internal.pp.core.provers.extensionality.ExtensionalityProver;
import org.eventb.internal.pp.core.provers.predicate.PredicateProver;
import org.eventb.internal.pp.core.provers.seedsearch.SeedSearchProver;
import org.eventb.internal.pp.core.simplifiers.EqualitySimplifier;
import org.eventb.internal.pp.core.simplifiers.ExistentialSimplifier;
import org.eventb.internal.pp.core.simplifiers.LiteralSimplifier;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.eventb.internal.pp.loader.clause.VariableContext;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.PPResult;
import org.eventb.pp.Util;
import org.eventb.pp.PPResult.Result;

public class TestConditions extends AbstractPPTest {

	public void testConditions() {
		initDebug();
		
		List<Clause> clauses = mList(
				cClause(cPred(0, a), cPred(1, a), cPred(2, a)),
				cClause(cProp(3), Util.cEqual(a, b)),
				cClause(cNotPred(0, b)),
				cClause(cNotPred(1, b)),
				cClause(cNotPred(2, b)),
				cClause(cNotProp(3))
		);
		
		ClauseDispatcher prover = initProver();
		prover.setClauses(clauses);
		prover.mainLoop(-1);
		PPResult result = prover.getResult();
		assertTrue(result.getResult() == Result.valid);
	}
	
	private ClauseDispatcher initProver() {
		ClauseDispatcher proofStrategy = new ClauseDispatcher();
		IVariableContext context = new VariableContext();
		PredicateTable table = new PredicateTable();
		
		PredicateProver prover = new PredicateProver(context);
		CaseSplitter casesplitter = new CaseSplitter(context, proofStrategy);
		SeedSearchProver seedsearch = new SeedSearchProver(context);
		EqualityProver equalityprover = new EqualityProver(context);
		ExtensionalityProver extensionalityProver = new ExtensionalityProver(table, context);
		proofStrategy.addProver(prover);
		proofStrategy.addProver(casesplitter);
		proofStrategy.addProver(seedsearch);
		proofStrategy.addProver(equalityprover);
		proofStrategy.addProver(extensionalityProver);
		
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
