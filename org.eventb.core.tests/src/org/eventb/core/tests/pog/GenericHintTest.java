/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.junit.Test;
import org.rodinp.core.IInternalElement;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericHintTest<F extends IInternalElement> 
extends GenericEventBPOTest<F> {

	/**
	 * proper local hypothesis selection hints for theorems 
	 */
	@Test
	public void testHints_00() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("T1", "T2", "T3"), makeSList("∀x·x>1", "∃x·x≠0", "∀x·x÷x≠0"), true, true, true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
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
	@Test
	public void testHints_01() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("N1", "N2", "N3"), makeSList("∀x·x>1", "∃x·x≠0", "∀x·x÷x≠0"), false, false, false);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N3/WD");
		
		sequentHasGoal(sequent, emptyEnv, "∀x·x≠0");
		sequentHasSelectionHints(sequent, emptyEnv, "∀x·x>1", "∃x·x≠0");
	
	}

	
}
