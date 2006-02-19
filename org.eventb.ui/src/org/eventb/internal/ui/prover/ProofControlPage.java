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

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.ProofState;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.Tactic;
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
				ISelectionChangedListener
{
	//public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID + ".views.ProofControl";
	private ListenerList selectionChangedListeners = new ListenerList();
	
//	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	private Text textInput;
	private ScrolledForm scrolledForm;
	private ProverUI editor;
	private Button pn;
	private Button dc;
	private Button nm;	
	
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
			
			if (label.equals("⊤")) {
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();

					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (!proofTree.isDischarged()) {
							Tactics.trivial.apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							// Expand the node
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							
							// Select the first pending "subgoal"
							IProofTreeNode subGoal = proofTree.getFirstOpenDescendant();
							if (subGoal != null) {
								viewer.setSelection(new StructuredSelection(subGoal));
							}
						}
					}
				}
				return;
			}

			if (label.equals("pn")) {
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (!proofTree.isOpen()) {
							Tactics.prune.apply(proofTree);
							viewer.refresh(proofTree);
							viewer.setSelection(new StructuredSelection(proofTree));
						}
					}
				}
				return;
			}
			
			if (label.equals("ne")) {
				if (editor != null) {
					ProofState ps = editor.getUserSupport().nextPO();
					if (ps != null) {
						editor.getContentOutline().setInput(ps);
						editor.getContentOutline().getViewer().expandAll();
						IProofTreeNode pt = ps.getNextPendingSubgoal();
						if (pt != null) 
							editor.getContentOutline().getViewer().setSelection(new StructuredSelection(pt));

					}
				}
				return;
			}
			
			if (label.equals("pv")) {
				if (editor != null) {
					ProofState ps = editor.getUserSupport().prevPO();
					if (ps != null) {
						editor.getContentOutline().setInput(ps);
						editor.getContentOutline().getViewer().expandAll();
						IProofTreeNode pt = ps.getNextPendingSubgoal();
						if (pt != null) 
							editor.getContentOutline().getViewer().setSelection(new StructuredSelection(pt));
					}
				}
				return;
			}
			
			if (label.equals("dc")) {
				System.out.println("Do CASE " + textInput.getText());
				
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (proofTree.isOpen()) {
							Tactic t = Tactics.doCase(textInput.getText());
							System.out.println(t.apply(proofTree));
							viewer.refresh(proofTree);
							ProofState ps = editor.getUserSupport().getCurrentPO();
							IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
							if (pt != null) 
								editor.getContentOutline().getViewer().setSelection(new StructuredSelection(pt));
						}
					}
				}
				return;
			}
			
			if (label.equals("nm")) {
				System.out.println("Do Normalisation " + textInput.getText());
				
				if (editor != null) {
					TreeViewer viewer = editor.getContentOutline().getViewer();
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode proofTree = (IProofTreeNode) obj;
						if (proofTree.isOpen()) {
							Tactics.norm().apply(proofTree);
							editor.getContentOutline().refresh(proofTree);
							
							viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
							//viewer.setExpandedState(proofTree, true);
							ProofState ps = editor.getUserSupport().getCurrentPO();
							IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
							if (pt != null) 
								editor.getContentOutline().getViewer().setSelection(new StructuredSelection(pt));
						}
					}
				}
				return;
			}
		}
	}
	   
	
	/**
	 * The constructor.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
	}

    /*
     *  (non-Javadoc)
     * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
     */
    public void init(IPageSite pageSite) {
        super.init(pageSite);
        pageSite.setSelectionProvider(this);
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
	public void createControl(Composite parent) {	
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		scrolledForm = toolkit.createScrolledForm(parent);
		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		gl.numColumns = 3;
		body.setLayout(gl);
		
		// Array of buttons
		Composite buttonContainer = toolkit.createComposite(body);
		gl = new GridLayout();
		gl.numColumns = 6;
		gl.makeColumnsEqualWidth = true;
		buttonContainer.setLayout(gl);
		buttonContainer.setLayoutData(new GridData());
				
		createButton(buttonContainer, "pv");
		createButton(buttonContainer, "ne");
		pn = createButton(buttonContainer, "pn");
		dc = createButton(buttonContainer, "dc");
		nm = createButton(buttonContainer, "nm");
		
		// A text field
		textInput = toolkit.createText(body, "", SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		gd.widthHint = 200;
		textInput.setLayoutData(gd);
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		textInput.setFont(font);
		textInput.addModifyListener(new EventBTextModifyListener());
		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
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
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
		//getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		//IActionBars bars = getViewSite().getActionBars();
		//fillLocalPullDown(bars.getMenuManager());
		//fillLocalToolBar(bars.getToolBarManager());
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
//				ISelection selection = viewer.getSelection();
//				Object obj = ((IStructuredSelection)selection).getFirstElement();
//				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
	}
	private void showMessage(String message) {
//		MessageDialog.openInformation(
//			viewer.getControl().getShell(),
//			"Proof Control",
//			message);
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
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
		selectionChangedListeners.add(listener);
	}

	/**
     * Fires a selection changed event.
     *
     * @param selection the new selection
     */
    protected void fireSelectionChanged(ISelection selection) {
        // create an event
        final SelectionChangedEvent event = new SelectionChangedEvent(this,
                selection);

        // fire the event
        Object[] listeners = selectionChangedListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
            Platform.run(new SafeRunnable() {
                public void run() {
                    l.selectionChanged(event);
                }
            });
        }
    }


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
//        if (viewer == null)
//            return StructuredSelection.EMPTY;
//        return viewer.getSelection();
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
//        if (viewer != null)
//            viewer.setSelection(selection);
	}

    /* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
        if (scrolledForm == null)
            return null;
        return scrolledForm;
//		return null;
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}


}