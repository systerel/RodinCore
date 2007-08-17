package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.cAEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cANEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cCons;
import static org.eventb.internal.pp.core.elements.terms.Util.cDiv;
import static org.eventb.internal.pp.core.elements.terms.Util.cELocVar;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cExpn;
import static org.eventb.internal.pp.core.elements.terms.Util.cFLocVar;
import static org.eventb.internal.pp.core.elements.terms.Util.cIntCons;
import static org.eventb.internal.pp.core.elements.terms.Util.cLE;
import static org.eventb.internal.pp.core.elements.terms.Util.cLess;
import static org.eventb.internal.pp.core.elements.terms.Util.cMinus;
import static org.eventb.internal.pp.core.elements.terms.Util.cMod;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cNotProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cPlus;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.cProp;
import static org.eventb.internal.pp.core.elements.terms.Util.cTimes;
import static org.eventb.internal.pp.core.elements.terms.Util.cVar;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.IntegerConstant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.predicate.PredicateBuilder;

// TODO test negation of the goal
public class TestClauseBuilder extends AbstractPPTest {
	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	class TestPair {
		List<String> predicate;
		Collection<Clause> clauses;
		Object[] constants;
		
		TestPair (List<String> predicate, List<Clause> clauses, Object... constants) {
			this.predicate = predicate;
			this.clauses = clauses;
			this.constants = constants;
		}
	}
	
	private static Sort Asort = AbstractPPTest.A;
	private static Sort Bsort = AbstractPPTest.B;
	private static Sort Csort = AbstractPPTest.C;
	private static Sort Ssort = AbstractPPTest.S;
	private static Sort Usort = AbstractPPTest.U;
	
	private static IntegerConstant zero = cIntCons(0);
	private static IntegerConstant one = cIntCons(1); 
//	private static IntegerConstant two = cIntCons(2);
	
	private static Constant aS = cCons("a", Ssort);
	private static Constant n = cCons("n",NAT);
	private static Constant m = cCons("m",Asort);
	private static Constant S = cCons("S",Ssort);
	private static Constant SS = cCons("SS",Ssort);
	private static Constant TRUE = cCons("TRUE",BOOL);
	private static Constant A = cCons("A",PS);
	private static Constant B = cCons("B",PS);
	private static Constant C = cCons("C",PS);
	private static Constant Q = cCons("Q",PA);
	private static Constant R = cCons("R",PA);
	private static Constant Abool = cCons("A",BOOL);
	private static Constant Bbool = cCons("B",BOOL);
	private static Constant bbool = cCons("b",BOOL);
	private static Constant cbool = cCons("c",BOOL);
	
	private static LocalVariable evar0N = cELocVar(0, NAT);
	
	private static Variable wInt = cVar(0,NAT);
	private static Variable xInt = cVar(1,NAT);
	private static Variable yInt = cVar(2,NAT);
	private static Variable zInt = cVar(3,NAT);
	private static Variable zA = cVar(4,Asort);
	private static Variable zB = cVar(5,Bsort);
	private static Variable zC = cVar(6,Csort);
	private static Variable tA = cVar(7,Asort);
	private static Variable tC = cVar(8,Csort);
	private static Variable tPAB = cVar(9,PAB); 
	private static Variable xA = cVar(10,Asort);
	private static Variable yB = cVar(11,Bsort);
	private static Variable yS = cVar(12,Ssort);
	private static Variable yA = cVar(13,Asort);
	private static Variable yPA = cVar(14,PA);
	private static Variable xB = cVar(15,Bsort);
	private static Variable yC = cVar(16,Csort);
	private static Variable yU = cVar(17,Usort);
	private static Variable xS = cVar(18,Ssort);
	
	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("x0", ff.makeGivenType("A"));
		env.addName("x1", ff.makeGivenType("B"));
		env.addName("a", ff.makeGivenType("S"));
		
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
	
