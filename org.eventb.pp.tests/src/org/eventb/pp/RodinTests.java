 package org.eventb.pp;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.pp.PPResult.Result;

public class RodinTests extends AbstractPPTest {
	
	private static class TestPair {
		Set<Predicate> hypotheses;
		Predicate goal;
		boolean result;
		
		public TestPair(Set<Predicate> hypotheses, Predicate goal, boolean result) {
			this.hypotheses = hypotheses;
			this.goal = goal;
			this.result = result;
		}
		
		public TestPair(Set<String> hypotheses, String goal, boolean result) {
			this.hypotheses = new HashSet<Predicate>();
			for (String string : hypotheses) {
				this.hypotheses.add(ff.parsePredicate(string).getParsedPredicate());
			}
			this.goal = ff.parsePredicate(goal).getParsedPredicate();
			this.result = result;
		}
	}
	
	static FormulaFactory ff = FormulaFactory.getDefault();
	private static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("f", REL(ty_S, ty_T));
		env.addName("g", REL(ty_T, ty_U));
		
		env.addName("a", ty_U);
//		env.addName("SIG", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("B"), ff.makeGivenType("S"))));
//		env.addName("fst", ff.makePowerSetType(ff.makeProductType(ff.makeGivenType("S"), ff.makeGivenType("B"))));
		
		env.addName("A", POW(ty_S));
		env.addName("B", POW(ty_S));
//		env.addName("r", REL(ty_S, ty_S));
		env.addName("R", POW(ty_T));
		env.addName("rtbl", REL(ty_S,ty_T));
		
		env.addName("S", POW(ty_S));
		
