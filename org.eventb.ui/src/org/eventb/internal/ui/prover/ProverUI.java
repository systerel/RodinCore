/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - separation of file and root element
 *     Systerel - handled user support saving state
 *     Systerel - redirected dialog opening and externalized strings
 ******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.utils.Messages.dialogs_prover_error_creating_page;
import static org.eventb.internal.ui.utils.Messages.error_cannot_save_as_message;
import static org.eventb.internal.ui.utils.Messages.error_unsupported_action;
import static org.eventb.internal.ui.utils.Messages.title_prover_editor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.cachehypothesis.CacheHypothesisPage;
import org.eventb.internal.ui.cachehypothesis.ICacheHypothesisPage;
import org.eventb.internal.ui.eventbeditor.EventBFormEditor;
import org.eventb.internal.ui.goal.GoalPage;
import org.eventb.internal.ui.goal.IGoalPage;
import org.eventb.internal.ui.proofcontrol.IProofControlPage;
import org.eventb.internal.ui.proofcontrol.ProofControlPage;
import org.eventb.internal.ui.proofinformation.IProofInformationPage;
import org.eventb.internal.ui.proofinformation.ProofInformationPage;
import org.eventb.internal.ui.prooftreeui.IProofTreeUIPage;
import org.eventb.internal.ui.prooftreeui.ProofTreeUIPage;
import org.eventb.internal.ui.searchhypothesis.ISearchHypothesisPage;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesisPage;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This implements the Prover UI Editor by extending the FormEditor
 */
