package org.eventb.contributer.seqprover.fr1942714.tests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.contributer.seqprover.fr1942714.AutoRewriterImpl;
import org.eventb.core.seqprover.rewriterTests.AbstractFormulaRewriterTests;
import org.junit.Test;

public class AutoFormulaRewriterTests extends AbstractFormulaRewriterTests 
{
	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();
	public AutoFormulaRewriterTests()
	{
		super(rewriter);
	}
	
	@Test
	public void test_overwriting()
	{
		// test size up to 6
		predicateTest("1=2", "1↦2∈{x↦y∣x=y}");
		predicateTest("1=2∧3=1", "1↦2↦3∈{x↦y↦z∣x=y∧z=x}");
		predicateTest("1=2∧3=4", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x∧y=z}");
		predicateTest("1=2∧3=4∧5=1", "1↦2↦3↦4↦5∈{v↦w↦x↦y↦z∣v=w∧x=y∧z=v}");
		predicateTest("1=2∧3=4∧5=6", "1↦2↦3↦4↦5↦6∈{u↦v↦w↦x↦y↦z∣u=v∧w=x∧y=z}");
		
		//test associative predicates land and lor
		predicateTest("1=2∧3=4", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x∧y=z}");
		predicateTest("1=2∨3=4", "1↦(2↦3)↦4∈{w↦(x↦y)↦z∣w=x∨y=z}");
		
		// test binary predicate limp and leqv
		predicateTest("1=2⇒3=4", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x⇒y=z}");
		predicateTest("1=2⇔3=4", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x⇔y=z}");
		
		// test unary predicate not
		predicateTest("¬(1=2)", "1↦2∈{x↦y∣¬(x=y)}");
		
		// test relational predicate equal, notequal, lt, le, gt, ge, in, notin,
		// subset, notsubset, subseteq and notsubseteq
		predicateTest("1=2", "1↦2∈{x↦y∣x=y}");
		predicateTest("1≠2", "1↦2∈{x↦y∣x≠y}");
		predicateTest("1<2", "1↦2∈{x↦y∣x<y}");
		predicateTest("1≤2", "1↦2∈{x↦y∣x≤y}");
		predicateTest("1>2", "1↦2∈{x↦y∣x>y}");
		predicateTest("1≥2", "1↦2∈{x↦y∣x≥y}");
		predicateTest("1∈{2}", "1↦2∈{x↦y∣x∈{y}}");
		predicateTest("1∉{2}", "1↦2∈{x↦y∣x∉{y}}");
		predicateTest("{1}⊂{2}", "1↦2∈{x↦y∣{x}⊂{y}}");
		predicateTest("{1}⊄{2}", "1↦2∈{x↦y∣{x}⊄{y}}");
		predicateTest("{1}⊆{2}", "1↦2∈{x↦y∣{x}⊆{y}}");
		predicateTest("{1}⊈{2}", "1↦2∈{x↦y∣{x}⊈{y}}");
		
		// test quantified predicate forall and exists
		predicateTest("∀q,v·q∈ℕ∧v∈ℕ⇒1=q∧2=v", "1↦2∈{x↦y∣∀q,v·q∈ℕ∧v∈ℕ⇒x=q∧y=v}");
		predicateTest("∃q,v·q∈ℕ∧v∈ℕ⇒1=q∧2=v", "1↦2∈{x↦y∣∃q,v·q∈ℕ∧v∈ℕ⇒x=q∧y=v}");
		
		// test associative expression bunion, binter, bcomp, fcomp, ovr, plus, mul
		predicateTest("{1}∪∅={2}", "1↦2∈{x↦y∣{x}∪∅={y}}");
		predicateTest("{1}∩∅={2}", "1↦2∈{x↦y∣{x}∩∅={y}}");
		predicateTest("{1↦2}∘{1↦2}={1↦2}", "1↦2∈{x↦y∣{x↦y}∘{x↦y}={x↦y}}");
		predicateTest("{1↦2};{1↦2}={1↦2}", "1↦2∈{x↦y∣{x↦y};{x↦y}={x↦y}}");
		predicateTest("{1↦2}∅={1↦2}", "1↦2∈{x↦y∣{x↦y}∅={x↦y}}");
		predicateTest("1+2=1", "1↦2∈{x↦y∣x+y=1}");
		predicateTest("1∗2=1", "1↦2∈{x↦y∣x∗y=1}");
		
		// since we use instatiate we can discard 
		// any further tests since this function is allready tested
		
	}

}