		env.addName("q", POW(ty_T));
		env.addName("r", REL(ty_T,ty_T));
		env.addName("s", REL(ty_T,ty_T));
		
		
		env.addName("org", REL(ty_T,ty_S));
		env.addName("sit", REL(ty_T,ty_S));
	}
	
	
	public void testAll() {
		initDebug();
		for (TestPair test : tests) {
			doTest(test);
		}
	}
	
	
	TestPair[] tests = new TestPair[]{
			// translation
//			new TestPair(new HashSet<String>(),
//					"((D=TRUE ⇔ E=TRUE) ⇔ F=TRUE) ⇔ (D=TRUE ⇔ (E=TRUE ⇔ F=TRUE))",true
//			),
//			new TestPair(mSet(
//					"c^2 ∈ C",
//					"c^2 ∉ C"
//					),"⊥",false
//			),
			
			
//			 r : a<->b
//			 c <: a
//			|---
//			 r[c]<:b
//			new TestPair(mSet(
//					"r ∈ d↔e",
//					"c ⊆ d"
//					), "r[c] ⊆ e", true
//			),
//			
////			  x|->y : r
////			  !x,y. y|->x : r => x : b
////			 |---
////			  y:b
//			
//			new TestPair(mSet(
//					"x↦y ∈ r",
//					"∀x,y· y↦x ∈ r ⇒ x∈b"
//					), "y∈b", true
//			),
			
//			new TestPair(mSet(
//					"∀x·x^2 ∈ C",
//					"∀x·x^2 ∉ C"
//					),"⊥",false
//			),
//			new TestPair(mSet(
//				"r ∈ E ↔ E"
//				),"r ∈ E ↔ E",true
//			),
//			new TestPair(mSet(
//					"r ∈ E ↔ E",
//					"s ∈ E ↔ E"
//				),"r;s ⊆ E × E",true
//			),
			
			new TestPair(mSet(
					"r ∈ E ↔ E",
					"s ∈ E ↔ E"
				),"r;s ∈ ℙ(E × E)",true
			),
			
			new TestPair(mSet(
					"∀x,y·x ↦ y ∈ s ⇒ (x∈E ∧ y∈E)",
					"∀x,y·x ↦ y ∈ r ⇒ (x∈E ∧ y∈E)"
					),"∀x,y·(∃z·x ↦ z ∈ r ∧ z ↦ y ∈ r) ⇒ (x∈E ∧ y∈E)",true
			),
			
			new TestPair(mSet(
					"r ∈ E ↣ E",
					"s ∈ E ↣ E"
				),"r;s ∈ E ↣ E",true
			),
			new TestPair(mSet(
					"r ∈ E ↠ E",
					"s ∈ E ↠ E"
				),"r;s ∈ E ↠ E",true
			),
//			new TestPair(mSet(
//					"r ∈ E ⤖ E",
//					"s ∈ E ⤖ E"
//				),"r;s ∈ E ⤖ E",true
//			),
//			fails
			new TestPair(
				mSet(
					"r ∈ E ↔ E",
					"s ∈ E ↔ E"
				),
				"r;s ∈ E ↔ E",true
			),
//			fails
			new TestPair(mSet(
						"r ∈ E ⇸ E",
						"s ∈ E ⇸ E"
					),"r;s ∈ E ⇸ E",true
			),
//			fails
			new TestPair(mSet(
					"r ∈ E → E",
					"s ∈ E → E"
				),"r;s ∈ E → E",true
			),
//			// requires adding set hypothesis
			new TestPair(mSet(
					"A = S",
					"C ⊆ S",
					"A ∈ U"
					),"C ∪ A ∈ U",true
			),
//			// requires adding set membership hypothesis
			new TestPair(mSet(
					"C ⊆ S",
					"S ∈ U"
					),"C ∪ S ∈ U",true
			),
			new TestPair(mSet(
					"C ⊆ B",
					"B ∈ U"
					),"C ∪ B ∈ U",true
			),
			new TestPair(mSet(
					"A ⊆ B",
					"B ⊆ C"
					),"A ⊆ C",true
			),
			// fails at the moment
			new TestPair(mSet(
					"f ∈ S ⇸ T",
					"x ∉ dom(f)",
					"y ∈ T"
					),"f ∪ {x ↦ y} ∈ S ⇸ T",true
			),
			new TestPair(mSet(
					"f ∈ S ⇸ T"
					),"f∼[C ∩ D] = f∼[C] ∩ f∼[D]", true
			),
			new TestPair(mSet(
					"f ∈ S ⇸ T"
					),"f∼[C ∖ D] = f∼[C] ∖ f∼[D]", true
			),
			new TestPair(mSet(
					"f ∈ S ⤔ T"
					),"f[C ∩ D] = f[C] ∩ f[D]", true
			),
			new TestPair(mSet(
					"f ∈ S ↔ T"
					),"f∼[C ∩ D] = f∼[C] ∩ f∼[D]", false
			),
			new TestPair(mSet(
					"dap;org ⊆ sit",
					"sit(p)=org(d)",
//					"p ∈ dom(sit)", // unneeded
//					"d ∈ dom(org)", // unneeded
					"org ∈ D ⇸ L",
					"sit ∈ P → L"
				),"(dap ∪ {p↦d});org ⊆ sit",true
			),
//			new TestPair(mSet(
//					"dap;org ⊆ sit",
//					"sit(p)=org(d)",
////					"p ∈ dom(sit)",
////					"d ∈ dom(org)",
//					"org ∈ D ⇸ L",
//					"sit ∈ P → L"
//				),"(dap  {p↦d});org ⊆ sit",true
//			),
			new TestPair(mSet(
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)∈U",true
			),
			// fails when instantiationcount = 1
			new TestPair(mSet(
					"A∪B∈U",
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)∈U",true
			),
			new TestPair(mSet(
					"A∪B∈U",
					"A∪C∈U",
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)=(A∪B)∩(A∪C)",true
			),
			new TestPair(mSet(
					"A∪B∈U",
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)=(A∪B)∩(A∪C)",true
			),
			new TestPair(mSet(
					"A∪B∈U",
					"A∪C∈U",
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)=(A∪B)∩(A∪C)",true
			),
			new TestPair(mSet(
					"∅∉U",
					"A∪B∈U",
					"A∪C∈U",
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)=(A∪B)∩(A∪C)",true
			),
			new TestPair(mSet(
					"(A∪B)∩(A∪C)∈U"
					),"A∪(B∩C)=(A∪B)∩(A∪C)",true
			),
			new TestPair(mSet(
					"(A∪B)∈U"
					),"A∈U",false),
			new TestPair(mSet("r ∈ ran(r)∖{x} → ran(r)","r∼[q]⊆q","x∈q"),
					"ran(r)∖r∼[ran(r)∖q]⊆q",true),
			new TestPair(mSet("A = G"),"G ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)",true),
			new TestPair(mSet("q ⊆ R"),"R ∖ q ⊆ R",true),
			new TestPair(mSet("∀r·r∈R⇒nxt(r)∈rtbl∼[{r}] ∖ {lst(r)} ⤖ rtbl∼[{r}] ∖ {fst(r)}","nxt∈R → (B ⤔ B)"),
					"∀r·r∈R⇒r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id(ℙ(B × B))∧r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id(ℙ(B × B))",true),
			new TestPair(mSet("R ⊆ C"),"r[R] ⊆ r[C]",true),
			new TestPair(mSet("a = c"),"a ∈ {c,d}",true),
			new TestPair(mSet("(∃x,y·f(x)=y ∧ g(y)=a)"),"(∃x·(g∘f)(x)=a)",true),
//			new TestPair(mSet("(∀x·(∃x0·x ↦ x0∈SIG)⇒(∃x0·x0 ↦ x∈fst))" +
//					"∧" +
//					"(∀x,x0,x1·x ↦ x0∈SIG∧x ↦ x1∈SIG⇒x0=x1)" +
//					"∧" +
//					"(∀x·(∃x0·x0 ↦ x∈fst)⇒(∃x0·x ↦ x0∈SIG))" +
//					"∧" +
//					"(∀x·∃x0·x0 ↦ x∈SIG)" +
//					"∧" +
//					"(∀x,x0,x1·x0 ↦ x∈SIG∧x1 ↦ x∈SIG⇒x0=x1)"),"⊥",false)
	};
	
	public void testTrueGoal() {
		doTest(new TestPair(new HashSet(),"⊤",true));
	}
	
	public void testFalseHypothesis() {
		doTest(new TestPair(mSet("⊥"),"⊥",true));
	}
	
	private void doTest(TestPair test) {
		ITypeEnvironment tenv = env.clone();
		
		for (Predicate pred : test.hypotheses) {
			typeCheck(pred,tenv);
		}
		typeCheck(test.goal,tenv);
		
		PPProof prover = new PPProof(test.hypotheses,test.goal);
		prover.translate();
		prover.prove(-1);
		PPResult result = prover.getResult();
		assertEquals(result.getResult()==Result.valid, test.result);
	}
	
	private void typeCheck(Predicate predicate, ITypeEnvironment environment) {
		ITypeCheckResult result = predicate.typeCheck(environment);
		assertTrue(result.toString(),result.isSuccess());
		environment.addAll(result.getInferredEnvironment());
	}
	
	public static void main(String[] args) {
		RodinTests test = new RodinTests();
		test.testAll();
	}
	
}
