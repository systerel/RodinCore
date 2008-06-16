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
 *******************************************************************************/

package org.eventb.ui.tests;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         jUnit tests for {@link EventBUtils}.
 */
public class TestEventBUtils extends EventBUITest {

	/**
	 * Tests for {@link EventBUtils#getAbstractMachine(IMachineFile)}.
	 */
	@Test
	public void testGetAbstractMachine() throws Exception {
		IMachineFile m0 = createMachine("m0"); //$NON-NLS-1$
		m0.save(null, true);
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		IMachineFile m1 = createMachine("m1"); //$NON-NLS-1$
		m1.save(null, true);
		assertNotNull("m1 should be created successfully ", m1); //$NON-NLS-1$

		// Testing that m1 refines m0
		IMachineFile abstractMachine = EventBUtils.getAbstractMachine(m1);
		assertNull(
				"There should not be any abstract machine at the moment for m1", //$NON-NLS-1$
				abstractMachine);

		IRefinesMachine refinesMachineClause = createRefinesMachineClause(m1,
				"m0"); //$NON-NLS-1$
		assertNotNull("The refines machine clause must not be null ", //$NON-NLS-1$
				refinesMachineClause);

		abstractMachine = EventBUtils.getAbstractMachine(m1);
		assertNotNull("The abstract machine should not be null ", //$NON-NLS-1$
				abstractMachine);
		assertEquals("Incorrect name of the abstract machine ", "m0", //$NON-NLS-1$ //$NON-NLS-2$
				abstractMachine.getBareName());

		// Testing that m0 refines m0 (i.e. a machine set to refine itself :-D).
		abstractMachine = EventBUtils.getAbstractMachine(m0);
		assertNull(
				"There should not be any abstract machine at the moment for m0", //$NON-NLS-1$
				abstractMachine);

		refinesMachineClause = createRefinesMachineClause(m0, "m0"); //$NON-NLS-1$
		assertNotNull("The refines machine clause must not be null ", //$NON-NLS-1$
				refinesMachineClause);

		abstractMachine = EventBUtils.getAbstractMachine(m0);
		assertNotNull("The abstract machine should not be null ", //$NON-NLS-1$
				abstractMachine);
		assertEquals("Incorrect name of the abstract machine ", "m0", //$NON-NLS-1$ //$NON-NLS-2$
				abstractMachine.getBareName());

		// Test if m0 refines m2 (m2 does not exist).
		refinesMachineClause.setAbstractMachineName("m2", //$NON-NLS-1$
				null);
		abstractMachine = EventBUtils.getAbstractMachine(m0);
		assertNotNull("The abstract machine should not be null ", //$NON-NLS-1$
				abstractMachine);
		assertEquals("Incorrect name of the abstract machine ", "m2", //$NON-NLS-1$ //$NON-NLS-2$
				abstractMachine.getBareName());

		// Test if there are more than one Refines Event clause
		refinesMachineClause = createRefinesMachineClause(m0, "m1"); //$NON-NLS-1$
		assertNotNull("The refines machine clause must not be null ", //$NON-NLS-1$
				refinesMachineClause);

		abstractMachine = EventBUtils.getAbstractMachine(m0);
		assertNull("There should be no abstract machine ", abstractMachine); //$NON-NLS-1$

	}

