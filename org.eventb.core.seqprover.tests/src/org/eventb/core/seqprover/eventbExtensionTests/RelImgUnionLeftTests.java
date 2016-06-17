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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RelImgUnionRightRewrites;

/**
 * Unit tests for the Relational Image with Union Right Rewrites reasoner
 * {@link RelImgUnionRightRewrites}
 * 
 * @author htson
 */
public class RelImgUnionLeftTests extends AbstractManualRewriterTests {

	// (p \/ ... \/ q)[S] == p[S] \/ ... \/ q[S]
	String P1 = "1 ∈ (p ∪ q ∪ r)[{x, 1}]";
	
	String resultP1 = "1∈p[{x,1}]∪q[{x,1}]∪r[{x,1}]";
	
	String P2 = "(0 = 1) ⇒ 1 ∈ (p ∪ q ∪ r)[{x, 1}]";

	String resultP2 = "0=1⇒1∈p[{x,1}]∪q[{x,1}]∪r[{x,1}]";

	String P3 = "∀x·x = 0 ⇒ 1 ∈ (p ∪ q ∪ r)[{x, 1}]";

	String resultP3 = "∀x·x=0⇒1∈p[{x,1}]∪q[{x,1}]∪r[{x,1}]";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.relImgUnionLeftRewrites";
	}
		
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.relImgUnionLeftGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "1", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
				new SuccessfulTest(P3, "1.1.1", resultP3)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0",
				P3, "1.1.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "1",
				P2, "1.1",	
				P3, "1.1.1"	
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
