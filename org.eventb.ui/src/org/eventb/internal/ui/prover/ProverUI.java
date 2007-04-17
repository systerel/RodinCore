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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.cachehypothesis.CacheHypothesisPage;
import org.eventb.internal.ui.cachehypothesis.ICacheHypothesisPage;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.proofcontrol.IProofControlPage;
import org.eventb.internal.ui.proofcontrol.ProofControlPage;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.internal.ui.proofinformation.IProofInformationPage;
import org.eventb.internal.ui.proofinformation.ProofInformationPage;
import org.eventb.internal.ui.prooftreeui.IProofTreeUIPage;
import org.eventb.internal.ui.prooftreeui.ProofTreeUIPage;
import org.eventb.internal.ui.searchhypothesis.ISearchHypothesisPage;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesisPage;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This implements the Prover UI Editor by extending the FormEditor
 */
public class ProverUI extends FormEditor implements
		IUserSupportManagerChangedListener {

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

	// The associated UserSupport
	IUserSupport userSupport;

	// The associated rodin file handle
	IPSFile prFile = null;

	private boolean saving;

	/**
	 * Constructor: Create a new UserSupport.
	 */
	public ProverUI() {
		super();
		saving = false;

		IUserSupportManager manager = EventBPlugin.getDefault()
				.getUserSupportManager();
		this.userSupport = manager.newUserSupport();
		manager.addChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		if (input instanceof IFileEditorInput) {
			IFile inputFile = ((IFileEditorInput) input).getFile();
			prFile = (IPSFile) RodinCore.valueOf(inputFile);
			UIUtils.runWithProgressDialog(this.getEditorSite().getShell(),
					new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								userSupport.setInput(prFile, monitor);
							} catch (RodinDBException e) {
								e.printStackTrace();
							}
						}
			});
			this.setPartName(prFile.getComponentName());
		}
		super.setInput(input);
	}

	/**
	 * Set the current Proof Obligation.
	 * <p>
	 * 
	 * @param prSequent
	 *            current pr Sequent
	 */
	public void setCurrentPO(IPSStatus prSequent, IProgressMonitor monitor) {
		IProofState proofState = userSupport.getCurrentPO();
		if (proofState != null && proofState.getPSStatus().equals(prSequent))
			return;
		try {
			userSupport.setCurrentPO(prSequent, monitor);
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
			MessageDialog.openError(null, "Prover UI Editor",
					"Error creating pages for Prover UI Editor");
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
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(this);
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
	public Object getAdapter(Class required) {
		if (IProofTreeUIPage.class.equals(required)) {
			if (fProofTreeUI == null) {
				fProofTreeUI = new ProofTreeUIPage(userSupport);
				if (userSupport.getCurrentPO() != null) {
					fProofTreeUI.setInput(userSupport.getCurrentPO()
							.getProofTree());
				}
			}
			return fProofTreeUI;
		}
		if (IProofControlPage.class.equals(required)) {
			if (fProofControlPage == null) {
				fProofControlPage = new ProofControlPage(this);
			}
			return fProofControlPage;
		}

		if (IProofInformationPage.class.equals(required)) {
			if (fProofInformationPage == null) {
				fProofInformationPage = new ProofInformationPage(this);
			}
			return fProofInformationPage;
		}

		if (ISearchHypothesisPage.class.equals(required)) {
			if (fSearchHypothesisPage == null) {
				fSearchHypothesisPage = new SearchHypothesisPage(this);
			}
			return fSearchHypothesisPage;
		}

		if (ICacheHypothesisPage.class.equals(required)) {
			if (fCacheHypothesisPage == null) {
				fCacheHypothesisPage = new CacheHypothesisPage(this);
			}
			return fCacheHypothesisPage;
		}

		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		MessageDialog.openInformation(null, null, "Saving");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		saving = true;
		IProofState[] proofStates = userSupport.getUnsavedPOs();

		final ListSelectionDialog dlg = new ListSelectionDialog(this.getSite()
				.getShell(), userSupport, new ProofStateContentProvider(
				proofStates), new ProofStateLabelProvider(),
				"Select the proof obligation(s) to save.");

		IProofState[] initSelection = { userSupport.getCurrentPO() };
		dlg.setInitialSelections(initSelection);
		dlg.setTitle("Save Proofs");
		dlg.open();
		final Object[] objects = dlg.getResult();
		if (objects != null && objects.length != 0) {
			final int length = objects.length;
			final IProofState[] results = new IProofState[length];
			System.arraycopy(objects, 0, results, 0, length);

			try {
				RodinCore.run(new IWorkspaceRunnable() {
					public void run(IProgressMonitor pm) throws CoreException {
						userSupport.doSave(results, pm);
					}
				}, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			UIUtils.runWithProgressDialog(this.getEditorSite().getShell(), new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						userSupport.setCurrentPO(currentPO.getPSStatus(),
								new NullProgressMonitor());
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		syncObligationExplorer();
		super.setFocus();
		// UIUtils.debugProverUI("Focus");
		// Find obligationExplorer and sync
	}

	/**
	 * Try to synchronise with the obligation explorer to show the current
	 * obligation.
	 */
	private void syncObligationExplorer() {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Sync");
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			ObligationExplorer obligationExplorer = (ObligationExplorer) activePage
					.findView(ObligationExplorer.VIEW_ID);
			if (obligationExplorer != null) {
				IProofState ps = this.getUserSupport().getCurrentPO();
				if (ps != null) {
					IPSStatus prSequent = this.getUserSupport().getCurrentPO()
							.getPSStatus();
					obligationExplorer.externalSetSelection(prSequent);
					obligationExplorer.getTreeViewer().reveal(prSequent);
				} else {
					// obligationExplorer.externalSetSelection(this.getRodinInput());
				}
				// TreeViewer viewer = obligationExplorer.getTreeViewer();
				// UIUtils.debug("Make new selection ");
			}
		}
	}

	/**
	 * Getting the RodinFile associated with this editor
	 * <p>
	 * 
	 * @return a handle to a Rodin file
	 */
	public IPSFile getRodinInput() {
		if (prFile == null) {
			FileEditorInput editorInput = (FileEditorInput) this
					.getEditorInput();

			IFile inputFile = editorInput.getFile();

			prFile = (IPSFile) RodinCore.valueOf(inputFile);
		}
		return prFile;
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
			if (ProofControlUtils.DEBUG)
				ProofControlUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
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
	}

}