	public void testSimple() {
		Object[] constants1 = new Object[]{
				"a",a,
				"b",b,
				"c",c,
				"d",d,
				"S",S,
				"SS",SS,
		};
		doTestP(
				mList("a ∈ S"),
				mList(cClause(cProp(0))),
				constants1
		);
		doTestP(
				mList("a = b"),
				mList(cClause(cEqual(a, b))),
				constants1
		);
		doTestP(
				mList("a ∈ S","a ∈ S"),
				mList(cClause(cProp(0)),cClause(cProp(0))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∨ d ∈ U"),
				mList(
						cClause(cProp(0),cProp(1))),
				constants1
		);
		doTestP(
				mList("¬(a ∈ S) ∨ d ∈ U"),
				mList(
						cClause(cNotProp(0),cProp(1))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∨ ¬(d ∈ U)"),
				mList(
						cClause(cProp(0),cNotProp(1))),
						constants1
		);
		doTestP(
				mList("¬(a ∈ S ∨ d ∈ U)"),
				mList(cClause(cNotProp(0)),cClause(cNotProp(1))),
				constants1
		);
		doTestP(
				mList("¬(¬(a ∈ S) ∨ ¬(d ∈ U))"),
				mList(cClause(cProp(0)),cClause(cProp(1))),
				constants1
		);
		doTestP(
				mList("(a ∈ S ∧ d ∈ U) ∧ d ∈ U"),
				mList(cClause(cProp(0)),cClause(cProp(1)),cClause(cProp(1))),
				constants1
		);

		doTestP(
				mList("a ∈ S ∧ (d ∈ U ∨ c ∈ P)"),
				mList(cClause(cProp(0)),
						cClause(cProp(1),cProp(2))),
						constants1
		);
		doTestP(
				mList("a ∈ S ∧ d ∈ U"),
				mList(cClause(cProp(0)),cClause(cProp(1))),
				constants1
		);
		doTestP(
				mList("¬(a ∈ S) ∧ d ∈ U"),
				mList(cClause(cNotProp(0)),cClause(cProp(1))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∧ ¬(d ∈ U)"),
				mList(cClause(cProp(0)),cClause(cNotProp(1))),
				constants1
		);
		doTestP(
				mList("¬(a ∈ S ∧ d ∈ U)"),
				mList(
						cClause(cNotProp(0),cNotProp(1))),
						constants1
		);
		doTestP(
				mList("¬(¬(a ∈ S) ∧ ¬(d ∈ U))"),
				mList(
						cClause(cProp(0),cProp(1))),
						constants1
		);

		doTestP(
				mList(	"(a ∈ S ∨ (d ∈ U ∧ c ∈ P)) ∨ a ∈ S"),
				mList(
						cClause(cProp(0),cProp(0),cProp(1)),
						cClause(cProp(0),cProp(0),cProp(2))),
						constants1
		);

		doTestP(
				mList("(a ∈ S ∨ (d ∈ U ∧ c ∈ P)) ∨ a ∈ S",
				"a ∈ S ∨ (d ∈ U ∧ c ∈ P)"),
				mList(
						cClause(cProp(0),cProp(0),cProp(1)),
						cClause(cProp(0),cProp(0),cProp(2)),
						cClause(cProp(0),cProp(1)),
						cClause(cProp(0),cProp(2))),
						constants1
		);
		doTestP(
				mList("a ∈ S ⇒ d ∈ U"),
				mList(
						cClause(cNotProp(0),cProp(1))),
						constants1
		);
		doTestP(
				mList("a ∈ S ⇒ b ∈ S"),
				mList(
						cClause(cPred(0, b),cNotPred(0, a))),
						constants1
		);
		doTestP(
				mList("¬(a ∈ S) ⇒ d ∈ U"),
				mList(
						cClause(cProp(0),cProp(1))),
						constants1
		);
		doTestP(
				mList("a ∈ S ⇒ ¬(d ∈ U)"),
				mList(
						cClause(cNotProp(0),cNotProp(1))),
						constants1
		);
		doTestP(
				mList("¬(a ∈ S) ⇒ b ∈ S"),
				mList(
						cClause(cPred(0, a),cPred(0, b))),
						constants1
		);
		doTestP(
				mList("¬(a ∈ S ⇒ d ∈ U)"),
				mList(cClause(cProp(0)),cClause(cNotProp(1))),
				constants1
		);

		doTestP(
				mList("¬(¬(a ∈ S) ⇒ ¬(d ∈ U))"),
				mList(cClause(cNotProp(0)),cClause(cProp(1))),
				constants1
		);

		doTestP(
				mList("a ∈ S ∨ a ∈ S"),
				mList(
						cClause(cProp(0), cProp(0))),
						constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ SS"),
				mList(
						cClause(cPred(0, a, S), cPred(0, b, SS))),
						constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ S"),
				mList(
						cClause(cPred(0, a), cPred(0, b))),
						constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ S ∨ c ∈ T"),
				mList(
						cClause(cPred(0, a), cPred(0, b), cProp(1))),
						constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ S ∨ c ∈ T ∨ d ∈ T"),
				mList(
						cClause(cPred(0, a), cPred(0, b), cPred(1, c), cPred(1, d))),
						constants1
		);
		doTestP(
				mList("¬(¬(a ∈ S) ∧ ¬(b ∈ S))"),
				mList(
						cClause(cPred(0, a), cPred(0, b))),
						constants1
		);

		doTestP(	
				mList("(a ∈ S ∨ b ∈ S) ∨ (a ∈ S ∨ b ∈ S)"),
				mList(
						cClause(cPred(0, a), cPred(0, b), cPred(0, a), cPred(0,b))),
						constants1
		);

		doTestP(
				mList("∀x·x ∈ S ∨ ¬(x ∈ A ⇔ ¬(x ∈ B ∨ x ∈ C))"),
				mList(
						cClause(cPred(0,xS,S), cNotPred(2,xS)),
						cClause(cNotPred(1,xS), cPred(0,xS,B), cPred(0,xS,C)),
						cClause(cPred(1,xS), cNotPred(0,xS,B)),
						cClause(cPred(1,xS), cNotPred(0,xS,C)),
						cEqClause(cPred(2,xS), cNotPred(0,xS,A), cPred(1,xS))),
				"A", A,
				"B", B,
				"C", C,
				"S", S
		);
	}

	public void testComplex() {
		Object[] constants = new Object[]{
				"a",a,
				"b",b,
				"c",c,
				"d",d,
		};
		doTestP(
				mList("(a ∈ S ∨ b ∈ S) ∨ (∀x,y·x ∈ S ∨ y ∈ S)"),
				mList(
						cClause(cPred(0,a), cPred(0,b), cPred(0,xS), cPred(0,yS))),
				constants
		);
		doTestP(
				mList("∃x·∀y·∃z·∃w·x ↦ w ∈ T ∧ w = y + z"),
				mList(cClause(cPred(4,cELocVar(0,NAT))),
						cClause(cNotPred(4,xInt),cPred(3,xInt,yInt)),
						cClause(cNotPred(3,xInt,yInt),cPred(2,xInt,yInt,cELocVar(1,NAT))),
						cClause(cNotPred(2,xInt,yInt,zInt),cNotPred(1,xInt,cELocVar(2,NAT),yInt,zInt)),
						cClause(cPred(1,xInt,yInt,zInt,wInt),cAEqual(yInt,cPlus(zInt, wInt))),
						cClause(cPred(1,xInt,yInt,zInt,wInt),cPred(0,xInt,yInt)),

						cClause(cNotPred(1,xInt,yInt,zInt,wInt),cANEqual(yInt,cPlus(zInt, wInt)),cNotPred(0,xInt,yInt)))
		);
		doTestP(
				mList("∃x·x ∈ P ∧ (∃y·y ∈ Q ∨ y ∈ R)"),
				mList(cClause(cNotPred(4,cELocVar(1,Bsort))),
						cClause(cPred(4,xB),cPred(0,xB)),
						cClause(cPred(4,xB),cPred(2,cELocVar(3,Asort))),
						cClause(cNotPred(2,xA),cPred(1,xA,Q),cPred(1,xA,R)),

						cClause(cNotPred(4,xB),cNotPred(0,xB),cNotPred(2,yA)),
						cClause(cPred(2,xA),cNotPred(1,xA,R)),
						cClause(cPred(2,xA),cNotPred(1,xA,Q))),
				"Q", Q,
				"R", R
		);
		doTestP(
				mList(
						"∀x, y · x ∈ P ∧ y ∈ Q ⇒ (∃z · z ∈ R ∧ (x↦y)↦z ∈ M)",
						"∀x · x ∈ Q ∨ x ∈ R",
						"∃x · x ∈ P ∧ (∀y · y ∈ R ⇒ (∃z · z ∈ Q ∧ (x↦y)↦z ∈ M))",
				"∃x · ∀y · ∃z · (x↦y)↦z ∈ M"),
				mList(/* 3 */cClause(cNotPred(12, cELocVar(1,Bsort))), /* 4 */cClause(cPred(15, cELocVar(2,Bsort))),
						// 1 ok
						cClause(cNotPred(0,xB),cNotPred(1,yA,Q),cNotPred(4, cELocVar(0,Asort),R,xB,yA)),
						cClause(cPred(4,xA,yPA,zB,tA),cPred(1,xA,yPA)),
						cClause(cPred(4,xA,yPA,zB,tA),cPred(3,zB,tA,xA)),
						// 2 ok
						cClause(cPred(1,xA,Q),cPred(1,xA,R)),
						// 3 ok
						cClause(cPred(12,xB),cNotPred(1,xA,R),cNotPred(4, cELocVar(9,Asort),Q,xB,xA)),
						cClause(cPred(12,xB),cPred(0,xB)),
						// 4 ok
						cClause(cNotPred(14,xB,yA),cPred(3,xB,yA,cELocVar(2,Asort))),
						cClause(cNotPred(15,xB),cPred(14,xB,yA)),
						// unneeded definitions
						cClause(cNotPred(4,xA,yPA,zB,tA),cNotPred(1,xA,yPA),cNotPred(3,zB,tA,xA)),
						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(1,xA,R)),
						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(4,yA,Q,xB,xA)),
						cClause(cNotPred(4,xA,yPA,zB,tA),cNotPred(1,xA,yPA),cNotPred(3,zB,tA,xA)),
						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(1,xA,R)),
						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(4,yA,Q,xB,xA))),
						"Q", Q,
						"R", R
		);
		doTestP(
				mList("∃f\u2982ℙ(A×B)·(∀x,x0,x1·x ↦ x0∈f∧x ↦ x1∈f⇒x0=x1)∧(∀x·∃x0·x ↦ x0∈f)"),
				mList(cClause(cNotPred(6,cELocVar(0,PAB))),
						cClause(cPred(6,tPAB),cEqual(xB,yB),cNotPred(0,zA,xB,tPAB),cNotPred(0,zA,yB,tPAB)),
						cClause(cPred(6,tPAB),cPred(0,xA,cELocVar(1,Bsort),tPAB)),

						cClause(cNotPred(6,tPAB),cNotPred(0,xA,yB,tPAB),cNEqual(xB,zB)),
						cClause(cNotPred(6,tPAB),cNotPred(0,xA,yB,tPAB),cPred(0,zA,xB,tPAB)),
						cClause(cNotPred(6,tPAB),cNotPred(0,xA,yB,tPAB),cPred(0,zA,xB,tPAB)))
		);
//		doTestP(
//		mList("∃x·" +
//		"	(∀x0,x1·x0 ↦ x1∈x⇒" +
//		"		(∀x·" +
//		"			(∀x0,x1·" +
//		"				x0 ↦ x1∈x" +
//		"				⇒" +
//		"					x0=n" +
//		"					∧" +
//		"					(∀x·n ↦ x∈D⇒x1=x+1)" +
//		"			)" +
//		"			∧" +
//		"			(∀x0,x1·" +
//		"				x0=n" +
//		"				∧" +
//		"				(∀x·n ↦ x∈D⇒x1=x+1)" +
//		"					⇒" +
//		"					x0 ↦ x1∈x" +
//		"			)" +
//		"			⇒" +
//		"			(x0 ↦ x1∈D∧" +
//		"				¬(∃x1·x0 ↦ x1∈x)" +
//		"			)" +
//		"			∨" +
//		"			x0 ↦ x1∈x" +
//		"		)" +
//		"	)"),
//		noLit
//		);
//		doTestP(
//		mList("(∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + 1) ∨ (∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + 1)"),
//		noLit,
//		cClause(cPred(1, cELocVar(0,INT)),cPred(1, cELocVar(1,INT)))
////		cClause(cNotPred(1,xInt),cPred(0,xInt, cPlus(yInt,one))),
////		cClause(cPred(1,xInt),cNotPred(0,xInt, cPlus(cELocVar(1,INT),one)))
//		);
//		doTestP(
//		mList("(∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + n + 1) ∨ (∃x·∀y·∃w·x ↦ w ∈ T ∧ w = y + n + 1)"),
//		noLit,
//		cClause(cPred(1, cELocVar(0,INT)),cPred(1, cELocVar(1,INT)))
////		cClause(cNotPred(1,xInt),cPred(0,xInt, cPlus(yInt,n,one))),
////		cClause(cPred(1,xInt),cNotPred(0,xInt, cPlus(cELocVar(1,INT),n,one)))
//		)
		doTestP(
				mList("∃y·∀x·x ∈ S ⇔ x = y"),
				mList(cClause(cPred(2,cELocVar(0, Ssort))),
						cEqClause(cPred(1, xS, yS), cPred(0, xS), cEqual(xS, yS)),
						cClause(cNotPred(2, xS), cPred(1, yS, xS))),
				"S",S
		);

