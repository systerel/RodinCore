/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.tests;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Tests on UI utils
 * 
 * @author Nicolas Beauger
 * @author Aurélien Gilles
 */

public class TestUIUtils extends EventBUITest {

	private static final String eventLabelPrefix = "evt";
	private static final String axiomLabelPrefix = "axm";
	private static final String constantIdentifierPrefix = "cst";
	private static final String guardLabelPrefix = "grd";

	protected static IMachineRoot m0;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		m0 = createMachine("m0");
		m0.getRodinFile().save(null, true);
		assertNotNull("m0 should have been created successfully ", m0);
	}

	private static void assertFreeIndex(IInternalElement element,
			IInternalElementType<?> type, IAttributeType.String attributeType,
			String prefix, String freeIndexExpected) throws RodinDBException,
			IllegalStateException {

		final String freeIndexActual = UIUtils.getFreePrefixIndex(element,
				type, attributeType, prefix);

		if (freeIndexActual != null) {
			final String kindOfIndex;
			if (attributeType == null) {
				kindOfIndex = "child name";
			} else {
				kindOfIndex = "attribute";
			}
			assertEquals("Incorrect free " + kindOfIndex + " index ",
					freeIndexExpected, freeIndexActual);
		}
	}

	private IEvent createRefiningEvent() throws RodinDBException {
		final IEvent e1 = createEvent(m0, eventLabelPrefix);

		final IMachineRoot m1 = createMachine("m1");
		createRefinesMachineClause(m1, m0.getElementName());
		final IEvent e2 = createEvent(m1, e1.getLabel());
		createRefinesEventClause(e2, e1.getLabel());
		return e2;
	}

	@Test
	public void testGetFreeIndexNameFirst() throws RodinDBException {
		// Currently, there are no elements, so the free index for a name
		// should be 1
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, eventLabelPrefix, "1");
	}

	@Test
	public void testGetFreeIndexNameSecond() throws RodinDBException {
		// Create 1 event with label "evt1".
		createNEvents(m0, eventLabelPrefix, 1, 1);
		// There is "evt1" so the free index starting from 1 is 2.
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, eventLabelPrefix, "2");
	}

	@Test
	public void testGetFreeIndexNameThird() throws RodinDBException {
		createNEvents(m0, eventLabelPrefix, 2, 1);
		// There are "event1" and "event2" so the free index
		// starting from 1 is 3.
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, eventLabelPrefix, "3");
	}

	@Test
	public void testGetFreeIndexNameWithHoles() throws RodinDBException {
		// create event with label "evt314"
		createNEvents(m0, eventLabelPrefix, 1, 314);

		// highest label index is 314 so the free index should now be 315.
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, eventLabelPrefix, "315");

	}

	@Test
	public void testGetFreeIndexAttributeFirst() throws RodinDBException {
		// no axiom has been created yet so it should return index 1
		assertFreeIndex(m0, IAxiom.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				axiomLabelPrefix, "1");
	}

	@Test
	public void testGetFreeIndexAttributeDifferentType()
			throws RodinDBException {
		// create events with a label attribute
		createNEvents(m0, eventLabelPrefix, 10, 314);

		// as axioms are of a different type, the research for axioms
		// with the same label prefix should return index 1
		assertFreeIndex(m0, IAxiom.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				eventLabelPrefix, "1");

	}

	@Test
	public void testGetFreeIndexAttributeSecond() throws RodinDBException {
		createNAxioms(m0, axiomLabelPrefix, 1, 1);

		// an axiom has been created with label index 1
		// so next available index should be 2
		assertFreeIndex(m0, IAxiom.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				axiomLabelPrefix, "2");
	}

	@Test
	public void testGetFreeIndexAttributeManyExisting() throws RodinDBException {
		createNAxioms(m0, axiomLabelPrefix, 100, 31);

		// many axioms have been created up to label index 130
		// so next available index should be 131
		assertFreeIndex(m0, IAxiom.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				axiomLabelPrefix, "131");
	}

	/**
	 * Ensures that abstract guards are taken into account when computing a free
	 * label for a guard in an extended concrete event. ( bug ID 2142052 fixed )
	 */
	@Test
	public void testGetFreeIndexLabelGuardExtended() throws Exception {
		final IEvent con = createRefiningEvent();
		con.setExtended(true, null);

		final IEvent abs = EventBUtils.getAbstractEvent(con);
		createGuard(abs, guardLabelPrefix + 1, "");

		assertFreeIndex(con, IGuard.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				guardLabelPrefix, "2");
	}

	/**
	 * Ensures that abstract guards are ignored when computing a free label for
	 * a guard in a non-extended concrete event.
	 */
	@Test
	public void testGetFreeIndexLabelGuardNotExtended() throws Exception {
		final IEvent con = createRefiningEvent();
		con.setExtended(false, null);

		final IEvent abs = EventBUtils.getAbstractEvent(con);
		createGuard(abs, guardLabelPrefix + 1, "");

		assertFreeIndex(con, IGuard.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				guardLabelPrefix, "1");
	}

	private IInternalElement createChildrenOfType(IEvent parent,
			IInternalElementType<?> type, String label) throws RodinDBException {
		if (type == IParameter.ELEMENT_TYPE)
			return createParameter(parent, label);
		if (type == IGuard.ELEMENT_TYPE)
			return createGuard(parent, label, "toto");
		if (type == IAction.ELEMENT_TYPE)
			return createAction(parent, label, "toto");
		fail();
		return null;
	}

	protected void createNChildrenOfType(IEvent event,
			IInternalElementType<?> type, String labelPrefix, long n,
			long beginIndex) throws RodinDBException {

		for (long i = beginIndex; i < beginIndex + n; i++) {
			createChildrenOfType(event, type, labelPrefix + i);
		}
	}

	@Test
	public void testGetFreeIndexAttributeDifferentAttribute()
			throws RodinDBException {
		createNAxioms(m0, axiomLabelPrefix, 100, 31);

		// many axioms have been created up to label index 130
		// but no predicate attribute has been set
		// so next available predicate index
		assertFreeIndex(m0, IAxiom.ELEMENT_TYPE, PREDICATE_ATTRIBUTE,
				"this axiom is false", "1");
	}

	@Test
	public void testConstantIdentifier() throws Exception {
		final IConstant cst = createInternalElement(m0, IConstant.ELEMENT_TYPE);
		cst.setIdentifierString(constantIdentifierPrefix + "1", null);
		assertFreeIndex(m0, IConstant.ELEMENT_TYPE, IDENTIFIER_ATTRIBUTE,
				constantIdentifierPrefix, "2");
	}

	private void doBigIndex(String idx) throws Exception {
		createEvent(m0, eventLabelPrefix + idx);
		final BigInteger expected = new BigInteger(idx).add(BigInteger.ONE);
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				eventLabelPrefix, expected.toString());
	}

	@Test
	public void testMaxInt() throws Exception {
		doBigIndex(Integer.toString(Integer.MAX_VALUE));
	}

	@Test
	public void testMaxLong() throws Exception {
		doBigIndex(Long.toString(Long.MAX_VALUE));
	}

	@Test
	public void testVeryBig() throws Exception {
		doBigIndex("314159265358979323846264338327950288419"
				+ "716939937510582097494459230781640628620"
				+ "89986280348253421170679821480865132823");
	}

	// CALLING THE CALLING METHODS //
	@Test
	public void testGetFreeIndexCallingMethods() throws RodinDBException {
		String freeIndexFound;

		freeIndexFound = UIUtils.getFreeElementLabelIndex(m0,
				IAxiom.ELEMENT_TYPE, axiomLabelPrefix);
		assertEquals("incorrect free element label index", "1", freeIndexFound);

		freeIndexFound = UIUtils.getFreeElementIdentifierIndex(m0,
				ICarrierSet.ELEMENT_TYPE, "set");
		assertEquals("incorrect free element identifier index", "1",
				freeIndexFound);

		freeIndexFound = EventBUtils.getFreeChildNameIndex(m0,
				ICarrierSet.ELEMENT_TYPE, "internal_element");
		assertEquals("incorrect free element identifier index", "1",
				freeIndexFound);
	}

	/**
	 * Ensures that the given prefix can look like a regular expression, in
	 * which case meta-characters are ignored.
	 */
	@Test
	public void testRegexPrefix() throws Exception {
		createEvent(m0, "cst+1");
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, "cst+", "2");
	}

	/**
	 * Ensures that the whole prefix of existing elements is taken into account
	 * (no partial match).
	 */
	@Test
	public void testLongerPrefix() throws Exception {
		createEvent(m0, "foo_cst1");
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, "cst", "1");
	}

	/**
	 * Ensures that the whole suffix of existing elements is taken into account
	 * (no partial match).
	 */
	@Test
	public void testLongerSuffix() throws Exception {
		createEvent(m0, "cst1a");
		assertFreeIndex(m0, IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, "cst", "1");
	}

	/**
	 * Ensures that the case where an attribute doesn't exist in the database is
	 * correctly handled (the element is ignored and no exception is thrown).
	 */
	@Test
	public void testInexistentLabel() throws Exception {
		createInternalElement(m0, IConstant.ELEMENT_TYPE);
		assertFreeIndex(m0, IConstant.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				"constant", "1");
	}
}
