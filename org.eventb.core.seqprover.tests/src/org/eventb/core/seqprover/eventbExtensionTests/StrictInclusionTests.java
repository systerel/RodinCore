/*******************************************************************************
 * Copyright (c) 2009, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.StrictInclusionRewrites;
import org.junit.Test;

/**
 * Unit tests for the sir reasoner {@link StrictInclusionRewrites}
 * 
 * @author Laurent Voisin
 */
public class StrictInclusionTests extends AbstractManualRewriterTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.sir";
	}

	@Test
	public void testPositions() {
		assertGetPositions("{FALSE} ⊂ {TRUE}", "ROOT");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.sirGetPositions(predicate);
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("{FALSE} ⊂ {TRUE}", "", "{FALSE}⊆{TRUE}", "¬{FALSE}={TRUE}");
		assertReasonerSuccess("⊤ ⇒ {FALSE} ⊂ {TRUE}", "1", "⊤⇒{FALSE}⊆{TRUE}∧¬{FALSE}={TRUE}");
	}

	@Test
	public void failure() {
		assertReasonerFailure("{FALSE} ⊆ {TRUE}", "");
		assertReasonerFailure("{FALSE} ⊂ {TRUE}", "0");
		assertReasonerFailure("{FALSE} ⊂ {TRUE}", "3");
	}

}
