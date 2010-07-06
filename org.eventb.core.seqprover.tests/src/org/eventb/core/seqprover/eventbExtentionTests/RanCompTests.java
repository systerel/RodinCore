package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RanCompRewrites;

/**
 * Unit tests for the Range manipulation with Composition Rewrites reasoner
 * {@link RanCompRewrites}
 * 
 * @author htson
 */
public class RanCompTests extends AbstractManualRewriterTests {

	// p;...;(q |> S);r;...;s == ((p;...;q) |> S);r;...;s
	String P1 = "({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP1 = "(({x ↦ 1} ▷ {1,2});{2 ↦ x} ▷ {1,2});({x ↦ 3} ▷ {1,2});({x ↦ 4} ▷ {1,2})={x ↦ 1}";

	String P2 = "1 = x ⇒ ({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP2 = "1=x⇒(({x ↦ 1} ▷ {1,2});{2 ↦ x} ▷ {1,2});({x ↦ 3} ▷ {1,2});({x ↦ 4} ▷ {1,2})={x ↦ 1}";

	String P3 = "∀x·x = 0 ⇒ ({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP3 = "∀x·x=0⇒(({x ↦ 1} ▷ {1,2});{2 ↦ x} ▷ {1,2});({x ↦ 3} ▷ {1,2});({x ↦ 4} ▷ {1,2})={x ↦ 1}";	
	
	String P4 = "({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP4 = "(({x ↦ 1} ▷ {1,2});({2 ↦ x} ▷ {1,2});{x ↦ 3} ▷ {1,2});({x ↦ 4} ▷ {1,2})={x ↦ 1}";

	String P5 = "1 = x ⇒ ({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP5 = "1=x⇒(({x ↦ 1} ▷ {1,2});({2 ↦ x} ▷ {1,2});{x ↦ 3} ▷ {1,2});({x ↦ 4} ▷ {1,2})={x ↦ 1}";

	String P6 = "∀x·x = 0 ⇒ ({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP6 = "∀x·x=0⇒(({x ↦ 1} ▷ {1,2});({2 ↦ x} ▷ {1,2});{x ↦ 3} ▷ {1,2});({x ↦ 4} ▷ {1,2})={x ↦ 1}";	
	
	String P7 = "({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP7 = "({x ↦ 1} ▷ {1,2});({2 ↦ x} ▷ {1,2});({x ↦ 3} ▷ {1,2});{x ↦ 4} ▷ {1,2}={x ↦ 1}";

	String P8 = "1 = x ⇒ ({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP8 = "1=x⇒({x ↦ 1} ▷ {1,2});({2 ↦ x} ▷ {1,2});({x ↦ 3} ▷ {1,2});{x ↦ 4} ▷ {1,2}={x ↦ 1}";

	String P9 = "∀x·x = 0 ⇒ ({x ↦ 1} ▷ {1, 2});({2 ↦ x} ▷ {1, 2});({x ↦ 3} ▷ {1, 2});({x ↦ 4} ▷ {1, 2}) = {x ↦ 1}";

	String resultP9 = "∀x·x=0⇒({x ↦ 1} ▷ {1,2});({2 ↦ x} ▷ {1,2});({x ↦ 3} ▷ {1,2});{x ↦ 4} ▷ {1,2}={x ↦ 1}";	

	
	// p;...;(q |>> S);r;...;s == ((p;...;q) |>> S);r;...;s
	String P10 = "({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP10 = "(({x ↦ 1} ⩥ {1,2});{2 ↦ x} ⩥ {1,2});({x ↦ 3} ⩥ {1,2});({x ↦ 4} ⩥ {1,2})={x ↦ 1}";

	String P11 = "1 = x ⇒ ({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP11 = "1=x⇒(({x ↦ 1} ⩥ {1,2});{2 ↦ x} ⩥ {1,2});({x ↦ 3} ⩥ {1,2});({x ↦ 4} ⩥ {1,2})={x ↦ 1}";

	String P12 = "∀x·x = 0 ⇒ ({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP12 = "∀x·x=0⇒(({x ↦ 1} ⩥ {1,2});{2 ↦ x} ⩥ {1,2});({x ↦ 3} ⩥ {1,2});({x ↦ 4} ⩥ {1,2})={x ↦ 1}";	
	
	String P13 = "({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP13 = "(({x ↦ 1} ⩥ {1,2});({2 ↦ x} ⩥ {1,2});{x ↦ 3} ⩥ {1,2});({x ↦ 4} ⩥ {1,2})={x ↦ 1}";

