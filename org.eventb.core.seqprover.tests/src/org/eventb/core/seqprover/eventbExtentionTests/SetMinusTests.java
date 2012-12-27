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
package org.eventb.core.seqprover.eventbExtentionTests;

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
public class SetMinusTests extends AbstractManualRewriterTests {

	// U \ (S /\ ... /\ T) == (U \ S) \/ ... \/ (S \ T)
	String P1 = "ℤ ∖ ({1,x} ∩ {2} ∩ {3,x}) = {2}";

	String resultP1 = "(ℤ ∖ {1,x})∪(ℤ ∖ {2})∪(ℤ ∖ {3,x})={2}";

	String P2 = "(1 = x) ⇒ {2} = ℤ ∖ ({1,x} ∩ {2} ∩ {3,x})";

	String resultP2 = "1=x⇒{2}=(ℤ ∖ {1,x})∪(ℤ ∖ {2})∪(ℤ ∖ {3,x})";

	String P3 = "∀x·x = 0 ⇒ ℤ ∖ ({1,x} ∩ {2} ∩ {3,x}) = {2}";

	String resultP3 = "∀x·x=0⇒(ℤ ∖ {1,x})∪(ℤ ∖ {2})∪(ℤ ∖ {3,x})={2}";


	// U \ (S \/ ... \/ T) == (U \ S) /\ ... /\ (S \ T)
	String P4 = "ℤ ∖ ({1,x} ∪ {2} ∪ {3,x}) = {2}";

	String resultP4 = "(ℤ ∖ {1,x})∩(ℤ ∖ {2})∩(ℤ ∖ {3,x})={2}";

	String P5 = "(1 = x) ⇒ {2} = ℤ ∖ ({1,x} ∪ {2} ∪ {3,x})";

	String resultP5 = "1=x⇒{2}=(ℤ ∖ {1,x})∩(ℤ ∖ {2})∩(ℤ ∖ {3,x})";

	String P6 = "∀x·x = 0 ⇒ ℤ ∖ ({1,x} ∪ {2} ∪ {3,x}) = {2}";

	String resultP6 = "∀x·x=0⇒(ℤ ∖ {1,x})∩(ℤ ∖ {2})∩(ℤ ∖ {3,x})={2}";


	// U \ (S \ T) == (U \ S) \/ T
	String P7 = "ℤ ∖ ({1,x} ∖ {2}) = {2}";

	String resultP7 = "(ℤ ∖ {1,x})∪{2}={2}";

	String P8 = "(1 = x) ⇒ {2} = ℤ ∖ ({1,x} ∖ {2})";

	String resultP8 = "1=x⇒{2}=(ℤ ∖ {1,x})∪{2}";

	String P9 = "∀x·x = 0 ⇒ ℤ ∖ ({1,x} ∖ {2}) = {2}";

	String resultP9 = "∀x·x=0⇒(ℤ ∖ {1,x})∪{2}={2}";


	// Failures
	String P10 = "ℕ ∖ ({1,x} ∩ {2} ∩ {3,x}) = {2}";

	String P11 = "(1 = x) ⇒ {2} = ℕ ∖ ({1,x} ∩ {2} ∩ {3,x})";

	String P12 = "∀x·x = 0 ⇒ ℕ ∖ ({1,x} ∩ {2} ∩ {3,x}) = {2}";

	String P13 = "ℕ ∖ ({1,x} ∪ {2} ∪ {3,x}) = {2}";

	String P14 = "(1 = x) ⇒ {2} = ℕ ∖ ({1,x} ∪ {2} ∪ {3,x})";

	String P15 = "∀x·x = 0 ⇒ ℕ ∖ ({1,x} ∪ {2} ∪ {3,x}) = {2}";

	String P16 = "ℕ ∖ ({1,x} ∖ {2}) = {2}";

	String P17 = "(1 = x) ⇒ {2} = ℕ ∖ ({1,x} ∖ {2})";

	String P18 = "∀x·x = 0 ⇒ ℕ ∖ ({1,x} ∖ {2}) = {2}";
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.setMinusRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.setMinusGetPositions(predicate);
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
				new SuccessfulTest(P9, "1.1.0", resultP9)
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
				P10, "0",
				P11, "1.1",
				P12, "1.1.0",
				P13, "0",
				P14, "1.1",
				P15, "1.1.0",
				P16, "0",
				P17, "1.1",
				P18, "1.1.0"
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
				P10, "",
				P11, "",
				P12, "",
				P13, "",
				P14, "",
				P15, "",
				P16, "",
				P17, "",
				P18, ""
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
