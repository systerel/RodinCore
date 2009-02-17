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
package org.rodinp.core.tests.indexer;

import static org.rodinp.core.tests.util.IndexTestsUtil.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.indexer.Declaration;
import org.rodinp.internal.core.indexer.IIndexingResult;
import org.rodinp.internal.core.indexer.IndexingBridge;
import org.rodinp.internal.core.indexer.Occurrence;

public class IndexingBridgeTests extends IndexTests {

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

	private static void assertIndexingSuccess(final IIndexingResult result) {
		assertTrue("indexing failed", result.isSuccess());
	}

	public IndexingBridgeTests(String name) {
		super(name);
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
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		final IDeclaration declElt1 = bridge.declare(elt1, name1);
		final IInternalLocation loc =
			RodinCore.getInternalLocation(file1.getRoot());
		bridge.addOccurrence(declElt1, TEST_KIND, loc);

		final IDeclaration expected = new Declaration(elt1, name1);

		bridge.complete();
		final IIndexingResult result = bridge.getResult();

		assertIndexingSuccess(result);

		final Collection<IDeclaration> declarations = result.getDeclarations();
		assertTrue("expected a declaration for: " + elt1, declarations
				.contains(expected));
	}

	public void testDeclareNoOccurrence() {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		bridge.declare(elt1, name1);

		bridge.complete();
		final IIndexingResult result = bridge.getResult();

		assertIndexingSuccess(result);

		final IDeclaration unexpected = new Declaration(elt1, name1);
		final Collection<IDeclaration> declarations = result.getDeclarations();
		assertFalse("expected declaration removed for: " + elt1, declarations
				.contains(unexpected));
	}

	public void testDeclareNullElem() throws Exception {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports,
						new NullProgressMonitor());

		try {
			bridge.declare(null, name1);
			fail("NullPointerException expected");
		} catch (NullPointerException e) {
			// OK
		}
	}

	public void testDeclareNullName() throws Exception {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		try {
			bridge.declare(elt1, null);
			fail("NullPointerException expected");
		} catch (NullPointerException e) {
			// OK
		}
	}

	public void testDeclareNotLocal() throws Exception {
		IndexingBridge bridge =
				new IndexingBridge(file1, f1ImportsElt2, null);

		try {
			bridge.declare(elt2, name2);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testDeclareTwice() throws Exception {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		bridge.declare(elt1, name1);

		try {
			bridge.declare(elt1, name1);
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testAddLocalOccurrence() {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		final IDeclaration declElt1 = bridge.declare(elt1, name1);
		final IInternalLocation loc =
				RodinCore.getInternalLocation(file1.getRoot());
		bridge.addOccurrence(declElt1, TEST_KIND, loc);

		final IOccurrence expOcc = new Occurrence(TEST_KIND, loc, declElt1);
		final Set<IOccurrence> expected = new HashSet<IOccurrence>();
		expected.add(expOcc);

		bridge.complete();
		final IIndexingResult result = bridge.getResult();

		assertIndexingSuccess(result);

		final Map<IInternalElement, Set<IOccurrence>> occurrences =
				result.getOccurrences();
		final Set<IOccurrence> actual = occurrences.get(elt1);
		assertNotNull("expected an occurrence for: " + elt1, actual);
		assertEquals("Bad occurrences", expected, actual);
	}

	public void testAddImportOccurrence() {
		IndexingBridge bridge =
				new IndexingBridge(file1, f1ImportsElt2, null);

		final IInternalLocation loc =
				RodinCore.getInternalLocation(file1.getRoot());
		bridge.addOccurrence(declElt2, TEST_KIND, loc);

		final IOccurrence expOcc = new Occurrence(TEST_KIND, loc, declElt2);
		final Set<IOccurrence> expected = new HashSet<IOccurrence>();
		expected.add(expOcc);

		bridge.complete();
		final IIndexingResult result = bridge.getResult();

		assertIndexingSuccess(result);

		final Map<IInternalElement, Set<IOccurrence>> occurrences =
				result.getOccurrences();
		final Set<IOccurrence> actual = occurrences.get(elt2);
		assertNotNull("expected an occurrence for: " + elt2, actual);
		assertEquals("Bad occurrences", expected, actual);
	}

	public void testAddOccurrenceLocationInOtherFile() throws Exception {
		IndexingBridge bridge =
			new IndexingBridge(file2, emptyImports, null);

		final IInternalLocation loc =
			RodinCore.getInternalLocation(file1.getRoot());
		
		try {
			bridge.addOccurrence(declElt2, TEST_KIND, loc);
			fail("Expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}
	
	public void testAddOccurrenceNotLocalNotImported() throws Exception {
		IndexingBridge bridge =
			new IndexingBridge(file1, emptyImports, null);

		final IInternalLocation loc =
			RodinCore.getInternalLocation(file1.getRoot());
		
		try {
			bridge.addOccurrence(declElt2, TEST_KIND, loc);
			fail("Expected IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}
	
	public void testLocalExport() {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		final IDeclaration declElt1 = bridge.declare(elt1, name1);
		final IInternalLocation loc =
			RodinCore.getInternalLocation(file1.getRoot());
		bridge.addOccurrence(declElt1, TEST_KIND, loc);
		bridge.export(declElt1);

		bridge.complete();
		final IIndexingResult result = bridge.getResult();

		assertIndexingSuccess(result);

		final Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(declElt1);
		final Set<IDeclaration> actual = result.getExports();

		assertNotNull("expected an export for: " + elt1, actual);
		assertEquals("Bad exports", expected, actual);
	}

	public void testImportExport() throws Exception {
		IndexingBridge bridge =
				new IndexingBridge(file1, f1ImportsElt2, null);

		bridge.export(declElt2);

		final Set<IDeclaration> expected = new HashSet<IDeclaration>();
		expected.add(declElt2);

		bridge.complete();
		final IIndexingResult result = bridge.getResult();

		assertIndexingSuccess(result);

		final Set<IDeclaration> actual = result.getExports();
		assertNotNull("expected an occurrence for: " + elt2, actual);
		assertEquals("Bad occurrences", expected, actual);

	}

	public void testIllegalExport() throws Exception {
		IndexingBridge bridge =
				new IndexingBridge(file1, emptyImports, null);

		try {
			bridge.export(declElt2);
			fail("expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testGetImports() {
		IndexingBridge bridge =
				new IndexingBridge(file1, f1ImportsElt2, null);

		final IDeclaration[] expected = new IDeclaration[] { declElt2 };

		final IDeclaration[] actual = bridge.getImports();
		assertSameElements(expected, actual, "imports");
	}

	public void testGetDeclarationsEmpty() throws Exception {
		IndexingBridge bridge =
			new IndexingBridge(file1, emptyImports, null);

		final IDeclaration[] declarations = bridge.getDeclarations();
		
		assertEquals("expected empty declarations", 0, declarations.length);
	}

	public void testGetDeclarationsNotEmpty() throws Exception {
		IndexingBridge bridge =
			new IndexingBridge(file1, emptyImports, null);

		final IDeclaration declElt1 = bridge.declare(elt1, name1);
		
		
		final IDeclaration[] declarations = bridge.getDeclarations();
		
		assertEquals("expected non empty declarations", 1, declarations.length);
		assertEquals("unexpected declaration", declElt1, declarations[0]);
	}


}
