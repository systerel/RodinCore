/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - test free index for label or identifier of event children
 *******************************************************************************/

package org.eventb.ui.tests;

import java.math.BigInteger;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.tests.utils.EventBUITest;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Tests on UI utils
 * 
 * @author Nicolas Beauger
 * 
 */

public class TestUIUtils extends EventBUITest {

	private static final String eventNamePrefix = "event";
	private static final String eventLabelPrefix = "evt";
	private static final String axiomNamePrefix = "axiom";
	private static final String axiomLabelPrefix = "axm";
	private static final String constantNamePrefix = "constant";
	private static final String constantIdentifierPrefix = "cst";
	private static final String guardIdentifierPrefix = "grd";
	private static final String parameterIdentifierPrefix = "prm";
	private static final String actionIdentifierPrefix = "act";

	protected static IMachineRoot m0;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		m0 = createMachine("m0");
		m0.getRodinFile().save(null, true);
		assertNotNull("m0 should be created successfully ", m0);
	}

	static void assertFreeIndex(IInternalElementType<?> type,
			IAttributeType.String attributeType, String prefix,
			String freeIndexExpected) throws RodinDBException,
			IllegalStateException {

		final String freeIndexActual = UIUtils.getFreePrefixIndex(m0, type,
				attributeType, prefix);

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

	public void testGetFreeIndexNameFirst() throws RodinDBException {
		// Currently, there are no elements, so the free index for a name
		// should be 1
		assertFreeIndex(IEvent.ELEMENT_TYPE, null, eventNamePrefix, "1");
	}

	public void testGetFreeIndexNameSecond() throws RodinDBException {
		// Create 1 event with internal name "event1" and label "evt1".
		createNEvents(m0, eventNamePrefix, eventLabelPrefix, 1, 1);
		// There is "event1" so the free index starting from 1 is 2.
		assertFreeIndex(IEvent.ELEMENT_TYPE, null, eventNamePrefix, "2");
	}

	public void testGetFreeIndexNameThird() throws RodinDBException {
		createNEvents(m0, eventNamePrefix, eventLabelPrefix, 2, 1);
		// There are "event1" and "event2" so the free index
		// starting from 1 is 3.
		assertFreeIndex(IEvent.ELEMENT_TYPE, null, eventNamePrefix, "3");
	}

	public void testGetFreeIndexNameWithHoles() throws RodinDBException {
		// create event with internal name "event314" and label "evt314"
		createNEvents(m0, eventNamePrefix, eventLabelPrefix, 1, 314);

		// highest name index is 314 so the free index should now be 315.
		assertFreeIndex(IEvent.ELEMENT_TYPE, null, eventNamePrefix, "315");

	}

	public void testGetFreeIndexAttributeFirst() throws RodinDBException {
		// no axiom has been created yet so it should return index 1
		assertFreeIndex(IAxiom.ELEMENT_TYPE, EventBAttributes.LABEL_ATTRIBUTE,
				axiomLabelPrefix, "1");
	}

	public void testGetFreeIndexAttributeDifferentType()
			throws RodinDBException {
		// create events with a label attribute
		createNEvents(m0, eventNamePrefix, eventLabelPrefix, 10, 314);

		// as axioms are of a different type, the research for axioms
		// with the same label prefix should return index 1
		assertFreeIndex(IAxiom.ELEMENT_TYPE, EventBAttributes.LABEL_ATTRIBUTE,
				eventLabelPrefix, "1");

	}

	public void testGetFreeIndexAttributeSecond() throws RodinDBException {
		createNAxioms(m0, axiomNamePrefix, axiomLabelPrefix, 1, 1);

		// an axiom has been created with label index 1
		// so next available index should be 2
		assertFreeIndex(IAxiom.ELEMENT_TYPE, EventBAttributes.LABEL_ATTRIBUTE,
				axiomLabelPrefix, "2");
	}

	public void testGetFreeIndexAttributeManyExisting() throws RodinDBException {
		createNAxioms(m0, axiomNamePrefix, axiomLabelPrefix, 100, 31);

		// many axioms have been created up to label index 130
		// so next available index should be 131
		assertFreeIndex(IAxiom.ELEMENT_TYPE, EventBAttributes.LABEL_ATTRIBUTE,
				axiomLabelPrefix, "131");
	}

	
	
	public void testGetFreeIndexLabelParameterFirst() throws Exception {
		helpTestGetFreeIndexNameEventExtendedFirst(IParameter.ELEMENT_TYPE,
				EventBAttributes.IDENTIFIER_ATTRIBUTE,
				parameterIdentifierPrefix);
	}

	public void testGetFreeIndexLabelGuardFirst() throws Exception {
		helpTestGetFreeIndexNameEventExtendedFirst(IGuard.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, guardIdentifierPrefix);
	}

	public void testGetFreeIndexLabelActionFirst() throws Exception {
		helpTestGetFreeIndexNameEventExtendedFirst(IAction.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, actionIdentifierPrefix);
	}

	public void testGetFreeIndexLabelParameterSecond() throws Exception {
		helpTestGetFreeIndexNameEventExtendedSecond(IParameter.ELEMENT_TYPE,
				EventBAttributes.IDENTIFIER_ATTRIBUTE,
				parameterIdentifierPrefix);
	}

	public void testGetFreeIndexLabelGuardSecond() throws Exception {
		helpTestGetFreeIndexNameEventExtendedSecond(IGuard.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, guardIdentifierPrefix);
	}

	public void testGetFreeIndexLabelActionSecond() throws Exception {
		helpTestGetFreeIndexNameEventExtendedSecond(IAction.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, actionIdentifierPrefix);
	}

	public void testGetFreeIndexLabelParameterManyExisting() throws Exception {
		helpTestGetFreeIndexNameEventExtendedManyExisting(
				IParameter.ELEMENT_TYPE, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				parameterIdentifierPrefix);
	}

	public void testGetFreeIndexLabelGuardManyExisting() throws Exception {
		helpTestGetFreeIndexNameEventExtendedManyExisting(IGuard.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, guardIdentifierPrefix);
	}

	public void testGetFreeIndexLabelActionManyExisting() throws Exception {
		helpTestGetFreeIndexNameEventExtendedManyExisting(IAction.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, actionIdentifierPrefix);
	}

	private IMachineRoot createRefinedMachine() throws Exception {
		final IMachineRoot m1 = createMachine("m1");
		m1.getRodinFile().save(null, true);
		m1.setAttributeValue(EventBAttributes.EXTENDED_ATTRIBUTE, true, null);

		AtomicOperation op = OperationFactory.createElement(openEditor(m1),
				IRefinesMachine.ELEMENT_TYPE,
				EventBAttributes.TARGET_ATTRIBUTE, m0.getComponentName());
		op.execute(null, null);
		return m1;
	}

	private IEvent createExtendedEvent(IMachineRoot parent, IEvent event,
			String eventLabel) throws Exception {
		final IEventBEditor<?> editor = openEditor(parent);
		final IEvent e2 = createEvent(parent, eventNamePrefix + 1, eventLabel);
		UIUtils.setBooleanAttribute(e2, EventBAttributes.EXTENDED_ATTRIBUTE,
				false, null);
		final AtomicOperation op = OperationFactory.createElementGeneric(
				editor, e2, IRefinesEvent.ELEMENT_TYPE, null);
		op.execute(null, null);
		((IRefinesEvent) op.getCreatedElement()).setAbstractEventLabel(event
				.getLabel(), null);
		e2.setExtended(true, null);
		return e2;
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

	private void helpTestGetFreeIndexNameEventExtendedFirst(
			IInternalElementType<?> type, IAttributeType.String attributesType,
			String labelPrefix) throws Exception {
		final IMachineRoot m1 = createRefinedMachine();
		final IEvent e1 = createEvent(m0, eventNamePrefix + 1, eventLabelPrefix);
		final IEvent e2 = createExtendedEvent(m1, e1, eventLabelPrefix);
		final String freeIndexActual = UIUtils.getFreePrefixIndex(e2, type,
				attributesType, labelPrefix);

		// no element has been created in e1 yet so it should return index 1
		assertEquals("incorrect free element label index", "1", freeIndexActual);
	}

	private void helpTestGetFreeIndexNameEventExtendedSecond(
			IInternalElementType<?> type, IAttributeType.String attributesType,
			String labelPrefix) throws Exception {
		final IMachineRoot m1 = createRefinedMachine();
		final IEvent e1 = createEvent(m0, eventNamePrefix + 1, eventLabelPrefix);
		final IEvent e2 = createExtendedEvent(m1, e1, eventLabelPrefix);
		createChildrenOfType(e1, type, labelPrefix + 1);
		final String freeIndexActual = UIUtils.getFreePrefixIndex(e2, type,
				attributesType, labelPrefix);
		// one element has been created in e1 so it should return index 2
		assertEquals("incorrect free element label index", "2", freeIndexActual);
	}

	private void helpTestGetFreeIndexNameEventExtendedManyExisting(
			IInternalElementType<?> type, IAttributeType.String attributesType,
			String labelPrefix) throws Exception {
		final IMachineRoot m1 = createRefinedMachine();
		final IEvent e1 = createEvent(m0, eventNamePrefix + 1, eventLabelPrefix);
		final IEvent e2 = createExtendedEvent(m1, e1, eventLabelPrefix);
		createNChildrenOfType(e1, type, labelPrefix, 130, 1);
		final String freeIndexActual = UIUtils.getFreePrefixIndex(e2, type,
				attributesType, labelPrefix);
		// 130 element has been created in e1 beginning at index 1 so it should
		// return index 131
		assertEquals("incorrect free element label index", "131",
				freeIndexActual);
	}

	public void testGetFreeIndexAttributeDifferentAttribute()
			throws RodinDBException {
		createNAxioms(m0, axiomNamePrefix, axiomLabelPrefix, 100, 31);

		// many axioms have been created up to label index 130
		// but no predicate attribute has been set
		// so next available predicate index
		assertFreeIndex(IAxiom.ELEMENT_TYPE,
				EventBAttributes.PREDICATE_ATTRIBUTE, "this axiom is false",
				"1");
	}

	public void testConstantIdentifier() throws Exception {
		final IConstant cst = createInternalElement(m0, IConstant.ELEMENT_TYPE,
				constantNamePrefix);
		cst.setIdentifierString(constantIdentifierPrefix + "1", null);
		assertFreeIndex(IConstant.ELEMENT_TYPE,
				EventBAttributes.IDENTIFIER_ATTRIBUTE,
				constantIdentifierPrefix, "2");
	}

	private void doBigIndex(String idx) throws Exception {
		createInternalElement(m0, IConstant.ELEMENT_TYPE, constantNamePrefix
				+ idx);
		final BigInteger expected = new BigInteger(idx).add(BigInteger.ONE);
		assertFreeIndex(IConstant.ELEMENT_TYPE, null, constantNamePrefix,
				expected.toString());
	}

	public void testMaxInt() throws Exception {
		doBigIndex(Integer.toString(Integer.MAX_VALUE));
	}

	public void testMaxLong() throws Exception {
		doBigIndex(Long.toString(Long.MAX_VALUE));
	}

	public void testBig() throws Exception {
		doBigIndex("3141592653");
	}

	public void testVeryBig() throws Exception {
		doBigIndex("314159265358979323846264338327950288419"
				+ "716939937510582097494459230781640628620"
				+ "89986280348253421170679821480865132823");
	}

	// CALLING THE CALLING METHODS //
	public void testGetFreeIndexCallingMethods() throws RodinDBException {
		String freeIndexFound;
		EditPage editPage = new EditPage();
		EventBContextEditor editor = (EventBContextEditor) editPage.getEditor();

		freeIndexFound = UIUtils.getFreeElementLabelIndex(editor, m0,
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
	public void testRegexPrefix() throws Exception {
		createInternalElement(m0, IConstant.ELEMENT_TYPE, "cst+1");
		assertFreeIndex(IConstant.ELEMENT_TYPE, null, "cst+", "2");
	}

	/**
	 * Ensures that the whole prefix of existing elements is taken into account
	 * (no partial match).
	 */
	public void testLongerPrefix() throws Exception {
		createInternalElement(m0, IConstant.ELEMENT_TYPE, "foo_cst1");
		assertFreeIndex(IConstant.ELEMENT_TYPE, null, "cst", "1");
	}

	/**
	 * Ensures that the whole suffix of existing elements is taken into account
	 * (no partial match).
	 */
	public void testLongerSuffix() throws Exception {
		createInternalElement(m0, IConstant.ELEMENT_TYPE, "cst1a");
		assertFreeIndex(IConstant.ELEMENT_TYPE, null, "cst", "1");
	}

	/**
	 * Ensures that the case where an attribute doesn't exist in the database is
	 * correctly handled (the element is ignored and no exception is thrown).
	 */
	public void testInexistentLabel() throws Exception {
		createInternalElement(m0, IConstant.ELEMENT_TYPE, "cst1");
		assertFreeIndex(IConstant.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE, "constant", "1");
	}
}
