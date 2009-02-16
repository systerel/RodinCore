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
		predicateTest("∃x,y·x=y∧1↦2=x↦y", "1↦2∈{x↦y∣x=y}");
		predicateTest("∃x,y,z·(x=y∧z=x)∧1↦2↦3=x↦y↦z", "1↦2↦3∈{x↦y↦z∣x=y∧z=x}");
		predicateTest("∃w,x,y,z·(w=x∧y=z)∧1↦2↦3↦4=w↦x↦y↦z", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x∧y=z}");
		predicateTest("∃v,w,x,y,z·(v=w∧x=y∧z=v)∧1↦2↦3↦4↦5=v↦w↦x↦y↦z", "1↦2↦3↦4↦5∈{v↦w↦x↦y↦z∣v=w∧x=y∧z=v}");
		predicateTest("∃u,v,w,x,y,z·(u=v∧w=x∧y=z)∧1↦2↦3↦4↦5↦6=u↦v↦w↦x↦y↦z", "1↦2↦3↦4↦5↦6∈{u↦v↦w↦x↦y↦z∣u=v∧w=x∧y=z}");
		
		//test associative predicates land and lor
		predicateTest("∃w,x,y,z·(w=x∧y=z)∧1↦2↦3↦4=w↦x↦y↦z", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x∧y=z}");
		predicateTest("∃w,x,y,z·(w=x∨y=z)∧1↦(2↦3)↦4=w↦(x↦y)↦z", "1↦(2↦3)↦4∈{w↦(x↦y)↦z∣w=x∨y=z}");
		
		// test binary predicate limp and leqv
		predicateTest("∃w,x,y,z·(w=x⇒y=z)∧1↦2↦3↦4=w↦x↦y↦z", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x⇒y=z}");
		predicateTest("∃w,x,y,z·(w=x⇔y=z)∧1↦2↦3↦4=w↦x↦y↦z", "1↦2↦3↦4∈{w↦x↦y↦z∣w=x⇔y=z}");
		
		// test unary predicate not
		predicateTest("∃x,y·¬(x=y)∧1↦2=x↦y", "1↦2∈{x↦y∣¬(x=y)}");
		
		// test relational predicate equal, notequal, lt, le, gt, ge, in, notin,
		// subset, notsubset, subseteq and notsubseteq
		predicateTest("∃x,y·x=y∧1↦2=x↦y", "1↦2∈{x↦y∣x=y}");
		predicateTest("∃x,y·x≠y∧1↦2=x↦y", "1↦2∈{x↦y∣x≠y}");
		predicateTest("∃x,y·x<y∧1↦2=x↦y", "1↦2∈{x↦y∣x<y}");
		predicateTest("∃x,y·x≤y∧1↦2=x↦y", "1↦2∈{x↦y∣x≤y}");
		predicateTest("∃x,y·x>y∧1↦2=x↦y", "1↦2∈{x↦y∣x>y}");
		predicateTest("∃x,y·x≥y∧1↦2=x↦y", "1↦2∈{x↦y∣x≥y}");
		predicateTest("∃x,y·x∈{y}∧1↦2=x↦y", "1↦2∈{x↦y∣x∈{y}}");
		predicateTest("∃x,y·x∉{y}∧1↦2=x↦y", "1↦2∈{x↦y∣x∉{y}}");
		predicateTest("∃x,y·{x}⊂{y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x}⊂{y}}");
		predicateTest("∃x,y·{x}⊄{y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x}⊄{y}}");
		predicateTest("∃x,y·{x}⊆{y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x}⊆{y}}");
		predicateTest("∃x,y·{x}⊈{y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x}⊈{y}}");
		
		// test quantified predicate forall and exists
		predicateTest("∃x,y·(∀q,v·q∈ℕ∧v∈ℕ⇒x=q∧y=v)∧1↦2=x↦y", "1↦2∈{x↦y∣∀q,v·q∈ℕ∧v∈ℕ⇒x=q∧y=v}");
		predicateTest("∃x,y·(∃q,v·q∈ℕ∧v∈ℕ∧x=q∧y=v)∧1↦2=x↦y", "1↦2∈{x↦y∣∃q,v·q∈ℕ∧v∈ℕ∧x=q∧y=v}");
		
		// test associative expression bunion, binter, bcomp, fcomp, ovr, plus, mul
		predicateTest("∃x,y·{x}∪∅={y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x}∪∅={y}}");
		predicateTest("∃x,y·{x}∩∅={y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x}∩∅={y}}");
		predicateTest("∃x,y·{x↦y}∘{x↦y}={x↦y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x↦y}∘{x↦y}={x↦y}}");
		predicateTest("∃x,y·{x↦y};{x↦y}={x↦y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x↦y};{x↦y}={x↦y}}");
		predicateTest("∃x,y·{x↦y}∅={x↦y}∧1↦2=x↦y", "1↦2∈{x↦y∣{x↦y}∅={x↦y}}");
		predicateTest("∃x,y·x+y=1∧1↦2=x↦y", "1↦2∈{x↦y∣x+y=1}");
		predicateTest("∃x,y·x∗y=1∧1↦2=x↦y", "1↦2∈{x↦y∣x∗y=1}");
		
		// test mix input where the left-handside and the expression does not match
		predicateTest("∃x·x∈ℕ×ℕ∧1↦2=x", "1↦2 ∈ {x∣x∈ℕ×ℕ}");
		predicateTest("∃x,y,z·(x∈ℕ∧x=y∧y=z)∧u↦v=x↦y↦z","u↦v∈{x↦y↦z∣x∈ℕ∧x=y∧y=z}");
		predicateTest("∃x,y,z·(x∈ℕ∧x=y∧y=z)∧u↦v=x↦(y↦z)","u↦v∈{x↦(y↦z)∣x∈ℕ∧x=y∧y=z}");
	}

}
