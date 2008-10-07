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

import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createNamedElement;
import static org.rodinp.internal.core.index.tests.IndexTestsUtil.createRodinFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.tests.basis.NamedElement;
import org.rodinp.internal.core.index.Declaration;
import org.rodinp.internal.core.index.IIndexingResult;
import org.rodinp.internal.core.index.IndexingToolkit;

public class IndexingToolkitTests extends IndexTests {

	private static IRodinProject project;
	private static IRodinFile file1;
	private static IRodinFile file2;
	private static NamedElement elt1;
	private static NamedElement elt2;
	private static final String name1 = "name1";
	private static final String name2 = "name2";
	private static final Map<IInternalElement, IDeclaration> f1ImportsElt2 = new HashMap<IInternalElement, IDeclaration>();
	private static final Map<IInternalElement, IDeclaration> emptyImports = Collections
			.emptyMap();

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

	}

	@Override
	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	public void testDeclare() {
		IndexingToolkit indexingToolkit = new IndexingToolkit(file1,
				emptyImports, new NullProgressMonitor());
		
		indexingToolkit.declare(elt1, name1);

		final IDeclaration expected = new Declaration(elt1, name1);
		
		final IIndexingResult result = indexingToolkit.getResult();

		final Map<IInternalElement, IDeclaration> declarations = result
				.getDeclarations();
		final IDeclaration actual = declarations.get(elt1);
		assertNotNull("expected a declaration for: " + elt1, actual);
		assertEquals("Bad declaration", expected, actual);
	}

	public void testAddOccurrence() {
		fail("Not yet implemented");
	}

	public void testExport() {
		fail("Not yet implemented");
	}

	public void testMustReindexDependents() {
		fail("Not yet implemented");
	}

	public void testGetResult() {
		fail("Not yet implemented");
	}

	public void testGetImports() {
		fail("Not yet implemented");
	}

}
