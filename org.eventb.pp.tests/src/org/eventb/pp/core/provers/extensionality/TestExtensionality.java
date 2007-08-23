package org.eventb.pp.core.provers.extensionality;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.descriptor;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.extensionality.ExtensionalityProver;

public class TestExtensionality extends AbstractPPTest {

	private static PredicateLiteralDescriptor P0 = descriptor(0, 2, 2, mList(A,PA));
	private static PredicateLiteralDescriptor P1 = descriptor(1, 3, 3, mList(A,B,PAB));
	
	private static Variable x = Util.cVar(1,A);
	private static Variable y = Util.cVar(2,A);
	private static Variable py = Util.cVar(3,PA);
	
	private static Constant e = Util.cCons("e",A);
	private static Constant f = Util.cCons("f",A);
	
	private static Constant a1 = Util.cCons("a1", PA);
	private static Constant a2 = Util.cCons("a2", PA);
	private static Constant a3 = Util.cCons("a3", PA);
	private static Constant a4 = Util.cCons("a4", PA);
	static Constant c0 = Util.cCons("0", PA);
	
	public void testAllowedInputWithEquality() {
		doTest(cClause(cProp(0)), null);
		doTest(cClause(cPred(0, x)), null);
		doTest(cClause(cEqual(a, b)), null);
		doTest(cClause(cEqual(a, b)), null);
		doTest(cClause(new ArrayList<Literal<?, ?>>(),cEqual(a1, a2)),null, P0, PA);
		doTest(cClause(cProp(1),cEqual(a1,a2)), null, P0, PA);
		doTest(cClause(cEqual(a1,a2)),null);
	}
	
	public void testEquality() {
		doTest(	cClause(cEqual(a1,a2)),
				cEqClause(cPred(0,x,a1),cPred(0,x,a2)),
				P0, PA
		);
	}
	
	public void testInEquality() {
		doTest(	cClause(cNEqual(a1,a2)),
				cEqClause(cNotPred(0,c0,a1),cPred(0,c0,a2)),
				new VariableContext() {
					@Override
					public Constant getNextFreshConstant(Sort sort) {return c0;}
				},
				P0, PA
		);
	}
	
	public void testAllowedInputWithEquivalence() {
		doTest(cEqClause(cPred(P0,x,a1),cPred(P0,a,a2)),null);
		doTest(cClause(cPred(P0,x,a1),cPred(P0,x,a2)),null);
		doTest(cEqClause(cPred(P0,x,a1),cPred(P0,y,a2)),null);
		doTest(cEqClause(mList(cPred(0,x,a1),cPred(0,x,a2)),cEqual(a1, a2)),null, P0, PA);
		doTest(cEqClause(cProp(1),cPred(0,x,a1),cPred(0,x,a2)),null, P0, PA);
		doTest(cEqClause(cEqual(a1,a2),cPred(0,x,a1),cPred(0,x,a2)),null);
		doTest(cEqClause(cPred(P0,x,a1),cPred(P1,x,a2)),null,P0,PA);
		doTest(cEqClause(cNotPred(P0,x,a1),cPred(P0,x,a2)), null, P0, PA);
		doTest(cEqClause(cNotPred(P0,e,a1),cPred(P0,f,a2)), null, P0, PA);
		doTest(cEqClause(cNotPred(P0,x,a1),cNotPred(P0,x,py)), null, P0, PA);
	}
	
	public void testPositiveEquivalenceClause() {
		doTest(	cEqClause(cPred(P0,x,a1),cPred(P0,x,a2)),
				cClause(cEqual(a1,a2)),
				P0, PA
		);
		doTest(	cEqClause(cNotPred(P0,x,a1),cNotPred(P0,x,a2)),
				cClause(cEqual(a1,a2)),
				P0, PA
		);
	}
	
	public void testNegativeEquivalenceClause() {
		doTest(	cEqClause(cNotPred(P0,e,a1),cPred(P0,e,a2)),
				cClause(cNEqual(a1,a2)),
				P0, PA
		);
		doTest(	cEqClause(cPred(P0,e,a1),cNotPred(P0,e,a2)),
				cClause(cNEqual(a1,a2)),
				P0, PA
		);
	}
	
	public void testPositiveDisjunctiveClauses() {
		doTest(	mList(
				cClause(cNotPred(P0,x,a1),cPred(P0,x,a2)),
				cClause(cPred(P0,y,a3),cNotPred(P0,y,a4))),
				cClause(mList(cEqual(a1,a2)),cNEqual(a1,a3),cNEqual(a2,a4)),
				P0,PA
		);
	}
	
	public void testNegativeDisjunctiveClauses() {
		doTest(	mList(
				cClause(cPred(P0,a,a1),cPred(P0,b,a2)),
				cClause(cNotPred(P0,a,a3),cNotPred(P0,b,a4))),
				cClause(mList(cNEqual(a1,a2)),cNEqual(a1,a3),cNEqual(a2,a4)),
				P0,PA
		);
	}
	
	public void testOneDisjunctiveClause() {
		doTest(	cClause(cNotPred(P0,x,a1),cPred(P0,x,a2)),
				null,
				P0,PA
		);
	}
	
	
	public void testLooping() {
		IProverModule module = getProver(getPredicateTable(P0, PA), new VariableContext());
		Clause clause = cEqClause(cPred(P0,x,a1),cPred(P0,x,a2));
		
		ProverResult proverResult = module.addClauseAndDetectContradiction(clause);
		while (!proverResult.equals(ProverResult.EMPTY_RESULT)) {
			clause = proverResult.getGeneratedClauses().iterator().next();
			proverResult = module.addClauseAndDetectContradiction(clause);
		}
	}
	
	private void doTest(Clause clause, Clause result, Object... objs) {
		doTest(clause, result, new VariableContext(), objs);
	}
	
	private void doTest(List<Clause> clauses, Clause result, Object... objs) {
		doTest(clauses, result, new VariableContext(), objs);
	}
	
	private void doTest(List<Clause> clauses, Clause result, VariableContext context, Object... objs) {
		IProverModule module = getProver(getPredicateTable(objs), context);
		Set<Clause> setResult = new HashSet<Clause>();
		setResult.add(result);
		
		ProverResult proverResult = null;
		for (Clause clause : clauses) {
			proverResult = module.addClauseAndDetectContradiction(clause);
		}
		if (result==null) assertEquals(proverResult, ProverResult.EMPTY_RESULT);
		else assertEquals(proverResult.getGeneratedClauses(), setResult);
		
		assertTrue(proverResult.getSubsumedClauses().isEmpty());
	}
	
	private void doTest(Clause clause, Clause result, VariableContext context, Object... objs) {
		doTest(Arrays.asList(new Clause[]{clause}), result, context, objs);
	}
	
	private IProverModule getProver(PredicateTable predicateTable, VariableContext context) {
		IProverModule prover = new ExtensionalityProver(predicateTable, context);
		return prover;
	}

	private PredicateTable getPredicateTable(Object... objs) {
		PredicateTable predicateTable = new PredicateTable();
		for (int i = 0; i < objs.length; i=1+2) {
			PredicateLiteralDescriptor descriptor = (PredicateLiteralDescriptor)objs[i];
			Sort sort = (Sort)objs[i+1];
			predicateTable.addSort(sort, descriptor);
		}
		return predicateTable;
	}
	
}

