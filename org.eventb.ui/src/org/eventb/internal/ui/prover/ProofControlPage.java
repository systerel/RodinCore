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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBControl;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.ExtensionLoader;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticDropdownToolItem;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticDropdownUI;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticToolItem;
import org.eventb.internal.ui.prover.globaltactics.GlobalTacticToolbar;
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

	// private Action switchLayout;

	private IEventBInputText textInput;

	private ProverUI editor;

	private IEventBFormText formTextInformation;

	private ScrolledForm scrolledForm;

	private HashMap<String, CoolItem> coolItems;

	private HashMap<String, GlobalTacticDropdownToolItem> dropdownItems;

	private Collection<GlobalTacticToolItem> toolItems;

	private Combo historyCombo;

	private EventBControl history;

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
		history.dispose();
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
	// private ToolItem createToolItem(ToolBar parent, int type, String text,
	// Image image, Image hotImage, String toolTipText) {
	// ToolItem item = new ToolItem(parent, type);
	// item.setText(text);
	// item.setImage(image);
	// item.setHotImage(hotImage);
	// item.setToolTipText(toolTipText);
	// return item;
	// }
	CoolItem createCoolItem(CoolBar coolBar) {
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		return item;

	}

	// This variable is for fixing the spacing problem with dropdown menu.
	private int dropdownCount;

	private static final int dropdownSize = 20;

	private ToolItem createToolItem(CoolItem coolItem, String text,
			Image image, int style) {
		ToolBar toolBar = (ToolBar) coolItem.getControl();
		ToolItem item = new ToolItem(toolBar, style);
		if (image != null)
			item.setImage(image);
		if (text != null)
			item.setText(text);

		toolBar.pack();
		Point size = toolBar.getSize();
		Point preferred = coolItem.computeSize(size.x + dropdownCount
				* dropdownSize, size.y);
		coolItem.setPreferredSize(preferred);
		return item;
	}

	// CoolItem createText(CoolBar coolBar) {
	// Text filterText = new Text(coolBar, SWT.SINGLE | SWT.BORDER);
	// filterText.pack();
	// Point size = filterText.getSize();
	// CoolItem item = new CoolItem(coolBar, SWT.NONE);
	// item.setControl(filterText);
	// Point preferred = item.computeSize(size.x, size.y);
	// item.setPreferredSize(preferred);
	// return item;
	// }

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(final Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		FormLayout layout = new FormLayout();
		parent.setLayout(layout);

		final CoolBar coolBar = new CoolBar(parent, SWT.FLAT);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);
		coolBar.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				coolBar.layout();
			}
		});

		// Create toolbars
		coolItems = new HashMap<String, CoolItem>();
		dropdownItems = new HashMap<String, GlobalTacticDropdownToolItem>();
		toolItems = new ArrayList<GlobalTacticToolItem>();

		ArrayList<GlobalTacticToolbar> toolbars = ExtensionLoader
				.getGlobalToolbar();
		ArrayList<GlobalTacticUI> tactics = ExtensionLoader.getGlobalTactics();
		ArrayList<GlobalTacticDropdownUI> dropdowns = ExtensionLoader
				.getGlobalDropdowns();

		for (GlobalTacticToolbar toolbar : toolbars) {

			CoolItem coolItem = createCoolItem(coolBar);
			coolItems.put(toolbar.getID(), coolItem);

			// Create dropdown
			int i = 0;
			dropdownCount = 0;
			while (i < dropdowns.size()) {
				GlobalTacticDropdownUI dropdown = dropdowns.get(i);
				String toolBarID = dropdown.getToolbar();
				if (toolBarID.equals(toolbar.getID())) {
					dropdownCount++;
					ToolItem item = createToolItem(coolItem, null, null,
							SWT.DROP_DOWN);
					GlobalTacticDropdownToolItem dropdownItem = new GlobalTacticDropdownToolItem(
							item, dropdown.getID()) {
						@Override
						public void apply(IGlobalTactic tactic) {
							try {
								Text textWidget = textInput.getTextWidget();
								String text = textWidget.getText();
								tactic.apply(editor.getUserSupport(), text);
								if (!text.equals("")) {
									historyCombo.add(text, 0);
									textWidget.setText("");
								}
							} catch (RodinDBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					};
					dropdownItems.put(dropdown.getID(), dropdownItem);
					dropdowns.remove(dropdown);
				} else {
					i++;
				}
			}

			i = 0;
			while (i < tactics.size()) {
				GlobalTacticUI tactic = tactics.get(i);
				if (tactic.getDropdown() == null) {
					if (tactic.getToolbar() != null
							&& tactic.getToolbar().equals(toolbar.getID())) {

						// CoolItem coolItem =
						// coolItems.get(tactic.getToolbar());
						// if (coolItem == null) coolItem = extraCoolItem;

						ToolItem item = createToolItem(coolItem, null,
								EventBUIPlugin.getDefault().getImageRegistry()
										.get(tactic.getImage()), SWT.PUSH);
						IGlobalTactic globalTactic = tactic.getTactic();

						final GlobalTacticToolItem globalTacticToolItem = new GlobalTacticToolItem(
								item, globalTactic);
						// items.add(globalTacticToolItem);

						item.addSelectionListener(new SelectionAdapter() {

							/*
							 * (non-Javadoc)
							 * 
							 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
							 */
							@Override
							public void widgetSelected(SelectionEvent e) {
								try {
									Text textWidget = textInput.getTextWidget();
									String text = textWidget.getText();
									globalTacticToolItem.getTactic().apply(
											editor.getUserSupport(), text);
									if (!text.equals("")) {
										historyCombo.add(text, 0);
										textWidget.setText("");
									}
									textInput.getTextWidget().setText("");
								} catch (RodinDBException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});
						// item.setWidth(defaultWidth);
						tactics.remove(tactic);
						toolItems.add(globalTacticToolItem);
					} else {
						i++;
					}
				} else {
					i++;
				}
			}

		}
		CoolItem extraCoolItem = createCoolItem(coolBar);

		// Create dropdowns for extraCoolItem
		for (GlobalTacticDropdownUI dropdown : dropdowns) {

			CoolItem coolItem = extraCoolItem;

			ToolItem item = createToolItem(coolItem, null, null, SWT.DROP_DOWN);
			GlobalTacticDropdownToolItem dropdownItem = new GlobalTacticDropdownToolItem(
					item, dropdown.getID()) {
				@Override
				public void apply(IGlobalTactic tactic) {
					try {
						Text textWidget = textInput.getTextWidget();
						String text = textWidget.getText();
						tactic.apply(editor.getUserSupport(), text);
						if (!text.equals("")) {
							historyCombo.add(text, 0);
							textWidget.setText("");
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
			dropdownItems.put(dropdown.getID(), dropdownItem);
		}

		// Add tactics
		for (GlobalTacticUI tactic : tactics) {
			if (tactic.getDropdown() != null) {

				GlobalTacticDropdownToolItem curr = dropdownItems.get(tactic
						.getDropdown());
				if (curr != null) {
					curr.addTactic(tactic);
				}

				else {
					ToolItem item = createToolItem(extraCoolItem, null,
							EventBUIPlugin.getDefault().getImageRegistry().get(
									tactic.getImage()), SWT.PUSH);
					IGlobalTactic globalTactic = tactic.getTactic();

					final GlobalTacticToolItem globalTacticToolItem = new GlobalTacticToolItem(
							item, globalTactic);
					// items.add(globalTacticToolItem);

					item.addSelectionListener(new SelectionAdapter() {

						/*
						 * (non-Javadoc)
						 * 
						 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
						 */
						@Override
						public void widgetSelected(SelectionEvent e) {
							try {
								Text textWidget = textInput.getTextWidget();
								String text = textWidget.getText();
								globalTacticToolItem.getTactic().apply(
										editor.getUserSupport(), text);
								if (!text.equals("")) {
									historyCombo.add(text, 0);
									textWidget.setText("");
								}
								textInput.getTextWidget().setText("");
							} catch (RodinDBException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});
					toolItems.add(globalTacticToolItem);

				}
				// addDropdown(tactic.getDropdown(), tactic);

				// GlobalTacticDropdownToolItem dropdownTool = dropdownItems
				// .get(tactic.getDropdown());

				// dropdownTool.addTactic(tactic);

			} else {
				ToolItem item = createToolItem(extraCoolItem, null,
						EventBUIPlugin.getDefault().getImageRegistry().get(
								tactic.getImage()), SWT.PUSH);
				IGlobalTactic globalTactic = tactic.getTactic();

				final GlobalTacticToolItem globalTacticToolItem = new GlobalTacticToolItem(
						item, globalTactic);
				// items.add(globalTacticToolItem);

				item.addSelectionListener(new SelectionAdapter() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							Text textWidget = textInput.getTextWidget();
							String text = textWidget.getText();
							globalTacticToolItem.getTactic().apply(
									editor.getUserSupport(), text);
							if (!text.equals("")) {
								historyCombo.add(text, 0);
								textWidget.setText("");
							}
							textInput.getTextWidget().setText("");
						} catch (RodinDBException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				toolItems.add(globalTacticToolItem);

			}
		}

		scrolledForm = toolkit.createScrolledForm(parent);
		FormData scrolledData = new FormData();
		scrolledData.left = new FormAttachment(0);
		scrolledData.right = new FormAttachment(100);
		scrolledData.top = new FormAttachment(coolBar);
		scrolledData.bottom = new FormAttachment(100);
		scrolledForm.setLayoutData(scrolledData);

		Composite body = scrolledForm.getBody();
		// body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		body.setLayout(gl);
		// A text field
		textInput = new EventBMath(toolkit.createText(body, "", SWT.MULTI));

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

		historyCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
		historyCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				textInput.getTextWidget().setText(historyCombo.getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		historyCombo.setLayoutData(gd);
		history = new EventBControl(historyCombo);

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

	/**
	 * Set the information (in the bottom of the page).
	 * <p>
	 * 
	 * @param information
	 *            the string (information from the UserSupport).
	 */
	private void setFormTextInformation(String information) {
		if (formTextInformation.getFormText().isDisposed()) return;
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
		// manager.add(switchLayout);
		// manager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		// manager.add(switchLayout);
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
		// manager.add(switchLayout);
	}

	/**
	 * Creat the actions used in this page.
	 */
	private void makeActions() {
		// switchLayout = new Action() {
		// public void run() {
		// isHorizontal = isHorizontal ? false : true;
		// if (isHorizontal) {
		// GridLayout gl = new GridLayout();
		// gl.numColumns = 2;
		// scrolledForm.getBody().setLayout(gl);
		// gl = new GridLayout();
		// gl.numColumns = 7; // Total number of buttons?
		// gl.makeColumnsEqualWidth = true;
		// buttonBar.setLayout(gl);
		// } else {
		// GridLayout gl = new GridLayout();
		// gl.numColumns = 1;
		// scrolledForm.getBody().setLayout(gl);
		// gl = new GridLayout();
		// gl.numColumns = 9; // TODO Should be the number of buttons
		// gl.makeColumnsEqualWidth = true;
		// buttonBar.setLayout(gl);
		// }
		// scrolledForm.reflow(true);
		// }
		// };
		// switchLayout.setText("Switch Layout");
		// switchLayout
		// .setToolTipText("Switch between horizontal and vertical layout of the
		// buttons");
		// switchLayout.setImageDescriptor(PlatformUI.getWorkbench()
		// .getSharedImages().getImageDescriptor(
		// ISharedImages.IMG_OBJS_INFO_TSK));

	}

	/**
	 * Passing the focus request to the button bar.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
		textInput.setFocus();
		// buttonBar.setFocus();
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
		for (GlobalTacticDropdownToolItem item : dropdownItems.values()) {
			item.updateStatus(node, textInput.getTextWidget().getText());
		}

		for (GlobalTacticToolItem item : toolItems) {
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
				List<Object> information = delta.getInformation();

				ProverUIUtils.debugProverUI("********** MESSAGE *********");
				for (Object info : information) {
					ProverUIUtils.debugProverUI(info.toString());
				}
				ProverUIUtils.debugProverUI("****************************");

				int size = information.size();
				if (size != 0)
					setFormTextInformation(information.get(size - 1).toString());
				else
					setFormTextInformation("");

				ProofState ps = delta.getProofState();
				IProofTreeNode node = null;
				if (delta.isNewProofState()) {
					if (ps != null) node = ps.getCurrentNode();
					else updateToolItems(null);
				} else if (delta.isDeleted()) {
					// Do nothing.
				} else {
					node = delta.getNewProofTreeNode();
				}
				
				if (node != null) {
					final IProofTreeNode newNode = node;
					updateToolItems(newNode);
				}
				scrolledForm.reflow(true);

			}
		});

	}

}
