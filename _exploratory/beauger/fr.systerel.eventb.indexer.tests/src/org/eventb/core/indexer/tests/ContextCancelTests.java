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
package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.tests.OccUtils.newDecl;
import static org.eventb.core.indexer.tests.ResourceUtils.CTX_BARE_NAME;
import static org.eventb.core.indexer.tests.ResourceUtils.INTERNAL_ELEMENT1;
import static org.eventb.core.indexer.tests.ResourceUtils.createContext;

import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.indexer.ContextIndexer;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.index.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextCancelTests extends EventBIndexerTests {

	private static IRodinProject project;

	private static final int NO_LIMIT = Integer.MAX_VALUE;

	/**
	 * @param name
	 */
	public ContextCancelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
	}

	// TODO factorize test Strings with other Context tests

	private static final String CST_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
			+ "</org.eventb.core.contextFile>";

	private static final String CST_1DECL_1REF_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.constant"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.identifier=\"cst2\"/>"
			+ "<org.eventb.core.axiom"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.label=\"axm1\""
			+ "		org.eventb.core.predicate=\"cst2 = 2\"/>"
			+ "</org.eventb.core.contextFile>";

	private static final String CST_1REF_AXM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "<org.eventb.core.axiom"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.comment=\"\""
			+ "		org.eventb.core.label=\"axm1\""
			+ "		org.eventb.core.predicate=\"1 = cst1\"/>"
			+ "</org.eventb.core.contextFile>";

	public void testCancelImmediately() throws Exception {
		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL);

		final CancelToolkitStub tk = new CancelToolkitStub(0, 0, 0, true,
				context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumExp(0);
	}

	public void testCancelAfterImports() throws Exception {
		final IContextFile exporter = createContext(project, EXPORTER,
				CST_1DECL);
		final IConstant cst = exporter.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCstExp = newDecl(cst, CST1);

		final IContextFile importer = createContext(project, IMPORTER,
				CST_1REF_AXM);

		final CancelToolkitStub tk = new CancelToolkitStub(NO_LIMIT, NO_LIMIT,
				1, false, importer, declCstExp);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumOcc(0);
	}

	public void testCancelAfterDecl() throws Exception {
		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1REF_AXM);

		final CancelToolkitStub tk = new CancelToolkitStub(1, NO_LIMIT, 1,
				false, context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		// only the DECLARATION occurrence
		tk.assertNumOcc(1);
	}

	public void testCancelInPredicates() throws Exception {
		final String CST_1DECL_3REF_2AXM_1THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.axiom"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"axm1\""
				+ "		org.eventb.core.predicate=\"cst1 = 1\"/>"
				+ "<org.eventb.core.axiom"
				+ "		name=\"internal_element2\""
				+ "		org.eventb.core.label=\"axm2\""
				+ "		org.eventb.core.predicate=\"cst1 = 2\"/>"
				+ "<org.eventb.core.theorem"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"thm1\""
				+ "		org.eventb.core.predicate=\"cst1 = 3\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_3REF_2AXM_1THM);

		final CancelToolkitStub tk = new CancelToolkitStub(NO_LIMIT, 2,
				NO_LIMIT, false, context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 REFERENCE occurrences
		tk.assertNumOcc(2);
	}

}
