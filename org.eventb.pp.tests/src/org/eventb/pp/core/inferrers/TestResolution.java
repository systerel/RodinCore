package org.eventb.pp.core.inferrers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPlus;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.mList;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.ResolutionResolver;
import org.eventb.pp.AbstractPPTest;
import org.eventb.pp.Util;

/**
 * This class tests the one point rule. There are several tests :
 * <ul>
 * 	<li>one-point rule on non-arithmetic</li>
 * 	<li>one-point rule on arithmetic with single expressions</li>
 * 	<li>one-point rule on arithmetic with complex expressions</li>
 * </ul>
 *
 * @author François Terrier
 *
 */
public class TestResolution extends AbstractPPTest {

	private class TestPair {
		IClause unit, nonUnit;
		IClause[] result;
		
		TestPair(IClause nonUnit, IClause unit, IClause... result) {
			this.unit = unit;
			this.nonUnit = nonUnit;
			this.result = result;
		}
		
//		TestPair(String input, IClause output) {
//			LoaderResult result = Util.doPhaseOneAndTwo(input);		
//			assert result.getClauses().size() == 1;
//			assert result.getLiterals().size() == 0;
//			this.input = result.getClauses().iterator().next();
//			this.output = output;
//		}
		
	}
	
	private static Sort A = new Sort(FormulaFactory.getDefault().makeGivenType("A"));
	
	private static Variable var0 = Util.cVar(A);
	private static Variable var1 = Util.cVar(A);
	
	private static Variable var00 = Util.cVar(A);
	private static Variable var11 = Util.cVar(A);
	
	private static LocalVariable fvar0 = Util.cFLocVar(0,A);
	private static LocalVariable fvar1 = Util.cFLocVar(1,A);
	private static LocalVariable fvar2 = Util.cFLocVar(2,A);
	private static LocalVariable evar0 = Util.cELocVar(0,A);
	private static LocalVariable evar1 = Util.cELocVar(1,A);
	private static LocalVariable evar2 = Util.cELocVar(2,A);
	
	
//	private IVariableContext variableContext() {
//		VariableContext context = new VariableContext();
//		context.putInCache(var00);
//		context.putInCache(var11);
//		return context;
//	}
	
//	private static Variable var2e = evar2.getVariable();
//	private static Variable var2f = fvar2.getVariable();
	
	private IClause[] noClause() {
		return new IClause[0];
	}
	
