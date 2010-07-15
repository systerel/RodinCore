package org.eventb.pp.core.elements.terms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.junit.Test;

public class TestComparable extends AbstractPPTest {

	private static class TestPair {
		Term term1, term2;
		int result;
		
		TestPair(Term term1, Term term2, int result) {
			this.term1 = term1;
			this.term2 = term2;
			this.result = result;
		}
	}
	
    @Test
	public void testIntegerConstants() {
		doTest(new TestPair(
				zero,one,-1
		));
		doTest(new TestPair(
				one,zero,1
		));
		doTest(new TestPair(
				zero,zero,0
		));
	}

    @Test
	public void testConstants() {
		doTest(new TestPair(
				a,b,-1
		));
		doTest(new TestPair(
				b,a,1
		));
		doTest(new TestPair(
				a,a,0
		));
	}
	
    @Test
	public void testConstantsWithVariables() {
		doTest(new TestPair(
				a,x,1
		));
		doTest(new TestPair(
				x,a,-1
		));
	}

    @Test
	public void testVariables() {
		doTest(new TestPair(
				x,y,-1
		));
		doTest(new TestPair(
				y,x,1
		));
		doTest(new TestPair(
				x,x,0
		));
	}
	
    @Test
	public void testLocalVariablesWithVariables() {
		doTest(new TestPair(
				x,evar0,-1
		));
		doTest(new TestPair(
				evar0,x,1		
		));
	}
	
    @Test
	public void testLocalVariables() {
		doTest(new TestPair(
				evar0,evar1,-1
		));
		doTest(new TestPair(
				evar1,evar0,1	
		));
		doTest(new TestPair(
				evar0,evar0,0
		));
	}

    @Test
	public void testLocalVariablesWithConstants() {
		doTest(new TestPair(
				evar0,a,-1
		));
		doTest(new TestPair(
				a,evar0,1	
		));
	}
	
    @Test
	public void testConstantWithIntegerConstants() {
		doTest(new TestPair(
				a,zero,-1
		));
		doTest(new TestPair(
				zero,a,1
		));
	}
	
	
    @Test
	public void testVariableWithIntegerConstant() {
		doTest(new TestPair(
				x,zero,-1
		));
		doTest(new TestPair(
				zero,x,1
		));
	}
	
    @Test
	public void testLocalVariableWithIntegerConstant() {
		doTest(new TestPair(
				evar0,zero,-1
		));
		doTest(new TestPair(
				zero,evar0,1
		));
	}
	
	
//	public void testArithmeticWithConstants() {
//		
//	}
	
	private void doTest(TestPair test) {
		int actual = test.term1.compareTo(test.term2);
		if (actual == 0) assertEquals(test.result, actual);
		if (actual < 0) assertTrue(test.result < 0);
		if (actual > 0) assertTrue(test.result > 0);
		
		actual = test.term2.compareTo(test.term1);
		if (actual == 0) assertEquals(test.result, actual);
		if (actual < 0) assertTrue(test.result > 0);
		if (actual > 0) assertTrue(test.result < 0);
	}
	
	
}
