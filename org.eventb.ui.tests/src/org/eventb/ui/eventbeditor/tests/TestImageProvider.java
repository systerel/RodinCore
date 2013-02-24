/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;
import org.rodinp.core.IInternalElement;

public class TestImageProvider extends EventBUITest {

	/**
	 * Ensures that the image with the given name is provided for the specified
	 * element.
	 * */
	private void assertImage(String message, String name,
			IInternalElement element) {
		final ImageDescriptor obtained = EventBImage.getImageDescriptor(element);
		final ImageDescriptor expected = EventBImage.getImageDescriptor(name);
		assertEquals("Unexpected image descriptor", expected, obtained);
	}

	/**
	 * Returns a handle to an event.
	 */
	private IEvent getEvent() {
		final IMachineRoot mch = getMachineRoot("mch");
		return mch.getEvent("evt");
	}

	/**
	 * Checks the image for a context.
	 */
	@Test
	public void testImageContextRoot() throws Exception {
		final IContextRoot ctx = getContextRoot("ctx");
		assertImage("Unexpected image descriptor for context root",
				"icons/full/obj16/ctx_obj.gif", ctx);
	}

	/**
	 * Checks the image for a machine.
	 */
	@Test
	public void testImageMachineRoot() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		assertImage("Unexpected image descriptor for machine root",
				"icons/full/obj16/mch_obj.gif", mch);
	}

	/**
	 * Checks the image for an action.
	 */
	@Test
	public void testImageAction() throws Exception {
		final IEvent evt = getEvent();
		final IAction act = evt.getAction("act");
		assertImage("Unexpected image descriptor for action",
				IEventBSharedImages.IMG_ACTION_PATH, act);
	}

	/**
	 * Checks the image for an axiom.
	 */
	@Test
	public void testImageAxiom() throws Exception {
		final IContextRoot ctx = createContext("ctx");
		final IAxiom axm = createAxiom(ctx, "axm", "TRUE", false);
		assertImage("Unexpected image descriptor for axiom",
				IEventBSharedImages.IMG_AXIOM_PATH, axm);

		axm.setTheorem(true, null);
		assertImage(
				"Unexpected image descriptor for axiom with theorem attribute",
				IEventBSharedImages.IMG_THEOREM_PATH, axm);
	}

	/**
	 * Checks the image for a carrier set.
	 */
	@Test
	public void testImageCarrierSet() throws Exception {
		final IContextRoot ctx = createContext("ctx");
		final ICarrierSet set = ctx.getCarrierSet("set");
		assertImage("Unexpected image descriptor for carrier set",
				IEventBSharedImages.IMG_CARRIER_SET_PATH, set);
	}

	/**
	 * Checks the image for a constant.
	 */
	@Test
	public void testImageConstant() throws Exception {
		final IContextRoot ctx = createContext("ctx");
		final IConstant cst = ctx.getConstant("cst");
		assertImage("Unexpected image descriptor for constant",
				IEventBSharedImages.IMG_CONSTANT_PATH, cst);
	}

	/**
	 * Checks the image for an event.
	 */
	@Test
	public void testImageEvent() throws Exception {
		final IEvent evt = getEvent();
		assertImage("Unexpected image descriptor for event",
				IEventBSharedImages.IMG_EVENT_PATH, evt);
	}

	/**
	 * Checks the image for a guard.
	 */
	@Test
	public void testImageGuard() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		final IEvent evt = createEvent(mch, "evt");
		final IGuard grd = createGuard(evt, "grd", "TRUE");
		assertImage("Unexpected image descriptor for guard",
				IEventBSharedImages.IMG_GUARD_PATH, grd);

		grd.setTheorem(true, null);
		assertImage(
				"Unexpected image descriptor for guard with theorem attribute",
				IEventBSharedImages.IMG_THEOREM_PATH, grd);
	}

	/**
	 * Checks the image for an invariant.
	 */
	@Test
	public void testImageInvariant() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		final IInvariant inv = createInvariant(mch, "inv", "TRUE", false);
		assertImage("Unexpected image descriptor for invariant",
				IEventBSharedImages.IMG_INVARIANT_PATH, inv);

		inv.setTheorem(true, null);
		assertImage(
				"Unexpected image descriptor for invariant with theorem attribute",
				IEventBSharedImages.IMG_THEOREM_PATH, inv);
	}

	/**
	 * Checks the image for a parameter.
	 */
	@Test
	public void testImageParameter() throws Exception {
		final IEvent evt = getEvent();
		final IParameter prm = evt.getParameter("prm");
		assertImage("Unexpected image descriptor for parameter",
				IEventBSharedImages.IMG_PARAMETER_PATH, prm);
	}

	/**
	 * Checks the image for a variable.
	 */
	@Test
	public void testImageVariable() throws Exception {
		final IMachineRoot mch = getMachineRoot("mch");
		final IVariable var = mch.getVariable("var");
		assertImage("Unexpected image descriptor for variable",
				IEventBSharedImages.IMG_VARIABLE_PATH, var);
	}

	/**
	 * Checks the image for a variant.
	 */
	@Test
	public void testImageVariant() throws Exception {
		final IMachineRoot mch = getMachineRoot("mch");
		final IVariant var = mch.getVariant("var");
		assertImage("Unexpected image descriptor for variant",
				"icons/sample.gif", var);
	}

	/**
	 * Checks the image for a witness.
	 */
	@Test
	public void testImageWitness() throws Exception {
		final IEvent evt = getEvent();
		final IWitness wit = evt.getWitness("wit");
		assertImage("Unexpected image descriptor for witness",
				"icons/sample.gif", wit);
	}

}