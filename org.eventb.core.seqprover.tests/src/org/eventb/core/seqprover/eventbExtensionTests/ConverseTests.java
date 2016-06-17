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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ConvRewrites;

/**
 * Unit tests for the Converse Relation Rewrites reasoner
 * {@link ConvRewrites}
 * 
 * @author htson
 */
public class ConverseTests extends AbstractManualRewriterTests {

	// (p \/ ... \/ q)~ == p~ \/ ... \/ q~
	String P1 = "(f ∪ {1 ↦ x, x ↦ 2} ∪ g)∼ = {x ↦ x}";

	String resultP1 = "f∼∪{1 ↦ x,x ↦ 2}∼∪g∼={x ↦ x}";

	String P2 = "(1 = x) ⇒ {x ↦ x} = (f ∪ {1 ↦ x, x ↦ 2} ∪ g)∼";

	String resultP2 = "1=x⇒{x ↦ x}=f∼∪{1 ↦ x,x ↦ 2}∼∪g∼";

	String P3 = "∀x·x = 0 ⇒ (f ∪ {1 ↦ x, x ↦ 2} ∪ g)∼ = {x ↦ x}";

	String resultP3 = "∀x·x=0⇒f∼∪{1 ↦ x,x ↦ 2}∼∪g∼={x ↦ x}";


	// (p /\ ... /\ q)~ == p~ /\ ... /\ q~
	String P4 = "(f ∩ {1 ↦ x, x ↦ 2} ∩ g)∼ = {x ↦ x}";

	String resultP4 = "f∼∩{1 ↦ x,x ↦ 2}∼∩g∼={x ↦ x}";

	String P5 = "(1 = x) ⇒  {x ↦ x} = (f ∩ {1 ↦ x, x ↦ 2} ∩ g)∼";

	String resultP5 = "1=x⇒{x ↦ x}=f∼∩{1 ↦ x,x ↦ 2}∼∩g∼";

	String P6 = "∀x·x = 0 ⇒ (f ∩ {1 ↦ x, x ↦ 2} ∩ g)∼ = {x ↦ x}";

	String resultP6 = "∀x·x=0⇒f∼∩{1 ↦ x,x ↦ 2}∼∩g∼={x ↦ x}";


	// (s <| r)~ == r~ |> s
	String P7 = "({x, 1} ◁ r)∼ = {x ↦ 1}";

	String resultP7 = "r∼ ▷ {x,1}={x ↦ 1}";

	String P8 = "(1 = x) ⇒ {x ↦ 1} = ({x, 1} ◁ r)∼";

	String resultP8 = "1=x⇒{x ↦ 1}=r∼ ▷ {x,1}";

	String P9 = "∀x·x = 0 ⇒ ({x, 1} ◁ r)∼ = {x ↦ 1}";

	String resultP9 = "∀x·x=0⇒r∼ ▷ {x,1}={x ↦ 1}";


	// (s <<| r)~ == r~ |>> s
	String P10 = "({x, 1} ⩤ r)∼ = {x ↦ 1}";

	String resultP10 = "r∼ ⩥ {x,1}={x ↦ 1}";

	String P11 = "(1 = x) ⇒ {x ↦ 1} = ({x, 1} ⩤ r)∼";

	String resultP11 = "1=x⇒{x ↦ 1}=r∼ ⩥ {x,1}";

	String P12 = "∀x·x = 0 ⇒ ({x, 1} ⩤ r)∼ = {x ↦ 1}";

	String resultP12 = "∀x·x=0⇒r∼ ⩥ {x,1}={x ↦ 1}";


	// (r |> s)~ == s <| r~
	String P13 = "(r ▷ {x, 1})∼ = {x ↦ 1}";

	String resultP13 = "{x,1} ◁ r∼={x ↦ 1}";

	String P14 = "(1 = x) ⇒ {x ↦ 1} = (r ▷ {x, 1})∼";

	String resultP14 = "1=x⇒{x ↦ 1}={x,1} ◁ r∼";

