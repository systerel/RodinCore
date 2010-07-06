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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.StrictInclusionRewrites;

/**
 * Unit tests for the sir reasoner {@link StrictInclusionRewrites}
 * 
 * @author Laurent Voisin
 */
public class StrictInclusionTests extends AbstractManualRewriterTests {

	private static final SuccessfulTest[] successes = new SuccessfulTest[] { //
	new SuccessfulTest("{FALSE} ⊂ {TRUE}", //
			"", //
			"{FALSE}⊆{TRUE}", "¬{FALSE}={TRUE}"//
	), //
			new SuccessfulTest("⊤ ⇒ {FALSE} ⊂ {TRUE}", //
					"1", //
					"⊤⇒{FALSE}⊆{TRUE}∧¬{FALSE}={TRUE}"//
			), //
	};

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.sir";
	}

	public String[] getTestGetPositions() {
		return new String[] { //
		"{FALSE} ⊂ {TRUE}", "ROOT", //
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.sirGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return successes;
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] { //
		"{FALSE} ⊆ {TRUE}", "", //
				"{FALSE} ⊂ {TRUE}", "0", //
		};
	}

}
