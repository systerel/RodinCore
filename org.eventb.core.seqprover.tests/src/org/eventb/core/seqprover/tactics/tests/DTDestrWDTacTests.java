/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.dtDestrWD;
import static org.eventb.core.seqprover.tests.TestLib.genIdent;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.reasonerExtentionTests.RecordDatatype;
import org.eventb.core.seqprover.reasonerExtentionTests.SimpleDatatype;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>DTDestrWDTac</code>.
 */
public class DTDestrWDTacTests extends AbstractTacticTests {

	public DTDestrWDTacTests() {
		super(new AutoTactics.DTDestrWDTac(),
				"org.eventb.core.seqprover.dtDestrWDTac",//
				RecordDatatype.getInstance(), //
				SimpleDatatype.getInstance());
	}

	/**
	 * Ensures that the tactic succeeds
	 */
	@Test
	public void success() {
		final FreeIdentifier prm0 = genIdent("p_intDestr", "ℤ", ff);
		final FreeIdentifier prm1 = genIdent("p_boolDestr", "BOOL", ff);
		assertSuccess(" ;H; ;S; |- ∃m,n·x=rd(m,n)",
				dtDestrWD("2.0", prm0, prm1));
	}

	/**
	 * Ensures that the tactic fails when the datatype is not a record
	 */
	@Test
	public void notRecord() {
		assertFailure(" ;H; ;S; |- ∃m,n·x=cons2(m,n)");
	}

	/**
	 * Ensures that the tactic fails when the the tree is not closed
	 */
	@Test
	public void notClosed() {
		assertFailure(" ;H; ;S; |- ∃m,n·x=rd(m+m,n)");
	}

}
