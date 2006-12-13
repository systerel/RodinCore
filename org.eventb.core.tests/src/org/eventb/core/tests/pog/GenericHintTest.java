/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.rodinp.core.IRodinFile;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericHintTest<IRF extends IRodinFile> 
extends BasicPOTest 
implements IGenericPOTest<IRF> {

	/**
	 * proper local hypothesis selection hints for theorems 
	 */
	public void testHints_00() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addTheorems(cmp, makeSList("T1", "T2", "T3"), makeSList("∀x·x>1", "∃x·x≠0", "∀x·x÷x≠0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T2/THM");
		
		sequentHasGoal(sequent, emptyEnv, "∃x·x≠0");
		sequentHasSelectionHints(sequent, emptyEnv, "∀x·x>1");
		
		sequent = getSequent(po, "T3/THM");
		
		sequentHasGoal(sequent, emptyEnv, "∀x·x÷x≠0");
		sequentHasSelectionHints(sequent, emptyEnv, "∀x·x>1", "∃x·x≠0");
		
		sequent = getSequent(po, "T3/WD");
		
		sequentHasGoal(sequent, emptyEnv, "∀x·x≠0");
		sequentHasSelectionHints(sequent, emptyEnv, "∀x·x>1", "∃x·x≠0");
	
	}
	
	/**
	 * proper local hypothesis selection hints for non-theorems 
	 */
	public void testHints_01() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addNonTheorems(cmp, makeSList("N1", "N2", "N3"), makeSList("∀x·x>1", "∃x·x≠0", "∀x·x÷x≠0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N3/WD");
		
		sequentHasGoal(sequent, emptyEnv, "∀x·x≠0");
		sequentHasSelectionHints(sequent, emptyEnv, "∀x·x>1", "∃x·x≠0");
	
	}

	
}
