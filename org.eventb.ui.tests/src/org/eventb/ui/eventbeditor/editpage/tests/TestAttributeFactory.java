/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - add test for IAttributeFactory
 *******************************************************************************/
package org.eventb.ui.eventbeditor.editpage.tests;

import java.util.Arrays;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendsContextAbstractContextNameAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesEventAbstractEventLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesMachineAbstractMachineNameAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.SeesContextNameAttributeFactory;
import org.eventb.ui.tests.utils.EventBUITest;
import org.rodinp.core.RodinDBException;

public class TestAttributeFactory extends EventBUITest {

	public void testExtendsContextGetPossibleValueWithoutExtendsClause()
			throws RodinDBException {
		final IAttributeFactory<IExtendsContext> factory = new ExtendsContextAbstractContextNameAttributeFactory();
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		IContextRoot ctx3 = createContext("ctx3");

		final IExtendsContext seeCtx = ctx3.getInternalElement(
				IExtendsContext.ELEMENT_TYPE, "extends_ctx0");

		String[] possibleValues = factory.getPossibleValues(seeCtx, null);
		String[] expected = new String[] { "ctx0", "ctx1", "ctx2" };

		// context doesn't have extends context clause so expected all context
		assertPossibleValues("Error in getPossibleValue for Extends Context",
				expected, possibleValues);
	}

	public void testExtendsContextGetPossibleValueWithExtendsClause()
			throws RodinDBException {
		final IAttributeFactory<IExtendsContext> factory = new ExtendsContextAbstractContextNameAttributeFactory();
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		IContextRoot ctx3 = createContext("ctx3");

		createExtendsContextClause(ctx3, "ctx1");
		createExtendsContextClause(ctx3, "ctx2");

		IExtendsContext seeCtx = ctx3.getInternalElement(
				IExtendsContext.ELEMENT_TYPE, "extends_ctx0");

		// ctx3 extends ctx1 and ctx2 context clause so expected ctx0
		String[] possibleValues = factory.getPossibleValues(seeCtx, null);
		String[] expected = new String[] { "ctx0" };
		assertPossibleValues("Error in getPossibleValue for Extends Context",
				expected, possibleValues);
	}

	public void testExtendsContextGetPossibleValueWithExtendsClauseCalledWithExistingClause()
			throws RodinDBException {
		final IAttributeFactory<IExtendsContext> factory = new ExtendsContextAbstractContextNameAttributeFactory();
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		IContextRoot ctx3 = createContext("ctx3");

		IExtendsContext seeCtx = createExtendsContextClause(ctx3, "ctx2");
		createExtendsContextClause(ctx3, "ctx1");

		// ctx3 extends ctx1 and ctx2 context clause and call with existing
		// clause so expected ctx0, ctx2
		String[] possibleValues = factory.getPossibleValues(seeCtx, null);
		String[] expected = new String[] { "ctx0", "ctx2" };
		assertPossibleValues("Error in getPossibleValue for Extends Context",
				expected, possibleValues);

	}

	public void testSeeContextGetPossibleValueWithoutSeesClause()
			throws RodinDBException {
		final IAttributeFactory<ISeesContext> factory = new SeesContextNameAttributeFactory();
		final IMachineRoot mch = createMachine("mch");
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		createContext("ctx3");

		final ISeesContext seeCtx = mch.getInternalElement(
				ISeesContext.ELEMENT_TYPE, "see_ctx0");

		final String[] possibleValues = factory.getPossibleValues(seeCtx, null);
		final String[] expected = new String[] { "ctx0", "ctx1", "ctx2", "ctx3" };

		// machine doesn't have see context clause so expected all context
		assertPossibleValues("Error in getPossibleValue for SeesContext",
				expected, possibleValues);
	}

	public void testSeeContextGetPossibleValueWithSeesClause()
			throws RodinDBException {
		final IAttributeFactory<ISeesContext> factory = new SeesContextNameAttributeFactory();
		final IMachineRoot mch = createMachine("mch");
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		createContext("ctx3");

		final ISeesContext seeCtx = mch.getInternalElement(
				ISeesContext.ELEMENT_TYPE, "extends_ctx0");
		createSeesContextClause(mch, "ctx1");

		final String[] possibleValues = factory.getPossibleValues(seeCtx, null);
		final String[] expected = new String[] { "ctx0", "ctx2", "ctx3" };
		// machine have 2 sees context clause so expected 2 context
		assertPossibleValues("Error in getPossibleValue for SeesContext",
				expected, possibleValues);
	}

	public void testSeeContextGetPossibleValueWithSeesClauseCalledWithExistingClause()
			throws RodinDBException {
		final IAttributeFactory<ISeesContext> factory = new SeesContextNameAttributeFactory();
		final IMachineRoot mch = createMachine("mch");
		createContext("ctx0");
		createContext("ctx1");
		createContext("ctx2");
		createContext("ctx3");

		final ISeesContext seeCtx = createSeesContextClause(mch, "ctx2");
		createSeesContextClause(mch, "ctx1");

		final String[] possibleValues = factory.getPossibleValues(seeCtx, null);
		final String[] expected = new String[] { "ctx0", "ctx2", "ctx3" };
		// machine have 2 sees context clause so expected 2 context
		assertPossibleValues("Error in getPossibleValue for SeesContext",
				expected, possibleValues);
	}

	public void testRefineMachineGetPossibleValue() throws RodinDBException {
		final IAttributeFactory<IRefinesMachine> factory = new RefinesMachineAbstractMachineNameAttributeFactory();
		createMachine("mch0");
		createMachine("mch1");
		final IMachineRoot mch2 = createMachine("mch2");

		final IRefinesMachine refined = createRefinesMachineClause(mch2, "mch0");

		String[] possibleValues = factory.getPossibleValues(refined, null);
		String[] expected = new String[] { "mch0", "mch1" };

		assertPossibleValues("Error in getPossibleValue for RefineMachine",
				expected, possibleValues);
	}

	public void testRefineEventGetPossibleValue() throws RodinDBException {
		final IAttributeFactory<IRefinesEvent> factory = new RefinesEventAbstractEventLabelAttributeFactory();
		final IMachineRoot mch0 = createMachine("mch0");
		final IMachineRoot mch1 = createMachine("mch1");
		final IMachineRoot mch2 = createMachine("mch2");

		createEvent(mch0, "event01");
		createEvent(mch0, "event02");
		createEvent(mch1, "event11");
		createEvent(mch1, "event12");
		IEvent event21 = createEvent(mch2, "event21");

		createRefinesMachineClause(mch2, "mch0");
		IRefinesEvent refined = createRefinesEventClause(event21, "event01");

		String[] possibleValues = factory.getPossibleValues(refined, null);
		String[] expected = new String[] { "event01", "event02" };

		assertPossibleValues("Error in getPossibleValue for RefineEvent",
				expected, possibleValues);

	}

	private String getString(String[] tab) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('{');
		boolean first = true;
		for (String string : tab) {
			buffer.append((first) ? string : "," + string);
			first = false;
		}
		buffer.append('}');
		return buffer.toString();
	}

	private void assertPossibleValues(String msg, String[] expected,
			String[] actual) {
		final String messageFail = msg + ": expected: " + getString(expected)
				+ ", but was: " + getString(actual);
		Arrays.sort(expected);
		Arrays.sort(actual);
		if (expected.length != actual.length) {
			fail(messageFail);
		}
		for (int i = 0; i < expected.length; i++) {
			if (!expected[i].equals(actual[i])) {
				fail(messageFail);
			}
		}
	}

}
