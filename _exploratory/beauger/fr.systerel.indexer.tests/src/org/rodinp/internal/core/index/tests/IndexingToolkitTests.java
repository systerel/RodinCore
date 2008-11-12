/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.index.tests;

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IInternalLocation;
import org.rodinp.core.index.IOccurrence;
import org.rodinp.core.index.RodinIndexer;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.IIndexingResult;
import org.rodinp.internal.core.index.IndexingToolkit;
import org.rodinp.internal.core.index.Occurrence;

public class IndexingToolkitTests extends IndexTests {

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static IDeclaration declElt2;
	private static final String name1 = "name1";
	private static final String name2 = "name2";
	private static final Map<IInternalElement, IDeclaration> f1ImportsElt2 =
			new HashMap<IInternalElement, IDeclaration>();
	private static final Map<IInternalElement, IDeclaration> emptyImports =
			Collections.emptyMap();

	public IndexingToolkitTests(String name) {
		super(name, true);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
		file1 = createRodinFile(project, "indFac1.test");
		file2 = createRodinFile(project, "indFac2.test");
		elt1 = createNamedElement(file1, "elt1");
		elt2 = createNamedElement(file2, "elt2");
		declElt2 = new Declaration(elt2, name2);
		f1ImportsElt2.put(elt2, declElt2);
	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testDeclare() {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports, null);

		indexingToolkit.declare(elt1, name1);

		final IDeclaration expected = new Declaration(elt1, name1);

		indexingToolkit.complete();
		final IIndexingResult result = indexingToolkit.getResult();

		assertTrue("indexing failed", result.isSuccess());

		final Map<IInternalElement, IDeclaration> declarations =
				result.getDeclarations();
		final IDeclaration actual = declarations.get(elt1);
		assertNotNull("expected a declaration for: " + elt1, actual);
		assertEquals("Bad declaration", expected, actual);
	}

	public void testDeclareNullElem() throws Exception {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports,
						new NullProgressMonitor());

		try {
			indexingToolkit.declare(null, name1);
			fail("NullPointerException expected");
		} catch (NullPointerException e) {
			// OK
		}
	}

	public void testDeclareNullName() throws Exception {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports, null);

		try {
			indexingToolkit.declare(elt1, null);
			fail("NullPointerException expected");
		} catch (NullPointerException e) {
			// OK
		}
	}

	public void testDeclareNotLocal() throws Exception {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, f1ImportsElt2, null);

		try {
			indexingToolkit.declare(elt2, name2);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testDeclareTwice() throws Exception {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports, null);

		indexingToolkit.declare(elt1, name1);

		try {
			indexingToolkit.declare(elt1, name1);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddLocalOccurrence() {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports, null);

		final IDeclaration declElt1 = indexingToolkit.declare(elt1, name1);
		final IInternalLocation loc =
				RodinIndexer.getInternalLocation(file1.getRoot());
		indexingToolkit.addOccurrence(declElt1, TEST_KIND, loc);

		final IOccurrence expOcc = new Occurrence(TEST_KIND, loc, declElt1);
		final Set<IOccurrence> expected = new HashSet<IOccurrence>();
		expected.add(expOcc);

		indexingToolkit.complete();
		final IIndexingResult result = indexingToolkit.getResult();

		assertTrue("indexing failed", result.isSuccess());

		final Map<IInternalElement, Set<IOccurrence>> occurrences =
				result.getOccurrences();
		final Set<IOccurrence> actual = occurrences.get(elt1);
		assertNotNull("expected an occurrence for: " + elt1, actual);
		assertEquals("Bad occurrences", expected, actual);
	}

	public void testAddImportOccurrence() {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, f1ImportsElt2, null);

		final IInternalLocation loc =
				RodinIndexer.getInternalLocation(file1.getRoot());
		indexingToolkit.addOccurrence(declElt2, TEST_KIND, loc);

		final IOccurrence expOcc = new Occurrence(TEST_KIND, loc, declElt2);
		final Set<IOccurrence> expected = new HashSet<IOccurrence>();
		expected.add(expOcc);

		indexingToolkit.complete();
		final IIndexingResult result = indexingToolkit.getResult();

		assertTrue("indexing failed", result.isSuccess());

		final Map<IInternalElement, Set<IOccurrence>> occurrences =
				result.getOccurrences();
		final Set<IOccurrence> actual = occurrences.get(elt2);
		assertNotNull("expected an occurrence for: " + elt2, actual);
		assertEquals("Bad occurrences", expected, actual);
	}

	public void testLocalExport() {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports, null);

		final IDeclaration declElt1 = indexingToolkit.declare(elt1, name1);
		indexingToolkit.export(declElt1);

		indexingToolkit.complete();
		final IIndexingResult result = indexingToolkit.getResult();

		assertTrue("indexing failed", result.isSuccess());

		final Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(declElt1);
		final Set<IDeclaration> actual = result.getExports();

		assertNotNull("expected an export for: " + elt1, actual);
		assertEquals("Bad exports", expected, actual);
	}

	public void testImportExport() throws Exception {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, f1ImportsElt2, null);

		indexingToolkit.export(declElt2);

		final Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(declElt2);

		indexingToolkit.complete();
		final IIndexingResult result = indexingToolkit.getResult();

		assertTrue("indexing failed", result.isSuccess());

		final Set<IDeclaration> actual = result.getExports();
		assertNotNull("expected an occurrence for: " + elt2, actual);
		assertEquals("Bad occurrences", expected, actual);

	}

	public void testIllegalExport() throws Exception {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, emptyImports, null);

		try {
			indexingToolkit.export(declElt2);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testGetImports() {
		IndexingToolkit indexingToolkit =
				new IndexingToolkit(file1, f1ImportsElt2, null);

		final IDeclaration[] expected = new IDeclaration[] { declElt2 };

		final IDeclaration[] actual = indexingToolkit.getImports();
		assertSameElements(expected, actual, "imports");
	}

}
