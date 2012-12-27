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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RelOvrRewrites;

/**
 * Unit tests for the Relation Overriding Rewrites reasoner
 * {@link RelOvrRewrites}
 * 
 * @author htson
 */
public class RelOvrTests extends AbstractManualRewriterTests {

	// p <+ ... <+ q <+ r <+ ... <+ s ==
	//             (dom(r <+ ... <+ s) <<| (p <+ ... <+ q)) \/ (r <+ ... <+ s)
	String P1 = "{x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP1 = "(dom({2 ↦ x}{x ↦ 3}{x ↦ 4}) ⩤ {x ↦ 1})∪({2 ↦ x}{x ↦ 3}{x ↦ 4})={x ↦ 7}";

	String P2 = "1 = x ⇒ {x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP2 = "1=x⇒(dom({2 ↦ x}{x ↦ 3}{x ↦ 4}) ⩤ {x ↦ 1})∪({2 ↦ x}{x ↦ 3}{x ↦ 4})={x ↦ 7}";

	String P3 = "∀x·x = 0 ⇒ {x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP3 = "∀x·x=0⇒(dom({2 ↦ x}{x ↦ 3}{x ↦ 4}) ⩤ {x ↦ 1})∪({2 ↦ x}{x ↦ 3}{x ↦ 4})={x ↦ 7}";

	String P4 = "{x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP4 = "(dom({x ↦ 3}{x ↦ 4}) ⩤ ({x ↦ 1}{2 ↦ x}))∪({x ↦ 3}{x ↦ 4})={x ↦ 7}";

	String P5 = "1 = x ⇒ {x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP5 = "1=x⇒(dom({x ↦ 3}{x ↦ 4}) ⩤ ({x ↦ 1}{2 ↦ x}))∪({x ↦ 3}{x ↦ 4})={x ↦ 7}";

	String P6 = "∀x·x = 0 ⇒ {x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP6 = "∀x·x=0⇒(dom({x ↦ 3}{x ↦ 4}) ⩤ ({x ↦ 1}{2 ↦ x}))∪({x ↦ 3}{x ↦ 4})={x ↦ 7}";

	String P7 = "{x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP7 = "(dom({x ↦ 4}) ⩤ ({x ↦ 1}{2 ↦ x}{x ↦ 3}))∪{x ↦ 4}={x ↦ 7}";

	String P8 = "1 = x ⇒ {x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP8 = "1=x⇒(dom({x ↦ 4}) ⩤ ({x ↦ 1}{2 ↦ x}{x ↦ 3}))∪{x ↦ 4}={x ↦ 7}";

	String P9 = "∀x·x = 0 ⇒ {x ↦ 1}  {2 ↦ x}  {x ↦ 3}  {x ↦ 4} = {x ↦ 7}";

	String resultP9 = "∀x·x=0⇒(dom({x ↦ 4}) ⩤ ({x ↦ 1}{2 ↦ x}{x ↦ 3}))∪{x ↦ 4}={x ↦ 7}";


	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.relOvrRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.relOvrGetPositions(predicate);
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
				new SuccessfulTest(P9, "1.1.0.3", resultP9)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0.0",
				P2, "1.0.0",
				P3, "1.1.0.0",
				P4, "0.0",
				P5, "1.0.0",
				P6, "1.1.0.0",
				P7, "0.0",
				P8, "1.0.0",
				P9, "1.1.0.0"
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
				P9, "1.1.0.1\n"+"1.1.0.2\n"+"1.1.0.3"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
