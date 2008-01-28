/*******************************************************************************
 * Copyright (c) 2005-2008 ETH Zurich.
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
import org.eventb.core.ICommentedElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixCstName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixEvtName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixInvName;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
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

	/**
	 * The debug flag. This is set by the option when the platform is launch.
	 * Client should not try to reset this flag.
	 */
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
					for (Iterator<?> it = ssel.iterator(); it.hasNext(); i++) {
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
	public static void addAction(final IEventBEditor<IMachineFile> editor,
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
									throws RodinDBException {
								String defaultPrefix = AttributeRelUISpecRegistry
										.getDefault().getDefaultPrefix(
												"org.eventb.core.actionLabel");
								String name = UIUtils.getFreeElementName(
										editor, event, IAction.ELEMENT_TYPE,
										defaultPrefix);
								String label = UIUtils.getFreeElementLabel(
										editor, event, IAction.ELEMENT_TYPE,
										defaultPrefix);
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
				} catch (RodinDBException e) {
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
	public static void addRefinesEvent(final IEventBEditor<IMachineFile> editor,
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
									throws RodinDBException {
								String name = UIUtils.getFreeElementName(
										editor, event,
										IRefinesEvent.ELEMENT_TYPE,
										"refinesEvent");
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
				} catch (RodinDBException e) {
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
	public static void addWitness(final IEventBEditor<IMachineFile> editor,
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
									throws RodinDBException {
								String defaultPrefix = AttributeRelUISpecRegistry
										.getDefault().getDefaultPrefix(
												"org.eventb.core.witnessLabel");
								String name = UIUtils.getFreeElementName(
										editor, event, IWitness.ELEMENT_TYPE,
										defaultPrefix);
								String label = UIUtils.getFreeElementLabel(
										editor, event, IWitness.ELEMENT_TYPE,
										defaultPrefix);
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
	public static void addGuard(final IEventBEditor<IMachineFile> editor,
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
									throws RodinDBException {
								String defaultPrefix = AttributeRelUISpecRegistry
										.getDefault().getDefaultPrefix(
												"org.eventb.core.guardLabel");
								String label = UIUtils.getFreeElementLabel(
										editor, event, IGuard.ELEMENT_TYPE,
										defaultPrefix);
								String name = UIUtils.getFreeElementName(
										editor, event, IGuard.ELEMENT_TYPE,
										defaultPrefix);
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
	public static void addLocalVariable(final IEventBEditor<IMachineFile> editor,
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
									throws RodinDBException {
								String defaultPrefix = AttributeRelUISpecRegistry
										.getDefault()
										.getDefaultPrefix(
												"org.eventb.core.variableIdentifier");
								String identifier = UIUtils
										.getFreeElementIdentifier(editor,
												event, IVariable.ELEMENT_TYPE,
												defaultPrefix);
								String name = UIUtils.getFreeElementName(
										editor, event, IVariable.ELEMENT_TYPE,
										defaultPrefix);
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
	public static void addVariable(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String defaultPrefix = AttributeRelUISpecRegistry
									.getDefault()
									.getDefaultPrefix(
											"org.eventb.core.variableIdentifier");

							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IVariable.ELEMENT_TYPE,
									defaultPrefix);
							String identifier = UIUtils
									.getFreeElementIdentifier(editor,
											rodinFile, IVariable.ELEMENT_TYPE,
											defaultPrefix);
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
	public static void addInvariant(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String label = UIUtils.getFreeElementLabel(editor,
									rodinFile, IInvariant.ELEMENT_TYPE,
									PrefixInvName.DEFAULT_PREFIX);
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IInvariant.ELEMENT_TYPE,
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
	public static void addTheorem(final IEventBEditor<?> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String defaultPrefix = AttributeRelUISpecRegistry
									.getDefault().getDefaultPrefix(
											"org.eventb.core.theoremLabel");
							String label = UIUtils.getFreeElementLabel(editor,
									rodinFile, ITheorem.ELEMENT_TYPE,
									defaultPrefix);
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, ITheorem.ELEMENT_TYPE,
									defaultPrefix);
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
	public static void addEvent(final IEventBEditor<IMachineFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {

					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String evtName = UIUtils.getFreeElementName(editor,
									rodinFile, IEvent.ELEMENT_TYPE,
									PrefixEvtName.DEFAULT_PREFIX);
							String evtLabel = UIUtils.getFreeElementLabel(
									editor, rodinFile, IEvent.ELEMENT_TYPE,
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

							String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
									.getDefaultPrefix("org.eventb.core.variableIdentifier");
							String namePrefix = UIUtils.getNamePrefix(editor,
									IVariable.ELEMENT_TYPE,
									defaultPrefix);
							int nameIndex =EventBUtils.getFreeChildNameIndex(
									newEvt, IVariable.ELEMENT_TYPE,
									namePrefix);

							String prefix = UIUtils.getFreeElementIdentifier(
									editor, newEvt, IVariable.ELEMENT_TYPE,
									defaultPrefix);

							int index = UIUtils.getFreeElementIdentifierIndex(
									newEvt, IVariable.ELEMENT_TYPE,
									prefix);

							for (int i = 0; i < 3; i++) {
								newVar = newEvt.getInternalElement(
										IVariable.ELEMENT_TYPE, namePrefix
												+ nameIndex);
								assert !newVar.exists();
								newVar.create(null, monitor);
								nameIndex = EventBUtils.getFreeChildNameIndex(
										newEvt, IVariable.ELEMENT_TYPE,
										namePrefix, nameIndex + 1);

								newVar.setIdentifierString(prefix + index,
										new NullProgressMonitor());
								index = UIUtils.getFreeElementIdentifierIndex(
										newEvt, IVariable.ELEMENT_TYPE,
										prefix, index + 1);
								editor.addNewElement(newVar);
							}

							defaultPrefix = AttributeRelUISpecRegistry.getDefault()
									.getDefaultPrefix("org.eventb.core.guardLabel");
							namePrefix = UIUtils.getNamePrefix(editor,
									IGuard.ELEMENT_TYPE, defaultPrefix);
							nameIndex = EventBUtils.getFreeChildNameIndex(newEvt,
									IGuard.ELEMENT_TYPE, namePrefix);
							prefix = UIUtils.getFreeElementLabel(editor,
									newEvt, IGuard.ELEMENT_TYPE,
									defaultPrefix);

							index = UIUtils.getFreeElementLabelIndex(editor,
									newEvt, IGuard.ELEMENT_TYPE, prefix);
							for (int i = 0; i < 3; i++) {
								newGrd = newEvt.getInternalElement(
										IGuard.ELEMENT_TYPE, namePrefix
												+ nameIndex);
								assert !newGrd.exists();
								newGrd.create(null, monitor);
								nameIndex = EventBUtils.getFreeChildNameIndex(
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

							defaultPrefix = AttributeRelUISpecRegistry.getDefault()
									.getDefaultPrefix("org.eventb.core.actionLabel");
							namePrefix = UIUtils.getNamePrefix(editor,
									IAction.ELEMENT_TYPE,
									defaultPrefix);
							nameIndex = EventBUtils.getFreeChildNameIndex(newEvt,
									IAction.ELEMENT_TYPE, namePrefix);
							prefix = UIUtils.getFreeElementLabel(editor,
									newEvt, IAction.ELEMENT_TYPE,
									defaultPrefix);

							index = UIUtils.getFreeElementLabelIndex(editor,
									newEvt, IAction.ELEMENT_TYPE, prefix);
							for (int i = 0; i < 3; i++) {
								newAct = newEvt.getInternalElement(
										IAction.ELEMENT_TYPE, namePrefix
												+ nameIndex);
								assert !newAct.exists();
								newAct.create(null, monitor);
								nameIndex = EventBUtils.getFreeChildNameIndex(
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
	public static void addAxiom(final IEventBEditor<IContextFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
									.getDefaultPrefix("org.eventb.core.axiomLabel");
							String label = UIUtils.getFreeElementLabel(editor,
									rodinFile, IAxiom.ELEMENT_TYPE,
									defaultPrefix);
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IAxiom.ELEMENT_TYPE,
									defaultPrefix);
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
	public static void addConstant(final IEventBEditor<IContextFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, IConstant.ELEMENT_TYPE,
									PrefixCstName.DEFAULT_PREFIX);
							String identifier = UIUtils
									.getFreeElementIdentifier(editor,
											rodinFile, IConstant.ELEMENT_TYPE,
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
	public static void addSet(final IEventBEditor<IContextFile> editor,
			final TreeViewer viewer) {
		BusyIndicator.showWhile(viewer.getTree().getDisplay(), new Runnable() {
			public void run() {
				final IRodinFile rodinFile = editor.getRodinInput();
				try {
					RodinCore.run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor)
								throws RodinDBException {
							String defaultPrefix = AttributeRelUISpecRegistry
									.getDefault()
									.getDefaultPrefix(
											"org.eventb.core.carrierSetIdentifier");
							String name = UIUtils.getFreeElementName(editor,
									rodinFile, ICarrierSet.ELEMENT_TYPE,
									defaultPrefix);
							String identifier = UIUtils
									.getFreeElementIdentifier(editor,
											rodinFile,
											ICarrierSet.ELEMENT_TYPE,
											defaultPrefix);
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
	public static void intelligentNewVariable(final IEventBEditor<IMachineFile> editor,
			final IRodinFile rodinFile) {
		try {

			String prefix = UIUtils.getPrefix(editor,
					IInvariant.ELEMENT_TYPE,
					PrefixInvName.DEFAULT_PREFIX);
			int index = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, prefix);

			final IntelligentNewVariableInputDialog dialog = new IntelligentNewVariableInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Variable", prefix, index);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {
					createNewVariable(editor, dialog.getName(), monitor);
					createNewInvariant(editor, dialog.getInvariants(), monitor);
					
					String actName = dialog.getInitActionName();
					String actSub = dialog.getInitActionSubstitution();
					createNewInitialisationAction(editor, actName, actSub, monitor);
				}

			}, new NullProgressMonitor());

		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	protected static void createNewInitialisationAction(final IEventBEditor<IMachineFile> editor,
			final String actLabel, final String actSub, final IProgressMonitor monitor)
			throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor m) throws RodinDBException {
				IRodinFile rodinFile = editor.getRodinInput();
				if (actSub != null) {
					IRodinElement[] events = rodinFile
							.getChildrenOfType(IEvent.ELEMENT_TYPE);
					boolean newInit = true;
					for (IRodinElement event : events) {
						IEvent element = (IEvent) event;
						if (element.getLabel().equals("INITIALISATION")) {
							newInit = false;

							String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
									.getDefaultPrefix("org.eventb.core.actionLabel");
							String actName = UIUtils.getFreeElementName(editor,
									element, IAction.ELEMENT_TYPE,
									defaultPrefix);
							newAct = element.getInternalElement(
									IAction.ELEMENT_TYPE, actName);
							assert !newAct.exists();
							newAct.create(null, m);
							newAct.setLabel(actLabel, m);
							newAct.setAssignmentString(actSub, m);

							editor.addNewElement(newAct);
							break;
						}
					}
					if (newInit) {
						newEvt = rodinFile.getInternalElement(
								IEvent.ELEMENT_TYPE, UIUtils
										.getFreeElementName(editor, rodinFile,
												IEvent.ELEMENT_TYPE,
												PrefixEvtName.DEFAULT_PREFIX));
						assert !newEvt.exists();
						newEvt.setLabel("INITIALISATION", m);
						String defaultPrefix = AttributeRelUISpecRegistry
								.getDefault().getDefaultPrefix(
										"org.eventb.core.actionLabel");
						String actName = UIUtils.getFreeElementName(editor,
								newEvt, IAction.ELEMENT_TYPE, defaultPrefix);
						newAct = newEvt.getInternalElement(
								IAction.ELEMENT_TYPE, actName);
						assert !newAct.exists();
						newAct.create(null, m);
						newAct.setLabel(actLabel, m);
						newAct.setAssignmentString(actSub, m);
						editor.addNewElement(newAct);
					}
				}
			}

		}, monitor);
	}

	protected static void createNewInvariant(final IEventBEditor<IMachineFile> editor,
			final Collection<Pair<String, String>> invariants, final IProgressMonitor monitor)
			throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor m) throws RodinDBException {
				IRodinFile rodinFile = editor.getRodinInput();
				String invPrefix = UIUtils.getNamePrefix(editor,
						IInvariant.ELEMENT_TYPE, PrefixInvName.DEFAULT_PREFIX);
				int invIndex = EventBUtils.getFreeChildNameIndex(rodinFile,
						IInvariant.ELEMENT_TYPE, invPrefix);
				if (invariants != null) {
					for (Pair<String, String> pair : invariants) {
						newInv = rodinFile.getInternalElement(
								IInvariant.ELEMENT_TYPE, invPrefix + invIndex);
						assert !newInv.exists();
						newInv.create(null, m);
						invIndex = EventBUtils.getFreeChildNameIndex(rodinFile,
								IInvariant.ELEMENT_TYPE, invPrefix,
								invIndex + 1);
						newInv.setLabel(pair.getFirst(), m);
						newInv.setPredicateString(pair.getSecond(),
								null);
						editor.addNewElement(newInv);
					}
				}
			}
			
		}, monitor);
	}

	public static void createNewVariable(final IEventBEditor<IMachineFile> editor, final String name,
			final IProgressMonitor monitor) throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor m) throws RodinDBException {
				IRodinFile rodinFile = editor.getRodinInput();
				String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
						.getDefaultPrefix("org.eventb.core.variableIdentifier");

				newVar = rodinFile.getInternalElement(
						IVariable.ELEMENT_TYPE, UIUtils.getFreeElementName(
								editor, rodinFile, IVariable.ELEMENT_TYPE,
								defaultPrefix));
				assert !newVar.exists();
				newVar.create(null, m);

				newVar.setIdentifierString(name, new NullProgressMonitor());
				editor.addNewElement(newVar);
			}
			
		}, monitor);
		
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
	public static void intelligentNewConstant(final IEventBEditor<IContextFile> editor,
			final IRodinFile rodinFile) {
		try {
			final IntelligentNewConstantInputDialog dialog = new IntelligentNewConstantInputDialog(
					editor, Display.getCurrent().getActiveShell(),
					"New Constant");

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {

					String identifier = dialog.getIdentifier();
					createNewConstant(editor, identifier, monitor);

					String [] axmNames = dialog.getAxiomNames();
					String [] axmSubs = dialog.getAxiomSubtitutions();
					createNewAxioms(editor, axmNames, axmSubs, monitor);
				}

			}, new NullProgressMonitor());

		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	protected static void createNewAxioms(final IEventBEditor<IContextFile> editor,
			final String[] axmNames, final String[] axmSubs,
			IProgressMonitor monitor) throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor pm) throws RodinDBException {
				IRodinFile rodinFile = editor.getRodinInput();
				String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
						.getDefaultPrefix("org.eventb.core.axiomLabel");

				String axmName = UIUtils.getNamePrefix(editor,
						IAxiom.ELEMENT_TYPE, defaultPrefix);
				int axmIndex = EventBUtils.getFreeChildNameIndex(rodinFile,
						IAxiom.ELEMENT_TYPE, axmName);
				for (int i = 0; i < axmNames.length; ++i) {
					newAxm = rodinFile.getInternalElement(IAxiom.ELEMENT_TYPE,
							axmName + axmIndex);
					assert !newAxm.exists();
					newAxm.create(null, pm);
					axmIndex = EventBUtils.getFreeChildNameIndex(rodinFile,
							IAxiom.ELEMENT_TYPE, axmName, axmIndex + 1);
					newAxm.setLabel(axmNames[i], pm);
					newAxm.setPredicateString(axmSubs[i], null);
					editor.addNewElement(newAxm);
				}
			}

		}, monitor);
	}

	protected static void createNewConstant(final IEventBEditor<IContextFile> editor,
			final String name, IProgressMonitor monitor) throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor pm) throws RodinDBException {
				IRodinFile rodinFile = editor.getRodinInput();
				newCst = rodinFile.getInternalElement(IConstant.ELEMENT_TYPE,
						UIUtils.getFreeElementName(editor, rodinFile,
								IConstant.ELEMENT_TYPE,
								PrefixCstName.DEFAULT_PREFIX));
				assert !newCst.exists();
				newCst.create(null, pm);

				newCst.setIdentifierString(name, new NullProgressMonitor());
				editor.addNewElement(newCst);
			}

		}, monitor);
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
			String invPrefix = UIUtils.getPrefix(editor,
					IInvariant.ELEMENT_TYPE, PrefixInvName.DEFAULT_PREFIX);

			int invIndex = UIUtils.getFreeElementLabelIndex(editor, rodinFile,
					IInvariant.ELEMENT_TYPE, invPrefix);
			final ElementNameContentInputDialog<IInvariant> dialog =
				new ElementNameContentInputDialog<IInvariant>(
					Display.getCurrent().getActiveShell(), "New Invariants",
					"Label(s) and predicate(s)", editor, IInvariant.ELEMENT_TYPE,
					invPrefix, invIndex);

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {

					String prefix = UIUtils.getNamePrefix(editor,
							IInvariant.ELEMENT_TYPE,
							PrefixInvName.DEFAULT_PREFIX);
					int index = EventBUtils.getFreeChildNameIndex(rodinFile,
							IInvariant.ELEMENT_TYPE, prefix);
					String[] names = dialog.getNewNames();
					String[] contents = dialog.getNewContents();
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newInv = rodinFile.getInternalElement(
								IInvariant.ELEMENT_TYPE, prefix + index);
						assert !newInv.exists();
						newInv.create(null, monitor);
						index = EventBUtils.getFreeChildNameIndex(rodinFile,
								IInvariant.ELEMENT_TYPE, prefix, index + 1);
						newInv.setLabel(name, monitor);
						newInv.setPredicateString(content, null);
						editor.addNewElement(newInv);
					}
				}

			}, null);
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
		try {
			final NewVariantInputDialog dialog = new NewVariantInputDialog(
					Display.getCurrent().getActiveShell(), "New Variant",
					"Expression");

			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws RodinDBException {
					String prefix = UIUtils.getNamePrefix(editor,
							IVariant.ELEMENT_TYPE,
							"variant");
					int index = EventBUtils.getFreeChildNameIndex(rodinFile,
							IVariant.ELEMENT_TYPE, prefix);
					newVariant = rodinFile.getInternalElement(
							IVariant.ELEMENT_TYPE, prefix + index);
					assert !newVariant.exists();
					newVariant.create(null, monitor);
					String expression = dialog.getExpression();

					newVariant.setExpressionString(expression, monitor);
					editor.addNewElement(newVariant);
				}
			}, null);
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
	public static void newTheorems(final IEventBEditor<?> editor,
			final IRodinFile rodinFile) {
		try {
			final String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
					.getDefaultPrefix("org.eventb.core.theoremLabel");
			String thmPrefix = UIUtils.getPrefix(editor,
					ITheorem.ELEMENT_TYPE, defaultPrefix);
			int thmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), ITheorem.ELEMENT_TYPE, thmPrefix);
			final ElementNameContentInputDialog<ITheorem> dialog =
				new ElementNameContentInputDialog<ITheorem>(
					Display.getCurrent().getActiveShell(), "New Theorems",
					"Label(s) and predicate(s)", editor, ITheorem.ELEMENT_TYPE,
					thmPrefix, thmIndex);
			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {
					String prefix = UIUtils.getNamePrefix(editor,
							ITheorem.ELEMENT_TYPE,
							defaultPrefix);
					int index = EventBUtils.getFreeChildNameIndex(rodinFile,
							ITheorem.ELEMENT_TYPE, prefix);
					String[] names = dialog.getNewNames();
					String[] contents = dialog.getNewContents();

					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newThm = rodinFile.getInternalElement(
								ITheorem.ELEMENT_TYPE, prefix + index);
						newThm.create(null, monitor);
						index = EventBUtils.getFreeChildNameIndex(rodinFile,
								ITheorem.ELEMENT_TYPE, prefix, index + 1);
						newThm.setLabel(name, monitor);
						newThm.setPredicateString(content, null);
						editor.addNewElement(newThm);
					}
				}

			}, null);
		} catch (RodinDBException e) {
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
		try {
			final NewEventInputDialog dialog = new NewEventInputDialog(editor,
					Display.getCurrent().getActiveShell(), "New Events");

			dialog.open();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws RodinDBException {
					String name = dialog.getLabel();
					IEvent evt = createNewEvent(editor, name, pm);

					String[] varNames = dialog.getParameters();
					createNewParameters(editor, evt, varNames, pm);

					String[] grdNames = dialog.getGrdLabels();
					String[] grdPredicates = dialog.getGrdPredicates();
					createNewGuards(editor, evt, grdNames, grdPredicates, pm);

					String[] actNames = dialog.getActLabels();
					String[] actSubstitutions = dialog.getActSubstitutions();
					createNewActions(editor, evt, actNames, actSubstitutions,
							pm);
				}

			}, monitor);

		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	protected static void createNewActions(IEventBEditor<IMachineFile> editor,
			IEvent evt, String[] actNames, String[] actSubstitutions,
			IProgressMonitor pm) throws RodinDBException {
		String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
				.getDefaultPrefix("org.eventb.core.actionLabel");
		String actPrefix = UIUtils.getNamePrefix(editor, IAction.ELEMENT_TYPE,
				defaultPrefix);
		int actIndex = EventBUtils.getFreeChildNameIndex(evt, IAction.ELEMENT_TYPE,
				actPrefix);
		for (int i = 0; i < actNames.length; i++) {
			IAction act = evt.getAction(actPrefix + actIndex);
			act.create(null, pm);
			act.setLabel(actNames[i], pm);
			act.setAssignmentString(actSubstitutions[i], pm);
			editor.addNewElement(act);
			actIndex = EventBUtils.getFreeChildNameIndex(evt,
					IAction.ELEMENT_TYPE, actPrefix, actIndex);
		}
	}

	protected static void createNewGuards(IEventBEditor<IMachineFile> editor,
			IEvent evt, String[] grdNames, String[] grdPredicates,
			IProgressMonitor pm) throws RodinDBException {
		String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
				.getDefaultPrefix("org.eventb.core.guardLabel");
		String grdPrefix = UIUtils.getNamePrefix(editor, IGuard.ELEMENT_TYPE,
				defaultPrefix);
		int grdIndex = EventBUtils.getFreeChildNameIndex(evt, IGuard.ELEMENT_TYPE,
				grdPrefix);
		for (int i = 0; i < grdNames.length; i++) {
			IGuard grd = evt.getGuard(grdPrefix + grdIndex);
			grd.create(null, pm);
			grd.setLabel(grdNames[i], pm);
			grd.setPredicateString(grdPredicates[i], null);
			editor.addNewElement(grd);
			grdIndex = EventBUtils.getFreeChildNameIndex(evt,
					IGuard.ELEMENT_TYPE, grdPrefix, grdIndex + 1);
		}
	}

	protected static void createNewParameters(IEventBEditor<IMachineFile> editor,
			IEvent evt, String[] identifiers, IProgressMonitor pm)
			throws RodinDBException {
		String defaultPrefix = AttributeRelUISpecRegistry.getDefault()
				.getDefaultPrefix("org.eventb.core.variableIdentifier");
		String varPrefix = UIUtils.getNamePrefix(editor,
				IVariable.ELEMENT_TYPE,
				defaultPrefix);

		int varIndex = EventBUtils.getFreeChildNameIndex(evt,
				IVariable.ELEMENT_TYPE, varPrefix);
		for (String varName : identifiers) {
			IVariable var = evt.getVariable(varPrefix + varIndex);
			var.create(null, pm);
			var.setIdentifierString(varName, pm);
			editor.addNewElement(var);
			varIndex = EventBUtils.getFreeChildNameIndex(evt,
					IVariable.ELEMENT_TYPE, varPrefix, varIndex + 1);
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
			
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws RodinDBException {
					String setPrefix = UIUtils.getNamePrefix(editor,
							IConstant.ELEMENT_TYPE,
							PrefixCstName.DEFAULT_PREFIX);
					int setIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
							ICarrierSet.ELEMENT_TYPE, setPrefix);
					Collection<String> names = dialog.getAttributes();
					
					for (String name : names) {
						ICarrierSet set = ctxFile.getCarrierSet(setPrefix
												+ setIndex);
						set.create(null, pm);
						set.setIdentifierString(name, pm);
						editor.addNewElement(set);
						setIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
								ICarrierSet.ELEMENT_TYPE, setPrefix,
								setIndex + 1);
					}
				}

			}, monitor);
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
			if (name == null)
				return;

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws RodinDBException {
					String[] elements = dialog.getElements();

					final String setName = UIUtils.getFreeElementName(editor, ctxFile,
							ICarrierSet.ELEMENT_TYPE,
							defaultPrefix);
					final ICarrierSet set = ctxFile.getCarrierSet(setName);
					set.create(null, pm);
					set.setIdentifierString(name, pm);
					editor.addNewElement(set);

					final int nbElements = elements.length;
					if (nbElements == 0)
						return;

					String defaultAxmPrefix = AttributeRelUISpecRegistry.getDefault()
							.getDefaultPrefix("org.eventb.core.axiomLabel");
					String namePrefix = UIUtils.getNamePrefix(editor,
							IAxiom.ELEMENT_TYPE, defaultAxmPrefix);
					int nameIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
							IAxiom.ELEMENT_TYPE, namePrefix);

					String labelPrefix = UIUtils.getPrefix(editor,
							IAxiom.ELEMENT_TYPE, defaultAxmPrefix);
					int labelIndex = UIUtils.getFreeElementLabelIndex(editor,
							ctxFile, IAxiom.ELEMENT_TYPE, labelPrefix);
					// String axmName = namePrefix + nameIndex;

					newAxm = ctxFile.getAxiom(namePrefix + nameIndex);
					newAxm.create(null, null);
					newAxm.setLabel(labelPrefix + labelIndex, pm);
					StringBuilder axmPred = new StringBuilder(name);
					axmPred.append(" = {");

					String cstPrefix = UIUtils.getNamePrefix(editor,
							IConstant.ELEMENT_TYPE,
							PrefixCstName.DEFAULT_PREFIX);
					int cstIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
							IConstant.ELEMENT_TYPE, cstPrefix);
					String axmSep = "";
					for (String element : elements) {
						IConstant cst = ctxFile.getConstant(cstPrefix + cstIndex);
						cst.create(null, pm);
						cst.setIdentifierString(element, pm);
						editor.addNewElement(cst);
						cstIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
								IConstant.ELEMENT_TYPE, cstPrefix, cstIndex + 1);

						nameIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
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
							nameIndex = EventBUtils.getFreeChildNameIndex(ctxFile,
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

			String axmPrefix = UIUtils.getPrefix(editor,
					IAxiom.ELEMENT_TYPE, defaultPrefix);
			int axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
					.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix);
			final ElementNameContentInputDialog<IAxiom> dialog =
				new ElementNameContentInputDialog<IAxiom>(
					Display.getCurrent().getActiveShell(), "New Axioms",
					"Label(s) and predicate(s)", editor, IAxiom.ELEMENT_TYPE,
					axmPrefix, axmIndex);
			dialog.open();
			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws RodinDBException {
					String prefix = UIUtils.getNamePrefix(editor,
							IAxiom.ELEMENT_TYPE,
							defaultPrefix);
					String[] names = dialog.getNewNames();
					String[] contents = dialog.getNewContents();

					int index = EventBUtils.getFreeChildNameIndex(rodinFile,
							IAxiom.ELEMENT_TYPE, prefix);
					for (int i = 0; i < names.length; i++) {
						String name = names[i];
						String content = contents[i];
						newAxm = rodinFile.getInternalElement(
								IAxiom.ELEMENT_TYPE, prefix + index);
						assert !newAxm.exists();
						newAxm.create(null, monitor);
						index = EventBUtils.getFreeChildNameIndex(rodinFile,
								IAxiom.ELEMENT_TYPE, prefix, index + 1);
						newAxm.setLabel(name, monitor);
						newAxm.setPredicateString(content, null);
						editor.addNewElement(newAxm);
					}
				}

			}, null);

		} catch (RodinDBException e) {
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
			return UIUtils.getPrefix(editor, IAction.ELEMENT_TYPE,
			defaultPrefix) + 1;
		else {
			return UIUtils.getFreeElementLabel(editor, initialisation,
					IAction.ELEMENT_TYPE, defaultPrefix);
		}
	}

	
	public static IEvent createNewEvent(final IEventBEditor<IMachineFile> editor, final String label,
			IProgressMonitor monitor) throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {

			public void run(IProgressMonitor pm) throws RodinDBException {
				IRodinFile rodinFile = editor.getRodinInput();
				String evtName = UIUtils.getFreeElementName(editor, rodinFile,
						IEvent.ELEMENT_TYPE, PrefixEvtName.DEFAULT_PREFIX);
				newEvt = ((IMachineFile) rodinFile).getEvent(evtName);
				newEvt.create(null, pm);
				newEvt.setLabel(label, pm);
				newEvt.setConvergence(IConvergenceElement.Convergence.ORDINARY,
						pm);
				newEvt.setInherited(false, pm);
				editor.addNewElement(newEvt);
			}

		}, monitor);
		return newEvt;
	}

}
