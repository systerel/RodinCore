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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.eventbeditor.editpage.ElementRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.IElementRelUISpecRegistry;
import org.junit.Test;
import org.rodinp.core.IElementType;

/**
 * @author htson
 *         <p>
 *         jUnit tests for {@link ElementRelUISpecRegistry}.
 */
public class TestElementRelUISpecRegistry {

	private static final class MockRelationship {
		final String id;
		final String prefix;
		final String postfix;

		MockRelationship(String id, String prefix, String postfix) {
			this.id = id;
			this.prefix = prefix;
			this.postfix = postfix;
		}

		void matches(IElementRelationship rel) {
			assertEquals(id, rel.getID());
			assertEquals(prefix, registry.getPrefix(rel));
			assertEquals(postfix, registry.getPostfix(rel));
		}
	}

	/**
	 * The registry for testing. Using an extension of
	 * {@link ElementRelUISpecRegistry} for testing.
	 */
	static final IElementRelUISpecRegistry registry = ElementRelUISpecTestRegistry
			.getDefault();
	
	private static final MockRelationship rel(String id, String prefix, String postfix) {
		return new MockRelationship(EventBPlugin.PLUGIN_ID + "." + id, prefix, postfix);
	}

	@Test
	public void machineRelationships() {
		assertRelationships(IMachineFile.ELEMENT_TYPE,
				rel("variable", "VARIABLES", null),
				rel("invariant", "INVARIANTS", null),
				rel("event", "EVENTS", "END"),
				rel("variant", "VARIANT", null)
		);
	}

	@Test
	public void variableRelationships() {
		assertRelationships(IVariable.ELEMENT_TYPE);
	}
	
	@Test
	public void invariantRelationships() {
		assertRelationships(IInvariant.ELEMENT_TYPE);
	}
	
	@Test
	public void eventRelationships() {
		assertRelationships(IEvent.ELEMENT_TYPE,
				rel("parameter", "ANY", null),
				rel("guard", "WHERE", null),
				rel("action", "THEN", "END")
		);
	}
	
	@Test
	public void guardRelationships() {
		assertRelationships(IGuard.ELEMENT_TYPE);
	}
	
	@Test
	public void actionRelationships() {
		assertRelationships(IAction.ELEMENT_TYPE);
	}
	
	@Test
	public void variantRelationships() {
		assertRelationships(IVariant.ELEMENT_TYPE);
	}
	
	private void assertRelationships(IElementType<?> type,
			MockRelationship... expected) {
		final List<IElementRelationship> actual = registry
				.getElementRelationships(type);
		final int expLen = expected.length;
		assertEquals("Incorrect number of relationships for " + type, expLen,
				actual.size());
		for (int i = 0; i < expLen; i++) {
			expected[i].matches(actual.get(i));
		}
	}

}
