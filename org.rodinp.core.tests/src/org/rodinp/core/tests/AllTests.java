/*******************************************************************************
 * Copyright (c) 2008, 2011 University of Dusseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Dusseldorf - initial API and implementation
 *     Systerel - added more tests
 *******************************************************************************/
package org.rodinp.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rodinp.core.tests.builder.CBuilderTest;
import org.rodinp.core.tests.builder.GraphBuilderTest;
import org.rodinp.core.tests.builder.MBuilderTest;
import org.rodinp.core.tests.builder.NullToolTest;
import org.rodinp.core.tests.indexer.DeclarationTests;
import org.rodinp.core.tests.indexer.DescriptorTests;
import org.rodinp.core.tests.indexer.IndexManagerTests;
import org.rodinp.core.tests.indexer.IndexerRegistryTests;
import org.rodinp.core.tests.indexer.IndexingBridgeTests;
import org.rodinp.core.tests.indexer.OccurrenceKindTests;
import org.rodinp.core.tests.indexer.QueryTests;
import org.rodinp.core.tests.indexer.RodinIndexTests;
import org.rodinp.core.tests.indexer.persistence.DeltaTests;
import org.rodinp.core.tests.indexer.persistence.XMLPersistorTests;
import org.rodinp.core.tests.indexer.tables.ExportTableTests;
import org.rodinp.core.tests.indexer.tables.ExportTableUsageTests;
import org.rodinp.core.tests.indexer.tables.FileTableTests;
import org.rodinp.core.tests.indexer.tables.FileTableUsageTests;
import org.rodinp.core.tests.indexer.tables.GraphTests;
import org.rodinp.core.tests.indexer.tables.NameTableTests;
import org.rodinp.core.tests.indexer.tables.NameTableUsageTests;
import org.rodinp.core.tests.indexer.tables.NodeTests;
import org.rodinp.core.tests.indexer.tables.TotalOrderTests;
import org.rodinp.core.tests.indexer.tables.TotalOrderUsageTests;
import org.rodinp.core.tests.location.LocationInclusionTests;
import org.rodinp.core.tests.location.RodinLocationTests;
import org.rodinp.core.tests.version.BasicVersionTest;
import org.rodinp.core.tests.version.FaultyVersionTest;
import org.rodinp.internal.core.util.tests.UtilTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rodinp.core.tests.*");
		// $JUnit-BEGIN$
		suite.addTestSuite(HandleTests.class);
		suite.addTestSuite(TestInternalManipulation.class);
		suite.addTestSuite(NameGeneratorTests.class);
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
		suite.addTestSuite(RefinementTests.class);

		suite.addTestSuite(MBuilderTest.class);
		suite.addTestSuite(CBuilderTest.class);
		suite.addTestSuite(GraphBuilderTest.class);

		suite.addTestSuite(BasicVersionTest.class);
		suite.addTestSuite(FaultyVersionTest.class);

		suite.addTestSuite(DeclarationTests.class);
		suite.addTestSuite(DescriptorTests.class);
		suite.addTestSuite(IndexerRegistryTests.class);
		suite.addTestSuite(IndexingBridgeTests.class);
		suite.addTestSuite(IndexManagerTests.class);
		suite.addTestSuite(RodinIndexTests.class);
		suite.addTestSuite(QueryTests.class);
		suite.addTestSuite(OccurrenceKindTests.class);
		
		suite.addTestSuite(DeltaTests.class);
		suite.addTestSuite(XMLPersistorTests.class);
		
		suite.addTestSuite(ExportTableTests.class);
		suite.addTestSuite(ExportTableUsageTests.class);
		suite.addTestSuite(FileTableTests.class);
		suite.addTestSuite(FileTableUsageTests.class);
		suite.addTestSuite(GraphTests.class);
		suite.addTestSuite(NameTableTests.class);
		suite.addTestSuite(NameTableUsageTests.class);
		suite.addTestSuite(NodeTests.class);
		suite.addTestSuite(TotalOrderTests.class);
		suite.addTestSuite(TotalOrderUsageTests.class);
		
		suite.addTestSuite(LocationInclusionTests.class);
		suite.addTestSuite(RodinLocationTests.class);
		
		suite.addTestSuite(NullToolTest.class);
		// $JUnit-END$
		return suite;
	}

}
