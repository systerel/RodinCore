/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich and others.
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
 *******************************************************************************/
package org.eventb.ui.tests;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         jUnit tests for {@link EventBUtils}.
 */
public class TestEventBUtils extends EventBUITest {

	private void assertAbstractMachine(IMachineFile machine,
			IMachineFile expected) throws RodinDBException {
		final IMachineFile actual = EventBUtils.getAbstractMachine(machine);
		assertEquals("Unexpected abstract machine", //$NON-NLS-1$
				expected, actual);
	}

	/**
	 * Ensures that a machine without a refines clause has no abstract machine.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineFile)
	 */
	@Test
	public void testAbstractMachineNoRefinesClause() throws Exception {
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertAbstractMachine(m0, null);
	}

	/**
	 * Ensures that a machine with a refines clause has an abstract machine.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineFile)
	 */
	@Test
	public void testAbstractMachineWithRefinesClause() throws Exception {
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		final IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		m1.save(null, true);
		assertAbstractMachine(m1, m0);
	}

	/**
	 * Ensures that a machine with a refines clause has an abstract machine,
	 * even when the file doesn't exist.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineFile)
	 */
	@Test
	public void testAbstractMachineWithRefinesClauseToInexistent()
			throws Exception {
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		createRefinesMachineClause(m0, "inexistent"); //$NON-NLS-1$
		m0.save(null, true);
		final String expName = EventBPlugin.getMachineFileName("inexistent"); //$NON-NLS-1$
		final IMachineFile expected = (IMachineFile) rodinProject
				.getRodinFile(expName);
		assertAbstractMachine(m0, expected);
	}

