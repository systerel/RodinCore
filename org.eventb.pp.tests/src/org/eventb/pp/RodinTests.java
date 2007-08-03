package org.eventb.pp;

import static org.eventb.pp.Util.mList;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pp.PPResult.Result;

public class RodinTests extends AbstractPPTest {

	static FormulaFactory ff = FormulaFactory.getDefault();

	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("f", REL(ty_S, ty_T));
		env.addName("g", REL(ty_T, ty_U));

		env.addName("a", ty_U);
		// env.addName("SIG",
		// ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("B"),
		// ff.makeGivenType("S"))));
		// env.addName("fst",
		// ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("S"),
		// ff.makeGivenType("B"))));

		env.addName("A", POW(ty_S));

		env.addName("B", POW(ty_S));

		env.addName("k", POW(ty_S));

		// env.addName("r", REL(ty_S, ty_S));
		env.addName("R", POW(ty_T));
		env.addName("rtbl", REL(ty_S, ty_T));

		env.addName("U", POW(POW(ty_S)));

		env.addName("S", POW(ty_S));

		env.addName("q", POW(ty_T));
		env.addName("r", REL(ty_T, ty_T));
		env.addName("s", REL(ty_T, ty_T));

		env.addName("org", REL(ty_T, ty_S));
		env.addName("sit", REL(ty_T, ty_S));

