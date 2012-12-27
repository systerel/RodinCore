/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.tests;

import static org.eventb.internal.ui.preferences.PreferenceConstants.PREFIX_PREFERENCE_PAGE_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.eventbeditor.actions.AutoActNaming;
import org.eventb.internal.ui.eventbeditor.actions.AutoAxmNaming;
import org.eventb.internal.ui.eventbeditor.actions.AutoElementNaming;
import org.eventb.internal.ui.eventbeditor.actions.AutoGrdNaming;
import org.eventb.internal.ui.eventbeditor.actions.AutoInvNaming;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Tests for the automatic renaming actions of the event-B editor.
 */
public class TestUIRenaming extends EventBUITest {
	
	/**
	 * Ensures that guard labels are renamed taking account of implicit
	 * inherited elements.
	 * <p>
	 * cf. <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=2723288&group_id=108850&atid=651669"
	 * >Bug 2723288</a>
	 * </p>
	 */
	@Test
	public void testGuardRenaming() throws Exception {
		testRenaming(IGuard.ELEMENT_TYPE);
	}

	/**
	 * Ensures that guard labels are renamed taking into account of prefix.
	 */
	@Test
	public void testGuardPrefixRenaming() throws Exception {
		testMachinePrefixRenaming(IGuard.ELEMENT_TYPE);
	}

	/**
	 * Ensures that action labels are renamed taking account of implicit
	 * inherited elements.
	 * <p>
	 * cf. <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=2723288&group_id=108850&atid=651669"
	 * >Bug 2723288</a>
	 * </p>
	 */
	@Test
	public void testActionRenaming() throws Exception {
		testRenaming(IAction.ELEMENT_TYPE);
	}

	/**
	 * Ensures that action labels are renamed taking into account of prefix.
	 */
	@Test
	public void testActionPrefixRenaming() throws Exception {
		testMachinePrefixRenaming(IAction.ELEMENT_TYPE);
	}

	/**
	 * Ensures that invariant labels are correctly renamed.
	 */
	@Test
	public void testInvariantRenaming() throws Exception {
		final IMachineRoot mch = createMachine("m1");
		final String prefix = PreferenceUtils.getAutoNamePrefix(mch, IInvariant.ELEMENT_TYPE);
		final IInvariant inv1 = createInvariant(mch, prefix + "24", "", false);
		final IInvariant inv2 = createInvariant(mch, "foo", "", true);

		renameInvariants(mch);
		assertEquals(prefix + "1", inv1.getLabel());
		assertEquals(prefix + "2", inv2.getLabel());
	}

	/**
	 * Ensures that invariant labels are renamed taking into account of prefix.
	 */
	@Test
	public void testInvariantPrefixRenaming() throws Exception {
		testMachinePrefixRenaming(IInvariant.ELEMENT_TYPE);
	}

	/**
	 * Ensures that axiom labels are correctly renamed.
	 */
	@Test
	public void testAxiomRenaming() throws Exception {
		final IContextRoot ctx = createContext("c1");
		final String prefix = PreferenceUtils.getAutoNamePrefix(ctx, IAxiom.ELEMENT_TYPE);
		final IAxiom axm1 = createAxiom(ctx, prefix + "24", "", false);
		final IAxiom axm2 = createAxiom(ctx, "foo", "", true);

		renameAxioms(ctx);
		assertEquals(prefix + "1", axm1.getLabel());
		assertEquals(prefix + "2", axm2.getLabel());
	}

	/**
	 * Ensures that axiom labels are renamed taking into account of prefix.
	 */
	@Test
	public void testAxiomPrefixRenaming() throws Exception {
		testContextPrefixRenaming(IAxiom.ELEMENT_TYPE);
	}
	
	/**
	 * Ensures that axiom labels are renamed taking into account of prefix.
	 * Testing the new preference/property mechanism for prefixes.
	 */
	@Test
	public void testAxiomPrefixRenamingPrefMechanism() throws Exception {
		testContextPrefixRenamingByPreference(IAxiom.ELEMENT_TYPE);
	}

	private void renameGuards(final IMachineRoot mchRoot)
			throws PartInitException {
		runAction(mchRoot, new AutoGrdNaming());
	}

	private void renameActions(final IMachineRoot mchRoot)
			throws PartInitException {
		runAction(mchRoot, new AutoActNaming());
	}

	private void renameInvariants(final IMachineRoot mchRoot)
			throws PartInitException {
		runAction(mchRoot, new AutoInvNaming());
	}

	private void renameAxioms(final IContextRoot ctxRoot)
			throws PartInitException {
		runAction(ctxRoot, new AutoAxmNaming());
	}

	private void runAction(final IEventBRoot root, AutoElementNaming action)
			throws PartInitException {
		final IEditorPart targetEditor = openEditor(root);
		action.setActiveEditor(null, targetEditor);
		action.run(null);
	}

