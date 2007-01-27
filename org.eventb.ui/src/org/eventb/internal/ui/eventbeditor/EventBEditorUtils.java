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

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eventb.core.IContextFile;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixActName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixAxmName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixCstName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixEvtName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixGrdName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixInvName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixRefinesEventName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixSetName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixThmName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVarName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVariantName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixWitName;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides some common methods that are used in Event-B
 *         Editors.
 */
public class EventBEditorUtils {

	public static boolean DEBUG = false;

	public final static String DEBUG_PREFIX = "*** EventBEditor *** ";

	static IAction newAct;

	static IGuard newGrd;

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
	public static void addAction(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						final IInternalElement event = TreeSupports
								.getEvent(obj);

						RodinCore.run(new IWorkspaceRunnable() {

							public void run(IProgressMonitor monitor)
									throws CoreException {
								String name = UIUtils.getFreeElementName(
										editor, event, IAction.ELEMENT_TYPE,
										PrefixActName.QUALIFIED_NAME,
										PrefixActName.DEFAULT_PREFIX);
								String label = UIUtils.getFreeElementLabel(
										editor, event, IAction.ELEMENT_TYPE,
										PrefixActName.QUALIFIED_NAME,
										PrefixActName.DEFAULT_PREFIX);
								newAct = event.getInternalElement(
										IAction.ELEMENT_TYPE, name);
								assert !newAct.exists();
								newAct.create(null, monitor);
								newAct.setLabel(label, monitor);
								newAct.setAssignmentString(
										EventBUIPlugin.SUB_DEFAULT,
										new NullProgressMonitor());
								editor.addNewElement(newAct);
							}

						}, null);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, newAct, 1);
					}
				} catch (CoreException e) {
					e.printStackTrace();
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
	public static void addRefinesEvent(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						final IInternalElement event = TreeSupports
								.getEvent(obj);

						RodinCore.run(new IWorkspaceRunnable() {

							public void run(IProgressMonitor monitor)
									throws CoreException {
								String name = UIUtils.getFreeElementName(
										editor, event,
										IRefinesEvent.ELEMENT_TYPE,
										PrefixRefinesEventName.QUALIFIED_NAME,
										PrefixRefinesEventName.DEFAULT_PREFIX);
								String abs_name = ((IEvent) event).getLabel();
								newRefEvt = event.getInternalElement(
												IRefinesEvent.ELEMENT_TYPE,
												name);
								assert !newRefEvt.exists();
								newRefEvt.create(null, monitor);
								newRefEvt.setAbstractEventLabel(abs_name, null);
								editor.addNewElement(newRefEvt);
							}

						}, null);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, newRefEvt, 0);
					}
				} catch (CoreException e) {
					e.printStackTrace();
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
	public static void addWitness(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						final IInternalElement event = TreeSupports
								.getEvent(obj);

						RodinCore.run(new IWorkspaceRunnable() {

							public void run(IProgressMonitor monitor)
									throws CoreException {
								String name = UIUtils.getFreeElementName(
										editor, event, IWitness.ELEMENT_TYPE,
										PrefixWitName.QUALIFIED_NAME,
										PrefixWitName.DEFAULT_PREFIX);
								String label = UIUtils.getFreeElementLabel(
										editor, event, IWitness.ELEMENT_TYPE,
										PrefixWitName.QUALIFIED_NAME,
										PrefixWitName.DEFAULT_PREFIX);
								newWit = event.getInternalElement(
										IWitness.ELEMENT_TYPE, name);
								assert !newWit.exists();
								newWit.create(null, monitor);
								newWit.setLabel(label, monitor);
								newWit.setPredicateString(
										EventBUIPlugin.PRD_DEFAULT, null);
								editor.addNewElement(newWit);
							}

						}, null);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, newWit, 0);
					}
				} catch (CoreException e) {
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
	public static void addGuard(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						final IInternalElement event = TreeSupports
								.getEvent(obj);

						RodinCore.run(new IWorkspaceRunnable() {

							public void run(IProgressMonitor monitor)
									throws CoreException {
								String label = UIUtils.getFreeElementLabel(
										editor, event, IGuard.ELEMENT_TYPE,
										PrefixGrdName.QUALIFIED_NAME,
										PrefixGrdName.DEFAULT_PREFIX);
								String name = UIUtils.getFreeElementName(
										editor, event, IGuard.ELEMENT_TYPE,
										PrefixGrdName.QUALIFIED_NAME,
										PrefixGrdName.DEFAULT_PREFIX);
								newGrd = event.getInternalElement(
										IGuard.ELEMENT_TYPE, name);
								assert !newGrd.exists();
								newGrd.create(null, monitor);
								newGrd.setLabel(label, monitor);
								newGrd.setPredicateString(
										EventBUIPlugin.GRD_DEFAULT, null);
								editor.addNewElement(newGrd);
							}

						}, null);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, newGrd, 1);
					}
				} catch (CoreException e) {
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
	public static void addLocalVariable(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				try {
					IStructuredSelection ssel = (IStructuredSelection) viewer
							.getSelection();
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						final IInternalElement event = TreeSupports
								.getEvent(obj);

						RodinCore.run(new IWorkspaceRunnable() {

							public void run(IProgressMonitor monitor)
									throws CoreException {
								String identifier = UIUtils
										.getFreeElementIdentifier(editor,
												event, IVariable.ELEMENT_TYPE,
												PrefixVarName.QUALIFIED_NAME,
												PrefixVarName.DEFAULT_PREFIX);
								String name = UIUtils.getFreeElementName(
										editor, event, IVariable.ELEMENT_TYPE,
										PrefixVarName.QUALIFIED_NAME,
										PrefixVarName.DEFAULT_PREFIX);
								newVar = event.getInternalElement(
										IVariable.ELEMENT_TYPE, name);
								assert !newVar.exists();
								newVar.create(null, monitor);
								newVar.setIdentifierString(identifier,
										new NullProgressMonitor());
								editor.addNewElement(newVar);
							}

						}, null);
						viewer.setExpandedState(TreeSupports.findItem(
								viewer.getTree(), event).getData(), true);
						select((EventBEditableTreeViewer) viewer, newVar, 0);
					}
				} catch (CoreException e) {
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
	public static void addVariable(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IVariable.ELEMENT_TYPE,
									PrefixVarName.QUALIFIED_NAME,
									PrefixVarName.DEFAULT_PREFIX);
							String identifier = UIUtils
									.getFreeElementIdentifier(editor,
											rodinFile, IVariable.ELEMENT_TYPE,
											PrefixVarName.QUALIFIED_NAME,
											PrefixVarName.DEFAULT_PREFIX);
							newVar = rodinFile.getInternalElement(
									IVariable.ELEMENT_TYPE, name);
							assert !newVar.exists();
							newVar.create(null, monitor);
							newVar.setIdentifierString(identifier,
									new NullProgressMonitor());
							editor.addNewElement(newVar);
						}

					}, null);
					((EventBEditableTreeViewer) viewer).edit(newVar);
				} catch (CoreException e) {
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
	public static void addInvariant(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String label = UIUtils.getFreeElementLabel(editor,
									rodinFile, IInvariant.ELEMENT_TYPE,
									PrefixInvName.QUALIFIED_NAME,
									PrefixInvName.DEFAULT_PREFIX);
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IInvariant.ELEMENT_TYPE,
									PrefixInvName.QUALIFIED_NAME,
									PrefixInvName.DEFAULT_PREFIX);
							newInv = rodinFile.getInternalElement(
									IInvariant.ELEMENT_TYPE, name);
							assert !newInv.exists();
							newInv.create(null, monitor);
							newInv.setLabel(label, monitor);
							newInv.setPredicateString(
									EventBUIPlugin.INV_DEFAULT, null);
							editor.addNewElement(newInv);
						}

					}, null);
					((EventBEditableTreeViewer) viewer).edit(newInv);

				} catch (CoreException e) {
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
	public static void addTheorem(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String label = UIUtils.getFreeElementLabel(editor,
									rodinFile, ITheorem.ELEMENT_TYPE,
									PrefixThmName.QUALIFIED_NAME,
									PrefixThmName.DEFAULT_PREFIX);
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, ITheorem.ELEMENT_TYPE,
									PrefixThmName.QUALIFIED_NAME,
									PrefixThmName.DEFAULT_PREFIX);
							newThm = rodinFile.getInternalElement(
									ITheorem.ELEMENT_TYPE, name);
							assert !newThm.exists();
							newThm.create(null, monitor);
							newThm.setLabel(label, monitor);
							newThm.setPredicateString(
									EventBUIPlugin.THM_DEFAULT, null);
							editor.addNewElement(newThm);
						}

					}, null);
					((EventBEditableTreeViewer) viewer).edit(newThm);

				} catch (CoreException e) {
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
	public static void addEvent(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {

					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String evtName = UIUtils.getFreeElementName(editor,
									rodinFile, IEvent.ELEMENT_TYPE,
									PrefixEvtName.QUALIFIED_NAME,
									PrefixEvtName.DEFAULT_PREFIX);
							String evtLabel = UIUtils.getFreeElementLabel(
									editor, rodinFile, IEvent.ELEMENT_TYPE,
									PrefixEvtName.QUALIFIED_NAME,
									PrefixEvtName.DEFAULT_PREFIX);
							newEvt = rodinFile.getInternalElement(
									IEvent.ELEMENT_TYPE, evtName);
							assert !newEvt.exists();
							newEvt.create(null, monitor);
							newEvt.setLabel(evtLabel, monitor);
							editor.addNewElement(newEvt);

							newEvt.setConvergence(
									IConvergenceElement.Convergence.ORDINARY,
									monitor);
							newEvt.setInherited(false, monitor);

							String namePrefix = UIUtils.getNamePrefix(editor,
									PrefixVarName.QUALIFIED_NAME,
									PrefixVarName.DEFAULT_PREFIX);
							int nameIndex = UIUtils.getFreeElementNameIndex(
									editor, newEvt, IVariable.ELEMENT_TYPE,
									namePrefix);

							String prefix = UIUtils.getFreeElementIdentifier(
									editor, newEvt, IVariable.ELEMENT_TYPE,
									PrefixVarName.QUALIFIED_NAME,
									PrefixVarName.DEFAULT_PREFIX);

							int index = UIUtils.getFreeElementIdentifierIndex(
									editor, newEvt, IVariable.ELEMENT_TYPE,
									prefix);

							for (int i = 0; i < 3; i++) {
								newVar = newEvt.getInternalElement(
										IVariable.ELEMENT_TYPE, namePrefix
												+ nameIndex);
								assert !newVar.exists();
								newVar.create(null, monitor);
								nameIndex = UIUtils.getFreeElementNameIndex(
										newEvt, IVariable.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);

								newVar.setIdentifierString(prefix + index,
										new NullProgressMonitor());
								index = UIUtils.getFreeElementIdentifierIndex(
										editor, newEvt, IVariable.ELEMENT_TYPE,
										prefix, index + 1);
								editor.addNewElement(newVar);
							}

							namePrefix = UIUtils.getNamePrefix(editor,
									PrefixGrdName.QUALIFIED_NAME,
									PrefixGrdName.DEFAULT_PREFIX);
							nameIndex = UIUtils.getFreeElementNameIndex(editor,
									newEvt, IGuard.ELEMENT_TYPE, namePrefix);
							prefix = UIUtils.getFreeElementLabel(editor,
									newEvt, IGuard.ELEMENT_TYPE,
									PrefixGrdName.QUALIFIED_NAME,
									PrefixGrdName.DEFAULT_PREFIX);

							index = UIUtils.getFreeElementLabelIndex(editor,
									newEvt, IGuard.ELEMENT_TYPE, prefix);
							for (int i = 0; i < 3; i++) {
								newGrd = newEvt.getInternalElement(
										IGuard.ELEMENT_TYPE, namePrefix
												+ nameIndex);
								assert !newGrd.exists();
								newGrd.create(null, monitor);
								nameIndex = UIUtils.getFreeElementNameIndex(
										newEvt, IGuard.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);
								newGrd.setLabel(prefix + index, monitor);
								index = UIUtils.getFreeElementLabelIndex(
										editor, newEvt, IGuard.ELEMENT_TYPE,
										prefix, index + 1);
								newGrd.setPredicateString(
										EventBUIPlugin.GRD_DEFAULT, null);
								editor.addNewElement(newGrd);
							}

							namePrefix = UIUtils.getNamePrefix(editor,
									PrefixActName.QUALIFIED_NAME,
									PrefixActName.DEFAULT_PREFIX);
							nameIndex = UIUtils.getFreeElementNameIndex(editor,
									newEvt, IAction.ELEMENT_TYPE, namePrefix);
							prefix = UIUtils.getFreeElementLabel(editor,
									newEvt, IAction.ELEMENT_TYPE,
									PrefixActName.QUALIFIED_NAME,
									PrefixActName.DEFAULT_PREFIX);

							index = UIUtils.getFreeElementLabelIndex(editor,
									newEvt, IAction.ELEMENT_TYPE, prefix);
							for (int i = 0; i < 3; i++) {
								newAct = newEvt.getInternalElement(
										IAction.ELEMENT_TYPE, namePrefix
												+ nameIndex);
								assert !newAct.exists();
								newAct.create(null, monitor);
								nameIndex = UIUtils.getFreeElementNameIndex(
										newEvt, IAction.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);
								newAct.setLabel(prefix + index, monitor);
								index = UIUtils.getFreeElementLabelIndex(
										editor, newEvt, IAction.ELEMENT_TYPE,
										prefix, index + 1);
								newAct.setAssignmentString(
										EventBUIPlugin.SUB_DEFAULT, monitor);
								editor.addNewElement(newAct);
							}
						}

					}, null);
					viewer.setExpandedState(newEvt, true);
					viewer.reveal(newAct);
					((EventBEditableTreeViewer) viewer).edit(newEvt);
				} catch (CoreException e) {
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
	public static void addAxiom(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String label = UIUtils.getFreeElementLabel(editor,
									rodinFile, IAxiom.ELEMENT_TYPE,
									PrefixAxmName.QUALIFIED_NAME,
									PrefixAxmName.DEFAULT_PREFIX);
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IAxiom.ELEMENT_TYPE,
									PrefixAxmName.QUALIFIED_NAME,
									PrefixAxmName.DEFAULT_PREFIX);
							newAxm = rodinFile.getInternalElement(
									IAxiom.ELEMENT_TYPE, name);
							assert !newAxm.exists();
							newAxm.create(null, monitor);
							newAxm.setLabel(label, monitor);
							newAxm.setPredicateString(
									EventBUIPlugin.AXM_DEFAULT, null);
							editor.addNewElement(newAxm);
						}

					}, null);
					((EventBEditableTreeViewer) viewer).edit(newAxm);
				} catch (CoreException e) {
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
	public static void addConstant(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IConstant.ELEMENT_TYPE,
									PrefixCstName.QUALIFIED_NAME,
									PrefixCstName.DEFAULT_PREFIX);
							String identifier = UIUtils
									.getFreeElementIdentifier(editor,
											rodinFile, IConstant.ELEMENT_TYPE,
											PrefixCstName.QUALIFIED_NAME,
											PrefixCstName.DEFAULT_PREFIX);
							newCst = rodinFile.getInternalElement(
									IConstant.ELEMENT_TYPE, name);
							assert !newCst.exists();
							newCst.create(null, monitor);
							newCst.setIdentifierString(identifier,
									new NullProgressMonitor());
							editor.addNewElement(newCst);
						}

					}, null);
					((EventBEditableTreeViewer) viewer).edit(newCst);
				} catch (CoreException e) {
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
	public static void addSet(final IEventBEditor editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws CoreException {
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, ICarrierSet.ELEMENT_TYPE,
									PrefixSetName.QUALIFIED_NAME,
									PrefixSetName.DEFAULT_PREFIX);
							String identifier = UIUtils
									.getFreeElementIdentifier(editor,
											rodinFile,
											ICarrierSet.ELEMENT_TYPE,
											PrefixSetName.QUALIFIED_NAME,
											PrefixSetName.DEFAULT_PREFIX);
							newSet = rodinFile
									.getInternalElement(
											ICarrierSet.ELEMENT_TYPE, name);
							assert !newSet.exists();
							newSet.create(null, monitor);
							newSet.setIdentifierString(identifier,
									new NullProgressMonitor());
							editor.addNewElement(newSet);
						}

					}, null);
					((EventBEditableTreeViewer) viewer).edit(newSet);
				} catch (CoreException e) {
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
	 * initialisation using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the variable and its invariant,
	 *            initialisation will be created in
	 */
	public static void intelligentNewVariable(final IEventBEditor editor,
			final IRodinFile rodinFile) {
		try {

			String prefix = UIUtils.getPrefix(editor,
					PrefixInvName.QUALIFIED_NAME, PrefixInvName.DEFAULT_PREFIX);
			int index = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, prefix);

			IInternalElement initialisation = getInitialisation(rodinFile);

			String defaultInitName = "";
			if (initialisation == null)
				defaultInitName = UIUtils.getPrefix(editor,
						PrefixActName.QUALIFIED_NAME,
						PrefixActName.DEFAULT_PREFIX) + 1;
			else {
				defaultInitName = UIUtils.getFreeElementLabel(editor,
						initialisation, IAction.ELEMENT_TYPE,
						PrefixActName.QUALIFIED_NAME,
						PrefixActName.DEFAULT_PREFIX);
			}

			String varName = UIUtils.getFreeElementIdentifier(editor,
					rodinFile, IVariable.ELEMENT_TYPE,
					PrefixVarName.QUALIFIED_NAME, PrefixVarName.DEFAULT_PREFIX);
			final IntelligentNewVariableInputDialog dialog = new IntelligentNewVariableInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Variable", varName, prefix, index, defaultInitName);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					newVar = rodinFile.getInternalElement(
							IVariable.ELEMENT_TYPE, UIUtils.getFreeElementName(
									editor, rodinFile, IVariable.ELEMENT_TYPE,
									PrefixVarName.QUALIFIED_NAME,
									PrefixVarName.DEFAULT_PREFIX));
					assert !newVar.exists();
					newVar.create(null, monitor);
					String name = dialog.getName();

					newVar.setIdentifierString(name, new NullProgressMonitor());
					editor.addNewElement(newVar);

					String init = dialog.getInitSubstitution();

					Collection<Pair> invariants = dialog.getInvariants();
					String invPrefix = UIUtils.getNamePrefix(editor,
							PrefixInvName.QUALIFIED_NAME,
							PrefixInvName.DEFAULT_PREFIX);
					int invIndex = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, IInvariant.ELEMENT_TYPE, invPrefix);
					if (invariants != null) {
						for (Pair pair : invariants) {
							newInv = rodinFile.getInternalElement(
									IInvariant.ELEMENT_TYPE, invPrefix
											+ invIndex);
							assert !newInv.exists();
							newInv.create(null, monitor);
							invIndex = UIUtils.getFreeElementNameIndex(
									rodinFile, IInvariant.ELEMENT_TYPE,
									invPrefix, invIndex + 1);
							newInv.setLabel((String) pair.getFirst(), monitor);
							newInv.setPredicateString(
									(String) pair.getSecond(), null);
							editor.addNewElement(newInv);
						}
					}

					if (init != null) {
						IRodinElement[] events = rodinFile
								.getChildrenOfType(IEvent.ELEMENT_TYPE);
						boolean newInit = true;
						for (IRodinElement event : events) {
							IEvent element = (IEvent) event;
							if (element.getLabel().equals("INITIALISATION")) {
								newInit = false;

								String actLabel = UIUtils.getFreeElementLabel(
										editor, element, IAction.ELEMENT_TYPE,
										PrefixActName.QUALIFIED_NAME,
										PrefixActName.DEFAULT_PREFIX);

								String actName = UIUtils.getFreeElementName(
										editor, element, IAction.ELEMENT_TYPE,
										PrefixActName.QUALIFIED_NAME,
										PrefixActName.DEFAULT_PREFIX);
								newAct = element.getInternalElement(
										IAction.ELEMENT_TYPE, actName);
								assert !newAct.exists();
								newAct.create(null, monitor);
								newAct.setLabel(actLabel, monitor);
								newAct.setAssignmentString(init, monitor);

								editor.addNewElement(newAct);
								break;
							}
						}
						if (newInit) {
							newEvt = rodinFile.getInternalElement(
									IEvent.ELEMENT_TYPE,
									UIUtils.getFreeElementName(editor,
											rodinFile, IEvent.ELEMENT_TYPE,
											PrefixEvtName.QUALIFIED_NAME,
											PrefixEvtName.DEFAULT_PREFIX));
							assert !newEvt.exists();
							newEvt.setLabel("INITIALISATION", monitor);
							String actName = UIUtils.getFreeElementName(editor,
									newEvt, IAction.ELEMENT_TYPE,
									PrefixActName.QUALIFIED_NAME,
									PrefixActName.DEFAULT_PREFIX);
							String actLabel = UIUtils.getFreeElementLabel(
									editor, newEvt, IAction.ELEMENT_TYPE,
									PrefixActName.QUALIFIED_NAME,
									PrefixActName.DEFAULT_PREFIX);
							newAct = newEvt.getInternalElement(
									IAction.ELEMENT_TYPE, actName);
							assert !newAct.exists();
							newAct.create(null, monitor);
							newAct.setLabel(actLabel, monitor);
							newAct.setAssignmentString(init, monitor);
							editor.addNewElement(newAct);
						}
					}
				}

			}, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}
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
	public static void intelligentNewConstant(final IEventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String prefix = UIUtils.getPrefix(editor,
					PrefixAxmName.QUALIFIED_NAME, PrefixAxmName.DEFAULT_PREFIX);
			int index = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IAxiom.ELEMENT_TYPE, prefix);

			String cstName = UIUtils.getFreeElementIdentifier(editor,
					rodinFile, IConstant.ELEMENT_TYPE,
					PrefixCstName.QUALIFIED_NAME, PrefixCstName.DEFAULT_PREFIX);
			final IntelligentNewConstantInputDialog dialog = new IntelligentNewConstantInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Constant", cstName, prefix, index);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					newCst = rodinFile.getInternalElement(
							IConstant.ELEMENT_TYPE, UIUtils.getFreeElementName(
									editor, rodinFile, IConstant.ELEMENT_TYPE,
									PrefixCstName.QUALIFIED_NAME,
									PrefixCstName.DEFAULT_PREFIX));
					assert !newCst.exists();
					newCst.create(null, monitor);

					String name = dialog.getName();

					newCst.setIdentifierString(name, new NullProgressMonitor());
					editor.addNewElement(newCst);

					Collection<Pair> axioms = dialog.getAxioms();

					if (axioms != null) {
						String axmName = UIUtils.getNamePrefix(editor,
								PrefixAxmName.QUALIFIED_NAME,
								PrefixAxmName.DEFAULT_PREFIX);
						int axmIndex = UIUtils.getFreeElementNameIndex(editor,
								rodinFile, IAxiom.ELEMENT_TYPE, axmName);
						for (Pair pair : axioms) {
							newAxm = rodinFile.getInternalElement(
									IAxiom.ELEMENT_TYPE, axmName + axmIndex);
							assert !newAxm.exists();
							newAxm.create(null, monitor);
							axmIndex = UIUtils.getFreeElementNameIndex(
									rodinFile, IAxiom.ELEMENT_TYPE, axmName,
									axmIndex + 1);
							newAxm.setLabel((String) pair.getFirst(), monitor);
							newAxm.setPredicateString(
									(String) pair.getSecond(), null);
							editor.addNewElement(newAxm);
						}
					}

				}

			}, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}
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
	public static void newInvariants(final IEventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String invPrefix = UIUtils.getPrefix(editor,
					PrefixInvName.QUALIFIED_NAME, PrefixInvName.DEFAULT_PREFIX);

			int invIndex = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, invPrefix);
			final ElementNameContentInputDialog dialog =
				new ElementNameContentInputDialog<IInvariant>(
					Display.getCurrent().getActiveShell(), "New Invariants",
					"Name and predicate", editor, IInvariant.ELEMENT_TYPE,
					invPrefix, invIndex);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {

					String prefix = UIUtils.getNamePrefix(editor,
							PrefixInvName.QUALIFIED_NAME,
							PrefixInvName.DEFAULT_PREFIX);
					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, IInvariant.ELEMENT_TYPE, prefix);
					String[] names = dialog.getNewNames();
					String[] contents = dialog.getNewContents();
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newInv = rodinFile.getInternalElement(
								IInvariant.ELEMENT_TYPE, prefix + index);
						assert !newInv.exists();
						newInv.create(null, monitor);
						index = UIUtils.getFreeElementNameIndex(rodinFile,
								IInvariant.ELEMENT_TYPE, prefix, index + 1);
						newInv.setLabel(name, monitor);
						newInv.setPredicateString(content, null);
						editor.addNewElement(newInv);
					}
				}

			}, null);
		} catch (CoreException e) {
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
	public static void newVariant(final IEventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String variantPrefix = UIUtils.getPrefix(editor,
					PrefixVariantName.QUALIFIED_NAME,
					PrefixVariantName.DEFAULT_PREFIX);

			final NewVariantInputDialog dialog = new NewVariantInputDialog(
					Display.getCurrent().getActiveShell(), "New Variant",
					"Expression", variantPrefix);

			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					String prefix = UIUtils.getNamePrefix(editor,
							PrefixVariantName.QUALIFIED_NAME,
							PrefixVariantName.DEFAULT_PREFIX);
					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, IVariant.ELEMENT_TYPE, prefix);
					newVariant = rodinFile.getInternalElement(
							IVariant.ELEMENT_TYPE, prefix + index);
					assert !newVariant.exists();
					newVariant.create(null, monitor);
					String expression = dialog.getExpression();

					newVariant.setExpressionString(expression, monitor);
					editor.addNewElement(newVariant);
				}
			}, null);
		} catch (CoreException e) {
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
	public static void newTheorems(final IEventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String thmPrefix = UIUtils.getPrefix(editor,
					PrefixThmName.QUALIFIED_NAME, PrefixThmName.DEFAULT_PREFIX);
			int thmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), ITheorem.ELEMENT_TYPE, thmPrefix);
			final ElementNameContentInputDialog dialog =
				new ElementNameContentInputDialog<ITheorem>(
					Display.getCurrent().getActiveShell(), "New Theorems",
					"Name and predicate", editor, ITheorem.ELEMENT_TYPE,
					thmPrefix, thmIndex);
			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					String prefix = UIUtils.getNamePrefix(editor,
							PrefixThmName.QUALIFIED_NAME,
							PrefixThmName.DEFAULT_PREFIX);
					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, ITheorem.ELEMENT_TYPE, prefix);
					String[] names = dialog.getNewNames();
					String[] contents = dialog.getNewContents();

					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newThm = rodinFile.getInternalElement(
								ITheorem.ELEMENT_TYPE, prefix + index);
						newThm.create(null, monitor);
						index = UIUtils.getFreeElementNameIndex(rodinFile,
								ITheorem.ELEMENT_TYPE, prefix, index + 1);
						newThm.setLabel(name, monitor);
						newThm.setPredicateString(content, null);
						editor.addNewElement(newThm);
					}
				}

			}, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create a event with its local variables, guards and
	 * actions using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 */
	public static void newEvent(final EventBMachineEditor editor,
			IProgressMonitor monitor) {

		final IMachineFile mchFile = editor.getRodinInput();
		try {
			String evtLabel = UIUtils.getFreeElementLabel(editor, mchFile,
					IEvent.ELEMENT_TYPE, PrefixEvtName.QUALIFIED_NAME,
					PrefixEvtName.DEFAULT_PREFIX);

			final NewEventInputDialog dialog = new NewEventInputDialog(Display
					.getCurrent().getActiveShell(), "New Events", evtLabel);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws CoreException {

					String name = dialog.getName();
					String[] varNames = dialog.getVariables();
					String[] grdNames = dialog.getGrdNames();
					String[] grdPredicates = dialog.getGrdPredicates();
					String[] actNames = dialog.getActNames();
					String[] actSubstitutions = dialog.getActSubstitutions();

					String evtName = UIUtils.getFreeElementName(editor,
							mchFile, IEvent.ELEMENT_TYPE,
							PrefixEvtName.QUALIFIED_NAME,
							PrefixEvtName.DEFAULT_PREFIX);
					IEvent evt = mchFile.getEvent(evtName);
					evt.create(null, pm);
					evt.setLabel(name, pm);
					evt.setConvergence(
							IConvergenceElement.Convergence.ORDINARY, pm);
					evt.setInherited(false, pm);
					editor.addNewElement(evt);

					String varPrefix = UIUtils.getNamePrefix(editor,
							PrefixVarName.QUALIFIED_NAME,
							PrefixVarName.DEFAULT_PREFIX);

					int varIndex = UIUtils.getFreeElementNameIndex(editor, evt,
							IVariable.ELEMENT_TYPE, varPrefix);
					for (String varName : varNames) {
						IVariable var = evt.getVariable(varPrefix + varIndex);
						var.create(null, pm);
						var.setIdentifierString(varName, pm);
						editor.addNewElement(var);
						varIndex = UIUtils
								.getFreeElementNameIndex(evt,
										IVariable.ELEMENT_TYPE, varPrefix,
										varIndex + 1);
					}

					String grdPrefix = UIUtils.getNamePrefix(editor,
							PrefixGrdName.QUALIFIED_NAME,
							PrefixGrdName.DEFAULT_PREFIX);
					int grdIndex = UIUtils.getFreeElementNameIndex(editor, evt,
							IGuard.ELEMENT_TYPE, grdPrefix);
					for (int i = 0; i < grdNames.length; i++) {
						IGuard grd = evt.getGuard(grdPrefix + grdIndex);
						grd.create(null, pm);
						grd.setLabel(grdNames[i], pm);
						grd.setPredicateString(grdPredicates[i], null);
						editor.addNewElement(grd);
						grdIndex = UIUtils.getFreeElementNameIndex(evt,
								IGuard.ELEMENT_TYPE, grdPrefix, grdIndex + 1);
					}

					String actPrefix = UIUtils.getNamePrefix(editor,
							PrefixActName.QUALIFIED_NAME,
							PrefixActName.DEFAULT_PREFIX);
					int actIndex = UIUtils.getFreeElementNameIndex(editor, evt,
							IAction.ELEMENT_TYPE, actPrefix);
					for (int i = 0; i < actNames.length; i++) {
						IAction act = evt.getAction(actPrefix + actIndex);
						act.create(null, pm);
						act.setLabel(actNames[i], pm);
						act.setAssignmentString(actSubstitutions[i], pm);
						editor.addNewElement(act);
						actIndex = UIUtils.getFreeElementNameIndex(evt,
								IAction.ELEMENT_TYPE, actPrefix, actIndex);
					}
				}

			}, monitor);

		} catch (CoreException e) {
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
	public static void newCarrierSets(final EventBContextEditor editor,
			IProgressMonitor monitor) {

		final IContextFile ctxFile = editor.getRodinInput();
		try {
			String identifier = UIUtils.getFreeElementIdentifier(editor, ctxFile,
					ICarrierSet.ELEMENT_TYPE, PrefixSetName.QUALIFIED_NAME,
					PrefixSetName.DEFAULT_PREFIX);
			final ElementAttributeInputDialog dialog = new ElementAttributeInputDialog(
					Display.getCurrent().getActiveShell(), "New Carrier Sets",
					"Name", identifier);

			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel
			
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws CoreException {
					String setPrefix = UIUtils.getNamePrefix(editor,
							PrefixCstName.QUALIFIED_NAME,
							PrefixCstName.DEFAULT_PREFIX);
					int setIndex = UIUtils.getFreeElementNameIndex(editor, ctxFile,
							ICarrierSet.ELEMENT_TYPE, setPrefix);
					Collection<String> names = dialog.getAttributes();
					
					for (String name : names) {
						ICarrierSet set = ctxFile.getCarrierSet(setPrefix
												+ setIndex);
						set.create(null, pm);
						set.setIdentifierString(name, pm);
						editor.addNewElement(set);
						setIndex = UIUtils.getFreeElementNameIndex(ctxFile,
								ICarrierSet.ELEMENT_TYPE, setPrefix,
								setIndex + 1);
					}
				}

			}, monitor);
		} catch (CoreException e) {
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
			String identifier = UIUtils.getFreeElementIdentifier(editor, ctxFile,
					ICarrierSet.ELEMENT_TYPE, PrefixSetName.QUALIFIED_NAME,
					PrefixSetName.DEFAULT_PREFIX);
			final NewEnumeratedSetInputDialog dialog = new NewEnumeratedSetInputDialog(
					Display.getCurrent().getActiveShell(),
					"New Enumerated Set", identifier);

			dialog.open();
			final String name = dialog.getName();
			if (name == null)
				return;

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws CoreException {
					String[] elements = dialog.getElements();

					final String setName = UIUtils.getFreeElementName(editor, ctxFile,
							ICarrierSet.ELEMENT_TYPE,
							PrefixSetName.QUALIFIED_NAME,
							PrefixSetName.DEFAULT_PREFIX);
					final ICarrierSet set = ctxFile.getCarrierSet(setName);
					set.create(null, pm);
					set.setIdentifierString(name, pm);
					editor.addNewElement(set);

					final int nbElements = elements.length;
					if (nbElements == 0)
						return;

					String namePrefix = UIUtils.getNamePrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					int nameIndex = UIUtils.getFreeElementNameIndex(editor, ctxFile,
							IAxiom.ELEMENT_TYPE, namePrefix);

					String labelPrefix = UIUtils.getPrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					int labelIndex = UIUtils.getFreeElementLabelIndex(editor,
							ctxFile, IAxiom.ELEMENT_TYPE, labelPrefix);
					// String axmName = namePrefix + nameIndex;

					newAxm = ctxFile.getAxiom(namePrefix + nameIndex);
					newAxm.create(null, null);
					newAxm.setLabel(labelPrefix + labelIndex, pm);
					StringBuilder axmPred = new StringBuilder(name);
					axmPred.append(" = {");

					String cstPrefix = UIUtils.getNamePrefix(editor,
							PrefixCstName.QUALIFIED_NAME,
							PrefixCstName.DEFAULT_PREFIX);
					int cstIndex = UIUtils.getFreeElementNameIndex(editor, ctxFile,
							IConstant.ELEMENT_TYPE, cstPrefix);
					String axmSep = "";
					for (String element : elements) {
						IConstant cst = ctxFile.getConstant(cstPrefix + cstIndex);
						cst.create(null, pm);
						cst.setIdentifierString(element, pm);
						editor.addNewElement(cst);
						cstIndex = UIUtils.getFreeElementNameIndex(ctxFile,
								IConstant.ELEMENT_TYPE, cstPrefix, cstIndex + 1);

						nameIndex = UIUtils.getFreeElementNameIndex(ctxFile,
								IAxiom.ELEMENT_TYPE, namePrefix, nameIndex);
						labelIndex = UIUtils.getFreeElementLabelIndex(editor,
								ctxFile, IAxiom.ELEMENT_TYPE, labelPrefix,
								labelIndex);
						axmPred.append(axmSep);
						axmSep = ", ";
						axmPred.append(element);
					}
					axmPred.append("}");
					newAxm.setPredicateString(axmPred.toString(), null);

					for (int i = 0; i < nbElements; ++i) {
						for (int j = i+1; j < nbElements; ++j) {
							nameIndex = UIUtils.getFreeElementNameIndex(ctxFile,
									IAxiom.ELEMENT_TYPE, namePrefix, nameIndex);
							labelIndex = UIUtils.getFreeElementLabelIndex(editor,
									ctxFile, IAxiom.ELEMENT_TYPE,
									labelPrefix, labelIndex);
							IAxiom axm = ctxFile.getAxiom(namePrefix + nameIndex);
							axm.create(null, pm);
							axm.setLabel(labelPrefix + labelIndex, pm);
							axm.setPredicateString(elements[i] + " \u2260 "
									+ elements[j], null);
						}
					}

				}

			}, monitor);
		} catch (CoreException e) {
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
	public static void newAxioms(final IEventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String axmPrefix = UIUtils.getPrefix(editor,
					PrefixAxmName.QUALIFIED_NAME, PrefixAxmName.DEFAULT_PREFIX);
			int axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix);
			final ElementNameContentInputDialog dialog =
				new ElementNameContentInputDialog<IAxiom>(
					Display.getCurrent().getActiveShell(), "New Axioms",
					"Name and predicate", editor, IAxiom.ELEMENT_TYPE,
					axmPrefix, axmIndex);
			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					String prefix = UIUtils.getNamePrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					String[] names = dialog.getNewNames();
					String[] contents = dialog.getNewContents();

					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, IAxiom.ELEMENT_TYPE, prefix);
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newAxm = rodinFile.getInternalElement(
								IAxiom.ELEMENT_TYPE, prefix + index);
						assert !newAxm.exists();
						newAxm.create(null, monitor);
						index = UIUtils.getFreeElementNameIndex(rodinFile,
								IAxiom.ELEMENT_TYPE, prefix, index + 1);
						newAxm.setLabel(name, monitor);
						newAxm.setPredicateString(content, null);
						editor.addNewElement(newAxm);
					}
				}

			}, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static IRodinFile getAbstractFile(IRodinFile concreteFile)
			throws RodinDBException {
		IRodinElement[] refines = concreteFile
				.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
		if (refines.length == 1) {
			IRefinesMachine refine = (IRefinesMachine) refines[0];
			String name = refine.getAbstractMachineName();
			IEventBProject prj = (IEventBProject) concreteFile
					.getRodinProject().getAdapter(IEventBProject.class);
			return prj.getMachineFile(name);
		}
		return null;

	}

	public static IRodinElement getAbstractElement(IRodinElement concreteElement)
			throws RodinDBException {
		IRodinFile rodinFile = (IRodinFile) concreteElement.getOpenable();
		IRodinFile abstractFile = getAbstractFile(rodinFile);
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

	public static IEvent getAbstractEvent(IRodinFile abstractFile,
			IEvent conc_evt) throws RodinDBException {
		IRodinElement abs_evt = null;
		IRodinElement[] abs_evts = conc_evt
				.getChildrenOfType(IRefinesEvent.ELEMENT_TYPE);
		if (abs_evts.length != 0) {
			abs_evt = UIUtils.getFirstChildOfTypeWithLabel(abstractFile,
					IEvent.ELEMENT_TYPE, ((IRefinesEvent) abs_evts[0])
							.getAbstractEventLabel());
		} else {
			// Do nothing at the moment. Should try to get the event with same
			// name?
		}
		return (IEvent) abs_evt;
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
}
