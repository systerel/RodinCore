package org.eventb.pp.core.inferrers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AA;
import static org.eventb.internal.pp.core.elements.terms.Util.d0AAA;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.d1AA;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.inferrers.ResolutionInferrer;
import org.eventb.internal.pp.core.provers.predicate.ResolutionResolver;
import org.eventb.internal.pp.core.provers.predicate.ResolutionResult;
import org.eventb.internal.pp.core.provers.predicate.iterators.IMatchIterable;
import org.junit.Test;

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



//	private Clause[] noClause() {
//		return new Clause[0];
//	}

    @Test
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

    @Test
	public void testRule1() {
		// PREDICATE LOGIC
		doTest(
				cClause(cPred(d0A,var0),cPred(d1A,var0)),
				cClause(cNotPred(d0A,var0)),
				cClause(mList(cPred(d1A,var0)),cNEqual(var0,var0))
		);
		doTest(
				cClause(cPred(d0A,a),cPred(d1A,a)),
				cClause(cNotPred(d0A,b)),
				cClause(mList(cPred(d1A,a)),cNEqual(a, b))
		);
	}

    @Test
	public void testRule2NewWithPredicatePreparation() {
		doTest(
				cClause(cPred(d0A,evar1),cProp(1)),
				cClause(cNotPred(d0A,var0)),
				cClause(mList(cProp(1)),cNEqual(evar1, evar1))
		);
	}

    @Test
	public void testRule2() {
		doTest(
				cClause(cPred(d0A,evar1),cProp(1)),
				cClause(cNotPred(d0A,var0)),
				cClause(mList(cProp(1)),cNEqual(evar1, evar1))
		);
		doTest(
				cClause(cPred(d0AA,evar0,var0),cPred(d1A,var0)),
				cClause(cNotPred(d0AA,var1,var1)),
				cClause(mList(cPred(d1A,var0)),cNEqual(evar0, evar0), cNEqual(evar0, var0))
		);
		doTest(
				cClause(cPred(d0AA,evar0,evar1),cPred(d1A,var0)),
				cClause(cNotPred(d0AA,var1,var0)),
				cClause(mList(cPred(d1A,var0)),cNEqual(evar0, evar0), cNEqual(evar1, evar1))
		);
		doTest(
				cClause(cPred(d0AA,evar0,var0),cPred(d1A,var1)),
				cClause(cNotPred(d0AA,var1,var0)),
				cClause(mList(cPred(d1A,var1)),cNEqual(evar0, evar0), cNEqual(var0, var0))
		);
		doTest(
				cClause(cPred(d0AA,evar0,evar1),cPred(d1A,var0)),
				cClause(cNotPred(d0AA,var1,var1)),
				cClause(mList(cPred(d1A,var0)),cNEqual(evar1, evar1), cNEqual(evar0, evar1))
		);
		
	}

    @Test
	public void testRule3() {
		doTest(
				cClause(cPred(d0A,var0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1, var00))
		);
	}

    @Test
	public void testRule4and5() {
		// PREDICATE LOGIC WITH EQUIVALENCE
		doTest(
				cEqClause(cPred(d0A,var0),cPred(d1A,var1),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cEqClause(mList(cPred(d1A,var1),cPred(d1A,var0)),cNEqual(var0, var0))
		);
		doTest(
				cEqClause(cPred(d0A,a),cPred(d0A,b),cNotPred(d0A,c)),
				cClause(cPred(d0A,b)),
				cEqClause(mList(cPred(d0A,b),cNotPred(d0A,c)),cNEqual(a, b)),
				cEqClause(mList(cPred(d0A,a),cNotPred(d0A,c)),cNEqual(b, b)),
				cEqClause(mList(cNotPred(d0A,a),cPred(d0A,b)),cNEqual(c, b))
		);
	}

    @Test
	public void testRule4and5WithTransformation() {
		// Tests transformation eqclause->disjclause
		doTest(
				cEqClause(cPred(d0A,a),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cPred(d1A,var0)),cNEqual(a,a))
		);
		doTest(
				cEqClause(cNotPred(d0A,a),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cNotPred(d1A,var0)),cNEqual(a,a))
		);
		doTest(
				cEqClause(cPred(d0A,a),cPred(d1A,evar0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cPred(d1A,evar0)),cNEqual(a,a))
		);
		doTest(
				cEqClause(cPred(d0A,a),cPred(d1A,fvar2)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cPred(d1A,var11)),cNEqual(a,a))
		);
		doTest(
				cEqClause(cPred(d0A,a),cPred(d1A,evar2)),
				cClause(cNotPred(d0A,var0)),
				cClause(mList(cNotPred(d1A,var11)),cNEqual(a, a))
		);
		doTest(
				cEqClause(cPred(d0A,a),cPred(d1A,fvar2)),
				cClause(cNotPred(d0A,var0)),
				cClause(mList(cNotPred(d1A,evar2)),cNEqual(a, a))
		);
	}

    @Test
	public void testRule6and7() {
		// 4 TESTS with local variables + transformation
		doTest(
				cEqClause(cNotPred(d0A,evar2),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cNotPred(d1A,var0)),cNEqual(evar2,evar2))
		);
		doTest(
				cEqClause(cPred(d0A,fvar2),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cPred(d1A,var0)),cNEqual(evar2,evar2))
		);
		doTest(
				cEqClause(cNotPred(d0A,evar2),cPred(d1A,var1),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cEqClause(mList(cNotPred(d1A,var1),cPred(d1A,var0)),cNEqual(evar2,evar2))
		);
		doTest(
				cEqClause(cPred(d0A,fvar2),cPred(d1A,var1),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cEqClause(mList(cPred(d1A,var1),cPred(d1A,var0)),cNEqual(evar2,evar2))
		);
		// 
		
//		doTest(
//				cEqClause(cNotPred(d0A,cPlus(evar2,evar2)),cPred(d1A,var1),cPred(d1A,var0)),
//				cClause(cPred(d0A,var0)),
//				cEqClause(mList(cNotPred(d1A,var1),cPred(d1A,var0)),cNEqual(cPlus(evar2,evar2),cPlus(evar2,evar2)))
//		);
//		doTest(
//				cEqClause(cPred(d0A,cPlus(fvar2,fvar2)),cPred(d1A,var1),cPred(d1A,var0)),
//				cClause(cPred(d0A,var0)),
//				cEqClause(mList(cPred(d1A,var1),cPred(d1A,var0)),cNEqual(cPlus(evar2,evar2),cPlus(evar2,evar2)))
//		);
	}

    @Test
	public void testRule8And9() {
		doTest(
				cEqClause(cPred(d0A,evar2),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cPred(d1A,var0)),cNEqual(var00,var00))
		);
		doTest(
				cEqClause(cNotPred(d0A,fvar2),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cClause(mList(cNotPred(d1A,var0)),cNEqual(var00,var00))
		);
		// same tests with constants
		doTest(
				cEqClause(cPred(d0A,evar2),cPred(d1A,var0)),
				cClause(cPred(d0A,a)),
				cClause(mList(cPred(d1A,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cNotPred(d0A,fvar2),cPred(d1A,var0)),
				cClause(cPred(d0A,a)),
				cClause(mList(cNotPred(d1A,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cNotPred(d0A,evar2),cPred(d1A,var0)),
				cClause(cNotPred(d0A,a)),
				cClause(mList(cPred(d1A,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cPred(d0A,fvar2),cPred(d1A,var0)),
				cClause(cNotPred(d0A,a)),
				cClause(mList(cNotPred(d1A,var0)),cNEqual(a,var11))
		);
		doTest(
				cEqClause(cPred(d0A,evar2),cPred(d1A,var1),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cEqClause(mList(cPred(d1A,var1),cPred(d1A,var0)),cNEqual(var00,var00))
		);
		doTest(
				cEqClause(cNotPred(d0A,fvar2),cPred(d1A,var1),cPred(d1A,var0)),
				cClause(cPred(d0A,var0)),
				cEqClause(mList(cNotPred(d1A,var1),cPred(d1A,var0)),cNEqual(var00,var00))
		);

//		doTest(
//				cEqClause(cPred(d0A,cPlus(evar2,evar2)),cPred(d1A,var1),cPred(d1A,var0)),
//				cClause(cPred(d0A,var0)),
//				cEqClause(mList(cPred(d1A,var1),cPred(d1A,var0)),cNEqual(var11,cPlus(var00,var00)))
//		);
//		doTest(
//				cEqClause(cNotPred(d0A,cPlus(fvar2,fvar2)),cPred(d1A,var1),cPred(d1A,var0)),
//				cClause(cPred(d0A,var0)),
//				cEqClause(mList(cNotPred(d1A,var1),cPred(d1A,var0)),cNEqual(var11,cPlus(var00,var00)))
//		);
	}

    @Test
	public void testOtherRule() {
		// TODO check this
		doTest(
				cEqClause(cPred(d0A,var0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cNotProp(1)),cNEqual(evar1,var0))
		);
		doTest(
				cEqClause(cNotPred(d0A,var0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1,var0))
		);
		doTest(
				cEqClause(cNotPred(d0AA,var0,var1),cPred(d1A,var1)),
				cClause(cNotPred(d0AA,evar0,var1)),
				cClause(mList(cPred(d1A,var1)),cNEqual(evar1,var0), cNEqual(var1, var1))
		);
		doTest(
				cEqClause(cNotPred(d0AA,var0,var1),cPred(d1A,var2)),
				cClause(cNotPred(d0AA,evar0,var1)),
				cClause(mList(cPred(d1A,var1)),cNEqual(evar0,var0), cNEqual(var2, var2))
		);
		// TODO document those rules
		doTest(
				cClause(cNotPred(d0AA,var0,var1),cPred(d1A,var1)),
				cClause(cPred(d0AA,evar0,var1)),
				cClause(mList(cPred(d1A,var1)),cNEqual(evar1,var0), cNEqual(var1, var1))
		);
		doTest(
				cClause(cNotPred(d0AA,var0,var1),cPred(d1A,var2)),
				cClause(cPred(d0AA,evar0,var1)),
				cClause(mList(cPred(d1A,var1)),cNEqual(evar0,var0), cNEqual(var2, var2))
		);
		doTest(
				cClause(cNotPred(d0AA,var0,var1),cPred(d1AA,var0,var1)),
				cClause(cPred(d0AA,evar0,var1)),
				cClause(mList(cPred(d1AA,var0,var1)),cNEqual(evar0,var0), cNEqual(var1, var1))
		);
		doTest(
				cClause(cNotPred(d0AAA,var0,var0,var1),cPred(d1AA,var0,var1)),
				cClause(cPred(d0AAA,evar0,evar1,var1)),
				cClause(mList(cPred(d1AA,var0,var1)),cNEqual(evar0,var0), cNEqual(evar1,var0), cNEqual(var1, var1))
		);
		doTest(
				cClause(cNotPred(d0AAA,var0,var0,var1),cPred(d1A,var1)),
				cClause(cPred(d0AAA,evar0,evar1,var1)),
				cClause(mList(cPred(d1A,var1)),cNEqual(evar0,var0), cNEqual(evar1,var0), cNEqual(var1, var1))
		);
		doTest(
				cClause(cNotPred(d0AA,var0,var0),cPred(d1A,var1)),
				cClause(cPred(d0AA,evar0,var1)),
				cClause(mList(cPred(d1A,var0)),cNEqual(evar0,var1),cNEqual(var1, var1))
		);
	}
	
	// constants + pseudo constant
	//	doTest(
	//	cClause(cPred(d0A,evar1),cProp(1)),
	//	cClause(cNotPred(d0A,a)),
	//	cClause(mList(cProp(1)),cNEqual(evar1, a))
	//	);
	//	doTest(
	//	cClause(cNotPred(d0A,evar1),cProp(1)),
	//	cClause(cPred(d0A,a)),
	//	cClause(mList(cProp(1)),cNEqual(evar1, a))
	//	);

    @Test
	public void testRuleDoNotApply() {
		// disjunctive clauses
		doTest(
				cClause(cPred(d0A,evar1),cProp(1)),
				cClause(cNotPred(d0A,a)),
				cClause(mList(cProp(1)),cNEqual(evar1, a))
		);
		doTest(
				cClause(cPred(d0A,a),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1, a))
		);
		doTest(
				cClause(cPred(d0A,evar0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1, evar0))
		);

		// equivalence clauses
		doTest(
				cEqClause(cPred(d0A,evar0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cNotProp(1)),cNEqual(evar0, evar1))
		);
		doTest(
				cEqClause(cNotPred(d0A,evar0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar0, var0))
		);
		doTest(
				cEqClause(cPred(d0A,fvar0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cNotProp(1)),cNEqual(evar0, var0))
		);
		doTest(
				cEqClause(cNotPred(d0A,fvar0),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar0, evar1))
		);
		
		doTest(
				cEqClause(cPred(d0A,a),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cNotProp(1)),cNEqual(evar1, a))
		);
		doTest(
				cEqClause(cNotPred(d0A,a),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(evar1, a))
		);
		doTest(
				cEqClause(cPred(d0A,a),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cNotProp(1)),cNEqual(evar1, a))
		);
		doTest(
				cEqClause(cNotPred(d0A,a),cProp(1)),
				cClause(cNotPred(d0A,evar1)),
				cClause(mList(cProp(1)),cNEqual(a, evar1))
		);

		// TODO variables mixed with local quantifiers
	}

    @Test
	public void testInitialization() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		Clause clause = cClause(cProp(0));
		try {
			clause.infer(inferrer);
			fail();
		}
		catch (IllegalStateException e) {
			//nothing
		}
	}
	
	private static class MyMatcher implements IMatchIterable {
		private List<Clause> list = new ArrayList<Clause>();
		
		MyMatcher(Clause clause) {
			this.list.add(clause);
		}
		
		public Iterator<Clause> iterator(PredicateLiteralDescriptor predicate, boolean isPositive) {
			return list.iterator();
		}
		
	}

	public void doTest(Clause nonUnit, Clause unit, Clause... result) {
//		nonUnit.checkIsBlockedOnInstantiationsAndUnblock();
//		unit.checkIsBlockedOnInstantiationsAndUnblock();
		
		VariableContext context = new VariableContext();
		ResolutionInferrer inferrer = new ResolutionInferrer(context);
		ResolutionResolver resolution = new ResolutionResolver(inferrer, new MyMatcher(nonUnit));
		resolution.initialize(unit);
//		cleanVariables();

		for (Clause clause : result) {
			Clause inferredClause = resolution.next().getDerivedClause();
			assertEquals(clause, inferredClause);
			disjointVariables(inferredClause, unit);
			disjointVariables(inferredClause, nonUnit);
		}
		ResolutionResult r = resolution.next();
		if (r != null) {
			System.out.println("When doing resolution between:\n  " + unit
					+ "\nand\n  " + nonUnit);
			for (; r != null; r = resolution.next()) {
				System.out.println("Missing inferred clause: "
						+ r.getDerivedClause());
			}
			fail("Missing inferred clauses, see stdout.");
		}
	}
	
    @Test
	public void testSubsumption() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(cProp(0)));
		inferrer.setPosition(0);
		Clause clause = cClause(cNotProp(0), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause().equals(cClause(cNotProp(0), cProp(1))));
	}

    @Test
	public void testNoSubsumptionWithConstants() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(cPred(d0A,a)));
		inferrer.setPosition(0);
		Clause clause = cClause(cNotPred(d0A,b), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause()==null);
	}
	
    @Test
	public void testSubsumptionWithConstants() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(cPred(d0A,a)));
		inferrer.setPosition(0);
		Clause clause = cClause(cNotPred(d0A,a), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause().equals(cClause(cNotPred(d0A,a), cProp(1))));
	}
	
    @Test
	public void testNoSubsumptionWithVariables() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(cPred(d0A,a)));
		inferrer.setPosition(0);
		Clause clause = cClause(cNotPred(d0A,x), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause()==null);
	}
	
    @Test
	public void testSubsumptionWithVariables() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(cPred(d0A,x)));
		inferrer.setPosition(0);
		Clause clause = cClause(cNotPred(d0A,a), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause().equals(cClause(cNotPred(d0A,a), cProp(1))));
	}
	
    @Test
	public void testSubsumptionWithVariables2() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(cPred(d0A,x)));
		inferrer.setPosition(0);
		Clause clause = cClause(cNotPred(d0A,x), cPred(d1AA,x,y));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause().equals( cClause(cNotPred(d0A,x), cPred(d1AA,x,y))));
	}
	
	
    @Test
	public void testSubsumptionWithLevels() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(BASE,cProp(0)));
		inferrer.setPosition(0);
		Clause clause = cClause(ONE,cNotProp(0), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause().equals(cClause(ONE,cNotProp(0), cProp(1))));
	}

    @Test
	public void testNoSubsumptionWithLevels() {
		ResolutionInferrer inferrer = new ResolutionInferrer(new VariableContext());
		inferrer.setUnitClause(cClause(ONE,cProp(0)));
		inferrer.setPosition(0);
		Clause clause = cClause(BASE,cNotProp(0), cProp(1));
		clause.infer(inferrer);
		assertTrue(inferrer.getSubsumedClause()==null);
	}
	
}
