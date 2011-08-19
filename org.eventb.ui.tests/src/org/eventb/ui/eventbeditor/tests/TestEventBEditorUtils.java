/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.tests;

import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.getChildTowards;

import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.ui.tests.utils.EventBUITest;
import org.rodinp.core.IRodinElement;

public class TestEventBEditorUtils extends EventBUITest {

	private static void assertChildTowards(IRodinElement expected,
			IRodinElement ancestor, IRodinElement target) {
		final IRodinElement actual = getChildTowards(ancestor, target);
		assertEquals(expected, actual);
	}

	/**
	 * Ensures that <code>getChildTowards()</code> returns the second element
	 * when the first element is a direct parent of it.
	 */
	public void testChildTowardsParent() {
		final IMachineRoot root = getMachineRoot("mch");
		final IEvent evt = root.getEvent("event");
		assertChildTowards(evt, root, evt);
	}

	/**
	 * Ensures that <code>getChildTowards()</code> returns the parent of the
	 * second element when the first element is a grand-parent of the second
	 * element.
	 */
	public void testChildTowardsAncestor() {
		final IMachineRoot root = getMachineRoot("mch");
		final IEvent evt = root.getEvent("event");
		final IParameter prm = evt.getParameter("prm");
		assertChildTowards(evt, root, prm);
	}

	/**
	 * Ensures that <code>getChildTowards()</code> returns <code>null</code>
	 * when passed twice the same element.
	 */
	public void testChildTowardsSame() {
		final IMachineRoot root = getMachineRoot("mch");
		assertChildTowards(null, root, root);
	}

	/**
	 * Ensures that <code>getChildTowards()</code> returns <code>null</code>
	 * when there is no ancestry relationship between its two arguments.
	 */
	public void testChildTowardsNotAncestor() {
		final IMachineRoot root1 = getMachineRoot("mch1");
		final IMachineRoot root2 = getMachineRoot("mch2");
		final IEvent evt2 = root2.getEvent("event");
		final IParameter prm2 = evt2.getParameter("prm");
		assertChildTowards(null, root1, root2);
		assertChildTowards(null, root1, evt2);
		assertChildTowards(null, root1, prm2);
	}

	/**
	 * Ensures that <code>getChildTowards()</code> returns <code>null</code>
	 * when the second element is an ancestor of the first element.
	 */
	public void testChildTowardsDescendant() {
		final IMachineRoot root = getMachineRoot("mch");
		final IEvent evt = root.getEvent("event");
		final IParameter prm = evt.getParameter("prm");
		assertChildTowards(null, evt, root);
		assertChildTowards(null, prm, root);
	}

}
