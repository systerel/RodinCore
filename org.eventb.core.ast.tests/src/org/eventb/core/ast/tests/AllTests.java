package org.eventb.core.ast.tests;

import org.eventb.core.ast.expander.tests.PartitionExpanderTests;
import org.eventb.core.ast.expander.tests.SmartFactoryTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eventb.core.ast.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestTypeCheckError.class);
		suite.addTestSuite(TestTypes.class);
		suite.addTestSuite(TestLegibility.class);
		suite.addTestSuite(TestExprTypeChecker.class);
		suite.addTestSuite(DocTests.class);
		suite.addTestSuite(TestTypeEnvironment.class);
		suite.addTestSuite(TestIdentRenaming.class);
		suite.addTestSuite(TestBA.class);
		suite.addTestSuite(TestTypedConstructor.class);
		suite.addTestSuite(TestOrigin.class);
		suite.addTestSuite(TestAST.class);
		suite.addTestSuite(TestFreeIdents.class);
		suite.addTestSuite(TestUnparse.class);
		suite.addTestSuite(TestSourceLocation.class);
		suite.addTestSuite(TestTypeChecker.class);
		suite.addTestSuite(TestSubstituteFormula.class);
		suite.addTestSuite(TestLexer.class);
		suite.addTestSuite(TestWD.class);
		suite.addTestSuite(TestTypedIdentDecl.class);
		suite.addTestSuite(TestDeBruijn.class);
		suite.addTestSuite(TestParser.class);
		suite.addTestSuite(TestTypedGeneric.class);
		suite.addTestSuite(TestCollectNamesAbove.class);
		suite.addTestSuite(TestFlattener.class);
		suite.addTestSuite(TestSimpleVisitor.class);
		suite.addTestSuite(TestPosition.class);
		suite.addTestSuite(TestVisitor.class);
		suite.addTestSuite(TestBoundIdentRenaming.class);
		suite.addTestSuite(TestEquals.class);
		suite.addTestSuite(TestFIS.class);
		suite.addTestSuite(TestErrors.class);
		suite.addTestSuite(TestLocation.class);
		suite.addTestSuite(TestIntStack.class);
		suite.addTestSuite(TestConflictResolver.class);
		suite.addTestSuite(TestGivenTypes.class);
		suite.addTestSuite(TestSubFormulas.class);
		suite.addTestSuite(TestVersionUpgrader.class);
		suite.addTestSuite(PartitionExpanderTests.class);
		suite.addTestSuite(SmartFactoryTests.class);
		suite.addTestSuite(TestPredicateVariables.class);
		suite.addTestSuite(TestFormulaInspector.class);
		//$JUnit-END$
		return suite;
	}

}
