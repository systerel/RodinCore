package org.eventb.pp.loader;

import static org.eventb.pp.Util.cAEqual;
import static org.eventb.pp.Util.cANEqual;
import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cCons;
import static org.eventb.pp.Util.cDiv;
import static org.eventb.pp.Util.cELocVar;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cExpn;
import static org.eventb.pp.Util.cFLocVar;
import static org.eventb.pp.Util.cLE;
import static org.eventb.pp.Util.cLess;
import static org.eventb.pp.Util.cMinus;
import static org.eventb.pp.Util.cMod;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPlus;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;
import static org.eventb.pp.Util.cTimes;
import static org.eventb.pp.Util.cVar;
import static org.eventb.pp.Util.mList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LoaderResult;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;
import org.eventb.pp.Util;

// TODO test negation of the goal
public class TestClauseBuilder extends TestCase {
	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	class TestPair {
		List<String> predicate;
		Collection<Clause> clauses;
		Collection<? extends Literal> unitClauses;
		
		TestPair (List<String> predicate, Collection<? extends Literal> unitClauses, Clause... clauses) {
			this.predicate = predicate;
			this.unitClauses = unitClauses;
			this.clauses = Arrays.asList(clauses);
		}
	}
	
	private static SimpleTerm x = cVar(0);
	private static SimpleTerm y = cVar(1);
	private static SimpleTerm z = cVar(2);
	private static SimpleTerm t = cVar(3);
	
	private static SimpleTerm a = cCons("a");
	private static SimpleTerm b = cCons("b");
	private static SimpleTerm c = cCons("c");
	private static SimpleTerm d = cCons("d");
	private static SimpleTerm e = cCons("e");
	private static SimpleTerm n = cCons("n");
	private static SimpleTerm m = cCons("m");
	private static SimpleTerm D = cCons("D");
	private static SimpleTerm M = cCons("M");
	private static SimpleTerm P = cCons("P");
	private static SimpleTerm Q = cCons("Q");
	private static SimpleTerm R = cCons("R");
	private static SimpleTerm S = cCons("S");
	private static SimpleTerm SS = cCons("SS");
	private static SimpleTerm T = cCons("T");
	private static SimpleTerm U = cCons("U");
	private static SimpleTerm one = cCons("1");
	private static SimpleTerm zero = cCons("0");
	private static SimpleTerm TRUE = cCons("TRUE");

