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
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.basis.EventBElement;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.eventbeditor.operation.tests.utils.Attribute;
import org.eventb.ui.eventbeditor.operation.tests.utils.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.tests.ModifyingResourceTests;


// TODO remove dependency on ModifyingResourceTests
public class TestOperation2 extends ModifyingResourceTests {

	private ArrayList<Element> list;
	
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

	public TestOperation2() {
		super("org.eventb.ui.eventbeditor.commands.tests.TestOperation2");
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

	/**
	 * @return Added - Removed
	 */
	private int numberOfNewElement(IRodinElementDelta delta) {
		return numberOfAddedElement(delta) - numberOfRemovedElement(delta);
	}

	private int numberOfAddedElement(IRodinElementDelta delta) {
		IRodinElementDelta[] childrenDelta = delta.getAddedChildren();
		return childrenDelta.length + numberOfAddedElement(childrenDelta);
	}

	private int numberOfAddedElement(IRodinElementDelta[] delta) {
		int result = 0;
		for (int i = 0; i < delta.length; i++) {
			result = result + numberOfAddedElement(delta[i]);
		}
		return result;
	}

	private int numberOfRemovedElement(IRodinElementDelta delta) {
		IRodinElementDelta[] childrenDelta = delta.getRemovedChildren();
		return childrenDelta.length + numberOfRemovedElement(childrenDelta);
	}

	private int numberOfRemovedElement(IRodinElementDelta[] delta) {
		int result = 0;
		for (int i = 0; i < delta.length; i++) {
			result = result + numberOfRemovedElement(delta[i]);
		}
		return result;
	}

	private Object getAttributeValue(EventBElement bElem, IAttributeType type)
			throws RodinDBException {
		if (!bElem.hasAttribute(type)) {
			return null;
		} else if (type instanceof IAttributeType.Long) {
			return bElem.getAttributeValue((IAttributeType.Long) type);
		} else if (type instanceof IAttributeType.String) {
			return bElem.getAttributeValue((IAttributeType.String) type);
		} else if (type instanceof IAttributeType.Boolean) {
			return bElem.getAttributeValue((IAttributeType.Boolean) type);
		} else if (type instanceof IAttributeType.Handle) {
			return bElem.getAttributeValue((IAttributeType.Handle) type);
		} else if (type instanceof IAttributeType.Integer) {
			return bElem.getAttributeValue((IAttributeType.Integer) type);
		} else {
			return null;
		}
	}

	private boolean isEquals(EventBElement bElem, Element tElem)
			throws RodinDBException {
		Collection<Attribute<?, ?>> listAttributesTest = tElem.getAttributes();
		if (bElem.getElementType() != tElem.getType()) {
			return false;
		} else if (bElem.getAttributeTypes().length != listAttributesTest
				.size()) {
			return false;
		} else {
			for (Iterator<Attribute<?, ?>> it = listAttributesTest.iterator(); it
					.hasNext();) {
				Attribute<?, ?> att = it.next();
				Object o = getAttributeValue(bElem, att.getType());
				if (o == null) {
					return false;
				} else if (!o.equals(att.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Return true if e1 is in delta.getAddedChildren
	 * <p>
	 * e1 is in delta.getAddedChildren if there is d such as
	 * delta.getAddedChildren().contains(d), d.getElement is equals to e1 and
	 * all children of e1 are in d.getAddedChildren()
	 */
	private boolean isAdd(IRodinElementDelta delta, Element e1)
			throws RodinDBException {
		IRodinElementDelta[] listDelta = delta.getAddedChildren();
		boolean result;
		for (int i = 0; i < listDelta.length; i++) {
			EventBElement element = (EventBElement) listDelta[i].getElement();
			result = isEquals(element, e1);
			if (result) {
				result = isAdd(listDelta[i], e1.getChildren());
				if (result) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * return true if all children are in delta.addedChildren
	 */
	private boolean isAdd(IRodinElementDelta delta, Collection<Element> children)
			throws RodinDBException {
		boolean result = true;
		for (Iterator<Element> iterator = children.iterator(); iterator
				.hasNext()
				&& result;) {
			Element element = iterator.next();
			result = isAdd(delta, element);
		}
		return result;
	}

	private void executeOperation(final ArrayList<AtomicOperation> op)
			throws ExecutionException, RodinDBException {

		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor m) throws RodinDBException {
				for (Iterator<AtomicOperation> iterator = op.iterator(); iterator
						.hasNext();) {
					try {
						iterator.next().execute(null, null);
					} catch (ExecutionException e) {
						throw new RodinDBException(e.getCause(), 1);
					}
				}
			}
		}, null);
	}

	private Element getAction(String label, String assignement) {
		Element e = new Element(IAction.ELEMENT_TYPE);
		Attribute<IAttributeType.String, String> labelAtt = new Attribute<IAttributeType.String, String>(
				EventBAttributes.LABEL_ATTRIBUTE, label);
		Attribute<IAttributeType.String, String> assignementAtt = new Attribute<IAttributeType.String, String>(
				EventBAttributes.ASSIGNMENT_ATTRIBUTE, assignement);
		e.addAttribute(labelAtt);
		e.addAttribute(assignementAtt);
		return e;
	}

	private Element newElementLabelPredicate(IInternalElementType<?> type,
			String label, String predicate) {
		Element e = new Element(type);
		Attribute<IAttributeType.String, String> labelAtt = new Attribute<IAttributeType.String, String>(
				EventBAttributes.LABEL_ATTRIBUTE, label);
		Attribute<IAttributeType.String, String> predicateAtt = new Attribute<IAttributeType.String, String>(
				EventBAttributes.PREDICATE_ATTRIBUTE, predicate);
		e.addAttribute(labelAtt);
		e.addAttribute(predicateAtt);
		return e;
	}

	public String[] getArray(ArrayList<String> l) {
		return l.toArray(new String[l.size()]);
	}

	private ArrayList<AtomicOperation> getUndoOperation(
			ArrayList<AtomicOperation> op) {
		ArrayList<AtomicOperation> result = new ArrayList<AtomicOperation>();
		for (int i = op.size() - 1; i >= 0; i--) {
			result.add(new UndoOperation(op.get(i)));
		}
		return result;
	}

	private ArrayList<AtomicOperation> getRedoOperation(
			ArrayList<AtomicOperation> op) {
		ArrayList<AtomicOperation> result = new ArrayList<AtomicOperation>();
		for (Iterator<AtomicOperation> iterator = op.iterator(); iterator
				.hasNext();) {
			result.add(new RedoOperation(iterator.next()));
		}
		return result;
	}

	public void assertUndoOperation(ArrayList<AtomicOperation> ListOp,
			IRodinElement parent) throws ExecutionException, RodinDBException {
		startDeltas();
		executeOperation(ListOp);
		IRodinElementDelta delta = getDeltaFor(parent, true);

		assertEquals("Some element have not been undo", 0, delta
				.getAffectedChildren().length);

		stopDeltas();
	}

	class UndoOperation extends AtomicOperation {
		private AtomicOperation op;

		public UndoOperation(AtomicOperation op) {
			super(null);
			this.op = op;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return op.undo(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.CANCEL_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.CANCEL_STATUS;
		}
	}

	class RedoOperation extends AtomicOperation {
		private AtomicOperation op;

		public RedoOperation(AtomicOperation op) {
			super(null);
			this.op = op;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return op.redo(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.CANCEL_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.CANCEL_STATUS;
		}
	}

	// #####################################################################
	// ########### METHODE TEST
	// #####################################################################

	public void setOperationCreateAction(ArrayList<String> label,
			ArrayList<String> assignement) {
		label.add("monAction1");
		label.add("monAction2");
		label.add("monAction3");
		assignement.add("a := 2");
		assignement.add("b := 2");
		assignement.add("c := 2");

		list = new ArrayList<Element>();
		list.add(getAction(label.get(0), assignement.get(0)));
		list.add(getAction(label.get(1), assignement.get(1)));
		list.add(getAction(label.get(2), assignement.get(2)));
	}

	public ArrayList<AtomicOperation> getOperationCreateAction(
			IInternalElement event) throws ExecutionException {
		final ArrayList<AtomicOperation> op = new ArrayList<AtomicOperation>();
		ArrayList<String> label = new ArrayList<String>();
		ArrayList<String> assignement = new ArrayList<String>();
		setOperationCreateAction(label, assignement);

		op.add(OperationFactory.createAction(machineEditor, event,
				label.get(0), assignement.get(0), null));
		op.add(OperationFactory.createAction(machineEditor, event,
				label.get(1), assignement.get(1), null));
		op.add(OperationFactory.createAction(machineEditor, event,
				label.get(2), assignement.get(2), null));
		return op;
	}

	public ArrayList<AtomicOperation> getOperationCreateActionList(
			IInternalElement event) throws ExecutionException {
		final ArrayList<AtomicOperation> op = new ArrayList<AtomicOperation>();
		ArrayList<String> label = new ArrayList<String>();
		ArrayList<String> assignement = new ArrayList<String>();
		setOperationCreateAction(label, assignement);

		op.add(OperationFactory.createAction(machineEditor, event,
				getArray(label), getArray(assignement), null));
		return op;
	}

	private void helpTestAction(boolean simple) throws Exception {
		final ArrayList<AtomicOperation> op;
		IInternalElement event;
		AtomicOperation eventOp = OperationFactory.createElementGeneric(
				machineEditor, mch, IEvent.ELEMENT_TYPE, null);
		eventOp.execute(null, null);
		event = eventOp.getCreatedElement();

		op = simple ? getOperationCreateAction(event)
				: getOperationCreateActionList(event);

		assertAction(op, event);

	}

	private void assertAction(ArrayList<AtomicOperation> op, IRodinElement event)
			throws ExecutionException, RodinDBException {
		startDeltas();

		executeOperation(op);
		IRodinElementDelta delta = getDeltaFor(event);

		assertEquals("Tout les √©l√©ments n'ont pas √©t√© ajout√©", true, isAdd(
				delta, list));
		assertEquals("D'autres √©l√©ments on √©t√© ajout√©", list.size(),
				numberOfAddedElement(delta));

		stopDeltas();
	}

	@Test
	public void testActionSimple() throws Exception {
		helpTestAction(true);
	}

	@Test
	public void testActionMultiple() throws Exception {
		helpTestAction(false);
	}

	@Test
	public void testActionSimpleUndo() throws Exception {
		final ArrayList<AtomicOperation> op;
		IInternalElement event;
		AtomicOperation eventOp = OperationFactory.createElementGeneric(
				machineEditor, mch, IEvent.ELEMENT_TYPE, null);
		eventOp.execute(null, null);
		event = eventOp.getCreatedElement();
		op = getOperationCreateAction(event);
		op.addAll(getUndoOperation(op));
		assertUndoOperation(op, event);
	}

	@Test
	public void testActionMultipleUndo() throws Exception {
		final ArrayList<AtomicOperation> op;
		IInternalElement event;
		AtomicOperation eventOp = OperationFactory.createElementGeneric(
				machineEditor, mch, IEvent.ELEMENT_TYPE, null);
		eventOp.execute(null, null);
		event = eventOp.getCreatedElement();
		op = getOperationCreateActionList(event);
		op.addAll(getUndoOperation(op));
		assertUndoOperation(op, event);
	}

	@Test
	public void testActionSimpleRedo() throws Exception {
		final ArrayList<AtomicOperation> op;
		final ArrayList<AtomicOperation> opUndo;
		final ArrayList<AtomicOperation> opRedo;
		IInternalElement event;
		AtomicOperation eventOp = OperationFactory.createElementGeneric(
				machineEditor, mch, IEvent.ELEMENT_TYPE, null);
		eventOp.execute(null, null);
		event = eventOp.getCreatedElement();

		op = getOperationCreateAction(event);
		opUndo = getUndoOperation(op);
		opRedo = getRedoOperation(op);
		op.addAll(opUndo);
		op.addAll(opRedo);
		assertAction(op, event);
	}

	@Test
	public void testActionMultipleRedo() throws Exception {
		final ArrayList<AtomicOperation> op;
		final ArrayList<AtomicOperation> opUndo;
		final ArrayList<AtomicOperation> opRedo;
		IInternalElement event;
		AtomicOperation eventOp = OperationFactory.createElementGeneric(
				machineEditor, mch, IEvent.ELEMENT_TYPE, null);
		eventOp.execute(null, null);
		event = eventOp.getCreatedElement();

		op = getOperationCreateActionList(event);
		opUndo = getUndoOperation(op);
		opRedo = getRedoOperation(op);
		op.addAll(opUndo);
		op.addAll(opRedo);

		assertAction(op, event);
	}

	/**
	 * Ensures that 2 axiom are created
	 */
	private void assertCreateAxiom(IRodinElementDelta delta)
			throws RodinDBException {
		assertEquals("Tout les √©l√©ments n'ont pas √©t√© ajout√©", true, isAdd(
				delta, list));
		assertEquals("Plus d'un √©lement ajout√©", numberOfNewElement(delta), 2);

	}

	@Test
	public void testCreateAxiomSimple() throws Exception {
		final ArrayList<AtomicOperation> op = getOperationTestCreateAxiom(true);

		startDeltas();
		executeOperation(op);
		IRodinElementDelta delta = getDeltaFor(ctx);

		assertCreateAxiom(delta);

		stopDeltas();
	}

	@Test
	public void testCreateAxiomMultiple() throws Exception {
		final ArrayList<AtomicOperation> op = getOperationTestCreateAxiom(true);

		startDeltas();
		executeOperation(op);
		IRodinElementDelta delta = getDeltaFor(ctx);
		assertCreateAxiom(delta);
		stopDeltas();
	}

	/**
	 * Ensures there is no added element after create 2 Axiom and undo
	 */
	@Test
	public void testCreateAxiomSimpleUndo() throws Exception {
		final ArrayList<AtomicOperation> op = getOperationTestCreateAxiom(true);
		op.addAll(getUndoOperation(op));
		assertUndoOperation(op, ctx);
	}

	@Test
	public void testCreateAxiomMultipleUndo() throws Exception {
		final ArrayList<AtomicOperation> op = getOperationTestCreateAxiom(false);
		op.addAll(getUndoOperation(op));
		assertUndoOperation(op, ctx);
	}


	private void helpTestCreateAxiomRedo(boolean simple) throws Exception {
		final ArrayList<AtomicOperation> op = getOperationTestCreateAxiom(simple);
		final ArrayList<AtomicOperation> opUndo = getUndoOperation(op);
		final ArrayList<AtomicOperation> opRedo = getRedoOperation(op);
		op.addAll(opUndo);
		op.addAll(opRedo);

		startDeltas();
		executeOperation(op);
		IRodinElementDelta delta = getDeltaFor(ctx);
		assertCreateAxiom(delta);
		stopDeltas();
	}

	@Test
	public void testCreateAxiomSimpleRedo() throws Exception {
		helpTestCreateAxiomRedo(true);
	}

	@Test
	public void testCreateAxiomMultipleRedo() throws Exception {
		helpTestCreateAxiomRedo(false);
	}

	private ArrayList<AtomicOperation> getOperationTestCreateAxiom(
			boolean simple) {
		list = new ArrayList<Element>();
		final ArrayList<AtomicOperation> op = new ArrayList<AtomicOperation>();
		final ArrayList<String> label = new ArrayList<String>();
		final ArrayList<String> predicate = new ArrayList<String>();

		label.add("monAxiom1");
		label.add("monAxiom2");
		predicate.add("(a <=> b) <=> (b <=> a)");
		predicate.add("(a <=> b) <=> (b <=> a)");
		final Element e1 = newElementLabelPredicate(IAxiom.ELEMENT_TYPE, label
				.get(0), predicate.get(0));
		final Element e2 = newElementLabelPredicate(IAxiom.ELEMENT_TYPE, label
				.get(1), predicate.get(1));

		list.add(e1);
		list.add(e2);

		if (simple) {
			op.add(OperationFactory.createAxiomWizard(contextEditor, label
					.get(0),

			predicate.get(0)));
			op.add(OperationFactory.createAxiomWizard(contextEditor, label
					.get(1), predicate.get(1)));
		} else {
			op.add(OperationFactory.createAxiomWizard(contextEditor,
					getArray(label), getArray(predicate)));
		}
		return op;
	}
	
	
	
	
	public ArrayList<AtomicOperation> getOperationCreateCarrierSet(boolean simple){
		list = new ArrayList<Element>();
		final ArrayList<AtomicOperation> op = new ArrayList<AtomicOperation>();
		final ArrayList<String> label = new ArrayList<String>();

		
		label.add("mySet1");
		label.add("mySet2");
		label.add("mySet3");
		final Element e1 = getCarrierSet(label.get(0));
		final Element e2 = getCarrierSet(label.get(1));
		final Element e3 = getCarrierSet(label.get(2));

		list.add(e1);
		list.add(e2);
		list.add(e3);

		if (simple) {
			op.add(OperationFactory.createCarrierSetWizard(contextEditor, label
					.get(0)));
			op.add(OperationFactory.createCarrierSetWizard(contextEditor, label
					.get(1)));
			op.add(OperationFactory.createCarrierSetWizard(contextEditor, label
					.get(2)));
		} else {
			op.add(OperationFactory.createCarrierSetWizard(contextEditor,
					getArray(label)));
		}
		return op;
	}
	
	private Element getCarrierSet(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Test
	public void testCreateCarrierSetSimple(){
		// TODO Auto-generated method stub
	}

}