public class ProverUI extends EventBFormEditor implements
		IUserSupportManagerChangedListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	/**
	 * The identifier of the Prover UI editor (value
	 * <code>"org.eventb.internal.ui"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editors.ProverUI";

	// The outline page
	private ProofTreeUIPage fProofTreeUI;

	// The associated Proof Control page
	private ProofControlPage fProofControlPage;

	// The associated Proof Information page
	private ProofInformationPage fProofInformationPage;

	// The associated Search Hypothesis
	private SearchHypothesisPage fSearchHypothesisPage;

	// The associated Cache Hypothesis
	private ICacheHypothesisPage fCacheHypothesisPage;

	// The associated Goal
	private IGoalPage fGoalPage;

	// The associated UserSupport
	IUserSupport userSupport;

	// The associated Rodin file handle
	IRodinFile psFile = null;

	private ProofStatusLineManager statusManager = null;
	
	private boolean saving;

	/**
	 * Constructor: Create a new UserSupport.
	 */
	public ProverUI() {
		super();
		saving = false;
		this.userSupport = USM.newUserSupport();
		USM.addChangeListener(this);
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof IFileEditorInput) {
			final IFile inputFile = ((IFileEditorInput) input).getFile();
			psFile = RodinCore.valueOf(inputFile);
			userSupport.setInput(psFile);
			this.setPartName(psFile.getBareName());
		}
		editorDirtyStateChanged();
	}

	/**
	 * Set the current Proof Obligation.
	 * <p>
	 * 
	 * @param psStatus
	 *            current pr Sequent
	 */
	public void setCurrentPO(IPSStatus psStatus, IProgressMonitor monitor) {
		final IProofState proofState = userSupport.getCurrentPO();
		if (proofState != null && proofState.getPSStatus().equals(psStatus))
			return;
		try {
			userSupport.setCurrentPO(psStatus, monitor);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the associated UserSupport
	 * <p>
	 * 
	 * @return the associated UserSupport
	 */
	public IUserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 * <p>
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {
			addPage(new ProofsPage(this));
		} catch (PartInitException e) {
			UIUtils.showError(title_prover_editor,
					dialogs_prover_error_creating_page);
			// TODO Handle exception
		}
	}

	/**
	 * The <code>EventBMachineEditor</code> implementation of this
	 * <code>AbstractTextEditor</code> method performs any extra disposal
	 * actions required by the event-B editor.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		USM.removeChangeListener(this);
		userSupport.dispose();
		if (fProofTreeUI != null)
			fProofTreeUI.setInput(null);
		
		super.dispose();
	}

	/**
	 * The <code>ProverUI</code> implementation of this method performs gets
	 * the proof tree UI, proof information or proof control page if request is
	 * for an outline page.
	 * <p>
	 * 
	 * @param required
	 *            the required type
	 *            <p>
	 * @return an adapter for the required type or <code>null</code>
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class required) {
		if (IProofTreeUIPage.class.equals(required)) {
			// Create a new Proof Tree UI Page
			fProofTreeUI = new ProofTreeUIPage(userSupport);
				if (userSupport.getCurrentPO() != null) {
					fProofTreeUI.setInput(userSupport.getCurrentPO()
							.getProofTree());
				}
			return fProofTreeUI;
		}
		if (IProofControlPage.class.equals(required)) {
			// Create a new Proof Control Page
			fProofControlPage = new ProofControlPage(this);
			return fProofControlPage;
		}

		if (IProofInformationPage.class.equals(required)) {
			// Create a new Proof Information Page
			fProofInformationPage = new ProofInformationPage(this
					.getUserSupport());
			return fProofInformationPage;
		}

		if (ISearchHypothesisPage.class.equals(required)) {
			// Create a new Search Hypothesis Page
			fSearchHypothesisPage = new SearchHypothesisPage(this
					.getUserSupport(), this);
			return fSearchHypothesisPage;
		}

		if (ICacheHypothesisPage.class.equals(required)) {
			// Create a new Cache Hypothesis Page
			fCacheHypothesisPage = new CacheHypothesisPage(this
					.getUserSupport(), this);
			return fCacheHypothesisPage;
		}

		if (IGoalPage.class.equals(required)) {
			// Create a new Goal Page.
			fGoalPage = new GoalPage(this, this.getUserSupport());
			return fGoalPage;
		}

		return super.getAdapter(required);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSaveAs() {
		UIUtils.showError(error_unsupported_action,
				error_cannot_save_as_message);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		saving = true;
		IProofState[] proofStates = userSupport.getUnsavedPOs();

		final ListSelectionDialog dlg = new ListSelectionDialog(this.getSite()
				.getShell(), userSupport, new ProofStateContentProvider(
				proofStates), new ProofStateLabelProvider(),
				"Select the proof obligation(s) to save.");

		dlg.setInitialSelections(proofStates);
		dlg.setTitle("Save Proofs");
		dlg.open();
		final Object[] objects = dlg.getResult();
		if (objects != null && objects.length != 0) {
			final int length = objects.length;
			final IProofState[] results = new IProofState[length];
			System.arraycopy(objects, 0, results, 0, length);

			try {
				userSupport.doSave(results, monitor);
			} catch (RodinDBException e) {
				UIUtils.log(e, "while saving");
			}
		}
		saving = false;
		editorDirtyStateChanged(); // Refresh the dirty state of the editor
	}

	private class ProofStateContentProvider implements
			IStructuredContentProvider {

		private IProofState[] proofStates;

		public ProofStateContentProvider(IProofState[] proofStates) {
			this.proofStates = proofStates;
		}

		public Object[] getElements(Object inputElement) {
			return proofStates;
		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Do nothing
		}

	}

	class ProofStateLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getText(Object element) {
			if (element instanceof IProofState) {
				return ((IProofState) element).getPSStatus().getElementName();
			}
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// Do nothing

		}

		public void dispose() {
			// Do nothing

		}

		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Getting the outline page associated with this editor
	 * 
	 * @return the outline page
	 */
	protected ProofTreeUIPage getProofTreeUI() {
		return fProofTreeUI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// if (userSupport.isOutOfDate()) {
		// updateUserSupport();
		// }
		final IProofState currentPO = userSupport.getCurrentPO();
		if (currentPO != null && currentPO.isUninitialised())
			UIUtils.runWithProgressDialog(this.getEditorSite().getShell(),
					new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException {
							try {
								userSupport.setCurrentPO(currentPO
										.getPSStatus(), monitor);
							} catch (RodinDBException e) {
								throw new InvocationTargetException(e);
							}
						}

					});
		super.setFocus();
	}

	@Override
	public IRodinFile getRodinInputFile() {
		if (psFile == null) {
			FileEditorInput editorInput = (FileEditorInput) this
					.getEditorInput();

			IFile inputFile = editorInput.getFile();

			psFile = RodinCore.valueOf(inputFile);
		}
		return psFile;
	}

	/**
	 * Get the current PRSequent.
	 * <p>
	 * 
	 * @return the current PRSequent
	 */
	public IPSStatus getCurrentProverSequent() {
		IProofState ps = getUserSupport().getCurrentPO();
		if (ps != null)
			return ps.getPSStatus();
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return this.userSupport.hasUnsavedChanges();
		// try {
		// // UIUtils.debug("Checking dirty state " +
		// // this.getRodinInput().hasUnsavedChanges());
		// return this.getRodinInput().hasUnsavedChanges();
		// } catch (RodinDBException e) {
		// e.printStackTrace();
		// }
		// return super.isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {

		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Begin User Support Manager Changed");

		if (saving)
			return; // Ignore delta while saving

		
		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		// Do nothing if there is no change for this current user support.
		if (affectedUserSupport == null)
			return;

		// If the user support has been removed. This should be only the effect
		// of closing the editor, hence doing nothing
		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = this.getEditorSite().getShell().getDisplay();
		
		display.syncExec(new Runnable() {
			public void run() {
				Control control = getActiveControl(); 
				if (control == null)
					return;

				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();

					// Set the information if it has been changed.
					if ((flags & IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}
					
					if ((flags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.
						IProofStateDelta[] affectedProofStates = affectedUserSupport
								.getAffectedProofStates();
						for (IProofStateDelta affectedProofState : affectedProofStates) {
							int psKind = affectedProofState.getKind();

							if (psKind == IProofStateDelta.ADDED) {
								ProverUI.this.editorDirtyStateChanged();
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								ProverUI.this.editorDirtyStateChanged();
								return;
							}

							if (psKind == IProofStateDelta.CHANGED) {
								// If there are some changes to the proof state.
								int psFlags = affectedProofState.getFlags();

								if ((psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
									ProverUI.this.editorDirtyStateChanged();
									return;
								}

							}
						}
					}
				}
			}
		});
	
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("End User Support Manager Changed");
	}

	Control getActiveControl() {
		int activePage = getActivePage();
		if (activePage == -1)
			return null;
		final Control control = this.getControl(activePage);
		if (control.isDisposed())
			return null;
		return control;
	}

	protected void setInformation(IUserSupportInformation[] information) {
		if (statusManager == null) {
			statusManager = new ProofStatusLineManager(this.getEditorSite()
					.getActionBars());
		}
		statusManager.setProofInformation(information);
		return;
	}

	public IProofControlPage getProofControl() {
		return fProofControlPage;
	}

}
