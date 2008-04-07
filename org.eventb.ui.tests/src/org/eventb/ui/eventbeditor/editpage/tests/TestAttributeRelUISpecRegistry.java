/*******************************************************************************
 * Copyright (c) 2007-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.eventbeditor.editpage.tests;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.CComboEditComposite;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.IEditComposite;
import org.eventb.internal.ui.eventbeditor.editpage.TextEditComposite;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

/**
 * @author htson
 *         <p>
 *         jUnit tests for {@link AttributeRelUISpecRegistry}.
 */
public class TestAttributeRelUISpecRegistry extends EventBUITest {

	/**
	 * The registry for testing. Using an extension of
	 * {@link AttributeRelUISpecRegistry} for testing.
	 */
	private static final IAttributeRelUISpecRegistry registry = AttributeRelUISpecTestRegistry
			.getDefault();

	private IMachineFile m0;

	private IEventBEditor<?> editor;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		m0 = createMachine("m0");
		m0.save(null, true);
		editor = openEditor(m0);
	}

	public <T extends IInternalElement> T createElement(IInternalParent parent,
			IInternalElementType<T> type) throws Exception {
		final T e = registry.createElement(editor, parent, type, null);
		assertNotNull("Couldn't create an element of type " + type, e);
		return e;
	}

	/**
	 * Ensures that a machine variable can be created through the editor
	 * registry.
	 */
	@Test
	public void testCreateVariables() throws Exception {
		final IVariable var = createElement(m0, IVariable.ELEMENT_TYPE);
		assertTrue("New variable should has identifier string", var
				.hasIdentifierString());
		assertEquals("Incorrect identifier string for new variable", "var1",
				var.getIdentifierString());
		assertFalse("New variable should has no comment", var.hasComment());
	}

	/**
	 * Ensures that an invariant can be created through the editor registry.
	 */
	@Test
	public void testCreateInvariants() throws Exception {
		final IInvariant inv = createElement(m0, IInvariant.ELEMENT_TYPE);
		assertTrue("New invariant should has label", inv.hasLabel());
		assertEquals("Incorrect label for new invariant", "inv1", inv
				.getLabel());
		assertFalse("New invariant should has no predicate", inv
				.hasPredicateString());
		assertFalse("New invariant should has no comment", inv.hasComment());
	}

	/**
	 * Ensures that an event can be created through the editor registry.
	 */
	@Test
	public void testCreateEvents() throws Exception {
		final IEvent evt = createElement(m0, IEvent.ELEMENT_TYPE);
		assertFalse("New event should has no label", evt.hasLabel());
		assertFalse("New event should has no comment", evt.hasComment());
		assertTrue("New event should has inherited attribute", evt
				.hasInherited());
		assertFalse("New event should not be inherited", evt.isInherited());
		assertFalse("New event should has no convergence", evt.hasConvergence());
	}

	/**
	 * Ensures that an event parameter can be created through the editor
	 * registry.
	 */
	@Test
	public void testCreateParameters() throws Exception {
		final IEvent evt = createElement(m0, IEvent.ELEMENT_TYPE);
		final IVariable param = createElement(evt, IVariable.ELEMENT_TYPE);
		assertTrue("New parameter should has identifier string", param
				.hasIdentifierString());
		assertEquals("Incorrect identifier string for new parameter", "var1",
				param.getIdentifierString());
		assertFalse("New parameter should has no comment", param.hasComment());
	}

	/**
	 * Ensures that an event guard can be created through the editor registry.
	 */
	@Test
	public void testCreateGuards() throws Exception {
		final IEvent evt = createElement(m0, IEvent.ELEMENT_TYPE);
		final IGuard guard = createElement(evt, IGuard.ELEMENT_TYPE);
		assertFalse("New guard should has no label", guard.hasLabel());
		assertFalse("New guard should has no predicate string", guard
				.hasPredicateString());
		assertFalse("New guard should has no comment", guard.hasComment());
	}

	/**
	 * Ensures that an event action can be created through the editor registry.
	 */
	@Test
	public void testCreateActions() throws Exception {
		final IEvent evt = createElement(m0, IEvent.ELEMENT_TYPE);
		final IAction act = createElement(evt, IAction.ELEMENT_TYPE);
		assertTrue("New action should has a label", act.hasLabel());
		assertEquals("Incorrect label for new action", "act1", act.getLabel());
		assertFalse("New action should has no assignment string", act
				.hasAssignmentString());
		assertFalse("New action should has no comment", act.hasComment());
	}

	/**
	 * Tests for
	 * {@link AttributeRelUISpecRegistry#getNumberOfAttributes(org.rodinp.core.IElementType)}.
	 */
	@Test
	public void testGetNumberOfAttributes() {
		assertNumberOfAttributes(IVariable.ELEMENT_TYPE, 1);
		assertNumberOfAttributes(IInvariant.ELEMENT_TYPE, 2);
		assertNumberOfAttributes(IEvent.ELEMENT_TYPE, 2);
		assertNumberOfAttributes(IGuard.ELEMENT_TYPE, 0);
		assertNumberOfAttributes(IAction.ELEMENT_TYPE, 1);
	}

	private void assertNumberOfAttributes(IInternalElementType<?> type,
			int expected) {
		final int actual = registry.getNumberOfAttributes(type);
		assertEquals("Incorrect number of attributes for " + type, expected,
				actual);
	}

	/**
	 * Tests for
	 * {@link AttributeRelUISpecRegistry#getEditComposites(org.rodinp.core.IElementType)}
	 * for {@link IVariable}.
	 */
	@Test
	public void testGetVariableEditComposites() {
		assertEditCompositeClasses(IVariable.ELEMENT_TYPE,
				TextEditComposite.class);
	}

	/**
	 * Tests for
	 * {@link AttributeRelUISpecRegistry#getEditComposites(org.rodinp.core.IElementType)}
	 * for {@link IInvariant}.
	 */
	@Test
	public void testGetInvariantEditComposites() {
		assertEditCompositeClasses(IInvariant.ELEMENT_TYPE,
				TextEditComposite.class, TextEditComposite.class);
	}

	/**
	 * Tests for
	 * {@link AttributeRelUISpecRegistry#getEditComposites(org.rodinp.core.IElementType)}
	 * for {@link IEvent}.
	 */
	@Test
	public void testGetEventEditComposites() {
		assertEditCompositeClasses(IEvent.ELEMENT_TYPE,
				CComboEditComposite.class, CComboEditComposite.class);
	}

	private void assertEditCompositeClasses(IInternalElementType<?> type,
			Class<?>... expected) {
		final IEditComposite[] composites = registry.getEditComposites(type);
		final int expLen = expected.length;
		assertEquals("Incorrect number of composites for " + type, expLen,
				composites.length);
		for (int i = 0; i < expLen; i++) {
			assertTrue("Wrong class for edit composite " + composites[i],
					expected[i].isInstance(composites[i]));
		}
	}

}