	private void testRenaming(IInternalElementType<?> type)
			throws RodinDBException, PartInitException {
		final IMachineRoot m1 = createMachine("m1");
		final IEvent event1 = createEvent(m1, "event");
		final String prefix = PreferenceUtils.getAutoNamePrefix(m1, type);

		final IMachineRoot m2 = createMachine("m2");
		createRefinesMachineClause(m2, m1.getElementName());
		final IEvent event2 = createEvent(m2, event1.getLabel());
		createRefinesEventClause(event2, event1.getLabel());
		event2.setExtended(true, null);

		final ILabeledElement elt;
		if (type.equals(IGuard.ELEMENT_TYPE)) {
			createGuard(event1, prefix + "1", "");
			elt = createGuard(event2, prefix + "24", "");
			renameGuards(m2);
		} else if (type.equals(IAction.ELEMENT_TYPE)) {
			createAction(event1, prefix + "1", "");
			elt = createAction(event2, prefix + "24", "");
			renameActions(m2);
		} else {
			throw new IllegalArgumentException("unexpected element type");
		}
		assertEquals(prefix + "2", elt.getLabel());
	}

	private void testMachinePrefixRenaming(IInternalElementType<?> type)
			throws Exception {
		
		final IMachineRoot m = createMachine("m");
		final IEvent event = createEvent(m, "event");
		final String oldValue = PreferenceUtils.getAutoNamePrefix(m, type);
		final String prefix = oldValue + "2";
		PreferenceUtils.setAutoNamePrefix(type, prefix);
		
		final String elt1Label = prefix + "4";
		final String elt2Label = "foo";

		final ILabeledElement elt1, elt2;
		if (type.equals(IGuard.ELEMENT_TYPE)) {
			elt1 = createGuard(event, elt1Label, "");
			elt2 = createGuard(event, elt2Label, "");
			renameGuards(m);
		} else if (type.equals(IAction.ELEMENT_TYPE)) {
			elt1 = createAction(event, elt1Label, "");
			elt2 = createAction(event, elt2Label, "");
			renameActions(m);
		} else if (type.equals(IInvariant.ELEMENT_TYPE)) {
			elt1 = createInvariant(m, elt1Label, "", false);
			elt2 = createInvariant(m, elt2Label, "", true);
			renameInvariants(m);
		} else {
			throw new IllegalArgumentException("unexpected element type");
		}
		
		assertEquals(prefix + "1", elt1.getLabel());
		assertEquals(prefix + "2", elt2.getLabel());
		
		// We restore previous prefix settings
		PreferenceUtils.setAutoNamePrefix(type, oldValue);
	}

	private void testContextPrefixRenaming(IInternalElementType<?> type)
			throws Exception {
		
		final IContextRoot c = createContext("c");
		final String oldValue = PreferenceUtils.getAutoNamePrefix(c, type);
		final String prefix = oldValue + "2";
		PreferenceUtils.setAutoNamePrefix(type, prefix);
		final String elt1label = prefix + "4";
		final String elt2Label = "foo";

		final ILabeledElement elt1, elt2;
		if (type.equals(IAxiom.ELEMENT_TYPE)) {
			elt1 = createAxiom(c, elt1label, "", false);
			elt2 = createAxiom(c, elt2Label, "", true);
			renameAxioms(c);
		} else {
			throw new IllegalArgumentException("unexpected element type");
		}
		assertEquals(prefix + "1", elt1.getLabel());
		assertEquals(prefix + "2", elt2.getLabel());
		
		// We restore previous prefix settings
		PreferenceUtils.setAutoNamePrefix(type, oldValue);
	}
	
	private void testContextPrefixRenamingByPreference(IInternalElementType<?> type)
			throws Exception {
		
		final IContextRoot c = createContext("c");
		final IProject p = c.getRodinProject().getProject();
		final String globalPrefix = PreferenceUtils.getAutoNamePrefix(c, type);
		final String prefix = globalPrefix + "2";
		// Now testing project specific prefix settings
		PreferenceUtils.setAutoNamePrefix(p, type, prefix);
		final String elt1label = prefix + "4";
		final String elt2Label = "foo";

		final ILabeledElement elt1, elt2;
		if (type.equals(IAxiom.ELEMENT_TYPE)) {
			elt1 = createAxiom(c, elt1label, "", false);
			elt2 = createAxiom(c, elt2Label, "", true);
			renameAxioms(c);
		} else {
			throw new IllegalArgumentException("unexpected element type");
		}
		assertEquals(prefix + "1", elt1.getLabel());
		assertEquals(prefix + "2", elt2.getLabel());
		PreferenceUtils.setAutoNamePrefix(p, type, "");
		
		renameAxioms(c);
		assertEquals(globalPrefix + "1", elt1.getLabel());
		assertEquals(globalPrefix + "2", elt2.getLabel());
		
		// We restore previous prefix values by erasing the project 
		// specific settings
		PreferenceUtils.clearAllProperties(PREFIX_PREFERENCE_PAGE_ID, p);
	}
	
}
