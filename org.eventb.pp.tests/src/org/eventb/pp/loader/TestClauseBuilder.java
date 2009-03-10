package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.cAEqual;
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
import static org.eventb.internal.pp.core.elements.terms.Util.d0A;
import static org.eventb.internal.pp.core.elements.terms.Util.d1A;
import static org.eventb.internal.pp.core.elements.terms.Util.d2A;
import static org.eventb.internal.pp.core.elements.terms.Util.descriptor;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.PredicateTable;
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
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.junit.Test;

// TODO test negation of the goal
public class TestClauseBuilder extends AbstractPPTest {
	
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
	
	private static final PredicateLiteralDescriptor d0B = descriptor(0, Bsort);
	private static final PredicateLiteralDescriptor d0S = descriptor(0, Ssort);
	private static final PredicateLiteralDescriptor d0Z = descriptor(0, NAT);
	private static final PredicateLiteralDescriptor d0AB = descriptor(0, Asort, Bsort);
	private static final PredicateLiteralDescriptor d0APA = descriptor(0, Asort, PA);
	private static final PredicateLiteralDescriptor d0SS = descriptor(0, Ssort, Ssort);
	private static final PredicateLiteralDescriptor d0SPS = descriptor(0, Ssort, PS);
	private static final PredicateLiteralDescriptor d0SPSS = descriptor(0, Ssort, PSS);
	private static final PredicateLiteralDescriptor d0ZZ = descriptor(0, NAT, NAT);
	private static final PredicateLiteralDescriptor d0ZZZ = descriptor(0, NAT, NAT, NAT);
	private static final PredicateLiteralDescriptor d0BAA = descriptor(0, Bsort, Asort, Asort);
	private static final PredicateLiteralDescriptor d0ABPAB = descriptor(0, Asort, Bsort, PAB);

	private static final PredicateLiteralDescriptor d1S = descriptor(1, Ssort);
	private static final PredicateLiteralDescriptor d1U = descriptor(1, Usort);
	private static final PredicateLiteralDescriptor d1Z = descriptor(1, NAT);
	private static final PredicateLiteralDescriptor d1ABC = descriptor(1, Asort, Bsort, Csort);
	private static final PredicateLiteralDescriptor d1APA = descriptor(1, Asort, PA);
	private static final PredicateLiteralDescriptor d1BA = descriptor(1, Bsort, Asort);
	private static final PredicateLiteralDescriptor d1SS = descriptor(1, Ssort, Ssort);
	private static final PredicateLiteralDescriptor d1ZZ = descriptor(1, NAT, NAT);
	private static final PredicateLiteralDescriptor d1ABPAB = descriptor(1, Asort, Bsort, PAB);
	private static final PredicateLiteralDescriptor d1ZZZZ = descriptor(1, NAT, NAT, NAT, NAT);

	private static final PredicateLiteralDescriptor d2B = descriptor(2, Bsort);
	private static final PredicateLiteralDescriptor d2S = descriptor(2, Ssort);
	private static final PredicateLiteralDescriptor d2Z = descriptor(2, NAT);
	private static final PredicateLiteralDescriptor d2AB = descriptor(2, Asort, Bsort);
	private static final PredicateLiteralDescriptor d2BA = descriptor(2, Bsort, Asort);
	private static final PredicateLiteralDescriptor d2SS = descriptor(2, Ssort, Ssort);
	private static final PredicateLiteralDescriptor d2ZZ = descriptor(2, NAT, NAT);
	private static final PredicateLiteralDescriptor d2ZZZ = descriptor(2, NAT, NAT, NAT);
	private static final PredicateLiteralDescriptor d2SPSS = descriptor(2, Ssort, PS, Ssort);

	private static final PredicateLiteralDescriptor d3B = descriptor(3, Bsort);
	private static final PredicateLiteralDescriptor d3Z = descriptor(3, NAT);
	private static final PredicateLiteralDescriptor d3AB = descriptor(3, Asort, Bsort);
	private static final PredicateLiteralDescriptor d3SS = descriptor(3, Ssort, Ssort);
	private static final PredicateLiteralDescriptor d3ZZ = descriptor(3, NAT, NAT);
	private static final PredicateLiteralDescriptor d3BAA = descriptor(3, Bsort, Asort, Asort);

	private static final PredicateLiteralDescriptor d4B = descriptor(4, Bsort);
	private static final PredicateLiteralDescriptor d4S = descriptor(4, Ssort);
	private static final PredicateLiteralDescriptor d4Z = descriptor(4, NAT);
	private static final PredicateLiteralDescriptor d4SPS = descriptor(4, Ssort, PS);
	private static final PredicateLiteralDescriptor d4ACPAC = descriptor(4, Asort, Csort, PAC);
	private static final PredicateLiteralDescriptor d4APABA = descriptor(4, Asort, PA, Bsort, Asort);

	private static final PredicateLiteralDescriptor d5S = descriptor(5, Ssort);
	private static final PredicateLiteralDescriptor d5AC = descriptor(5, Asort, Csort);

	private static final PredicateLiteralDescriptor d6PAB = descriptor(6, PAB);

	private static final PredicateLiteralDescriptor d12B = descriptor(12, Bsort);

	private static final PredicateLiteralDescriptor d15B = descriptor(15, Bsort);

	private static IntegerConstant zero = cIntCons(0);
	private static IntegerConstant one = cIntCons(1); 
//	private static IntegerConstant two = cIntCons(2);
	
	private static Constant a = cCons("a", Ssort);
	private static Constant b = cCons("b", Ssort);
	private static Constant c = cCons("c", Usort);
	private static Constant cS = cCons("c", Ssort);
	private static Constant d = cCons("d", Usort);
	private static Constant eS = cCons("e", Ssort);
	private static Constant n = cCons("n",NAT);
	private static Constant m = cCons("m",NAT);
	private static Constant S = cCons("S", PS);
	private static Constant SS = cCons("SS", PS);
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
//	private static Variable zC = cVar(6,Csort);
	private static Variable tA = cVar(7,Asort);
//	private static Variable tC = cVar(8,Csort);
	private static Variable tPAB = cVar(9,PAB); 
	private static Variable xA = cVar(10,Asort);
	private static Variable xB = cVar(15,Bsort);
	private static Variable yC = cVar(16,Csort);
	private static Variable yU = cVar(17,Usort);
	private static Variable yB = cVar(18,Bsort);
	private static Variable yS = cVar(19,Ssort);
	private static Variable yA = cVar(20,Asort);
	private static Variable yPA = cVar(21,PA);
	private static Variable xS = cVar(22,Ssort);
	private static Variable zS = cVar(23,Ssort);
	private static Variable xPS = cVar(24,PS);
	
