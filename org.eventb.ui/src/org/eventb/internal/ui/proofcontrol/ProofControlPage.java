/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - used EventBPreferenceStore
 *     Systerel - added direct access to preference pages
 *     Systerel - passed the focus request to the text field
 *     Systerel - the input area is now a StyledText
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - redirected dialog opening and externalized strings
 *     Systerel - fixed Hyperlink.setImage() calls
 *     Systerel - removed direct access to tactic preferences
 *     Systerel - added support for autocompletion
 *     Systerel - dynamic label for open preferences (with scope)
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;
import static org.eventb.internal.ui.prover.CharacterPairHighlighter.highlight;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyCommand;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyTactic;
import static org.eventb.internal.ui.utils.Messages.title_unexpectedError;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.ui.EventBControl;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBStyledText;
import org.eventb.internal.ui.IEventBControl;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.internal.ui.prover.CharacterPairHighlighter;
import org.eventb.internal.ui.prover.ICommandApplication;
import org.eventb.internal.ui.prover.ProofStatusLineManager;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.registry.DropdownInfo;
import org.eventb.internal.ui.prover.registry.DynamicDropdownInfo;
import org.eventb.internal.ui.prover.registry.TacticApplicationProxy;
import org.eventb.internal.ui.prover.registry.TacticUIInfo;
import org.eventb.internal.ui.prover.registry.TacticUIRegistry;
import org.eventb.internal.ui.prover.registry.ToolbarInfo;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 *         </p>
 */