	String P15 = "∀x·x = 0 ⇒ (r ▷ {x, 1})∼ = {x ↦ 1}";

	String resultP15 = "∀x·x=0⇒{x,1} ◁ r∼={x ↦ 1}";


	// (r |>> s)~ == s <<| r~
	String P16 = "(r ⩥ {x, 1})∼ = {x ↦ 1}";

	String resultP16 = "{x,1} ⩤ r∼={x ↦ 1}";

	String P17 = "(1 = x) ⇒ {x ↦ 1} = (r ⩥ {x, 1})∼";

	String resultP17 = "1=x⇒{x ↦ 1}={x,1} ⩤ r∼";

	String P18 = "∀x·x = 0 ⇒ (r ⩥ {x, 1})∼ = {x ↦ 1}";

	String resultP18 = "∀x·x=0⇒{x,1} ⩤ r∼={x ↦ 1}";

	
	// (p;...;q)~ == q~;...;p~
	String P19 = "(p;{2 ↦ x};{3 ↦ x})∼ = {x ↦ 1}";

	String resultP19 = "{3 ↦ x}∼;{2 ↦ x}∼;p∼={x ↦ 1}";

	String P20 = "(1 = x) ⇒ (p;{2 ↦ x};{3 ↦ x})∼ = {x ↦ 1}";

	String resultP20 = "1=x⇒{3 ↦ x}∼;{2 ↦ x}∼;p∼={x ↦ 1}";

	String P21 = "∀x·x = 0 ⇒ (p;{2 ↦ x};{3 ↦ x})∼ = {x ↦ 1}";

	String resultP21 = "∀x·x=0⇒{3 ↦ x}∼;{2 ↦ x}∼;p∼={x ↦ 1}";
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.convRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.convGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "0", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
				new SuccessfulTest(P3, "1.1.0", resultP3),
				new SuccessfulTest(P4, "0", resultP4),
				new SuccessfulTest(P5, "1.1", resultP5),
				new SuccessfulTest(P6, "1.1.0", resultP6),
				new SuccessfulTest(P7, "0", resultP7),
				new SuccessfulTest(P8, "1.1", resultP8),
				new SuccessfulTest(P9, "1.1.0", resultP9),
				new SuccessfulTest(P10, "0", resultP10),
				new SuccessfulTest(P11, "1.1", resultP11),
				new SuccessfulTest(P12, "1.1.0", resultP12),
				new SuccessfulTest(P13, "0", resultP13),
				new SuccessfulTest(P14, "1.1", resultP14),
				new SuccessfulTest(P15, "1.1.0", resultP15),
				new SuccessfulTest(P16, "0", resultP16),
				new SuccessfulTest(P17, "1.1", resultP17),
				new SuccessfulTest(P18, "1.1.0", resultP18),
				new SuccessfulTest(P19, "0", resultP19),
				new SuccessfulTest(P20, "1.0", resultP20),
				new SuccessfulTest(P21, "1.1.0", resultP21)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "1",
				P2, "1.0",
				P3, "1.0.1",
				P4, "1",
				P5, "1.0",
				P6, "1.0.1",
				P7, "1",
				P8, "1.0",
				P9, "1.0.1",
				P10, "1",
				P11, "1.0",
				P12, "1.0.1",
				P13, "1",
				P14, "1.0",
				P15, "1.0.1",
				P16, "1",
				P17, "1.0",
				P18, "1.0.1",
				P19, "1",
				P20, "1.1",
				P21, "1.0.1"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "0",
				P2, "1.1",
				P3, "1.1.0",
				P4, "0",
				P5, "1.1",
				P6, "1.1.0",
				P7, "0",
				P8, "1.1",
				P9, "1.1.0",
				P10, "0",
				P11, "1.1",
				P12, "1.1.0",
				P13, "0",
				P14, "1.1",
				P15, "1.1.0",
				P16, "0",
				P17, "1.1",
				P18, "1.1.0",
				P19, "0",
				P20, "1.0",
				P21, "1.1.0"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
