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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomRanUnionDistRewrites;

/**
 * Unit tests for the Domain or Range /Union Distribution Rewrites reasoner
 * {@link DomRanUnionDistRewrites}
 * 
 * @author htson
 */
public class DomRanUnionDistTests extends AbstractManualRewriterTests {

	// dom(p \/ ... \/ q) == dom(p) \/ ... \/ dom(q)
	String P1 = "dom(p ∪ {0 ↦ x} ∪ {x ↦ 1}) = {x}";
	
	String resultP1 = "dom(p)∪dom({0 ↦ x})∪dom({x ↦ 1})={x}";
	
	String P2 = "(1 = x) ⇒ dom(p ∪ {0 ↦ x} ∪ {x ↦ 1}) = {x}";

	String resultP2 = "1=x⇒dom(p)∪dom({0 ↦ x})∪dom({x ↦ 1})={x}";

	String P3 = "∀x·x = 0 ⇒ dom(p ∪ {0 ↦ x} ∪ {x ↦ 1}) = {x}";

	String resultP3 = "∀x·x=0⇒dom(p)∪dom({0 ↦ x})∪dom({x ↦ 1})={x}";


	// ran(p \/ ... \/ q) == ran(p) \/ ... \/ ran(q)
	String P4 = "ran(p ∪ {0 ↦ x} ∪ {x ↦ 1}) = {x}";
	
	String resultP4 = "ran(p)∪ran({0 ↦ x})∪ran({x ↦ 1})={x}";
	
	String P5 = "(1 = x) ⇒ ran(p ∪ {0 ↦ x} ∪ {x ↦ 1}) = {x}";

	String resultP5 = "1=x⇒ran(p)∪ran({0 ↦ x})∪ran({x ↦ 1})={x}";

	String P6 = "∀x·x = 0 ⇒ ran(p ∪ {0 ↦ x} ∪ {x ↦ 1}) = {x}";

	String resultP6 = "∀x·x=0⇒ran(p)∪ran({0 ↦ x})∪ran({x ↦ 1})={x}";


	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.domRanUnionDistRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.domRanUnionDistGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "0", resultP1),
				new SuccessfulTest(P2, "1.0", resultP2),
				new SuccessfulTest(P3, "1.1.0", resultP3),
				new SuccessfulTest(P4, "0", resultP4),
				new SuccessfulTest(P5, "1.0", resultP5),
				new SuccessfulTest(P6, "1.1.0", resultP6)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "1",
				P2, "1.1",
				P3, "1.1.1",
				P4, "1",
				P5, "1.1",
				P6, "1.1.1"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "0",
				P2, "1.0",
				P3, "1.1.0",
				P4, "0",
				P5, "1.0",
				P6, "1.1.0"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
