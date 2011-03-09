/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PartitionRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PartitionRewrites;

/**
 * Tests automatic reasoner {@link PartitionRewriterImpl}
 * 
 * @author Nicolas Beauger
 *
 */
public class PartitionTests extends AbstractManualRewriterTests {
	
	// The automatic reasoner to be tested
	private static final PartitionRewrites reasoner = new PartitionRewrites();
	

	private static final String P1 = "partition({1})";
	private static final String resultP1 = "{1}=∅";
	
	private static final String P2 = "0=1 ⇒ partition(S, {1})";
	private static final String resultP2 = "0=1⇒S={1}";
	
	private static final String P3 = "∀x·x = 4 ⇒ partition(S, {1}, {2}, {3})";
	private static final String resultP3 = "∀x·x=4⇒S={1,2,3}∧¬1=2∧¬1=3∧¬2=3";
	
	@Override
	public String getReasonerID() {
		return reasoner.getReasonerID();
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.partitionGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1),
				new SuccessfulTest(P2, "1", resultP2),
				new SuccessfulTest(P3, "1.1", resultP3),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "1",
				P2, "1.0",
				P3, "1"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "ROOT",
				P2, "1",
				P3, "1.1",
		};
	}

}
