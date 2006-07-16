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

package org.eventb.internal.ui.prover;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.ExtensionLoader;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticDropdownToolItem;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticDropdownUI;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticToolItem;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticUI;
import org.eventb.ui.prover.IGlobalTactic;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 */
public class ProofControlPage extends Page implements IProofControlPage,
		IProofStateChangedListener {

	boolean share;

	private Action switchLayout;

	private IEventBInputText textInput;

	private IEventBFormText formTextInformation;

	private ScrolledForm scrolledForm;

	private ToolBar buttonBar;

	private boolean isHorizontal;

	private ProverUI editor;

	private Collection<GlobalTacticToolItem> items;

	private Collection<GlobalTacticDropdownToolItem> dropdownItems;

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI editor associated with this Proof Control page.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
		editor.getUserSupport().addStateChangedListeners(this);
	}

	/**
	 * Apply a tactic with a progress monitor (providing cancel button).
	 * <p>
	 * 
	 * @param op
	 *            a runnable with progress monitor.
	 */
	private static void applyTacticWithProgress(IRunnableWithProgress op) {
		final Shell shell = Display.getDefault().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	/**
	 * Runs the predicate prover on the current proof tree node.
	 * 
	 * @param restricted
	 *            <code>true</code> is only selected hypotheses should be
	 *            passed to PP
	 */
	public static void runPP(final UserSupport userSupport,
			final boolean restricted) {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				userSupport
						.applyTactic(Tactics.externalPP(restricted, monitor));
			}
		};
		applyTacticWithProgress(op);
	}

	/**
	 * Runs the mono-lemma prover on the current proof tree node.
	 * 
	 * @param forces
	 *            list of forces to use
	 */
	public static void runML(final UserSupport userSupport, final int forces) {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				userSupport.applyTactic(Tactics.externalML(forces, monitor));
			}
		};
		applyTacticWithProgress(op);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the UserSupport
		editor.getUserSupport().removeStateChangedListeners(this);
		formTextInformation.dispose();
		textInput.dispose();
		super.dispose();
	}

	/**
	 * Helper function to create tool item
	 * 
	 * @param parent
	 *            the parent toolbar
	 * @param type
	 *            the type of tool item to create
	 * @param text
	 *            the text to display on the tool item
	 * @param image
	 *            the image to display on the tool item
	 * @param hotImage
	 *            the hot image to display on the tool item
	 * @param toolTipText
	 *            the tool tip text for the tool item
	 * @return ToolItem
	 */
	private ToolItem createToolItem(ToolBar parent, int type, String text,
			Image image, Image hotImage, String toolTipText) {
		ToolItem item = new ToolItem(parent, type);
		item.setText(text);
		item.setImage(image);
		item.setHotImage(hotImage);
		item.setToolTipText(toolTipText);
		return item;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		int defaultWidth = 40;

		isHorizontal = false;

		scrolledForm = toolkit.createScrolledForm(parent);
		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		body.setLayout(gl);

		// Composite of toolbars
		Composite comp = toolkit.createComposite(body);
		gl = new GridLayout();
		gl.numColumns = 1;
		comp.setLayout(gl);
		comp.setLayoutData(new GridData());

		buttonBar = new ToolBar(body, SWT.FLAT | SWT.WRAP);
		buttonBar.setLayoutData(new GridData());

		dropdownItems = new HashSet<GlobalTacticDropdownToolItem>();

		// Create the dropdown first
		GlobalTacticDropdownUI[] dropdowns = ExtensionLoader
				.getGlobalDropdowns();
		GlobalTacticDropdownToolItem dropdownItem = null;
		for (GlobalTacticDropdownUI dropdown : dropdowns) {

			ToolItem item = createToolItem(buttonBar, SWT.DROP_DOWN, "", null,
					null, "");
			dropdownItem = new GlobalTacticDropdownToolItem(item, dropdown
					.getID()) {

				@Override
				public void apply(IGlobalTactic tactic) {
					try {
						tactic.apply(editor.getUserSupport(), textInput
								.getTextWidget().getText());
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
			dropdownItems.add(dropdownItem);
		}

		items = new HashSet<GlobalTacticToolItem>();
		GlobalTacticUI[] tactics = ExtensionLoader.getGlobalTactics();

		for (final GlobalTacticUI tactic : tactics) {
			if (tactic.getDropdown() != null) {
				addDropdown(tactic.getDropdown(), tactic);
			} else {
				ToolItem item = createToolItem(buttonBar, SWT.PUSH, "",
						EventBUIPlugin.getDefault().getImageRegistry().get(
								tactic.getImage()), null, tactic.getTips());

				IGlobalTactic globalTactic = tactic.getTactic();

				final GlobalTacticToolItem globalTacticToolItem = new GlobalTacticToolItem(
						item, globalTactic);
				items.add(globalTacticToolItem);

				item.addSelectionListener(new SelectionAdapter() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							globalTacticToolItem.getTactic().apply(
									editor.getUserSupport(),
									textInput.getTextWidget().getText());
						} catch (RodinDBException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				item.setWidth(defaultWidth);
			}
		}

		// A text field
		textInput = new EventBMath(toolkit.createText(body, "", SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL));

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		gd.widthHint = 200;
		textInput.getTextWidget().setLayoutData(gd);
		textInput.getTextWidget().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateToolItems(editor.getUserSupport().getCurrentPO()
						.getCurrentNode());
			}
		});

		ProofState proofState = editor.getUserSupport().getCurrentPO();
		if (proofState != null) {
			updateToolItems(proofState.getCurrentNode());
		} else {
			updateToolItems(null);
		}

		formTextInformation = new EventBFormText(toolkit.createFormText(body,
				true));
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.minimumHeight = 20;
		gd.heightHint = 20;
		formTextInformation.getFormText().setLayoutData(gd);
		setFormTextInformation("");

		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);

		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void addDropdown(String dropdown, GlobalTacticUI tactic) {
		for (Iterator<GlobalTacticDropdownToolItem> it = dropdownItems
				.iterator(); it.hasNext();) {
			GlobalTacticDropdownToolItem curr = it.next();
			if (curr.getID().equals(dropdown)) {
				curr.addTactic(tactic);
				return;
			}
		}
	}

	/**
	 * Set the information (in the bottom of the page).
	 * <p>
	 * 
	 * @param information
	 *            the string (information from the UserSupport).
	 */
	private void setFormTextInformation(String information) {
		formTextInformation.getFormText().setText(information, false, false);
	}

	/**
	 * Setup the context menu.
	 */
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
	}

	/**
	 * Setup the action bars
	 */
	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(switchLayout);
		manager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(switchLayout);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Fill the local toolbar.
	 * <p>
	 * 
	 * @param manager
	 *            the toolbar manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(switchLayout);
	}

	/**
	 * Creat the actions used in this page.
	 */
	private void makeActions() {
		switchLayout = new Action() {
			public void run() {
				isHorizontal = isHorizontal ? false : true;
				if (isHorizontal) {
					GridLayout gl = new GridLayout();
					gl.numColumns = 2;
					scrolledForm.getBody().setLayout(gl);
					gl = new GridLayout();
					gl.numColumns = 7; // Total number of buttons?
					gl.makeColumnsEqualWidth = true;
					buttonBar.setLayout(gl);
				} else {
					GridLayout gl = new GridLayout();
					gl.numColumns = 1;
					scrolledForm.getBody().setLayout(gl);
					gl = new GridLayout();
					gl.numColumns = 9; // TODO Should be the number of buttons
					gl.makeColumnsEqualWidth = true;
					buttonBar.setLayout(gl);
				}
				scrolledForm.reflow(true);
			}
		};
		switchLayout.setText("Switch Layout");
		switchLayout
				.setToolTipText("Switch between horizontal and vertical layout of the buttons");
		switchLayout.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

	}

	/**
	 * Passing the focus request to the button bar.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
		buttonBar.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		if (scrolledForm == null)
			return null;
		return scrolledForm;
	}

	/**
	 * Update the status of the toolbar items.
	 */
	private void updateToolItems(IProofTreeNode node) {
		for (GlobalTacticDropdownToolItem item : dropdownItems) {
			item.updateStatus(node, textInput.getTextWidget().getText());
		}

		for (GlobalTacticToolItem item : items) {
			item.updateStatus(node, textInput.getTextWidget().getText());
		}

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void proofStateChanged(final IProofStateDelta delta) {

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				ProofState ps = delta.getNewProofState();
				IProofTreeNode node = null;
				if (ps != null) {
					node = ps.getCurrentNode();
				} else {
					node = delta.getNewProofTreeNode();
				}
				if (node != null) {
					final IProofTreeNode newNode = node;
					updateToolItems(newNode);
				}
				Object information = delta.getInformation();
				if (information != null)
					setFormTextInformation(information.toString());
				else
					setFormTextInformation("");
				scrolledForm.reflow(true);

			}
		});

	}

}
