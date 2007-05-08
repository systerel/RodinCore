package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ManualRewriterImpl;
import org.junit.Test;

public class ManualFormulaRewriterTests extends AutoFormulaRewriterTests {

	@Override
	public void setUp() throws Exception {
		r = new ManualRewriterImpl();
		P.typeCheck(Lib.makeTypeEnvironment());
		Q.typeCheck(Lib.makeTypeEnvironment());
		R.typeCheck(Lib.makeTypeEnvironment());
		notP.typeCheck(Lib.makeTypeEnvironment());
		notQ.typeCheck(Lib.makeTypeEnvironment());
		notR.typeCheck(Lib.makeTypeEnvironment());
		S.typeCheck(Lib.makeTypeEnvironment());
		T.typeCheck(Lib.makeTypeEnvironment());
		U.typeCheck(Lib.makeTypeEnvironment());
		E.typeCheck(Lib.makeTypeEnvironment());
		F.typeCheck(Lib.makeTypeEnvironment());
		bE.typeCheck(Lib.makeTypeEnvironment());;
	}

	@Test
	public void testConjunction() {
		super.testConjunction();
	}

	@Test
	public void testArithmetic() {
		// TODO Auto-generated method stub
		super.testArithmetic();
	}

	@Test
	public void testDisjunction() {
		// TODO Auto-generated method stub
		super.testDisjunction();
	}

	@Test
	public void testEquality() {
		// TODO Auto-generated method stub
		super.testEquality();
	}

	@Test
	public void testEquivalent() {
		// TODO Auto-generated method stub
		super.testEquivalent();
	}

	@Test
	public void testImplication() {
		// TODO Auto-generated method stub
		super.testImplication();
	}

	@Test
	public void testNegation() {
		// TODO Auto-generated method stub
		super.testNegation();
	}

	@Test
	public void testQuantification() {
		// TODO Auto-generated method stub
		super.testQuantification();
	}

	@Test
	public void testSetTheory() {
		// TODO Auto-generated method stub
		super.testSetTheory();
	}

}