	private static Sort A = new Sort(ff.makeGivenType("A"));
	private static Sort PA = new Sort(ff.makePowerSetType(ff.makeGivenType("A")));
	private static Sort PAB = new Sort(ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"),ff.makeGivenType("B"))));
	
	private static Sort B = new Sort(ff.makeGivenType("B"));
	private static Sort C = new Sort(ff.makeGivenType("C"));
	private static Sort Ssort = new Sort(ff.makeGivenType("S"));
	private static Sort Usort = new Sort(ff.makeGivenType("U"));
	
	private static Sort INT = Sort.ARITHMETIC;
	
	
	private static SimpleTerm evar0 = cELocVar(0, INT);
	
	private static SimpleTerm wInt = cVar(INT);
	private static SimpleTerm xInt = cVar(INT);
	private static SimpleTerm yInt = cVar(INT);
	private static SimpleTerm zInt = cVar(INT);
	
	private static SimpleTerm zA = cVar(A);
	private static SimpleTerm zB = cVar(B);
	private static SimpleTerm zC = cVar(C);
	
	private static SimpleTerm tA = cVar(A);
	private static SimpleTerm tC = cVar(C);
	private static SimpleTerm tPAB = cVar(PAB); 
	
	private static SimpleTerm xA = cVar(A);
	private static SimpleTerm xB = cVar(B);
	private static SimpleTerm xS = cVar(Ssort);
	private static SimpleTerm xPA = cVar(PA);
	
	private static SimpleTerm yA = cVar(A);
	private static SimpleTerm yPA = cVar(PA);
	
	private static SimpleTerm yB = cVar(B);
	private static SimpleTerm yC = cVar(C);
	private static SimpleTerm yU = cVar(Usort);
	private static SimpleTerm yS = cVar(Ssort);
	
	
	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		
		env.addName("x0", ff.makeGivenType("A"));
		env.addName("x1", ff.makeGivenType("B"));
		env.addName("a", ff.makeGivenType("S"));
//		env.addName("b", ff.makeGivenType("S"));
		
		
		env.addName("VV", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("B")),ff.makeGivenType("C"))));
		env.addName("V", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("B"))));
		env.addName("W", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("B"))));
		env.addName("X", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("C"))));
		env.addName("Y", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("C"))));
		
		env.addName("e", ff.makeBooleanType());
		env.addName("f", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("A"), ff.makeGivenType("B"))));
		env.addName("n", ff.makeIntegerType());
		env.addName("N", ff.makePowerSetType(ff.makeIntegerType()));
		env.addName("S", ff.makePowerSetType(ff.makeGivenType("S")));
		env.addName("P", ff.makePowerSetType(ff.makeGivenType("B")));
		env.addName("Q", ff.makePowerSetType(ff.makeGivenType("A")));
		env.addName("R", ff.makePowerSetType(ff.makeGivenType("A")));
		env.addName("U", ff.makePowerSetType(ff.makeGivenType("U")));
		env.addName("M", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(ff.makeGivenType("B"),ff.makeGivenType("A")),ff.makeGivenType("A"))));
		env.addName("SS", ff.makePowerSetType(ff.makeGivenType("S")));
		env.addName("T", ff.makePowerSetType(ff.makeProductType(ff.makeIntegerType(), ff.makeIntegerType())));
		env.addName("TT", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(ff.makeIntegerType(),ff.makeIntegerType()), ff.makeIntegerType())));
	}
	
	private static List<Literal> noLit = new ArrayList<Literal>();
	
	TestPair[] tests1 = new TestPair[]{
			new TestPair(
					mList("a ∈ S"),
					mList(cProp(0))
			),
			new TestPair(
					mList("a = b"),
					mList(cEqual(a, b))
			),
			new TestPair(
					mList("a ∈ S","a ∈ S"),
					mList(cProp(0))
			),
			new TestPair(
					mList("a ∈ S ∨ d ∈ U"),
					noLit,
					cClause(cProp(0),cProp(1))
			),
			new TestPair(
					mList("¬(a ∈ S) ∨ d ∈ U"),
					noLit,
					cClause(cNotProp(0),cProp(1))
			),
			new TestPair(
					mList("a ∈ S ∨ ¬(d ∈ U)"),
					noLit,
					cClause(cProp(0),cNotProp(1))
			),
			new TestPair(
					mList("¬(a ∈ S ∨ d ∈ U)"),
					mList(cNotProp(0),cNotProp(1))
			),
			new TestPair(
					mList("¬(¬(a ∈ S) ∨ ¬(d ∈ U))"),
					mList(cProp(0),cProp(1))
			),
			new TestPair(
					mList("(a ∈ S ∧ d ∈ U) ∧ d ∈ U"),
					mList(cProp(0),cProp(1))
			),
			
			new TestPair(
					mList("a ∈ S ∧ (d ∈ U ∨ c ∈ P)"),
					mList(cProp(0)),
					cClause(cProp(1),cProp(2))
			),
			new TestPair(
					mList("a ∈ S ∧ d ∈ U"),
					mList(cProp(0),cProp(1))
			),
			new TestPair(
					mList("¬(a ∈ S) ∧ d ∈ U"),
					mList(cNotProp(0),cProp(1))
			),
			new TestPair(
					mList("a ∈ S ∧ ¬(d ∈ U)"),
					mList(cProp(0),cNotProp(1))
			),
			new TestPair(
					mList("¬(a ∈ S ∧ d ∈ U)"),
					noLit,
					cClause(cNotProp(0),cNotProp(1))
			),
			new TestPair(
					mList("¬(¬(a ∈ S) ∧ ¬(d ∈ U))"),
					noLit,
					cClause(cProp(0),cProp(1))
			),
			
			new TestPair(
					mList(	"(a ∈ S ∨ (d ∈ U ∧ c ∈ P)) ∨ a ∈ S"),
					noLit,
					cClause(cProp(0),cProp(0),cProp(1)),
					cClause(cProp(0),cProp(0),cProp(2))
			),
			
			new TestPair(
					mList("(a ∈ S ∨ (d ∈ U ∧ c ∈ P)) ∨ a ∈ S",
						  "a ∈ S ∨ (d ∈ U ∧ c ∈ P)"),
					noLit, 
					cClause(cProp(0),cProp(0),cProp(1)),
					cClause(cProp(0),cProp(0),cProp(2)),
					cClause(cProp(0),cProp(1)),
					cClause(cProp(0),cProp(2))
			),
			new TestPair(
					mList("a ∈ S ⇒ d ∈ U"),
					noLit,
					cClause(cNotProp(0),cProp(1))
			),
			new TestPair(
					mList("a ∈ S ⇒ b ∈ S"),
					noLit,
					cClause(cPred(0, b),cNotPred(0, a))
			),
			new TestPair(
					mList("¬(a ∈ S) ⇒ d ∈ U"),
					noLit,
					cClause(cProp(0),cProp(1))
			),
			new TestPair(
					mList("a ∈ S ⇒ ¬(d ∈ U)"),
					noLit,
					cClause(cNotProp(0),cNotProp(1))
			),
			new TestPair(
					mList("¬(a ∈ S) ⇒ b ∈ S"),
					noLit,
					cClause(cPred(0, a),cPred(0, b))
			),
			new TestPair(
					mList("¬(a ∈ S ⇒ d ∈ U)"),
					mList(cProp(0),cNotProp(1))
			),
			
			new TestPair(
					mList("¬(¬(a ∈ S) ⇒ ¬(d ∈ U))"),
					mList(cNotProp(0),cProp(1))
			),

			new TestPair(
					mList("a ∈ S ∨ a ∈ S"),
					noLit,
					cClause(cProp(0), cProp(0))
			),
			new TestPair(
					mList("a ∈ S ∨ b ∈ SS"),
					noLit,
					cClause(cPred(0, a, S), cPred(0, b, SS))
			),
			new TestPair(
					mList("a ∈ S ∨ b ∈ S"),
					noLit,
					cClause(cPred(0, a), cPred(0, b))
			),
			new TestPair(
					mList("a ∈ S ∨ b ∈ S ∨ c ∈ T"),
					noLit,
					cClause(cPred(0, a), cPred(0, b), cProp(1))
			),
			new TestPair(
					mList("a ∈ S ∨ b ∈ S ∨ c ∈ T ∨ d ∈ T"),
					noLit,
					cClause(cPred(0, a), cPred(0, b), cPred(1, c), cPred(1, d))
			),
			new TestPair(
					mList("¬(¬(a ∈ S) ∧ ¬(b ∈ S))"),
					noLit,
					cClause(cPred(0, a), cPred(0, b))
			),
			
			new TestPair(	
					mList("(a ∈ S ∨ b ∈ S) ∨ (a ∈ S ∨ b ∈ S)"),
					noLit,
					cClause(cPred(0, a), cPred(0, b), cPred(0, a), cPred(0,b))
			),
	};	
		
	TestPair[] tests3 = new TestPair[]{
			new TestPair(
					mList("(a ∈ S ∨ b ∈ S) ∨ (∀x,y·x ∈ S ∨ y ∈ S)"),
					noLit,
					cClause(cPred(0,a), cPred(0,b), cPred(0,xS), cPred(0,yS))
			),
			new TestPair(
					mList("∃x·∀y·∃z·∃w·x ↦ w ∈ T ∧ w = y + z"),
					mList(cPred(4,cELocVar(0,INT))),
//					cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,cPlus(yInt,cELocVar(2,INT)))),
//					cClause(cPred(1,xInt,yInt),cNotPred(0,xInt,cPlus(yInt,zInt))),
					cClause(cNotPred(4,xInt),cPred(3,xInt,yInt)),
					cClause(cNotPred(3,xInt,yInt),cPred(2,xInt,yInt,cELocVar(1,INT))),
					cClause(cNotPred(2,xInt,yInt,zInt),cNotPred(1,xInt,cELocVar(2,INT),yInt,zInt)),
					cClause(cPred(1,xInt,yInt,zInt,wInt),cAEqual(yInt,cPlus(zInt, wInt))),
					cClause(cPred(1,xInt,yInt,zInt,wInt),cPred(0,xInt,yInt))
			),
			new TestPair(
					mList(
							"∀x, y · x ∈ P ∧ y ∈ Q ⇒ (∃z · z ∈ R ∧ (x↦y)↦z ∈ M)",
							"∀x · x ∈ Q ∨ x ∈ R",
							"∃x · x ∈ P ∧ (∀y · y ∈ R ⇒ (∃z · z ∈ Q ∧ (x↦y)↦z ∈ M))",
							"∃x · ∀y · ∃z · (x↦y)↦z ∈ M"),
							mList(cNotPred(12, cELocVar(1,B)),cPred(15, cELocVar(2,B))),
							cClause(cNotPred(0,xB),cNotPred(1,yA,cCons("Q")),cNotPred(4, cELocVar(0,A),cCons("R"),xB,yA)),
							cClause(cPred(1,xA,cCons("Q")),cPred(1,xA,cCons("R"))),
							cClause(cNotPred(4,xA,yPA,zB,tA),cNotPred(1,xA,yPA),cNotPred(3,zB,tA,xA)),
							cClause(cPred(4,xA,yPA,zB,tA),cPred(1,xA,yPA)),
							cClause(cPred(4,xA,yPA,zB,tA),cPred(3,zB,tA,xA)),
							cClause(cNotPred(10,xA,zB),cNotPred(1,xA,cCons("R")),cNotPred(4, cELocVar(5,A),cCons("Q"),zB,xA)),
//							cClause(cPred(10,xA,zB),cPred(1,xA,cCons("R"))),
//							cClause(cPred(10,xA,zB),cPred(4,yA,cCons("Q"),zB,xA)),
//							cClause(cNotPred(12,xB),cNotPred(0,xB),cNotPred(10, cELocVar(1),xB)),
							cClause(cPred(12,xB),cPred(0,xB)),
							cClause(cPred(12,xB),cPred(10,yA,xB)),
							cClause(cNotPred(14,xB,yA),cPred(3,xB,yA,cELocVar(2,A))),
//							cClause(cPred(14,xB,yA),cNotPred(3,xB,yA,zA)),
							cClause(cNotPred(15,xB),cPred(14,xB,yA))
//							cClause(cPred(15,xB),cNotPred(14,xB,cELocVar(1,A)))

			),
			new TestPair(
					mList("∃f\u2982ℙ(A×B)·(∀x,x0,x1·x ↦ x0∈f∧x ↦ x1∈f⇒x0=x1)∧(∀x·∃x0·x ↦ x0∈f)"),
					mList(cNotPred(6,cELocVar(0,PAB))),
					cClause(cNotPred(2,xB,yB,zA,tPAB),cEqual(xB,yB),cNotPred(0,zA,xB,tPAB),cNotPred(0,zA,yB,tPAB)),
//					cClause(cPred(2,xB,yB,zA,tPAB),cPred(0,zA,yB,tPAB)),
//					cClause(cPred(2,xB,yB,zA,tPAB),cPred(0,zA,xB,tPAB)),
//					cClause(cPred(2,xB,yB,zA,tPAB),cNEqual(xB,yB)),
//					cClause(cNotPred(6,tPAB),cPred(4, cELocVar(1,A),tPAB),cNotPred(2, cELocVar(2,B),cELocVar(3,B),cELocVar(4,A),tPAB)),
					cClause(cPred(6,tPAB),cPred(2,xB,yB,zA,tPAB)),
					cClause(cPred(6,tPAB),cNotPred(4,zA,tPAB)),
//					cClause(cNotPred(4,xA,tPAB),cNotPred(0,xA,yB,tPAB)),
					cClause(cPred(4,xA,tPAB),cPred(0,xA,cELocVar(1,B),tPAB))
			),
//			new TestPair(
//					mList("∃x·" +
//					"	(∀x0,x1·x0 ↦ x1∈x⇒" +
//					"		(∀x·" +
//					"			(∀x0,x1·" +
//					"				x0 ↦ x1∈x" +
//					"				⇒" +
//					"					x0=n" +
//					"					∧" +
//					"					(∀x·n ↦ x∈D⇒x1=x+1)" +
//					"			)" +
//					"			∧" +
//					"			(∀x0,x1·" +
//					"				x0=n" +
//					"				∧" +
//					"				(∀x·n ↦ x∈D⇒x1=x+1)" +
//					"					⇒" +
//					"					x0 ↦ x1∈x" +
//					"			)" +
//					"			⇒" +
//					"			(x0 ↦ x1∈D∧" +
//					"				¬(∃x1·x0 ↦ x1∈x)" +
//					"			)" +
//					"			∨" +
//					"			x0 ↦ x1∈x" +
//					"		)" +
//					"	)"),
//					noLit,
//					cClause()
//			),
//			new TestPair(
//					mList("(∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + 1) ∨ (∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + 1)"),
//					noLit,
//					cClause(cPred(1, cELocVar(0,INT)),cPred(1, cELocVar(1,INT)))
////					cClause(cNotPred(1,xInt),cPred(0,xInt, cPlus(yInt,one))),
////					cClause(cPred(1,xInt),cNotPred(0,xInt, cPlus(cELocVar(1,INT),one)))
//			),
//			new TestPair(
//					mList("(∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + n + 1) ∨ (∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + n + 1)"),
//					noLit,
//					cClause(cPred(1, cELocVar(0,INT)),cPred(1, cELocVar(1,INT)))
////					cClause(cNotPred(1,xInt),cPred(0,xInt, cPlus(yInt,n,one))),
////					cClause(cPred(1,xInt),cNotPred(0,xInt, cPlus(cELocVar(1,INT),n,one)))
//			)
	};

	TestPair[] tests2 = new TestPair[]{
		new TestPair(
				mList("∃x·x ∈ P ∨ (a ∈ S ∧ b ∈ U)"),
				mList(cPred(4, cELocVar(0,B))),
				cClause(cNotPred(4,xB),cPred(0,xB),cProp(1)),
				cClause(cNotPred(4,xB),cPred(0,xB),cProp(2))
//				cClause(cPred(4,xB),cNotPred(0,xB)),
//				cClause(cPred(4,xB),cNotProp(1),cNotProp(2))
		),
		
		new TestPair(
				mList("∀x·a ∈ S ∨ x ∈ S ∨ (∀y·x ∈ S ∨ y ∈ U)"),
				noLit,
				cClause(cPred(0, a),cPred(0,xS),cPred(0,xS),cPred(1,yU))
		),
		new TestPair(
				mList("∀x·(a ∈ S ∨ x ∈ S) ∧ (∀y·a ∈ S ∨ y ∈ S)"),
				noLit,
				cClause(cPred(0,a),cPred(0,xS))
		),
		new TestPair(
				mList("∀x·(a ∈ S ∨ x ∈ S) ∨ (∀y·a ∈ S ∨ y ∈ S)"),
				noLit,
				cClause(cPred(0, a),cPred(0,xS),cPred(0, a),cPred(0,yS))
		),
		new TestPair(
				mList("∀x·a ∈ S ∨ x ∈ S ∨ (∀y·a ∈ S ∨ y ∈ U)"),
				noLit,
				cClause(cPred(0, a),cPred(0,xS),cPred(0, a),cPred(1,yU))
		),
		// if the formula is universally quantified, do not labelize
		new TestPair(
				mList("∀x·a ∈ S ∨ b ∈ S ∨ x ∈ S"),
				noLit,
				cClause(cPred(0, a),cPred(0, b),cPred(0,xS))
		),
		new TestPair(
				mList("∀x·x ∈ S ∨ x ∈ S"),
				noLit,
				cClause(cPred(0,xS),cPred(0,xS))
		),
		new TestPair(
				mList("∀x·¬(¬x ∈ S ∨ ¬x ∈ S)"),
				mList(cPred(0,xS))
		),
		new TestPair(
				mList("∀x·x ∈ S ∧ x ∈ S"),
				mList(cPred(0,xS))
		),
//		new TestPair(
//				mList("(∀x·x + 1 ∈ N)", "n + 1 ∈ N"),
//				noLit
////				mList(cPred(0,cPlus(xInt,one)),cPred(0, cPlus(n,one)))
//		),
		new TestPair(
				mList("(∀x·x ∈ S)"),
				mList(cPred(0,xS))
		),
		new TestPair(
				mList("(∃x·x ∈ S)"),
				mList(cPred(0,cELocVar(0,Ssort)))
		),
		new TestPair(
				mList("¬(∀x·x ∈ S)"),
				mList(cNotPred(0,cELocVar(0,Ssort)))
		),
		new TestPair(
				mList("¬(∃x·x ∈ S)"),
				mList(cNotPred(0,xS))
		),
		new TestPair(
				mList("¬(∃x·x ∈ S) ∨ (∃x·x ∈ S)"),
				noLit,
				cClause(cPred(0,cELocVar(0,Ssort)),cNotPred(0,yS))
		),
		new TestPair(
				mList("(∀x·∃y·x ↦ y ∈ T)"),
				mList(cPred(0,xInt,cELocVar(1,INT)))
		),
		new TestPair(
				mList("(∃x·∀y·x ↦ y ∈ T)"),
				mList(cPred(1, cELocVar(0,INT))),
				cClause(cNotPred(1,xInt),cPred(0,xInt,yInt))
		),
		new TestPair(
				mList("(∀x·∃y·∀z·x ↦ y ↦ z ∈ TT)"),
				mList(cPred(1,xInt,cELocVar(1,INT))),
				cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt,zInt))
		),
		new TestPair(
				mList("∃x·∀y·∃z·x ↦ y ↦ z ∈ TT"),
				mList(cPred(2,cELocVar(0,INT))),
				cClause(cNotPred(2,xInt),cPred(1,xInt,yInt)),
				cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt,cELocVar(2,INT)))
		),
		
		new TestPair(
				mList("¬(∃x·∀y·¬(x ↦ y ∈ T))"),
				mList(cPred(0,xInt,cELocVar(1,INT)))
		),
		new TestPair(
				mList("¬(∀x·∃y·x ↦ y ∈ T)"),
				mList(cPred(1,cELocVar(0,INT))),
				cClause(cNotPred(1,xInt),cNotPred(0,xInt,yInt))
		),
		new TestPair(
				mList("¬(∃x·∀y·∃z·(x ↦ y) ↦ z ∈ M)"),
				mList(cPred(1,xB,cELocVar(0,A))),
				cClause(cNotPred(1,xB,yA),cNotPred(0,xB,yA,zA))
		),
		new TestPair(
				mList("(∀x·x ∈ S) ∨ (∀x·x ∈ S)"),
				noLit,
				cClause(cPred(0,xS),cPred(0,yS))
		),
		new TestPair(
				mList("(∃y·∀x·x ↦ y ∈ T) ∨ (∃y·∀x·x ↦ y ∈ T)"),
				noLit,
				cClause(cPred(1,cELocVar(0,INT)),cPred(1,cELocVar(1,INT))),
				cClause(cNotPred(1,xInt),cPred(0,yInt,xInt)),
				cClause(cPred(1,xInt),cNotPred(0,cELocVar(2,INT),xInt))
		),
		
		
