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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.VariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.ResolutionResolver;
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
public class TestResolution extends AbstractInferrerTests {

	private static Sort A = new Sort(FormulaFactory.getDefault().makeIntegerType());

	private static Variable var0 = Util.cVar(A);
	private static Variable var1 = Util.cVar(A);

	private static Variable var00 = Util.cVar(A);
	private static Variable var11 = Util.cVar(A);

	private static LocalVariable fvar0 = Util.cFLocVar(0,A);
//	private static LocalVariable fvar1 = Util.cFLocVar(1,A);
	private static LocalVariable fvar2 = Util.cFLocVar(2,A);
	private static LocalVariable evar0 = Util.cELocVar(0,A);
	private static LocalVariable evar1 = Util.cELocVar(1,A);
	private static LocalVariable evar2 = Util.cELocVar(2,A);


	private IClause[] noClause() {
		return new IClause[0];
	}

	public void testSimple() {
		// normal case
		doTest(
				cClause(cProp(0),cProp(1)),
				cClause(cNotProp(0)),
				cClause(cProp(1))
		);
		// several match case
		doTest(
				cClause(cProp(0),cProp(0)),
				cClause(cNotProp(0)),
				cClause(cProp(0)),
				cClause(cProp(0))
		);
		// no match
		doTest(
				cClause(cProp(0),cProp(1)),
				cClause(cProp(2))
		);
		// no match
		doTest(
				cClause(cProp(0),cProp(1)),
				cClause(cProp(0))
		);
		//
		doTest(
				cClause(cNotProp(0),cProp(0)),
				cClause(cProp(0)),
				cClause(cProp(0))
		);
		doTest(
				cClause(cProp(0),cProp(0),cProp(1)),
				cClause(cNotProp(0)),
				cClause(cProp(0),cProp(1)),
				cClause(cProp(0),cProp(1))
		);

		// EQUIVALENCE
		doTest(
				cEqClause(cProp(0),cProp(1)),
				cClause(cNotProp(0)),
				cClause(cNotProp(1))
		);
		doTest(
				cEqClause(cProp(0),cProp(1)),
				cClause(cProp(0)),
				cClause(cProp(1))
		);
		// several match case
		doTest(
				cEqClause(cProp(0),cProp(0)),
				cClause(cNotProp(0)),
				cClause(cNotProp(0)),
				cClause(cNotProp(0))
		);
		doTest(
				cEqClause(cProp(0),cNotProp(0)),
				cClause(cNotProp(0)),
				cClause(cProp(0)),
				cClause(cProp(0))
		);
	}

	public void testRule1() {
		// PREDICATE LOGIC
		doTest(
				cClause(cPred(0,var0),cPred(1,var0)),
				cClause(cNotPred(0,var0)),
				cClause(mList(cPred(1,var0)),cNEqual(var00,var0))
		);
		doTest(
				cClause(cPred(0,a),cPred(1,a)),
				cClause(cNotPred(0,b)),
				cClause(mList(cPred(1,a)),cNEqual(a, b))
		);
	}

	public void testRule2() {
		doTest(
				cClause(cPred(0,evar1),cProp(1)),
				cClause(cNotPred(0,var0)),
				cClause(mList(cProp(1)),cNEqual(evar1, var00))
		);
	}

	public void testRule3() {
		doTest(
				cClause(cPred(0,var0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1, var00))
		);
	}

	// constants + pseudo constant
//	doTest(
//	cClause(cPred(0,evar1),cProp(1)),
//	cClause(cNotPred(0,a)),
//	cClause(mList(cProp(1)),cNEqual(evar1, a))
//	);
//	doTest(
//	cClause(cNotPred(0,evar1),cProp(1)),
//	cClause(cPred(0,a)),
//	cClause(mList(cProp(1)),cNEqual(evar1, a))
//	);

