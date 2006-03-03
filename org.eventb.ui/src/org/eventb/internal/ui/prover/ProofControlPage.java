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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.IGoalChangeEvent;
import org.eventb.core.pm.IGoalChangedListener;
import org.eventb.core.pm.IStatusChangedListener;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

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
				IGoalChangedListener,
				IStatusChangedListener
{

	boolean share;
	private Action switchLayout;
	private Action action2;
	private EventBMath textInput;
	private FormText formTextInformation;
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
	private Button se;
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
			
			try {
				if (label.equals("pn")) {
					editor.getUserSupport().applyTactic(Tactics.prune());
					return;
				}
			
				if (label.equals("ne")) {
					editor.getUserSupport().nextUndischargedPO();
					return;
				}
			
				if (label.equals("pv")) {
					editor.getUserSupport().prevUndischargedPO();
					return;
				}
			
				if (label.equals("dc")) {
					editor.getUserSupport().applyTactic(Tactics.doCase(textInput.getTextWidget().getText()));
					return;
				}
			
				if (label.equals("nm")) {				
					editor.getUserSupport().applyTactic(Tactics.norm());
					return;
				}
			
				if (label.equals("pp")) {
					final UserSupport userSupport = editor.getUserSupport();
					IRunnableWithProgress op = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException {
							try {
								userSupport.applyTactic(Tactics.legacyProvers(monitor));
//								apply(Tactics.legacyProvers(), monitor);
							} catch (RodinDBException e) {
								e.printStackTrace();
								throw new InvocationTargetException(e);
							}
						}
					};
					
					if (UIUtils.debug) System.out.println("Here");
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(ProofControlPage.this.scrolledForm.getShell());
					
					try {
						if (UIUtils.debug) System.out.println("Here 1");
						dialog.run(true, true, op);
						if (UIUtils.debug) System.out.println("Here 2");
					} catch (InterruptedException exception) {
						if (UIUtils.debug) System.out.println("Interrupt ");
						return;
					} catch (InvocationTargetException exception) {
						Throwable realException = exception.getTargetException();
						realException.printStackTrace();
						MessageDialog.openError(ProofControlPage.this.scrolledForm.getShell(), "Error here", realException.getMessage());
						return;
					}
					
					return;
				}
			
				if (label.equals("ah")) {
					editor.getUserSupport().applyTactic(Tactics.lemma(textInput.getTextWidget().getText()));
					return;
				}
			
				if (label.equals("ct")) {
					editor.getUserSupport().applyTactic(Tactics.contradictGoal());
					return;
				}

				if (label.equals("se")) {
					if (UIUtils.debug) System.out.println("Search for " + textInput.getTextWidget().getText());
					editor.getUserSupport().searchHyps(textInput.getTextWidget().getText());
					return;
				}
			}
			catch (RodinDBException exception) {
				exception.printStackTrace();
			}
		}
	}
	   	
	/**
	 * The constructor.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
		editor.getUserSupport().addGoalChangedListener(this);
		editor.getUserSupport().addStatusChangedListener(this);
	}
    
	@Override
	public void dispose() {
		// Deregister with the main plugin
		editor.getUserSupport().removeGoalChangedListener(this);
		editor.getUserSupport().removeStatusChangedListener(this);
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
		se = createButton(buttonContainer, "se");
		createButton(buttonContainer, "pv");
		createButton(buttonContainer, "ne");
		
		// A text field
		textInput = new EventBMath(toolkit.createText(body, "", SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		gd.widthHint = 200;
		textInput.getTextWidget().setLayoutData(gd);
		textInput.getTextWidget().addModifyListener(new ModifyListener() {
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

		formTextInformation = toolkit.createFormText(body, true);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.minimumHeight = 20;
		gd.heightHint = 20;
        formTextInformation.setLayoutData(gd);
        setFormTextInformation("");
        
		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
		
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void setFormTextInformation(String information) {
		formTextInformation.setText(information, false, false);
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
					gl.numColumns = 7;  // Total number of buttons?
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
		buttonContainer.setFocus();
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
		Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
		display.syncExec (new Runnable () {
			public void run () {
				updateButtons();
			}
		});
	}
	
	private void updateButtons() {
		if (isOpened) {
			pn.setEnabled(false);
			nm.setEnabled(true);
			pp.setEnabled(true);
			ct.setEnabled(true);
			if (textInput.getTextWidget().getText().equals("")) dc.setEnabled(false);
			else dc.setEnabled(true);
			if (textInput.getTextWidget().getText().equals("")) ah.setEnabled(false);
			else ah.setEnabled(true);
			se.setEnabled(true);
		}
		else {
			pn.setEnabled(true);
			nm.setEnabled(false);
			pp.setEnabled(false);
			dc.setEnabled(false);
			ct.setEnabled(false);
			ah.setEnabled(false);
			se.setEnabled(false);
		}
		return;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IStatusChangedListener#statusChanged(java.lang.Object)
	 */
	public void statusChanged(final Object information) {
		final ProofControlPage page = this;

		Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
		display.syncExec (new Runnable () {
			public void run () {
				if (information != null) setFormTextInformation(information.toString());
				else setFormTextInformation("");
				scrolledForm.reflow(true);
				page.setFocus();
			}
		});
	}

}