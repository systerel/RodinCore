package org.eventb.core.tests.pm;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.tests.pom.POUtil;
import org.rodinp.core.RodinDBException;

public abstract class TestPM extends BasicTest {
	IUserSupportManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = EventBPlugin.getPlugin().getUserSupportManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Creates a new type environment from the given strings. The given strings
	 * are alternatively an identifier name and its type.
	 * 
	 * @param strings
	 *            an even number of strings
	 * @return a new type environment
	 */
	protected ITypeEnvironment mTypeEnvironment(String... strings) {
		// even number of strings
		assert (strings.length & 1) == 0;
		final ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < strings.length; i += 2) {
			final String name = strings[i];
			final String typeString = strings[i + 1];
			final IParseResult pResult = factory.parseType(typeString);
			assertTrue("Parsing type failed for " + typeString, pResult
					.isSuccess());
			final Type type = pResult.getParsedType();
			result.addName(name, type);
		}
		return result;
	}

	IPOFile createPOFile(String fileName) throws RodinDBException {
		IPOFile poFile = (IPOFile) rodinProject.getRodinFile(fileName + ".bpo");
		poFile.create(true, null);
		IPOPredicateSet hyp0 = POUtil.addPredicateSet(poFile, "hyp0", null,
				mTypeEnvironment("x", "ℤ"), "1=1", "2=2", "x∈ℕ");
		POUtil.addSequent(poFile, "PO1", "1=1 ∧2=2 ∧x ∈ℕ", hyp0,
				mTypeEnvironment());
		POUtil.addSequent(poFile, "PO2", "1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y", "ℤ"), "y∈ℕ");
		POUtil.addSequent(poFile, "PO3", "∃x·x=3", hyp0, mTypeEnvironment(),
				"3=3");
		POUtil.addSequent(poFile, "PO4", "1=1 ∧2=2 ∧x ∈ℕ∧(∃x·(x=3))", hyp0,
				mTypeEnvironment(), "3=3");
		POUtil.addSequent(poFile, "PO5", "1=1 ∧2=2 ∧y ∈ℕ∧y ∈ℕ", hyp0,
				mTypeEnvironment("y", "ℤ"), "y∈ℕ");
		POUtil.addSequent(poFile, "PO6", "1=1 ∧2=2 ∧x ∈ℕ∧y ∈ℕ", hyp0,
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
