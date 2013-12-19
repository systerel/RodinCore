/*******************************************************************************
 * Copyright (c) 2008, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - mathematical language V2
 *     Systerel - changed condition for including WD predicates
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.tests.BuilderTest;
import org.eventb.internal.core.pom.POLoader;
import org.junit.Test;

/**
 * This class contains unit tests to test the proof loader.
 * 
 * <p>
 * Tests Done :
 * <ul>
 * <li> The global and local hypotheses are correctly loaded.
 * <li> The global and local type environments are correctly loaded.
 * <li> The assumed WD lemmas for the goal and hyps are correctly generated. 
 * </ul> 
 * </p>
 * 
 * TODO : Test that selection hints are properly used.
 * 
 * @author Farhad Mehta
 *
 */
public class POLoaderTest extends BuilderTest {

	private IPORoot poRoot;

	private void createPOFile() throws CoreException {
		poRoot = createPOFile("x");
		IPOPredicateSet hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				mTypeEnvironment("x=ℤ"),
				"1=1", "2=2", "x∈ℕ"
		);
		IPOPredicateSet hyp1 = POUtil.addPredicateSet(poRoot, "hyp1", hyp0,
				mTypeEnvironment("y=ℤ"),
				"1=1", "2=2", "y∈ℕ", "x÷x = 1"
		);
		// Empty local type environment and local hyps
		POUtil.addSequent(poRoot, "PO1", 
				"x ≠ 0",
				hyp0, 
				mTypeEnvironment()
		);
		// With local type environment and local hyps
		POUtil.addSequent(poRoot, "PO2", 
				"x ≠ 0",
				hyp0, 
				mTypeEnvironment("z=ℤ"),
				"z=3"
		);
		// With goal with implicit WD asumption
		POUtil.addSequent(poRoot, "PO3", 
				"x÷x = 1",
				hyp0, 
				mTypeEnvironment()
		);
		// With goal, global hyp & local hyp with implicit WD assumption
		// WD assumption for goal contains a conjunction that needs to be split
		POUtil.addSequent(poRoot, "PO4", 
				"x÷(x+y) = 1 ∧ x÷(y+x) = 1", 
				hyp1, 
				mTypeEnvironment(),
				"y÷y = 1"
		);
		// With hypotheses and goal producing long WD POs that must not be loaded
		POUtil.addSequent(poRoot, "WDFiltering",
				"∀x·2÷x = 1",
				hyp0,
				mTypeEnvironment("c=BOOL"),
				"∀y·3÷y = 1",
				"c = TRUE ⇒ (∀z·5÷z = 1)"
		);
		saveRodinFileOf(poRoot);
	}

	
	@Test
	public final void testReadPO() throws CoreException {
		
		createPOFile();
		
		final IPOSequent[] poSequents = poRoot.getSequents();
				
		String[] expectedSequents = {
				"{x=ℤ}[][1=1, 2=2, x∈ℕ][] |- x≠0",
				"{z=ℤ, x=ℤ}[][1=1, 2=2, x∈ℕ, z=3][] |- x≠0",
				"{x=ℤ}[][1=1, 2=2, x∈ℕ, x≠0][] |- x ÷ x=1",
				"{y=ℤ, x=ℤ}[][1=1, 2=2, x∈ℕ, y∈ℕ, x ÷ x=1, x≠0, y ÷ y=1, y≠0,"
						+ " x+y≠0, x ÷ (x+y)=1⇒y+x≠0][] |- x ÷ (x+y)=1∧x ÷ (y+x)=1",
				"{c=BOOL, x=ℤ}[][1=1, 2=2, x∈ℕ, ∀y·3 ÷ y=1,"
						+ " c=TRUE⇒(∀z·5 ÷ z=1)][]"
						+ " |- ∀x·2 ÷ x=1", //
		};
		
		assertEquals("Wrong number of POs in PO file",poSequents.length, expectedSequents.length);

		for (int i = 0; i < expectedSequents.length; i++) {
			
			final IPOSequent poSequent = poSequents[i];
			assertTrue("POSequent "+ poSequent.getElementName() +" does not exist",poSequent.exists());
			IProverSequent seq = POLoader.readPO(poSequent, factory);
			assertNotNull("Error generating prover sequent", seq);
			assertEquals("Sequents for " + poSequent.getElementName() + " do not match",
					expectedSequents[i],seq.toString());
			assertSame("wrong origin for " + poSequent.getElementName(),
					poSequent, seq.getOrigin());
		}
		
	}
	
	public static String[] mp(String... strings) {
		return strings;
	}
	
	public static String[] mh(String... strings) {
		return strings;
	}

}
