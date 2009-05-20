/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.operation.tests.utils;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.TestCase;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;

public class TestElement extends TestCase {

	protected static void assertChildren(Element element, Element... expected) {
		assertEquals(Arrays.asList(expected), element.getChildren());
	}

	protected static void assertAttributes(Element element,
			IAttributeValue... expected) {
		HashSet<IAttributeValue> expSet = new HashSet<IAttributeValue>(Arrays
				.asList(expected));
		assertEquals(expSet, element.getAttributes());
	}

	@Test
	public void testAddChidren() throws Exception {
		Element parent = new Element(IAxiom.ELEMENT_TYPE);
		Element e2 = new Element(IAction.ELEMENT_TYPE);
		Element e3 = new Element(IEvent.ELEMENT_TYPE);
		Element e1 = new Element(IInvariant.ELEMENT_TYPE);

		parent.addChild(e2, null);
		assertChildren(parent, e2);

		parent.addChild(e3, null);
		assertChildren(parent, e2, e3);

		parent.addChild(e1, e2);
		assertChildren(parent, e1, e2, e3);
	}

	private void addStringAttribute(Element element,
			IAttributeType.String attribute, String value) {
		element.addAttribute(attribute.makeValue(value));
	}

	private Element addEvent(Element parent, String label) {
		final Element event = new Element(IEvent.ELEMENT_TYPE);
		addStringAttribute(event, LABEL_ATTRIBUTE, "event");
		parent.addChild(event, null);
		return event;
	}

	private Element addAction(Element parent, String label, String assignment) {
		final Element action = new Element(IAction.ELEMENT_TYPE);
		addStringAttribute(action, LABEL_ATTRIBUTE, label);
		addStringAttribute(action, ASSIGNMENT_ATTRIBUTE, assignment);
		parent.addChild(action, null);
		return action;
	}

	private Element addAction(Element parent, String label) {
		final Element action = new Element(IAction.ELEMENT_TYPE);
		addStringAttribute(action, LABEL_ATTRIBUTE, label);
		parent.addChild(action, null);
		return action;
	}

	private Element addSeesContext(Element parent) {
		final Element sees = new Element(ISeesContext.ELEMENT_TYPE);
		parent.addChild(sees, null);
		return sees;
	}

	private void assertNotEquals(Element expected, Element actual) {
		final String message = "element :\n " + expected.toString()
				+ "\nand element :\n" + actual.toString() + "\nshould differ.";
		if (expected == null && actual == null)
			fail(message);
		if (expected != null && expected.equals(actual))
			fail(message);
	}

	@Test
	public void testEquals() throws Exception {
		final Element mch1 = new Element(IMachineRoot.ELEMENT_TYPE);
		addSeesContext(mch1);
		final Element event1 = addEvent(mch1, "event");
		addAction(event1, "action", "assignment");

		final Element mch2 = new Element(IMachineRoot.ELEMENT_TYPE);
		addSeesContext(mch2);
		final Element event2 = addEvent(mch2, "event");
		addAction(event2, "action", "assignment");

		final Element mch3 = new Element(IMachineRoot.ELEMENT_TYPE);
		addSeesContext(mch3);
		final Element event3 = addEvent(mch3, "event");
		addAction(event3, "action");

		assertEquals(mch1, mch1);
		assertEquals(mch1, mch2);
		assertEquals(mch2, mch1);

		assertNotEquals(mch1, mch3);
		assertNotEquals(mch3, mch1);
		assertNotEquals(mch2, mch3);
		assertNotEquals(mch3, mch2);
	}

	@Test
	public void testAddAttribute() throws Exception {
		Element e1 = new Element(IAxiom.ELEMENT_TYPE);
		IAttributeValue att1 = LABEL_ATTRIBUTE.makeValue("myLabel");
		e1.addAttribute(att1);
		assertAttributes(e1, att1);
	}

}