	TestPair[] tests = new TestPair[]{
			// normal case
			new TestPair(
					cClause(cProp(0),cProp(1)),
					cClause(cNotProp(0)),
					cClause(cProp(1))
			),
			// several match case
			new TestPair(
					cClause(cProp(0),cProp(0)),
					cClause(cNotProp(0)),
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			// no match
			new TestPair(
					cClause(cProp(0),cProp(1)),
					cClause(cProp(2))
			),
			// no match
			new TestPair(
					cClause(cProp(0),cProp(1)),
					cClause(cProp(0))
			),
			//
			new TestPair(
					cClause(cNotProp(0),cProp(0)),
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			new TestPair(
					cClause(cProp(0),cProp(0),cProp(1)),
					cClause(cNotProp(0)),
					cClause(cProp(0),cProp(1)),
					cClause(cProp(0),cProp(1))
			),
			
			// EQUIVALENCE
			new TestPair(
					cEqClause(cProp(0),cProp(1)),
					cClause(cNotProp(0)),
					cClause(cNotProp(1))
			),
			new TestPair(
					cEqClause(cProp(0),cProp(1)),
					cClause(cProp(0)),
					cClause(cProp(1))
			),
			// several match case
			new TestPair(
					cEqClause(cProp(0),cProp(0)),
					cClause(cNotProp(0)),
					cClause(cNotProp(0)),
					cClause(cNotProp(0))
			),
			new TestPair(
					cEqClause(cProp(0),cNotProp(0)),
					cClause(cNotProp(0)),
					cClause(cProp(0)),
					cClause(cProp(0))
			),
			
			// PREDICATE LOGIC
			new TestPair(
					cClause(cPred(0,var0),cPred(1,var0)),
					cClause(cNotPred(0,var0)),
					cClause(cPred(1,var0),cNEqual(var00,var0))
			),
			new TestPair(
					cClause(cPred(0,a),cPred(1,a)),
					cClause(cNotPred(0,b)),
					cClause(cPred(1,a),cNEqual(a, b))
			),
			new TestPair(
					cClause(cPred(0,evar1),cProp(1)),
					cClause(cNotPred(0,var0)),
					cClause(cProp(1),cNEqual(evar1, var00))
			),
			// constants + pseudo constant
//			new TestPair(
//					cClause(cPred(0,evar1),cProp(1)),
//					cClause(cNotPred(0,a)),
//					cClause(cProp(1),cNEqual(evar1, a))
//			),
			
			// PREDICATE LOGIC WITH EQUIVALENCE
			new TestPair(
					cEqClause(cPred(0,var0),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var0, var00))
			),
			new TestPair(
					cEqClause(cPred(0,a),cPred(0,b),cNotPred(0,c)),
					cClause(cPred(0,b)),
					cEqClause(mList(cPred(0,b),cNotPred(0,c)),cNEqual(a, b)),
					cEqClause(mList(cPred(0,a),cNotPred(0,c)),cNEqual(b, b)),
					cEqClause(mList(cNotPred(0,a),cPred(0,b)),cNEqual(c, b))
			),
			
			// Tests transformation eqclause->disjclause
			new TestPair(
					cEqClause(cPred(0,a),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cClause(cPred(1,var0),cNEqual(var00,a))
			),
			new TestPair(
					cEqClause(cNotPred(0,a),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cClause(cNotPred(1,var0),cNEqual(a,var00))
			),
			new TestPair(
					cEqClause(cPred(0,a),cPred(1,evar0)),
					cClause(cPred(0,var0)),
					cClause(cPred(1,evar0),cNEqual(a,var00))
			),
			new TestPair(
					cEqClause(cPred(0,a),cPred(1,fvar2)),
					cClause(cPred(0,var0)),
					cClause(cPred(1,var11),cNEqual(a,var00))
			),
			new TestPair(
					cEqClause(cPred(0,a),cPred(1,evar2)),
					cClause(cNotPred(0,var0)),
					cClause(cNotPred(1,var11),cNEqual(a, var00))
			),
			new TestPair(
					cEqClause(cPred(0,a),cPred(1,fvar2)),
					cClause(cNotPred(0,var0)),
					cClause(cNotPred(1,evar2),cNEqual(a, var00))
			),
			
			// 4 TESTS with local variables + transformation
			new TestPair(
					cEqClause(cNotPred(0,evar2),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cClause(cNotPred(1,var0),cNEqual(var00,evar2))
			),
			new TestPair(
					cEqClause(cPred(0,fvar2),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cClause(cPred(1,var0),cNEqual(var00,evar2))
			),
			new TestPair(
					cEqClause(cPred(0,evar2),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cClause(cPred(1,var0),cNEqual(var00,var11))
			),
			new TestPair(
					cEqClause(cNotPred(0,fvar2),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cClause(cNotPred(1,var0),cNEqual(var00,var11))
			),
			// same tests with constants
			new TestPair(
					cEqClause(cPred(0,evar2),cPred(1,var0)),
					cClause(cPred(0,a)),
					cClause(cPred(1,var0),cNEqual(a,var11))
			),
			new TestPair(
					cEqClause(cNotPred(0,fvar2),cPred(1,var0)),
					cClause(cPred(0,a)),
					cClause(cNotPred(1,var0),cNEqual(a,var11))
			),
			new TestPair(
					cEqClause(cNotPred(0,evar2),cPred(1,var0)),
					cClause(cNotPred(0,a)),
					cClause(cPred(1,var0),cNEqual(a,var11))
			),
			new TestPair(
					cEqClause(cPred(0,fvar2),cPred(1,var0)),
					cClause(cNotPred(0,a)),
					cClause(cNotPred(1,var0),cNEqual(a,var11))
			),
			
			
			// 4 TESTS with local variables
			new TestPair(
					cEqClause(cNotPred(0,evar2),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var00,evar2))
			),
			new TestPair(
					cEqClause(cPred(0,fvar2),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var00,evar2))
			),
			new TestPair(
					cEqClause(cPred(0,evar2),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var00,var11))
			),
			new TestPair(
					cEqClause(cNotPred(0,fvar2),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var00,var11))
			),
			
			// 
			new TestPair(
					cEqClause(cNotPred(0,cPlus(evar2,evar2)),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var00,cPlus(evar2,evar2)))
			),
			new TestPair(
					cEqClause(cPred(0,cPlus(fvar2,fvar2)),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var00,cPlus(evar2,evar2)))
			),
			new TestPair(
					cEqClause(cPred(0,cPlus(evar2,evar2)),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var11,cPlus(var00,var00)))
			),
			new TestPair(
					cEqClause(cNotPred(0,cPlus(fvar2,fvar2)),cPred(1,var1),cPred(1,var0)),
					cClause(cPred(0,var0)),
					cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var11,cPlus(var00,var00)))
			),
	};
	
	TestPair[] tests2 = new TestPair[]{
			// disjunctive clauses
			new TestPair(
					cClause(cPred(0,evar1),cProp(1)),
					cClause(cNotPred(0,a)),
					noClause()
			),
			new TestPair(
					cClause(cPred(0,a),cProp(1)),
					cClause(cNotPred(0,evar1)),
					noClause()
			),
			new TestPair(
					cClause(cPred(0,evar0),cProp(1)),
					cClause(cNotPred(0,evar1)),
					noClause()
			),

			// equivalence clauses
			new TestPair(
					cEqClause(cPred(0,evar1),cProp(1)),
					cClause(cNotPred(0,a)),
					noClause()
			),
			new TestPair(
					cEqClause(cPred(0,fvar1),cProp(1)),
					cClause(cPred(0,a)),
					noClause()
			),
			new TestPair(
					cEqClause(cNotPred(0,evar1),cProp(1)),
					cClause(cPred(0,a)),
					noClause()
			),
			new TestPair(
					cEqClause(cNotPred(0,fvar1),cProp(1)),
					cClause(cNotPred(0,a)),
					noClause()
			),
			new TestPair(
					cEqClause(cPred(0,evar0),cProp(1)),
					cClause(cNotPred(0,evar1)),
					noClause()
			),
			new TestPair(
					cEqClause(cNotPred(0,evar0),cProp(1)),
					cClause(cNotPred(0,evar1)),
					noClause()
			),
			new TestPair(
					cEqClause(cPred(0,fvar0),cProp(1)),
					cClause(cNotPred(0,evar1)),
					noClause()
			),
			new TestPair(
					cEqClause(cNotPred(0,fvar0),cProp(1)),
					cClause(cNotPred(0,evar1)),
					noClause()
			),
			
			// TODO variables mixed with local quantifiers
			
	};

	public void testRules() {
		doTest(tests);
	}
	
	public void testNoMatchBetweenPseudoConstantAndConstants() {
		doTest(tests2);
	}
	
	public void testInitialization() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		IClause clause = cClause(cPred(0));
		try {
			clause.infer(inferrer);
			fail();
		}
		catch (IllegalStateException e) {}
		try {
			inferrer.canInfer(clause);
			fail();
		}
		catch (IllegalStateException e) {}
	}

	
	public void doTest(TestPair[] tests) {
		for (TestPair test : tests) {
			IVariableContext context = new VariableContext();
			ResolutionInferrer inferrer = new ResolutionInferrer(context);
			ResolutionResolver resolution = new ResolutionResolver(inferrer);
			resolution.initialize(test.unit, test.nonUnit);
//			cleanVariables();
			
			for (IClause clause : test.result) {
				IClause inferredClause = resolution.next().getClause();
				assertEquals(clause, inferredClause);
				disjointVariables(inferredClause, test.unit);
				disjointVariables(inferredClause, test.nonUnit);
			}
			assertNull("\nUnit: " + test.unit + "NonUnit: " + test.nonUnit, resolution.next());
		}
	}

	public void disjointVariables(IClause clause1, IClause clause2) {
		Set<Variable> vars1 = collectVariables(clause1);
		Set<Variable> vars2 = collectVariables(clause2);
		for (Variable variable : vars2) {
			assertFalse("Clause1: "+clause1.toString()+", clause2: "+clause2.toString(),
					vars1.contains(variable));
		}
		for (Variable variable : vars1) {
			assertFalse("Clause1: "+clause1.toString()+", clause2: "+clause2.toString(),
					vars2.contains(variable));
		}
	}
	
	private Set<Variable> collectVariables(IClause clause) {
		Set<Variable> vars = new HashSet<Variable>();
		for (IPredicate literal : clause.getPredicateLiterals()) {
			for (Term term : literal.getTerms()) {
				term.collectVariables(vars);
			}
		}
		for (IEquality equality : clause.getEqualityLiterals()) {
			for (Term term : equality.getTerms()) {
				term.collectVariables(vars);
			}
		}
		return vars;
	}
	
}
