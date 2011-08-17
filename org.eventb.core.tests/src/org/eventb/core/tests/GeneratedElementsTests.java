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
package org.eventb.core.tests;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.basis.EventBElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class GeneratedElementsTests extends EventBTest {

	private static final String[] EMPTY_STRING = new String[0];

	private void assertGenerated(IInternalElement element) throws RodinDBException {
		EventBElement evbElem = (EventBElement) element;
		assertTrue("Expected generated", evbElem.isGenerated());
	}

	// verifies that:
	// - hasGenerated() always returns true
	// - set() and is() methods work properly
	public void testHasSetIsGenerated() throws Exception {
		final IContextRoot ctx = createContext("generated");
		assertTrue("hasGenerated() should always be true", ctx.hasGenerated());
		assertFalse("Expected not generated", ctx.isGenerated());
		ctx.setGenerated(true, null);
		assertTrue("Expected generated", ctx.isGenerated());
	}

	// verifies that an element is generated if it has a generated ancestor
	public void testIsGeneratedRecursive() throws Exception {
		final IMachineRoot mch = createMachine("generated");
		mch.setGenerated(true, null);

		final String evtName = "evt";
		final String grdName = "axm";
		this.addEvent(mch, evtName, EMPTY_STRING, makeSList(grdName), makeSList("⊤"), EMPTY_STRING, EMPTY_STRING);
		final IEvent evt = mch.getEvent(evtName);
		evt.create(null, null);
		final IGuard grd = evt.getGuard(grdName);
		grd.create(null, null);

		assertGenerated(evt);
		assertGenerated(grd);
	}
}
