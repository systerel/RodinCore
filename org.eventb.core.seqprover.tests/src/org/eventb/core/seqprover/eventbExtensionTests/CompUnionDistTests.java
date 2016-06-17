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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.CompUnionDistRewrites;

/**
 * Unit tests for the Composition/Union Distribution Rewrites reasoner
 * {@link CompUnionDistRewrites}
 * 
 * @author htson
 */
public class CompUnionDistTests extends AbstractManualRewriterTests {

	// p; ...;(q \/ ... \/ r); ...; s ==
	//          (p; ...;q; ...; s) \/ ... \/ (p; ...;r; ...; s)
	String P1 = "{x ↦ 1};{x ↦ 2};({x ↦ 3} ∪ {x ↦ 4} ∪ {x ↦ 5});{x ↦ 6};{x ↦ 7} = {x↦0}";

	String resultP1 = "({x ↦ 1};{x ↦ 2};{x ↦ 3};{x ↦ 6};{x ↦ 7})∪({x ↦ 1};{x ↦ 2};{x ↦ 4};{x ↦ 6};{x ↦ 7})∪({x ↦ 1};{x ↦ 2};{x ↦ 5};{x ↦ 6};{x ↦ 7})={x ↦ 0}";

	String P2 = "1 = x ⇒ {x ↦ 1};{x ↦ 2};({x ↦ 3} ∪ {x ↦ 4} ∪ {x ↦ 5});{x ↦ 6};{x ↦ 7} = {x↦0}";

	String resultP2 = "1=x⇒({x ↦ 1};{x ↦ 2};{x ↦ 3};{x ↦ 6};{x ↦ 7})∪({x ↦ 1};{x ↦ 2};{x ↦ 4};{x ↦ 6};{x ↦ 7})∪({x ↦ 1};{x ↦ 2};{x ↦ 5};{x ↦ 6};{x ↦ 7})={x ↦ 0}";

	String P3 = "∀x·x = 0 ⇒ {x ↦ 1};{x ↦ 2};({x ↦ 3} ∪ {x ↦ 4} ∪ {x ↦ 5});{x ↦ 6};{x ↦ 7} = {x↦0}";

	String resultP3 = "∀x·x=0⇒({x ↦ 1};{x ↦ 2};{x ↦ 3};{x ↦ 6};{x ↦ 7})∪({x ↦ 1};{x ↦ 2};{x ↦ 4};{x ↦ 6};{x ↦ 7})∪({x ↦ 1};{x ↦ 2};{x ↦ 5};{x ↦ 6};{x ↦ 7})={x ↦ 0}";


	// Failures
	String P4 = "{x ↦ 1};{x ↦ 2};({x ↦ 3} ∩ {x ↦ 4} ∩ {x ↦ 5});{x ↦ 6};{x ↦ 7} = {x↦0}";

	String P5 = "1 = x ⇒ {x ↦ 1};{x ↦ 2};({x ↦ 3} ∩ {x ↦ 4} ∩ {x ↦ 5});{x ↦ 6};{x ↦ 7} = {x↦0}";

	String P6 = "∀x·x = 0 ⇒ {x ↦ 1};{x ↦ 2};({x ↦ 3} ∩ {x ↦ 4} ∩ {x ↦ 5});{x ↦ 6};{x ↦ 7} = {x↦0}";
	
	String P7 = "{x ↦ 1}∘{x ↦ 2}∘({x ↦ 3} ∩ {x ↦ 4} ∩ {x ↦ 5})∘{x ↦ 6}∘{x ↦ 7} = {x↦0}";

	String P8 = "1 = x ⇒ {x ↦ 1}∘{x ↦ 2}∘({x ↦ 3} ∩ {x ↦ 4} ∩ {x ↦ 5})∘{x ↦ 6}∘{x ↦ 7} = {x↦0}";

	String P9 = "∀x·x = 0 ⇒ {x ↦ 1}∘{x ↦ 2}∘({x ↦ 3} ∩ {x ↦ 4} ∩ {x ↦ 5})∘{x ↦ 6}∘{x ↦ 7} = {x↦0}";
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.compUnionDistRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.compUnionDistGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "0.2", resultP1),
				new SuccessfulTest(P2, "1.0.2", resultP2),
				new SuccessfulTest(P3, "1.1.0.2", resultP3),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0.3",
				P2, "1.0.3",
				P3, "1.1.0.3",
				P4, "0.2",
				P5, "1.0.2",
				P6, "1.1.0.2",
				P7, "0.2",
				P8, "1.0.2",
				P9, "1.1.0.2"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "0.2",
				P2, "1.0.2",
				P3, "1.1.0.2",
				P4, "",
				P5, "",
				P6, "",
				P7, "",
				P8, "",
				P9, ""
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