	public void testRule4and5() {
		// PREDICATE LOGIC WITH EQUIVALENCE
		doTest(
				cEqClause(cPred(0,var0),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var0, var00))
		);
		doTest(
				cEqClause(cPred(0,a),cPred(0,b),cNotPred(0,c)),
				cClause(cPred(0,b)),
				cEqClause(mList(cPred(0,b),cNotPred(0,c)),cNEqual(a, b)),
				cEqClause(mList(cPred(0,a),cNotPred(0,c)),cNEqual(b, b)),
				cEqClause(mList(cNotPred(0,a),cPred(0,b)),cNEqual(c, b))
		);
	}

	public void testRule4and5WithTransformation() {
		// Tests transformation eqclause->disjclause
		doTest(
				cEqClause(cPred(0,a),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cClause(mList(cPred(1,var0)),cNEqual(var00,a))
		);
		doTest(
				cEqClause(cNotPred(0,a),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cClause(mList(cNotPred(1,var0)),cNEqual(a,var00))
		);
		doTest(
				cEqClause(cPred(0,a),cPred(1,evar0)),
				cClause(cPred(0,var0)),
				cClause(mList(cPred(1,evar0)),cNEqual(a,var00))
		);
		doTest(
				cEqClause(cPred(0,a),cPred(1,fvar2)),
				cClause(cPred(0,var0)),
				cClause(mList(cPred(1,var11)),cNEqual(a,var00))
		);
		doTest(
				cEqClause(cPred(0,a),cPred(1,evar2)),
				cClause(cNotPred(0,var0)),
				cClause(mList(cNotPred(1,var11)),cNEqual(a, var00))
		);
		doTest(
				cEqClause(cPred(0,a),cPred(1,fvar2)),
				cClause(cNotPred(0,var0)),
				cClause(mList(cNotPred(1,evar2)),cNEqual(a, var00))
		);
	}

	public void testRule6and7() {
		// 4 TESTS with local variables + transformation
		doTest(
				cEqClause(cNotPred(0,evar2),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cClause(mList(cNotPred(1,var0)),cNEqual(var00,evar2))
		);
		doTest(
				cEqClause(cPred(0,fvar2),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cClause(mList(cPred(1,var0)),cNEqual(var00,evar2))
		);
		doTest(
				cEqClause(cNotPred(0,evar2),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var00,evar2))
		);
		doTest(
				cEqClause(cPred(0,fvar2),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var00,evar2))
		);
		// 
		doTest(
				cEqClause(cNotPred(0,cPlus(evar2,evar2)),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var00,cPlus(evar2,evar2)))
		);
		doTest(
				cEqClause(cPred(0,cPlus(fvar2,fvar2)),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var00,cPlus(evar2,evar2)))
		);
	}

	public void testRule8And9() {
		doTest(
				cEqClause(cPred(0,evar2),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cClause(mList(cPred(1,var0)),cNEqual(var00,var11))
		);
		doTest(
				cEqClause(cNotPred(0,fvar2),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cClause(mList(cNotPred(1,var0)),cNEqual(var00,var11))
		);
		// same tests with constants
		doTest(
				cEqClause(cPred(0,evar2),cPred(1,var0)),
				cClause(cPred(0,a)),
				cClause(mList(cPred(1,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cNotPred(0,fvar2),cPred(1,var0)),
				cClause(cPred(0,a)),
				cClause(mList(cNotPred(1,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cNotPred(0,evar2),cPred(1,var0)),
				cClause(cNotPred(0,a)),
				cClause(mList(cPred(1,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cPred(0,fvar2),cPred(1,var0)),
				cClause(cNotPred(0,a)),
				cClause(mList(cNotPred(1,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cPred(0,evar2),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var00,var11))
		);
		doTest(
				cEqClause(cNotPred(0,fvar2),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var00,var11))
		);

		doTest(
				cEqClause(cPred(0,cPlus(evar2,evar2)),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cPred(1,var1),cPred(1,var0)),cNEqual(var11,cPlus(var00,var00)))
		);
		doTest(
				cEqClause(cNotPred(0,cPlus(fvar2,fvar2)),cPred(1,var1),cPred(1,var0)),
				cClause(cPred(0,var0)),
				cEqClause(mList(cNotPred(1,var1),cPred(1,var0)),cNEqual(var11,cPlus(var00,var00)))
		);
	}

	public void testOtherRule() {
		// TODO check this
		doTest(
				cEqClause(cPred(0,var0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				cClause(mList(cNotProp(1)),cNEqual(evar1,var0))
		);
		doTest(
				cEqClause(cNotPred(0,var0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1,var0))
		);
	}

	public void testRuleDoNotApply() {
		// disjunctive clauses
		doTest(
				cClause(cPred(0,evar1),cProp(1)),
				cClause(cNotPred(0,a)),
				noClause()
		);
		doTest(
				cClause(cPred(0,a),cProp(1)),
				cClause(cNotPred(0,evar1)),
				noClause()
		);
		doTest(
				cClause(cPred(0,evar0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				noClause()
		);

		// equivalence clauses
		doTest(
				cEqClause(cPred(0,evar0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				noClause()
		);
		doTest(
				cEqClause(cNotPred(0,evar0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				noClause()
		);
		doTest(
				cEqClause(cPred(0,fvar0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				noClause()
		);
		doTest(
				cEqClause(cNotPred(0,fvar0),cProp(1)),
				cClause(cNotPred(0,evar1)),
				noClause()
		);

		// TODO variables mixed with local quantifiers
	}

	public void testInitialization() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		IClause clause = cClause(cPred(0));
		try {
			clause.infer(inferrer);
			fail();
		}
		catch (IllegalStateException e) {
			//nothing
		}
		try {
			inferrer.canInfer(clause);
			fail();
		}
		catch (IllegalStateException e) {
			//nothing
		}
	}


	public void doTest(IClause nonUnit, IClause unit, IClause... result) {
		IVariableContext context = new VariableContext();
		ResolutionInferrer inferrer = new ResolutionInferrer(context);
		ResolutionResolver resolution = new ResolutionResolver(inferrer);
		resolution.initialize(unit, nonUnit);
//		cleanVariables();

		for (IClause clause : result) {
			IClause inferredClause = resolution.next().getClause();
			assertEquals(clause, inferredClause);
			disjointVariables(inferredClause, unit);
			disjointVariables(inferredClause, nonUnit);
		}
		assertNull("\nUnit: " + unit + "NonUnit: " + nonUnit, resolution.next());
	}


}
