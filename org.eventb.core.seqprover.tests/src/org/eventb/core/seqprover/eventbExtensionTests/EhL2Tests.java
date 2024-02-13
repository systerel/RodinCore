/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L2;

import org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level;
import org.junit.Test;

public class EhL2Tests extends EhL1Tests {

	public EhL2Tests() {
		super(L2);
	}

	protected EhL2Tests(Level level) {
		super(level);
	}

	/**
	 * Test whether the rewritten hypothesis is deselected or hidden depending on
	 * other hypotheses.
	 */
	@Test
	public void testDeselectHide() throws Exception {
		// Name not used in other hypotheses: equality hidden
		assertReasonerSuccess("x = 0 |- x + 1 > 0", makeInput("x = 0"), //
				"{}[x = 0][][] |- 0 + 1 > 0");
		// Name only used in selected hypotheses: equality hidden
		assertReasonerSuccess("x = 0 ;; x < 10 |- x + 1 > 0", makeInput("x = 0"), //
				"{}[x = 0][x < 10][0 < 10] |- 0 + 1 > 0");
		// Name only used in hidden hypotheses: equality hidden
		assertReasonerSuccess(genFullSeq("x < 10 ;; x = 0 ;H; x < 10 ;S; x = 0 |- x + 1 > 0", ff), makeInput("x = 0"), //
				"{}[x < 10 ;; x = 0][][] |- 0 + 1 > 0");
		// Name only used in default hypotheses: equality deselected
		assertReasonerSuccess(genFullSeq("x < 10 ;; x = 0 ;H; ;S; x = 0 |- x + 1 > 0", ff), makeInput("x = 0"), //
				"{}[][x < 10 ;; x = 0][] |- 0 + 1 > 0");
		// Name used in selected and hidden hypotheses: equality hidden
		assertReasonerSuccess(genFullSeq("x < 15 ;; x = 0 ;; x < 10 ;H; x < 15 ;S; x = 0 ;; x < 10 |- x + 1 > 0", ff),
				makeInput("x = 0"), //
				"{}[x < 15 ;; x = 0][x < 10][0 < 10] |- 0 + 1 > 0");
		// Name used in selected and default hypotheses: equality deselected
		assertReasonerSuccess(genFullSeq("x < 15 ;; x = 0 ;; x < 10 ;H; ;S; x = 0 ;; x < 10 |- x + 1 > 0", ff),
				makeInput("x = 0"), //
				"{}[][x < 15 ;; x < 10 ;; x = 0][0 < 10] |- 0 + 1 > 0");
		// Name used in hidden and default hypotheses: equality deselected
		assertReasonerSuccess(genFullSeq("x < 15 ;; x = 0 ;; x < 10 ;H; x < 15 ;S; x = 0 |- x + 1 > 0", ff),
				makeInput("x = 0"), //
				"{}[x < 15][x < 10 ;; x = 0][] |- 0 + 1 > 0");
	}

}
