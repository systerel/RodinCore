/*******************************************************************************
 * Copyright (c) 2008, 2013 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *     Systerel - added more tests
 *******************************************************************************/
package org.rodinp.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
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
import org.rodinp.core.tests.relations.ItemRelationParserTests;
import org.rodinp.core.tests.relations.RelationsTests;
import org.rodinp.core.tests.version.BasicVersionTest;
import org.rodinp.core.tests.version.FaultyVersionTest;
import org.rodinp.internal.core.util.tests.UtilTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// $JUnit-BEGIN$
	HandleTests.class,
	TestInternalManipulation.class,
	NameGeneratorTests.class,
	DeleteTests.class,
	ClearTests.class,
	MementoTests.class,
	OpenableTests.class,
	CopyMoveResourcesTests.class,
	RodinElementDeltaTests.class,
	TestRodinDB.class,
	TestRodinProject.class,
	AttributeTests.class,
	MarkerTests.class,
	ElementTypeTests.class,
	CopyMoveElementsTests.class,
	SnapshotTests.class,
	TestFileCreation.class,
	AdapterFactoryTests.class,
	SameContentsTests.class,
	RootElementTests.class,
	AncestryTests.class,
	UtilTests.class,
	RunnableTests.class,
	RefinementTests.class,

	MBuilderTest.class,
	CBuilderTest.class,
	GraphBuilderTest.class,

	BasicVersionTest.class,
	FaultyVersionTest.class,

	DeclarationTests.class,
	DescriptorTests.class,
	IndexerRegistryTests.class,
	IndexingBridgeTests.class,
	IndexManagerTests.class,
	RodinIndexTests.class,
	QueryTests.class,
	OccurrenceKindTests.class,
	
	DeltaTests.class,
	XMLPersistorTests.class,
	
	ExportTableTests.class,
	ExportTableUsageTests.class,
	FileTableTests.class,
	FileTableUsageTests.class,
	GraphTests.class,
	NameTableTests.class,
	NameTableUsageTests.class,
	NodeTests.class,
	TotalOrderTests.class,
	TotalOrderUsageTests.class,
	
	LocationInclusionTests.class,
	RodinLocationTests.class,
	
	NullToolTest.class,
	ItemRelationParserTests.class,
	
	RelationsTests.class,
	TestElementRelationLoading.class,
	// $JUnit-END$
})
public class AllTests {

	// Now empty, all is in the annotations above

}
