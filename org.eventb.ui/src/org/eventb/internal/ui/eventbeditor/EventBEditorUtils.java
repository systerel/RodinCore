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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBUIPlugin;
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
import org.eventb.internal.ui.eventbeditor.actions.PrefixWitName;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
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

	private static IAction newAct;

	private static IGuard newGrd;

	private static IVariable newVar;

	private static IInvariant newInv;

	private static ITheorem newThm;

	private static IEvent newEvt;

	private static IAxiom newAxm;

	private static ICarrierSet newSet;

	private static IConstant newCst;

	private static IRefinesEvent newRefEvt;

	private static IWitness newWit;

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
								newAct = (IAction) event.createInternalElement(
										IAction.ELEMENT_TYPE, name, null,
										monitor);
								newAct.setLabel(label, monitor);
								newAct
										.setAssignmentString(EventBUIPlugin.SUB_DEFAULT);
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
	 * Add a new action.
	 * <p>
	 * 
	 * @param editor
	 *            The current Event-B Editor
	 * @param viewer
	 *            The current Tree Viewer in the Event-B Editor
	 */
	public static void addRefinesEvent(final EventBEditor editor,
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
								String abs_name = ((IEvent) event)
										.getLabel(monitor);
								newRefEvt = (IRefinesEvent) event
										.createInternalElement(
												IRefinesEvent.ELEMENT_TYPE,
												name, null, monitor);
								newRefEvt.setAbstractEventLabel(abs_name);
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
	public static void addWitness(final EventBEditor editor,
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
								newWit = (IWitness) event
										.createInternalElement(
												IWitness.ELEMENT_TYPE, name,
												null, monitor);
								newWit.setLabel(label, monitor);
								newWit
										.setPredicateString(EventBUIPlugin.PRD_DEFAULT);
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
	public static void addGuard(final EventBEditor editor,
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
								newGrd = (IGuard) event.createInternalElement(
										IGuard.ELEMENT_TYPE, name, null,
										monitor);
								newGrd.setLabel(label, monitor);
								newGrd
										.setPredicateString(EventBUIPlugin.GRD_DEFAULT);
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
	public static void addLocalVariable(final EventBEditor editor,
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
								newVar = (IVariable) event
										.createInternalElement(
												IVariable.ELEMENT_TYPE, name,
												null, monitor);
								newVar.setIdentifierString(identifier);
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
	public static void addVariable(final EventBEditor editor,
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
							newVar = (IVariable) rodinFile
									.createInternalElement(
											IVariable.ELEMENT_TYPE, name, null,
											monitor);
							newVar.setIdentifierString(identifier);
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
	public static void addInvariant(final EventBEditor editor,
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
							newInv = (IInvariant) rodinFile
									.createInternalElement(
											IInvariant.ELEMENT_TYPE, name,
											null, monitor);
							newInv.setLabel(label, monitor);
							newInv
									.setPredicateString(EventBUIPlugin.INV_DEFAULT);
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
	public static void addTheorem(final EventBEditor editor,
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
							newThm = (ITheorem) rodinFile
									.createInternalElement(
											ITheorem.ELEMENT_TYPE, name, null,
											monitor);
							newThm.setLabel(label, monitor);
							newThm
									.setPredicateString(EventBUIPlugin.THM_DEFAULT);
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
	public static void addEvent(final EventBEditor editor,
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
							newEvt = (IEvent) rodinFile
									.createInternalElement(IEvent.ELEMENT_TYPE,
											evtName, null, monitor);
							newEvt.setLabel(evtLabel, monitor);
							editor.addNewElement(newEvt);

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
								newVar = (IVariable) newEvt
										.createInternalElement(
												IVariable.ELEMENT_TYPE,
												namePrefix + nameIndex, null,
												monitor);
								nameIndex = UIUtils.getFreeElementNameIndex(
										editor, newEvt, IVariable.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);

								newVar.setIdentifierString(prefix + index);
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
								newGrd = (IGuard) newEvt.createInternalElement(
										IGuard.ELEMENT_TYPE, namePrefix
												+ nameIndex, null, monitor);
								nameIndex = UIUtils.getFreeElementNameIndex(
										editor, newEvt, IGuard.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);
								newGrd.setLabel(prefix + index, monitor);
								index = UIUtils.getFreeElementLabelIndex(
										editor, newEvt, IGuard.ELEMENT_TYPE,
										prefix, index + 1);
								newGrd
										.setPredicateString(EventBUIPlugin.GRD_DEFAULT);
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
								newAct = (IAction) newEvt
										.createInternalElement(
												IAction.ELEMENT_TYPE,
												namePrefix + nameIndex, null,
												monitor);
								nameIndex = UIUtils.getFreeElementNameIndex(
										editor, newEvt, IAction.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);
								newAct.setLabel(prefix + index, monitor);
								index = UIUtils.getFreeElementLabelIndex(
										editor, newEvt, IAction.ELEMENT_TYPE,
										prefix, index + 1);
								newAct
										.setAssignmentString(EventBUIPlugin.SUB_DEFAULT);
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
	public static void addAxiom(final EventBEditor editor,
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
							newAxm = (IAxiom) rodinFile.createInternalElement(
									IAxiom.ELEMENT_TYPE, name, null, monitor);
							newAxm.setLabel(label, monitor);
							newAxm
									.setPredicateString(EventBUIPlugin.AXM_DEFAULT);
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
	public static void addConstant(final EventBEditor editor,
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
							newCst = (IConstant) rodinFile
									.createInternalElement(
											IConstant.ELEMENT_TYPE, name, null,
											monitor);
							newCst.setIdentifierString(identifier);
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
	public static void addSet(final EventBEditor editor, final TreeViewer viewer) {
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
							newSet = (ICarrierSet) rodinFile
									.createInternalElement(
											ICarrierSet.ELEMENT_TYPE, name,
											null, monitor);
							newSet.setIdentifierString(identifier);
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
	public static void intelligentNewVariable(final EventBEditor editor,
			final IRodinFile rodinFile) {
		try {

			String invPrefix = UIUtils.getPrefix(editor,
					PrefixInvName.QUALIFIED_NAME, PrefixInvName.DEFAULT_PREFIX);
			int invIndex = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, invPrefix);

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
					"New Variable", varName, invPrefix, invIndex,
					defaultInitName);

			dialog.open();
			final String name = dialog.getName();
			final String init = dialog.getInitSubstitution();
			if (name != null) {

				RodinCore.run(new IWorkspaceRunnable() {

					public void run(IProgressMonitor monitor)
							throws CoreException {
						newVar = (IVariable) rodinFile.createInternalElement(
								IVariable.ELEMENT_TYPE, UIUtils
										.getFreeElementName(editor, rodinFile,
												IVariable.ELEMENT_TYPE,
												PrefixVarName.QUALIFIED_NAME,
												PrefixVarName.DEFAULT_PREFIX),
								null, monitor);
						newVar.setIdentifierString(name);
						editor.addNewElement(newVar);

						Collection<Pair> invariants = dialog.getInvariants();
						String invPrefix = UIUtils.getNamePrefix(editor,
								PrefixInvName.QUALIFIED_NAME,
								PrefixInvName.DEFAULT_PREFIX);
						int invIndex = UIUtils.getFreeElementNameIndex(editor,
								rodinFile, IInvariant.ELEMENT_TYPE, invPrefix);
						if (invariants != null) {
							for (Pair pair : invariants) {
								newInv = (IInvariant) rodinFile
										.createInternalElement(
												IInvariant.ELEMENT_TYPE,
												invPrefix + invIndex, null,
												monitor);
								invIndex = UIUtils.getFreeElementNameIndex(
										editor, rodinFile,
										IInvariant.ELEMENT_TYPE, invPrefix,
										invIndex + 1);
								newInv.setLabel((String) pair.getFirst(),
										monitor);
								newInv.setPredicateString((String) pair
										.getSecond());
								editor.addNewElement(newInv);
							}
						}

						if (init != null) {
							IRodinElement[] events = rodinFile
									.getChildrenOfType(IEvent.ELEMENT_TYPE);
							boolean newInit = true;
							for (IRodinElement event : events) {
								IEvent element = (IEvent) event;
								if (element.getLabel(monitor).equals(
										"INITIALISATION")) {
									newInit = false;

									String actLabel = UIUtils
											.getFreeElementLabel(
													editor,
													element,
													IAction.ELEMENT_TYPE,
													PrefixActName.QUALIFIED_NAME,
													PrefixActName.DEFAULT_PREFIX);

									String actName = UIUtils
											.getFreeElementName(
													editor,
													element,
													IAction.ELEMENT_TYPE,
													PrefixActName.QUALIFIED_NAME,
													PrefixActName.DEFAULT_PREFIX);
									newAct = (IAction) element
											.createInternalElement(
													IAction.ELEMENT_TYPE,
													actName, null, monitor);
									newAct.setLabel(actLabel, monitor);
									newAct.setAssignmentString(init);

									editor.addNewElement(newAct);
									break;
								}
							}
							if (newInit) {
								newEvt = (IEvent) rodinFile
										.createInternalElement(
												IEvent.ELEMENT_TYPE,
												UIUtils
														.getFreeElementName(
																editor,
																rodinFile,
																IEvent.ELEMENT_TYPE,
																PrefixEvtName.QUALIFIED_NAME,
																PrefixEvtName.DEFAULT_PREFIX),
												null, monitor);
								newEvt.setLabel("INITIALISATION", monitor);
								String actName = UIUtils.getFreeElementName(
										editor, newEvt, IAction.ELEMENT_TYPE,
										PrefixActName.QUALIFIED_NAME,
										PrefixActName.DEFAULT_PREFIX);
								String actLabel = UIUtils.getFreeElementLabel(
										editor, newEvt, IAction.ELEMENT_TYPE,
										PrefixActName.QUALIFIED_NAME,
										PrefixActName.DEFAULT_PREFIX);
								newAct = (IAction) newEvt
										.createInternalElement(
												IAction.ELEMENT_TYPE, actName,
												null, monitor);
								newAct.setLabel(actLabel, monitor);
								newAct.setAssignmentString(init);
								editor.addNewElement(newAct);
							}
						}
					}

				}, null);
			}
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
	public static void intelligentNewConstant(final EventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String axmPrefix = UIUtils.getPrefix(editor,
					PrefixAxmName.QUALIFIED_NAME, PrefixAxmName.DEFAULT_PREFIX);
			int axmIndex = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IAxiom.ELEMENT_TYPE, axmPrefix);

			String cstName = UIUtils.getFreeElementIdentifier(editor,
					rodinFile, IConstant.ELEMENT_TYPE,
					PrefixCstName.QUALIFIED_NAME, PrefixCstName.DEFAULT_PREFIX);
			final IntelligentNewConstantInputDialog dialog = new IntelligentNewConstantInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Constant", cstName, axmPrefix, axmIndex);

			dialog.open();
			final String name = dialog.getName();

			if (name != null) {

				RodinCore.run(new IWorkspaceRunnable() {

					public void run(IProgressMonitor monitor)
							throws CoreException {
						newCst = (IConstant) rodinFile.createInternalElement(
								IConstant.ELEMENT_TYPE, UIUtils
										.getFreeElementName(editor, rodinFile,
												IConstant.ELEMENT_TYPE,
												PrefixCstName.QUALIFIED_NAME,
												PrefixCstName.DEFAULT_PREFIX),
								null, monitor);
						newCst.setIdentifierString(name);
						editor.addNewElement(newCst);

						Collection<Pair> axioms = dialog.getAxioms();

						if (axioms != null) {
							String axmName = UIUtils.getNamePrefix(editor,
									PrefixAxmName.QUALIFIED_NAME,
									PrefixAxmName.DEFAULT_PREFIX);
							int axmIndex = UIUtils.getFreeElementNameIndex(
									editor, rodinFile, IAxiom.ELEMENT_TYPE,
									axmName);
							for (Pair pair : axioms) {
								newAxm = (IAxiom) rodinFile
										.createInternalElement(
												IAxiom.ELEMENT_TYPE, axmName
														+ axmIndex, null,
												monitor);
								axmIndex = UIUtils.getFreeElementNameIndex(
										editor, rodinFile, IAxiom.ELEMENT_TYPE,
										axmName, axmIndex + 1);
								newAxm.setLabel((String) pair.getFirst(),
										monitor);
								newAxm.setPredicateString((String) pair
										.getSecond());
								editor.addNewElement(newAxm);
							}
						}

					}

				}, null);
			}

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
			if (element.getLabel(null).equals("INITIALISATION")) {
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
	public static void newInvariants(final EventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String invPrefix = UIUtils.getPrefix(editor,
					PrefixInvName.QUALIFIED_NAME, PrefixInvName.DEFAULT_PREFIX);

			int invIndex = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, invPrefix);
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(
					Display.getCurrent().getActiveShell(), "New Invariants",
					"Name and predicate", editor, IInvariant.ELEMENT_TYPE,
					invPrefix, invIndex);

			dialog.open();
			final String[] names = dialog.getNewNames();
			final String[] contents = dialog.getNewContents();
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {

					String prefix = UIUtils.getNamePrefix(editor,
							PrefixInvName.QUALIFIED_NAME,
							PrefixInvName.DEFAULT_PREFIX);
					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, IInvariant.ELEMENT_TYPE, prefix);
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newInv = (IInvariant) rodinFile.createInternalElement(
								IInvariant.ELEMENT_TYPE, prefix + index, null,
								monitor);
						index = UIUtils.getFreeElementNameIndex(editor,
								rodinFile, IInvariant.ELEMENT_TYPE, prefix,
								index + 1);
						newInv.setLabel(name, monitor);
						newInv.setPredicateString(content);
						editor.addNewElement(newInv);
					}
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
	public static void newTheorems(final EventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String thmPrefix = UIUtils.getPrefix(editor,
					PrefixThmName.QUALIFIED_NAME, PrefixThmName.DEFAULT_PREFIX);
			int thmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), ITheorem.ELEMENT_TYPE, thmPrefix);
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(
					Display.getCurrent().getActiveShell(), "New Theorems",
					"Name and predicate", editor, ITheorem.ELEMENT_TYPE,
					thmPrefix, thmIndex);
			dialog.open();
			final String[] names = dialog.getNewNames();
			final String[] contents = dialog.getNewContents();

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					String prefix = UIUtils.getNamePrefix(editor,
							PrefixThmName.QUALIFIED_NAME,
							PrefixThmName.DEFAULT_PREFIX);
					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, ITheorem.ELEMENT_TYPE, prefix);
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newThm = (ITheorem) rodinFile.createInternalElement(
								ITheorem.ELEMENT_TYPE, prefix + index, null,
								monitor);
						index = UIUtils.getFreeElementNameIndex(editor,
								rodinFile, ITheorem.ELEMENT_TYPE, prefix,
								index + 1);
						newThm.setLabel(name, monitor);
						newThm.setPredicateString(content);
						editor.addNewElement(newThm);
					}
				}

			}, null);
		} catch (CoreException e) {
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
	public static void newAxioms(final EventBEditor editor,
			final IRodinFile rodinFile) {
		try {
			String axmPrefix = UIUtils.getPrefix(editor,
					PrefixAxmName.QUALIFIED_NAME, PrefixAxmName.DEFAULT_PREFIX);
			int axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix);
			ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(
					Display.getCurrent().getActiveShell(), "New Axioms",
					"Name and predicate", editor, IAxiom.ELEMENT_TYPE,
					axmPrefix, axmIndex);
			dialog.open();
			final String[] names = dialog.getNewNames();
			final String[] contents = dialog.getNewContents();
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					String prefix = UIUtils.getNamePrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					int index = UIUtils.getFreeElementNameIndex(editor,
							rodinFile, IAxiom.ELEMENT_TYPE, prefix);
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newAxm = (IAxiom) rodinFile.createInternalElement(
								IAxiom.ELEMENT_TYPE, prefix + index, null,
								monitor);
						index = UIUtils.getFreeElementNameIndex(editor,
								rodinFile, IAxiom.ELEMENT_TYPE, prefix,
								index + 1);
						newAxm.setLabel(name, monitor);
						newAxm.setPredicateString(content);
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
			IRodinProject prj = concreteFile.getRodinProject();
			return prj.getRodinFile(EventBPlugin.getMachineFileName(name));
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

}