		env.addName("M", POW(POW(ty_M)));
		env.addName("N", POW(ty_M));
	}

	private static class TestPair {
		ITypeEnvironment typeEnvironment = env;

		Set<Predicate> hypotheses;

		Predicate goal;

		public int timeout = -1;

		boolean result;

		public TestPair(List<String> typeEnvironment, Set<String> hypotheses,
				String goal, boolean result) {
			this.hypotheses = new LinkedHashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string)
						.getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = result;
			this.typeEnvironment = parseTypeEnvironment(typeEnvironment);
		}

		private ITypeEnvironment parseTypeEnvironment(
				List<String> typeEnvironment2) {
			ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
			for (int i = 0; i < typeEnvironment2.size(); i=i+2) {
				String name = typeEnvironment2.get(i);
				String type = typeEnvironment2.get(i+1);
				
				typeEnvironment.addName(name, ff.parseType(type).getParsedType());
			}
			return typeEnvironment;
		}

		public TestPair(ITypeEnvironment typeEnvironment,
				Set<String> hypotheses, String goal, boolean result) {
			this.hypotheses = new LinkedHashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string)
						.getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = result;
			this.typeEnvironment = typeEnvironment;
		}

		public TestPair(Set<String> hypotheses, String goal, boolean result) {
			this.hypotheses = new LinkedHashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string)
						.getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = result;
		}

		public TestPair(Set<String> hypotheses, String goal, boolean result,
				int timeout) {
			this.hypotheses = new LinkedHashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string)
						.getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = result;
			this.timeout = timeout;
		}

		void typeCheck() {
			ITypeEnvironment env = typeEnvironment.clone();
			for (Predicate pred : hypotheses) {
				typeCheck(pred, env);
			}
			typeCheck(goal, env);
		}

		private void typeCheck(Predicate predicate, ITypeEnvironment environment) {
			ITypeCheckResult result = predicate.typeCheck(environment);
			assertTrue(predicate + " " + result.toString(), result.isSuccess());
			environment.addAll(result.getInferredEnvironment());
		}
	}

	private static void doTest(List<String> typeEnvironment,
			Set<String> hypotheses, String goal, boolean result) {
		TestPair pair = new TestPair(typeEnvironment, hypotheses, goal, result);
		doTestHelper(pair);
	}
	
	private static void doTest(Set<String> hypotheses, String goal, boolean result, int timeout) {
		TestPair pair = new TestPair(hypotheses, goal, result, timeout);
		doTestHelper(pair);
	}
	
	private static void doTest(Set<String> hypotheses, String goal, boolean result) {
		TestPair pair = new TestPair(hypotheses, goal, result);
		doTestHelper(pair);
	}
	
	public void testCelebrity() {
		initDebug();
		
		doTest(
				mList(
				"Q","ℙ(ℤ)",
				"P","ℙ(ℤ)",
				"x","ℤ",
				"y","ℤ",
				"c","ℤ",
				"k","ℙ(ℤ×ℤ)"
				),
				 mSet(
				"c∈Q",
				"x∈Q",
				"y∈Q",
				"x ↦ y∈k",
				"k∈P ∖ {c} ↔ P"
				),"¬x=c",true);
	}

	public void testFailingExamples() {
		initDebug();
		
		doTest(
				mList(
				"I","ℙ(ℙ(E)×ℙ(E))",
				"O","ℙ(ℙ(E))",
				"K","ℙ(ℙ(E)×ℙ(E))",
				"C","ℙ(ℙ(E))",
				"N","ℙ(E×ℙ(ℙ(E)))",
				"B","ℙ(ℙ(E)×ℙ(E))",
				"E","ℙ(E)"
				),
				 mSet(
				"O∈ℙ(ℙ(E))",
				"∀S,T·S∈O∧T∈O⇒S∩T∈O",
				"∀S·S⊆O⇒union(S)∈O",
				"E∈O",
				"N∈E → ℙ(ℙ(E))",
				"∀x·N(x)={S·∃X·X∈O∧x∈X∧X⊆S ∣ S}",
				"∀S,T,x·S⊆T∧S∈N(x)⇒T∈N(x)",
				"∀S,T,x·S∈N(x)∧T∈N(x)⇒S∩T∈N(x)",
				"∀S,x·S∈N(x)⇒x∈S",
				"∀S,x·S∈N(x)⇒(∃T·T∈N(x)∧(∀y·y∈T⇒S∈N(y)))",
				"∀x·N(x)≠∅",
				"∀S,x·S∈O∧x∈S⇒S∈N(x)",
				"∀S·(∀x·x∈S⇒S∈N(x))⇒S∈O",
				"O={X·∀x·x∈X⇒X∈N(x) ∣ X}",
				"∀X,x·x∈X⇒x∈dom(N)∧N∈E ⇸ ℙ(ℙ(E))",
				"C={X·E ∖ X∈O ∣ X}",
				"∀S·S≠∅∧S⊆C⇒inter(S)∈C",
				"∀S,T·S∈C∧T∈C⇒S∪T∈C",
				"∅∈C",
				"E∈C",
				"I∈ℙ(E) → ℙ(E)",
				"∀X·I(X)={x·x∈X∧X∈N(x) ∣ x}",
				"K∈ℙ(E) → ℙ(E)",
				"∀X·K(X)={x·∀y·y∈N(x)⇒y∩X≠∅ ∣ x}",
				"B∈ℙ(E) → ℙ(E)",
				"∀X·B(X)=K(X)∩K(E ∖ X)",
				"∀X·I(X)=union({o·o∈O∧o⊆X ∣ o})",
				"∀X·X∈O⇔X=I(X)"
				),"∀X,Y·I(X∩Y)=I(X)∩I(Y)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↔ S",
				"ran(r)=S",
				"∀p·p⊆S∧p⊆r∼[p]⇒p=∅",
				"∀q·q⊆S∧S ∖ r∼[S ∖ q]⊆q⇒S⊆q"
				),"r∈S ⇸ S⇒(∀q·q⊆S∧S ∖ dom(r)⊆q∧r∼[q]⊆q⇒S⊆q)",true);
	}
	
