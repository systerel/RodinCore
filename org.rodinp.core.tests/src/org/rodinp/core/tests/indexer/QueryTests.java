/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.tests.indexer;

import static java.util.Arrays.asList;
import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import java.util.List;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.indexer.IPropagator;
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
	private static IOccurrence occElt4InF1Root;
	private static IOccurrence occElt4InElt1;

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
		occElt1K2F2 = addInternalOccurrence(rodinIndex, declElt1, file2
				.getRoot(), TEST_KIND_2);
		makeDescAndDefaultOcc(rodinIndex, declElt3, file2.getRoot());
		occElt4InF1Root = makeDescAndDefaultOcc(rodinIndex, declElt4, file1
				.getRoot());
		occElt4InElt1 = addInternalOccurrence(rodinIndex, declElt4, elt1,
				TEST_KIND);
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

		final Set<IOccurrence> actualOccsElt1 = query
				.getOccurrences(actualDeclElt1);

		assertSameElements(asList(occElt1F1, occElt1K2F2), actualOccsElt1,
				OCCURRENCES);
	}

	public void testGetOccsUnknown() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IOccurrence> actualOccsElt2 = query
				.getOccurrences(declElt2);

		assertIsEmpty(actualOccsElt2);
	}

	public void testGetDeclsPrjName() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> declsName1 = query.getDeclarations(project,
				name1);

		assertSameElements(asList(declElt1), declsName1, DECLARATIONS);
	}

	public void testGetDeclsPrjNameNoneExpected() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> declsName2 = query.getDeclarations(project,
				name2);

		assertIsEmpty(declsName2);
	}

	public void testGetDeclsFile() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> declsFile2 = query.getDeclarations(file2);

		assertSameElements(asList(declElt1, declElt3), declsFile2, DECLARATIONS);
	}

	public void testGetVisibleDecls() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> visibleDeclsFile2 = query
				.getVisibleDeclarations(file2);

		assertSameElements(asList(declElt1, declElt3, declElt4),
				visibleDeclsFile2, DECLARATIONS);
	}

	public void testGetDeclsFileName() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> declsName1F1 = query.getDeclarations(file1);
		query.filterName(declsName1F1, name1);

		assertSameElements(asList(declElt1), declsName1F1, DECLARATIONS);
	}

	public void testGetDeclsFileNameOtherThanDecl() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> declsName1F2 = query.getDeclarations(file2);
		query.filterName(declsName1F2, name1);

		assertSameElements(asList(declElt1), declsName1F2, DECLARATIONS);
	}

	public void testGetDeclsFileType() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IDeclaration> declsF2NE = query.getDeclarations(file2);
		query.filterType(declsF2NE, NamedElement.ELEMENT_TYPE);
		final Set<IDeclaration> declsF2NE2 = query.getDeclarations(file2);
		query.filterType(declsF2NE2, NamedElement2.ELEMENT_TYPE);

		assertSameElements(asList(declElt1), declsF2NE, DECLARATIONS);
		assertSameElements(asList(declElt3), declsF2NE2, DECLARATIONS);
	}

	public void testGetOccsKind() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IOccurrence> occsElt1K2 = query.getOccurrences(declElt1);
		query.filterKind(occsElt1K2, TEST_KIND_2);

		assertSameElements(asList(occElt1K2F2), occsElt1K2, OCCURRENCES);
	}

	public void testGetOccsFile() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IOccurrence> occsElt1F2 = query.getOccurrences(declElt1);
		query.filterFile(occsElt1F2, file2);

		assertSameElements(asList(occElt1K2F2), occsElt1F2, OCCURRENCES);
	}

	public void testGetOccsLocation() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IOccurrence> occsElt4 = query.getOccurrences(declElt4);
		assertSameElements(asList(occElt4InF1Root, occElt4InElt1), occsElt4,
				OCCURRENCES);
		query.filterLocation(occsElt4, RodinCore.getInternalLocation(elt1));

		assertSameElements(asList(occElt4InElt1), occsElt4, OCCURRENCES);
	}

	public void testGetOccsKindFile() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final Set<IOccurrence> occsKF2 = query.getOccurrences(declElt1);
		query.filterKind(occsKF2, TEST_KIND);
		query.filterFile(occsKF2, file2);

		assertIsEmpty(occsKF2);
	}

	private static class OccElemPropagator implements IPropagator {

		public IDeclaration getRelativeDeclaration(IOccurrence occurrence,
				IIndexQuery query) {
			final IInternalElement locElem = occurrence.getLocation()
					.getElement();
			return query.getDeclaration(locElem);
		}

	}

	public void testPropagator() throws Exception {
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final OccElemPropagator propagator = new OccElemPropagator();
		final Set<IOccurrence> occsPropElt4 = query.getOccurrences(declElt4,
				propagator);

		
		assertSameElements(asList(occElt4InF1Root, occElt4InElt1, occElt1F1,
				occElt1K2F2), occsPropElt4, OCCURRENCES);

	}

	public void testGetOccsSeveralDecls() throws Exception {
		final List<IDeclaration> declarations = asList(declElt1, declElt4);
		final List<IOccurrence> expected = asList(occElt1F1, occElt1K2F2, occElt4InElt1, occElt4InF1Root);
		
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();

		final Set<IOccurrence> actual = query.getOccurrences(declarations);
		
		assertSameElements(expected, actual, "occurrences");
	}
	
	public void testGetDeclsSeveralOccs() throws Exception {
		final List<IOccurrence> occurrences = asList(occElt1F1, occElt1K2F2, occElt4InElt1, occElt4InF1Root);
		final List<IDeclaration> expected = asList(declElt1, declElt4);
		
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();

		final Set<IDeclaration> actual = query.getDeclarations(occurrences);
		
		assertSameElements(expected, actual, "declarations");
	
	}
	
	public void testGetVisibleDeclsFileUnknown() throws Exception {
		// no indexer is registered for this file type
		final IRodinFile unknown = createRodinFile(project, "unknown.test2");
		
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();

		query.getVisibleDeclarations(unknown);
	}
}
