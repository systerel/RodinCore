/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Rodin @ ETH Zurich
******************************************************************************/

package org.eventb.ui.tests;

import org.eclipse.core.runtime.NullProgressMonitor;
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

	/**
	 * Tests for {@link EventBUtils#getAbstractMachine(IMachineFile)}.
	 */
	@Test
	public void testGetAbstractMachine() {
		IMachineFile m0 = null;
		try {
			m0 = createMachine("m0"); //$NON-NLS-1$
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		IMachineFile m1 = null;
		try {
			m1 = createMachine("m1"); //$NON-NLS-1$
			m1.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m1", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m1 should be created successfully ", m1); //$NON-NLS-1$

		// Testing that m1 refines m0
		IMachineFile abstractMachine;
		try {
			abstractMachine = EventBUtils.getAbstractMachine(m1);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract machine of  m1", //$NON-NLS-1$
					false);
			return;
		}

		assertNull(
				"There should not be any abstract machine at the moment for m1", //$NON-NLS-1$
				abstractMachine);

		IRefinesMachine refinesMachineClause;
		try {
			refinesMachineClause = createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating refines machine clause of  m1", //$NON-NLS-1$
					false);
			return;
		}

		assertNotNull("The refines machine clause must not be null ", //$NON-NLS-1$
				refinesMachineClause);

		try {
			abstractMachine = EventBUtils.getAbstractMachine(m1);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract machine of  m1", //$NON-NLS-1$
					false);
			return;
		}

		assertNotNull("The abstract machine should not be null ", //$NON-NLS-1$
				abstractMachine);
		assertEquals("Incorrect name of the abstract machine ", "m0", //$NON-NLS-1$ //$NON-NLS-2$
				abstractMachine.getBareName());

		// Testing that m0 refines m0 (i.e. a machine set to refine itself :-D).
		try {
			abstractMachine = EventBUtils.getAbstractMachine(m0);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract machine of  m0", //$NON-NLS-1$
					false);
			return;
		}

		assertNull(
				"There should not be any abstract machine at the moment for m0", //$NON-NLS-1$
				abstractMachine);

		try {
			refinesMachineClause = createRefinesMachineClause(m0, "m0"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating refines machine clause of  m0", //$NON-NLS-1$
					false);
			return;
		}

		assertNotNull("The refines machine clause must not be null ", //$NON-NLS-1$
				refinesMachineClause);

		try {
			abstractMachine = EventBUtils.getAbstractMachine(m0);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract machine of  m0", //$NON-NLS-1$
					false);
			return;
		}

		assertNotNull("The abstract machine should not be null ", //$NON-NLS-1$
				abstractMachine);
		assertEquals("Incorrect name of the abstract machine ", "m0", //$NON-NLS-1$ //$NON-NLS-2$
				abstractMachine.getBareName());

		// Test if m0 refines m2 (m2 does not exist).
		try {
			refinesMachineClause.setAbstractMachineName("m2", //$NON-NLS-1$
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the abstract machine name for  refines machine clause of  m0", //$NON-NLS-1$
					false);
			return;
		}
		try {
			abstractMachine = EventBUtils.getAbstractMachine(m0);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract machine of  m0", //$NON-NLS-1$
					false);
			return;
		}

		assertNotNull("The abstract machine should not be null ", //$NON-NLS-1$
				abstractMachine);
		assertEquals("Incorrect name of the abstract machine ", "m2", //$NON-NLS-1$ //$NON-NLS-2$
				abstractMachine.getBareName());

		// Test if there are more than one Refines Event clause
		try {
			refinesMachineClause = createRefinesMachineClause(m0, "m1"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating refines machine clause of  m0", //$NON-NLS-1$
					false);
			return;
		}

		assertNotNull("The refines machine clause must not be null ", //$NON-NLS-1$
				refinesMachineClause);

		try {
			abstractMachine = EventBUtils.getAbstractMachine(m0);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract machine of  m0", //$NON-NLS-1$
					false);
			return;
		}

		assertNull("There should be no abstract machine ", abstractMachine); //$NON-NLS-1$

	}

	/**
	 * Tests for {@link EventBUtils#getAbstractEvent(IEvent)}.
	 */
	@Test
	public void testGetAbstractEvent() {
		IMachineFile m0 = null;
		try {
			m0 = createMachine("m0"); //$NON-NLS-1$
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		IEvent m0Event;
		try {
			m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event of  m0", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"The event m0Event should have been created successfully", //$NON-NLS-1$
				m0Event);

		IMachineFile m1 = null;
		try {
			m1 = createMachine("m1"); //$NON-NLS-1$
			m1.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m1", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m1 should be created successfully ", m1); //$NON-NLS-1$

		IEvent m1Event;
		try {
			m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event of  m1", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"The event m1Event should have been created successfully", //$NON-NLS-1$
				m1Event);

		// Test for having no abstract event for m1Event because m1 does not
		// have refines machine clause.
		IEvent abstractEvent;
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of  m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having no abstract event for m1Event because m1Event is
		// non-inherited and having no refines event clause.
		IRefinesMachine refinesMachine = null;
		try {
			refinesMachine = createRefinesMachineClause(m1, "m0"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating refines machine clause of m1", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"The refines machine clause should be created successfully", //$NON-NLS-1$
				refinesMachine);
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of  m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having abstract event since m1Event is inherited and m0Event
		// having the same label as m1Event.
		try {
			m1Event.setInherited(true, new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the inherited attribute of m1Event ", //$NON-NLS-1$
					false);
			return;
		}
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"There should be an abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);
		assertEquals("Incorrect abstract machine ", m0, abstractEvent //$NON-NLS-1$
				.getParent());
		String label;
		try {
			label = abstractEvent.getLabel();
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the label of the abstract event", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect abstract event label (inherited)", "event", //$NON-NLS-1$ //$NON-NLS-2$
				label);

		// Test for having no abstract event because m1Event is inherited and
		// there is no corresponding event in the m0.
		try {
			m0Event.setLabel("abstract_event", new NullProgressMonitor()); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the label of the abstract event", //$NON-NLS-1$
					false);
			return;
		}
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having abstract event because m1Event is non-inherited and
		// having refines event clause point to an existing event in m0.
		try {
			m1Event.setInherited(false, new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the inherited attribute of m1Event ", //$NON-NLS-1$
					false);
			return;
		}
		try {
			createRefinesEventClause(m1Event, "abstract_event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating refines event clause for m1Event ", //$NON-NLS-1$
					false);
			return;
		}
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"There should be an abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);
		assertEquals("Incorrect abstract machine ", m0, abstractEvent //$NON-NLS-1$
				.getParent());

		try {
			label = abstractEvent.getLabel();
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the label of the abstract event", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect abstract event label (non-inherited)", //$NON-NLS-1$
				"abstract_event", label); //$NON-NLS-1$

		// Test for having no abstract event because m1Event is non-inherited
		// and
		// having refines event clause point to an non-existing event in m0.
		try {
			m0Event.setLabel("event", new NullProgressMonitor()); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when setting the label of m0Event ", //$NON-NLS-1$
					false);
			return;
		}
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);

		// Test for having no abstract event because m1 refines a non-existing
		// machine m2.
		try {
			refinesMachine.setAbstractMachineName("m2", //$NON-NLS-1$
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the abstract machine name of the refines clause for m1", //$NON-NLS-1$
					false);
			return;
		}
		try {
			abstractEvent = EventBUtils.getAbstractEvent(m1Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the abstract event of m1Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no abstract event corresponding to m1Event", //$NON-NLS-1$
				abstractEvent);
	}

	/**
	 * Tests for {@link EventBUtils#getNonInheritedAbstractEvent(IEvent)}.
	 */
	@Test
	public void testGetNonInheritedAbstractEvent() {
		IMachineFile m0 = null;
		try {
			m0 = createMachine("m0"); //$NON-NLS-1$
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		IEvent m0Event;
		try {
			m0Event = createEvent(m0, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event of  m0", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"The event m0Event should have been created successfully", //$NON-NLS-1$
				m0Event);

		IMachineFile m1 = null;
		try {
			m1 = createMachine("m1"); //$NON-NLS-1$
			m1.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m1", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m1 should be created successfully ", m1); //$NON-NLS-1$

		IEvent m1Event;
		try {
			m1Event = createEvent(m1, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event of  m1", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"The event m1Event should have been created successfully", //$NON-NLS-1$
				m1Event);

		IMachineFile m2 = null;
		try {
			m2 = createMachine("m2"); //$NON-NLS-1$
			m2.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m2", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m2 should be created successfully ", m2); //$NON-NLS-1$

		IEvent m2Event;
		try {
			m2Event = createEvent(m2, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event of  m2", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"The event m1Event should have been created successfully", //$NON-NLS-1$
				m2Event);

		// Test for having no non-inherited abstract event because m2Event does
		// not have any abstract event.
		IEvent nonInheritedAbstractEvent;
		try {
			nonInheritedAbstractEvent = EventBUtils
					.getNonInheritedAbstractEvent(m2Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the non-inherited abstract event of m2Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having non-inherited abstract event because m2 refines m1
		// and m2Event refines m1Event, and m1Event is non-inherited.
		// Make m2 refines m1.
		try {
			createRefinesMachineClause(m2, "m1"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating the refines machine clause of m2", //$NON-NLS-1$
					false);
			return;
		}

		// m2Event is inherited, i.e m2Event refines m1Event.
		try {
			m2Event.setInherited(true, new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the inherited attribute of m2Event", //$NON-NLS-1$
					false);
			return;
		}
		try {
			nonInheritedAbstractEvent = EventBUtils
					.getNonInheritedAbstractEvent(m2Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the non-inherited abstract event of m2Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNotNull(
				"There should be a non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);
		assertEquals("Incorrect non-inherited abstract", m1Event, //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having no non-inherited abstract event because m2 refines
		// m1
		// and m2Event refines m1Event, and m1Event is inherited but does not
		// correspond to any abstract event.
		try {
			m1Event.setInherited(true, new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the inherited attribute of m1Event", //$NON-NLS-1$
					false);
			return;
		}
		try {
			nonInheritedAbstractEvent = EventBUtils
					.getNonInheritedAbstractEvent(m2Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the non-inherited abstract event of m2Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having no non-inherited abstract event because m2 refines
		// m1
		// and m2Event refines m1Event, and m1Event is inherited, but m1 refines
		// m2 hence m1Event refines m2Event, hence looping.

		// Make m1 refines m2.
		IRefinesMachine m1RefinesMachine;
		try {
			m1RefinesMachine = createRefinesMachineClause(m1, "m2"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when creating the refines machine clause of m1", //$NON-NLS-1$
					false);
			return;
		}
		try {
			nonInheritedAbstractEvent = EventBUtils
					.getNonInheritedAbstractEvent(m2Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the non-inherited abstract event of m2Event", //$NON-NLS-1$
					false);
			return;
		}
		assertNull(
				"There should be no non-inherited abstract event for m2Event", //$NON-NLS-1$
				nonInheritedAbstractEvent);

		// Testing for having non-inherited abstract event because m2 refines m1
		// and m2Event refines m1Event, and m1Event is inherited, and m1 refines
		// m0 hence m1Event refines m0Event.
		try {
			m1RefinesMachine.setAbstractMachineName("m0", //$NON-NLS-1$
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when setting the abstract machine name for refine machine clause of m1", //$NON-NLS-1$
					false);
			return;
		}
		try {
			nonInheritedAbstractEvent = EventBUtils
					.getNonInheritedAbstractEvent(m2Event);
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the non-inherited abstract event of m2Event", //$NON-NLS-1$
					false);
			return;
		}
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
	public void testGetFreeChildName() {
		IMachineFile m0 = null;
		try {
			m0 = createMachine("m0"); //$NON-NLS-1$
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		String childName;
		// Currently, there are no events so the free child name should be "event1". 
		try {
			childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
					"event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect child name", "event1", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Gets the free child name again and it should return event1 still.
		try {
			childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
					"event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect child name", "event1", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Creates "event1".
		try {
			createEvent(m0, "event1", "event1Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}

		// There is "event1" so the free child name now should be "event2".
		try {
			childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
					"event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect child name", "event2", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Creates "event3".
		try {
			createEvent(m0, "event3", "event3Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}

		// There are "event1", "event3" so the free child name now should be "event2"
		try {
			childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
					"event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect child name", "event2", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Gets the free child name again and it should be "event2".
		try {
			childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
					"event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect child name", "event2", childName); //$NON-NLS-1$ //$NON-NLS-2$

		// Create "event2"
		try {
			createEvent(m0, "event2", "event2Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}
		// Gets the free child name and it should be "event4".
		try {
			childName = EventBUtils.getFreeChildName(m0, IEvent.ELEMENT_TYPE,
					"event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect child name", "event4", childName); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test for {@link EventBUtils#getFreeChildNameIndex(org.rodinp.core.IInternalParent, org.rodinp.core.IInternalElementType, String)}.
	 */
	@Test
	public void testGetFreeChildNameIndex() {
		IMachineFile m0 = null;
		try {
			m0 = createMachine("m0"); //$NON-NLS-1$
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		int freeIndex;
		// There are no events, so the free index now should be 1.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 1, freeIndex); //$NON-NLS-1$

		// Do it again and should be 1 still.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 1, freeIndex); //$NON-NLS-1$

		// Create "event1"
		try {
			createEvent(m0, "event1", "event1Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}

		// There is "event1" so the free index now should be 2.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 2, freeIndex); //$NON-NLS-1$

		// Gets the free index again and it should be 2 still.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 2, freeIndex); //$NON-NLS-1$

		// Create "event3"
		try {
			createEvent(m0, "event3", "event3Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}
		
		// There are now "event1", "event3" so the free index should be 2.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event"); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 2, freeIndex); //$NON-NLS-1$

	}

	/**
	 * Tests for {@link EventBUtils#getFreeChildNameIndex(org.rodinp.core.IInternalParent, org.rodinp.core.IInternalElementType, String, int)}.
	 */
	@Test
	public void testGetFreeChildNameIndexWithBeginIndex() {
		IMachineFile m0 = null;
		try {
			m0 = createMachine("m0"); //$NON-NLS-1$
			m0.save(new NullProgressMonitor(), true);
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating machine m0", false); //$NON-NLS-1$
			return;
		}
		assertNotNull("m0 should be created successfully ", m0); //$NON-NLS-1$

		int freeIndex;
		// There are no events so the free index starting from 1 is 1.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 1); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 1, freeIndex); //$NON-NLS-1$

		// There are no events so the free index starting from 3 is 3.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 3); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 3, freeIndex); //$NON-NLS-1$

		// Create "event1".
		try {
			createEvent(m0, "event1", "event1Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}
		// There is "event1" so the free index starting from 2 is 2.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 2); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 2, freeIndex); //$NON-NLS-1$

		// There is "event1" so the free index starting from 3 is 3.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 3); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 3, freeIndex); //$NON-NLS-1$

		// Create "event3"
		try {
			createEvent(m0, "event3", "event3Label"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RodinDBException e) {
			assertTrue("Problem occured when creating a new event for m0", //$NON-NLS-1$
					false);
			return;
		}
		// There are "event1" and "event3" so the free index starting from 2 is 2.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 2); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 2, freeIndex); //$NON-NLS-1$

		// There are "event1" and "event3" so the free index starting from 3 is 4.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 3); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 4, freeIndex); //$NON-NLS-1$

		// There are "event1" and "event3" so the free index starting from 5 is 5.
		try {
			freeIndex = EventBUtils.getFreeChildNameIndex(m0,
					IEvent.ELEMENT_TYPE, "event", 5); //$NON-NLS-1$
		} catch (RodinDBException e) {
			assertTrue(
					"Problem occured when getting the free child name index for a new event in machine m0", //$NON-NLS-1$
					false);
			return;
		}
		assertEquals("Incorrect free child name index ", 5, freeIndex); //$NON-NLS-1$
	}

}
