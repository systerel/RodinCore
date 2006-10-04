package org.eventb.core.tests;

import org.eventb.core.testpom.AutoPOMTest;
import org.eventb.core.tests.pog.TestEvents;
import org.eventb.core.tests.pog.TestInit;
import org.eventb.core.tests.pog.TestRefines;
import org.eventb.core.tests.pog.TestSeesContext;
import org.eventb.core.tests.sc.TestAxiomsAndTheorems;
import org.eventb.core.tests.sc.TestCarrierSets;
import org.eventb.core.tests.sc.TestConstants;
import org.eventb.core.tests.sc.TestConvergence;
import org.eventb.core.tests.sc.TestEventRefines;
import org.eventb.core.tests.sc.TestExtendsContext;
import org.eventb.core.tests.sc.TestInvariantsAndTheorems;
import org.eventb.core.tests.sc.TestMachineRefines;
import org.eventb.core.tests.sc.TestVariables;
import org.eventb.core.tests.sc.TestVariant;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eventb.core.tests");
		suite.addTestSuite(AutoPOMTest.class);
		suite.addTestSuite(TestRefines.class);
		suite.addTestSuite(TestSeesContext.class);
		suite.addTestSuite(TestInit.class);
		suite.addTestSuite(TestEvents.class);
		suite.addTestSuite(TestCarrierSets.class);
		suite.addTestSuite(TestAxiomsAndTheorems.class);
		suite.addTestSuite(TestExtendsContext.class);
		suite.addTestSuite(TestVariant.class);
		suite.addTestSuite(TestSeesContext.class);
		suite.addTestSuite(TestEventRefines.class);
		suite.addTestSuite(TestMachineRefines.class);
		suite.addTestSuite(TestVariables.class);
		suite.addTestSuite(TestInvariantsAndTheorems.class);
		suite.addTestSuite(TestConstants.class);
		suite.addTestSuite(TestConvergence.class);
		suite.addTestSuite(TestEvents.class);
		//$JUnit-BEGIN$
		//$JUnit-END$
		return suite;
	}

}
