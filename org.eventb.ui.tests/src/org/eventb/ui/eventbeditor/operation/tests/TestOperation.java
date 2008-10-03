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
package org.eventb.ui.eventbeditor.operation.tests;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariant;
import org.eventb.core.basis.EventBElement;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.ModifyingResourceTests;

public class TestOperation extends ModifyingResourceTests {

	/**
	 * The pointer to the test Rodin project.
	 */
	protected IRodinProject rodinProject;

	/**
	 * The testing workspace.
	 */
	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	protected IMachineFile mch;
	protected IEventBEditor<IMachineFile> machineEditor;
	protected IContextFile ctx;
	protected IEventBEditor<IContextFile> contextEditor;

	public TestOperation() {
		super("org.eventb.ui.eventbeditor.commands.tests.TestOperation");
	}

	@Before
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}

		// Create a new project
		IProject project = workspace.getRoot().getProject("P"); //$NON-NLS-1$
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		rodinProject = RodinCore.valueOf(project);

		mch = createMachine("mch0");
		machineEditor = (IEventBEditor<IMachineFile>) openEditor(mch);

		ctx = createContext("ctx0");
		contextEditor = (IEventBEditor<IContextFile>) openEditor(ctx);

	}

	@After
	@Override
	public void tearDown() throws Exception {
		EventBUIPlugin.getActivePage().closeAllEditors(false);
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

	@Test
	public void testWizardNewTheorem() throws Exception {
		final String label = "thm0";
		final String predicate = "a>b";
		final AtomicOperation cmd = OperationFactory.createTheoremWizard(
				contextEditor, label, predicate);
		startDeltas();
		executeAddOneElementWithAttributs(ctx, cmd, ITheorem.ELEMENT_TYPE,
				label, predicate);
		stopDeltas();
	}

	@Test
	public void testWizardNewAxiom() throws Exception {
		final String label = "axm0";
		final String predicate = "a>b";
		final AtomicOperation cmd = OperationFactory.createAxiomWizard(
				contextEditor, label, predicate);
		startDeltas();
		executeAddOneElementWithAttributs(ctx, cmd, IAxiom.ELEMENT_TYPE, label,
				predicate);
		stopDeltas();
	}

	@Test
	public void testWizardNewConstants() throws Exception {
		final IRodinElementDelta[] deltaAffected;
		final AtomicOperation cmd;
		final String identifier = "cst0";
		final ArrayList<String> labels = getLabels__testWizardNewConstants();
		final ArrayList<String> predicates = getPredicates__testWizardNewConstants();

		cmd = OperationFactory.createConstantWizard(contextEditor, identifier,
				getStringTabFromList(labels), getStringTabFromList(predicates));

		assertExists("RodinFile should exist", ctx);
		startDeltas();
		cmd.execute(null, null);
		deltaAffected = getDeltaFor(ctx, true).getAffectedChildren();

		assertEquals("The numbers of affected elements are not equals",
				1 + labels.size(), deltaAffected.length);

		for (IRodinElementDelta delta : deltaAffected) {
			IRodinElement element = delta.getElement();
			if (element.getElementType().equals(IConstant.ELEMENT_TYPE)) {
				IConstant constant = (IConstant) element;
				assertEquals("Bad Constant identifier", identifier, constant
						.getIdentifierString());
			} else if (element.getElementType().equals(IAxiom.ELEMENT_TYPE)) {
				IAxiom axiom = (IAxiom) element;
				assertTrue("Bad Axiom label", labels.contains(axiom.getLabel()));
				assertTrue("Bad Axiom predicate", predicates.contains(axiom
						.getPredicateString()));
			} else {
				new AssertionError("An element has been affected");
			}
		}
		stopDeltas();
	}

	/**
	 * 
	 * ensures :
	 * <ul>
	 * <li>set is added ( carrier set )</li>
	 * <li>elements are added ( constant )</li>
	 * <li>one axiom is created for set definition</li>
	 * <li>axioms for no equality between elements</li>
	 * <li>added axioms have different label</li>
	 * </ul>
	 */
	@Test
	public void testWizardNewEnumeratedSet() throws Exception {
		final IRodinElementDelta[] deltaAffected;
		final String identifier = getName__WizardNewEnumeratedSet();
		final ArrayList<String> elements = getElements__testWizardNewEnumeratedSet();
		final ArrayList<String> axioms = getResultedAxioms__WizardNewEnumeratedSet();
		final ArrayList<String> labels = new ArrayList<String>();
		final AtomicOperation cmd = OperationFactory.createEnumeratedSetWizard(
				contextEditor, identifier, getStringTabFromList(elements));

		assertExists("RodinFile should exist", ctx);
		startDeltas();
		cmd.execute(null, null);
		deltaAffected = getDeltaFor(ctx, true).getAffectedChildren();

		assertEquals("The numbers of affected elements are not equals", 1
				+ elements.size() + axioms.size(), deltaAffected.length);

		for (IRodinElementDelta delta : deltaAffected) {
			IRodinElement element = delta.getElement();
			if (element.getElementType().equals(IConstant.ELEMENT_TYPE)) {
				IConstant constant = (IConstant) element;
				assertTrue("Bad Element", elements.contains(constant
						.getIdentifierString()));
			} else if (element.getElementType().equals(IAxiom.ELEMENT_TYPE)) {
				final IAxiom axiom = (IAxiom) element;
				final String label = axiom.getLabel();
				assertTrue("Bad Axiom predicate", axioms.contains(axiom
						.getPredicateString()));
				assertTrue("Two axioms have label : " + label, !labels
						.contains(label));
				labels.add(label);
			} else if (element.getElementType()
					.equals(ICarrierSet.ELEMENT_TYPE)) {
				ICarrierSet set = (ICarrierSet) element;
				assertEquals("Bad Identifier", identifier, set
						.getIdentifierString());
			} else {
				new AssertionError("An element has been affected");
			}
		}
		stopDeltas();
	}

	@Test
	public void testWizardNewCarrierSet() throws Exception {
		final String identifier = "carotte";
		final AtomicOperation cmd = OperationFactory.createCarrierSetWizard(
				contextEditor, identifier);
		startDeltas();
		executeAddOneElementWithAttributs(ctx, cmd, ICarrierSet.ELEMENT_TYPE,
				"", identifier);
		stopDeltas();
	}

	@Test
	public void testWizardNewVariant() throws Exception {
		final String expression = "a>b";
		final AtomicOperation cmd = OperationFactory.createVariantWizard(
				machineEditor, expression);
		startDeltas();
		executeAddOneElementWithAttributs(mch, cmd, IVariant.ELEMENT_TYPE, "",
				expression);
		stopDeltas();
	}

	@Test
	public void testWizardNewInvariant() throws Exception {
		final String label = "inv0";
		final String predicate = "a>b";
		final AtomicOperation cmd = OperationFactory.createInvariantWizard(
				machineEditor, label, predicate);
		startDeltas();
		executeAddOneElementWithAttributs(mch, cmd, IInvariant.ELEMENT_TYPE,
				label, predicate);
		stopDeltas();
	}

	@Test
	public void testWizardVariable() throws Exception {
		final IRodinElementDelta[] deltaAffected;
		final String varName = "var0";
		final String actName = "act0";
		final String actSub = "var0 := 0";
		ArrayList<Pair<String, String>> invariants = new ArrayList<Pair<String, String>>();
		invariants.add(new Pair<String, String>("inv0", "var0 : NAT"));
		invariants.add(new Pair<String, String>("inv0", "var0 < 50"));

		final AtomicOperation cmd = OperationFactory.createVariableWizard(
				machineEditor, varName, invariants, actName, actSub);
		assertExists("RodinFile should exist", ctx);

		startDeltas();
		cmd.execute(null, null);
		deltaAffected = getDeltaFor(mch, true).getAffectedChildren();

		// add initialisation + variable + invariant
		assertEquals("The numbers of affected elements are not equals",
				2 + invariants.size(), deltaAffected.length);
		//ajouter un test pour les sous fils
		stopDeltas();
		throw new Exception("This test is not yet finished.") ;
	}

	// public void testWizardEvent() throws Exception {
	// final String label = "";
	// final String predicate = "a>b";
	// final AtomicCommand cmd = CommandFactory.createEventWizard(
	// machineEditor, label, predicate);
	// }

	@Test
	public void testWizardNewTheoremUndo() throws Exception {
		final String label = "thm0";
		final String predicate = "a>b";
		final AtomicOperation cmd = OperationFactory.createTheoremWizard(
				contextEditor, label, predicate);
		executeAddAndUndo(ctx, cmd);
	}

	@Test
	public void testWizardNewAxiomUndo() throws Exception {
		final String label = "thm0";
		final String predicate = "a>b";
		final AtomicOperation cmd = OperationFactory.createAxiomWizard(
				contextEditor, label, predicate);
		executeAddAndUndo(ctx, cmd);
	}

	@Test
	public void testWizardNewConstantUndo() throws Exception {
		final AtomicOperation cmd;
		final String identifier = "cst0";
		final ArrayList<String> labels = getLabels__testWizardNewConstants();
		final ArrayList<String> predicates = getPredicates__testWizardNewConstants();

		cmd = OperationFactory.createConstantWizard(contextEditor, identifier,
				getStringTabFromList(labels), getStringTabFromList(predicates));
		executeAddAndUndo(ctx, cmd);
	}

	@Test
	public void testWizardNewEnumeratedSetUndo() throws Exception {
		final AtomicOperation cmd;
		final String identifier = getName__WizardNewEnumeratedSet();
		final ArrayList<String> elements = getElements__testWizardNewEnumeratedSet();

		cmd = OperationFactory.createEnumeratedSetWizard(contextEditor,
				identifier, getStringTabFromList(elements));
		executeAddAndUndo(ctx, cmd);
	}

	@Test
	public void testWizardNewCarrierSetUndo() throws Exception {
		final String identifier = "mySet";
		final AtomicOperation cmd = OperationFactory.createCarrierSetWizard(
				contextEditor, identifier);
		executeAddAndUndo(ctx, cmd);
	}

	@Test
	public void testWizardNewVariantUndo() throws Exception {
		final String expression = "a>b";
		final AtomicOperation cmd = OperationFactory.createVariantWizard(
				machineEditor, expression);
		executeAddAndUndo(mch, cmd);
	}

	@Test
	public void testWizardNewInvariantUndo() throws Exception {
		final String label = "inv0";
		final String predicate = "a>b";
		final AtomicOperation cmd = OperationFactory.createInvariantWizard(
				machineEditor, label, predicate);
		executeAddAndUndo(mch, cmd);
	}

	//
	// public void testWizardNewVaribaleUndo() throws Exception {
	// final String label = "thm0";
	// final String predicate = "a>b";
	// final AtomicCommand cmd = CommandFactory.createVariableWizard(
	// contextEditor, label, predicate);
	// executeAddAndUndo(ctx, cmd);
	// }
	//
	// public void testWizardNewEventUndo() throws Exception {
	// final String label = "thm0";
	// final String predicate = "a>b";
	// final AtomicCommand cmd = CommandFactory.createEventWizard(
	// contextEditor, label, predicate);
	// executeAddAndUndo(ctx, cmd);
	// }

	/**
	 * Execute the command cmd. cmd must be a simple command. Ensures : - there
	 * is only one added element - the type of element is type - the label of
	 * element is label
	 */
	private void executeAddOneElementWithAttributs(final IRodinFile rf,
			final AtomicOperation cmd,
			final IElementType<? extends IRodinElement> type,
			final String label, final String content)
			throws ExecutionException, RodinDBException {
		final IRodinElementDelta delta;

		assertExists("RodinFile should exist", rf);

		cmd.execute(null, null);
		delta = getDeltaFor(rf, true);

		assertNotNull("No delta", delta);
		assertTrue("Childrens have not changed", deltaChildrenChanged(delta));
		assertEquals("More than one element which have been added", 1, delta
				.getAddedChildren().length);

		for (IRodinElementDelta childrenDelta : delta.getAddedChildren()) {
			assertElement(childrenDelta, type, label, content);
		}
	}

	/**
	 * Execute and undo the command cmd. cmd must add element
	 * <p>
	 * Ensures : all added elements do not exist
	 */
	private void executeAddAndUndo(final IRodinFile rf, final AtomicOperation cmd)
			throws ExecutionException, RodinDBException {
		final IRodinElementDelta delta;

		assertExists("RodinFile should exist", rf);

		startDeltas();
		cmd.execute(null, null);
		cmd.undo(null, null);
		delta = getDeltaFor(rf, true);

		assertNotNull("No delta", delta);

		for (IRodinElementDelta d : delta.getAddedChildren()) {
			assertFalse("One Element has not be removed", d.getElement()
					.exists());
		}
		stopDeltas();
	}

	/**
	 * ensures the type, label and content of element described by delta are
	 * equals to arguments
	 */
	private void assertElement(IRodinElementDelta delta,
			IElementType<? extends IRodinElement> type, String label,
			String content) throws RodinDBException {
		EventBElement element = (EventBElement) delta.getElement();

		assertTrue("Added element does not exist", element.exists());
		assertEquals("Wrong delta kind", IRodinElementDelta.ADDED, delta
				.getKind());
		assertEquals("Added element has a wrong type", type, element
				.getElementType());
		if (type.equals(IInvariant.ELEMENT_TYPE)
				|| type.equals(ITheorem.ELEMENT_TYPE)
				|| type.equals(IAxiom.ELEMENT_TYPE)) {
			assertEquals("Added element has a wrong label", label, element
					.getLabel());
			assertEquals("Added element has a wrong predicate", content,
					element.getPredicateString());
		} else if (type.equals(ICarrierSet.ELEMENT_TYPE)) {
			assertEquals("Added CarrierSet has a wrong identifier", content,
					element.getIdentifierString());
		} else if (type.equals(IVariant.ELEMENT_TYPE)) {
			assertEquals("Added Variant has a wrong expression", content,
					element.getExpressionString());
		} else {
			throw new AssertionError("No test for this element");
		}

	}

	private void assertEquals(String msg,
			IElementType<? extends IRodinElement> expected,
			IElementType<? extends IRodinElement> actual) {
		if (!expected.equals(actual)) {
			throw new AssertionError(msg + " (expected : " + expected
					+ ", actual : " + actual);
		}
	}

	private ArrayList<String> getLabels__testWizardNewConstants() {
		final ArrayList<String> labels = new ArrayList<String>();
		labels.add("axm0");
		labels.add("axm1");
		labels.add("axm2");
		return labels;
	}

	private ArrayList<String> getPredicates__testWizardNewConstants() {
		final ArrayList<String> predicates = new ArrayList<String>();
		predicates.add("myConst : NAT");
		predicates.add("myConst > 0");
		predicates.add("myConst < 42");
		return predicates;
	}

	private String getName__WizardNewEnumeratedSet() {
		return "enum0";
	}

	private ArrayList<String> getElements__testWizardNewEnumeratedSet() {
		final ArrayList<String> elements = new ArrayList<String>();
		elements.add("a");
		elements.add("b");
		elements.add("c");
		return elements;
	}

	private ArrayList<String> getResultedAxioms__WizardNewEnumeratedSet() {
		final ArrayList<String> elements = new ArrayList<String>();
		elements.add(getName__WizardNewEnumeratedSet() + " = {a, b, c}");
		elements.add("\u00ac a = b");
		elements.add("\u00ac a = c");
		elements.add("\u00ac b = c");
		return elements;
	}

	/* UTILS FUNCTIONS */

	/**
	 * Open the an Event-B Editor ({@link IEventBEditor}) for a given
	 * component. Assuming that the component is either a machine ({@link IMachineFile})
	 * or a context ({@link IContextFile}).
	 * 
	 * @param component
	 *            the input component
	 * @return the Event-B Editor for the input component.
	 * @throws PartInitException
	 *             if some problems occur when open the editor.
	 */
	protected IEventBEditor<?> openEditor(IRodinFile component)
			throws PartInitException {
		IEditorInput fileInput = new FileEditorInput(component.getResource());
		String editorId = ""; //$NON-NLS-1$
		if (component instanceof IMachineFile) {
			editorId = EventBMachineEditor.EDITOR_ID;
		} else if (component instanceof IContextFile) {
			editorId = EventBContextEditor.EDITOR_ID;
		}
		return (IEventBEditor<?>) EventBUIPlugin.getActivePage().openEditor(
				fileInput, editorId);
	}

	/**
	 * Utility method to create a context with the given bare name. The context
	 * is created as a child of the test Rodin project ({@link #rodinProject}).
	 * 
	 * @param bareName
	 *            the bare name (without the extension .ctx) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IContextFile createContext(String bareName)
			throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IContextFile result = (IContextFile) rodinProject
				.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected IMachineFile createMachine(String bareName)
			throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IMachineFile result = (IMachineFile) rodinProject
				.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	private String[] getStringTabFromList(ArrayList<String> list) {
		String[] tab = new String[list.size()];
		int i = 0;
		for (String string : list) {
			tab[i] = string;
			i++;
		}
		return tab;
	}

}