	/**
	 * Tests for {@link EventBUtils#getAbstractEvent(IEvent)}.
	 */
	@Test
	public void testGetAbstractEvent() throws Exception {
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

		// Test for having no abstract event for m1Event because m1 does not
		// have refines machine clause.
		IEvent abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having no abstract event for m1Event because m1Event is
		// non-inherited and having no refines event clause.
		IRefinesMachine refinesMachine = createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		assertNotNull(
				"The refines machine clause should be created successfully", //$NON-NLS-1$
				refinesMachine);
		abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having abstract event since m1Event is inherited and m0Event
		// having the same label as m1Event.
		m1Event.setInherited(true, null);
		abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNotNull(
				"There should be an abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);
		assertEquals("Incorrect abstract machine ", m0, abstractEvent //$NON-NLS-1$
				.getParent());
		String label = abstractEvent.getLabel();
		assertEquals("Incorrect abstract event label (inherited)", "event", //$NON-NLS-1$ //$NON-NLS-2$
				label);

		// Test for having no abstract event because m1Event is inherited and
		// there is no corresponding event in the m0.
		m0Event.setLabel("abstract_event", null); //$NON-NLS-1$
		abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having abstract event because m1Event is non-inherited and
		// having refines event clause point to an existing event in m0.
		m1Event.setInherited(false, null);
		createRefinesEventClause(m1Event, "abstract_event"); //$NON-NLS-1$
		abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNotNull(
				"There should be an abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);
		assertEquals("Incorrect abstract machine ", m0, abstractEvent //$NON-NLS-1$
				.getParent());

		label = abstractEvent.getLabel();
		assertEquals("Incorrect abstract event label (non-inherited)", //$NON-NLS-1$
				"abstract_event", label); //$NON-NLS-1$

		// Test for having no abstract event because m1Event is non-inherited
		// and
		// having refines event clause point to an non-existing event in m0.
		m0Event.setLabel("event", null); //$NON-NLS-1$
		abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having no abstract event because m1 refines a non-existing
		// machine m2.
		refinesMachine.setAbstractMachineName("m2", //$NON-NLS-1$
				null);
		abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);
	}

	/**
	 * Tests for {@link EventBUtils#getNonInheritedAbstractEvent(IEvent)}.
	 */
	@Test
	public void testGetNonInheritedAbstractEvent() throws Exception {
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

		// Test for having no non-inherited abstract event because m2Event does
		// not have any abstract event.
		IEvent nonInheritedAbstractEvent = EventBUtils
				.getNonInheritedAbstractEvent(m2Event);
		assertNull(
				"There should be no non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having non-inherited abstract event because m2 refines m1
		// and m2Event refines m1Event, and m1Event is non-inherited.
		// Make m2 refines m1.
		createRefinesMachineClause(m2, "m1"); //$NON-NLS-1$

		// m2Event is inherited, i.e m2Event refines m1Event.
		m2Event.setInherited(true, null);
		nonInheritedAbstractEvent = EventBUtils
				.getNonInheritedAbstractEvent(m2Event);
		assertNotNull(
				"There should be a non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);
		assertEquals("Incorrect non-inherited abstract", m1Event, //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having no non-inherited abstract event because m2 refines
		// m1
		// and m2Event refines m1Event, and m1Event is inherited but does not
		// correspond to any abstract event.
		m1Event.setInherited(true, null);
		nonInheritedAbstractEvent = EventBUtils
				.getNonInheritedAbstractEvent(m2Event);
		assertNull(
				"There should be no non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having no non-inherited abstract event because m2 refines
		// m1
		// and m2Event refines m1Event, and m1Event is inherited, but m1 refines
		// m2 hence m1Event refines m2Event, hence looping.

		// Make m1 refines m2.
		IRefinesMachine m1RefinesMachine = createRefinesMachineClause(m1, "m2"); //$NON-NLS-1$
		nonInheritedAbstractEvent = EventBUtils
				.getNonInheritedAbstractEvent(m2Event);
		assertNull(
				"There should be no non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having non-inherited abstract event because m2 refines m1
		// and m2Event refines m1Event, and m1Event is inherited, and m1 refines
		// m0 hence m1Event refines m0Event.
		m1RefinesMachine.setAbstractMachineName("m0", //$NON-NLS-1$
				null);
		nonInheritedAbstractEvent = EventBUtils
				.getNonInheritedAbstractEvent(m2Event);
		assertNotNull(
				"There should be a non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);
		assertEquals("Incorrect non-inherited abstract", m0Event, //$NON-NLS-1$
				nonInheritedAbstractEvent);
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
