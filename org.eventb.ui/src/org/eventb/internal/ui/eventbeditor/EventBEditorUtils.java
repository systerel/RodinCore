/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed axiom form for enumerated sets
 *     Systerel - replaced inherited by extended, local variable by parameter
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - added getChildTowards
 *     Systerel - theorems almost everywhere
 *     Systerel - added changeFocusWhenDispose
 *     Systerel - added checkAndShowReadOnly
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;
import static org.eventb.internal.ui.UIUtils.showInfo;
import static org.eventb.internal.ui.utils.Messages.bind;
import static org.eventb.internal.ui.utils.Messages.dialogs_readOnlyElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.dialogs.NewCarrierSetDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewConstantDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewDerivedPredicateDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewEnumeratedSetDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewEventDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewVariableDialog;
import org.eventb.internal.ui.eventbeditor.dialogs.NewVariantDialog;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
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
	
	static IRefinesEvent newRefEvt;

	static IParameter newParam;
	
	static IGuard newGrd;
	
	static IWitness newWit;
	
	static IAction newAct;
	
	static IRefinesMachine newRefMch;
	
	static ISeesContext newSeeCtx;

	static IVariable newVar;

	static IInvariant newInv;
	
	static IEvent newEvt;

	static IVariant newVariant;

	static IExtendsContext newExtCtx;

	static ICarrierSet newSet;

	static IConstant newCst;
	
	static IAxiom newAxm;


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
						.deleteElement(elements, true);
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
	 * return the first selected item
	 */
	private static  TreeItem getCurrentItem(Tree tree) {
		TreeItem[] items = tree.getSelection();
		return items[0];
	}

	private static  IInternalElement getElement(TreeItem item) {
		if (item == null)
			return null;
		return (IInternalElement) item.getData();
	}

	private static boolean equalsType(TreeItem leftItem, TreeItem rightItem) {
		final IInternalElement left = getElement(leftItem);
		final IInternalElement right = getElement(rightItem);
		if (left == null || right == null) {
			return false;
		}
		return left.getElementType() == right.getElementType();
	}

	/**
	 * Return the previous element of item with the same type or
	 * <code>null</code> if there isn't
	 */
	private static  IInternalElement getPreviousElement(Tree tree, TreeItem item) {
		final TreeItem prevItem = TreeSupports.findPrevItem(tree, item);
		if (equalsType(prevItem, item)) {
			return getElement(prevItem);
		}
		return null;
	}

	/**
	 * Return the next element of item with the same type or <code>null</code>
	 * if there isn't
	 */
	private static IInternalElement getNextElement(Tree tree, TreeItem item) {
		final TreeItem nextItem = TreeSupports.findNextItem(tree, item);
		if (equalsType(nextItem, item)) {
			return getElement(nextItem);
		}
		return null;
	}
	
	/**
	 * @param up
	 *            if <code>up</code> is true do handleUp else do handleDown
	 */
	public static void handleGeneric(IEventBEditor<?> editor,
			final TreeViewer viewer, boolean up) {
		final Tree tree = viewer.getTree();
		final TreeItem currentItem = getCurrentItem(tree);
		final IInternalElement current = getElement(currentItem);
		final IInternalElement previous = getPreviousElement(tree, currentItem);
		final IInternalElement next = getNextElement(tree, currentItem);

		handle(up, current, previous, next);
	}

	/**
	 * Move up or down the elements between first (include) and next (not include). 
	 * 
	 * @param up
	 *            if <code>up</code> is true do handleUp else do handleDown
	 *            @param first the first element to move
	 * */
	public static void handle(boolean up, IInternalElement first,
			IInternalElement previous, IInternalElement next) {

		// if up, we move the previous element before the next
		// if down, we move the next element before the selected
		final IInternalElement movedElement = (up) ? previous : next;
		final IInternalElement nextSibling = (up) ? next : first;

		if (movedElement == null)
			return;

		final IInternalElement newParent = (IInternalElement) movedElement
				.getParent();

		AtomicOperation operation = OperationFactory.move(first.getRoot(),
				movedElement, newParent, nextSibling);
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
	public static void addAction(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation operation = OperationFactory.createAction(
							event, null, EventBUIPlugin.SUB_DEFAULT, null);
					History.getInstance().addOperation(operation);
					displayInSynthesis(viewer, event, operation
							.getCreatedElement());
				}
			}
		});
	}

	/**
	 * Add a refines event clause.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addRefinesEvent(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					String abs_name;
					try {
						abs_name = ((IEvent) event).getLabel();
						AtomicOperation op = OperationFactory.createElement(
								event, IRefinesEvent.ELEMENT_TYPE,
								EventBAttributes.TARGET_ATTRIBUTE, abs_name);
						History.getInstance().addOperation(op);
						displayInSynthesis(viewer, event, op
								.getCreatedElement());
					} catch (RodinDBException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Add a new witness.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addWitness(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation op = OperationFactory.createElement(event,
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
	public static void addGuard(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation operation = OperationFactory.createGuard(
							event, null, EventBUIPlugin.GRD_DEFAULT, null);
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
	public static void addParameter(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				final IInternalElement event = getEvent(viewer);
				if (event != null) {
					AtomicOperation operation = OperationFactory
							.createElementGeneric(event,
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
	public static void addVariable(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				AtomicOperation op = OperationFactory.createElementGeneric(
						editor.getRodinInput(), IVariable.ELEMENT_TYPE,
						null);
				addOperationToHistory(op, editor, viewer);
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
	public static void addInvariant(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createInvariantWizard(editor
				.getRodinInput(), null, EventBUIPlugin.INV_DEFAULT, false);
		addOperationToHistory(op, editor, viewer);
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
	public static void addEvent(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {

		final String name = null ;
		
		final String[] varNames = defaultArray(3, null);
		final String[] grdNames = defaultArray(3, null);
		final String[] grdPredicates = defaultArray(3,
				EventBUIPlugin.PRD_DEFAULT);
		final String[] actNames = defaultArray(3, null);
		final String[] actSubstitutions = defaultArray(3,
				EventBUIPlugin.SUB_DEFAULT);
		final AtomicOperation op = OperationFactory.createEvent(editor
				.getRodinInput(), name, varNames, grdNames, grdPredicates,
				actNames, actSubstitutions);
		History.getInstance().addOperation(op);
		IInternalElement event = op.getCreatedElement();
		displayInSynthesis(viewer, event, event);
		editElement(viewer, op);
	}

	/**
	 * Add a new variant.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addVariant(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createVariantWizard(editor
				.getRodinInput(), "");
		addOperationToHistory(op, editor, viewer);
	}

	/**
	 * Add a refines machine clause.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addRefinesMachine(
			final IEventBEditor<IMachineRoot> editor, final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				AtomicOperation op = OperationFactory.createElementGeneric(
						editor.getRodinInput(), IRefinesMachine.ELEMENT_TYPE,
						null);
				History.getInstance().addOperation(op);
				IInternalElement ref = op.getCreatedElement();
				displayInSynthesis(viewer, ref, ref);
			}
		});
	}

	/**
	 * Add a sees context clause.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addSeesContext(final IEventBEditor<IMachineRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				AtomicOperation op = OperationFactory
						.createElementGeneric(editor.getRodinInput(),
								ISeesContext.ELEMENT_TYPE, null);
				History.getInstance().addOperation(op);
				IInternalElement ref = op.getCreatedElement();
				displayInSynthesis(viewer, ref, ref);
			}
		});
	}

	private static void displayInSynthesis(final TreeViewer viewer,
			IInternalElement expanded, IInternalElement selected) {
		viewer.setExpandedState(TreeSupports.findItem(viewer.getTree(),
				expanded).getData(), true);
		try {
			select((EventBEditableTreeViewer) viewer, selected,
					IElementDescRegistry.Column.LABEL.getId());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String[] defaultArray(int size, String defaultString) {
		assert size > 0;
		final String[] result = new String[size];
		Arrays.fill(result, defaultString);
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
	public static void addAxiom(final IEventBEditor<IContextRoot> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createAxiomWizard(editor
				.getRodinInput(), null, EventBUIPlugin.AXM_DEFAULT, false);
		addOperationToHistory(op, editor, viewer);
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
	public static void addConstant(final IEventBEditor<IContextRoot> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createElementGeneric(editor
				.getRodinInput(), IConstant.ELEMENT_TYPE, null);
		addOperationToHistory(op, editor, viewer);
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
	public static void addSet(final IEventBEditor<IContextRoot> editor,
			final TreeViewer viewer) {
		AtomicOperation op = OperationFactory.createElementGeneric(editor
				.getRodinInput(), ICarrierSet.ELEMENT_TYPE, null);
		addOperationToHistory(op, editor, viewer);
	}
	
	/**
	 * Add an extends context clause.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addExtendsContext(final IEventBEditor<IContextRoot> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			@SuppressWarnings("synthetic-access")
			public void run() {
				AtomicOperation op = OperationFactory
						.createElementGeneric(editor.getRodinInput(),
								IExtendsContext.ELEMENT_TYPE, null);
				History.getInstance().addOperation(op);
				IInternalElement ref = op.getCreatedElement();
				displayInSynthesis(viewer, ref, ref);
			}
		});
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
		if (item == null)
			return;

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
	 * @param root
	 *            the root element that the variable and its invariant,
	 *            initialization will be created in
	 */
	public static void intelligentNewVariable(final IEventBEditor<IMachineRoot> editor,
			final IMachineRoot root) {

		final String prefix = UIUtils.getAutoNamePrefix(editor.getRodinInput(),
				IInvariant.ELEMENT_TYPE);

		final NewVariableDialog dialog = new NewVariableDialog(
				editor, Display.getCurrent().getActiveShell(), "New Variable",
				prefix);

		dialog.open();

		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String varName = dialog.getName();
		final Collection<Pair<String, String>> invariant = dialog
				.getInvariants();
		final String actName = dialog.getInitActionName();
		final String actSub = dialog.getInitActionSubstitution();
		final AtomicOperation operation = OperationFactory
				.createVariableWizard(editor.getRodinInput(), varName,
						invariant, actName, actSub);
		addOperationToHistory(operation, editor);
	}

	public static void newVariable(IEventBEditor<IMachineRoot> editor, String varName,
			final Collection<Pair<String, String>> invariant, String actName,
			String actSub) {
		final AtomicOperation operation = OperationFactory
				.createVariableWizard(editor.getRodinInput(), varName,
						invariant, actName, actSub);
		addOperationToHistory(operation, editor);
	}
	
	/**
	 * Utility method to create a constant with its type axiom using a modal
	 * dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void intelligentNewConstant(
			final IEventBEditor<IContextRoot> editor) {

		final NewConstantDialog dialog = new NewConstantDialog(
				editor, Display.getCurrent().getActiveShell(), "New Constant");

		dialog.open();

		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String identifier = dialog.getIdentifier();
		final String[] axmNames = dialog.getAxiomNames();
		final String[] axmSubs = dialog.getAxiomPredicates();
		newConstant(editor, identifier, axmNames, axmSubs);
	}
	
	public static void newConstant(IEventBEditor<IContextRoot> editor, String identifier,
			String[] axmNames, String[] axmSubs) {
		AtomicOperation operation = OperationFactory.createConstantWizard(
				editor.getRodinInput(), identifier, axmNames, axmSubs);
		History.getInstance().addOperation(operation);
		addNewElements(editor, operation);
	}

	public static IEvent getInitialisation(IMachineRoot root)
			throws RodinDBException {
		final IRodinElement[] events = root
				.getChildrenOfType(IEvent.ELEMENT_TYPE);
		for (IRodinElement element : events) {
			final IEvent event = (IEvent) element;
			if (event.getLabel().equals(IEvent.INITIALISATION)) {
				return event;
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
	 */
	public static void newInvariants(final IEventBEditor<IMachineRoot> editor) {
		final IMachineRoot root = editor.getRodinInput();
		final NewDerivedPredicateDialog<IInvariant> dialog = new NewDerivedPredicateDialog<IInvariant>(
				Display.getCurrent().getActiveShell(), "New Invariants", root,
				IInvariant.ELEMENT_TYPE);

		dialog.open();

		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String[] names = dialog.getNewNames();
		final String[] contents = dialog.getNewContents();
		final boolean[] isTheorem = dialog.getIsTheorem();
		final AtomicOperation operation = OperationFactory
				.createInvariantWizard(root, names, contents, isTheorem);
		addOperationToHistory(operation, editor);
	}

	/**
	 * Utility method to create a new variant using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newVariant(final IEventBEditor<IMachineRoot> editor) {
		final NewVariantDialog dialog = new NewVariantDialog(Display
				.getCurrent().getActiveShell(), "New Variant", "Expression");
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String expression = dialog.getExpression();
		final AtomicOperation operation = OperationFactory.createVariantWizard(
				editor.getRodinInput(), expression);
		addOperationToHistory(operation, editor);
	}

	/**
	 * Utility method to create an event with its parameters, guards and
	 * actions using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newEvent(IEventBEditor<IMachineRoot> editor,
			IProgressMonitor monitor) {

		final NewEventDialog dialog = new NewEventDialog(editor,
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

	public static void newEvent(IEventBEditor<IMachineRoot> editor, String name,
			String[] paramNames, String[] grdNames, String[] grdPredicates,
			String[] actNames, String[] actSubstitutions) {
		AtomicOperation operation = OperationFactory.createEvent(editor
				.getRodinInput(), name, paramNames, grdNames, grdPredicates,
				actNames, actSubstitutions);
		addOperationToHistory(operation, editor);
	}
	

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newCarrierSets(IEventBEditor<IContextRoot> editor,
			IProgressMonitor monitor) {

		final IContextRoot ctxRoot = editor.getRodinInput();
		final String identifier = UIUtils.getFreeElementIdentifier(ctxRoot,
				ICarrierSet.ELEMENT_TYPE);
		final NewCarrierSetDialog dialog = new NewCarrierSetDialog(
				Display.getCurrent().getActiveShell(), "New Carrier Sets",
				"Identifier", identifier);

		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel
		final Collection<String> attributes = dialog.getNames();
		final String[] names = attributes
				.toArray(new String[attributes.size()]);
		final AtomicOperation operation = OperationFactory
				.createCarrierSetWizard(editor.getRodinInput(), names);
		addOperationToHistory(operation, editor);
	}

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newEnumeratedSet(IEventBEditor<IContextRoot> editor,
			IProgressMonitor monitor) {
		final IContextRoot ctxRoot = editor.getRodinInput();
		final String identifier = UIUtils.getFreeElementIdentifier(ctxRoot,
				ICarrierSet.ELEMENT_TYPE);
		final NewEnumeratedSetDialog dialog = new NewEnumeratedSetDialog(
				Display.getCurrent().getActiveShell(), "New Enumerated Set",
				identifier);

		dialog.open();
		final String name = dialog.getName();
		final String[] elements = dialog.getElements();
		if (name != null) {
			final AtomicOperation operation = OperationFactory
					.createEnumeratedSetWizard(editor.getRodinInput(), name,
							elements);
			addOperationToHistory(operation, editor);
		}
	}

	/**
	 * Utility method to create new axioms using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newAxioms(final IEventBEditor<IContextRoot> editor) {
		final IContextRoot root = editor.getRodinInput();
		final NewDerivedPredicateDialog<IAxiom> dialog = new NewDerivedPredicateDialog<IAxiom>(
				Display.getCurrent().getActiveShell(), "New Axioms", root,
				IAxiom.ELEMENT_TYPE);
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel

		final String[] names = dialog.getNewNames();
		final String[] contents = dialog.getNewContents();
		final boolean[] isTheorem = dialog.getIsTheorem();
		final AtomicOperation operation = OperationFactory.createAxiomWizard(
				root, names, contents, isTheorem);
		addOperationToHistory(operation, editor);
	}

	public static IRodinElement getAbstractElement(IRodinElement concreteElement)
			throws RodinDBException {
		IRodinFile rodinFile = (IRodinFile) concreteElement.getOpenable();
		IMachineRoot root = (IMachineRoot) rodinFile.getRoot();
		IMachineRoot abstractRoot = EventBUtils.getAbstractMachine(root);
		if (abstractRoot == null)
			return null;
		if (!abstractRoot.exists())
			return null;

		IRodinElement abstractElement = null;
		if (concreteElement instanceof IEvent) {
			IRodinElement[] abs_evts = ((IEvent) concreteElement)
					.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
			if (abs_evts.length == 0) {
				abstractElement = abstractRoot.getInternalElement(
						IEvent.ELEMENT_TYPE, ((IEvent) concreteElement)
								.getElementName());
			} else {
				abstractElement = abstractRoot.getInternalElement(
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
			if (element.hasComment()) {
				return element.getComment();
			}
			return "";
		} catch (RodinDBException e) {
			return "";
		}
	}
	
	public static String getFreeInitialisationActionName(IMachineRoot root) {
		try {
			final IInternalElement initialisation = getInitialisation(root);
			if (initialisation != null)
				return UIUtils.getFreeElementLabel(initialisation,
						IAction.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return UIUtils.getAutoNamePrefix(root, IAction.ELEMENT_TYPE) + 1;
	}

	private static void addNewElement(IEventBEditor<?> editor,
			AtomicOperation op) {
		IInternalElement element = op.getCreatedElement();
		if (element != null) {
			editor.addNewElement(element);
		}
	}

	private static void addNewElements(IEventBEditor<?> editor,
			AtomicOperation op) {
		for (IInternalElement element : op.getCreatedElements()) {
			editor.addNewElement(element);
		}
	}


	/**
	 * Add the operation to the history and add the new element to the editor
	 * */
	private static void addOperationToHistory(AtomicOperation op,
			IEventBEditor<?> editor) {
		History.getInstance().addOperation(op);
		addNewElement(editor, op);
	}

	/**
	 * Add the operation to the history, add the new element to the editor and
	 * set the focus on the new element
	 * */
	private static void addOperationToHistory(AtomicOperation op,
			IEventBEditor<?> editor, TreeViewer viewer) {
		addOperationToHistory(op, editor);
		editElement(viewer, op);
	}

	/** to set the focus on the edit field of the created element */
	private static void editElement(TreeViewer viewer, AtomicOperation op) {
		IInternalElement element = op.getCreatedElement();
		if (element != null) {
			((EventBEditableTreeViewer) viewer).edit(element);
		}
	}

	/**
	 * Returns the child of <code>source</code> which is an ancestor of
	 * <code>target</code>. The returned element is thus the element directly
	 * following <code>source</code> in a path going to <code>target</code>.
	 * <p>
	 * In particular, if <code>source</code> is the parent of
	 * <code>target</code>, returns <code>target</code>, if <code>source</code>
	 * is the grand-parent of <code>target</code>, returns the parent of
	 * <code>target</code>, and so on.
	 * </p>
	 * <p>
	 * If <code>source</code> is not an ancestor of <code>target</code>, returns
	 * <code>null</code> as there is no path from <code>source </code> to
	 * <code>target</code>.
	 * </p>
	 * 
	 * @param source
	 *            the source element
	 * @param target
	 *            the target element
	 * @return the child of <code>source</code> which is an ancestor of
	 *         <code>target</code> or <code>null</code> if none
	 */
	public static IRodinElement getChildTowards(IRodinElement source,
			IRodinElement target) {
		IRodinElement current = target;
		while (current != null && !source.equals(current.getParent())) {
			current = current.getParent();
		}
		return current;
	}

	/**
	 * Adds a dispose listener that gives the focus to another composite.
	 * 
	 * @param disposed
	 *            the composite to be listened to
	 * @param parent
	 *            the composite that will receive the focus
	 * */
	public static void changeFocusWhenDispose(Composite disposed,
			Composite parent) {
		disposed.addDisposeListener(new EditDisposeListener(parent));
	}

	static class EditDisposeListener implements DisposeListener {

		private final Composite parent;

		public EditDisposeListener(Composite parent) {
			this.parent = parent;
		}

		public void widgetDisposed(DisposeEvent e) {
			parent.setFocus();
		}
	}

	/**
	 * Returns whether the given element is read only. Additionally, if the
	 * given element is read only, this method informs the user through an info
	 * window.
	 * 
	 * @param element
	 *            an element to check
	 * @return true iff the given element is read only
	 */
	public static boolean checkAndShowReadOnly(IRodinElement element) {
		if (!(element instanceof IInternalElement)) {
			return false;
		}
		final boolean readOnly = isReadOnly((IInternalElement) element);
		if (readOnly) {
			final String displayName = getDisplayName(element);
			showInfo(bind(dialogs_readOnlyElement, displayName));
		}
		return readOnly;
	}

	private static String getDisplayName(IRodinElement element) {
		try {
			if(element instanceof ILabeledElement) {
				return ((ILabeledElement)element).getLabel();
			} else if (element instanceof IIdentifierElement) {
				return ((IIdentifierElement)element).getIdentifierString();
			} else if (element instanceof IEventBRoot) {
				return element.getElementName();
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "when checking for read-only element");
		}
		return "";
	}
}