	String P14 = "1 = x ⇒ ({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP14 = "1=x⇒(({x ↦ 1} ⩥ {1,2});({2 ↦ x} ⩥ {1,2});{x ↦ 3} ⩥ {1,2});({x ↦ 4} ⩥ {1,2})={x ↦ 1}";

	String P15 = "∀x·x = 0 ⇒ ({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP15 = "∀x·x=0⇒(({x ↦ 1} ⩥ {1,2});({2 ↦ x} ⩥ {1,2});{x ↦ 3} ⩥ {1,2});({x ↦ 4} ⩥ {1,2})={x ↦ 1}";	
	
	String P16 = "({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP16 = "({x ↦ 1} ⩥ {1,2});({2 ↦ x} ⩥ {1,2});({x ↦ 3} ⩥ {1,2});{x ↦ 4} ⩥ {1,2}={x ↦ 1}";

	String P17 = "1 = x ⇒ ({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP17 = "1=x⇒({x ↦ 1} ⩥ {1,2});({2 ↦ x} ⩥ {1,2});({x ↦ 3} ⩥ {1,2});{x ↦ 4} ⩥ {1,2}={x ↦ 1}";

	String P18 = "∀x·x = 0 ⇒ ({x ↦ 1} ⩥ {1, 2});({2 ↦ x} ⩥ {1, 2});({x ↦ 3} ⩥ {1, 2});({x ↦ 4} ⩥ {1, 2}) = {x ↦ 1}";

	String resultP18 = "∀x·x=0⇒({x ↦ 1} ⩥ {1,2});({2 ↦ x} ⩥ {1,2});({x ↦ 3} ⩥ {1,2});{x ↦ 4} ⩥ {1,2}={x ↦ 1}";	

	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.ranCompRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.ranCompGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "0.1", resultP1),
				new SuccessfulTest(P2, "1.0.1", resultP2),
				new SuccessfulTest(P3, "1.1.0.1", resultP3),
				new SuccessfulTest(P4, "0.2", resultP4),
				new SuccessfulTest(P5, "1.0.2", resultP5),
				new SuccessfulTest(P6, "1.1.0.2", resultP6),
				new SuccessfulTest(P7, "0.3", resultP7),
				new SuccessfulTest(P8, "1.0.3", resultP8),
				new SuccessfulTest(P9, "1.1.0.3", resultP9),
				new SuccessfulTest(P10, "0.1", resultP10),
				new SuccessfulTest(P11, "1.0.1", resultP11),
				new SuccessfulTest(P12, "1.1.0.1", resultP12),
				new SuccessfulTest(P13, "0.2", resultP13),
				new SuccessfulTest(P14, "1.0.2", resultP14),
				new SuccessfulTest(P15, "1.1.0.2", resultP15),
				new SuccessfulTest(P16, "0.3", resultP16),
				new SuccessfulTest(P17, "1.0.3", resultP17),
				new SuccessfulTest(P18, "1.1.0.3", resultP18)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0.0.0",
				P2, "1.0.0.0",
				P3, "1.1.0.0.0",
				P4, "0.0.0",
				P5, "1.0.0.0",
				P6, "1.1.0.0.0",
				P7, "0.0.0",
				P8, "1.0.0.0",
				P9, "1.1.0.0.0",
				P10, "0.0.0",
				P11, "1.0.0.0",
				P12, "1.1.0.0.0",
				P13, "0.0.0",
				P14, "1.0.0.0",
				P15, "1.1.0.0.0",
				P16, "0.0.0",
				P17, "1.0.0.0",
				P18, "1.1.0.0.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "0.1\n"+"0.2\n"+"0.3",
				P2, "1.0.1\n"+"1.0.2\n"+"1.0.3",
				P3, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3",
				P4, "0.1\n"+"0.2\n"+"0.3",
				P5, "1.0.1\n"+"1.0.2\n"+"1.0.3",
				P6, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3",
				P7, "0.1\n"+"0.2\n"+"0.3",
				P8, "1.0.1\n"+"1.0.2\n"+"1.0.3",
				P9, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3",
				P10, "0.1\n"+"0.2\n"+"0.3",
				P11, "1.0.1\n"+"1.0.2\n"+"1.0.3",
				P12, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3",
				P13, "0.1\n"+"0.2\n"+"0.3",
				P14, "1.0.1\n"+"1.0.2\n"+"1.0.3",
				P15, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3",
				P16, "0.1\n"+"0.2\n"+"0.3",
				P17, "1.0.1\n"+"1.0.2\n"+"1.0.3",
				P18, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
