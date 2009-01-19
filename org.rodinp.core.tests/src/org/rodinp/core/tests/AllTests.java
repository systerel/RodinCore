package org.rodinp.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rodinp.core.tests.builder.CBuilderTest;
import org.rodinp.core.tests.builder.MBuilderTest;
import org.rodinp.core.tests.version.BasicVersionTest;
import org.rodinp.core.tests.version.FaultyVersionTest;
import org.rodinp.internal.core.util.tests.UtilTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rodinp.core.tests.*");
		// $JUnit-BEGIN$
		suite.addTestSuite(HandleTests.class);
		suite.addTestSuite(TestInternalManipulation.class);
		suite.addTestSuite(DeleteTests.class);
		suite.addTestSuite(ClearTests.class);
		suite.addTestSuite(MementoTests.class);
		suite.addTestSuite(OpenableTests.class);
		suite.addTestSuite(CopyMoveResourcesTests.class);
		suite.addTestSuite(RodinElementDeltaTests.class);
		suite.addTestSuite(TestRodinDB.class);
		suite.addTestSuite(TestRodinProject.class);
		suite.addTestSuite(AttributeTests.class);
		suite.addTestSuite(MarkerTests.class);
		suite.addTestSuite(ElementTypeTests.class);
		suite.addTestSuite(CopyMoveElementsTests.class);
		suite.addTestSuite(SnapshotTests.class);
		suite.addTestSuite(TestFileCreation.class);
		suite.addTestSuite(AdapterFactoryTests.class);
		suite.addTestSuite(SameContentsTests.class);
		suite.addTestSuite(RootElementTests.class);
		suite.addTestSuite(AncestryTests.class);
		suite.addTestSuite(UtilTests.class);
		suite.addTestSuite(RunnableTests.class);

		suite.addTestSuite(MBuilderTest.class);
		suite.addTestSuite(CBuilderTest.class);

		suite.addTestSuite(BasicVersionTest.class);
		suite.addTestSuite(FaultyVersionTest.class);

		// $JUnit-END$
		return suite;
	}

}
