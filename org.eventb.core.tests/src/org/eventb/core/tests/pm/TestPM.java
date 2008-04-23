package org.eventb.core.tests.pm;

import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.tests.pom.POUtil;
import org.rodinp.core.RodinDBException;

public abstract class TestPM extends BasicTest {
	IUserSupportManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = EventBPlugin.getUserSupportManager();
		// Disable the Post tactic
		EventBPlugin.getPostTacticPreference().setEnabled(false);
		// Enable the POM-Tactic
		EventBPlugin.getAutoTacticPreference().setEnabled(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	IPOFile createPOFile(String fileName) throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		IPOPredicateSet hyp0 = POUtil.addPredicateSet(poFile, "hyp0", null,
				mTypeEnvironment("x", "ℤ"), "¬x=1", "¬x=2", "x∈ℕ");
		POUtil.addSequent(poFile, "PO1", "¬x=1 ∧¬x=2 ∧x ∈ℕ", hyp0,
				mTypeEnvironment());
		POUtil.addSequent(poFile, "PO2", "¬x=1 ∧¬x=2 ∧x ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y", "ℤ"), "y∈ℕ");
		POUtil.addSequent(poFile, "PO3", "3=3", hyp0, mTypeEnvironment(),
				"3=3");
		POUtil.addSequent(poFile, "PO4", "¬x=1 ∧ ¬x=2 ∧ x∈ℕ ∧ 3=3", hyp0,
				mTypeEnvironment(), "3=3");
		POUtil.addSequent(poFile, "PO5", "¬x=1 ∧¬x=2 ∧y ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y", "ℤ"), "y∈ℕ");
		POUtil.addSequent(poFile, "PO6", "¬x=1 ∧¬x=2 ∧x ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y", "ℤ", "x'", "ℤ"), "y∈ℕ");
		POUtil.addSequent(poFile, "PO7", "y∈ℕ", hyp0,
				mTypeEnvironment("y", "ℤ"), "x=x");
		poFile.save(null, true);
		return poFile;
	}
	
	void assertString(String message, String actual, String expected) {
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
