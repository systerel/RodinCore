/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import static org.eventb.core.tests.ResourceUtils.CTX_BARE_NAME;
import static org.eventb.core.tests.ResourceUtils.INTERNAL_ELEMENT1;
import static org.eventb.core.tests.indexers.CancelBridgeStub.NO_LIMIT;
import static org.eventb.core.tests.indexers.OccUtils.newDecl;

import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.indexers.ContextIndexer;
import org.junit.Test;
import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 * 
 */
public class ContextCancelTests extends EventBIndexerTests {

	@Test
	public void testCancelImmediately() throws Exception {
		final IContextRoot context =
			ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL);

		final CancelBridgeStub tk =
				new CancelBridgeStub(NO_LIMIT, NO_LIMIT, NO_LIMIT, true,
						context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertNumDecl(0);
		tk.assertNumExp(0);
	}

	@Test
	public void testCancelAfterImports() throws Exception {
		final IContextRoot exporter =
				ResourceUtils.createContext(rodinProject, EXPORTER, CST_1DECL);
		final IConstant cst = exporter.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCstExp = newDecl(cst, CST1);

		final IContextRoot importer =
				ResourceUtils.createContext(rodinProject, IMPORTER, CST_1DECL_1REF_AXM);

		final CancelBridgeStub tk =
				new CancelBridgeStub(NO_LIMIT, NO_LIMIT, 1, false, importer,
						declCstExp);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		tk.assertNumDecl(1); // root declaration
		tk.assertNumOcc(1); // root occurrence
	}

	@Test
	public void testCancelAfterDecl() throws Exception {
		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_1REF_AXM);

		final CancelBridgeStub tk =
				new CancelBridgeStub(1, NO_LIMIT, NO_LIMIT, false, context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		// only the DECLARATION occurrence
		tk.assertNumOcc(1);
	}

	@Test
	public void testCancelInPredicates() throws Exception {
		final String CST_1DECL_2REF_2AXM =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.constant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"cst1\"/>"
						+ "<org.eventb.core.axiom"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"axm1\""
						+ "		org.eventb.core.theorem=\"false\""
						+ "		org.eventb.core.predicate=\"cst1 = 1\"/>"
						+ "<org.eventb.core.axiom"
						+ "		name=\"internal_element2\""
						+ "		org.eventb.core.label=\"axm2\""
						+ "		org.eventb.core.theorem=\"false\""
						+ "		org.eventb.core.predicate=\"cst1 = 2\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_2REF_2AXM);

		final CancelBridgeStub tk =
				new CancelBridgeStub(NO_LIMIT, 2, NO_LIMIT, false, context);

		final ContextIndexer indexer = new ContextIndexer();

		indexer.index(tk);

		// only 1 DECLARATION and 1 REFERENCE occurrences
		tk.assertNumOcc(2);
	}

}
