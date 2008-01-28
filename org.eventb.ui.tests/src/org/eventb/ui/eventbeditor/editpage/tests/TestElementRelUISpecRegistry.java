/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich.
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

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.elementSpecs.ElementRelationship;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.editpage.ElementRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.IElementRelUISpecRegistry;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         jUnit tests for {@link ElementRelUISpecRegistry}.
 */
public class TestElementRelUISpecRegistry extends EventBUITest {

	/**
	 * The registry for testing. Using an extension of
	 * {@link ElementRelUISpecRegistry} for testing.
	 */
	IElementRelUISpecRegistry registry = ElementRelUISpecTestRegistry
			.getDefault();
	
	/**
	 * Tests for
	 * {@link ElementRelUISpecRegistry#getElementRelationships(org.rodinp.core.IElementType)}.
	 */
	@Test
	public void testGetElementRelationships() {
		
		// IMachineFile
		List<IElementRelationship> machineRelationships = registry
				.getElementRelationships(IMachineFile.ELEMENT_TYPE);
		
		assertNotNull("There should be some relationships for IMachineFile",
				machineRelationships);
		assertEquals("Incorrect number of relationships for IMachineFile", 4,
				machineRelationships.size());

		assertEquals(
				"Incorrect first relationship for IMachineFile",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".variable",
						null, null), machineRelationships.get(0));
		
		assertEquals(
				"Incorrect second relationship for IMachineFile",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".invariant",
						null, null), machineRelationships.get(1));
		
		assertEquals(
				"Incorrect third relationship for IMachineFile",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".event",
						null, null), machineRelationships.get(2));
		
		assertEquals(
				"Incorrect third relationship for IMachineFile",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".variant",
						null, null), machineRelationships.get(3));

		// IVariable
		List<IElementRelationship> variableRelationships = registry
			.getElementRelationships(IVariable.ELEMENT_TYPE);

		assertNotNull("Relationships for IVariable should not be null ",
				variableRelationships);
		assertEquals("There should be no relationships for IVariable", 0,
				variableRelationships.size());

		// IInvariant
		List<IElementRelationship> invariantRelationships = registry
			.getElementRelationships(IInvariant.ELEMENT_TYPE);

		assertNotNull("Relationships for IInvariant should not be null ",
				invariantRelationships);
		assertEquals("There should be no relationships for IInvariant", 0,
				invariantRelationships.size());
		
		// IEvent
		List<IElementRelationship> eventRelationships = registry
				.getElementRelationships(IEvent.ELEMENT_TYPE);
		
		assertNotNull("There should be some relationships for IEvent",
				eventRelationships);
		assertEquals("Incorrect number of relationships for IEvent", 3,
				eventRelationships.size());

		assertEquals(
				"Incorrect first relationship for IEvent",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".parameter",
						null, null), eventRelationships.get(0));
		
		assertEquals(
				"Incorrect second relationship for IEvent",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".guard",
						null, null), eventRelationships.get(1));
		
		assertEquals(
				"Incorrect third relationship for IEvent",
				new ElementRelationship(EventBPlugin.PLUGIN_ID + ".action",
						null, null), eventRelationships.get(2));
		
		// IGuard
		List<IElementRelationship> guardRelationships = registry
			.getElementRelationships(IGuard.ELEMENT_TYPE);

		assertNotNull("Relationships for IGuard should not be null ",
				guardRelationships);
		assertEquals("There should be no relationships for IGuard", 0,
				guardRelationships.size());
		
		// IAction
		List<IElementRelationship> actionRelationships = registry
			.getElementRelationships(IAction.ELEMENT_TYPE);

		assertNotNull("Relationships for IAction should not be null ",
				actionRelationships);
		assertEquals("There should be no relationships for IAction", 0,
				actionRelationships.size());
		
		// IVariant
		List<IElementRelationship> variantRelationships = registry
			.getElementRelationships(IVariant.ELEMENT_TYPE);

		assertNotNull("Relationships for IVariant should not be null ",
				variantRelationships);
		assertEquals("There should be no relationships for IVariant", 0,
				variantRelationships.size());
		
	}

	/**
	 * Tests for
	 * {@link ElementRelUISpecRegistry#getPrefix(IElementRelationship)}.
	 */
	@Test
	public void testGetPrefix() {
		// IMachineFile
		List<IElementRelationship> machineRelationships = registry
				.getElementRelationships(IMachineFile.ELEMENT_TYPE);
		
		assertEquals(
				"Incorrect prefix for the relationship between IMachineFile and IVariable",
				"VARIABLES", registry.getPrefix(machineRelationships.get(0)));
		
		assertEquals(
				"Incorrect prefix for the relationship between IMachineFile and IInvariant",
				"INVARIANTS", registry.getPrefix(machineRelationships.get(1)));
		
		assertEquals(
				"Incorrect prefix for the relationship between IMachineFile and IEvent",
				"EVENTS", registry.getPrefix(machineRelationships.get(2)));

		assertEquals(
				"Incorrect prefix for the relationship between IMachineFile and IVariant",
				"VARIANT", registry.getPrefix(machineRelationships.get(3)));

		// IEvent
		List<IElementRelationship> eventRelationships = registry
				.getElementRelationships(IEvent.ELEMENT_TYPE);

		assertEquals(
				"Incorrect prefix for the relationship between IEvent and IVariable",
				"ANY", registry.getPrefix(eventRelationships.get(0)));

		assertEquals(
				"Incorrect prefix for the relationship between IEvent and IGuard",
				"WHERE", registry.getPrefix(eventRelationships.get(1)));

		assertEquals(
				"Incorrect prefix for the relationship between IEvent and IAction",
				"THEN", registry.getPrefix(eventRelationships.get(2)));
	}
	
	/**
	 * Tests for
	 * {@link ElementRelUISpecRegistry#getPostfix(IElementRelationship)}.
	 */
	@Test
	public void testGetPostfix() {
		// IMachineFile
		List<IElementRelationship> machineRelationships = registry
				.getElementRelationships(IMachineFile.ELEMENT_TYPE);
		
		assertNull(
				"There should be no postfix for the relationship between IMachineFile and IVariable",
				registry.getPostfix(machineRelationships.get(0)));
		
		assertNull(
				"There should be no postfix for the relationship between IMachineFile and IInvariant",
				registry.getPostfix(machineRelationships.get(1)));
		
		assertEquals(
				"Incorrect postfix for the relationship between IMachineFile and IEvent",
				"END", registry.getPostfix(machineRelationships.get(2)));

		assertNull(
				"There should be no postfix for the relationship between IMachineFile and IVariant",
				registry.getPostfix(machineRelationships.get(3)));

		// IEvent
		List<IElementRelationship> eventRelationships = registry
				.getElementRelationships(IEvent.ELEMENT_TYPE);

		assertNull(
				"There should be no postfix for the relationship between IEvent and IVariable",
				registry.getPostfix(eventRelationships.get(0)));

		assertNull(
				"There should be no postfix for the relationship between IEvent and IGuard",
				registry.getPostfix(eventRelationships.get(1)));

		assertEquals(
				"Incorrect postfix for the relationship between IEvent and IAction",
				"END", registry.getPostfix(eventRelationships.get(2)));
	}
	
}