public class ProofControlPage extends Page implements IProofControlPage,
		IUserSupportManagerChangedListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	boolean share;

	EnablePostTacticAction enablePostTactic;
	
	OpenPreferencesAction openPreferences;

	IEventBControl textInput;
	StyledText textWidget;

	final ProverUI editor;

	ScrolledForm scrolledForm;

	private Collection<GlobalTacticDropdownToolItem> dropdownItems;

	private Collection<DynamicDropdownManager> dynDropdowns;

	private Collection<GlobalTacticToolItem> toolItems;

	Combo historyCombo;

	private EventBControl history;

	private Composite pgComp;

	String currentInput = "";
	
	private Composite midComp;

	ImageHyperlink smiley; 
	
	private ProofStatusLineManager statusManager;

	private CharacterPairHighlighter ch;

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI editor associated with this Proof Control page.
	 */
	public ProofControlPage(ProverUI editor) {
		this.editor = editor;
		USM.addChangeListener(this);
	}

	/**
	 * Apply a tactic with a progress monitor (providing cancel button).
	 * 
	 * @param op
	 *            a runnable with progress monitor.
	 */
	private static void applyTacticWithProgress(IRunnableWithProgress op) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		final Shell shell = display.getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			UIUtils.showError(title_unexpectedError,message);
			return;
		}
	}

	@Override
	public void dispose() {
		// Deregister with the UserSupport
		USM.removeChangeListener(this);
		enablePostTactic.dispose();
		openPreferences.dispose();
		textInput.dispose();
		history.dispose();
		scrolledForm.dispose();
		if (ch != null){
			ch.remove();
		}
		for (GlobalTacticDropdownToolItem dropdown : dropdownItems) {
			dropdown.dispose();
		}
		for(DynamicDropdownManager dropdown: dynDropdowns) {
			dropdown.dispose();
		}
		super.dispose();
	}

	CoolItem createItem(CoolBar coolBar, ToolbarInfo info) {
		final ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);

		final List<DropdownInfo> dropdowns = info.getDropdowns();
		for (final DropdownInfo dropdown : dropdowns) {
			final List<TacticUIInfo> tactics = dropdown.getTactics();

			if (tactics.size() != 0) {
				ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);

				GlobalTacticDropdownToolItem dropdownItem = new GlobalTacticDropdownToolItem(
						item, dropdown) {
					@Override
					public void apply(final TacticUIInfo tactic) {
						if (ProofControlUtils.DEBUG)
							ProofControlUtils.debug("File "
									+ ProofControlPage.this.editor
									.getRodinInputFile()
											.getElementName());
						final IUserSupport userSupport = editor
								.getUserSupport();
						final boolean interruptable = tactic.isInterruptable();
						final Object application = tactic.getGlobalApplication(
								userSupport, currentInput);

						if (application instanceof TacticApplicationProxy<?>) {
							applyTacticProvider(
									(TacticApplicationProxy<?>) application,
									userSupport, interruptable);
						} else if (application instanceof ICommandApplication) {
							applyGlobalExpertTactic((ICommandApplication) application,
									userSupport, interruptable);
						} else {
							return;
						}
						if (!currentInput.equals("")) {
							historyCombo.add(currentInput, 0);
						}
						if (!textWidget.getText().isEmpty()) {
							textWidget.setText("");
						}
						currentInput = "";
					}
				};

				dropdownItems.add(dropdownItem);

				for (TacticUIInfo tactic : tactics) {
					if (ProofControlUtils.DEBUG)
						ProofControlUtils.debug("Tactic: " + tactic);
					dropdownItem.addTactic(tactic);
				}
				if (ProofControlUtils.DEBUG)
					ProofControlUtils.debug("----------------------------");

			}

		}

		for (final TacticUIInfo tactic : info.getTactics()) {
			ToolItem item = new ToolItem(toolBar, SWT.PUSH);
			item.setImage(tactic.getIcon());
			item.setToolTipText(tactic.getTooltip());

			final GlobalTacticToolItem globalTacticToolItem = new GlobalTacticToolItem(
					item, tactic, tactic.isInterruptable());

			item.addSelectionListener(new SelectionAdapter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (ProofControlUtils.DEBUG)
						ProofControlUtils.debug("File "
								+ ProofControlPage.this.editor.getRodinInputFile()
										.getElementName());

					final IUserSupport userSupport = editor
					.getUserSupport();
					final boolean interruptable = tactic.isInterruptable();
					final Object application = tactic.getGlobalApplication(
							userSupport, currentInput);
					if (application instanceof TacticApplicationProxy<?>) {
						applyTacticProvider(
								(TacticApplicationProxy<?>) application,
								userSupport, interruptable);
					} else if (application instanceof ICommandApplication) {
						applyGlobalExpertTactic((ICommandApplication) application, userSupport,
								interruptable);
					} else {
						return;
					}
					if (!currentInput.equals("")) {
						historyCombo.add(currentInput, 0);
					}
					if (!textWidget.getText().isEmpty()) {
						textWidget.setText("");
					}
				}
			});
			toolItems.add(globalTacticToolItem);
		}
		
		for (DynamicDropdownInfo dynDropdown : info.getDynamicDropdowns()) {
			final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
			item.setText(dynDropdown.getName());

			final DynamicDropdownManager manager = new DynamicDropdownManager(
					item, dynDropdown.getTacticProvider(), editor);
			item.addSelectionListener(manager);
			dynDropdowns.add(manager);
		}

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		// width increment required on windows platforms
		// in order not to hide right most button with right separation bar
		final int widthIncr = 4;
		Point preferred = item.computeSize(size.x + widthIncr, size.y);
		item.setPreferredSize(preferred);
		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget target = new DropTarget(toolBar, operations);

		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
		target.setTransfer(types);

		target.addDropListener(new ToolBarDropTargetListener(toolBar,
				textTransfer, fileTransfer));

		return item;
	}

	// Applies a global tactic to the current proof tree node.
	void applyGlobalExpertTactic(final ICommandApplication command,
			final IUserSupport userSupport, final boolean interruptable) {

		final String[] inputs = { currentInput };
		if (interruptable) {
			applyTacticWithProgress(new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor pm)
						throws InvocationTargetException {
					applyCommand(command.getProofCommand(), userSupport, null,
							inputs, pm);
				}
			});

		} else {
			applyCommand(command.getProofCommand(), userSupport, null, inputs, null);
		}
	}

	
	// Applies a global tactic to the current proof tree node.
	void applyTacticProvider(TacticApplicationProxy<?> appli,
			final IUserSupport userSupport, boolean interruptable) {

		final ITactic tactic = appli.getTactic(null, currentInput);
		final boolean skipPostTactic = appli.isSkipPostTactic();
		if (interruptable) {
			applyTacticWithProgress(new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor pm)
						throws InvocationTargetException {
					applyTactic(tactic, userSupport, null, skipPostTactic, pm);
				}
			});
		} else {
			applyTactic(tactic, userSupport, null, skipPostTactic, null);
		}
	}

	private class ToolBarDropTargetListener implements DropTargetListener {

		ToolBar toolBar;

		TextTransfer textTransfer;

		FileTransfer fileTransfer;

		ToolItem currentItem;

		public ToolBarDropTargetListener(ToolBar toolBar,
				TextTransfer textTransfer, FileTransfer fileTransfer) {
			this.toolBar = toolBar;
			this.textTransfer = textTransfer;
			this.fileTransfer = fileTransfer;
		}

		private ToolItem findCurrentItem(Point pt) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Drop target location " + pt.x + ", "
						+ pt.y);

			Point loc = toolBar.getLocation();

			Composite parent = toolBar.getParent();
			while (parent != null) {
				Point point = parent.getLocation();
				loc.x = loc.x + point.x;
				loc.y = loc.y + point.y;
				parent = parent.getParent();
			}
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Location Toolbar " + loc);

			ToolItem[] children = toolBar.getItems();
			ToolItem found = null;
			for (ToolItem child : children) {
				Rectangle rec = child.getBounds();
				// Rectangle area = toolBar.getLocation();
				if (ProofControlUtils.DEBUG) {
					ProofControlUtils.debug("Tool Item " + child);
					ProofControlUtils.debug("Rec: " + rec);
				}
				// ProofControl.debug("Area: " + area);
				if (loc.x + rec.x <= pt.x && pt.x <= loc.x + rec.x + rec.width) {
					found = child;
					break;
				}
			}

			return found;
		}

		@Override
		public void dragEnter(DropTargetEvent event) {

			Point pt = new Point(event.x, event.y);
			ToolItem item = findCurrentItem(pt);

			if (item != currentItem) {
				currentItem = item;
			}
			if (item != null && !item.isEnabled())
				item.setEnabled(true);

			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
			// will accept text but prefer to have files dropped
			for (int i = 0; i < event.dataTypes.length; i++) {
				if (fileTransfer.isSupportedType(event.dataTypes[i])) {
					event.currentDataType = event.dataTypes[i];
					// files should only be copied
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
					break;
				}
			}
		}

		@Override
		public void dragOver(DropTargetEvent event) {
			// Determine which button are there

			Point pt = new Point(event.x, event.y);
			ToolItem item = findCurrentItem(pt);

			if (item != currentItem) {
				currentItem = item;
			}
			if (item != null && !item.isEnabled())
				item.setEnabled(true);

			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			if (textTransfer.isSupportedType(event.currentDataType)) {
				// NOTE: on unsupported platforms this will return null
				Object o = textTransfer.nativeToJava(event.currentDataType);
				String t = (String) o;
				if (t != null)
					if (ProofControlUtils.DEBUG)
						ProofControlUtils.debug(t);
			}

		}

		@Override
		public void dragOperationChanged(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0) {
					event.detail = DND.DROP_COPY;
				} else {
					event.detail = DND.DROP_NONE;
				}
			}
			// allow text to be moved but files should only be copied
			if (fileTransfer.isSupportedType(event.currentDataType)) {
				if (event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_NONE;
				}
			}
		}

		@Override
		public void dragLeave(DropTargetEvent event) {
			updateToolItems();
		}

		@Override
		public void dropAccept(DropTargetEvent event) {
			// Do nothing
		}

		@Override
		public void drop(DropTargetEvent event) {
			if (textTransfer.isSupportedType(event.currentDataType)) {
				if (ProofControlUtils.DEBUG)
					ProofControlUtils.debug("Drop Text: " + event.data);
				currentInput = (String) event.data;
				currentItem.notifyListeners(SWT.Selection, new Event());
				// String text = (String) event.data;
				// TableItem item = new TableItem(dropTable, SWT.NONE);
				// item.setText(text);
			}
			if (fileTransfer.isSupportedType(event.currentDataType)) {
				String[] files = (String[]) event.data;
				for (int i = 0; i < files.length; i++) {
					// TableItem item = new TableItem(dropTable, SWT.NONE);
					// item.setText(files[i]);
				}
			}
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		pgComp = toolkit.createComposite(parent, SWT.NULL);
		pgComp.setLayout(new FormLayout());

		CoolBar coolBar = new CoolBar(pgComp, SWT.FLAT);

		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);

		scrolledForm = toolkit.createScrolledForm(pgComp);
		FormData scrolledData = new FormData();
		scrolledData.left = new FormAttachment(0);
		scrolledData.right = new FormAttachment(100);
		scrolledData.top = new FormAttachment(coolBar);
		scrolledData.bottom = new FormAttachment(100);
		scrolledForm.setLayoutData(scrolledData);

		Composite body = scrolledForm.getBody();
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		body.setLayout(gl);

		// Create toolbars
		dropdownItems = new ArrayList<GlobalTacticDropdownToolItem>();
		dynDropdowns = new ArrayList<DynamicDropdownManager>();
		toolItems = new ArrayList<GlobalTacticToolItem>();

		final TacticUIRegistry registry = TacticUIRegistry.getDefault();
		final List<ToolbarInfo> toolbars = registry.getToolbars();
		for (ToolbarInfo toolbar : toolbars) {
			createItem(coolBar, toolbar);
		}

		midComp = toolkit.createComposite(body, SWT.NULL);
		midComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		midComp.setBackground(scrolledForm.getBackground());
		gl = new GridLayout();
		gl.numColumns = 2;
		midComp.setLayout(gl);
		
		smiley = new ImageHyperlink(midComp, SWT.BOTTOM);
		smiley.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		smiley.setBackground(scrolledForm.getBackground());

		// A text field
		textWidget = new StyledText(midComp, SWT.MULTI | SWT.BORDER);
		ch = highlight(textWidget);
		toolkit.adapt(textWidget, true, false);	
		textInput = new EventBStyledText(textWidget, true);

		if (ProofControlUtils.DEBUG) {
			textWidget.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					ProofControlUtils.debug("File: "
							+ ProofControlPage.this.editor.getRodinInputFile()
									.getElementName());
				}

			});
		}

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = textWidget.getLineHeight() * 2;
		gd.widthHint = 200;
		textWidget.setLayoutData(gd);
		textWidget.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				currentInput = textWidget.getText();
				updateToolItems();
			}
		});

		ContentProposalFactory.makeContentProposal(textWidget,
				editor.getUserSupport());

		toolkit.paintBordersFor(midComp);
		
		historyCombo = new Combo(body, SWT.DROP_DOWN | SWT.READ_ONLY);
		historyCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				textWidget.setText(historyCombo.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		historyCombo.setLayoutData(gd);
		history = new EventBControl(historyCombo);

		scrolledForm.reflow(true);

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		updateToolItems();
		updateSmiley();
		coolBar.pack();
		pgComp.setVisible(false);
	}

	void updateSmiley() {
		Image image;
		IProofState currentPO = editor.getUserSupport().getCurrentPO();
		if (currentPO == null)
			image = EventBImage.getImage(EventBImage.IMG_DISCHARGED_SMILEY);
		else {
			IProofTree proofTree = currentPO.getProofTree();
			if (proofTree == null)
				image = EventBImage.getImage(EventBImage.IMG_DISCHARGED_SMILEY);
			else {
				IProofTreeNode root = proofTree.getRoot();
				if (root == null)
					image = EventBImage
							.getImage(EventBImage.IMG_DISCHARGED_SMILEY);
				else {
					int confidence = root.getConfidence();
					if (confidence <= IConfidence.PENDING)
						image = EventBImage
								.getImage(EventBImage.IMG_PENDING_SMILEY);
					else if (confidence <= IConfidence.REVIEWED_MAX)
						image = EventBImage
								.getImage(EventBImage.IMG_REVIEW_SMILEY);
					else
						image = EventBImage
								.getImage(EventBImage.IMG_DISCHARGED_SMILEY);
				}
			}
		}
		setHyperlinkImage(smiley, image);
		midComp.pack();
	}

	/**
	 * Set the information (in the bottom of the page).
	 * 
	 * @param information
	 *            the string (information from the UserSupport).
	 */
	void setStatusInformation(String information) {
		IStatusLineManager slManager = getSite().getActionBars()
				.getStatusLineManager();
		slManager.setMessage(information);
	}

	/**
	 * Setup the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
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
	}

	/**
	 * Fill the local pull down.
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(enablePostTactic);
		manager.add(openPreferences);
		manager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	void fillContextMenu(IMenuManager manager) {
		manager.add(enablePostTactic);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Create the actions used in this page.
	 */
	private void makeActions() {
		enablePostTactic = new EnablePostTacticAction();
		openPreferences = new OpenPreferencesAction(editor.getUserSupport());
	}

	/**
	 * Passing the focus request to the text field.
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		textWidget.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		return pgComp;
	}

	/**
	 * Update the status of the toolbar items.
	 */
	void updateToolItems() {
		final IUserSupport userSupport = editor.getUserSupport();
		final String input = textWidget.getText();

		for (GlobalTacticDropdownToolItem item : dropdownItems) {
			item.updateStatus(userSupport, input);
		}
		for (GlobalTacticToolItem item : toolItems) {
			item.updateStatus(userSupport, input);
		}
		for (DynamicDropdownManager dynDropdown : dynDropdowns) {
			dynDropdown.update();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	@Override
	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {

		if (ProofControlUtils.DEBUG)
			ProofControlUtils.debug("Begin User Support Manager Changed");

		// Do nothing if the form is disposed.
		if (scrolledForm.isDisposed())
			return;

		final IUserSupport userSupport = this.editor.getUserSupport();

		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		// Do nothing if there is no change for this current user support.
		if (affectedUserSupport == null)
			return;

		// If the user support has been removed, do nothing. This will be handle
		// by the main proof editor.
		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = scrolledForm.getDisplay();

		display.syncExec(new Runnable() {
			@Override
			public void run() {
				// Do nothing if the form is disposed.
				if (scrolledForm.isDisposed())
					return;

				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();

					// Set the information if it has been changed.
					if ((flags & IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}

					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed, update the tool
						// items.
						updateToolItems();
						updateSmiley();
						openPreferences.updateText();
						scrolledForm.reflow(true);
					} else if ((flags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.	
						IProofState proofState = userSupport.getCurrentPO();
						// Trying to get the change for the current proof state. 
						final IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						if (affectedProofState != null) {
							// If there are some changes
							int psKind = affectedProofState.getKind();

							if (psKind == IProofStateDelta.ADDED) {
								// This case should not happened
								if (ProofControlUtils.DEBUG)
									ProofControlUtils
											.debug("Error: Delta said that the proof state is added");
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								// Do nothing in this case, this will be handled
								// by the main proof editor.
								return;
							}
							
							if (psKind == IProofStateDelta.CHANGED) {
								// If there are some changes to the proof state.
								int psFlags = affectedProofState.getFlags();

								if ((psFlags & IProofStateDelta.F_NODE) != 0) {
									// Update the items if the current node has
									// been changed.
									updateToolItems();
									if ((psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
										updateSmiley();									
									}
								}
								else if ((psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
									// Update the items if the current node has
									// been changed.
									updateToolItems();
									updateSmiley();
								}
								scrolledForm.reflow(true);
							}
						}
					}
				}

				scrolledForm.reflow(true);
			}
		});

		if (ProofControlUtils.DEBUG)
			ProofControlUtils.debug("End User Support Manager Changed");

	}

	void setInformation(final IUserSupportInformation[] information) {
		if (statusManager == null) {
			statusManager = new ProofStatusLineManager(this.getSite()
					.getActionBars());
		}
		statusManager.setProofInformation(information);
	}

	@Override
	public String getInput() {
		return textWidget.getText();
	}
}
