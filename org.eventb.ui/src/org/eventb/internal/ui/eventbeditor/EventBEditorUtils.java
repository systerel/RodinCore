/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed axiom form for enumerated sets
 *     Systerel - replaced inherited by extended, local variable by parameter
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixInvName;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides some common methods that are used in Event-B
 *         Editors.
 */
public class EventBEditorUtils {

	/**
	 * The debug flag. This is set by the option when the platform is launch.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** EventBEditor *** ";

	static IAction newAct;

	static IGuard newGrd;

	static IParameter newParam;

	static IVariable newVar;

	static IInvariant newInv;

	static IVariant newVariant;

	static ITheorem newThm;

	static IEvent newEvt;

	static IAxiom newAxm;

	static ICarrierSet newSet;

	static IConstant newCst;

	static IRefinesEvent newRefEvt;

	static IWitness newWit;

	/**
	 * Delete selected elements in a tree viewer.
	 * <p>
	 * 
	 * @param viewer
	 *            The current tree viewer in the Event-B Editor.
	 */
	public static void deleteElements(final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IStructuredSelection ssel = (IStructuredSelection) viewer
						.getSelection();
				IInternalElement[] elements = new IInternalElement[ssel.size()];
				int i = 0;
				for (Iterator<?> it = ssel.iterator(); it.hasNext(); i++) {
					elements[i] = (IInternalElement) it.next();
				}
				AtomicOperation operation = OperationFactory
						.deleteElement(elements);
				History.getInstance().addOperation(operation);
			}
		});
	}

	/**
	 * Handle the "up" action in a tree viewer.
	 * <p>
	 * 
	 * @param viewer
	 *            The current tree viewer in the Event-B Editor.
	 */
	public static void handleUp(final IEventBEditor<?> editor, final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				handleGeneric(editor, viewer, true);
				return;
			}
		});

	}

	/**
	 * Handle the "down" action in a tree viewer.
	 * <p>
	 * 
	 * @param viewer
	 *            The current tree viewer in the Event-B Editor
	 */
	public static void handleDown(final IEventBEditor<?> editor,final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				handleGeneric(editor, viewer, false);
				return;
			}
		});
	}

	/**
	 * @param up if <code>up</code> is true do handleUp else do handleDown
	 * */
	public static void handleGeneric(IEventBEditor<?> editor, final TreeViewer viewer, boolean up) {
		AtomicOperation operation = OperationFactory.handle(editor,viewer,up);
		History.getInstance().addOperation(operation);
	}
	
	private static IInternalElement getEvent(TreeViewer viewer) {
		IStructuredSelection ssel = (IStructuredSelection) viewer
				.getSelection();
		if (ssel.size() == 1) {
			Object obj = ssel.getFirstElement();
			return TreeSupports.getEvent(obj);
		} else {
			return null;
		}
	}
	
	/**
	 * Add a new action.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addAction(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation operation = OperationFactory.createAction(
							editor, event, null, EventBUIPlugin.SUB_DEFAULT,
							null);
					History.getInstance().addOperation(operation);
					displayInSynthesis(viewer, event, operation
							.getCreatedElement());
				}
			}
		});
	}

	/**
	 * Add a refines event element.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addRefinesEvent(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					String abs_name;
					try {
						abs_name = ((IEvent) event).getLabel();
						AtomicOperation op = OperationFactory.createElement(editor,
								IRefinesEvent.ELEMENT_TYPE,
								EventBAttributes.TARGET_ATTRIBUTE, abs_name);
						History.getInstance().addOperation(op);
						displayInSynthesis(viewer, event, op.getCreatedElement());
					} catch (RodinDBException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Add a new action.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addWitness(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation op = OperationFactory.createElement(editor,
							IWitness.ELEMENT_TYPE,
							EventBAttributes.PREDICATE_ATTRIBUTE,
							EventBUIPlugin.PRD_DEFAULT);
					History.getInstance().addOperation(op);
					displayInSynthesis(viewer, event, op.getCreatedElement());
				}
			}
		});
	}

	/**
	 * Add a new guard.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addGuard(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation operation = OperationFactory.createGuard(
							editor, event, null, EventBUIPlugin.GRD_DEFAULT,
							null);
					History.getInstance().addOperation(operation);
					displayInSynthesis(viewer, event, operation.getCreatedElement());
				}
			}
		});
	}

	/**
	 * Add a new event parameter.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addParameter(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation operation = OperationFactory
							.createElementGeneric(editor, event,
									IParameter.ELEMENT_TYPE, null);
					History.getInstance().addOperation(operation);
					displayInSynthesis(viewer, event, operation.getCreatedElement());
				}
			}
		});
	}

	/**
	 * Add a new variable.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addVariable(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				AtomicOperation op = OperationFactory.createElementGeneric(
						editor, editor.getRodinInput(), IVariable.ELEMENT_TYPE,
						null);
				History.getInstance().addOperation(op);
				addNewElement(editor,op);
			}
		});
	}

	/**
	 * Add a new invariant.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addInvariant(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createInvariantWizard(editor,
				null, EventBUIPlugin.INV_DEFAULT);
		History.getInstance().addOperation(op);
		addNewElement(editor,op);
	}

	/**
	 * Add a new theorem.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addTheorem(final IEventBEditor<?> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createTheoremWizard(editor, null,
				EventBUIPlugin.THM_DEFAULT);
		History.getInstance().addOperation(op);
		addNewElement(editor,op);
	}

	/**
	 * Add a new event.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addEvent(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {

		final String name = null ;
		
		final String[] varNames = defaultArray(3, null);
		final String[] grdNames = defaultArray(3, null);
		final String[] grdPredicates = defaultArray(3,
				EventBUIPlugin.PRD_DEFAULT);
		final String[] actNames = defaultArray(3, null);
		final String[] actSubstitutions = defaultArray(3,
				EventBUIPlugin.SUB_DEFAULT);
		final AtomicOperation op = OperationFactory.createEvent(editor, name,
				varNames, grdNames, grdPredicates, actNames, actSubstitutions);
		History.getInstance().addOperation(op);
		IInternalElement event = op.getCreatedElement();
		displayInSynthesis(viewer, event, event);
	}

	private static void displayInSynthesis(final TreeViewer viewer,
			IInternalElement expanded, IInternalElement selected) {
		viewer.setExpandedState(TreeSupports.findItem(viewer.getTree(),
				expanded).getData(), true);
		try {
			select((EventBEditableTreeViewer) viewer, selected, 0);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String[] defaultArray(int size, String defaultString) {
		assert size > 0;
		String[] result = new String[size];
		for(int i = 0 ; i < size ; i++){
			result[i] = defaultString ;
		}
		return result ;
	}
	
	/**
	 * Add a new axiom.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addAxiom(final IEventBEditor<IContextFile> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createAxiomWizard(editor, null,
				EventBUIPlugin.AXM_DEFAULT);
		History.getInstance().addOperation(op);
		addNewElement(editor, op);
	}

	/**
	 * Add a new constant.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addConstant(final IEventBEditor<IContextFile> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createElementGeneric(editor,
				editor.getRodinInput(), IConstant.ELEMENT_TYPE, null);
		History.getInstance().addOperation(op);
		addNewElement(editor, op);
	}

	/**
	 * Add a new carriet set.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addSet(final IEventBEditor<IContextFile> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createElementGeneric(editor,
				editor.getRodinInput(), ICarrierSet.ELEMENT_TYPE, null);
		History.getInstance().addOperation(op);
		addNewElement(editor, op);
	}

	/**
	 * Try to select an object in the viewer at a specific column.
	 * <p>
	 * 
	 * @param obj
	 *            the object
	 * @param column
	 *            the column
	 * @throws RodinDBException
	 *             a Rodin Exception when selecting the element.
	 */
	static void select(EventBEditableTreeViewer viewer, Object obj, int column)
			throws RodinDBException {
		TreeItem item = TreeSupports.findItem(viewer.getTree(),
				(IRodinElement) obj);
		viewer.reveal(item.getData());

		// try to select the column to edit element
		viewer.selectItem(item, column);
	}

	/**
	 * Utility method to create a variable with its type invariant and
	 * initialization using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the variable and its invariant,
	 *            initialization will be created in
	 */
	public static void intelligentNewVariable(final IEventBEditor<IMachineFile> editor,
			final IRodinFile rodinFile) {
		try {

			String prefix = UIUtils.getPrefix(editor.getRodinInput(),
					IInvariant.ELEMENT_TYPE, PrefixInvName.DEFAULT_PREFIX);
			String index = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, prefix);

			final IntelligentNewVariableInputDialog dialog = new IntelligentNewVariableInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Variable", prefix, index);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			final String varName = dialog.getName();
			final Collection<Pair<String, String>> invariant = dialog
					.getInvariants();
			final String actName = dialog.getInitActionName();
			final String actSub = dialog.getInitActionSubstitution();
			final AtomicOperation operation = OperationFactory
					.createVariableWizard(editor, varName, invariant, actName,
							actSub);
			History.getInstance().addOperation(operation);
			addNewElement(editor, operation);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	static void newVariable(IEventBEditor<IMachineFile> editor, String varName,
			final Collection<Pair<String, String>> invariant, String actName,
			String actSub) {
		final AtomicOperation operation = OperationFactory
				.createVariableWizard(editor, varName, invariant, actName,
						actSub);
		History.getInstance().addOperation(operation);
		addNewElement(editor, operation);
	}
	
	/**
	 * Utility method to create a constant with its type axiom using a modal
	 * dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the constant and its axiom will be created
	 *            in
	 */
	public static void intelligentNewConstant(
			final IEventBEditor<IContextFile> editor, final IRodinFile rodinFile) {

		final IntelligentNewConstantInputDialog dialog = new IntelligentNewConstantInputDialog(
				editor, Display.getCurrent().getActiveShell(), "New Constant");

		dialog.open();

		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String identifier = dialog.getIdentifier();
		final String[] axmNames = dialog.getAxiomNames();
		final String[] axmSubs = dialog.getAxiomPredicates();
		newConstant(editor, identifier, axmNames, axmSubs);
	}
	
	static void newConstant(IEventBEditor<IContextFile> editor, String identifier,
			String[] axmNames, String[] axmSubs) {
		AtomicOperation operation = OperationFactory.createConstantWizard(
				editor, identifier, axmNames, axmSubs);
		History.getInstance().addOperation(operation);
		addNewElements(editor, operation);
	}

	private static IInternalElement getInitialisation(IRodinFile rodinFile)
			throws RodinDBException {
		IRodinElement[] events = rodinFile
				.getChildrenOfType(IEvent.ELEMENT_TYPE);
		for (IRodinElement event : events) {
			IEvent element = (IEvent) event;
			if (element.getLabel().equals("INITIALISATION")) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Utility method to create new invariants using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new invariants will be created in
	 */
	public static void newInvariants(final IEventBEditor<IMachineFile> editor,
			final IRodinFile rodinFile) {
		try {
			String invPrefix = UIUtils.getPrefix(editor.getRodinInput(),
					IInvariant.ELEMENT_TYPE, PrefixInvName.DEFAULT_PREFIX);

			String invIndex = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, invPrefix);
			final ElementNameContentInputDialog<IInvariant> dialog =
				new ElementNameContentInputDialog<IInvariant>(
					Display.getCurrent().getActiveShell(), "New Invariants",
					"Label(s) and predicate(s)", editor, IInvariant.ELEMENT_TYPE,
					invPrefix, invIndex);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			String[] names = dialog.getNewNames();
			String[] contents = dialog.getNewContents();
			AtomicOperation operation = OperationFactory.createInvariantWizard(
					editor, names, contents);
			History.getInstance().addOperation(operation);
			addNewElements(editor, operation);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create a new variant using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new invariants will be created in
	 */
	public static void newVariant(final IEventBEditor<IMachineFile> editor,
			final IRodinFile rodinFile) {
		final NewVariantInputDialog dialog = new NewVariantInputDialog(Display
				.getCurrent().getActiveShell(), "New Variant", "Expression");
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String expression = dialog.getExpression();
		AtomicOperation operation = OperationFactory.createVariantWizard(
				editor, expression);
		History.getInstance().addOperation(operation);
		addNewElements(editor, operation);
	}

	/**
	 * Utility method to create new theorems using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new theorems will be created in
	 */
	public static void newTheorems(final IEventBEditor<?> editor,
			final IRodinFile rodinFile) {
		try {
			final String defaultPrefix = AttributeRelUISpecRegistry
					.getDefault().getDefaultPrefix(
							"org.eventb.core.theoremLabel");
			String thmPrefix = UIUtils.getPrefix(editor.getRodinInput(),
					ITheorem.ELEMENT_TYPE, defaultPrefix);
			String thmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), ITheorem.ELEMENT_TYPE, thmPrefix);
			final ElementNameContentInputDialog<ITheorem> dialog =
				new ElementNameContentInputDialog<ITheorem>(
					Display.getCurrent().getActiveShell(), "New Theorems",
					"Label(s) and predicate(s)", editor, ITheorem.ELEMENT_TYPE,
					thmPrefix, thmIndex);
			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			String[] names = dialog.getNewNames();
			String[] contents = dialog.getNewContents();
			AtomicOperation operation = OperationFactory.createTheoremWizard(
					editor, names, contents);
			History.getInstance().addOperation(operation);
			addNewElements(editor, operation);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create an event with its parameters, guards and
	 * actions using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newEvent(final EventBMachineEditor editor,
			IProgressMonitor monitor) {

		final NewEventInputDialog dialog = new NewEventInputDialog(editor,
				Display.getCurrent().getActiveShell(), "New Events");

		dialog.open();

		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		String name = dialog.getLabel();

		String[] paramNames = dialog.getParameters();

		String[] grdNames = dialog.getGrdLabels();
		String[] grdPredicates = dialog.getGrdPredicates();

		String[] actNames = dialog.getActLabels();
		String[] actSubstitutions = dialog.getActSubstitutions();

		newEvent(editor, name, paramNames, grdNames, grdPredicates, actNames,
				actSubstitutions);

	}

	public static void newEvent(IEventBEditor<IMachineFile> editor, String name,
			String[] paramNames, String[] grdNames, String[] grdPredicates,
			String[] actNames, String[] actSubstitutions) {
		AtomicOperation operation = OperationFactory
				.createEvent(editor, name, paramNames, grdNames, grdPredicates,
						actNames, actSubstitutions);
		History.getInstance().addOperation(operation);
		addNewElements(editor, operation);
	}
	

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newCarrierSets(final EventBContextEditor editor,
			IProgressMonitor monitor) {

		final IContextFile ctxFile = editor.getRodinInput();
		try {
			String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
			.getDefaultPrefix("org.eventb.core.carrierSetIdentifier");

			String identifier = UIUtils.getFreeElementIdentifier(editor, ctxFile,
					ICarrierSet.ELEMENT_TYPE, defaultPrefix);
			final ElementAttributeInputDialog dialog = new ElementAttributeInputDialog(
					Display.getCurrent().getActiveShell(), "New Carrier Sets",
					"Identifier", identifier);

			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel
			final Collection<String> attributes = dialog.getAttributes();
			final String[] names = attributes.toArray(new String[attributes.size()]);
			final AtomicOperation operation = OperationFactory.createCarrierSetWizard(editor, names);
			History.getInstance().addOperation(operation);
			addNewElements(editor, operation);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newEnumeratedSet(final EventBContextEditor editor,
			IProgressMonitor monitor) {

		final IContextFile ctxFile = editor.getRodinInput();
		try {
			final String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
					.getDefaultPrefix("org.eventb.core.carrierSetIdentifier");
			String identifier = UIUtils.getFreeElementIdentifier(editor,
					ctxFile, ICarrierSet.ELEMENT_TYPE, defaultPrefix);
			final NewEnumeratedSetInputDialog dialog = new NewEnumeratedSetInputDialog(
					Display.getCurrent().getActiveShell(),
					"New Enumerated Set", identifier);

			dialog.open();
			final String name = dialog.getName();
			final String[] elements = dialog.getElements();
			if (name != null) {
				final AtomicOperation operation = OperationFactory
						.createEnumeratedSetWizard(editor, name, elements);
				History.getInstance().addOperation(operation);
				addNewElements(editor, operation);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create new axioms using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new axioms will be created in
	 */
	public static void newAxioms(final IEventBEditor<IContextFile> editor,
			final IRodinFile rodinFile) {
		try {
			final String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
					.getDefaultPrefix("org.eventb.core.axiomLabel");

			String axmPrefix = UIUtils.getPrefix(editor.getRodinInput(),
					IAxiom.ELEMENT_TYPE, defaultPrefix);
			String axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix);
			final ElementNameContentInputDialog<IAxiom> dialog =
				new ElementNameContentInputDialog<IAxiom>(
					Display.getCurrent().getActiveShell(), "New Axioms",
					"Label(s) and predicate(s)", editor, IAxiom.ELEMENT_TYPE,
					axmPrefix, axmIndex);
			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			String[] names = dialog.getNewNames();
			String[] contents = dialog.getNewContents();
			final AtomicOperation operation = OperationFactory
					.createAxiomWizard(editor, names, contents);
			History.getInstance().addOperation(operation);
			addNewElements(editor, operation);
		} catch (RodinDBException e) {
			// TODO auto-generated catch block
			e.printStackTrace();
		}
	}

	public static IRodinElement getAbstractElement(IRodinElement concreteElement)
			throws RodinDBException {
		IMachineFile rodinFile = (IMachineFile) concreteElement.getOpenable();
		IRodinFile abstractFile = EventBUtils.getAbstractMachine(rodinFile);
		if (abstractFile == null)
			return null;
		if (!abstractFile.exists())
			return null;

		IRodinElement abstractElement = null;
		if (concreteElement instanceof IEvent) {
			IRodinElement[] abs_evts = ((IEvent) concreteElement)
					.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
			if (abs_evts.length == 0) {
				abstractElement = abstractFile.getInternalElement(
						IEvent.ELEMENT_TYPE, ((IEvent) concreteElement)
								.getElementName());
			} else {
				abstractElement = abstractFile.getInternalElement(
						IEvent.ELEMENT_TYPE, ((IRefinesEvent) abs_evts[0])
								.getAbstractEventLabel());
			}
		}
		return abstractElement;
	}

	public static void debug(String message) {
		System.out.println(EventBEditorUtils.DEBUG_PREFIX + message);
	}

	public static void debugAndLogError(Throwable e, String message) {
		if (DEBUG) {
			debug(message);
			e.printStackTrace();
		}
		UIUtils.log(e, message);
	}

	public static String getComments(ICommentedElement element) {
		try {
			return element.getComment();
		} catch (RodinDBException e) {
			return "";
		}
	}
	
	public static String getFreeInitialisationActionName(IEventBEditor<IMachineFile> editor)
			throws RodinDBException {
		IRodinFile rodinFile = editor.getRodinInput();

		IInternalElement initialisation = getInitialisation(rodinFile);

		String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
				.getDefaultPrefix("org.eventb.core.actionLabel");
		if (initialisation == null)
			return UIUtils.getPrefix(editor.getRodinInput(),
					IAction.ELEMENT_TYPE, defaultPrefix) + 1;
		else {
			return UIUtils.getFreeElementLabel(editor, initialisation,
					IAction.ELEMENT_TYPE, defaultPrefix);
		}
	}

	
	static public <T1 extends IRodinFile, T extends IInternalElement> String getFreeChildName(
			IEventBEditor<T1> editor, String defaultPrefix,
			IInternalElementType<T> element_type) throws RodinDBException {
		final String prefix = UIUtils.getNamePrefix(editor.getRodinInput(),
				element_type, defaultPrefix);
		final String index = EventBUtils.getFreeChildNameIndex(editor
				.getRodinInput(), element_type, prefix);
		final String name = prefix + index;
		return name;
	}

	private static void addNewElement(IEventBEditor<?> editor, AtomicOperation op){
		IInternalElement element = op.getCreatedElement();
		if(element != null){
			editor.addNewElement(element);
		}
	}
	private static void addNewElements(IEventBEditor<?> editor, AtomicOperation op){
		for(IInternalElement element:op.getCreatedElements()){
			editor.addNewElement(element);
		}
	}
	
}
