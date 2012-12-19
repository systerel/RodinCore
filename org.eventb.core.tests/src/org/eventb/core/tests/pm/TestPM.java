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
 *******************************************************************************/
package org.eventb.core.tests.pm;

import static junit.framework.Assert.fail;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.tests.pom.POUtil;
import org.junit.Before;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public abstract class TestPM extends BasicTest {
	IUserSupportManager manager;

	@Before
	public void setUpTPM() throws Exception {
		manager = EventBPlugin.getUserSupportManager();
		disablePostTactic();
		enableTestAutoProver();
		manager.setConsiderHiddenHypotheses(false);
	}

	
	IPORoot createPOFile(String fileName) throws RodinDBException {
		IRodinFile poFile = rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		IPORoot poRoot = (IPORoot) poFile.getRoot();
		IPOPredicateSet hyp0 = POUtil.addPredicateSet(poRoot, "hyp0", null,
				mTypeEnvironment("x=ℤ"), "¬x=1", "¬x=2", "x∈ℕ");
		POUtil.addSequent(poRoot, "PO1", "¬x=1 ∧¬x=2 ∧x ∈ℕ", hyp0,
				mTypeEnvironment());
		POUtil.addSequent(poRoot, "PO2", "¬x=1 ∧¬x=2 ∧x ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y=ℤ"), "y∈ℕ");
		POUtil.addSequent(poRoot, "PO3", "3=3", hyp0, mTypeEnvironment(),
				"3=3");
		POUtil.addSequent(poRoot, "PO4", "¬x=1 ∧ ¬x=2 ∧ x∈ℕ ∧ 3=3", hyp0,
				mTypeEnvironment(), "3=3");
		POUtil.addSequent(poRoot, "PO5", "¬x=1 ∧¬x=2 ∧y ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y=ℤ"), "y∈ℕ");
		POUtil.addSequent(poRoot, "PO6", "¬x=1 ∧¬x=2 ∧x ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y=ℤ; x'=ℤ"), "y∈ℕ");
		POUtil.addSequent(poRoot, "PO7", "y∈ℕ", hyp0,
				mTypeEnvironment("y=ℤ"), "x=x");
		poFile.save(null, true);
		return poRoot;
	}
	
	void assertString(String message, String actual, String expected) {
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
