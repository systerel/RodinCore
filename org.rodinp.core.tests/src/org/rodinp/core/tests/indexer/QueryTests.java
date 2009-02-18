/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.core.tests.indexer;

import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.core.tests.basis.NamedElement2;
import org.rodinp.core.tests.indexer.tables.DependenceTable;
import org.rodinp.core.tests.indexer.tables.FakeDependenceIndexer;
import org.rodinp.internal.core.debug.DebugHelpers;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.IndexManager;
import org.rodinp.internal.core.indexer.tables.ExportTable;
import org.rodinp.internal.core.indexer.tables.RodinIndex;

/**
 * @author Nicolas Beauger
 */
public class QueryTests extends IndexTests {

	private static final String DECLARATIONS = "declarations";
	private static final String OCCURRENCES = "occurrences";
	private static final String intName1 = "intName1";
	private static final String intName2 = "intName2";
	private static final String intName3 = "intName3";
	private static final String intName4 = "intName4";
	private static final String name1 = "eltName1";
	private static final String name2 = "eltName2";
	private static final String name3 = "eltName3";
	private static final String name4 = "eltName4";
	private static final RodinIndex rodinIndex = new RodinIndex();
	private static final ExportTable f1exportsElt1Elt4 = new ExportTable();
	private static final DependenceTable f2DepsOnf1 = new DependenceTable();

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static NamedElement2 elt3;
	private static NamedElement2 elt4;
	private static IDeclaration declElt1;
	private static IDeclaration declElt2;
	private static IDeclaration declElt3;
	private static IDeclaration declElt4;
	private static IOccurrence occElt1F1;
	private static IOccurrence occElt1K2F2;
	
	public QueryTests(String name) {
		super(name);
	}
	
	private void init() throws Exception {
		project = createRodinProject("P");
		file1 = createRodinFile(project, "query.test");
		file2 = createRodinFile(project, "query2.test");
		elt1 = createNamedElement(file1, intName1);
		elt2 = createNamedElement(file1, intName2);
		elt3 = createNamedElement2(file2, intName3);
		elt4 = createNamedElement2(file1, intName4);

		declElt1 = new Declaration(elt1, name1);
		declElt2 = new Declaration(elt2, name2);
		declElt3 = new Declaration(elt3, name3);
		declElt4 = new Declaration(elt4, name4);
		occElt1F1 = makeDescAndDefaultOcc(rodinIndex, declElt1, file1.getRoot());
		occElt1K2F2 = addInternalOccurrence(rodinIndex, declElt1,
				file2.getRoot(), TEST_KIND_2);
		makeDescAndDefaultOcc(rodinIndex, declElt3, file2.getRoot());
		makeDescAndDefaultOcc(rodinIndex, declElt4, file1.getRoot());
		// elt2 will not be indexed
		
		f1exportsElt1Elt4.add(file1, declElt1);
		f1exportsElt1Elt4.add(file1, declElt4);
		f2DepsOnf1.put(file2, makeArray(file1));

		final FakeDependenceIndexer indexer = new FakeDependenceIndexer(
				rodinIndex, f2DepsOnf1, f1exportsElt1Elt4);
		IndexManager.getDefault().addIndexer(indexer, TEST_FILE_TYPE);
	}

	private static void forceIndexing(IRodinFile f) throws Exception {
		DebugHelpers.enableIndexing();
		f.getResource().touch(null);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IndexManager.getDefault().clear();
		init();
		forceIndexing(file1);
		forceIndexing(file2);
	}
	
	@Override
	protected void tearDown() throws Exception {
		rodinIndex.clear();
		deleteProject("P");
		DebugHelpers.disableIndexing();
		IndexManager.getDefault().clear();
		super.tearDown();
	}
	
	public void testGetDecl() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration actualDeclElt1 = query.getDeclaration(elt1);
		assertEquals("expected a declaration", declElt1, actualDeclElt1);
	}

	public void testGetDeclUnknown() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration actualDeclElt2 = query.getDeclaration(elt2);
		assertNull(actualDeclElt2);
	}
	
	public void testGetOccs() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration actualDeclElt1 = query.getDeclaration(elt1);

		final IOccurrence[] actualOccsElt1 = query.getOccurrences(actualDeclElt1);
		
		assertSameElements(makeArray(occElt1F1, occElt1K2F2), actualOccsElt1,
				OCCURRENCES);
	}
	
	public void testGetOccsUnknown() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IOccurrence[] actualOccsElt2 = query.getOccurrences(declElt2);
		
		assertIsEmpty(actualOccsElt2);
	}
	
	public void testGetDeclsPrjName() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration[] declsName1 = query.getDeclarations(project, name1);
		
		assertSameElements(makeArray(declElt1), declsName1, DECLARATIONS);
	}
	
	public void testGetDeclsPrjNameNoneExpected() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration[] declsName2 = query.getDeclarations(project, name2);

		assertIsEmpty(declsName2);
	}
	
}