		doTestP(
				mList("∀x·x ∈ N ⇒ (∀y·y ∈ N ⇔ ¬x ↦ y ∈ T)"),
				mList(
						cClause(cNotPred(0,xInt),cPred(2,yInt,xInt)),
						cEqClause(cPred(2,xInt,yInt),cNotPred(0,xInt),cPred(1,yInt,xInt)))
		);
	}

	public void testQuantifiers() {
		doTestP(
				mList("∃x·x ∈ P ∨ (a ∈ S ∧ b ∈ U)"),
				mList(cClause(cPred(4, cELocVar(0,Bsort))),
						cClause(cNotPred(4,xB),cPred(0,xB),cProp(1)),
						cClause(cNotPred(4,xB),cPred(0,xB),cProp(2)),

						cClause(cPred(4,xB),cNotPred(0,xB)),
						cClause(cPred(4,xB),cNotProp(1),cNotProp(2)))
		);
		doTestP(
				mList("∀x·a ∈ S ∨ x ∈ S ∨ (∀y·x ∈ S ∨ y ∈ U)"),
				mList(
						cClause(cPred(0,aS),cPred(0,xS),cPred(0,xS),cPred(1,yU))),
				"a", aS
		);
		doTestP(
				mList("∀x·(a ∈ S ∨ x ∈ S) ∧ (∀y·a ∈ S ∨ y ∈ S)"),
				mList(
						cClause(cPred(0,aS),cPred(0,xS)),
						cClause(cPred(0,aS),cPred(0,xS))),
						"a",aS
		);
		doTestP(
				mList("∀x·(a ∈ S ∨ x ∈ S) ∨ (∀y·a ∈ S ∨ y ∈ S)"),
				mList(
						cClause(cPred(0, aS),cPred(0,xS),cPred(0, aS),cPred(0,yS))),
						"a",aS
		);
		doTestP(
				mList("∀x·a ∈ S ∨ x ∈ S ∨ (∀y·a ∈ S ∨ y ∈ U)"),
				mList(
						cClause(cPred(0, aS),cPred(0,xS),cPred(0, aS),cPred(1,yU))),
						"a",aS
		);
		// if the formula is universally quantified, do not labelize
		doTestP(
				mList("∀x·a ∈ S ∨ b ∈ S ∨ x ∈ S"),
				mList(
						cClause(cPred(0, aS),cPred(0, b),cPred(0,xS))),
						"a",aS,
						"b",b
		);
		doTestP(
				mList("∀x·x ∈ S ∨ x ∈ S"),
				mList(
						cClause(cPred(0,xS),cPred(0,xS)))
		);
		doTestP(
				mList("∀x·¬(¬x ∈ S ∨ ¬x ∈ S)"),
				mList(cClause(cPred(0,xS)),cClause(cPred(0,xS)))
		);
		doTestP(
				mList("∀x·x ∈ S ∧ x ∈ S"),
				mList(cClause(cPred(0,xS)),cClause(cPred(0,xS)))
		);
//		doTestP(
//		mList("(∀x·x + 1 ∈ N)", "n + 1 ∈ N"),
//		noLit
////		mList(cPred(0,cPlus(xInt,one)),cPred(0, cPlus(n,one)))
//		);
		doTestP(
				mList("(∀x·x ∈ S)"),
				mList(cClause(cPred(0,xS)))
		);
		doTestP(
				mList("(∃x·x ∈ S)"),
				mList(cClause(cPred(0,cELocVar(0,Ssort))))
		);
		doTestP(
				mList("¬(∀x·x ∈ S)"),
				mList(cClause(cNotPred(0,cELocVar(0,Ssort))))
		);
		doTestP(
				mList("¬(∃x·x ∈ S)"),
				mList(cClause(cNotPred(0,xS)))
		);
		doTestP(
				mList("¬(∃x·x ∈ S) ∨ (∃x·x ∈ S)"),
				mList(
						cClause(cPred(0,cELocVar(0,Ssort)),cNotPred(0,yS)))
		);
		doTestP(
				mList("(∀x·∃y·x ↦ y ∈ T)"),
				mList(cClause(cPred(0,xInt,cELocVar(1,NAT))))
		);
		doTestP(
				mList("(∃x·∀y·x ↦ y ∈ T)"),
				mList(cClause(cPred(1, cELocVar(0,NAT))),
						cClause(cNotPred(1,xInt),cPred(0,xInt,yInt)))
		);
		doTestP(
				mList("(∀x·∃y·∀z·x ↦ y ↦ z ∈ TT)"),
				mList(cClause(cPred(1,xInt,cELocVar(1,NAT))),
						cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt,zInt)))
		);
		doTestP(
				mList("∃x·∀y·∃z·x ↦ y ↦ z ∈ TT"),
				mList(cClause(cPred(2,cELocVar(0,NAT))),
						cClause(cNotPred(2,xInt),cPred(1,xInt,yInt)),
						cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt,cELocVar(2,NAT))))
		);

		doTestP(
				mList("¬(∃x·∀y·¬(x ↦ y ∈ T))"),
				mList(cClause(cPred(0,xInt,cELocVar(1,NAT))))
		);
		doTestP(
				mList("¬(∀x·∃y·x ↦ y ∈ T)"),
				mList(cClause(cPred(1,cELocVar(0,NAT))),
						cClause(cNotPred(1,xInt),cNotPred(0,xInt,yInt)))
		);
		doTestP(
				mList("¬(∃x·∀y·∃z·(x ↦ y) ↦ z ∈ M)"),
				mList(cClause(cPred(1,xB,cELocVar(0,Asort))),
						cClause(cNotPred(1,xB,yA),cNotPred(0,xB,yA,zA)))
		);
		doTestP(
				mList("(∀x·x ∈ S) ∨ (∀x·x ∈ S)"),
				mList(
						cClause(cPred(0,xS),cPred(0,yS)))
		);
		doTestP(
				mList("(∃y·∀x·x ↦ y ∈ T) ∨ (∃y·∀x·x ↦ y ∈ T)"),
				mList(
						cClause(cPred(1,cELocVar(0,NAT)),cPred(1,cELocVar(1,NAT))),
						cClause(cNotPred(1,xInt),cPred(0,yInt,xInt)))
//						cClause(cPred(1,xInt),cNotPred(0,cELocVar(2,INT),xInt))
		);


		doTestP(
				mList("(∃y·∀x·x ↦ y ∈ T) ∨ (∀z·(∃y·∀x·x ↦ y ∈ T) ∨ z ∈ S)"),
				mList(
						cClause(cPred(5,xS),cPred(3,cELocVar(2,NAT)),cPred(1,cELocVar(3,NAT))),
						cClause(cNotPred(3,xInt),cPred(0,yInt,xInt)),
						cClause(cNotPred(1,xInt),cPred(0,yInt,xInt)))
		);

		doTestP(
				mList("(∀x·x ∈ S ∧ x ∈ SS)"),
				mList(cClause(cPred(0,xS,S)),cClause(cPred(0,xS,SS))),
				"S",S,
				"SS",SS
		);

		doTestP(
				mList("∀x·x ∈ N ⇔ (∀y·y ∈ N ⇔ x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(0,xInt),cPred(2,cFLocVar(1,NAT),xInt)),
						cEqClause(cPred(2,xInt,yInt),cPred(0,xInt),cPred(1,yInt,xInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ⇔ ¬(∀y·y ∈ N ⇔ x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(0,xInt),cNotPred(2,cELocVar(1,NAT),xInt)),
						cEqClause(cPred(2,xInt,yInt),cPred(0,xInt),cPred(1,yInt,xInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ⇔ ¬(∃y·y ∈ N ⇔ x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(0,xInt),cNotPred(2,cFLocVar(1,NAT),xInt)),
						cEqClause(cPred(2,xInt,yInt),cPred(0,xInt),cPred(1,yInt,xInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ∨ (∀y·y ∈ N ∨ x ↦ y ∈ T)"),
				mList(
						cClause(cPred(0,xInt),cPred(0,yInt),cPred(1,xInt,yInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ∨ ¬(∀y·y ∈ N ∨ x ↦ y ∈ T)"),
				mList(
						cClause(cPred(0,xInt),cNotPred(2,cELocVar(1,NAT),xInt)),
						cClause(cPred(2,xInt,yInt),cNotPred(0,xInt)),
						cClause(cPred(2,xInt,yInt),cNotPred(1,yInt,xInt)),

						cClause(cNotPred(2,xInt,yInt),cPred(0,xInt),cPred(1,yInt,xInt)))
		);

		doTestP(
				// Rubin Exercise 8.A(18), p174
				mList(	"∀x,y,z·x ↦ y ∈ T ∧ y ↦ z ∈ T ⇒ x ↦ z ∈ T",
						"∀x,y·x ↦ y ∈ T ⇒ y ↦ x ∈ T",
				"¬((∀x·∃y·x ↦ y ∈ T) ⇒ (∀x·x ↦ x ∈ T))"),
				mList(	cClause(cNotPred(0,evar0N,evar0N)),
						cClause(cPred(0,xInt,cELocVar(1,NAT))),
						cClause(cPred(0,xInt,yInt),cNotPred(0,xInt,zInt),cNotPred(0,zInt,yInt)),
						cClause(cPred(0,xInt,yInt),cNotPred(0,yInt,xInt)))
		);
	}


	public void testEquivalence() {
		doTestP(
				mList("a ∈ S ⇔ a ∈ S"),
				mList(
						cEqClause(cProp(0),cProp(0)))
		);
		doTestP(
				mList("a ∈ S ⇔ d ∈ U"),
				mList(
						cEqClause(cProp(0),cProp(1)))
		);
		doTestP(
				mList("a ∈ S ⇔ b ∈ S"),
				mList(
						cEqClause(cPred(0, a),cPred(0, b))),
				"a",a,
				"b",b
		);
		doTestP(
				mList("∀x,y·x ∈ S ⇔ y ∈ S"),
				mList(
						cEqClause(cPred(0,xS),cPred(0,yS)))
		);
		doTestP(
				mList("a ∈ S ⇔ ¬(a ∈ S)"),
				mList(
						cEqClause(cNotProp(0),cProp(0)))
		);
		doTestP(
				mList("∀y·y ∈ N ⇔ (∀x·x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(0,xInt),cPred(1,cFLocVar(1,NAT),xInt)))
		);
		doTestP(
				mList("∀y·(∀x·x ↦ y ∈ T) ⇔ y ∈ N"),
				mList(
						cEqClause(cPred(2,xInt),cPred(0,cFLocVar(0,NAT),xInt)))
		);
		doTestP(
				mList("∀y·y ∈ N ⇔ (∃x·x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(0,xInt),cPred(1,cELocVar(1,NAT),xInt)))
		);
		doTestP(
				mList("a ∈ S ⇔ ¬(a ∈ S ⇔ a ∈ S)"),
				mList(
						cEqClause(cNotProp(0),cProp(0),cProp(0)))
		);
		doTestP(
				mList("(a ∈ S ⇔ (d ∈ U ⇔ c ∈ P)) ⇔ ((a ∈ S ⇔ d ∈ U) ⇔ c ∈ P)"),
				mList(
						cEqClause(cProp(0),cProp(1),cProp(2),cProp(2),cProp(0),cProp(1)))
		);
		doTestP(
				mList("¬(a ∈ S ⇔ (d ∈ U ⇔ c ∈ P))"),
				mList(
						cEqClause(cNotProp(0),cProp(1),cProp(2)))
		);
		doTestP(
				mList("∃x·x ∈ S ⇔ x ∈ S"),
				mList(cClause(cPred(1,cELocVar(0,Ssort))),
						cEqClause(cPred(1,xS),cPred(0,xS),cPred(0,xS)))
		);
		doTestP(
				mList("¬(∀x·¬(x ∈ S ⇔ x ∈ S))"),
				mList(cClause(cPred(1,cELocVar(0,Ssort))),
						cEqClause(cPred(1,xS),cPred(0,xS),cPred(0,xS)))
		);
		doTestP(
				mList("¬(∀x·x ∈ S ⇔ x ∈ S)"),
				mList(cClause(cNotPred(1,cELocVar(0,Ssort))),
						cEqClause(cPred(1,xS),cPred(0,xS),cPred(0,xS)))
		);
		doTestP(
				mList("¬(∃x·x ∈ S) ⇔ (∃x·x ∈ S)"),
				mList(
						cEqClause(cPred(0, cELocVar(0,Ssort)),cNotPred(0,cFLocVar(1,Ssort))))
		);
		doTestP(
				mList("∀y·¬(∃x·x ∈ N) ⇔ (∃x·x ↦ y ∈ T ∨ y ∈ N)"),
				mList(
						cEqClause(cPred(3,xInt, cELocVar(1,NAT)),cNotPred(0,cFLocVar(3,NAT))),
						cClause(cNotPred(3,xInt,yInt),cPred(0,xInt),cPred(2,yInt,xInt)),
						cClause(cPred(3,xInt,yInt),cNotPred(0,xInt)),
						cClause(cPred(3,xInt,yInt),cNotPred(2,yInt,xInt)))
		);


		/* MIXED */
		doTestP(
				mList("a ∈ S ⇔ (a ∈ S ∨ a ∈ S)"),
				mList(
						cEqClause(cProp(0),cProp(1)),
						cClause(cNotProp(1),cProp(0),cProp(0)),
						cClause(cProp(1),cNotProp(0)),
						cClause(cProp(1),cNotProp(0)))
		);
		doTestP(
				mList("a ∈ S ∨ (a ∈ S ⇔ a ∈ S)"),
				mList(
						cClause(cProp(0),cProp(1)),
						cEqClause(cProp(1),cProp(0),cProp(0)))
		);
		doTestP(
				mList("¬(d ∈ U ⇔ c ∈ P) ∨ (d ∈ U ⇔ c ∈ P)"),
				mList(
						cEqClause(cProp(2),cProp(0),cProp(1)),
						cEqClause(cProp(2),cProp(0),cProp(1)),
						cClause(cNotProp(2),cProp(2)))
		);
		doTestP(
				mList("¬(d ∈ U ∨ c ∈ P) ⇔ (d ∈ U ∨ c ∈ P)"),
				mList(
						cEqClause(cNotProp(2),cProp(2)),
						cClause(cNotProp(2),cProp(0),cProp(1)),
						cClause(cProp(2),cNotProp(0)),
						cClause(cProp(2),cNotProp(1)),
						cClause(cNotProp(2),cProp(0),cProp(1)),
						cClause(cProp(2),cNotProp(0)),
						cClause(cProp(2),cNotProp(1)))
		);

		doTestP(
				mList("a ∈ S ⇔ (d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				mList(
						cEqClause(cProp(0),cProp(4)),
						cClause(cNotProp(4),cProp(1),cProp(3)),
						cClause(cProp(4),cNotProp(1)),
						cClause(cProp(4),cNotProp(3)),
						cEqClause(cProp(3),cProp(2),cProp(2)))
		);
		doTestP(
				mList("a ∈ S ⇔ (d ∈ U ∨ ¬(c ∈ P ⇔ c ∈ P))"),
				mList(
						cEqClause(cProp(0),cProp(4)),
						cClause(cNotProp(4),cProp(1),cNotProp(3)),
						cClause(cProp(4),cNotProp(1)),
						cClause(cProp(4),cProp(3)),
						cEqClause(cProp(3),cProp(2),cProp(2)))
		);
		doTestP(
				mList("a ∈ S ⇔ ¬(d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				mList(
						cEqClause(cNotProp(0),cProp(4)),
						cClause(cNotProp(4),cProp(1),cProp(3)),
						cClause(cProp(4),cNotProp(1)),
						cClause(cProp(4),cNotProp(3)),
						cEqClause(cProp(3),cProp(2),cProp(2)))
		);
		doTestP(
				mList("(d ∈ U ∨ (c ∈ P ⇔ c ∈ P)) ⇔ a ∈ S"),
				mList(
						cEqClause(cProp(4),cProp(3)),
						cClause(cNotProp(3),cProp(0),cProp(2)),
						cClause(cProp(3),cNotProp(0)),
						cClause(cProp(3),cNotProp(2)),
						cEqClause(cProp(2),cProp(1),cProp(1)))
		);
		doTestP(
				mList(	"(d ∈ U ∨ (c ∈ P ⇔ c ∈ P)) ⇔ a ∈ S",
				"a ∈ S ⇔ (d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				mList(
						cClause(cNotProp(3),cProp(0),cProp(2)),
						cClause(cProp(3),cNotProp(0)),
						cClause(cProp(3),cNotProp(2)),
						cEqClause(cProp(2),cProp(1),cProp(1)),
						cEqClause(cProp(4),cProp(3)),
						cClause(cNotProp(3),cProp(0),cProp(2)),
						cClause(cProp(3),cNotProp(0)),
						cClause(cProp(3),cNotProp(2)),
						cEqClause(cProp(2),cProp(1),cProp(1)),
						cEqClause(cProp(4),cProp(3)))
		);

		doTestP(
				mList("a ∈ S ∧ (d ∈ U ⇔ c ∈ P)"),
				mList(cClause(cProp(0)),cClause(cProp(3)),
						cEqClause(cProp(3),cProp(1),cProp(2)))
		);
		doTestP(
				mList("a ∈ S ⇔ (d ∈ U ∧ c ∈ P)"),
				mList(
						cEqClause(cNotProp(0),cProp(3)),
						cClause(cNotProp(3),cNotProp(1),cNotProp(2)),
						cClause(cProp(3),cProp(1)),
						cClause(cProp(3),cProp(2)))
		);

		doTestP(
				mList("a ∈ S ⇔ b ∈ S","a ∈ S ⇔ c ∈ S"),
				mList(
						cEqClause(cPred(0,a),cPred(0,b)),
						cEqClause(cPred(0,a),cPred(0,c))),
				"a",a,
				"b",b,
				"c",c
		);
	}

	public void testArithmetic() {
		doTestP(
				mList("∃x· x = n + 1 ∧ x ∈ N"),
				mList(cClause(cNotPred(1,cELocVar(0,NAT))),
						cClause(cPred(1,xInt),cPred(0,xInt)),
						cClause(cPred(1,xInt),cAEqual(xInt, cPlus(n,one))),
						cClause(cNotPred(1,xInt),cNotPred(0,xInt),cANEqual(xInt, cPlus(n,one)))),
				1,one,
				"n",n
		);

		doTestP(
				mList("n = 1"),
				mList(cClause(cEqual(one, n))),
				1,one,
				"n",n
		);
		doTestP(
				mList("n ≠ 1"),
				mList(cClause(cNEqual(n, one))),
				1,one,
				"n",n
		);
		doTestP(
				mList("n > 1"),
				mList(cClause(cLess(one, n))),
				1,one,
				"n",n
		);
		doTestP(
				mList("n < 1"),
				mList(cClause(cLess(n, one))),
				1,one,
				"n",n
		);
		doTestP(
				mList("n ≥ 1"),
				mList(cClause(cLE(one, n))),
				1,one,
				"n",n
		);
		doTestP(
				mList("n ≤ 1"),
				mList(cClause(cLE(n, one))),
				1,one,
				"n",n
		);

		doTestP(
				mList("¬(n = 1)"),
				mList(cClause(cNEqual(n, one))),
				1,one,
				"n",n
		);
		doTestP(
				mList("¬(n ≠ 1)"),
				mList(cClause(cEqual(n, one))),
				1,one,
				"n",n
		);
		doTestP(
				mList("¬(n > 1)"),
				mList(cClause(cLE(n, one))),
				1,one,
				"n",n
		);
		doTestP(
				mList("¬(n < 1)"),
				mList(cClause(cLE(one, n))),
				1,one,
				"n",n
		);
		doTestP(
				mList("¬(n ≥ 1)"),
				mList(cClause(cLess(n, one))),
				1,one,
				"n",n
		);
		doTestP(
				mList("¬(n ≤ 1)"),
				mList(cClause(cLess(one, n))),
				1,one,
				"n",n
		);

		/* ***********/
		doTestP(
				mList("∀x·x = 1"),
				mList(cClause(cEqual(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·x ≠ 1"),
				mList(cClause(cNEqual(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·x > 1"),
				mList(cClause(cLess(one,xInt))),
				1,one
		);
		doTestP(
				mList("∀x·x < 1"),
				mList(cClause(cLess(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·x ≥ 1"),
				mList(cClause(cLE(one,xInt))),
				1,one
		);
		doTestP(
				mList("∀x·x ≤ 1"),
				mList(cClause(cLE(xInt,one))),
				1,one
		);

		doTestP(
				mList("∀x·¬(x = 1)"),
				mList(cClause(cNEqual(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·¬(x ≠ 1)"),
				mList(cClause(cEqual(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·¬(x > 1)"),
				mList(cClause(cLE(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·¬(x < 1)"),
				mList(cClause(cLE(one,xInt))),
				1,one
		);
		doTestP(
				mList("∀x·¬(x ≥ 1)"),
				mList(cClause(cLess(xInt,one))),
				1,one
		);
		doTestP(
				mList("∀x·¬(x ≤ 1)"),
				mList(cClause(cLess(one,xInt))),
				1,one
		);

		/* ******************/
		doTestP(
				mList("∃x·x = 1"),
				mList(cClause(cEqual(cELocVar(0,NAT), one))),
				1,one
		);
		doTestP(
				mList("∃x·x ≠ 1"),
				mList(cClause(cNEqual(cELocVar(0,NAT), one))),
				1,one
		);
		doTestP(
				mList("∃x·x > 1"),
				mList(cClause(cLess(one, cELocVar(0,NAT)))),
				1,one
		);
		doTestP(
				mList("∃x·x < 1"),
				mList(cClause(cLess(cELocVar(0,NAT), one))),
				1,one
		);
		doTestP(
				mList("∃x·x ≥ 1"),
				mList(cClause(cLE(one, cELocVar(0,NAT)))),
				1,one
		);
		doTestP(
				mList("∃x·x ≤ 1"),
				mList(cClause(cLE(cELocVar(0,NAT), one))),
				1,one
		);

		doTestP(
				mList("¬(∀x·x = 1)"),
				mList(cClause(cNEqual(cELocVar(0,NAT), one))),
				1,one
		);
		doTestP(
				mList("¬(∀x·x ≠ 1)"),
				mList(cClause(cEqual(cELocVar(0,NAT), one))),
				1,one
		);
		doTestP(
				mList("¬(∀x·x > 1)"),
				mList(cClause(cLE(cELocVar(0,NAT),one))),
				1,one
		);
		doTestP(
				mList("¬(∀x·x < 1)"),
				mList(cClause(cLE(one,cELocVar(0,NAT)))),
				1,one
		);
		doTestP(
				mList("¬(∀x·x ≥ 1)"),
				mList(cClause(cLess(cELocVar(0,NAT),one))),
				1,one
		);
		doTestP(
				mList("¬(∀x·x ≤ 1)"),
				mList(cClause(cLess(one,cELocVar(0,NAT)))),
				1,one
		);
		/* ******************/

		doTestP(
				mList("∀x·x > 1 ∨ x ≤ 1"),
				mList(
						cClause(cLess(one,xInt),cLE(xInt,one))),
						1,one
		);
		doTestP(
				mList("∃x·x > 1 ∨ x ≤ 1"),
				mList(cClause(cPred(0,cELocVar(0,NAT))),
						cClause(cNotPred(0,xInt),cLess(one,xInt),cLE(xInt,one)),

						cClause(cPred(0,xInt),cLE(xInt,one)),
						cClause(cPred(0,xInt),cLess(one,xInt))),
						1,one
		);

		doTestP(
				mList("n + m + 1 = 0"),
				mList(cClause(cAEqual(cPlus(n,m,one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n + m) ∗ 1 = 0"),
				mList(cClause(cAEqual(cTimes(cPlus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("n ∗ m + 1 = 0"),
				mList(cClause(cAEqual(cPlus(cTimes(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("n ∗ m ∗ 1 = 0"),
				mList(cClause(cAEqual(cTimes(n,m,one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n − m) ∗ 1 = 0"),
				mList(cClause(cAEqual(cTimes(cMinus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("n ∗ m − 1 = 0"),
				mList(cClause(cAEqual(cMinus(cTimes(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n − m) − 1 = 0"),
				mList(cClause(cAEqual(cMinus(cMinus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n − m) ^ 1 = 0"),
				mList(cClause(cAEqual(cExpn(cMinus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ^ m) − 1 = 0"),
				mList(cClause(cAEqual(cMinus(cExpn(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n + m) ^ 1 = 0"),
				mList(cClause(cAEqual(cExpn(cPlus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ^ m) + 1 = 0"),
				mList(cClause(cAEqual(cPlus(cExpn(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n mod m) ^ 1 = 0"),
				mList(cClause(cAEqual(cExpn(cMod(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ^ m) mod 1 = 0"),
				mList(cClause(cAEqual(cMod(cExpn(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n + m) mod 1 = 0"),
				mList(cClause(cAEqual(cMod(cPlus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n mod m) + 1 = 0"),
				mList(cClause(cAEqual(cPlus(cMod(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n − m) mod 1 = 0"),
				mList(cClause(cAEqual(cMod(cMinus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n mod m) − 1 = 0"),
				mList(cClause(cAEqual(cMinus(cMod(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n − m) ÷ 1 = 0"),
				mList(cClause(cAEqual(cDiv(cMinus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ÷ m) − 1 = 0"),
				mList(cClause(cAEqual(cMinus(cDiv(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ÷ m) mod 1 = 0"),
				mList(cClause(cAEqual(cMod(cDiv(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n mod m) ÷ 1 = 0"),
				mList(cClause(cAEqual(cDiv(cMod(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ÷ m) ∗ 1 = 0"),
				mList(cClause(cAEqual(cTimes(cDiv(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ∗ m) ÷ 1 = 0"),
				mList(cClause(cAEqual(cDiv(cTimes(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ÷ m) + 1 = 0"),
				mList(cClause(cAEqual(cPlus(cDiv(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n + m) ÷ 1 = 0"),
				mList(cClause(cAEqual(cDiv(cPlus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ÷ m) ^ 1 = 0"),
				mList(cClause(cAEqual(cExpn(cDiv(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ^ m) ÷ 1 = 0"),
				mList(cClause(cAEqual(cDiv(cExpn(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);

		doTestP(
				mList("(n − m) − 1 = 0"),
				mList(cClause(cAEqual(cMinus(cMinus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n + m) + 1 = 0"),
				mList(cClause(cAEqual(cPlus(cPlus(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ÷ m) ÷ 1 = 0"),
				mList(cClause(cAEqual(cDiv(cDiv(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ^ m) ^ 1 = 0"),
				mList(cClause(cAEqual(cExpn(cExpn(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n mod m) mod 1 = 0"),
				mList(cClause(cAEqual(cMod(cMod(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);
		doTestP(
				mList("(n ∗ m) ∗ 1 = 0"),
				mList(cClause(cAEqual(cTimes(cTimes(n,m),one), zero))),
				"n",n,
				"m",m,
				1,one,
				0,zero
		);

	}

	public void testBool() {
		doTestP(
				mList("b = TRUE"),
				mList(cClause(cProp(0)))
		);
		doTestP(
				mList("b = FALSE"),
				mList(cClause(cNotProp(0)))
		);
		doTestP(
				mList("b = TRUE","b = TRUE"),
				mList(cClause(cProp(0)),cClause(cProp(0)))
		);
		doTestP(
				mList("b = TRUE","b = FALSE"),
				mList(cClause(cProp(0)),cClause(cNotProp(0)))
		);
		doTestP(
				mList("b = TRUE","c = TRUE"),
				mList(cClause(cProp(0)),cClause(cProp(1)))
		);
		doTestP(
				mList("b = TRUE","c = b"),
				mList(	cClause(cEqual(bbool, TRUE)),
						cClause(cEqual(cbool, bbool))),
						"b",bbool,
						"c",cbool,
						"TRUE",TRUE
		);
		doTestP(
				mList("b = TRUE","c = e"),
				mList(
						cClause(cProp(0)),
						cClause(cEqual(c, e))),
						"c",c,
						"e",e
		);

		doTestP(
				mList("A=TRUE ⇒ B=TRUE","A=TRUE ⇒ C=TRUE"),
				mList(
						cClause(cProp(1),cNotProp(2)),
						cClause(cProp(3),cNotProp(2)))
		);
//		doTestP(
//		mList("(A=TRUE∨B=TRUE⇒C=TRUE)⇔(A=TRUE⇒C=TRUE)∧(B=TRUE⇒C=TRUE)"),
//		noLit

//		);
		doTestP(
				mList("A = TRUE ∨ a ∈ S","B = TRUE ∨ b ∈ S"),
				mList(
						cClause(cPred(0,aS),cProp(2)),
						cClause(cPred(0,b),cProp(3))),
					"a",aS,
					"b",b
		);
		doTestP(
				mList("A = TRUE ∨ a ∈ S","B = TRUE ∨ b ∈ S","A = B"),
				mList(	cClause(cEqual(Abool,Bbool)),
						cClause(cPred(0,a),cEqual(Abool,TRUE)),
						cClause(cPred(0,b),cEqual(Bbool,TRUE))),
				"A", Abool,
				"B", Bbool,
				"a",a,
				"b",b,
				"TRUE",TRUE
		);
	}

	public void testGoal() {
		doTestG(
				mList("∀x,y·x ↦ y ∈ V ∨ (∀z·(x ↦ y) ↦ z ∈ VV) ∨ (∀z·(x ↦ y) ↦ z ∈ VV)"),
				mList(cClause(cNotPred(3, cELocVar(0,Asort),cELocVar(1,Bsort))),
						cClause(cPred(3, xA, yB),cNotPred(0,xA,yB)),
						cClause(cPred(3, xA, yB),cNotPred(1,xA,yB,cELocVar(2,Csort))),
						cClause(cPred(3, xA, yB),cNotPred(1,xA,yB,cELocVar(3,Csort))),
						cClause(cNotPred(3, xA, yB),cPred(0,xA,yB),cPred(1,xA,yB,zC),cPred(1,xA,yB,tC)))
		);
		doTestG(
				mList("∀x,y·x ↦ y ∈ T ⇒ y ↦ x ∈ T"),
				mList(cClause(cNotPred(1,cELocVar(0,NAT),cELocVar(1,NAT))),
						cClause(cPred(1,xInt,yInt),cNotPred(0,xInt,yInt)),
						cClause(cPred(1,xInt,yInt),cPred(0,yInt,xInt)),

						cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt),cNotPred(0,yInt,xInt)))
		);
		doTestG(
				mList(
				"(∀x·∃y·x ↦ y ∈ T) ⇒ (∀x·x ↦ x ∈ T)"),
				mList(	cClause(cNotPred(0,evar0N,evar0N)),
						cClause(cPred(0,xInt,cELocVar(1,NAT))))
		);
		doTestG(
				mList(
						"((∀x·x∈R)⇒(∃y·¬(y∈Q)))⇒((∀x·x∈Q)⇒(∃y·¬(y∈R)))"
				),
				mList(cClause(cPred(0, xA, Q)),cClause(cPred(0, xA, R)),
						cClause(cNotPred(0, cELocVar(0,Asort), R),cNotPred(0, cELocVar(1,Asort), Q))),
				"Q",Q,
				"R",R
		);

		doTestG(
				mList("∀y·∃x·x ∈ N ∧ x ↦ y ∈ T"),
				mList(cClause(cPred(3,cELocVar(1,NAT))),
						cClause(cNotPred(3,xInt),cPred(2,yInt,xInt)),
						cClause(cNotPred(2,xInt,yInt),cNotPred(0,xInt),cNotPred(1,xInt,yInt)),

						cClause(cPred(2,xInt,yInt),cPred(1,xInt,yInt)),
						cClause(cPred(2,xInt,yInt),cPred(0,xInt)))
		);
		doTestG(
				mList("∀y·¬(∀x·¬(x ∈ N ∧ x ↦ y ∈ T))"),
				mList(cClause(cPred(3,cELocVar(1,NAT))),
						cClause(cNotPred(3,xInt),cPred(2,yInt,xInt)),
						cClause(cNotPred(2,xInt,yInt),cNotPred(0,xInt),cNotPred(1,xInt,yInt)),

						cClause(cPred(2,xInt,yInt),cPred(1,xInt,yInt)),
						cClause(cPred(2,xInt,yInt),cPred(0,xInt)))
		);

		doTestG(
				mList("∃x·∀y·x ∈ N ⇒ x ↦ y ∈ T"),
				mList(cClause(cNotPred(2,xInt,cELocVar(1,NAT))),
						cClause(cPred(2,xInt,yInt),cPred(0,xInt)),
						cClause(cPred(2,xInt,yInt),cNotPred(1,xInt,yInt)),

						cClause(cNotPred(2,xInt,yInt),cNotPred(0,xInt),cPred(1,xInt,yInt)))
		);
		doTestG(
				mList("¬(∀x·¬(∀y·x ∈ N ⇒ x ↦ y ∈ T))"),
				mList(cClause(cNotPred(2,xInt,cELocVar(1,NAT))),
						cClause(cPred(2,xInt,yInt),cPred(0,xInt)),
						cClause(cPred(2,xInt,yInt),cNotPred(1,xInt,yInt)),

						cClause(cNotPred(2,xInt,yInt),cNotPred(0,xInt),cPred(1,xInt,yInt)))
		);
	}


	public void testPairVariable() {
		Constant X = cCons("X",PAC);
		Constant Y = cCons("Y",PAC);
		Constant V = cCons("V",PAB);
		Constant W = cCons("W",PAB);
		Object[] constants = new Object[] {
				"X",X,
				"Y",Y,
				"V",V,
				"W",W
		};
		
		doTestP(
				mList("(∀x·x ∈ Q) ∧ (∀x·x ∈ P)"),
				mList(cClause(cPred(0,xA)),cClause(cPred(2,xB))),
				constants
		);
		doTestP(
				mList("∀x·x ∈ Q ∨ (∃y·x ↦ y ∈ V ∨ x ↦ y ∈ W) ∨ (∃y·x ↦ y ∈ X ∨ x ↦ y ∈ Y)"),
				mList(
						cClause(cNotPred(5, xA, yC), cPred(4, xA, yC, X), cPred(4,xA,yC,Y)),
						cClause(cNotPred(2, xA, yB), cPred(1, xA, yB, V), cPred(1,xA,yB,W)),
						cClause(cPred(0,xA),cPred(5,xA,cELocVar(0,Csort)),cPred(2,xA,cELocVar(1,Bsort))),

						cClause(cPred(5, xA, yC), cNotPred(4, xA, yC, X)),
						cClause(cPred(5, xA, yC), cNotPred(4, xA, yC, Y)),
						cClause(cPred(2, xA, yB), cNotPred(1, xA, yB, V)),
						cClause(cPred(2, xA, yB), cNotPred(1, xA, yB, W))),
				constants
		);
	}

	public void testHypotheses() {
			doTestG(
					mList("b ∈ S", "a ∈ C"),
					mList(cClause(cNotPred(0,b,S)),cClause(cNotPred(0,a,C))),
					"a",a,
					"b",b,
					"C",C,
					"S",S
			);
//			doTestP(
//			mList("S ∈ O", "∃x·(∀x0·x0∈x)∧x∈O"),
//			mList(cNotPred(0,S)),
//			cClause(cNotPred(0,xS),cNotPred(1,cELocVar(1,Ssort),xS))
//			);
			doTestG(
					mList("a ∈ S", "b ∈ S"),
					mList(cClause(cNotPred(0,a)),cClause(cNotPred(0,b))),
					"a",a,
					"b",b
			);
	}
	
	
	public void doTestP(List<String> strPredicate, Collection<Clause> clauses, Object... constants) {
		doTest(strPredicate, clauses, constants, false);
	}
	
	public void doTestG(List<String> strPredicate, Collection<Clause> clauses, Object... constants) {
		doTest(strPredicate, clauses, constants, true);
	}
	
	
	public void doTest(List<String> strPredicate, Collection<Clause> clauses, Object[] constants, boolean goal) {
		ClauseBuilder result = load(strPredicate, goal, constants);
		
		assertSameClauses(strPredicate, clauses, result.getClauses());
		assertEquals("Actual: "+result.getClauses()+"\nExpected: "+clauses,result.getClauses().size(), clauses.size());
//		assertSameClauses(strPredicate, literals, result.getLiterals());
	}
	
	private ClauseBuilder load(List<String> strPredicate, boolean goal, Object... constants) {
		PredicateBuilder builder = new PredicateBuilder();
		ClauseBuilder cBuilder = new ClauseBuilder();
		ITypeEnvironment tmp = env.clone();
		
		for (String str : strPredicate) {
			ITypeCheckResult res = getResult(str, builder, tmp, goal);
			tmp.addAll(res.getInferredEnvironment());
		}
		
		VariableTable variableTable = getVariableTable(constants);
		cBuilder.loadClausesFromContext(builder.getContext(), variableTable);
		return cBuilder;
	}
	
	private VariableTable getVariableTable(Object[] constants) {
		MyVariableTable variableTable = new MyVariableTable();
		for (int i = 0; i < constants.length; i=i+2) {
			Object object = constants[i];
			Object constant = constants[i+1];
			variableTable.addConstant(object, constant);
		}
		return variableTable;
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
	
	static class MyVariableTable extends VariableTable {

		public MyVariableTable() {
			super(new VariableContext());
		}

		public void addConstant(Object object, Object constant) {
			if (object instanceof Integer) integerTable.put(BigInteger.valueOf((Integer)object), (IntegerConstant)constant);
			else if (object instanceof String) constantTable.put((String)object, (Constant)constant);
			else assert false;
		}
	}
	
//	public void testEquivalence() {
//		for (TestPair test : equiClauses) {
//			doTestP(test.predicate, test.clauses, false, constants);	
//		}
//	}
//	
//	public void testLocalVariable() {
//		for (TestPair test : testVariableTable) {
//			doTestP(test.predicate, test.clauses, false, constants);
//		}
//	}
//
//	public void testClauseBuilder() {
//		for (TestPair test : tests1) {
//			doTestP(test.predicate, test.clauses, false, constants);
//		}
//	}
//	
//	public void testQuantifiers() {
//		for (TestPair test : tests2) {
//			doTestP(test.predicate, test.clauses, false);
//		}
//	}
//	
//	public void testComplex() {
//		for (TestPair test : tests3) {
//			doTestP(test.predicate, test.clauses, false);
//		}
//	}
//	
//	public void testArithmetic() {
//		for (TestPair test : arithmetic) {
//			doTestP(test.predicate, test.clauses, false);
//		}
//	}
//	
//	public void testBoolean() {
//		for (TestPair test : testsBool) {
//			doTestP(test.predicate, test.clauses, false);
//		}
//	}
//	
//	public void testGoal() {
//		for (TestPair test : testsGoal) {
//			doTestP(test.predicate, test.clauses, true);
//		}
//	}
//	
//	public void testTypeClauseGeneration() {
//		for (TestPair test : testTypeClauseGeneration) {
//			doTestP(test.predicate, test.clauses, true);
//		}
//	}
//	
	
	public void testNotSameVariable() {
		ClauseBuilder result = load(mList("(∀x·x ∈ S ∨ x ∈ S) ⇒ (∀x·x ∈ S ∨ x ∈ S )"),false);
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