/*******************************************************************************
 * Copyright (c) 2009 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - modified expected results after getFreeIndex modification
 *     Systerel - replaced inherited by extended
 *     Systerel - refactored all tests to make them unit tests
 *     Systerel - added tests for getImplicitChildren()
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.ui.tests;

import java.util.Arrays;

import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Unit tests for static methods of class {@link EventBUtils}.
 * 
 * @author htson
 * @author Laurent Voisin
 */
public class TestEventBUtils extends EventBUITest {

	private void assertAbstractMachine(IMachineRoot machine,
			IMachineRoot expected) throws RodinDBException {
		final IMachineRoot actual = EventBUtils.getAbstractMachine(machine);
		assertEquals("Unexpected abstract machine", //$NON-NLS-1$
				expected, actual);
	}

	/**
	 * Ensures that a machine without a refines clause has no abstract machine.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineRoot)
	 */
	@Test
	public void testAbstractMachineNoRefinesClause() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		assertAbstractMachine(m0, null);
	}

	/**
	 * Ensures that a machine with a refines clause has an abstract machine.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineRoot)
	 */
	@Test
	public void testAbstractMachineWithRefinesClause() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		m1.getRodinFile().save(null, true);
		assertAbstractMachine(m1, m0);
	}

	/**
	 * Ensures that a machine with a refines clause has an abstract machine,
	 * even when the file doesn't exist.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineRoot)
	 */
	@Test
	public void testAbstractMachineWithRefinesClauseToInexistent()
			throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		createRefinesMachineClause(m0, "inexistent"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final String expName = EventBPlugin.getMachineFileName("inexistent"); //$NON-NLS-1$
		final IRodinFile expected = rodinProject.getRodinFile(expName);
		assertAbstractMachine(m0, (IMachineRoot) expected.getRoot());
	}

	/**
	 * Ensures that a machine with a refines clause has an abstract machine that
	 * can be itself.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineRoot)
	 */
	@Test
	public void testAbstractMachineWithRefinesClauseToItself() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		createRefinesMachineClause(m0, "m0"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		assertAbstractMachine(m0, m0);
	}

	/**
	 * Ensures that a machine with two refines clauses has no abstract machine.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineRoot)
	 */
	@Test
	public void testAbstractMachineTwoRefinesClause() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		m1.getRodinFile().save(null, true);
		final IMachineRoot m2 = createMachine("m2"); //$NON-NLS-1$
		createRefinesMachineClause(m2, "m0"); //$NON-NLS-1$
		createRefinesMachineClause(m2, "m1"); //$NON-NLS-1$
		m2.getRodinFile().save(null, true);
		assertAbstractMachine(m2, null);
	}

	private void assertAbstractEvent(IEvent event, IEvent expected)
			throws RodinDBException {
		final IEvent actual = EventBUtils.getAbstractEvent(event);
		assertEquals("Unexpected abstract event", //$NON-NLS-1$
				expected, actual);
	}

	/**
	 * Ensures that an event has no abstraction when its machine has no refines
	 * machine clause (even if the event has a refines event clause).
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventNoRefinesMachine() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		createRefinesEventClause(m0Event, "event"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		assertAbstractEvent(m0Event, null);
	}

	/**
	 * Ensures that an event has no abstraction when its machine has a refines
	 * machine clause, but pointing to an inexistent machine (even if the event
	 * has a refines event clause).
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventNoAbstractMachine() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		createRefinesMachineClause(m0, "inexistent"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		createRefinesEventClause(m0Event, "event"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		assertAbstractEvent(m0Event, null);
	}

	/**
	 * Ensures that an event has no abstraction when its machine has a refines
	 * machine clause, but the event has no refines event clause.
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventNoRefinesEvent() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		createEvent(m0, "event"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		m1.getRodinFile().save(null, true);
		assertAbstractEvent(m1Event, null);
	}

	/**
	 * Ensures that an event has no abstraction when its machine has a refines
	 * machine clause and the event has a refines event clause to an event that
	 * doesn't exist in the abstract machine.
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventInexistent() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		createRefinesEventClause(m1Event, "event"); //$NON-NLS-1$
		m1.getRodinFile().save(null, true);
		assertAbstractEvent(m1Event, null);
	}

	/**
	 * Ensures that an event has an abstraction when its machine has a refines
	 * machine clause and the event has a refines event clause to an event of
	 * the abstract machine with the same name.
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventSameName() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		createRefinesEventClause(m1Event, "event"); //$NON-NLS-1$
		m1.getRodinFile().save(null, true);
		assertAbstractEvent(m1Event, m0Event);
	}

	/**
	 * Ensures that an event has an abstraction when its machine has a refines
	 * machine clause and the event has a refines event clause to an event of
	 * the abstract machine with a different name.
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventDifferentName() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "abstract_event"); //$NON-NLS-1$
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		createRefinesEventClause(m1Event, "abstract_event"); //$NON-NLS-1$
		m1.getRodinFile().save(null, true);
		assertAbstractEvent(m1Event, m0Event);
	}

	/**
	 * Ensures that the abstraction of the INITIALISATION event is correctly returned.
	 * 
	 * @see EventBUtils#getAbstractEvent(IEvent)
	 */
	@Test
	public void testAbstractEventINITIALISATION() throws Exception {
		final IMachineRoot m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "foo", IEvent.INITIALISATION);
		m0.getRodinFile().save(null, true);
		final IMachineRoot m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "bar", IEvent.INITIALISATION);
		m1.getRodinFile().save(null, true);
		assertAbstractEvent(m1Event, m0Event);
	}

	private void assertFreeChildName(final int expected,
			final IInternalElement parent, final IInternalElementType<?> type,
			final String prefix) throws RodinDBException {
		final String actual = EventBUtils.getFreeChildNameIndex(parent, type,
				prefix);
		assertEquals("" + expected, actual);
		final String childName = EventBUtils.getFreeChildName(parent, type,
				prefix);
		assertEquals("Incorrect child name", prefix + expected, childName);
	}

	/**
	 * Ensures that the first free child name is numbered "1".
	 * 
	 * @see EventBUtils#getFreeChildName(IInternalElement, IInternalElementType,
	 *      String)
	 * @see EventBUtils#getFreeChildNameIndex(IInternalElement,
	 *      IInternalElementType, String)
	 */
	@Test
	public void testGetFreeChildNameNone() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		m0.getRodinFile().save(null, true);
		assertFreeChildName(1, m0, IEvent.ELEMENT_TYPE, "event");
	}

	/**
	 * Ensures that the second free child name is numbered "2".
	 * 
	 * @see EventBUtils#getFreeChildName(IInternalElement, IInternalElementType,
	 *      String)
	 * @see EventBUtils#getFreeChildNameIndex(IInternalElement,
	 *      IInternalElementType, String)
	 */
	@Test
	public void testGetFreeChildNameOne() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		createEvent(m0, "event1", "label1");
		m0.getRodinFile().save(null, true);
		assertFreeChildName(2, m0, IEvent.ELEMENT_TYPE, "event");
	}

	/**
	 * Ensures that the nth free child name is numbered 1 greater than the
	 * greatest children.
	 * 
	 * @see EventBUtils#getFreeChildName(IInternalElement, IInternalElementType,
	 *      String)
	 * @see EventBUtils#getFreeChildNameIndex(IInternalElement,
	 *      IInternalElementType, String)
	 */
	@Test
	public void testGetFreeChildNameNth() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		createEvent(m0, "event1", "label1");
		createEvent(m0, "event3", "label2");
		m0.getRodinFile().save(null, true);
		assertFreeChildName(4, m0, IEvent.ELEMENT_TYPE, "event");
	}

	private <T> void assertEquals(T[] expected, T[] actual) {
		assertEquals(Arrays.asList(expected), Arrays.asList(actual));
	}

	private void assertImplicitChildren(IEvent event, IRodinElement... expected)
			throws Exception {
		final IRodinElement[] actual = EventBUtils.getImplicitChildren(event);
		assertEquals(expected, actual);
	}

	/**
	 * Ensures that no children is returned for an event of a top-level machine.
	 * 
	 * @see EventBUtils#getImplicitChildren(IEvent)
	 */
	@Test
	public void testImplicitChildrenTop() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		final IEvent m0Event = createEvent(m0, "event");
		m0.getRodinFile().save(null, true);
		assertImplicitChildren(m0Event);
	}

	/**
	 * Ensures that no children is returned for an event that is not extended.
	 * 
	 * @see EventBUtils#getImplicitChildren(IEvent)
	 */
	@Test
	public void testImplicitChildrenNotExtended() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		final IEvent m0Event = createEvent(m0, "event");
		createParameter(m0Event, "p");
		createGuard(m0Event, "grd1", "foo");
		createWitness(m0Event, "wit1", "bar");
		createAction(m0Event, "act1", "baz");
		m0.getRodinFile().save(null, true);

		final IMachineRoot m1 = createMachine("m1");
		createRefinesMachineClause(m1, "m0");
		final IEvent m1Event = createEvent(m1, "event");
		createRefinesEventClause(m1Event, "event");
		m1.getRodinFile().save(null, true);

		assertImplicitChildren(m1Event);
	}

	/**
	 * Ensures that no children is returned for an event that has no
	 * abstraction.
	 * 
	 * @see EventBUtils#getImplicitChildren(IEvent)
	 */
	@Test
	public void testImplicitChildrenNoAbstraction() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		final IEvent m0Event = createEvent(m0, "event");
		createParameter(m0Event, "p");
		createGuard(m0Event, "grd1", "foo");
		createWitness(m0Event, "wit1", "bar");
		createAction(m0Event, "act1", "baz");
		m0.getRodinFile().save(null, true);

		final IMachineRoot m1 = createMachine("m1");
		createRefinesMachineClause(m1, "m0");
		final IEvent m1Event = createEvent(m1, "event");
		m1Event.setExtended(true, null);
		m1.getRodinFile().save(null, true);

		assertAbstractEvent(m1Event, null);
		assertImplicitChildren(m1Event);
	}

	/**
	 * Ensures that the children of the abstraction of an extended event are
	 * properly returned, when the abstract event is not itself extended.
	 * 
	 * @see EventBUtils#getImplicitChildren(IEvent)
	 */
	@Test
	public void testImplicitChildrenSimple() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		final IEvent m0Event = createEvent(m0, "event");
		final IParameter p = createParameter(m0Event, "p");
		final IGuard grd1 = createGuard(m0Event, "grd1", "foo");
		createWitness(m0Event, "wit1", "bar");
		final IAction act1 = createAction(m0Event, "act1", "baz");
		m0.getRodinFile().save(null, true);

		final IMachineRoot m1 = createMachine("m1");
		createRefinesMachineClause(m1, "m0");
		final IEvent m1Event = createEvent(m1, "event");
		createRefinesEventClause(m1Event, "event");
		m1Event.setExtended(true, null);
		m1.getRodinFile().save(null, true);

		assertImplicitChildren(m1Event, p, grd1, act1);
	}

	/**
	 * Ensures that the children of several abstractions of an extended event
	 * are properly returned, when some abstract event are themselves extended.
	 * 
	 * @see EventBUtils#getImplicitChildren(IEvent)
	 */
	@Test
	public void testImplicitChildrenMultiple() throws Exception {
		final IMachineRoot m0 = createMachine("m0");
		final IEvent m0Event = createEvent(m0, "event");
		final IParameter p = createParameter(m0Event, "p");
		final IGuard grd1 = createGuard(m0Event, "grd1", "foo");
		createWitness(m0Event, "wit1", "bar");
		final IAction act1 = createAction(m0Event, "act1", "baz");
		m0.getRodinFile().save(null, true);

		final IMachineRoot m1 = createMachine("m1");
		createRefinesMachineClause(m1, "m0");
		final IEvent m1Event = createEvent(m1, "event");
		m1Event.setExtended(true, null);
		createRefinesEventClause(m1Event, "event");
		final IParameter q = createParameter(m1Event, "q");
		final IGuard grd2 = createGuard(m1Event, "grd2", "foo");
		createWitness(m1Event, "wit2", "bar");
		final IAction act2 = createAction(m1Event, "act2", "baz");
		m1.getRodinFile().save(null, true);

		final IMachineRoot m2 = createMachine("m2");
		createRefinesMachineClause(m2, "m1");
		final IEvent m2Event = createEvent(m2, "event");
		createRefinesEventClause(m2Event, "event");
		m2Event.setExtended(true, null);
		m2.getRodinFile().save(null, true);

		assertImplicitChildren(m2Event, p, grd1, act1, q, grd2, act2);
	}

	private static void setGenerated(IInternalElement element)
			throws RodinDBException {
		element.setAttributeValue(EventBAttributes.GENERATED_ATTRIBUTE, true,
				null);
	}
	
	private static void assertReadOnly(IInternalElement element) {
		assertTrue("Read only expected", EventBUtils.isReadOnly(element));
	}

	private static void assertNotReadOnly(IInternalElement element) {
		assertFalse("Expected NOT read only", EventBUtils.isReadOnly(element));
	}

	/**
	 * Ensures that a generated element (bearing the generated attribute) is
	 * read only.
	 */
	@Test
	public void testIsReadOnlyInternalElement() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		final IInvariant inv = createInvariant(mch, "inv", "", false);

		assertNotReadOnly(inv);
		setGenerated(inv);
		assertReadOnly(inv);
	}
	
	/**
	 * Ensures that a generated root element (bearing the generated attribute)
	 * is read only as well as its descendants.
	 */
	@Test
	public void testIsReadOnlyRoot() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		final IInvariant inv = createInvariant(mch, "inv", "", false);
		
		assertNotReadOnly(mch);
		assertNotReadOnly(inv);
		setGenerated(mch);
		assertReadOnly(mch);
		assertReadOnly(inv);
	}

	/**
	 * Ensures that elements inside a generated subtree are read only and
	 * elements outside are not read only.
	 */
	@Test
	public void testIsReadOnlySubTree() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		final IInvariant inv = createInvariant(mch, "inv", "", false);
		final IEvent evt = createEvent(mch, "evt");
		final IAction act = createAction(evt, "act", "");
		
		setGenerated(evt);
		assertReadOnly(evt);
		assertReadOnly(act);
		assertNotReadOnly(mch);
		assertNotReadOnly(inv);
	}

}
