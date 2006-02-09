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

package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.tactics.Tactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.eventBKeyboard.translators.EventBTextModifyListener;
import org.eventb.internal.ui.EventBUIPlugin;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ProofControl 
	extends ViewPart 
{
	public static final String VIEW_ID = "org.eventb.internal.ui.prover.ProofControl";
	
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

//	private Button impI;
//	private Button conjI;
//	private Button hp;
//	private Button allI;
//	private Button trivial;
//	private Button pn;
	private Button ne;
	private Button pv;
	
	
	private Button [] buttons;
	private Text goal;
	
	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	private class IContextButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.getSource();
			String label = button.getText();
			if (label.equals("⇒")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();

					if (obj instanceof ProofTree) {
						ProofTree proofTree = (ProofTree) obj;
						if (!proofTree.isClosed()) {
							Tactics.impI.apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							// Expand the node
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							
							// Select the first pending "subgoal"
							List<ProofTree> subGoals = proofTree.pendingSubgoals();
							if (subGoals.size() != 0) {
								viewer.setSelection(new StructuredSelection(subGoals.get(0)));
							}
						}
					}
				}
				return;
			}
			
			if (label.equals("∧")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
				
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					if (obj instanceof ProofTree) {
						ProofTree proofTree = (ProofTree) obj;
						if (!proofTree.isClosed()) {
							Tactics.conjI.apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							// Expand the node
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							
							// Select the first pending "subgoal"
							List<ProofTree> subGoals = proofTree.pendingSubgoals();
							if (subGoals.size() != 0) {
								viewer.setSelection(new StructuredSelection(subGoals.get(0)));
							}
						}
					}
				}
				return;
			}
			
			if (label.equals("hp")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
				
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();

					if (obj instanceof ProofTree) {
						ProofTree proofTree = (ProofTree) obj;
						if (!proofTree.isClosed()) {
							Tactics.hyp.apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							// Expand the node
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);

							// Select the next pending "subgoal"
							editor.getContentOutline().selectNextPendingSubgoal(proofTree);
						}
					}
				}
				return;
			}
			
			if (label.equals("∀")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
				
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();

					if (obj instanceof ProofTree) {
						ProofTree proofTree = (ProofTree) obj;
						if (!proofTree.isClosed()) {
							Tactics.allI.apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							// Expand the node
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							
							// Select the first pending "subgoal"
							List<ProofTree> subGoals = proofTree.pendingSubgoals();
							if (subGoals.size() != 0) {
								viewer.setSelection(new StructuredSelection(subGoals.get(0)));
							}
						}
					}
				}
				return;
			}

			if (label.equals("⊤")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();

					if (obj instanceof ProofTree) {
						ProofTree proofTree = (ProofTree) obj;
						if (!proofTree.isClosed()) {
							Tactics.trivial.apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							// Expand the node
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							
							// Select the first pending "subgoal"
							List<ProofTree> subGoals = proofTree.pendingSubgoals();
							if (subGoals.size() != 0) {
								viewer.setSelection(new StructuredSelection(subGoals.get(0)));
							}
						}
					}
				}
				return;
			}

			if (label.equals("pn")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof ProofTree) {
						ProofTree proofTree = (ProofTree) obj;
						if (!proofTree.rootIsOpen()) {
							Tactics.prune.apply(proofTree);
							viewer.refresh(proofTree);
							viewer.setSelection(new StructuredSelection(proofTree));
						}
					}
				}
				return;
			}
			
			if (label.equals("ne")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					editor.nextPO();
				}
				return;
			}
			
			if (label.equals("pv")) {
				ProverUI editor = getActiveProverUI();
				if (editor != null) {
					editor.prevPO();
				}
				return;
			}
		}
	}
	   
	
	
	private ProverUI getActiveProverUI() {
		IEditorPart editor = EventBUIPlugin.getActivePage().getActiveEditor();
		if (editor instanceof ProverUI) return (ProverUI) editor;
		else return null;
	}
	
	
	/**
	 * The constructor.
	 */
	public ProofControl() {
		// Register with the main plugin
//		buttons = new ArrayList<Button>();
		//ProverUIPlugin.getDefault().setProofControlView(this);
	}

	
	@Override
	public void dispose() {
		// Deregister with the main plugin
		//ProverUIPlugin.getDefault().setProofControlView(null);
		super.dispose();
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		parent.setLayout(gl);
		
		
		// Array of buttons
		Composite buttonContainer = new Composite(parent, SWT.NONE);
		gl = new GridLayout();
		gl.numColumns = 6;
		gl.makeColumnsEqualWidth = true;
		buttonContainer.setLayout(gl);
		buttonContainer.setLayoutData(new GridData());
				
		String [] labels = {"it", "pt", "re", "st",
					         "n", "ba", "as", "qu", "hp", "dc",
					         "cm", "ah", "ed", "od", "ne", "pv",
					         "bg", "np", "pp", "rs"};
		
//		impI = createButton(buttonContainer, "⇒");
//		conjI = createButton(buttonContainer, "∧");
//		hp = createButton(buttonContainer, "hp");
//		allI = createButton(buttonContainer, "∀");
//		trivial = createButton(buttonContainer, "⊤");
//		pn = createButton(buttonContainer, "pn");
		ne = createButton(buttonContainer, "ne");
		pv = createButton(buttonContainer, "pv");
		for (int i = 0; i < labels.length; i++) {
			createButton(buttonContainer, labels[i]);
		}
		
//		Composite container = new Composite(parent, SWT.NONE);
//		GridData gD = new GridData(GridData.FILL_HORIZONTAL);
//		gD.widthHint = 250;
//		container.setLayoutData(gD);
//		gl = new GridLayout();
//		gl.numColumns = 1;
//		container.setLayout(gl);
//
//		Composite goalContainer = new Composite(container, SWT.NONE);
//		gD = new GridData(GridData.FILL_BOTH);
//		gD.widthHint = 250;
//		goalContainer.setLayoutData(gD);
//		gl = new GridLayout();
//		gl.numColumns = 2;
//		goalContainer.setLayout(gl);
//		
//		Composite goalComposite = new Composite(goalContainer, SWT.NONE);
//		gD = new GridData();
//		gD.widthHint = 250;
//		goalComposite.setLayoutData(gD);
//		gl = new GridLayout();
//		gl.numColumns = 1;
//		goalComposite.setLayout(gl);
//
//		Label label = new Label(goalComposite, SWT.NONE);
//		label.setText("Goal: ");
//		gD = new GridData(GridData.FILL_HORIZONTAL);
//		gD.horizontalAlignment = SWT.RIGHT;
//		label.setLayoutData(gD);
//
//		Composite goalButtons = new Composite(goalComposite, SWT.NONE);
//		gD = new GridData();
//		gD.horizontalAlignment = SWT.RIGHT;
//		gl = new GridLayout();
//		gl.numColumns = 5;
//		gl.makeColumnsEqualWidth = true;
//		goalButtons.setLayout(gl);
//		
//		buttons = new Button[5];
//		for (int i = 0; i < 5; i++) {
//			buttons[i] = createButton(goalButtons, "mm");
//			buttons[i].setVisible(false);
//		}
		
//		goal = new Text(goalContainer, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		goal.setEditable(false);
//		gD = new GridData(GridData.FILL_BOTH);
////		gD.horizontalAlignment = SWT.LEFT;
//		gD.widthHint = 200;
//		gD.heightHint = 50;
//		goal.setLayoutData(gD);
//		
		//		 A text field
		Text textInput = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 50;
		gd.widthHint = 250;
		textInput.setLayoutData(gd);
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		textInput.setFont(font);
		textInput.addModifyListener(new EventBTextModifyListener());
			
//		JFaceResources.getFontRegistry().addListener(this);

//		Text goalInput = new Text(goalContainer, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		gD = new GridData(GridData.FILL_VERTICAL);
//		gD.widthHint = 250;
//		gD.heightHint = 125;
//		goalInput.setLayoutData(gD);
//		goalInput.setText("∀x·(\n  x ∈ S\n ⇒\n  x ∈ T\n)");
//		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//		viewer.setContentProvider(new ViewContentProvider());
//		viewer.setLabelProvider(new ViewLabelProvider());
//		viewer.setSorter(new NameSorter());
//		viewer.setInput(getViewSite());
//		makeActions();
//		hookContextMenu();
//		hookDoubleClickAction();
//		contributeToActionBars();
	}

	
	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.addSelectionListener(new IContextButtonListener());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText(label);
		return button;
	}
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProofControl.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Proof Control",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
//		viewer.getControl().setFocus();
	}
	
	public void setInput(ISelection sel) {
		// TODO Update the buttons
//		if (sel instanceof IStructuredSelection) {
//			IStructuredSelection ssel = (IStructuredSelection) sel;
//			if (ssel.size() == 1) {
//				ProofTree pt = (ProofTree) ssel.getFirstElement();
//				
//				if (!pt.rootIsOpen()) {
//					pn.setEnabled(true);
//					impI.setEnabled(false);
//					conjI.setEnabled(false);
//					hp.setEnabled(false);
//					allI.setEnabled(false);
//					disableButtons();
//					goal.setText(":-)");
//				}
//				else {
//					
//					pn.setEnabled(false);
//					pn.setVisible(false);
//					impI.setEnabled(true);
//					conjI.setEnabled(true);
//					hp.setEnabled(true);
//					allI.setEnabled(true);
//					goal.setText(pt.getRootSeq().goal().toString());
//					enableButtons(pt.getRootSeq());
//				}
//			}
//			else {
//				pn.setEnabled(true);
//				goal.setText("None/Multiple selection");
//			}
//		}
//		else {
			// TODO Error message???
//		}
	}
	
	private void disableButtons() {
		for (int i = 0;i < 5; i++) {
			buttons[i].setVisible(false);
		}
		return;
	}
	
	private void enableButtons(IProverSequent sequent) {
//		System.out.println("Enable Buttons");
		
		List<Tactic> tactics = getApplicable(sequent);
		int i = 0;
		for (Iterator<Tactic> it = tactics.iterator(); it.hasNext();) {
			
			Tactic tactic = it.next();
//			System.out.println("Applicable: " + tactic.toString());
			if (tactic.equals(Tactics.conjI)) {
				buttons[i].setText("∧");
				buttons[i].setVisible(true);
				i++;
			}
			else if (tactic.equals(Tactics.impI)) {
				buttons[i].setText("⇒");
				buttons[i].setVisible(true);
				i++;
			}
			else if (tactic.equals(Tactics.hyp)) {
				buttons[i].setText("hp");
				buttons[i].setVisible(true);
				i++;
			}
		}
		
		for (;i < 5; i++) {
			buttons[i].setVisible(false);
		}
		
		return;
	}

	private List<Tactic> getApplicable(IProverSequent sequent) {
		List<Tactic> result = new ArrayList<Tactic>();
//		conjI conjITactic = new conjI();
//		if (conjITactic.isApplicable(sequent)) result.add(Tactics.conjI);
//		impI impITactic = new impI();
//		if (impITactic.isApplicable(sequent)) result.add(Tactics.impI);
//		Hyp hypTactic = new Hyp();
//		if (hypTactic.isApplicable(sequent)) result.add(Tactics.hyp);
		return result;
	}

}