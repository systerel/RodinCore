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

import junit.framework.TestCase;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.junit.Test;
import org.rodinp.core.IAttributeType;

public class TestElement extends TestCase {

	@Test
	public void testAddChidren() throws Exception {
		Element e1 = new Element(IAxiom.ELEMENT_TYPE);
		Element e2 = new Element(IAxiom.ELEMENT_TYPE);
		Element e3 = new Element(IAxiom.ELEMENT_TYPE);
		e1.addChildren(e2);
		assertTrue(e1.getChildren().contains(e2));
		assertEquals(e1.getChildren().size(),1);

		e1.addChildren(e3);
		assertTrue(e1.getChildren().contains(e3));
		assertEquals(e1.getChildren().size(),2);

	}

	@Test
	public void testEqualsAttribute() throws Exception{
		Attribute<IAttributeType.String,String> att1 = new Attribute<IAttributeType.String,String>(EventBAttributes.LABEL_ATTRIBUTE, "monLabel");
		assertTrue(att1.equals(att1));
	}
	
	@Test
	public void testAddAttribute() throws Exception{
		Element e1 = new Element(IAxiom.ELEMENT_TYPE);
		Attribute<IAttributeType.String,String> att1 = new Attribute<IAttributeType.String,String>(EventBAttributes.LABEL_ATTRIBUTE, "monLabel");
		e1.addAttribute(att1);
		assertTrue(e1.getAttributes().contains(att1));
		assertEquals(e1.getAttributes().size(),1);
	}
	
}