//		new TestPair(
//				mList("(∃y·∀x·x ↦ y ∈ T) ∨ (∀z·(∃y·∀x·x ↦ y ∈ T) ∨ z ∈ S)"),
//				noLit
//				
//		),
		
		new TestPair(
				mList("(∀x·x ∈ S ∧ x ∈ SS)"),
				mList(cPred(0,xS,cCons("S")),cPred(0,xS,cCons("SS")))
		),
		
		
		new TestPair(
				// Rubin Exercise 8.A(18), p174
				mList(	"∀x,y,z·x ↦ y ∈ T ∧ y ↦ z ∈ T ⇒ x ↦ z ∈ T",
						"∀x,y·x ↦ y ∈ T ⇒ y ↦ x ∈ T",
						"¬((∀x·∃y·x ↦ y ∈ T) ⇒ (∀x·x ↦ x ∈ T))"),
				mList(	cNotPred(0,evar0,evar0),
						cPred(0,xInt,cELocVar(1,INT))),
						cClause(cPred(0,xInt,yInt),cNotPred(0,xInt,zInt),cNotPred(0,zInt,yInt)),
						cClause(cPred(0,xInt,yInt),cNotPred(0,yInt,xInt))
		)
	};
	
	
	TestPair[] equiClauses = new TestPair[]{
		new TestPair(
				mList("a ∈ S ⇔ a ∈ S"),
				noLit,
				cEqClause(cProp(0),cProp(0))
		),
		new TestPair(
				mList("a ∈ S ⇔ d ∈ U"),
				noLit,
				cEqClause(cProp(0),cProp(1))
		),
		new TestPair(
				mList("a ∈ S ⇔ b ∈ S"),
				noLit,
				cEqClause(cPred(0, a),cPred(0, b))
		),
		new TestPair(
				mList("∀x,y·x ∈ S ⇔ y ∈ S"),
				noLit,
				cEqClause(cPred(0,xS),cPred(0,yS))
		),
		new TestPair(
				mList("a ∈ S ⇔ ¬(a ∈ S)"),
				noLit,
				cEqClause(cNotProp(0),cProp(0))
		),
		new TestPair(
				mList("∀y·y ∈ N ⇔ (∀x·x ↦ y ∈ T)"),
				noLit,
				cEqClause(cPred(0,xInt),cPred(1,cFLocVar(1,INT),xInt))
		),
		new TestPair(
				mList("∀y·(∀x·x ↦ y ∈ T) ⇔ y ∈ N"),
				noLit,
				cEqClause(cPred(2,xInt),cPred(0,cFLocVar(0,INT),xInt))
		),
		new TestPair(
				mList("∀y·y ∈ N ⇔ (∃x·x ↦ y ∈ T)"),
				noLit,
				cEqClause(cPred(0,xInt),cPred(1,cELocVar(1,INT),xInt))
		),
		new TestPair(
				mList("a ∈ S ⇔ ¬(a ∈ S ⇔ a ∈ S)"),
				noLit,
				cEqClause(cNotProp(0),cProp(0),cProp(0))
		),
		new TestPair(
				mList("(a ∈ S ⇔ (d ∈ U ⇔ c ∈ P)) ⇔ ((a ∈ S ⇔ d ∈ U) ⇔ c ∈ P)"),
				noLit,
				cEqClause(cProp(0),cProp(1),cProp(2),cProp(2),cProp(0),cProp(1))
		),
		new TestPair(
				mList("¬(a ∈ S ⇔ (d ∈ U ⇔ c ∈ P))"),
				noLit,
				cEqClause(cNotProp(0),cProp(1),cProp(2))
		),
		new TestPair(
				mList("∃x·x ∈ S ⇔ x ∈ S"),
				mList(cPred(1,cELocVar(0,Ssort))),
				cEqClause(cPred(1,xS),cPred(0,xS),cPred(0,xS))
		),
		new TestPair(
				mList("¬(∀x·¬(x ∈ S ⇔ x ∈ S))"),
				mList(cPred(1,cELocVar(0,Ssort))),
				cEqClause(cPred(1,xS),cPred(0,xS),cPred(0,xS))
		),
		new TestPair(
				mList("¬(∀x·x ∈ S ⇔ x ∈ S)"),
				mList(cNotPred(1,cELocVar(0,Ssort))),
				cEqClause(cPred(1,xS),cPred(0,xS),cPred(0,xS))
		),
		new TestPair(
				mList("¬(∃x·x ∈ S) ⇔ (∃x·x ∈ S)"),
				noLit,
				cEqClause(cPred(0, cELocVar(0,Ssort)),cNotPred(0,cFLocVar(1,Ssort)))
		),
		new TestPair(
				mList("∀y·¬(∃x·x ∈ N) ⇔ (∃x·x ↦ y ∈ T ∨ y ∈ N)"),
				noLit,
				cEqClause(cPred(3,xInt, cELocVar(1,INT)),cNotPred(0,cFLocVar(3,INT))),
				cClause(cNotPred(3,xInt,yInt),cPred(0,xInt),cPred(2,yInt,xInt)),
				cClause(cPred(3,xInt,yInt),cNotPred(0,xInt)),
				cClause(cPred(3,xInt,yInt),cNotPred(2,yInt,xInt))
		),
		
		
		/* MIXED */
		new TestPair(
				mList("a ∈ S ⇔ (a ∈ S ∨ a ∈ S)"),
				noLit,
				cEqClause(cProp(0),cProp(1)),
				cClause(cNotProp(1),cProp(0),cProp(0)),
//				cClause(cProp(1),cNotProp(0)),
				cClause(cProp(1),cNotProp(0))
		),
		new TestPair(
				mList("a ∈ S ∨ (a ∈ S ⇔ a ∈ S)"),
				noLit,
				cClause(cProp(0),cProp(1)),
				cEqClause(cProp(1),cProp(0),cProp(0))
		),
		new TestPair(
				mList("¬(d ∈ U ⇔ c ∈ P) ∨ (d ∈ U ⇔ c ∈ P)"),
				noLit,
				cEqClause(cProp(2),cProp(0),cProp(1)),
				cClause(cNotProp(2),cProp(2))
		),
		new TestPair(
				mList("¬(d ∈ U ∨ c ∈ P) ⇔ (d ∈ U ∨ c ∈ P)"),
				noLit,
				cEqClause(cNotProp(2),cProp(2)),
				cClause(cNotProp(2),cProp(0),cProp(1)),
				cClause(cProp(2),cNotProp(0)),
				cClause(cProp(2),cNotProp(1))
		),
		
		new TestPair(
				mList("a ∈ S ⇔ (d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				noLit,
				cEqClause(cProp(0),cProp(4)),
				cClause(cNotProp(4),cProp(1),cProp(3)),
				cClause(cProp(4),cNotProp(1)),
				cClause(cProp(4),cNotProp(3)),
				cEqClause(cProp(3),cProp(2),cProp(2))
		),
		new TestPair(
				mList("a ∈ S ⇔ (d ∈ U ∨ ¬(c ∈ P ⇔ c ∈ P))"),
				noLit,
				cEqClause(cProp(0),cProp(4)),
				cClause(cNotProp(4),cProp(1),cNotProp(3)),
				cClause(cProp(4),cNotProp(1)),
				cClause(cProp(4),cProp(3)),
				cEqClause(cProp(3),cProp(2),cProp(2))
		),
		new TestPair(
				mList("a ∈ S ⇔ ¬(d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				noLit,
				cEqClause(cNotProp(0),cProp(4)),
				cClause(cNotProp(4),cProp(1),cProp(3)),
				cClause(cProp(4),cNotProp(1)),
				cClause(cProp(4),cNotProp(3)),
				cEqClause(cProp(3),cProp(2),cProp(2))
		),
		new TestPair(
				mList("(d ∈ U ∨ (c ∈ P ⇔ c ∈ P)) ⇔ a ∈ S"),
				noLit,
				cEqClause(cProp(4),cProp(3)),
				cClause(cNotProp(3),cProp(0),cProp(2)),
				cClause(cProp(3),cNotProp(0)),
				cClause(cProp(3),cNotProp(2)),
				cEqClause(cProp(2),cProp(1),cProp(1))
		),
		new TestPair(
				mList(	"(d ∈ U ∨ (c ∈ P ⇔ c ∈ P)) ⇔ a ∈ S",
						"a ∈ S ⇔ (d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				noLit,
				cClause(cNotProp(3),cProp(0),cProp(2)),
				cClause(cProp(3),cNotProp(0)),
				cClause(cProp(3),cNotProp(2)),
				cEqClause(cProp(2),cProp(1),cProp(1)),
				cEqClause(cProp(4),cProp(3))
		),
		
		new TestPair(
				mList("a ∈ S ∧ (d ∈ U ⇔ c ∈ P)"),
				mList(cProp(0),cProp(3)),
				cEqClause(cProp(3),cProp(1),cProp(2))
		),
		new TestPair(
				mList("a ∈ S ⇔ (d ∈ U ∧ c ∈ P)"),
				noLit,
				cEqClause(cNotProp(0),cProp(3)),
				cClause(cNotProp(3),cNotProp(1),cNotProp(2)),
				cClause(cProp(3),cProp(1)),
				cClause(cProp(3),cProp(2))
		),
		
		new TestPair(
				mList("a ∈ S ⇔ b ∈ S","a ∈ S ⇔ c ∈ S"),
				noLit,
				cEqClause(cPred(0,a),cPred(0,b)),
				cEqClause(cPred(0,a),cPred(0,c))
		),
	};
	
	TestPair[] arithmetic = new TestPair[]{
		new TestPair(
				mList("∃x· x = n + 1 ∧ x ∈ N"),
				mList(cNotPred(1,cELocVar(0,INT))),
				cClause(cPred(1,xInt),cPred(0,xInt)),
				cClause(cPred(1,xInt),cAEqual(xInt, cPlus(n,cCons("1"))))
		),
//		new TestPair(
//				mList("n ∗ 1 ∈ N"),
//				mList(cProp(0))
//		),
//		new TestPair(
//				mList("n ÷ 1 ∈ N"),
//				mList(cProp(0))
//		),
//		new TestPair(
//				mList("n ^ 1 ∈ N"),
//				mList(cProp(0))
//		),
//		new TestPair(
//				mList("n mod 1 ∈ N"),
//				mList(cProp(0))
//		),
//		new TestPair(
//				mList("n − 1 ∈ N"),
//				mList(cProp(0))
//		),
//		new TestPair(
//				mList("−n ∈ N"),
//				mList(cProp(0))
//		),
		
//		new TestPair(
//				mList("∀x·x + 1 ∈ N"),
//				noLit
//		),
//		new TestPair(
//				mList("∀x·x ∗ 1 ∈ N"),
//				mList(cPred(0,cTimes(xInt,one)))
//		),
//		new TestPair(
//				mList("∀x·x ÷ 1 ∈ N"),
//				mList(cPred(0,cDiv(xInt,one)))
//		),
//		new TestPair(
//				mList("∀x·x ^ 1 ∈ N"),
//				mList(cPred(0,cExpn(xInt,one)))
//		),
//		new TestPair(
//				mList("∀x·x mod 1 ∈ N"),
//				mList(cPred(0,cMod(xInt,one)))
//		),
//		new TestPair(
//				mList("∀x·x − 1 ∈ N"),
//				mList(cPred(0,cMinus(xInt,one)))
//		),
//		new TestPair(
//				mList("∀x·−x ∈ N"),
//				mList(cPred(0,cUnMin(xInt)))
//		),
		
		new TestPair(
				mList("n = 1"),
				mList(cAEqual(n, one))
		),
		new TestPair(
				mList("n ≠ 1"),
				mList(cANEqual(n, one))
		),
		new TestPair(
				mList("n > 1"),
				mList(cLess(one, n))
		),
		new TestPair(
				mList("n < 1"),
				mList(cLess(n, one))
		),
		new TestPair(
				mList("n ≥ 1"),
				mList(cLE(one, n))
		),
		new TestPair(
				mList("n ≤ 1"),
				mList(cLE(n, one))
		),
		
		new TestPair(
				mList("¬(n = 1)"),
				mList(cANEqual(n, one))
		),
		new TestPair(
				mList("¬(n ≠ 1)"),
				mList(cAEqual(n, one))
		),
		new TestPair(
				mList("¬(n > 1)"),
				mList(cLE(n, one))
		),
		new TestPair(
				mList("¬(n < 1)"),
				mList(cLE(one, n))
		),
		new TestPair(
				mList("¬(n ≥ 1)"),
				mList(cLess(n, one))
		),
		new TestPair(
				mList("¬(n ≤ 1)"),
				mList(cLess(one, n))
		),
		
		/* ***********/
		new TestPair(
				mList("∀x·x = 1"),
				mList(cAEqual(xInt,one))
		),
		new TestPair(
				mList("∀x·x ≠ 1"),
				mList(cANEqual(xInt,one))
		),
		new TestPair(
				mList("∀x·x > 1"),
				mList(cLess(one,xInt))
		),
		new TestPair(
				mList("∀x·x < 1"),
				mList(cLess(xInt,one))
		),
		new TestPair(
				mList("∀x·x ≥ 1"),
				mList(cLE(one,xInt))
		),
		new TestPair(
				mList("∀x·x ≤ 1"),
				mList(cLE(xInt,one))
		),
		
		new TestPair(
				mList("∀x·¬(x = 1)"),
				mList(cANEqual(xInt,one))
		),
		new TestPair(
				mList("∀x·¬(x ≠ 1)"),
				mList(cAEqual(xInt,one))
		),
		new TestPair(
				mList("∀x·¬(x > 1)"),
				mList(cLE(xInt,one))
		),
		new TestPair(
				mList("∀x·¬(x < 1)"),
				mList(cLE(one,xInt))
		),
		new TestPair(
				mList("∀x·¬(x ≥ 1)"),
				mList(cLess(xInt,one))
		),
		new TestPair(
				mList("∀x·¬(x ≤ 1)"),
				mList(cLess(one,xInt))
		),
		
		/* ******************/
		new TestPair(
				mList("∃x·x = 1"),
				mList(cAEqual(cELocVar(0,INT), one))
		),
		new TestPair(
				mList("∃x·x ≠ 1"),
				mList(cANEqual(cELocVar(0,INT), one))
		),
		new TestPair(
				mList("∃x·x > 1"),
				mList(cLess(one, cELocVar(0,INT)))
		),
		new TestPair(
				mList("∃x·x < 1"),
				mList(cLess(cELocVar(0,INT), one))
		),
		new TestPair(
				mList("∃x·x ≥ 1"),
				mList(cLE(one, cELocVar(0,INT)))
		),
		new TestPair(
				mList("∃x·x ≤ 1"),
				mList(cLE(cELocVar(0,INT), one))
		),
		
		new TestPair(
				mList("¬(∀x·x = 1)"),
				mList(cANEqual(cELocVar(0,INT), one))
		),
		new TestPair(
				mList("¬(∀x·x ≠ 1)"),
				mList(cAEqual(cELocVar(0,INT), one))
		),
		new TestPair(
				mList("¬(∀x·x > 1)"),
				mList(cLE(cELocVar(0,INT),one))
		),
		new TestPair(
				mList("¬(∀x·x < 1)"),
				mList(cLE(one,cELocVar(0,INT)))
		),
		new TestPair(
				mList("¬(∀x·x ≥ 1)"),
				mList(cLess(cELocVar(0,INT),one))
		),
		new TestPair(
				mList("¬(∀x·x ≤ 1)"),
				mList(cLess(one,cELocVar(0,INT)))
		),
		/* ******************/
		
		new TestPair(
				mList("∀x·x > 1 ∨ x ≤ 1"),
				noLit,
				cClause(cLess(one,xInt),cLE(xInt,one))
		),
		new TestPair(
				mList("∃x·x > 1 ∨ x ≤ 1"),
				mList(cPred(0,cELocVar(0,INT))),
				cClause(cNotPred(0,xInt),cLess(one,xInt),cLE(xInt,one))
//				cClause(cPred(0,xInt),cLE(xInt,one)),
//				cClause(cPred(0,xInt),cLess(one,xInt))
		),
		
		new TestPair(
				mList("n + m + 1 = 0"),
				mList(cAEqual(cPlus(n,m,one), zero))
		),
		new TestPair(
				mList("(n + m) ∗ 1 = 0"),
				mList(cAEqual(cTimes(cPlus(n,m),one), zero))
		),
		new TestPair(
				mList("n ∗ m + 1 = 0"),
				mList(cAEqual(cPlus(cTimes(n,m),one), zero))
		),
		new TestPair(
				mList("n ∗ m ∗ 1 = 0"),
				mList(cAEqual(cTimes(n,m,one), zero))
		),
		new TestPair(
				mList("(n − m) ∗ 1 = 0"),
				mList(cAEqual(cTimes(cMinus(n,m),one), zero))
		),
		new TestPair(
				mList("n ∗ m − 1 = 0"),
				mList(cAEqual(cMinus(cTimes(n,m),one), zero))
		),
		new TestPair(
				mList("(n − m) − 1 = 0"),
				mList(cAEqual(cMinus(cMinus(n,m),one), zero))
		),
		new TestPair(
				mList("(n − m) ^ 1 = 0"),
				mList(cAEqual(cExpn(cMinus(n,m),one), zero))
		),
		new TestPair(
				mList("(n ^ m) − 1 = 0"),
				mList(cAEqual(cMinus(cExpn(n,m),one), zero))
		),
		new TestPair(
				mList("(n + m) ^ 1 = 0"),
				mList(cAEqual(cExpn(cPlus(n,m),one), zero))
		),
		new TestPair(
				mList("(n ^ m) + 1 = 0"),
				mList(cAEqual(cPlus(cExpn(n,m),one), zero))
		),
		new TestPair(
				mList("(n mod m) ^ 1 = 0"),
				mList(cAEqual(cExpn(cMod(n,m),one), zero))
		),
		new TestPair(
				mList("(n ^ m) mod 1 = 0"),
				mList(cAEqual(cMod(cExpn(n,m),one), zero))
		),
		new TestPair(
				mList("(n + m) mod 1 = 0"),
				mList(cAEqual(cMod(cPlus(n,m),one), zero))
		),
		new TestPair(
				mList("(n mod m) + 1 = 0"),
				mList(cAEqual(cPlus(cMod(n,m),one), zero))
		),
		new TestPair(
				mList("(n − m) mod 1 = 0"),
				mList(cAEqual(cMod(cMinus(n,m),one), zero))
		),
		new TestPair(
				mList("(n mod m) − 1 = 0"),
				mList(cAEqual(cMinus(cMod(n,m),one), zero))
		),
		new TestPair(
				mList("(n − m) ÷ 1 = 0"),
				mList(cAEqual(cDiv(cMinus(n,m),one), zero))
		),
		new TestPair(
				mList("(n ÷ m) − 1 = 0"),
				mList(cAEqual(cMinus(cDiv(n,m),one), zero))
		),
		new TestPair(
				mList("(n ÷ m) mod 1 = 0"),
				mList(cAEqual(cMod(cDiv(n,m),one), zero))
		),
		new TestPair(
				mList("(n mod m) ÷ 1 = 0"),
				mList(cAEqual(cDiv(cMod(n,m),one), zero))
		),
		new TestPair(
				mList("(n ÷ m) ∗ 1 = 0"),
				mList(cAEqual(cTimes(cDiv(n,m),one), zero))
		),
		new TestPair(
				mList("(n ∗ m) ÷ 1 = 0"),
				mList(cAEqual(cDiv(cTimes(n,m),one), zero))
		),
		new TestPair(
				mList("(n ÷ m) + 1 = 0"),
				mList(cAEqual(cPlus(cDiv(n,m),one), zero))
		),
		new TestPair(
				mList("(n + m) ÷ 1 = 0"),
				mList(cAEqual(cDiv(cPlus(n,m),one), zero))
		),
		new TestPair(
				mList("(n ÷ m) ^ 1 = 0"),
				mList(cAEqual(cExpn(cDiv(n,m),one), zero))
		),
		new TestPair(
				mList("(n ^ m) ÷ 1 = 0"),
				mList(cAEqual(cDiv(cExpn(n,m),one), zero))
		),
		
		new TestPair(
				mList("(n − m) − 1 = 0"),
				mList(cAEqual(cMinus(cMinus(n,m),one), zero))
		),
		new TestPair(
				mList("(n + m) + 1 = 0"),
				mList(cAEqual(cPlus(cPlus(n,m),one), zero))
		),
		new TestPair(
				mList("(n ÷ m) ÷ 1 = 0"),
				mList(cAEqual(cDiv(cDiv(n,m),one), zero))
		),
		new TestPair(
				mList("(n ^ m) ^ 1 = 0"),
				mList(cAEqual(cExpn(cExpn(n,m),one), zero))
		),
		new TestPair(
				mList("(n mod m) mod 1 = 0"),
				mList(cAEqual(cMod(cMod(n,m),one), zero))
		),
		new TestPair(
				mList("(n ∗ m) ∗ 1 = 0"),
				mList(cAEqual(cTimes(cTimes(n,m),one), zero))
		),
		
	};
	
	TestPair[] testsBool = new TestPair[]{
			new TestPair(
					mList("b = TRUE"),
					mList(cProp(0))
			),
			new TestPair(
					mList("b = FALSE"),
					mList(cNotProp(0))
			),
			new TestPair(
					mList("b = TRUE","b = TRUE"),
					mList(cProp(0))
			),
			new TestPair(
					mList("b = TRUE","b = FALSE"),
					mList(cProp(0),cNotProp(0))
			),
			new TestPair(
					mList("b = TRUE","c = TRUE"),
					mList(cProp(0),cProp(1))
			),
			new TestPair(
					mList("b = TRUE","c = b"),
					mList(	cEqual(b, TRUE),
							cEqual(c, b))
			),
			new TestPair(
					mList("b = TRUE","c = e"),
					mList(	cProp(0),
							cEqual(c, e))
			),
			
			new TestPair(
					mList("A=TRUE ⇒ B=TRUE","A=TRUE ⇒ C=TRUE"),
					noLit,
					cClause(cProp(1),cNotProp(2)),
					cClause(cProp(3),cNotProp(2))
			),
//			new TestPair(
//					mList("(A=TRUE∨B=TRUE⇒C=TRUE)⇔(A=TRUE⇒C=TRUE)∧(B=TRUE⇒C=TRUE)"),
//					noLit
//					
//			),
			new TestPair(
					mList("A = TRUE ∨ a ∈ S","B = TRUE ∨ b ∈ S"),
					noLit,
					cClause(cPred(0,a),cProp(2)),
					cClause(cPred(0,b),cProp(3))
			),
			new TestPair(
					mList("A = TRUE ∨ a ∈ S","B = TRUE ∨ b ∈ S","A = B"),
					mList(	cEqual(cCons("A"),cCons("B"))),
					cClause(cPred(0,a),cEqual(cCons("A"),TRUE)),
					cClause(cPred(0,b),cEqual(cCons("B"),TRUE))
			),
	};
	
	TestPair[] testsGoal = new TestPair[]{
		new TestPair(
				mList("∀x,y·x ↦ y ∈ V ∨ (∀z·(x ↦ y) ↦ z ∈ VV) ∨ (∀z·(x ↦ y) ↦ z ∈ VV)"),
				mList(cNotPred(3, cELocVar(0,A),cELocVar(1,B))),
//				cClause(cNotPred(3, xA, yB),cPred(0,xA,yB),cPred(1,xA,yB,zC),cPred(1,xA,yB,tC)),
				cClause(cPred(3, xA, yB),cNotPred(0,xA,yB)),
				cClause(cPred(3, xA, yB),cNotPred(1,xA,yB,cELocVar(2,C)))
//				cClause(cPred(3, xA, yB),cNotPred(1,xA,yB,cELocVar(3,C)))
		),
		new TestPair(
				mList("∀x,y·x ↦ y ∈ T ⇒ y ↦ x ∈ T"),
				mList(cNotPred(1,cELocVar(0,INT),cELocVar(1,INT))),
//				cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt),cNotPred(0,yInt,xInt)),
				cClause(cPred(1,xInt,yInt),cNotPred(0,xInt,yInt)),
				cClause(cPred(1,xInt,yInt),cPred(0,yInt,xInt))
		),
		new TestPair(
				mList(
						"(∀x·∃y·x ↦ y ∈ T) ⇒ (∀x·x ↦ x ∈ T)"),
				mList(	cNotPred(0,evar0,evar0),
						cPred(0,xInt,cELocVar(1,INT)))
		),
		new TestPair(
				mList(
						"((∀x·x∈R)⇒(∃y·¬(y∈Q)))⇒((∀x·x∈Q)⇒(∃y·¬(y∈R)))"
				),
				mList(cPred(0, xA, cCons("Q")),cPred(0, xA, cCons("R"))),
				cClause(cNotPred(0, cELocVar(0,A), cCons("R")),cNotPred(0, cELocVar(1,A), cCons("Q")))
		),
	};
	
	
	TestPair[] testVariableTable = new TestPair[]{
			new TestPair(
					mList("(∀x·x ∈ Q) ∧ (∀x·x ∈ P)"),
					mList(cPred(0,xA),cPred(2,xB))
			),
			new TestPair(
					mList("∀x·x ∈ Q ∨ (∃y·x ↦ y ∈ V ∨ x ↦ y ∈ W) ∨ (∃y·x ↦ y ∈ X ∨ x ↦ y ∈ Y)"),
					noLit,
					cClause(cNotPred(5, xA, yC), cPred(4, xA, yC, cCons("X")), cPred(4,xA,yC,cCons("Y"))),
//					cClause(cPred(5, xA, yC), cNotPred(4, xA, yC, cCons("X"))),
//					cClause(cPred(5, xA, yC), cNotPred(4, xA, yC, cCons("Y"))),
					
					cClause(cNotPred(2, xA, yB), cPred(1, xA, yB, cCons("V")), cPred(1,xA,yB,cCons("W"))),
//					cClause(cPred(2, xA, yB), cNotPred(1, xA, yB, cCons("V"))),
//					cClause(cPred(2, xA, yB), cNotPred(1, xA, yB, cCons("W"))),
					
					cClause(cPred(0,xA),cPred(5,xA,cELocVar(0,C)),cPred(2,xA,cELocVar(1,B)))
			),
	};
	
	// TODO move this test
	TestPair[] testTypeClauseGeneration = new TestPair[] {
//			new TestPair(
//					mList("b ∈ S", "a ∈ C"),
//					noLit
//			),
//			new TestPair(
//					mList("S ∈ O", "∃x·(∀x0·x0∈x)∧x∈O"),
//					noLit
//			),
//			new TestPair(
//					mList("a ∈ S", "b ∈ S"),
//					noLit
//			),
	};
	
	public void doTest(List<String> strPredicate, Collection<? extends Literal> literals, Collection<Clause> clauses, boolean goal) {
		LoaderResult result = load(strPredicate, goal);
		List<Clause> allClauses = new ArrayList<Clause>();
		allClauses.addAll(clauses);
		for (Literal lit : literals) {
			allClauses.add(cClause(lit));
		}
		
		assertSameClauses(strPredicate, allClauses, result.getClauses());
		assertEquals("Actual: "+result.getClauses()+"\nExpected: "+allClauses,result.getClauses().size(), allClauses.size());
//		assertSameClauses(strPredicate, literals, result.getLiterals());
	}
	
	private LoaderResult load(List<String> strPredicate, boolean goal) {
//		PredicateBuilder.DEBUG = true;
//		ClauseBuilder.DEBUG = true;
		
		PredicateBuilder builder = new PredicateBuilder();
		ClauseBuilder cBuilder = new ClauseBuilder();
		ITypeEnvironment tmp = env.clone();
		
		for (String str : strPredicate) {
			ITypeCheckResult res = getResult(str, builder, tmp, goal);
			tmp.addAll(res.getInferredEnvironment());
		}
		cBuilder.buildClauses(builder.getContext());
		LoaderResult result = cBuilder.getResult();
		return result;
	}
	
	private <T> void assertSameClauses(List<String> predicate, Collection<T> expected, Collection<T> actual) {
		StringBuilder message = new StringBuilder();
		for (T clause : actual) {
			if (!expected.contains(clause)){
				message.append("Missing clause : "+clause.toString()+"\n");
			}
		}
		for (T clause : expected) {
			if (!actual.contains(clause)){
				message.append("Superfluous clause : "+clause.toString()+"\n");
			}
		}
		assertTrue("\n"+predicate+"\n"+message.toString(), message.length()==0);
	}
	
	private ITypeCheckResult getResult(String strPredicate, PredicateBuilder builder, ITypeEnvironment types, boolean goal) {
		Predicate predicate = Util.parsePredicate(strPredicate);
		ITypeCheckResult result = predicate.typeCheck(types);
		assertTrue(result+"",result.isSuccess());
		builder.build(predicate,goal);
		return result;
	}
	
	public void testEquivalence() {
		for (TestPair test : equiClauses) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);	
		}
	}
	
	public void testLocalVariable() {
		for (TestPair test : testVariableTable) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);
		}
	}

	public void testClauseBuilder() {
		for (TestPair test : tests1) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);
		}
	}
	
	public void testQuantifiers() {
		for (TestPair test : tests2) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);
		}
	}
	
	public void testComplex() {
		for (TestPair test : tests3) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);
		}
	}
	
	public void testArithmetic() {
		for (TestPair test : arithmetic) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);
		}
	}
	
	public void testBoolean() {
		for (TestPair test : testsBool) {
			doTest(test.predicate, test.unitClauses, test.clauses, false);
		}
	}
	
	public void testGoal() {
		for (TestPair test : testsGoal) {
			doTest(test.predicate, test.unitClauses, test.clauses, true);
		}
	}
	
	public void testTypeClauseGeneration() {
		for (TestPair test : testTypeClauseGeneration) {
			doTest(test.predicate, test.unitClauses, test.clauses, true);
		}
	}
	
	public void testNotSameVariable() {
		LoaderResult result = load(mList("(∀x·x ∈ S ∨ x ∈ S) ⇒ (∀x·x ∈ S ∨ x ∈ S )"),false);
		for (Clause clause1 : result.getClauses()) {
			for (Clause clause2 : result.getClauses()) {
				if (clause1 == clause2) continue;
				for (PredicateLiteral predicate1 : clause1.getPredicateLiterals()) {
					for (PredicateLiteral predicate2 : clause2.getPredicateLiterals()) {
						assertNotSame(""+clause1+", "+clause2,predicate1,predicate2);
					}
				}
			}
		}
	}

}