//	public void testLoop() {
//		doTest(
//				mList(
//				"q","ℙ(S)",
//				"r","ℙ(S×S)"
//				),
//				 mSet(
//				"∀q·q⊆ran(r)∧ran(r) ∖ r∼[ran(r) ∖ q]⊆q⇒ran(r)⊆q",
//				"r∈ran(r) ⇸ ran(r)",
//				"r∼[q]⊆q",
//				"ran(r) ∖ dom(r)⊆q",
//				"q⊆ran(r)"
//				),"ran(r)⊆q",true);	
//	}
	
	
	public void testProfile() {
		initDebug();
		// !x!y!z (Rxy & Ryz -> Rxz),
		// !x Rxx,
		// !x!y#z (Rxz & Ryz)
		// |- !x!y (Rxy | Ryx);
		doTest(mSet("∀N,T·N∈M∧T∈M⇒(∃x·(∀x0·x0∈x⇔x0∈N∧x0∈T)∧x∈M)", "E∈M",
				"∀x·x∈ae⇒x∈N", "∀x·x∈N⇒x∈ae", "¬(∀x·x∈ae⇔x∈N)"), "ae=N", true);
		doTest(mSet("∀x·∀y·∀z·x↦y∈r ∧ y↦z∈r ⇒ x↦z∈r", "∀x·x↦x∈r",
				"∀x·∀y·∃z·x↦z∈r ∧ y↦z∈r"), "∀x·∀y·x↦y∈r ∨ y↦x∈r", false, 500);
		doTest(mSet("r∼[q]⊆q", "ran(r) ∖ dom(r)⊆q", "q⊆ran(r)",
				"q⊆ran(r) ∧ ran(r) ∖ r∼[ran(r) ∖ q]⊆q ⇒ ran(r)⊆q"), "ran(r)⊆q",
				true);
		doTest(mSet("r∼[q]⊆q", "ran(r) ∖ dom(r)⊆q", "q⊆ran(r)",
				"∀q·q⊆ran(r)∧ran(r) ∖ r∼[ran(r) ∖ q]⊆q⇒ran(r)⊆q"), "ran(r)⊆q",
				true);
	}
	
	public void testRubin() {
		initDebug();
		
		doTest(
				mList("A","ℙ(E)"),
				mSet(	"∀x·x∈A⇒x∈B",
						"∀y·y∈B⇒(∀x·x∈A)"
				),"(∀x·x∈B)⇔a∈B",true
		);
		
		doTest(mList("S","ℙ(E)","R","ℙ(E×E)"),mSet(
				"∃x·x∈P∧x ↦ a∈R",
				"a∈S",
				"∀x·x∈P∧¬(∃y·y∈Q∧x ↦ y∈R)⇒¬(∃z·z∈S∧x ↦ z∈R)"),
				"∃x,y·x∈P∧y∈Q∧x ↦ y∈R",true
		);
		
		doTest(mList("P","ℙ(E)"),new HashSet<String>(),"(∀x·x∈P⇔x∈Q)⇒((∀x·x∈P)⇔(∀x·x∈Q))",true);
	}
	
	public void testInjection() {
		initDebug();
		
//		doTest(mSet("r ∈ E ↣ E" , "s ∈ E ↣ E"), 
//				"r;s ∈ E ↣ E", true);
		
//		(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)
//		∧
//		(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)
//		∧
//		(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)",true);

		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)"+
					"∧(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)",true);

		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)"+
					"∧(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)"+
					"∧(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)",true);
		
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ↣ S", "s∈S ↣ S"),
				"r;s∈S ↣ S",true);
	}
	
	public void testFunction() {
		doTest(mSet("r ∈ E → E", "s ∈ E → E"), "r;s ∈ E → E", true);
	}
	
	public void testAllFunctionSameType() {
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ↣ S", "s∈S ↣ S"),
				"r;s∈S ↣ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ⤖ S", "s∈S ⤖ S"),
				"r;s∈S ⤖ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ↠ S", "s∈S ↠ S"),
				"r;s∈S ↠ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ⤔ S", "s∈S ⤔ S"),
				"r;s∈S ⤔ S",true);

		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ⤀ S", "s∈S ⤀ S"),
				"r;s∈S ⤀ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S → S", "s∈S → S"),
				"r;s∈S → S",true);
	}
	
	public void testSurjection() {
		initDebug();
		
//		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), "r;s ∈ E ↣ E", true);
		
		doTest(mSet("r ∈ E ↣ E" , "s ∈ E ↣ E"), 
				"r ∈ E ↣ E", true);
		
		// injection + total + fonction + definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"),
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x·x∈E⇒(∃x0·x ↦ x0∈r))" +
				"∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)∧(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);
		
		// injection 
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)", true);
		// total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x·x∈E⇒(∃x0·x ↦ x0∈r))", true);
		
		// fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);
		
		// definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);
		
		// injection + total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x·x∈E⇒(∃x0·x ↦ x0∈r))", true);
		
		// injection + total + fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x·x∈E⇒(∃x0·x ↦ x0∈r))∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);

		// injection + fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);

		// injection + fonction + definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)∧(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);

		// injection + definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);

		
		
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)", true);
		// total
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x·x∈E⇒(∃x0·x ↦ x0∈r))", true);
		
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);
		
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);
		
		// injection 
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)", true);
		// total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x·x∈E⇒(∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s))", true);
		
		// fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)", true);
		
		// definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)⇒x∈E∧x0∈E)", true);
		
		
		// injection total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)" +
				"∧(∀x·x∈E⇒(∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s))", true);
		
		// injection total fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)" +
				"∧(∀x·x∈E⇒(∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s))" +
				"∧(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)", true);
		
		// fails
		doTest(mSet("r ∈ E ↠ E", "s ∈ E ↠ E"), "r;s ∈ E ↠ E", true);
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), "r;s ∈ E ⤖ E", true);
		
		// works
		doTest(mSet("r ∈ E ⤔ E", "s ∈ E ⤔ E"), "r;s ∈ E ⤔ E", true);
		doTest(mSet("r ∈ E ⤀ E", "s ∈ E ⤀ E"), "r;s ∈ E ⤀ E", true);
	}

	public void testOverride() {
		initDebug();
		
		doTest(mList("C","ℙ(C)","D","ℙ(D)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
				"(∀x,x0,x1·((x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b))∧((x ↦ x1∈f∧¬x=c)∨(x=c∧x1=b))⇒x0=x1)", true);
		
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)⇒x∈C∧x0∈D)", true);
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x·x∈C⇒(∃x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)))", true);
//
//		
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)⇒x∈C∧x0∈D)" +
//				"∧(∀x,x0,x1·((x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b))∧((x ↦ x1∈f∧¬x=c)∨(x=c∧x1=b))⇒x0=x1)", true);
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)⇒x∈C∧x0∈D)" +
//				"∧(∀x·x∈C⇒(∃x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)))", true);
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0,x1·((x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b))∧((x ↦ x1∈f∧¬x=c)∨(x=c∧x1=b))⇒x0=x1)" +
//				"∧(∀x·x∈C⇒(∃x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)))", true);
		
		
//		doTest(mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(({c}⩤f)∪{c↦b}) ∈  C → D", true);	
	}
	
	public void testAll() {
		initDebug();
			// f: S-->T
			// a/:S
			// b/:T
			// |--
			// f<+{a|->b} : S\/{a} --> T\/{b}

		doTest(mSet("f ∈ C ↔ D", "c ∈ C", "b ∈ D"), 
				"(({c}⩤f)∪{c↦b}) ∈ C ↔ D",
				true);
// doTest(mSet(
//			 "f ∈ C ↔ D",
//			 "c ∈ C",
//			 "b ∈ D"
//			 ), "f{c↦b} ∈ C ↔ D",true
//			);

			// (!(x?$10,x?$9).(x?$10,x?$9: f and not(x?$10 = c) or (x?$10 = c
			// and x?$9 = d) => x?$10: C and x?$9: D)) and
			// !(x?$13,x?$12,x?$11).(x?$13,x?$12: f and not(x?$13 = c) or (x?$13
			// = c and x?$12 = d) and (x?$13,x?$11: f and not(x?$13 = c) or
			// (x?$13 = c and x?$11 = d)) => x?$12 = x?$11) and
			// !(x?$14).(x?$14: C => #(x?$15).(x?$14,x?$15: f and not(x?$14 = c)
			// or (x?$14 = c and x?$15 = d)))

			// doTest(mSet(
			// "f ∈ C → D",
			// "c ∈ C",
			// "b ∈ D"
			// ), "f{c↦b} ∈ C → D",true
			// );

			// doTest(mSet(
			// "f ∈ C → D",
			// "c ∉ C",
			// "b ∉ D"
			// ), "f{c↦b} ∈ C ∪ {c} → D ∪ {b}",true
			// );
			doTest(mSet("∃y·y = k ∧ y ∈ x"), "k∈x", true);

			doTest(mSet("X ⊆ B", "B ⊆ X"), "∀x·x∈X ⇔ x∈B", true);
			doTest(mSet("X ⊆ M", "M ⊆ X"), "M = X", true);
			doTest(mSet("X ⊆ B", "B ⊆ X"), "X = B", true);
			doTest(mSet("x ⊆ B"), "B ∖ (B ∖ x) = x", true);
			doTest(mSet("x ⊆ B"), "B ∖ (B ∖ x) = x", true);
			doTest(new HashSet<String>(), "S ∖ (S ∖ k) = k", true);
			// doTest(mSet(
			// "S ∖ (S ∖ k) ∈ x"
			// ),"k ∈ x",true
			// );

			// translation
			// doTest(new HashSet<String>(),
			// "((D=TRUE ⇔ E=TRUE) ⇔ F=TRUE) ⇔ (D=TRUE ⇔ (E=TRUE ⇔
			// F=TRUE))",true
			// );
			// doTest(mSet(
			// "c^2 ∈ C",
			// "c^2 ∉ C"
			// ),"⊥",false
			// );

			// r : a<->b
			// c <: a
			// |---
			// r[c]<:b
			// doTest(mSet(
			// "r ∈ d↔e",
			// "c ⊆ d"
			// ), "r[c] ⊆ e", true
			// );
			//			
			// // x|->y : r
			// // !x,y. y|->x : r => x : b
			// // |---
			// // y:b
			//			
			// doTest(mSet(
			// "x↦y ∈ r",
			// "∀x,y· y↦x ∈ r ⇒ x∈b"
			// ), "y∈b", true
			// );

			// doTest(mSet(
			// "∀x·x^2 ∈ C",
			// "∀x·x^2 ∉ C"
			// ),"⊥",false
			// );
			
			doTest(mSet("r ∈ E ↔ E"), "r ∈ E ↔ E", true);
			
			doTest(mSet("r ∈ E ↔ E", "s ∈ E ↔ E"), "r;s ⊆ E × E", true);
			doTest(mSet("r ∈ E ↔ E", "s ∈ E ↔ E"), "r;s ∈ E ↔ E", true);
			doTest(mSet("r ∈ E ⇸ E", "s ∈ E ⇸ E"), "r;s ∈ E ⇸ E", true);
			doTest(mSet("r ∈ E ↔ E", "s ∈ E ↔ E"), "r;s ∈ ℙ(E × E)", true);
			doTest(mSet("∀x,y·x ↦ y ∈ s ⇒ (x∈E ∧ y∈E)",
					"∀x,y·x ↦ y ∈ r ⇒ (x∈E ∧ y∈E)"),
					"∀x,y·(∃z·x ↦ z ∈ r ∧ z ↦ y ∈ r) ⇒ (x∈E ∧ y∈E)", true);


			// // requires adding set hypothesis
			doTest(mSet("A = S", "C ⊆ S", "A ∈ U"), "C ∪ A ∈ U", true);
			// // requires adding set membership hypothesis
			doTest(mSet("C ⊆ S", "S ∈ U"), "C ∪ S ∈ U", true);
			doTest(mSet("C ⊆ B", "B ∈ U"), "C ∪ B ∈ U", true);
			doTest(mSet("A ⊆ B", "B ⊆ C"), "A ⊆ C", true);
			// fails at the moment
			doTest(mSet("f ∈ S ⇸ T", "x ∉ dom(f)", "y ∈ T"),
					"f ∪ {x ↦ y} ∈ S ⇸ T", true);
			doTest(mSet("f ∈ S ⇸ T"), "f∼[C ∩ D] = f∼[C] ∩ f∼[D]", true);
			// fails when not generating negative labels
			doTest(mSet("f ∈ S ⇸ T"), "f∼[C ∖ D] = f∼[C] ∖ f∼[D]", true);
			doTest(mSet("f ∈ S ⤔ T"), "f[C ∩ D] = f[C] ∩ f[D]", true);
			// doTest(mSet(
			// "f ∈ S ↔ T"
			// ),"f∼[C ∩ D] = f∼[C] ∩ f∼[D]", false
			// );
			doTest(mSet("dap;org ⊆ sit", "sit(p)=org(d)",
			// "p ∈ dom(sit)", // unneeded
					// "d ∈ dom(org)", // unneeded
					"org ∈ D ⇸ L", "sit ∈ P → L"), "(dap ∪ {p↦d});org ⊆ sit",
					true);
			// doTest(mSet(
			// "dap;org ⊆ sit",
			// "sit(p)=org(d)",
			// // "p ∈ dom(sit)",
			// // "d ∈ dom(org)",
			// "org ∈ D ⇸ L",
			// "sit ∈ P → L"
			// ),"(dap  {p↦d});org ⊆ sit",true
			// );
			doTest(mSet("(A∪B)∩(A∪C)∈U"), "A∪(B∩C)∈U", true);
			// fails when instantiationcount = 1
			doTest(mSet("A∪B∈U", "(A∪B)∩(A∪C)∈U"), "A∪(B∩C)∈U", true);
			doTest(mSet("A∪B∈U", "A∪C∈U", "(A∪B)∩(A∪C)∈U"),
					"A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("A∪B∈U", "(A∪B)∩(A∪C)∈U"), "A∪(B∩C)=(A∪B)∩(A∪C)",
					true);
			doTest(mSet("A∪B∈U", "A∪C∈U", "(A∪B)∩(A∪C)∈U"),
					"A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("∅∉U", "A∪B∈U", "A∪C∈U", "(A∪B)∩(A∪C)∈U"),
					"A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("(A∪B)∩(A∪C)∈U"), "A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("(A∪B)∈U"), "A∈U", false);
			doTest(mSet("r ∈ ran(r)∖{x} → ran(r)", "r∼[q]⊆q", "x∈q"),
					"ran(r)∖r∼[ran(r)∖q]⊆q", true);
			doTest(mSet("A = G"), "G ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)", true);
			doTest(mSet("q ⊆ R"), "R ∖ q ⊆ R", true);
			doTest(
					mSet(
							"∀r·r∈R⇒nxt(r)∈rtbl∼[{r}] ∖ {lst(r)} ⤖ rtbl∼[{r}] ∖ {fst(r)}",
							"nxt∈R → (B ⤔ B)"),
					"∀r·r∈R⇒r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id(ℙ(B × B))∧r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id(ℙ(B × B))",
					true);
			doTest(mSet("R ⊆ C"), "r[R] ⊆ r[C]", true);
			doTest(mSet("a = c"), "a ∈ {c,d}", true);
			doTest(mSet("(∃x,y·f(x)=y ∧ g(y)=a)"), "(∃x·(g∘f)(x)=a)",
					true);
	// doTest(mSet("(∀x·(∃x0·x ↦ x0∈SIG)⇒(∃x0·x0 ↦ x∈fst))" +
	// "∧" +
	// "(∀x,x0,x1·x ↦ x0∈SIG∧x ↦ x1∈SIG⇒x0=x1)" +
	// "∧" +
	// "(∀x·(∃x0·x0 ↦ x∈fst)⇒(∃x0·x ↦ x0∈SIG))" +
	// "∧" +
	// "(∀x·∃x0·x0 ↦ x∈SIG)" +
	// "∧" +
	// "(∀x,x0,x1·x0 ↦ x∈SIG∧x1 ↦ x∈SIG⇒x0=x1)"),"⊥",false)
	}

	public void testTrueGoal() {
		doTest(new HashSet<String>(), "⊤", true);
	}

	public void testFalseHypothesis() {
		doTest(mSet("⊥"), "⊥", true);
	}

	
	private static void doTestHelper(TestPair test) {
		test.typeCheck();

		PPProof prover = new PPProof(test.hypotheses, test.goal);
		prover.translate();
		prover.load();
		prover.prove(test.timeout);
		PPResult result = prover.getResult();
		assertEquals(result.getResult() == Result.valid, test.result);
	}

	// public static void main(String[] args) {
	// RodinTests test = new RodinTests();
	// test.testAll();
	// }

}
