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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.IGoalChangeEvent;
import org.eventb.core.pm.IGoalChangedListener;
import org.eventb.core.pm.ProofState;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.eventBKeyboard.translators.EventBTextModifyListener;

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

public class ProofControlPage 
	extends Page 
	implements	IProofControlPage,
				IGoalChangedListener
{

	private Action switchLayout;
	private Action action2;
	private Text textInput;
	private ScrolledForm scrolledForm;
	private Composite buttonContainer;
	private boolean isHorizontal;
	private ProverUI editor;
	private Button pn;
	private Button dc;
	private Button nm;
	private Button pp;
	private Button ah;
	private Button ct;
	private boolean isOpened;
	
	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	private class ContextButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.getSource();
			String label = button.getText();
			
			if (label.equals("pn")) {
				if (editor != null) {
					TreeViewer viewer = editor.getProofTreeUI().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (!proofTree.isOpen()) {
							Tactics.prune().apply(proofTree);
							viewer.refresh(proofTree);
							viewer.setSelection(new StructuredSelection(proofTree));
						}
					}
				}
				return;
			}
			
			if (label.equals("ne")) {
				if (editor != null) {
					editor.getUserSupport().nextUndischargedPO();
//					if (ps != null) {
//						editor.getProofTreeUI().setInput(ps.getProofTree());
//						editor.getProofTreeUI().getViewer().expandAll();
//						IProofTreeNode pt = ps.getNextPendingSubgoal();
//						if (pt != null) 
//							editor.getProofTreeUI().getViewer().setSelection(new StructuredSelection(pt));
//
//					}
				}
				return;
			}
			
			if (label.equals("pv")) {
				if (editor != null) {
					editor.getUserSupport().prevUndischargedPO();
//					if (ps != null) {
//						editor.getProofTreeUI().setInput(ps.getProofTree());
//						editor.getProofTreeUI().getViewer().expandAll();
//						IProofTreeNode pt = ps.getNextPendingSubgoal();
//						if (pt != null) 
//							editor.getProofTreeUI().getViewer().setSelection(new StructuredSelection(pt));
//					}
				}
				return;
			}
			
			if (label.equals("dc")) {			
				if (editor != null) {
					TreeViewer viewer = editor.getProofTreeUI().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (proofTree.isOpen()) {
							ITactic t = Tactics.doCase(textInput.getText());
							System.out.println(t.apply(proofTree));
							viewer.refresh(proofTree);
							ProofState ps = editor.getUserSupport().getCurrentPO();
							IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
							if (pt != null) 
								editor.getProofTreeUI().getViewer().setSelection(new StructuredSelection(pt));
							else
								editor.getProofTreeUI().selectRoot();
						}
					}
				}
				return;
			}
			
			if (label.equals("nm")) {				
				if (editor != null) {
					TreeViewer viewer = editor.getProofTreeUI().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (proofTree.isOpen()) {
							Tactics.norm().apply(proofTree);
							editor.getProofTreeUI().refresh(proofTree);
							
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							ProofState ps = editor.getUserSupport().getCurrentPO();
							IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
							if (pt != null) 
								editor.getProofTreeUI().getViewer().setSelection(new StructuredSelection(pt));
							else
								editor.getProofTreeUI().selectRoot();
						}
					}
				}
				return;
			}
			
			if (label.equals("pp")) {
				if (editor != null) {
					TreeViewer viewer = editor.getProofTreeUI().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (proofTree.isOpen()) {
							Tactics.legacyProvers().apply(proofTree);
							editor.getProofTreeUI().refresh(proofTree);
							
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							ProofState ps = editor.getUserSupport().getCurrentPO();
							IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
							if (pt != null) 
								editor.getProofTreeUI().getViewer().setSelection(new StructuredSelection(pt));
							else
								editor.getProofTreeUI().selectRoot();
						}
					}
				}
				return;
			}
			
			if (label.equals("ah")) {
				if (editor != null) {
					TreeViewer viewer = editor.getProofTreeUI().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (proofTree.isOpen()) {
							ITactic t = Tactics.lemma(textInput.getText());
							System.out.println(t.apply(proofTree));
							viewer.refresh(proofTree);
							ProofState ps = editor.getUserSupport().getCurrentPO();
							IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
							if (pt != null) 
								editor.getProofTreeUI().getViewer().setSelection(new StructuredSelection(pt));
							else
								editor.getProofTreeUI().selectRoot();
						}
					}
				}
				return;
			}
			
			if (label.equals("ct")) {
				System.out.println("TODO: Implements contradiction");
				return;
			}
		}
	}
	   
	
	/**
	 * The constructor.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
		editor.getUserSupport().addGoalChangedListener(this);
	}
    
	@Override
	public void dispose() {
		// Deregister with the main plugin
		editor.getUserSupport().removeGoalChangedListener(this);
		super.dispose();
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createControl(Composite parent) {	
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		isHorizontal = true;
		
		scrolledForm = toolkit.createScrolledForm(parent);
		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		body.setLayout(gl);
		
		// Array of buttons
		buttonContainer = toolkit.createComposite(body);
		gl = new GridLayout();
		gl.numColumns = 6;
		gl.makeColumnsEqualWidth = true;
		buttonContainer.setLayout(gl);
		buttonContainer.setLayoutData(new GridData());
				
		pp = createButton(buttonContainer, "pp");
		nm = createButton(buttonContainer, "nm");
		ah = createButton(buttonContainer, "ah");
		dc = createButton(buttonContainer, "dc");
		ct = createButton(buttonContainer, "ct");
		pn = createButton(buttonContainer, "pn");
		createButton(buttonContainer, "pv");
		createButton(buttonContainer, "ne");
		
		// A text field
		textInput = toolkit.createText(body, "", SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		gd.widthHint = 200;
		textInput.setLayoutData(gd);
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		textInput.setFont(font);
		textInput.addModifyListener(new EventBTextModifyListener());
		textInput.addModifyListener(new ModifyListener() {
			 public void modifyText(ModifyEvent e) {
				 updateButtons();
			 }
		});
		
		ProofState proofState = editor.getUserSupport().getCurrentPO();
		if (proofState != null) {
			IProofTreeNode node = proofState.getCurrentNode();
			isOpened = (node != null && node.isOpen()) ? true : false;
		}
		else isOpened = false;
		updateButtons();

		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
		
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	
	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.addSelectionListener(new ContextButtonListener());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText(label);
		return button;
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProofControlPage.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(this.getControl());
		this.getControl().setMenu(menu);
//		this.getSite().registerContextMenu(menuMgr, this);
//		viewer.getControl().setMenu(menu);
		//getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(switchLayout);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(switchLayout);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(switchLayout);
		manager.add(action2);
	}

	private void makeActions() {
		switchLayout = new Action() {
			public void run() {
				isHorizontal = isHorizontal ? false : true;
				if (isHorizontal) {
					GridLayout gl = new GridLayout();
					gl.numColumns = 2;
					scrolledForm.getBody().setLayout(gl);
					gl = new GridLayout();
					gl.numColumns = 6;
					gl.makeColumnsEqualWidth = true;
					buttonContainer.setLayout(gl);
				}
				else {
					GridLayout gl = new GridLayout();
					gl.numColumns = 1;
					scrolledForm.getBody().setLayout(gl);
					gl = new GridLayout();
					gl.numColumns = 8;  // TODO Should be the number of buttons
					gl.makeColumnsEqualWidth = true;
					buttonContainer.setLayout(gl);					
				}
				scrolledForm.reflow(true);
			}
		};
		switchLayout.setText("Switch Layout");
		switchLayout.setToolTipText("Switch between horizontal and vertical layout of the buttons");
		switchLayout.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
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
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			this.getControl().getShell(),
			"Proof Control",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		textInput.setFocus();
	}
	
    /* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
        if (scrolledForm == null)
            return null;
        return scrolledForm;
    }

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IGoalChangedListener#goalChanged(org.eventb.core.pm.IGoalChangeEvent)
	 */
	public void goalChanged(IGoalChangeEvent e) {
		IProofTreeNode node = e.getDelta().getProofTreeNode();
		if (node != null && node.isOpen()) isOpened = true;
		else isOpened = false;
		updateButtons();
	}
	
	private void updateButtons() {
		if (isOpened) {
			pn.setEnabled(false);
			nm.setEnabled(true);
			pp.setEnabled(true);
			ct.setEnabled(true);
			if (textInput.getText().equals("")) dc.setEnabled(false);
			else dc.setEnabled(true);
			if (textInput.getText().equals("")) ah.setEnabled(false);
			else ah.setEnabled(true);
		}
		else {
			pn.setEnabled(true);
			nm.setEnabled(false);
			pp.setEnabled(false);
			dc.setEnabled(false);
			ct.setEnabled(false);
			ah.setEnabled(false);
		}
		return;
	}

}