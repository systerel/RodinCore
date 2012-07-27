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

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.reasonerExtentionTests.RecordDatatype;
import org.eventb.core.seqprover.reasonerExtentionTests.SimpleDatatype;
import org.eventb.core.seqprover.tests.ProverSequentTests;
import org.junit.Test;

/**
 * Unit tests for the auto-tactic <code>DTDestrWDTac</code>.
 */
public class DTDestrWDTacTests extends AbstractTacticTests {

	public DTDestrWDTacTests() {
		super(new AutoTactics.DTDestrWDTac(),
				"org.eventb.core.seqprover.dtDestrWDTac");
	}

	private static final FormulaFactory DEFAULT_FACTORY = ProverSequentTests.factory;
	private static final IDatatype RECORD_DT = DEFAULT_FACTORY
			.makeDatatype(RecordDatatype.getInstance());
	private static final IDatatype SIMPLE_DT = DEFAULT_FACTORY
	.makeDatatype(SimpleDatatype.getInstance());
	private static final Set<IFormulaExtension> EXTENSIONS = new HashSet<IFormulaExtension>();
	static {
		EXTENSIONS.addAll(RECORD_DT.getExtensions());
		EXTENSIONS.addAll(SIMPLE_DT.getExtensions());
	}

	private static final FormulaFactory DT_FAC = FormulaFactory
			.getInstance(EXTENSIONS);

	/**
	 * Ensures that the tactic succeeds
	 */
	@Test
	public void success() {
		setFormulaFactory(DT_FAC);
		final FreeIdentifier prm0 = DT_FAC.makeFreeIdentifier("p_intDestr",
				null, DT_FAC.makeIntegerType());
		final FreeIdentifier prm1 = DT_FAC.makeFreeIdentifier("p_boolDestr",
				null, DT_FAC.makeBooleanType());
		assertSuccess(" ;H; ;S; |- ∃m,n·x=rd(m,n)",
				TreeShape.dtDestrWD("2.0", prm0, prm1));
	}

	/**
	 * Ensures that the tactic fails when the datatype is not a record
	 */
	@Test
	public void notRecord() {
		setFormulaFactory(DT_FAC);
		assertFailure(" ;H; ;S; |- ∃m,n·x=cons2(m,n)");
	}

	/**
	 * Ensures that the tactic fails when the the tree is not closed
	 */
	@Test
	public void notClosed() {
		setFormulaFactory(DT_FAC);
		assertFailure(" ;H; ;S; |- ∃m,n·x=rd(m+m,n)");
	}

}
