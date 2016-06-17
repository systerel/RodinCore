/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomCompRewrites;

/**
 * Unit tests for the Domain manipulation with Composition Rewrites reasoner
 * {@link DomCompRewrites}
 * 
 * @author htson
 */
public class DomCompTests extends AbstractManualRewriterTests {

	// (p;...;q;(S <| r);...;s) == (p;...;q);(S <| (r;...;s))
	String P1 = "{1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP1 = "{1,2} ◁ ({x ↦ 1};({1,2} ◁ {2 ↦ x});({1,2} ◁ {x ↦ 3});({1,2} ◁ {x ↦ 4}))={x ↦ 1}";

	String P2 = "1 = x ⇒ {1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP2 = "1=x⇒{1,2} ◁ ({x ↦ 1};({1,2} ◁ {2 ↦ x});({1,2} ◁ {x ↦ 3});({1,2} ◁ {x ↦ 4}))={x ↦ 1}";

	String P3 = "∀x·x = 0 ⇒ {1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP3 = "∀x·x=0⇒{1,2} ◁ ({x ↦ 1};({1,2} ◁ {2 ↦ x});({1,2} ◁ {x ↦ 3});({1,2} ◁ {x ↦ 4}))={x ↦ 1}";	
	
	String P4 = "{1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP4 = "{1,2} ◁ {x ↦ 1};({1,2} ◁ ({2 ↦ x};({1,2} ◁ {x ↦ 3});({1,2} ◁ {x ↦ 4})))={x ↦ 1}";

	String P5 = "1 = x ⇒ {1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP5 = "1=x⇒{1,2} ◁ {x ↦ 1};({1,2} ◁ ({2 ↦ x};({1,2} ◁ {x ↦ 3});({1,2} ◁ {x ↦ 4})))={x ↦ 1}";

	String P6 = "∀x·x = 0 ⇒ {1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP6 = "∀x·x=0⇒{1,2} ◁ {x ↦ 1};({1,2} ◁ ({2 ↦ x};({1,2} ◁ {x ↦ 3});({1,2} ◁ {x ↦ 4})))={x ↦ 1}";	

	String P7 = "{1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP7 = "{1,2} ◁ {x ↦ 1};({1,2} ◁ {2 ↦ x});({1,2} ◁ ({x ↦ 3};({1,2} ◁ {x ↦ 4})))={x ↦ 1}";

	String P8 = "1 = x ⇒ {1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP8 = "1=x⇒{1,2} ◁ {x ↦ 1};({1,2} ◁ {2 ↦ x});({1,2} ◁ ({x ↦ 3};({1,2} ◁ {x ↦ 4})))={x ↦ 1}";

	String P9 = "∀x·x = 0 ⇒ {1, 2} ◁ {x ↦ 1};({1, 2}◁{2 ↦ x});({1, 2}◁{x ↦ 3});({1, 2}◁{x ↦ 4}) = {x ↦ 1}";

	String resultP9 = "∀x·x=0⇒{1,2} ◁ {x ↦ 1};({1,2} ◁ {2 ↦ x});({1,2} ◁ ({x ↦ 3};({1,2} ◁ {x ↦ 4})))={x ↦ 1}";	


	// (p;...;q;(S <<| r);...;s) == (p;...;q);(S <<| (r;...;s))
	String P10 = "{1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP10 = "{1,2} ⩤ ({x ↦ 1};({1,2} ⩤ {2 ↦ x});({1,2} ⩤ {x ↦ 3});({1,2} ⩤ {x ↦ 4}))={x ↦ 1}";

	String P11 = "1 = x ⇒ {1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP11 = "1=x⇒{1,2} ⩤ ({x ↦ 1};({1,2} ⩤ {2 ↦ x});({1,2} ⩤ {x ↦ 3});({1,2} ⩤ {x ↦ 4}))={x ↦ 1}";

	String P12 = "∀x·x = 0 ⇒ {1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP12 = "∀x·x=0⇒{1,2} ⩤ ({x ↦ 1};({1,2} ⩤ {2 ↦ x});({1,2} ⩤ {x ↦ 3});({1,2} ⩤ {x ↦ 4}))={x ↦ 1}";	
	
	String P13 = "{1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP13 = "{1,2} ⩤ {x ↦ 1};({1,2} ⩤ ({2 ↦ x};({1,2} ⩤ {x ↦ 3});({1,2} ⩤ {x ↦ 4})))={x ↦ 1}";

	String P14 = "1 = x ⇒ {1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP14 = "1=x⇒{1,2} ⩤ {x ↦ 1};({1,2} ⩤ ({2 ↦ x};({1,2} ⩤ {x ↦ 3});({1,2} ⩤ {x ↦ 4})))={x ↦ 1}";

