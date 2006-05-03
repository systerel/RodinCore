/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
import org.eventb.core.pm.IGoalChangeEvent;
import org.eventb.core.pm.IGoalChangedListener;
import org.eventb.core.pm.IStatusChangedListener;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.externalReasoners.ExternalML;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.IEventBFormText;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 */
public class ProofControlPage extends Page implements IProofControlPage,
		IGoalChangedListener, IStatusChangedListener {

	boolean share;

	private Action switchLayout;

	private EventBMath textInput;

	private IEventBFormText formTextInformation;

	private ScrolledForm scrolledForm;

	private ToolBar buttonBar;

	private boolean isHorizontal;

	private ProverUI editor;

	private ToolItem ba;

	private ToolItem pn;

	private ToolItem dc;

	private ToolItem nm;

	private ToolItem externalProvers;

	private ToolItem ah;

	private ToolItem ct;

	private ToolItem sh;

	private ToolItem pv;

	private ToolItem ne;

	private boolean isOpened;

	private boolean isTop;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends the SelectionAdapter and response to
	 *         selections of buttons.
	 */
	private class ContextButtonListener extends SelectionAdapter {
		String label;

		/**
		 * Constructor.
		 * <p>
		 * 
		 * @param label
		 *            The label of the button.
		 */
		ContextButtonListener(String label) {
			this.label = label;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			buttonSelectedResponse(label);
		}
	}

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI editor associated with this Proof Control page.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
		editor.getUserSupport().addGoalChangedListener(this);
		editor.getUserSupport().addStatusChangedListener(this);
	}

	/**
	 * Method responds to the button selections depends on the label of the
	 * button.
	 * <p>
	 * 
	 * @param label
	 *            the label of the button.
	 */
	private void buttonSelectedResponse(String label) {
		try {
			if (label.equals("ba")) {
				editor.getUserSupport().back();
				return;
			}

			if (label.equals("pn")) {
				editor.getUserSupport().prune();
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
				editor.getUserSupport().applyTactic(
						Tactics.doCase(textInput.getTextWidget().getText()));
				return;
			}

			if (label.equals("nm")) {
				editor.getUserSupport().applyTactic(Tactics.norm());
				return;
			}

			if (label.equals("p0")) {
				runPP(true);
				return;
			}

			if (label.equals("pp")) {
				runPP(false);
				return;
			}

			if (label.equals("m0")) {
				runML(ExternalML.Input.FORCE_0);
				return;
			}

			if (label.equals("m1")) {
				runML(ExternalML.Input.FORCE_1);
				return;
			}

			if (label.equals("m2")) {
				runML(ExternalML.Input.FORCE_2);
				return;
			}

			if (label.equals("m3")) {
				runML(ExternalML.Input.FORCE_3);
				return;
			}

			if (label.equals("ml")) {
				runML(ExternalML.Input.FORCE_0 | ExternalML.Input.FORCE_1
						| ExternalML.Input.FORCE_2 | ExternalML.Input.FORCE_3);
				return;
			}

			if (label.equals("ah")) {
				editor.getUserSupport().applyTactic(
						Tactics.lemma(textInput.getTextWidget().getText()));
				return;
			}

			if (label.equals("ct")) {
				editor.getUserSupport().applyTactic(Tactics.contradictGoal());
				return;
			}

			if (label.equals("sh")) {
				editor.getUserSupport().searchHyps(
						textInput.getTextWidget().getText());
				return;
			}
		} catch (RodinDBException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Runs the predicate prover on the current proof tree node.
	 * 
	 * @param restricted
	 *            <code>true</code> is only selected hypotheses should be
	 *            passed to PP
	 */
	private void runPP(final boolean restricted) {
		final UserSupport userSupport = editor.getUserSupport();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					userSupport.applyTactic(Tactics.externalPP(restricted,
							monitor));
				} catch (RodinDBException e) {
					e.printStackTrace();
					throw new InvocationTargetException(e);
				}
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
	private void runML(final int forces) {
		final UserSupport userSupport = editor.getUserSupport();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					userSupport
							.applyTactic(Tactics.externalML(forces, monitor));
				} catch (RodinDBException e) {
					e.printStackTrace();
					throw new InvocationTargetException(e);
				}
			}
		};
		applyTacticWithProgress(op);
	}

	/**
	 * Apply a tactic with a progress monitor (providing cancel button).
	 * <p>
	 * 
	 * @param op
	 *            a runnable with progress monitor.
	 */
	private void applyTacticWithProgress(IRunnableWithProgress op) {
		final Shell shell = ProofControlPage.this.scrolledForm.getShell();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the UserSupport
		editor.getUserSupport().removeGoalChangedListener(this);
		editor.getUserSupport().removeStatusChangedListener(this);
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

		buttonBar = new ToolBar(body, SWT.NONE);
		buttonBar.setLayoutData(new GridData());

		new ToolItem(buttonBar, SWT.SEPARATOR);

		// Create drop-down externalProvers
		externalProvers = createToolItem(buttonBar, SWT.DROP_DOWN, "p0", null,
				null, "External Prover: PP restricted to selected hypotheses");
		NewDropdownSelectionListener listenerOne = new NewDropdownSelectionListener(
				externalProvers);
		listenerOne.add("p0");
		listenerOne.add("pp");
		listenerOne.add("m0");
		listenerOne.add("m1");
		listenerOne.add("m2");
		listenerOne.add("m3");
		listenerOne.add("ml");
		externalProvers.addSelectionListener(listenerOne);
		Rectangle rec = externalProvers.getBounds();
		// UIUtils.debug("Width: " + rec.width);

		ToolItem separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		nm = createToolItem(buttonBar, SWT.PUSH, "nm", null, null,
				"Normalize tactic");
		nm.addSelectionListener(new ContextButtonListener("nm"));
		rec = nm.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		ah = createToolItem(buttonBar, SWT.PUSH, "ah", null, null,
				"Add hypothesis");
		ah.addSelectionListener(new ContextButtonListener("ah"));
		ah.setWidth(defaultWidth);
		rec = ah.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		dc = createToolItem(buttonBar, SWT.PUSH, "dc", null, null,
				"Case distinction");
		dc.addSelectionListener(new ContextButtonListener("dc"));
		dc.setWidth(defaultWidth);
		rec = dc.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		ct = createToolItem(buttonBar, SWT.PUSH, "ct", null, null,
				"Contradiction");
		ct.addSelectionListener(new ContextButtonListener("ct"));
		ct.setWidth(defaultWidth);
		rec = ct.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		ba = createToolItem(buttonBar, SWT.PUSH, "ba", null, null,
				"Backtrack from current node");
		ba.addSelectionListener(new ContextButtonListener("ba"));
		ba.setWidth(defaultWidth);
		rec = ba.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		pn = createToolItem(buttonBar, SWT.PUSH, "pn", null, null,
				"Prune the current subtree");
		pn.addSelectionListener(new ContextButtonListener("pn"));
		pn.setWidth(defaultWidth);
		rec = pn.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		sh = createToolItem(buttonBar, SWT.PUSH, "", EventBUIPlugin
				.getDefault().getImageRegistry().get(
						EventBImage.IMG_SEARCH_BUTTON), null,
				"Search hypotheses");
		sh.addSelectionListener(new ContextButtonListener("sh"));
		sh.setWidth(defaultWidth);
		rec = sh.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		pv = createToolItem(buttonBar, SWT.PUSH, "", PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_TOOL_BACK), null,
				"Previous undischarged PO");
		pv.addSelectionListener(new ContextButtonListener("pv"));
		pv.setWidth(defaultWidth);
		rec = pv.getBounds();
		separator = new ToolItem(buttonBar, SWT.SEPARATOR);
		separator.setWidth(defaultWidth - rec.width);

		ne = createToolItem(buttonBar, SWT.PUSH, "", PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD),
				null, "Next undischarged PO");
		ne.addSelectionListener(new ContextButtonListener("ne"));
		ne.setWidth(defaultWidth);
		// rec = ne.getBounds();
		new ToolItem(buttonBar, SWT.SEPARATOR);
		// separator.setWidth(defaultWidth - rec.width);

		// A text field
		textInput = new EventBMath(toolkit.createText(body, "", SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL));

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		gd.widthHint = 200;
		textInput.getTextWidget().setLayoutData(gd);
		textInput.getTextWidget().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateToolItems();
			}
		});

		ProofState proofState = editor.getUserSupport().getCurrentPO();
		if (proofState != null) {
			IProofTreeNode node = proofState.getCurrentNode();
			isOpened = (node != null && node.isOpen()) ? true : false;
			isTop = (node != null && node.getParent() == null) ? true : false;
		} else {
			isOpened = false;
		}
		updateToolItems();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IGoalChangedListener#goalChanged(org.eventb.core.pm.IGoalChangeEvent)
	 */
	public void goalChanged(IGoalChangeEvent e) {
		IProofTreeNode node = e.getDelta().getProofTreeNode();
		if (node != null && node.isOpen())
			isOpened = true;
		else
			isOpened = false;
		if (node.getParent() == null)
			isTop = true;
		else
			isTop = false;
		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				updateToolItems();
			}
		});
	}

	/**
	 * Update the status of the toolbar items.
	 */
	private void updateToolItems() {
		if (isOpened) {
			pn.setEnabled(false);
			nm.setEnabled(true);
			if (isTop)
				ba.setEnabled(false);
			else
				ba.setEnabled(true);
			externalProvers.setEnabled(true);
			ct.setEnabled(true);
			if (textInput.getTextWidget().getText().equals(""))
				dc.setEnabled(false);
			else
				dc.setEnabled(true);
			if (textInput.getTextWidget().getText().equals(""))
				ah.setEnabled(false);
			else
				ah.setEnabled(true);
			sh.setEnabled(true);
		} else {
			pn.setEnabled(true);
			nm.setEnabled(false);
			ba.setEnabled(false);
			externalProvers.setEnabled(false);
			dc.setEnabled(false);
			ct.setEnabled(false);
			ah.setEnabled(false);
			sh.setEnabled(false);
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IStatusChangedListener#statusChanged(java.lang.Object)
	 */
	public void statusChanged(final Object information) {
		final ProofControlPage page = this;

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (information != null)
					setFormTextInformation(information.toString());
				else
					setFormTextInformation("");
				scrolledForm.reflow(true);
				page.setFocus();
			}
		});
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the "drop down" functionality for our
	 *         dropdown tool items.
	 */
	class NewDropdownSelectionListener extends SelectionAdapter {
		private ToolItem dropdown;

		private Menu menu;

		/**
		 * Constructs a DropdownSelectionListener
		 * 
		 * @param dropdown
		 *            the dropdown this listener belongs to
		 */
		public NewDropdownSelectionListener(ToolItem dropdown) {
			this.dropdown = dropdown;
			menu = new Menu(dropdown.getParent().getShell());
		}

		/**
		 * Adds an item to the dropdown list
		 * 
		 * @param item
		 *            the item to add
		 */
		public void add(String item) {
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(item);
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					MenuItem selected = (MenuItem) event.widget;
					String label = selected.getText();
					dropdown.setText(label);
					if (label.equals("p0"))
						dropdown
								.setToolTipText("External Prover: PP restricted to selected hypotheses");
					else if (label.equals("pp"))
						dropdown
								.setToolTipText("External Prover: PP with all hypotheses");
					else if (label.equals("m0"))
						dropdown
								.setToolTipText("External Prover: ML in force 0");
					else if (label.equals("m1"))
						dropdown
								.setToolTipText("External Prover: ML in force 1");
					else if (label.equals("m2"))
						dropdown
								.setToolTipText("External Prover: ML in force 2");
					else if (label.equals("m3"))
						dropdown
								.setToolTipText("External Prover: ML in force 3");
					else if (label.equals("ml"))
						dropdown
								.setToolTipText("External Prover: ML in all forces");

					dropdown.getParent().redraw();
					scrolledForm.reflow(true);
					buttonSelectedResponse(label);
				}
			});
		}

		/**
		 * Called when either the button itself or the dropdown arrow is clicked
		 * 
		 * @param event
		 *            the event that trigged this call
		 */
		public void widgetSelected(SelectionEvent event) {
			// If they clicked the arrow, we show the list
			if (event.detail == SWT.ARROW) {
				// Determine where to put the dropdown list
				ToolItem item = (ToolItem) event.widget;
				Rectangle rect = item.getBounds();
				Point pt = item.getParent()
						.toDisplay(new Point(rect.x, rect.y));
				menu.setLocation(pt.x, pt.y + rect.height);
				menu.setVisible(true);
			} else {
				// They pushed the button; take appropriate action
				String label = dropdown.getText();
				buttonSelectedResponse(label);
			}
		}
	}

}