	private static final ITypeEnvironment env = mTypeEnvironment(
		"x0", ty_A,
		"x1", ty_B,
		"a", ty_S,
		
		"VV", REL(CPROD(ty_A, ty_B),ty_C),
		"V", REL(ty_A, ty_B),
		"W", REL(ty_A, ty_B),
		"X", REL(ty_A, ty_C),
		"Y", REL(ty_A, ty_C),
		"AA", REL(ty_S, ty_S),
		
		"e", ty_BOOL,
		"f", REL(ty_A, ty_B),
		"n", INT,
		"N", POW(INT),
		"S", POW(ty_S),
		"P", POW(ty_B),
		"Q", POW(ty_A),
		"R", POW(ty_A),
		"U", POW(ty_U),
		"M", REL(CPROD(ty_B,ty_A),ty_A),
		"SS", POW(ty_S),
		"T", REL(INT, INT),
		"TT", REL(CPROD(INT,INT), INT)
	);
	
    @Test
	public void testSimple() {
//		final ITypeEnvironment env = ff.makeTypeEnvironment();
//		env.addName("a", tyS);
//		env.addName("b", tyS);
//		env.addName("S", POW(tyS));
//		
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
				mList(cClause(cNotProp(0),cProp(1))),
				constants1
		);
		doTestP(
				mList("a ∈ S ⇒ b ∈ S"),
				mList(cClause(cPred(d0S, b),cNotPred(d0S, a))),
				constants1
		);
		doTestP(
				mList("¬(a ∈ S) ⇒ d ∈ U"),
				mList(cClause(cProp(0),cProp(1))),
				constants1
		);
		doTestP(
				mList("a ∈ S ⇒ ¬(d ∈ U)"),
				mList(cClause(cNotProp(0),cNotProp(1))),
				constants1
		);
		doTestP(
				mList("¬(a ∈ S) ⇒ b ∈ S"),
				mList(cClause(cPred(d0S, a),cPred(d0S, b))),
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
				mList(cClause(cProp(0), cProp(0))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ SS"),
				mList(cClause(cPred(d0SPS, a, S), cPred(d0SPS, b, SS))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ S"),
				mList(cClause(cPred(d0S, a), cPred(d0S, b))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ S ∨ c ∈ U"),
				mList(cClause(cPred(d0S, a), cPred(d0S, b), cProp(1))),
				constants1
		);
		doTestP(
				mList("a ∈ S ∨ b ∈ S ∨ c ∈ U ∨ d ∈ U"),
				mList(cClause(cPred(d0S, a), cPred(d0S, b), cPred(d1U, c), cPred(d1U, d))),
				constants1
		);
		doTestP(
				mList("¬(¬(a ∈ S) ∧ ¬(b ∈ S))"),
				mList(cClause(cPred(d0S, a), cPred(d0S, b))),
				constants1
		);

		doTestP(	
				mList("(a ∈ S ∨ b ∈ S) ∨ (a ∈ S ∨ b ∈ S)"),
				mList(cClause(cPred(d0S, a), cPred(d0S, b), cPred(d0S, a), cPred(d0S,b))),
				constants1
		);

		doTestP(
				mList("∀x·x ∈ S ∨ ¬(x ∈ A ⇔ ¬(x ∈ B ∨ x ∈ C))"),
				mList(
						cClause(cPred(d0SPS,xS,S), cNotPred(d2S,xS)),
						cClause(cNotPred(d1S,xS), cPred(d0SPS,xS,B), cPred(d0SPS,xS,C)),
						cClause(cPred(d1S,xS), cNotPred(d0SPS,xS,B)),
						cClause(cPred(d1S,xS), cNotPred(d0SPS,xS,C)),
						cEqClause(cPred(d2S,xS), cNotPred(d0SPS,xS,A), cPred(d1S,xS))
				),
				"A", A,
				"B", B,
				"C", C,
				"S", S
		);

		doTestP(	
				mList("∃x·x ∈ S"),
				mList(cClause(cPred(d0S, cELocVar(0,Ssort)))),
				constants1
		);
	}

    @Test
	public void testComplex() {
		Object[] constants = new Object[]{
				"a", a,
				"b", b,
				"c", c,
				"d", d,
		};
		doTestP(
				mList("(a ∈ S ∨ b ∈ S) ∨ (∀x,y·x ∈ S ∨ y ∈ S)"),
				mList(
						cClause(cPred(d0S,a), cPred(d0S,b), cPred(d0S,xS), cPred(d0S,yS))),
				constants
		);
		doTestP(
				mList("∃x·∀y·∃z·∃w·x ↦ w ∈ T ∧ w = y + z"),
				mList(	cClause(cPred(d4Z,cELocVar(0,NAT))),
						cClause(cNotPred(d4Z,xInt),cPred(d2ZZZ,xInt,yInt,cELocVar(1,NAT))),
						cClause(cNotPred(d2ZZZ,xInt,yInt,zInt),cNotPred(d1ZZZZ,xInt,cELocVar(2,NAT),yInt,zInt)),
						cClause(cPred(d1ZZZZ,xInt,yInt,zInt,wInt),cAEqual(yInt,cPlus(zInt, wInt))),
						cClause(cPred(d1ZZZZ,xInt,yInt,zInt,wInt),cPred(d0ZZ,xInt,yInt))

//						cClause(cNotPred(1,xInt,yInt,zInt,wInt),cANEqual(yInt,cPlus(zInt, wInt)),cNotPred(0,xInt,yInt))
				)
		);
		doTestP(
				mList("∃x·x ∈ P ∧ (∃y·y ∈ Q ∨ y ∈ R)"),
				mList(	
						cClause(cNotPred(d4B,cELocVar(1,Bsort))),
						cClause(cPred(d4B,xB),cPred(d2A,cELocVar(1,Asort))),
						cClause(cPred(d4B,xB),cPred(d0B,xB)),
						cClause(cNotPred(d2A,xA),cPred(d1APA,xA,Q),cPred(d1APA,xA,R))
						
//						cClause(cPred(4,xB),cPred(2,cELocVar(3,Asort))),
//						cClause(cNotPred(4,xB),cNotPred(0,xB),cNotPred(2,yA)),
//						cClause(cPred(2,xA),cNotPred(1,xA,R)),
//						cClause(cPred(2,xA),cNotPred(1,xA,Q))
				),
				"Q", Q,
				"R", R
		);
		doTestP(
				mList(
						"∀x, y · x ∈ P ∧ y ∈ Q ⇒ (∃z · z ∈ R ∧ (x↦y)↦z ∈ M)",
						"∀x · x ∈ Q ∨ x ∈ R",
						"∃x · x ∈ P ∧ (∀y · y ∈ R ⇒ (∃z · z ∈ Q ∧ (x↦y)↦z ∈ M))",
						"∃x · ∀y · ∃z · (x↦y)↦z ∈ M"
				),
				mList(/* 3 */cClause(cNotPred(d12B, cELocVar(1,Bsort))), /* 4 */cClause(cPred(d15B, cELocVar(2,Bsort))),
						// 1 ok
						cClause(cNotPred(d0B,xB),cNotPred(d1APA,yA,Q),cNotPred(d4APABA, cELocVar(0,Asort),R,xB,yA)),
						cClause(cPred(d4APABA,xA,yPA,zB,tA),cPred(d1APA,xA,yPA)),
						cClause(cPred(d4APABA,xA,yPA,zB,tA),cPred(d3BAA,zB,tA,xA)),
						cClause(cPred(d4APABA,xA,yPA,zB,tA),cPred(d1APA,xA,yPA)),
						cClause(cPred(d4APABA,xA,yPA,zB,tA),cPred(d3BAA,zB,tA,xA)),
						// 2 ok
						cClause(cPred(d1APA,xA,Q),cPred(d1APA,xA,R)),
						// 3 ok
						cClause(cPred(d12B,xB),cNotPred(d1APA,xA,R),cNotPred(d4APABA, cELocVar(9,Asort),Q,xB,xA)),
						cClause(cPred(d12B,xB),cPred(d0B,xB)),
						cClause(cNotPred(d15B,xB),cPred(d3BAA,xB,yA,cELocVar(3,Asort)))
						// 4 ok
//						cClause(cNotPred(14,xB,yA),cPred(3,xB,yA,cELocVar(2,Asort))),
//						cClause(cNotPred(15,xB),cPred(14,xB,yA)),
						// unneeded definitions
//						cClause(cNotPred(4,xA,yPA,zB,tA),cNotPred(1,xA,yPA),cNotPred(3,zB,tA,xA)),
//						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(1,xA,R)),
//						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(4,yA,Q,xB,xA)),
//						cClause(cNotPred(4,xA,yPA,zB,tA),cNotPred(1,xA,yPA),cNotPred(3,zB,tA,xA)),
//						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(1,xA,R)),
//						cClause(cNotPred(12,xB),cNotPred(0,xB),cPred(4,yA,Q,xB,xA))
						),
						"Q", Q,
						"R", R
		);
		doTestP(
				mList("∃f\u2982ℙ(A×B)·(∀x,x0,x1·x ↦ x0∈f∧x ↦ x1∈f⇒x0=x1)∧(∀x·∃x0·x ↦ x0∈f)"),
				mList(	cClause(cNotPred(d6PAB,cELocVar(0,PAB))),
						cClause(cPred(d6PAB,tPAB),cEqual(xB,yB),cNotPred(d0ABPAB,zA,xB,tPAB),cNotPred(d0ABPAB,zA,yB,tPAB)),
						cClause(cPred(d6PAB,tPAB),cPred(d0ABPAB,xA,cELocVar(1,Bsort),tPAB))

//						cClause(cNotPred(6,tPAB),cNotPred(0,xA,yB,tPAB),cNEqual(xB,zB)),
//						cClause(cNotPred(6,tPAB),cNotPred(0,xA,yB,tPAB),cPred(0,zA,xB,tPAB)),
//						cClause(cNotPred(6,tPAB),cNotPred(0,xA,yB,tPAB),cPred(0,zA,xB,tPAB))
				)
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
				mList(cClause(cPred(d2S,cELocVar(0, Ssort))),
						cEqClause(cPred(d1SS, xS, yS), cPred(d0S, xS), cEqual(xS, yS)),
						cClause(cNotPred(d2S, xS), cPred(d1SS, yS, xS))),
				"S",S
		);

		doTestP(
				mList("∀x·x ∈ N ⇒ (∀y·y ∈ N ⇔ ¬x ↦ y ∈ T)"),
				mList(
						cClause(cNotPred(d0Z,xInt),cPred(d2ZZ,yInt,xInt)),
						cEqClause(cPred(d2ZZ,xInt,yInt),cNotPred(d0Z,xInt),cPred(d1ZZ,yInt,xInt)))
		);
	}

    @Test
	public void testQuantifiers() {
		doTestP(
				mList("∃x·x ∈ P ∨ (a ∈ S ∧ b ∈ U)"),
				mList(cClause(cPred(d4B, cELocVar(0,Bsort))),
						cClause(cNotPred(d4B,xB),cPred(d0B,xB),cProp(1)),
						cClause(cNotPred(d4B,xB),cPred(d0B,xB),cProp(2))

//						cClause(cPred(4,xB),cNotPred(0,xB)),
//						cClause(cPred(4,xB),cNotProp(1),cNotProp(2))
				)
		);
		doTestP(
				mList("∀x·a ∈ S ∨ x ∈ S ∨ (∀y·x ∈ S ∨ y ∈ U)"),
				mList(
						cClause(cPred(d0S,a),cPred(d0S,xS),cPred(d0S,xS),cPred(d1U,yU))),
				"a", a
		);
		doTestP(
				mList("∀x·(a ∈ S ∨ x ∈ S) ∧ (∀y·a ∈ S ∨ y ∈ S)"),
				mList(
						cClause(cPred(d0S,a),cPred(d0S,xS)),
						cClause(cPred(d0S,a),cPred(d0S,xS))),
						"a",a
		);
		doTestP(
				mList("∀x·(a ∈ S ∨ x ∈ S) ∨ (∀y·a ∈ S ∨ y ∈ S)"),
				mList(
						cClause(cPred(d0S, a),cPred(d0S,xS),cPred(d0S, a),cPred(d0S,yS))),
						"a",a
		);
		doTestP(
				mList("∀x·a ∈ S ∨ x ∈ S ∨ (∀y·a ∈ S ∨ y ∈ U)"),
				mList(
						cClause(cPred(d0S, a),cPred(d0S,xS),cPred(d0S, a),cPred(d1U,yU))),
						"a",a
		);
		// if the formula is universally quantified, do not labelize
		doTestP(
				mList("∀x·a ∈ S ∨ b ∈ S ∨ x ∈ S"),
				mList(
						cClause(cPred(d0S, a),cPred(d0S, b),cPred(d0S,xS))),
						"a",a,
						"b",b
		);
		doTestP(
				mList("∀x·x ∈ S ∨ x ∈ S"),
				mList(
						cClause(cPred(d0S,xS),cPred(d0S,xS)))
		);
		doTestP(
				mList("∀x·¬(¬x ∈ S ∨ ¬x ∈ S)"),
				mList(cClause(cPred(d0S,xS)),cClause(cPred(d0S,xS)))
		);
		doTestP(
				mList("∀x·x ∈ S ∧ x ∈ S"),
				mList(cClause(cPred(d0S,xS)),cClause(cPred(d0S,xS)))
		);
//		doTestP(
//		mList("(∀x·x + 1 ∈ N)", "n + 1 ∈ N"),
//		noLit
////		mList(cPred(0,cPlus(xInt,one)),cPred(0, cPlus(n,one)))
//		);
		doTestP(
				mList("(∀x·x ∈ S)"),
				mList(cClause(cPred(d0S,xS)))
		);
		doTestP(
				mList("(∃x·x ∈ S)"),
				mList(cClause(cPred(d0S,cELocVar(0,Ssort))))
		);
		doTestP(
				mList("¬(∀x·x ∈ S)"),
				mList(cClause(cNotPred(d0S,cELocVar(0,Ssort))))
		);
		doTestP(
				mList("¬(∃x·x ∈ S)"),
				mList(cClause(cNotPred(d0S,xS)))
		);
		doTestP(
				mList("¬(∃x·x ∈ S) ∨ (∃x·x ∈ S)"),
				mList(
						cClause(cPred(d0S,cELocVar(0,Ssort)),cNotPred(d0S,yS)))
		);
		doTestP(
				mList("(∀x·∃y·x ↦ y ∈ T)"),
				mList(cClause(cPred(d0ZZ,xInt,cELocVar(1,NAT))))
		);
		doTestP(
				mList("(∃x·∀y·x ↦ y ∈ T)"),
				mList(cClause(cPred(d1Z, cELocVar(0,NAT))),
						cClause(cNotPred(d1Z,xInt),cPred(d0ZZ,xInt,yInt)))
		);
		doTestP(
				mList("(∀x·∃y·∀z·x ↦ y ↦ z ∈ TT)"),
				mList(cClause(cPred(d1ZZ,xInt,cELocVar(1,NAT))),
						cClause(cNotPred(d1ZZ,xInt,yInt),cPred(d0ZZZ,xInt,yInt,zInt)))
		);
		doTestP(
				mList("∃x·∀y·∃z·x ↦ y ↦ z ∈ TT"),
				mList(cClause(cPred(d2Z,cELocVar(0,NAT))),
//						cClause(cNotPred(2,xInt),cPred(1,xInt,yInt)),
						cClause(cNotPred(d2Z,xInt),cPred(d0ZZZ,xInt,yInt,cELocVar(2,NAT))))
		);

		doTestP(
				mList("¬(∃x·∀y·¬(x ↦ y ∈ T))"),
				mList(cClause(cPred(d0ZZ,xInt,cELocVar(1,NAT))))
		);
		doTestP(
				mList("¬(∀x·∃y·x ↦ y ∈ T)"),
				mList(cClause(cPred(d1Z,cELocVar(0,NAT))),
						cClause(cNotPred(d1Z,xInt),cNotPred(d0ZZ,xInt,yInt)))
		);
		doTestP(
				mList("¬(∃x·∀y·∃z·(x ↦ y) ↦ z ∈ M)"),
				mList(cClause(cPred(d1BA,xB,cELocVar(0,Asort))),
						cClause(cNotPred(d1BA,xB,yA),cNotPred(d0BAA,xB,yA,zA)))
		);
		doTestP(
				mList("(∀x·x ∈ S) ∨ (∀x·x ∈ S)"),
				mList(
						cClause(cPred(d0S,xS),cPred(d0S,yS)))
		);
		doTestP(
				mList("(∃y·∀x·x ↦ y ∈ T) ∨ (∃y·∀x·x ↦ y ∈ T)"),
				mList(
						cClause(cPred(d1Z,cELocVar(0,NAT)),cPred(d1Z,cELocVar(1,NAT))),
						cClause(cNotPred(d1Z,xInt),cPred(d0ZZ,yInt,xInt)))
//						cClause(cPred(1,xInt),cNotPred(0,cELocVar(2,INT),xInt))
		);


		doTestP(
				mList("(∃y·∀x·x ↦ y ∈ T) ∨ (∀z·(∃y·∀x·x ↦ y ∈ T) ∨ z ∈ S)"),
				mList(
						cClause(cPred(d5S,xS),cPred(d3Z,cELocVar(2,NAT)),cPred(d1Z,cELocVar(3,NAT))),
						cClause(cNotPred(d3Z,xInt),cPred(d0ZZ,yInt,xInt)),
						cClause(cNotPred(d1Z,xInt),cPred(d0ZZ,yInt,xInt)))
		);

		doTestP(
				mList("(∀x·x ∈ S ∧ x ∈ SS)"),
				mList(cClause(cPred(d0SPS,xS,S)),cClause(cPred(d0SPS,xS,SS))),
				"S",S,
				"SS",SS
		);

		doTestP(
				mList("∀x·x ∈ N ⇔ (∀y·y ∈ N ⇔ x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(d0Z,xInt),cPred(d2ZZ,cFLocVar(1,NAT),xInt)),
						cEqClause(cPred(d2ZZ,xInt,yInt),cPred(d0Z,xInt),cPred(d1ZZ,yInt,xInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ⇔ ¬(∀y·y ∈ N ⇔ x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(d0Z,xInt),cNotPred(d2ZZ,cELocVar(1,NAT),xInt)),
						cEqClause(cPred(d2ZZ,xInt,yInt),cPred(d0Z,xInt),cPred(d1ZZ,yInt,xInt))
				)
		);
		doTestP(
				mList("∀x·x ∈ N ⇔ ¬(∃y·y ∈ N ⇔ x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(d0Z,xInt),cNotPred(d2ZZ,cFLocVar(1,NAT),xInt)),
						cEqClause(cPred(d2ZZ,xInt,yInt),cPred(d0Z,xInt),cPred(d1ZZ,yInt,xInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ∨ (∀y·y ∈ N ∨ x ↦ y ∈ T)"),
				mList(
						cClause(cPred(d0Z,xInt),cPred(d0Z,yInt),cPred(d1ZZ,xInt,yInt)))
		);
		doTestP(
				mList("∀x·x ∈ N ∨ ¬(∀y·y ∈ N ∨ x ↦ y ∈ T)"),
				mList(
						cClause(cPred(d0Z,xInt),cNotPred(d2ZZ,cELocVar(1,NAT),xInt)),
						cClause(cPred(d2ZZ,xInt,yInt),cNotPred(d0Z,xInt)),
						cClause(cPred(d2ZZ,xInt,yInt),cNotPred(d1ZZ,yInt,xInt))

//						cClause(cNotPred(2,xInt,yInt),cPred(0,xInt),cPred(1,yInt,xInt))
				)
		);

		doTestP(
				// Rubin Exercise 8.A(18), p174
				mList(	"∀x,y,z·x ↦ y ∈ T ∧ y ↦ z ∈ T ⇒ x ↦ z ∈ T",
						"∀x,y·x ↦ y ∈ T ⇒ y ↦ x ∈ T",
				"¬((∀x·∃y·x ↦ y ∈ T) ⇒ (∀x·x ↦ x ∈ T))"),
				mList(	cClause(cNotPred(d0ZZ,evar0N,evar0N)),
						cClause(cPred(d0ZZ,xInt,cELocVar(1,NAT))),
						cClause(cPred(d0ZZ,xInt,yInt),cNotPred(d0ZZ,xInt,zInt),cNotPred(d0ZZ,zInt,yInt)),
						cClause(cPred(d0ZZ,xInt,yInt),cNotPred(d0ZZ,yInt,xInt)))
		);
	}

    @Test
	public void testQuantifier() {
		doTestP(
				mList("¬(A=TRUE⇒(∀x·∃y·x∈P∧y∈Q))"),
				mList(	cClause(cProp(6)),
						cClause(cNotPred(d3B,cELocVar(0,Bsort))),
						cClause(cNotPred(d2BA,xB,yA),cNotPred(d0B,xB),cNotPred(d1A,yA)),
						cClause(cPred(d3B,xB),cPred(d2BA,xB,yA))
				)
		);
		doTestP(
				mList("(A=TRUE∧¬(∃x·∀y·x∈P∧y∈Q))"),
				mList(	cClause(cProp(6)),
						cClause(cPred(d2BA,xB,cELocVar(0,Asort))),
						cClause(cNotPred(d2BA,xB,yA),cNotPred(d0B,xB),cNotPred(d1A,yA))
				)
		);
		doTestP(
				mList("(∀x·∃y·x ∈ S ∧ y ∈ S)"),
				mList(	cClause(cNotPred(d1SS,xS,cELocVar(0,Ssort))),
						cClause(cPred(d1SS,xS,yS),cPred(d0S,xS)),
						cClause(cPred(d1SS,xS,yS),cPred(d0S,yS))
				)
		);
	}

    @Test
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
						cEqClause(cPred(d0S, a),cPred(d0S, b))),
				"a",a,
				"b",b
		);
		doTestP(
				mList("∀x,y·x ∈ S ⇔ y ∈ S"),
				mList(
						cEqClause(cPred(d0S,xS),cPred(d0S,yS)))
		);
		doTestP(
				mList("a ∈ S ⇔ ¬(a ∈ S)"),
				mList(
						cEqClause(cNotProp(0),cProp(0)))
		);
		doTestP(
				mList("∀y·y ∈ N ⇔ (∀x·x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(d0Z,xInt),cPred(d1ZZ,cFLocVar(1,NAT),xInt)))
		);
		doTestP(
				mList("∀y·(∀x·x ↦ y ∈ T) ⇔ y ∈ N"),
				mList(
						cEqClause(cPred(d2Z,xInt),cPred(d0ZZ,cFLocVar(0,NAT),xInt)))
		);
		doTestP(
				mList("∀y·y ∈ N ⇔ (∃x·x ↦ y ∈ T)"),
				mList(
						cEqClause(cPred(d0Z,xInt),cPred(d1ZZ,cELocVar(1,NAT),xInt)))
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
				mList(cClause(cPred(d1S,cELocVar(0,Ssort))),
						cEqClause(cPred(d1S,xS),cPred(d0S,xS),cPred(d0S,xS)))
		);
		doTestP(
				mList("¬(∀x·¬(x ∈ S ⇔ x ∈ S))"),
				mList(cClause(cPred(d1S,cELocVar(0,Ssort))),
						cEqClause(cPred(d1S,xS),cPred(d0S,xS),cPred(d0S,xS)))
		);
		doTestP(
				mList("¬(∀x·x ∈ S ⇔ x ∈ S)"),
				mList(cClause(cNotPred(d1S,cELocVar(0,Ssort))),
						cEqClause(cPred(d1S,xS),cPred(d0S,xS),cPred(d0S,xS)))
		);
		doTestP(
				mList("¬(∃x·x ∈ S) ⇔ (∃x·x ∈ S)"),
				mList(
						cEqClause(cPred(d0S, cELocVar(0,Ssort)),cNotPred(d0S,cFLocVar(1,Ssort))))
		);
		doTestP(
				mList("∀y·¬(∃x·x ∈ N) ⇔ (∃x·x ↦ y ∈ T ∨ y ∈ N)"),
				mList(
						cEqClause(cPred(d3ZZ,xInt, cELocVar(1,NAT)),cNotPred(d0Z,cFLocVar(3,NAT))),
						cClause(cNotPred(d3ZZ,xInt,yInt),cPred(d0Z,xInt),cPred(d2ZZ,yInt,xInt)),
						cClause(cPred(d3ZZ,xInt,yInt),cNotPred(d0Z,xInt)),
						cClause(cPred(d3ZZ,xInt,yInt),cNotPred(d2ZZ,yInt,xInt))
				)
		);


		/* MIXED */
		doTestP(
				mList("a ∈ S ⇔ (a ∈ S ∨ a ∈ S)"),
				mList(
						cEqClause(cProp(0),cProp(1)),
						cClause(cNotProp(1),cProp(0),cProp(0)),
						cClause(cProp(1),cNotProp(0)),
						cClause(cProp(1),cNotProp(0))
				)
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
						cClause(cNotProp(2),cProp(0),cProp(1)),
						cClause(cProp(2),cNotProp(0)),
						cClause(cProp(2),cNotProp(1)),
						cClause(cProp(2),cNotProp(0)),
						cClause(cProp(2),cNotProp(1))
				)
		);

		doTestP(
				mList("a ∈ S ⇔ (d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				mList(
						cEqClause(cProp(0),cProp(4)),
						cClause(cNotProp(4),cProp(1),cProp(3)),
						cClause(cProp(4),cNotProp(1)),
						cClause(cProp(4),cNotProp(3)),
						cEqClause(cProp(3),cProp(2),cProp(2))
				)
		);
		doTestP(
				mList("a ∈ S ⇔ (d ∈ U ∨ ¬(c ∈ P ⇔ c ∈ P))"),
				mList(
						cEqClause(cProp(0),cProp(4)),
						cClause(cNotProp(4),cProp(1),cNotProp(3)),
						cClause(cProp(4),cNotProp(1)),
						cClause(cProp(4),cProp(3)),
						cEqClause(cProp(3),cProp(2),cProp(2))
				)
		);
		doTestP(
				mList("a ∈ S ⇔ ¬(d ∈ U ∨ (c ∈ P ⇔ c ∈ P))"),
				mList(
						cEqClause(cNotProp(0),cProp(4)),
						cClause(cNotProp(4),cProp(1),cProp(3)),
						cClause(cProp(4),cNotProp(1)),
						cClause(cProp(4),cNotProp(3)),
						cEqClause(cProp(3),cProp(2),cProp(2))
				)
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
						cClause(cProp(3),cProp(2))
				)
		);

		doTestP(
				mList("a ∈ S ⇔ b ∈ S","a ∈ S ⇔ c ∈ S"),
				mList(
						cEqClause(cPred(d0S,a),cPred(d0S,b)),
						cEqClause(cPred(d0S,a),cPred(d0S,cS))),
				"a",a,
				"b",b,
				"c",cS
		);
	}

    @Test
	public void testArithmetic() {
		doTestP(
				mList("∃x· x = n + 1 ∧ x ∈ N"),
				mList(cClause(cNotPred(d1Z,cELocVar(0,NAT))),
						cClause(cPred(d1Z,xInt),cPred(d0Z,xInt)),
						cClause(cPred(d1Z,xInt),cAEqual(xInt, cPlus(n,one)))
//						cClause(cNotPred(1,xInt),cNotPred(0,xInt),cANEqual(xInt, cPlus(n,one)))
				),
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
				mList(cClause(cPred(d0Z,cELocVar(0,NAT))),
						cClause(cNotPred(d0Z,xInt),cLess(one,xInt),cLE(xInt,one))

//						cClause(cPred(0,xInt),cLE(xInt,one)),
//						cClause(cPred(0,xInt),cLess(one,xInt))
						),
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

    @Test
	public void testBool() {
		doTestP(
				mList("b = TRUE"),
				mList(cClause(cProp(0)))
		);
		doTestP(
				mList("¬(b = TRUE)"),
				mList(cClause(cNotProp(0)))
		);
		doTestP(
				mList("b = TRUE","b = TRUE"),
				mList(cClause(cProp(0)),cClause(cProp(0)))
		);
		doTestP(
				mList("b = TRUE","¬(b = TRUE)"),
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
						cClause(cEqual(cS, eS))
				),
				mTypeEnvironment("c", ty_S),
				"b", bbool,
				"c", cS,
				"e", eS
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
						cClause(cPred(d0S,a),cProp(2)),
						cClause(cPred(d0S,b),cProp(3))),
					"a",a,
					"b",b
		);
		doTestP(
				mList("A = TRUE ∨ a ∈ S","B = TRUE ∨ b ∈ S","A = B"),
				mList(	cClause(cEqual(Abool,Bbool)),
						cClause(cPred(d0S,a),cEqual(Abool,TRUE)),
						cClause(cPred(d0S,b),cEqual(Bbool,TRUE))),
				"A", Abool,
				"B", Bbool,
				"a",a,
				"b",b,
				"TRUE",TRUE
		);
	}

    @Test
	public void testGoal() {
		doTestG(
				mList("∀x,y·x ↦ y ∈ V ∨ (∀z·(x ↦ y) ↦ z ∈ VV) ∨ (∀z·(x ↦ y) ↦ z ∈ VV)"),
				mList(	cClause(cNotPred(d3AB, cELocVar(0,Asort),cELocVar(1,Bsort))),
						cClause(cPred(d3AB, xA, yB),cNotPred(d1ABC,xA,yB,cELocVar(3,Csort))),
						cClause(cPred(d3AB, xA, yB),cNotPred(d1ABC,xA,yB,cELocVar(3,Csort))),
						cClause(cPred(d3AB, xA, yB),cNotPred(d0AB,xA,yB))
				)
		);
		doTestG(
				mList("∀x,y·x ↦ y ∈ T ⇒ y ↦ x ∈ T"),
				mList(	cClause(cNotPred(d1ZZ,cELocVar(0,NAT),cELocVar(1,NAT))),
						cClause(cPred(d1ZZ,xInt,yInt),cNotPred(d0ZZ,xInt,yInt)),
						cClause(cPred(d1ZZ,xInt,yInt),cPred(d0ZZ,yInt,xInt))

//						cClause(cNotPred(1,xInt,yInt),cPred(0,xInt,yInt),cNotPred(0,yInt,xInt))
				)
		);
		doTestG(
				mList(
				"(∀x·∃y·x ↦ y ∈ T) ⇒ (∀x·x ↦ x ∈ T)"),
				mList(	cClause(cNotPred(d0ZZ,evar0N,evar0N)),
						cClause(cPred(d0ZZ,xInt,cELocVar(1,NAT))))
		);
		doTestG(
				mList(
						"((∀x·x∈R)⇒(∃y·¬(y∈Q)))⇒((∀x·x∈Q)⇒(∃y·¬(y∈R)))"
				),
				mList(cClause(cPred(d0APA, xA, Q)),cClause(cPred(d0APA, xA, R)),
						cClause(cNotPred(d0APA, cELocVar(0,Asort), R),cNotPred(d0APA, cELocVar(1,Asort), Q))),
				"Q",Q,
				"R",R
		);

		doTestG(
				mList("∀y·∃x·x ∈ N ∧ x ↦ y ∈ T"),
				mList(cClause(cPred(d3Z,cELocVar(1,NAT))),
						cClause(cNotPred(d3Z,xInt),cNotPred(d0Z,yInt),cNotPred(d1ZZ,yInt,xInt))

//						cClause(cPred(2,xInt,yInt),cPred(1,xInt,yInt)),
//						cClause(cPred(2,xInt,yInt),cPred(0,xInt))
				)
		);
		doTestG(
				mList("∀y·¬(∀x·¬(x ∈ N ∧ x ↦ y ∈ T))"),
				mList(cClause(cPred(d3Z,cELocVar(1,NAT))),
						cClause(cNotPred(d3Z,xInt),cNotPred(d0Z,yInt),cNotPred(d1ZZ,yInt,xInt))

//						cClause(cPred(2,xInt,yInt),cPred(1,xInt,yInt)),
//						cClause(cPred(2,xInt,yInt),cPred(0,xInt))
				)
		);

		doTestG(
				mList("∃x·∀y·x ∈ N ⇒ x ↦ y ∈ T"),
				mList(cClause(cNotPred(d2ZZ,xInt,cELocVar(1,NAT))),
						cClause(cPred(d2ZZ,xInt,yInt),cPred(d0Z,xInt)),
						cClause(cPred(d2ZZ,xInt,yInt),cNotPred(d1ZZ,xInt,yInt))

//						cClause(cNotPred(2,xInt,yInt),cNotPred(0,xInt),cPred(1,xInt,yInt))
				)
		);
		doTestG(
				mList("¬(∀x·¬(∀y·x ∈ N ⇒ x ↦ y ∈ T))"),
				mList(cClause(cNotPred(d2ZZ,xInt,cELocVar(1,NAT))),
						cClause(cPred(d2ZZ,xInt,yInt),cPred(d0Z,xInt)),
						cClause(cPred(d2ZZ,xInt,yInt),cNotPred(d1ZZ,xInt,yInt))

//						cClause(cNotPred(2,xInt,yInt),cNotPred(0,xInt),cPred(1,xInt,yInt))
				)
		);
	}

    @Test
	public void testEquivalence2() {
		doTestP(
				mList("A=TRUE∨B=TRUE⇔A=TRUE"),
				mList(
					cEqClause(cProp(2),cProp(0)),
					cClause(cNotProp(0),cProp(2),cProp(3)),
					cClause(cProp(0),cNotProp(2)),
					cClause(cProp(0),cNotProp(3))
				),
				"A",Abool,
				"B",Bbool
		);
		doTestP(
				mList("(∀x,y·x ∈ S ∨ y ∈ S)⇔A=TRUE"),
				mList(
					cEqClause(cProp(4),cPred(d1SS,cFLocVar(0,Ssort),cFLocVar(1,Ssort))),
					cClause(cNotPred(d1SS,xS,yS),cPred(d0S,xS),cPred(d0S,yS)),
					cClause(cPred(d1SS,xS,yS),cNotPred(d0S,xS)),
					cClause(cPred(d1SS,xS,yS),cNotPred(d0S,yS))
				),
				"A",Abool,
				"B",Bbool
		);
		doTestP(
				mList("(∃x·∀y·x ↦ y ∈ AA)⇔A=TRUE"),
				mList(
					cEqClause(cProp(4),cPred(d1S,cELocVar(0,Ssort))),
					cClause(cNotPred(d1S,xS),cPred(d0SS,xS,yS)),
					cClause(cPred(d1S,xS),cNotPred(d0SS,xS,cELocVar(1,Ssort)))
				),
				"A",Abool,
				"B",Bbool
		);
	}

    @Test
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
				mList(cClause(cPred(d0A,xA)),cClause(cPred(d2B,xB))),
				constants
		);
		doTestP(
				mList("∀x·x ∈ Q ∨ (∃y·x ↦ y ∈ V ∨ x ↦ y ∈ W) ∨ (∃y·x ↦ y ∈ X ∨ x ↦ y ∈ Y)"),
				mList(
						cClause(cNotPred(d5AC, xA, yC), cPred(d4ACPAC, xA, yC, X), cPred(d4ACPAC,xA,yC,Y)),
						cClause(cNotPred(d2AB, xA, yB), cPred(d1ABPAB, xA, yB, V), cPred(d1ABPAB,xA,yB,W)),
						cClause(cPred(d0A,xA),cPred(d5AC,xA,cELocVar(0,Csort)),cPred(d2AB,xA,cELocVar(1,Bsort)))

//						cClause(cPred(5, xA, yC), cNotPred(4, xA, yC, X)),
//						cClause(cPred(5, xA, yC), cNotPred(4, xA, yC, Y)),
//						cClause(cPred(2, xA, yB), cNotPred(1, xA, yB, V)),
//						cClause(cPred(2, xA, yB), cNotPred(1, xA, yB, W))
				),
				constants
		);
	}

    @Test
	public void testHypotheses() {
			doTestG(
					mList("b ∈ S", "a ∈ C"),
					mList(cClause(cNotPred(d0SPS,b,S)),cClause(cNotPred(d0SPS,a,C))),
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
					mList(cClause(cNotPred(d0S,a)),cClause(cNotPred(d0S,b))),
					"a",a,
					"b",b
			);
	}
	
    @Test
	public void testExistentialBUG() {
		doTestP(
			mList("∃x·x∈A∧¬(∃y·y∈B∧x ↦ y∈AA)"),
			mList(	cClause(cNotPred(d4S,cELocVar(0,Ssort))),
					cClause(cPred(d4S,xS),cPred(d0SPS,xS,A)),
					cClause(cPred(d4S,xS),cNotPred(d0SPS,yS,B),cNotPred(d1SS,xS,yS))),
			"A",A,
			"B",B
		);
		doTestP(
			mList("¬(∃x·x∈A∧¬(∃y·y∈B∧x ↦ y∈AA))"),
			mList(	cClause(cNotPred(d0SPS,xS,A),cNotPred(d2SS,cELocVar(0,Ssort),xS)),
					cClause(cPred(d2SS,xS,yS),cPred(d0SPS,xS,B)),
					cClause(cPred(d2SS,xS,yS),cPred(d1SS,yS,xS))
			),
			"A",A,
			"B",B
		);
		doTestG(
			mList("∃x·x∈A∧¬(∃y·y∈B∧x ↦ y∈AA)"),
			mList(	cClause(cNotPred(d0SPS,xS,A),cNotPred(d2SS,cELocVar(0,Ssort),xS)),
					cClause(cPred(d2SS,xS,yS),cPred(d0SPS,xS,B)),
					cClause(cPred(d2SS,xS,yS),cPred(d1SS,yS,xS))
			),
			"A",A,
			"B",B
		);
		doTestG(
			mList("¬(∃x·x∈A∧¬(∃y·y∈B∧x ↦ y∈AA))"),
			mList(	cClause(cNotPred(d4S,cELocVar(0,Ssort))),
					cClause(cPred(d4S,xS),cPred(d0SPS,xS,A)),
					cClause(cPred(d4S,xS),cNotPred(d0SPS,yS,B),cNotPred(d1SS,xS,yS))),
			"A",A,
			"B",B
		);
		doTestP(
			mList("∃x·x∈A∧¬(∃y·x ↦ y∈AA∧(∃z·x ↦ z∈AA))"),
			mList(	cClause(cNotPred(d5S,cELocVar(0,Ssort))),
					cClause(cPred(d5S,xS),cPred(d0S,xS)),
					cClause(cPred(d5S,xS),cNotPred(d1SS,xS,yS),cNotPred(d1SS,xS,zS))
			),
			"A",A,
			"B",B
		);
		doTestP(
			mList("¬(∃x·x∈A∧¬(∃y·x ↦ y∈AA∧(∃z·x ↦ z∈AA)))"),
			mList(	cClause(cNotPred(d0S,xS),cNotPred(d3SS,xS,cELocVar(0,Ssort))),
					cClause(cPred(d3SS,xS,yS),cPred(d1SS,xS,yS)),
					cClause(cPred(d3SS,xS,yS),cPred(d1SS,xS,cELocVar(1,Ssort)))
			),
			"A",A,
			"B",B
		);
		doTestP(
			mList("∀x·(∀y·(∃z·z∈x∧z ↦ y∈AA)∨y∈x)⇒(∀y·¬y∈x)"),
			mList(	cClause(cNotPred(d0SPS,yS,xPS),cNotPred(d4SPS,cELocVar(0,Ssort),xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d0SPS,yS,xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d0SPS,zS,xPS),cNotPred(d1SS,zS,yS))
			)
		);
		doTestP(
			mList("∀x·(∀y·(∃z·z∈x∧z ↦ y∈AA)∧y∈x)⇒(∀y·¬y∈x)"),
			mList(	cClause(cNotPred(d0SPS,yS,xPS),cPred(d4SPS,cELocVar(0,Ssort),xPS)),
					cClause(cNotPred(d4SPS,yS,xPS),cNotPred(d0SPS,yS,xPS),cNotPred(d0SPS,zS,xPS),cNotPred(d1SS,zS,yS))
			)
		);
		doTestP(
			mList("∀x·(∀y·(∃z·z∈x∧z ↦ y∈AA)⇒y∈x)⇒(∀y·¬y∈x)"),
			mList( 	cClause(cNotPred(d0SPS,yS,xPS),cNotPred(d4SPS,cELocVar(0,Ssort),xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d0SPS,yS,xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d2SPSS,cELocVar(1,Ssort),xPS,yS)),
					cClause(cPred(d2SPSS,yS,xPS,zS),cPred(d0SPS,yS,xPS)),
					cClause(cPred(d2SPSS,yS,xPS,zS),cPred(d1SS,yS,zS))
			)
		);
		doTestP(
			mList("∀x·(∀y·¬(∃z·z∈x∧z ↦ y∈AA)∨y∈x)⇒(∀y·¬y∈x)"),
			mList(	cClause(cNotPred(d0SPS,yS,xPS),cNotPred(d4SPS,cELocVar(0,Ssort),xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d0SPS,yS,xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d2SPSS,cELocVar(1,Ssort),xPS,yS)),
					cClause(cPred(d2SPSS,yS,xPS,zS),cPred(d0SPS,yS,xPS)),
					cClause(cPred(d2SPSS,yS,xPS,zS),cPred(d1SS,yS,zS))
			)
		);
		doTestP(
			mList("∀x·(∀y·y∈x⇒(∃z·z∈x∧z ↦ y∈AA))⇒(∀y·¬y∈x)"),
			mList(	cClause(cNotPred(d0SPS,yS,xPS),cNotPred(d4SPS,cELocVar(0,Ssort),xPS)),
					cClause(cPred(d4SPS,yS,xPS),cPred(d0SPS,yS,xPS)),
					cClause(cPred(d4SPS,yS,xPS),cNotPred(d0SPS,zS,xPS),cNotPred(d1SS,zS,yS))
			)
		);
		doTestP(
			mList("∀x0·x0∈S⇒(∃y·y∈S∧y ↦ x∈AA)"),
			mList(	cClause(cNotPred(d0S,xS),cNotPred(d2S,cELocVar(0,Ssort))),
					cClause(cPred(d2S,xS),cPred(d0S,xS)),
					cClause(cPred(d2S,xS),cPred(d1S,xS))
			)
		);
	}

    @Test
	public void testTypeInformation() {
		// Shall create a predicate "∀x·x ∈ S"
		doTestT(
			mList("∀x·a ∈ x", "∀y·b ∈ y"),
			mList(
					cClause(cPred(d0SPS, a, xPS)),
					cClause(cPred(d0SPS, b, xPS)),
					cClause(cPred(d0SPS, xS, S))
			),
			mTypeEnvironment("a", ty_S, "b", ty_S),
			"a", a,
			"b", b,
			"S", S
		);
		// Shall also create a predicate "∀x·x ∈ S"
		doTestT(
			mList("a ∈ A", "b ∈ B"),
			mList(
					cClause(cPred(d0SPS, a, A)),
					cClause(cPred(d0SPS, b, B)),
					cClause(cPred(d0SPS, xS, S))
			),
			mTypeEnvironment("a", ty_S, "b", ty_S),
			"a", a,
			"b", b,
			"A", A,
			"B", B,
			"S", S
		);
		// Shall not create a predicate "∀x·x ∈ S"
		doTestT(
			mList("a ∈ A", "b ∈ A"),
			mList(
					cClause(cPred(d0S, a)),
					cClause(cPred(d0S, b))
			),
			"a", a,
			"b", b
		);

		// Shall not create a type predicate for the label
		final PredicateTable table = new PredicateTable();
		PredicateLiteralDescriptor L1SS = table.newDescriptor(1, 2, 4, true,
				false, mList(Ssort, Ssort));
		doTestT(
			mList("∀x·∃y·x ∈ A ⇒ y ∈ B", "∀y·∃z·y ∈ A ⇒ z ∈ B"),
			mList(
					cClause(cPred(L1SS,cELocVar(0,Ssort), xS)),
					cClause(cPred(L1SS,cELocVar(1,Ssort), xS)),
					cClause(cNotPred(L1SS,xS,yS),cPred(d0SPS,xS,B),cNotPred(d0SPS,yS,A)),
					cClause(cNotPred(L1SS,xS,yS),cPred(d0SPS,xS,B),cNotPred(d0SPS,yS,A)),
					cClause(cPred(d0SPS, xS, S))
			),
			mTypeEnvironment("A", POW(ty_S), "B", POW(ty_S)),
			"A", A,
			"B", B,
			"S", S
		);

		// Shall not create a type predicate for the literal
		final Constant AA = cCons("AA", PSS);
		final Constant BB = cCons("BB", PSS);
		doTestT(
			mList("a ↦ a ∈ AA", "b ↦ b ∈ BB"),
			mList(
					cClause(cPred(d0SPSS, a, AA)),
					cClause(cPred(d0SPSS, b, BB))
			),
			mTypeEnvironment("a", ty_S, "b", ty_S),
			"a", a,
			"b", b,
			"AA", AA,
			"BB", BB
		);
	}

	public void doTestP(List<String> strPredicate, Collection<Clause> clauses, Object... constants) {
		doTest(strPredicate, clauses, constants, false, false, env);
	}
	
	public void doTestP(List<String> strPredicate, Collection<Clause> clauses,
			ITypeEnvironment typenv, Object... constants) {
		doTest(strPredicate, clauses, constants, false, false, typenv);
	}
	
	public void doTestG(List<String> strPredicate, Collection<Clause> clauses, Object... constants) {
		doTest(strPredicate, clauses, constants, true, false, env);
	}
	
	public void doTestT(List<String> strPredicate, Collection<Clause> clauses, Object... constants) {
		doTest(strPredicate, clauses, constants, false, true, env);
	}
	
	public void doTestT(List<String> strPredicate, Collection<Clause> clauses,
			ITypeEnvironment typenv, Object... constants) {
		doTest(strPredicate, clauses, constants, false, true, typenv);
	}
	
	public void doTest(List<String> strPredicate, Collection<Clause> clauses,
			Object[] constants, boolean goal, boolean withTypeInfo,
			ITypeEnvironment typenv) {
		ClauseBuilder result = load(strPredicate, goal, withTypeInfo, typenv, constants);
		
		assertSameClauses(strPredicate, clauses, result.getClauses());
		assertEquals("Actual: "+result.getClauses()+"\nExpected: "+clauses,result.getClauses().size(), clauses.size());
//		assertSameClauses(strPredicate, literals, result.getLiterals());
	}
	
	private ClauseBuilder load(List<String> strPredicate, boolean goal,
			boolean withTypeInfo, ITypeEnvironment typenv, Object... constants) {
		final AbstractContext context = new AbstractContext();
		final ClauseBuilder cBuilder = new ClauseBuilder(null);
		ITypeEnvironment tmp = typenv.clone();
		
		for (String str : strPredicate) {
			final ITypeCheckResult res = getResult(str, context, tmp, goal);
			tmp.addAll(res.getInferredEnvironment());
		}
		
		VariableTable variableTable = getVariableTable(constants);
		cBuilder.loadClausesFromContext(context, variableTable);
		if (withTypeInfo) {
			cBuilder.buildPredicateTypeInformation(context);
		}
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
				message.append("Superfluous clause : "+clause.toString()+"\n");
			}
		}
		for (T clause : expected) {
			if (!actual.contains(clause)){
				message.append("Missing clause : "+clause.toString()+"\n");
			}
		}
		assertTrue("\n"+predicate+"\n"+message.toString(), message.length()==0);
	}
	
	private ITypeCheckResult getResult(String strPredicate, AbstractContext context, ITypeEnvironment types, boolean goal) {
		final Predicate predicate = Util.parsePredicate(strPredicate, types);
		final ITypeCheckResult result = predicate.typeCheck(types);
		assertTrue("TypeCheck failed for " + predicate, result.isSuccess());
		context.load(predicate, goal);
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
	
    @Test
	public void testNotSameVariable() {
		ClauseBuilder result = load(mList("(∀x·x ∈ S ∨ x ∈ S) ⇒ (∀x·x ∈ S ∨ x ∈ S )"),false,false,env);
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
