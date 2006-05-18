/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBImageDescriptor;
import org.eventb.internal.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the tree
 *         viewer for Events (Rodin elements).
 */
public class EventBMasterSectionActionGroup extends ActionGroup {

	// The Event-B Editor.
	private EventBEditor editor;

	// The tree viewer in the master section
	private TreeViewer viewer;

	// Some actions
	protected Action addVariable;

	protected Action addInvariant;

	protected Action addTheorem;

	protected Action addEvent;

	protected Action addSet;

	protected Action addConstant;

	protected Action addAxiom;

	protected Action addLocalVariable;

	protected Action addGuard;

	protected Action addAction;

	protected Action delete;

	protected Action handleUp;

	protected Action handleDown;

	/**
	 * Constructor: Create the actions
	 * <p>
	 * 
	 * @param eventSection
	 *            The Event Master section
	 */
	public EventBMasterSectionActionGroup(EventBEditor eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		addVariable = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement[] vars = rodinFile
											.getChildrenOfType(IVariable.ELEMENT_TYPE);
									for (counter = 1; counter <= vars.length; counter++) {
										IInternalElement element = rodinFile
												.getInternalElement(
														IVariable.ELEMENT_TYPE,
														"var" + counter);
										if (!element.exists())
											break;
									}
									IRodinElement var = rodinFile
											.createInternalElement(
													IVariable.ELEMENT_TYPE,
													"var" + counter, null, null);
									editor.addNewElement(var);
									((EventBEditableTreeViewer) viewer)
											.edit(var);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addVariable.setText("New &Variable");
		addVariable.setToolTipText("Create a new variable");
		addVariable.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addInvariant = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement[] invs = rodinFile
											.getChildrenOfType(IInvariant.ELEMENT_TYPE);
									for (counter = 1; counter <= invs.length; counter++) {
										IInternalElement element = rodinFile
												.getInternalElement(
														IInvariant.ELEMENT_TYPE,
														"inv" + counter);
										if (!element.exists())
											break;
									}
									IInternalElement inv = rodinFile
											.createInternalElement(
													IInvariant.ELEMENT_TYPE,
													"inv" + counter, null, null);
									inv.setContents(EventBUIPlugin.INV_DEFAULT);
									editor.addNewElement(inv);
									((EventBEditableTreeViewer) viewer)
											.edit(inv);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addInvariant.setText("New &Invariant");
		addInvariant.setToolTipText("Create a new invariant");
		addInvariant.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addTheorem = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement[] thms = rodinFile
											.getChildrenOfType(ITheorem.ELEMENT_TYPE);
									for (counter = 1; counter <= thms.length; counter++) {
										IInternalElement element = rodinFile
												.getInternalElement(
														ITheorem.ELEMENT_TYPE,
														"thm" + counter);
										if (!element.exists())
											break;
									}
									IInternalElement thm = rodinFile
											.createInternalElement(
													ITheorem.ELEMENT_TYPE,
													"thm" + counter, null, null);
									thm.setContents(EventBUIPlugin.THM_DEFAULT);
									editor.addNewElement(thm);
									((EventBEditableTreeViewer) viewer)
											.edit(thm);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addTheorem.setText("New &Theorem");
		addTheorem.setToolTipText("Create a new theorem");
		addTheorem.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addEvent = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement[] vars = rodinFile
											.getChildrenOfType(IEvent.ELEMENT_TYPE);
									for (counter = 1; counter <= vars.length; counter++) {
										IInternalElement element = rodinFile
												.getInternalElement(
														IEvent.ELEMENT_TYPE,
														"evt" + counter);
										if (!element.exists())
											break;
									}
									String[] varNames = { "var1", "var2",
											"var3" };
									String[] grdNames = { "grd1", "grd2",
											"grd3" };
									String[] actions = { "act1", "act2", "act3" };

									IInternalElement event = rodinFile
											.createInternalElement(
													IEvent.ELEMENT_TYPE, "evt"
															+ counter, null,
													null);
									editor.addNewElement(event);
									for (String varName : varNames) {
										IInternalElement var = event
												.createInternalElement(
														IVariable.ELEMENT_TYPE,
														varName, null, null);
										editor.addNewElement(var);
									}

									for (int i = 0; i < grdNames.length; i++) {
										IInternalElement grd = event
												.createInternalElement(
														IGuard.ELEMENT_TYPE,
														grdNames[i], null, null);
										grd
												.setContents(EventBUIPlugin.GRD_DEFAULT);
										editor.addNewElement(grd);
									}

									IInternalElement act = null;
									for (String action : actions) {
										act = event.createInternalElement(
												IAction.ELEMENT_TYPE, null,
												null, null);
										act.setContents(action);
										editor.addNewElement(act);
									}
									// Leaf leaf = (Leaf) TreeSupports.findItem(
									//		viewer.getTree(), event).getData();
									viewer.setExpandedState(event, true);
									viewer.reveal(act);
									// viewer.reveal(TreeSupports.findItem(
									//		viewer.getTree(), act).getData());
									((EventBEditableTreeViewer) viewer)
											.edit(event);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addEvent.setText("New &Event");
		addEvent.setToolTipText("Create a new event");
		addEvent.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addLocalVariable = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer
											.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports
												.getEvent(obj);
										int counter = 1;
										IRodinElement[] vars = event
												.getChildrenOfType(IVariable.ELEMENT_TYPE);
										for (counter = 1; counter <= vars.length; counter++) {
											IInternalElement element = event
													.getInternalElement(
															IVariable.ELEMENT_TYPE,
															"var" + counter);
											if (!element.exists())
												break;
										}
										IInternalElement var = event
												.createInternalElement(
														IVariable.ELEMENT_TYPE,
														"var" + counter, null,
														null);
										editor.addNewElement(var);
										viewer.setExpandedState(TreeSupports
												.findItem(viewer.getTree(),
														event).getData(), true);
										select(var, 0);
									}
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addLocalVariable.setText("New &Local Variable");
		addLocalVariable.setToolTipText("Create a new (local) variable");
		addLocalVariable.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addGuard = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer
											.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports
												.getEvent(obj);
										int counter = 1;
										IRodinElement[] vars = event
												.getChildrenOfType(IGuard.ELEMENT_TYPE);
										for (counter = 1; counter <= vars.length; counter++) {
											IInternalElement element = event
													.getInternalElement(
															IGuard.ELEMENT_TYPE,
															"grd" + counter);
											if (!element.exists())
												break;
										}
										IInternalElement grd = event
												.createInternalElement(
														IGuard.ELEMENT_TYPE,
														"grd" + counter, null,
														null);
										grd
												.setContents(EventBUIPlugin.GRD_DEFAULT);
										editor.addNewElement(grd);
										viewer.setExpandedState(TreeSupports
												.findItem(viewer.getTree(),
														event).getData(), true);
										select(grd, 1);
									}
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addGuard.setText("New &Guard");
		addGuard.setToolTipText("Create a new guard");
		addGuard.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer
											.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports
												.getEvent(obj);
										IInternalElement act = event
												.createInternalElement(
														IAction.ELEMENT_TYPE,
														null, null, null);
										act.setContents(EventBUIPlugin.SUB_DEFAULT);
										editor.addNewElement(act);
										viewer.setExpandedState(TreeSupports
												.findItem(viewer.getTree(),
														event).getData(), true);
										select(act, 1);
									}
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addAction.setText("New &Action");
		addAction.setToolTipText("Create a new action");
		addAction.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addSet = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement [] vars = rodinFile.getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
									for (counter = 1; counter <= vars.length; counter++) {
										IInternalElement element = rodinFile.getInternalElement(ICarrierSet.ELEMENT_TYPE, "set"+counter);
										if (!element.exists()) break;
									}
									IRodinElement set = rodinFile
											.createInternalElement(
													ICarrierSet.ELEMENT_TYPE,
													"set" + counter,
													null, null);
									editor.addNewElement(set);
									((EventBEditableTreeViewer) viewer)
											.edit(set);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addSet.setText("New Carrier &Set");
		addSet.setToolTipText("Create a new carrier set");
		addSet.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addConstant = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement [] vars = rodinFile.getChildrenOfType(IConstant.ELEMENT_TYPE);
									for (counter = 1; counter <= vars.length; counter++) {
										IInternalElement element = rodinFile.getInternalElement(IConstant.ELEMENT_TYPE, "cst" + counter);
										if (!element.exists()) break;
									}
									IInternalElement cst = rodinFile
											.createInternalElement(
													IConstant.ELEMENT_TYPE,
													"cst" + counter,
													null, null);
									editor.addNewElement(cst);
									((EventBEditableTreeViewer) viewer)
											.edit(cst);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addConstant.setText("New &Constant");
		addConstant.setToolTipText("Create a new constant");
		addConstant.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		addAxiom = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IRodinFile rodinFile = editor.getRodinInput();
								try {
									int counter = 1;
									IRodinElement [] vars = rodinFile.getChildrenOfType(IAxiom.ELEMENT_TYPE);
									for (counter = 1; counter <= vars.length; counter++) {
										IInternalElement element = rodinFile.getInternalElement(IAxiom.ELEMENT_TYPE, "axm" + counter);
										if (!element.exists()) break;
									}
									IInternalElement axm = rodinFile
											.createInternalElement(
													IAxiom.ELEMENT_TYPE, "axm"
															+ counter,
													null, null);
									axm.setContents(EventBUIPlugin.AXM_DEFAULT);
									editor.addNewElement(axm);
									((EventBEditableTreeViewer) viewer)
											.edit(axm);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		addAxiom.setText("New &Axiom");
		addAxiom.setToolTipText("Create a new axiom");
		addAxiom.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		delete = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer
											.getSelection();
									IRodinElement[] elements = new IRodinElement[ssel
											.size()];
									int i = 0;
									for (Iterator it = ssel.iterator(); it
											.hasNext(); i++) {
										Object obj = it.next();
										elements[i] = ((Leaf) obj).getElement();
									}
									EventBUIPlugin.getRodinDatabase().delete(
											elements, true, null);
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		delete.setText("&Delete");
		delete.setToolTipText("Delete selected element");
		delete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		handleUp = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								Tree tree = viewer.getTree();
								TreeItem[] items = tree.getSelection();
								TreeItem item = items[0];
								TreeItem prev = TreeSupports.findPrevItem(tree,
										item);
								IRodinElement currObj = ((Leaf) item.getData())
										.getElement();
								IRodinElement prevObj = ((Leaf) prev.getData())
										.getElement();

								try {
									((IInternalElement) currObj).move(currObj
											.getParent(), prevObj, null, true,
											null);
									TreeItem newItem = TreeSupports.findItem(
											tree, currObj);
									viewer
											.setSelection(new StructuredSelection(
													newItem.getData()));
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						});
			}
		};
		handleUp.setText("&Up");
		handleUp.setToolTipText("Move the element up");
		handleUp.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		handleDown = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								Tree tree = viewer.getTree();
								TreeItem[] items = tree.getSelection();
								TreeItem item = items[0];
								TreeItem next = TreeSupports.findNextItem(tree,
										item);
								IRodinElement currObj = ((Leaf) item.getData())
										.getElement();
								IRodinElement nextObj = ((Leaf) next.getData())
										.getElement();

								try {
									((IInternalElement) nextObj).move(nextObj
											.getParent(), currObj, null, false,
											null);
									TreeItem newItem = TreeSupports.findItem(
											tree, currObj);
									viewer
											.setSelection(new StructuredSelection(
													newItem.getData()));
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
								return;
							}
						});
			}
		};
		handleDown.setText("D&own");
		handleDown.setToolTipText("Move the element down");
		handleDown.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));
	}

	/**
	 * Fill the context menu with the actions create initially.
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();

				if (((Leaf) obj).getElement() instanceof IEvent) {
					menu.add(addLocalVariable);
					menu.add(addGuard);
					menu.add(addAction);
					// MenuManager newMenu = new MenuManager("&New");
					// newMenu.add(addLocalVariable);
					// newMenu.add(addGuard);
					// newMenu.add(addAction);
					// menu.add(newMenu);
				}
			}
			menu.add(delete);
			// menu.add(deleteAction);
			// menu.add(new Separator());
			// drillDownAdapter.addNavigationActions(menu);
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}
	}

	private void select(Object obj, int column) throws RodinDBException {
		// UIUtils.debug("Element: " + obj);
		// if (obj instanceof IAction) {
		// UIUtils.debug("Action: " + ((IAction) obj).getContents());
		// }
		TreeItem item = TreeSupports.findItem(viewer.getTree(),
				(IRodinElement) obj);
		viewer.reveal(item.getData());

		((EventBEditableTreeViewer) viewer).selectItem(item, column); // try
																		// to
																		// select
																		// the
																		// second
																		// column
																		// to
																		// edit
																		// name
	}
}
