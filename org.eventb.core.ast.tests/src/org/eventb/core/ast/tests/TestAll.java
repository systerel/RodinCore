package org.eventb.core.ast.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author franz
 *
 */
public class TestAll {

	/**
	 * Returns the suite of all unit tests of this project.
	 * 
	 * @return the suite of all tests.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eventb.core.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestLexer.class);
		suite.addTestSuite(TestAST.class);
		suite.addTestSuite(TestConflictResolver.class);
		suite.addTestSuite(TestParser.class);
		suite.addTestSuite(TestDeBruijn.class);
		suite.addTestSuite(TestUnparse.class);
		suite.addTestSuite(TestErrors.class);
		suite.addTestSuite(TestLegibility.class);
		suite.addTestSuite(TestFlattener.class);
		suite.addTestSuite(TestTypeChecker.class);
		suite.addTestSuite(TestFreeIdents.class);
		suite.addTestSuite(TestBoundIdentRenaming.class);
		suite.addTestSuite(TestEquals.class);
		suite.addTestSuite(TestVisitor.class);
		suite.addTestSuite(TestWD.class);
		suite.addTestSuite(TestSubstituteFormula.class);
		suite.addTestSuite(TestTypes.class);
		suite.addTestSuite(TestBA.class);
		suite.addTestSuite(TestFIS.class);
		suite.addTestSuite(TestTypeEnvironment.class);
		// suite.addTestSuite(TestTypedConstructor.class);
		//$JUnit-END$
		return suite;
	}

}
