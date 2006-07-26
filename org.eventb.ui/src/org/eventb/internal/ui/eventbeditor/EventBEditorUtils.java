/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.eventbeditor.actions.PrefixAxmName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixInvName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixThmName;
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
	 * Delete selected elements in a tree viewer.
	 * <p>
	 * 
	 * @param viewer
	 *            The current tree viewer in the Event-B Editor.
	 */
	public static void deleteElements(final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					IRodinElement[] elements = new IRodinElement[ssel.size()];
					int i = 0;
					for (Iterator it = ssel.iterator(); it.hasNext(); i++) {
						elements[i] = (IRodinElement) it.next();
					}
					EventBUIPlugin.getRodinDatabase().delete(elements, true,
							null);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
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
	public static void handleUp(final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				Tree tree = viewer.getTree();
				TreeItem[] items = tree.getSelection();
				TreeItem item = items[0];
				TreeItem prev = TreeSupports.findPrevItem(tree, item);
				IRodinElement currObj = (IRodinElement) item.getData();
				IRodinElement prevObj = (IRodinElement) prev.getData();

				try {
					((IInternalElement) currObj).move(currObj.getParent(),
							prevObj, null, true, null);
					TreeItem newItem = TreeSupports.findItem(tree, currObj);
					viewer.setSelection(new StructuredSelection(newItem
							.getData()));
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
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
	public static void handleDown(final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				Tree tree = viewer.getTree();
				TreeItem[] items = tree.getSelection();
				TreeItem item = items[0];
				TreeItem next = TreeSupports.findNextItem(tree, item);
				IRodinElement currObj = (IRodinElement) item.getData();
				IRodinElement nextObj = (IRodinElement) next.getData();

				try {
					((IInternalElement) nextObj).move(nextObj.getParent(),
							currObj, null, false, null);
					TreeItem newItem = TreeSupports.findItem(tree, currObj);
					viewer.setSelection(new StructuredSelection(newItem
							.getData()));
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
				return;
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
	public static void addAction(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						IInternalElement event = TreeSupports.getEvent(obj);
						int counter = 1;
						IRodinElement[] acts = event
								.getChildrenOfType(IAction.ELEMENT_TYPE);
						for (counter = 1; counter <= acts.length; counter++) {
							IInternalElement element = event
									.getInternalElement(IAction.ELEMENT_TYPE,
											"act" + counter);
							if (!element.exists())
								break;
						}
						IInternalElement act = event.createInternalElement(
								IAction.ELEMENT_TYPE, "act" + counter, null,
								null);
						act.setContents(EventBUIPlugin.SUB_DEFAULT);
						editor.addNewElement(act);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, act, 1);
					}
				} catch (RodinDBException e) {
					e.printStackTrace();
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
	public static void addGuard(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						IInternalElement event = TreeSupports.getEvent(obj);
						int counter = 1;
						IRodinElement[] grds = event
								.getChildrenOfType(IGuard.ELEMENT_TYPE);
						for (counter = 1; counter <= grds.length; counter++) {
							IInternalElement element = event
									.getInternalElement(IGuard.ELEMENT_TYPE,
											"grd" + counter);
							if (!element.exists())
								break;
						}
						IInternalElement grd = event.createInternalElement(
								IGuard.ELEMENT_TYPE, "grd" + counter, null,
								null);
						grd.setContents(EventBUIPlugin.GRD_DEFAULT);
						editor.addNewElement(grd);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, grd, 1);
					}
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Add a new local variable.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addLocalVariable(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						IInternalElement event = TreeSupports.getEvent(obj);
						int counter = 1;
						IRodinElement[] vars = event
								.getChildrenOfType(IVariable.ELEMENT_TYPE);
						for (counter = 1; counter <= vars.length; counter++) {
							IInternalElement element = event
									.getInternalElement(IVariable.ELEMENT_TYPE,
											"var" + counter);
							if (!element.exists())
								break;
						}
						IInternalElement var = event.createInternalElement(
								IVariable.ELEMENT_TYPE, "var" + counter, null,
								null);
						editor.addNewElement(var);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, var, 0);
					}
				} catch (RodinDBException e) {
					e.printStackTrace();
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
	public static void addVariable(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] vars = rodinFile
							.getChildrenOfType(IVariable.ELEMENT_TYPE);
					for (counter = 1; counter <= vars.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(IVariable.ELEMENT_TYPE,
										"var" + counter);
						if (!element.exists())
							break;
					}
					IRodinElement var = rodinFile
							.createInternalElement(IVariable.ELEMENT_TYPE,
									"var" + counter, null, null);
					editor.addNewElement(var);
					((EventBEditableTreeViewer) viewer).edit(var);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
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
	public static void addInvariant(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] invs = rodinFile
							.getChildrenOfType(IInvariant.ELEMENT_TYPE);
					String invPrefix = getPrefix(editor,
							PrefixInvName.QUALIFIED_NAME,
							PrefixInvName.DEFAULT_PREFIX);
					for (counter = 1; counter <= invs.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(IInvariant.ELEMENT_TYPE,
										invPrefix + counter);
						if (!element.exists())
							break;
					}
					IInternalElement inv = rodinFile.createInternalElement(
							IInvariant.ELEMENT_TYPE, invPrefix + counter, null,
							null);
					inv.setContents(EventBUIPlugin.INV_DEFAULT);
					editor.addNewElement(inv);
					((EventBEditableTreeViewer) viewer).edit(inv);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		});
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
	public static void addTheorem(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] thms = rodinFile
							.getChildrenOfType(ITheorem.ELEMENT_TYPE);
					String thmPrefix = getPrefix(editor,
							PrefixThmName.QUALIFIED_NAME,
							PrefixThmName.DEFAULT_PREFIX);
					for (counter = 1; counter <= thms.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(ITheorem.ELEMENT_TYPE,
										thmPrefix + counter);
						if (!element.exists())
							break;
					}
					IInternalElement thm = rodinFile.createInternalElement(
							ITheorem.ELEMENT_TYPE, thmPrefix + counter, null,
							null);
					thm.setContents(EventBUIPlugin.THM_DEFAULT);
					editor.addNewElement(thm);
					((EventBEditableTreeViewer) viewer).edit(thm);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		});
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
	public static void addEvent(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] vars = rodinFile
							.getChildrenOfType(IEvent.ELEMENT_TYPE);
					for (counter = 1; counter <= vars.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(IEvent.ELEMENT_TYPE, "evt"
										+ counter);
						if (!element.exists())
							break;
					}
					String[] varNames = { "var1", "var2", "var3" };
					String[] grdNames = { "grd1", "grd2", "grd3" };
					String[] actions = { "act1", "act2", "act3" };

					IInternalElement event = rodinFile.createInternalElement(
							IEvent.ELEMENT_TYPE, "evt" + counter, null, null);
					editor.addNewElement(event);
					for (String varName : varNames) {
						IInternalElement var = event.createInternalElement(
								IVariable.ELEMENT_TYPE, varName, null, null);
						editor.addNewElement(var);
					}

					for (int i = 0; i < grdNames.length; i++) {
						IInternalElement grd = event.createInternalElement(
								IGuard.ELEMENT_TYPE, grdNames[i], null, null);
						grd.setContents(EventBUIPlugin.GRD_DEFAULT);
						editor.addNewElement(grd);
					}

					IInternalElement act = null;
					for (String action : actions) {
						act = event.createInternalElement(IAction.ELEMENT_TYPE,
								action, null, null);
						act.setContents(EventBUIPlugin.SUB_DEFAULT);
						editor.addNewElement(act);
					}
					viewer.setExpandedState(event, true);
					viewer.reveal(act);
					((EventBEditableTreeViewer) viewer).edit(event);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		});
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
	public static void addAxiom(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] vars = rodinFile
							.getChildrenOfType(IAxiom.ELEMENT_TYPE);
					String axmPrefix = getPrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					for (counter = 1; counter <= vars.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(IAxiom.ELEMENT_TYPE,
										axmPrefix + counter);
						if (!element.exists())
							break;
					}
					IInternalElement axm = rodinFile.createInternalElement(
							IAxiom.ELEMENT_TYPE, axmPrefix + counter, null,
							null);
					axm.setContents(EventBUIPlugin.AXM_DEFAULT);
					editor.addNewElement(axm);
					((EventBEditableTreeViewer) viewer).edit(axm);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		});
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
	public static void addConstant(final EventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] vars = rodinFile
							.getChildrenOfType(IConstant.ELEMENT_TYPE);
					for (counter = 1; counter <= vars.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(IConstant.ELEMENT_TYPE,
										"cst" + counter);
						if (!element.exists())
							break;
					}
					IInternalElement cst = rodinFile
							.createInternalElement(IConstant.ELEMENT_TYPE,
									"cst" + counter, null, null);
					editor.addNewElement(cst);
					((EventBEditableTreeViewer) viewer).edit(cst);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
		});
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
	public static void addSet(final EventBEditor editor, final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					int counter = 1;
					IRodinElement[] vars = rodinFile
							.getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
					for (counter = 1; counter <= vars.length; counter++) {
						IInternalElement element = rodinFile
								.getInternalElement(ICarrierSet.ELEMENT_TYPE,
										"set" + counter);
						if (!element.exists())
							break;
					}
					IRodinElement set = rodinFile.createInternalElement(
							ICarrierSet.ELEMENT_TYPE, "set" + counter, null,
							null);
					editor.addNewElement(set);
					((EventBEditableTreeViewer) viewer).edit(set);
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
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
	private static void select(EventBEditableTreeViewer viewer, Object obj,
			int column) throws RodinDBException {
		TreeItem item = TreeSupports.findItem(viewer.getTree(),
				(IRodinElement) obj);
		viewer.reveal(item.getData());

		// try to select the column to edit element
		viewer.selectItem(item, column);
	}

	/**
	 * Utility method to create a variable with its type invariant and
	 * initialisation using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the variable and its invariant,
	 *            initialisation will be created in
	 */
	public static void intelligentNewVariables(EventBEditor editor,
			IRodinFile rodinFile) {
		try {

			int counter = 1;
			IRodinElement[] vars = rodinFile
					.getChildrenOfType(IVariable.ELEMENT_TYPE);
			for (counter = 1; counter <= vars.length; counter++) {
				IInternalElement element = rodinFile.getInternalElement(
						IVariable.ELEMENT_TYPE, "var" + counter);
				if (!element.exists())
					break;
			}
			int invCounter = 1;

			IRodinElement[] invs = rodinFile
					.getChildrenOfType(IInvariant.ELEMENT_TYPE);
			String invPrefix = getPrefix(editor, PrefixInvName.QUALIFIED_NAME,
					PrefixInvName.DEFAULT_PREFIX);
			for (invCounter = 1; invCounter <= invs.length; invCounter++) {
				IInternalElement element = rodinFile.getInternalElement(
						IInvariant.ELEMENT_TYPE, invPrefix + invCounter);
				if (!element.exists())
					break;
			}

			IInternalElement initialisation = getInitialisation(rodinFile);

			String defaultInitName = "";
			if (initialisation == null)
				defaultInitName = "act1";
			else {
				IRodinElement[] acts = initialisation
						.getChildrenOfType(IAction.ELEMENT_TYPE);
				int i;
				for (i = 1; i <= acts.length; i++) {
					IInternalElement element = initialisation
							.getInternalElement(IAction.ELEMENT_TYPE, "act" + i);
					if (!element.exists())
						break;
				}
				defaultInitName = "act" + i;
			}
			IntelligentNewVariableInputDialog dialog = new IntelligentNewVariableInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Variable", "var" + counter, invCounter,
					defaultInitName);

			dialog.open();
			String name = dialog.getName();
			String init = dialog.getInitSubstitution();
			boolean newInit = true;

			if (name != null) {
				IInternalElement var = rodinFile.createInternalElement(
						IVariable.ELEMENT_TYPE, name, null, null);
				editor.addNewElement(var);

				Collection<Pair> invariants = dialog.getInvariants();
				if (invariants != null) {
					for (Pair pair : invariants) {
						IInternalElement inv = rodinFile.createInternalElement(

						IInvariant.ELEMENT_TYPE, (String) pair.getFirst(),
								null, null);
						inv.setContents((String) pair.getSecond());
						editor.addNewElement(inv);
					}
				}

				if (init != null) {
					IRodinElement[] events = rodinFile
							.getChildrenOfType(IEvent.ELEMENT_TYPE);
					for (IRodinElement event : events) {
						IInternalElement element = (IInternalElement) event;
						if (event.getElementName().equals("INITIALISATION")) {
							newInit = false;
							int j = 1;
							IRodinElement[] acts = element
									.getChildrenOfType(IAction.ELEMENT_TYPE);
							for (j = 1; j <= acts.length; j++) {
								IInternalElement tmp = element
										.getInternalElement(
												IAction.ELEMENT_TYPE, "act" + j);
								if (!tmp.exists())
									break;
							}
							IInternalElement act = element
									.createInternalElement(
											IAction.ELEMENT_TYPE, "act" + j,
											null, null);
							act.setContents(init);

							editor.addNewElement(act);
						}
					}
					if (newInit) {
						IInternalElement event = rodinFile
								.createInternalElement(IEvent.ELEMENT_TYPE,
										"INITIALISATION", null, null);
						IInternalElement act = (IInternalElement) event
								.createInternalElement(IAction.ELEMENT_TYPE,
										"act1", null, null);
						act.setContents(init);
						editor.addNewElement(act);
					}
				}
			}

		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private static IInternalElement getInitialisation(IRodinFile rodinFile)
			throws RodinDBException {
		IRodinElement[] events = rodinFile
				.getChildrenOfType(IEvent.ELEMENT_TYPE);
		for (IRodinElement event : events) {
			IInternalElement element = (IInternalElement) event;
			if (event.getElementName().equals("INITIALISATION")) {
				return element;
			}
		}
		return null;
	}

	public static String getPrefix(EventBEditor editor,
			QualifiedName qualifiedName, String defaultPrefix) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					qualifiedName);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (prefix == null)
			prefix = defaultPrefix;
		return prefix;
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
	public static void newInvariants(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE).length;
			String invPrefix = getPrefix(editor, PrefixInvName.QUALIFIED_NAME,
					PrefixInvName.DEFAULT_PREFIX);
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(
					Display.getCurrent().getActiveShell(), "New Invariants",
					"Name and predicate", invPrefix, counter + 1);
			dialog.open();
			String[] names = dialog.getNewNames();
			String[] contents = dialog.getNewContents();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String content = contents[i];
				IInternalElement inv = rodinFile.createInternalElement(
						IInvariant.ELEMENT_TYPE, name, null, null);
				inv.setContents(content);
				editor.addNewElement(inv);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
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
	public static void newTheorems(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE).length;
			String thmPrefix = getPrefix(editor, PrefixThmName.QUALIFIED_NAME,
					PrefixThmName.DEFAULT_PREFIX);
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(
					Display.getCurrent().getActiveShell(), "New Theorems",
					"Name and predicate", thmPrefix, counter + 1);
			dialog.open();
			String[] names = dialog.getNewNames();
			String[] contents = dialog.getNewContents();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String content = contents[i];
				IInternalElement thm = rodinFile.createInternalElement(
						ITheorem.ELEMENT_TYPE, name, null, null);
				thm.setContents(content);
				editor.addNewElement(thm);
			}
		} catch (RodinDBException e) {
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
	public static void newAxioms(EventBEditor editor, IRodinFile rodinFile) {
		try {
			int counter = rodinFile.getChildrenOfType(IAxiom.ELEMENT_TYPE).length;
			String axmPrefix = getPrefix(editor, PrefixAxmName.QUALIFIED_NAME,
					PrefixAxmName.DEFAULT_PREFIX);
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(
					Display.getCurrent().getActiveShell(), "New Axioms",
					"Name and predicate", axmPrefix, counter + 1);
			dialog.open();
			String[] names = dialog.getNewNames();
			String[] contents = dialog.getNewContents();
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String content = contents[i];
				IInternalElement axm = rodinFile.createInternalElement(
						IAxiom.ELEMENT_TYPE, name, null, null);
				axm.setContents(content);
				editor.addNewElement(axm);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
}
