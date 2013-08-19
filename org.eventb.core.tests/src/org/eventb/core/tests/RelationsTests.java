/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests;

import static org.eventb.core.EventBAttributes.GENERATED_ATTRIBUTE;
import static org.junit.Assert.assertTrue;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.TypeTreeShape;
import org.rodinp.core.tests.TypeTreeShape.TypeTreeShapeComparisonResult;

/**
 * Unit tests of the database relations for Event-B. The purpose of these tests
 * is to show that all possible elements and attributes of user visible Event-B
 * files are registered with the Rodin database. We also check that the
 * <code>generated</code> attribute is present everywhere.
 * 
 * @author Thomas muller
 */
public class RelationsTests extends BuilderTest {

	/**
	 * Checks the loading of a machine which contains all possible children
	 * elements which also carry the attribute <code>generated</code>.
	 */
	@Test
	public void checkMachineHasAllPossibleCoreChildren() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<org.eventb.core.machineFile"
				+ "    org.eventb.core.generated=\"true\""
				+ "    org.eventb.core.comment=\"the comment of the machine\""
				+ "    org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "    version=\"5\">"
				+ "  <org.eventb.core.refinesMachine"
				+ "      name=\"refinesMachine\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.target=\"machine0\"/>"
				+ "  <org.eventb.core.seesContext"
				+ "      name=\"sees\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.target=\"ctx\"/>"
				+ "  <org.eventb.core.variable"
				+ "      name=\"a\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.identifier=\"a\"/>"
				+ "  <org.eventb.core.invariant"
				+ "      name=\"inv1\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.label=\"inv1\""
				+ "      org.eventb.core.predicate=\"a ∈ ℤ\"/>"
				+ "  <org.eventb.core.variant"
				+ "      name=\"variant\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.theorem=\"true\""
				+ "      org.eventb.core.expression=\"a+1\"/>"
				+ "  <org.eventb.core.event"
				+ "      name=\"evt1\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.convergence=\"1\""
				+ "      org.eventb.core.extended=\"false\""
				+ "      org.eventb.core.label=\"evt1\">"
				+ "    <org.eventb.core.refinesEvent"
				+ "        name=\"refines_evt1\""
				+ "        org.eventb.core.generated=\"true\""
				+ "        org.eventb.core.target=\"evt1\"/>"
				+ "    <org.eventb.core.parameter"
				+ "        name=\"prm1\""
				+ "        org.eventb.core.generated=\"true\""
				+ "        org.eventb.core.identifier=\"prm1\"/>"
				+ "    <org.eventb.core.guard"
				+ "        name=\"grd1\""
				+ "        org.eventb.core.generated=\"true\""
				+ "        org.eventb.core.theorem=\"true\""
				+ "        org.eventb.core.label=\"grd1\""
				+ "        org.eventb.core.predicate=\"prm1 ∈ ℤ\"/>"
				+ "    <org.eventb.core.witness"
				+ "        name=\"wit_a\""
				+ "        org.eventb.core.generated=\"true\""
				+ "        org.eventb.core.label=\"a\""
				+ "        org.eventb.core.predicate=\"b\"/>"
				+ "    <org.eventb.core.action"
				+ "        name=\"act1\""
				+ "        org.eventb.core.generated=\"true\""
				+ "        org.eventb.core.assignment=\"b ≔ b + 1\""
				+ "        org.eventb.core.label=\"act1\"/>"
				+ "  </org.eventb.core.event>"
				+ "</org.eventb.core.machineFile>";
		final TypeTreeShape expectedShape = s(
				"machineFile", //
				s("refinesMachine"),
				s("seesContext"),
				s("variable"),
				s("invariant"),
				s("variant"),
				s("event", //
						s("refinesEvent"), s("parameter"), s("guard"),
						s("witness"), s("action")));
		final IMachineRoot root = ResourceUtils.createMachine(rodinProject,
				"mch", contents);
		assertSameShape(expectedShape, root);
	}

	/**
	 * Checks the loading of a machine which contains all possible children
	 * elements which also carry the attribute <code>generated</code>.
	 */
	@Test
	public void checkContextHasAllPossibleCoreChildren() throws Exception {
		final String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<org.eventb.core.contextFile"
				+ "    org.eventb.core.generated=\"true\""
				+ "    org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "    version=\"3\">"
				+ "  <org.eventb.core.extendsContext"
				+ "      name=\"extendsCtx\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.target=\"ctx0\"/>"
				+ "  <org.eventb.core.carrierSet"
				+ "      name=\"set\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.identifier=\"S\"/>"
				+ "  <org.eventb.core.constant"
				+ "      name=\"cst1\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.identifier=\"a\"/>"
				+ "  <org.eventb.core.axiom"
				+ "      name=\"axm1\""
				+ "      org.eventb.core.generated=\"true\""
				+ "      org.eventb.core.label=\"axm1\""
				+ "      org.eventb.core.predicate=\"a = 0\""
				+ "      org.eventb.core.theorem=\"true\"/>"
				+ "</org.eventb.core.contextFile>";
		final TypeTreeShape expectedShape = s("contextFile",
				s("extendsContext"), s("carrierSet"), s("constant"), s("axiom"));
		final IContextRoot root = ResourceUtils.createContext(rodinProject,
				"ctx", contents);
		assertSameShape(expectedShape, root);
	}

	private void assertSameShape(final TypeTreeShape expectedShape,
			final IEventBRoot root) throws RodinDBException {
		final TypeTreeShapeComparisonResult result = expectedShape
				.assertSameTreeWithMessage(root);
		assertTrue(result.getMessage(), result.same());
	}

	public static TypeTreeShape s(String id, TypeTreeShape... children) {
		final IAttributeType[] attrs = { GENERATED_ATTRIBUTE, };
		return TypeTreeShape.s(EventBPlugin.PLUGIN_ID, id, attrs, children);
	}

}