	/**
	 * Ensures that a machine with a refines clause has an abstract machine that
	 * can be itself.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineFile)
	 */
	@Test
	public void testAbstractMachineWithRefinesClauseToItself() throws Exception {
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		createRefinesMachineClause(m0, "m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertAbstractMachine(m0, m0);
	}

	/**
	 * Ensures that a machine with two refines clauses has no abstract machine.
	 * 
	 * @see EventBUtils#getAbstractMachine(IMachineFile)
	 */
	@Test
	public void testAbstractMachineTwoRefinesClause() throws Exception {
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		final IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		m1.save(null, true);
		final IMachineFile m2 = createMachine("m2"); //$NON-NLS-1$
		createRefinesMachineClause(m2, "m0"); //$NON-NLS-1$
		createRefinesMachineClause(m2, "m1"); //$NON-NLS-1$
		m2.save(null, true);
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
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		createRefinesEventClause(m0Event, "event"); //$NON-NLS-1$
		m0.save(null, true);
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
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		createRefinesMachineClause(m0, "inexistent"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		createRefinesEventClause(m0Event, "event"); //$NON-NLS-1$
		m0.save(null, true);
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
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		createEvent(m0, "event"); //$NON-NLS-1$
		m0.save(null, true);
		final IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		m1.save(null, true);
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
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		final IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		createRefinesEventClause(m1Event, "event"); //$NON-NLS-1$
		m1.save(null, true);
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
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		m0.save(null, true);
		final IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		createRefinesEventClause(m1Event, "event"); //$NON-NLS-1$
		m1.save(null, true);
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
		final IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		final IEvent m0Event = createEvent(m0, "abstract_event"); //$NON-NLS-1$
		m0.save(null, true);
		final IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		final IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		createRefinesEventClause(m1Event, "abstract_event"); //$NON-NLS-1$
		m1.save(null, true);
		assertAbstractEvent(m1Event, m0Event);
	}

	/**
	 * Tests for {@link EventBUtils#getNonExtendedAbstractEvent(IEvent)}.
	 */
	@Test
	public void testGetNonExtendedAbstractEvent() throws Exception {
		IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		IEvent m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		assertNotNull(
				"The event m0Event should have been created successfully", //$NON-NLS-1$
				m0Event);

		IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		m1.save(null, true);
		assertNotNull("m1 should be created successfully ", m1); //$NON-NLS-1$

		IEvent m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		assertNotNull(
				"The event m1Event should have been created successfully", //$NON-NLS-1$
				m1Event);

		IMachineFile m2 = createMachine("m2"); //$NON-NLS-1$
		m2.save(null, true);
		assertNotNull("m2 should be created successfully ", m2); //$NON-NLS-1$

		IEvent m2Event = createEvent(m2, "event"); //$NON-NLS-1$
		assertNotNull(
				"The event m1Event should have been created successfully", //$NON-NLS-1$
				m2Event);

		// Test for having no non-extended abstract event because m2Event does
		// not have any abstract event.
		IEvent nonExtendedAbstractEvent = EventBUtils
				.getNonExtendedAbstractEvent(m2Event);
		assertNull(
				"There should be no non-extended abstract event for m2Event", //$NON-NLS-1$
				nonExtendedAbstractEvent);

		// Testing for having non-extended abstract event because m2 refines m1
		// and m2Event refines m1Event, and m1Event is non-extended.
		// Make m2 refines m1.
		createRefinesMachineClause(m2, "m1"); //$NON-NLS-1$

		// m2Event is extended, i.e m2Event refines m1Event.
		m2Event.setExtended(true, null);
		nonExtendedAbstractEvent = EventBUtils
				.getNonExtendedAbstractEvent(m2Event);
		assertNotNull(
				"There should be a non-extended abstract event for m2Event", //$NON-NLS-1$
				nonExtendedAbstractEvent);
		assertEquals("Incorrect non-extended abstract", m1Event, //$NON-NLS-1$
				nonExtendedAbstractEvent);

		// Testing for having no non-extended abstract event because m2 refines
		// m1
		// and m2Event refines m1Event, and m1Event is extended but does not
		// correspond to any abstract event.
		m1Event.setExtended(true, null);
		nonExtendedAbstractEvent = EventBUtils
				.getNonExtendedAbstractEvent(m2Event);
		assertNull(
				"There should be no non-extended abstract event for m2Event", //$NON-NLS-1$
				nonExtendedAbstractEvent);

		// Testing for having no non-extended abstract event because m2 refines
		// m1
		// and m2Event refines m1Event, and m1Event is extended, but m1 refines
		// m2 hence m1Event refines m2Event, hence looping.

		// Make m1 refines m2.
		IRefinesMachine m1RefinesMachine = createRefinesMachineClause(m1, "m2"); //$NON-NLS-1$
		nonExtendedAbstractEvent = EventBUtils
				.getNonExtendedAbstractEvent(m2Event);
		assertNull(
				"There should be no non-extended abstract event for m2Event", //$NON-NLS-1$
				nonExtendedAbstractEvent);

		// Testing for having non-extended abstract event because m2 refines m1
		// and m2Event refines m1Event, and m1Event is extended, and m1 refines
		// m0 hence m1Event refines m0Event.
		m1RefinesMachine.setAbstractMachineName("m0", //$NON-NLS-1$
				null);
		nonExtendedAbstractEvent = EventBUtils
				.getNonExtendedAbstractEvent(m2Event);
		assertNotNull(
				"There should be a non-extended abstract event for m2Event", //$NON-NLS-1$
				nonExtendedAbstractEvent);
		assertEquals("Incorrect non-extended abstract", m0Event, //$NON-NLS-1$
				nonExtendedAbstractEvent);
	}

	/**
	 * Tests for
	 * {@link EventBUtils#getFreeChildName(org.rodinp.core.IInternalParent, org.rodinp.core.IInternalElementType, String)}.
	 */
	@Test
	public void testGetFreeChildName() throws Exception {
		IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		// Currently, there are no events so the free child name should be
		// "event1".
		String childName = EventBUtils.getFreeChildName(m0,
				IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "event1", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Gets the free child name again and it should return event1 still.
		childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "event1", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Creates "event1".
		createEvent(m0, "event1", "event1Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// There is "event1" so the free child name now should be "event2".
		childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "event2", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Creates "event3".
		createEvent(m0, "event3", "event3Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// There are "event1", "event3" so the free child name now should be
		// "event4"
		childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "event4", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Gets the free child name again and it should be "event4".
		childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "event4", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Create "event2"
		createEvent(m0, "event2", "event2Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// Gets the free child name and it should be "event4".
		childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "event4", childName); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test for
	 * {@link EventBUtils#getFreeChildNameIndex(org.rodinp.core.IInternalParent, org.rodinp.core.IInternalElementType, String)}.
	 */
	@Test
	public void testGetFreeChildNameIndex() throws Exception {
		IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		// There are no events, so the free index now should be 1.
		String freeIndex = EventBUtils.getFreeChildNameIndex(m0,
				IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "1", freeIndex); //$NON-NLS-1$

		// Do it again and should be 1 still.
		freeIndex = EventBUtils.getFreeChildNameIndex(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "1", freeIndex); //$NON-NLS-1$

		// Create "event1"
		createEvent(m0, "event1", "event1Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// There is "event1" so the free index now should be 2.
		freeIndex = EventBUtils.getFreeChildNameIndex(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "2", freeIndex); //$NON-NLS-1$

		// Gets the free index again and it should be 2 still.
		freeIndex = EventBUtils.getFreeChildNameIndex(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "2", freeIndex); //$NON-NLS-1$

		// Create "event3"
		createEvent(m0, "event3", "event3Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// There are now "event1", "event3" so the free index should be 4.
		freeIndex = EventBUtils.getFreeChildNameIndex(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect child name", "4", freeIndex); //$NON-NLS-1$
	}

	/**
	 * Tests for
	 * {@link EventBUtils#getFreeChildNameIndex(org.rodinp.core.IInternalParent, org.rodinp.core.IInternalElementType, String)}.
	 */
	@Test
	public void testGetFreeChildNameIndexWithBeginIndex() throws Exception {
		IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		// There are no events so the free index is 1.
		String freeIndex = EventBUtils.getFreeChildNameIndex(m0,
				IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "1", freeIndex); //$NON-NLS-1$

		// Create "event1".
		createEvent(m0, "event1", "event1Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// There is "event1" so the free index is 2.
		freeIndex = EventBUtils.getFreeChildNameIndex(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "2", freeIndex); //$NON-NLS-1$

		// Create "event3"
		createEvent(m0, "event3", "event3Label"); //$NON-NLS-1$ //$NON-NLS-2$

		// There are "event1" and "event3" so the free index is 4.
		freeIndex = EventBUtils.getFreeChildNameIndex(m0, IEvent.ELEMENT_TYPE,
				"event"); //$NON-NLS-1$
		assertEquals("Incorrect free child name index ", "4", freeIndex); //$NON-NLS-1$

	}

}