	String P15 = "∀x·x = 0 ⇒ {1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP15 = "∀x·x=0⇒{1,2} ⩤ {x ↦ 1};({1,2} ⩤ ({2 ↦ x};({1,2} ⩤ {x ↦ 3});({1,2} ⩤ {x ↦ 4})))={x ↦ 1}";	

	String P16 = "{1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP16 = "{1,2} ⩤ {x ↦ 1};({1,2} ⩤ {2 ↦ x});({1,2} ⩤ ({x ↦ 3};({1,2} ⩤ {x ↦ 4})))={x ↦ 1}";

	String P17 = "1 = x ⇒ {1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP17 = "1=x⇒{1,2} ⩤ {x ↦ 1};({1,2} ⩤ {2 ↦ x});({1,2} ⩤ ({x ↦ 3};({1,2} ⩤ {x ↦ 4})))={x ↦ 1}";

	String P18 = "∀x·x = 0 ⇒ {1, 2} ⩤ {x ↦ 1};({1, 2}⩤{2 ↦ x});({1, 2}⩤{x ↦ 3});({1, 2}⩤{x ↦ 4}) = {x ↦ 1}";

	String resultP18 = "∀x·x=0⇒{1,2} ⩤ {x ↦ 1};({1,2} ⩤ {2 ↦ x});({1,2} ⩤ ({x ↦ 3};({1,2} ⩤ {x ↦ 4})))={x ↦ 1}";	

	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.domCompRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.domCompGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "0.0", resultP1),
				new SuccessfulTest(P2, "1.0.0", resultP2),
				new SuccessfulTest(P3, "1.1.0.0", resultP3),
				new SuccessfulTest(P4, "0.1", resultP4),
				new SuccessfulTest(P5, "1.0.1", resultP5),
				new SuccessfulTest(P6, "1.1.0.1", resultP6),
				new SuccessfulTest(P7, "0.2", resultP7),
				new SuccessfulTest(P8, "1.0.2", resultP8),
				new SuccessfulTest(P9, "1.1.0.2", resultP9),
				new SuccessfulTest(P10, "0.0", resultP10),
				new SuccessfulTest(P11, "1.0.0", resultP11),
				new SuccessfulTest(P12, "1.1.0.0", resultP12),
				new SuccessfulTest(P13, "0.1", resultP13),
				new SuccessfulTest(P14, "1.0.1", resultP14),
				new SuccessfulTest(P15, "1.1.0.1", resultP15),
				new SuccessfulTest(P16, "0.2", resultP16),
				new SuccessfulTest(P17, "1.0.2", resultP17),
				new SuccessfulTest(P18, "1.1.0.2", resultP18)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0.0.3",
				P2, "1.0.0.3",
				P3, "1.1.0.0.3",
				P4, "0.0.3",
				P5, "1.0.0.3",
				P6, "1.1.0.0.3",
				P7, "0.0.3",
				P8, "1.0.0.3",
				P9, "1.1.0.0.3",
				P10, "0.0.3",
				P11, "1.0.0.3",
				P12, "1.1.0.0.3",
				P13, "0.0.3",
				P14, "1.0.0.3",
				P15, "1.1.0.0.3",
				P16, "0.0.3",
				P17, "1.0.0.3",
				P18, "1.1.0.0.3"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "0.0\n"+"0.1\n"+"0.2",
				P2, "1.0.0\n"+"1.0.1\n"+"1.0.2",
				P3, "1.1.0.0\n"+"1.1.0.1\n"+"1.1.0.2",
				P4, "0.0\n"+"0.1\n"+"0.2",
				P5, "1.0.0\n"+"1.0.1\n"+"1.0.2",
				P6, "1.1.0.0\n"+"1.1.0.1\n"+"1.1.0.2",
				P7, "0.0\n"+"0.1\n"+"0.2",
				P8, "1.0.0\n"+"1.0.1\n"+"1.0.2",
				P9, "1.1.0.0\n"+"1.1.0.1\n"+"1.1.0.2",
				P10, "0.0\n"+"0.1\n"+"0.2",
				P11, "1.0.0\n"+"1.0.1\n"+"1.0.2",
				P12, "1.1.0.0\n"+"1.1.0.1\n"+"1.1.0.2",
				P13, "0.0\n"+"0.1\n"+"0.2",
				P14, "1.0.0\n"+"1.0.1\n"+"1.0.2",
				P15, "1.1.0.0\n"+"1.1.0.1\n"+"1.1.0.2",
				P16, "0.0\n"+"0.1\n"+"0.2",
				P17, "1.0.0\n"+"1.0.1\n"+"1.0.2",
				P18, "1.1.0.0\n"+"1.1.0.1\n"+"1.1.0.2"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
