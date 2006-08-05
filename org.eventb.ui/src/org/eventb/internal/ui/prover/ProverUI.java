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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.pm.UserSupportManager;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This implements the Prover UI Editor by extending the FormEditor
 */
public class ProverUI extends FormEditor implements IProofStateChangedListener {

	/**
	 * The identifier of the Prover UI editor (value
	 * <code>"org.eventb.internal.ui"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editors.ProverUI";

	// Debug flag.
	public static boolean DEBUG = false;

	// The outline page
	private ProofTreeUIPage fProofTreeUI;

	// The associated Proof Control page
	private ProofControlPage fProofControlPage;

	// The associated Proof Information page
	private ProofInformationPage fProofInformationPage;

	// The associated UserSupport
	private UserSupport userSupport;

	// The associated rodin file handle
	private IPRFile prFile = null;

	private boolean saving;

	/**
	 * Constructor: Create a new UserSupport.
	 */
	public ProverUI() {
		super();
		saving = false;
		this.userSupport = UserSupportManager.newUserSupport();
		userSupport.addStateChangedListeners(this);
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
			prFile = (IPRFile) RodinCore.create(inputFile);
			try {
				UserSupportManager.setInput(userSupport, prFile);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
			this.setPartName(EventBPlugin.getComponentName(prFile
					.getElementName()));
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
	public void setCurrentPO(IPRSequent prSequent) {
		ProofState proofState = userSupport.getCurrentPO();
		if (proofState != null && proofState.getPRSequent().equals(prSequent))
			return;
		try {
			userSupport.setCurrentPO(prSequent);
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
	public UserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 * <p>
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
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
	public void dispose() {
		userSupport.removeStateChangedListeners(this);
		UserSupportManager.disposeUserSupport(userSupport);
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
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		MessageDialog.openInformation(null, null, "Saving");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		// try {
		// TODO Commit the information in the UI to the database
		// clear the dirty state on all the pages
		// if (this.pages != null) {
		// for (int i = 0; i < pages.size(); i++) {
		// Object page = pages.get(i);
		// // if (UIUtils.DEBUG) System.out.println("Trying to save page "
		// // + i + " : " + page);
		// if (page instanceof IFormPage) {
		// // if (UIUtils.DEBUG) System.out.println("Saving");
		// IFormPage fpage = (IFormPage) page;
		//
		// fpage.doSave(monitor);
		// // if (UIUtils.DEBUG) System.out.println("Dirty? " + i + " "
		// // + fpage.isDirty());
		// }
		// }
		// }
		saving = true;
		ProofState[] proofStates = userSupport.getUnsavedPOs();

		final ListSelectionDialog dlg = new ListSelectionDialog(this.getSite()
				.getShell(), userSupport, new ProofStateContentProvider(
				proofStates), new ProofStateLabelProvider(),
				"Select the proof obligation(s) to save.");

		ProofState[] initSelection = { userSupport.getCurrentPO() };
		dlg.setInitialSelections(initSelection);
		dlg.setTitle("Save Proofs");
		dlg.open();
		final Object[] results = dlg.getResult();

		if (results != null && results.length != 0) {

			final IPRFile prFile = this.getRodinInput();

			try {
				RodinCore.run(new IWorkspaceRunnable() {

					public void run(IProgressMonitor monitor)
							throws CoreException {
						for (Object result : results) {
							((ProofState) result).doSave();
						}
						// Save the file from the database to disk
						prFile.save(monitor, true);
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

		private ProofState[] proofStates;

		public ProofStateContentProvider(ProofState[] proofStates) {
			this.proofStates = proofStates;
		}

		public Object[] getElements(Object inputElement) {
			return proofStates;
		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

	private class ProofStateLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getText(Object element) {
			if (element instanceof ProofState) {
				return ((ProofState) element).getPRSequent().getElementName();
			}
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		public void dispose() {
			// TODO Auto-generated method stub

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
		if (userSupport.isOutOfDate()) {
			updateUserSupport();
		}
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
		ProverUIUtils.debugProverUI("Sync");
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		if (activePage != null) {
			ObligationExplorer obligationExplorer = (ObligationExplorer) activePage
					.findView(ObligationExplorer.VIEW_ID);
			if (obligationExplorer != null) {
				ProofState ps = this.getUserSupport().getCurrentPO();
				if (ps != null) {
					IPRSequent prSequent = this.getUserSupport().getCurrentPO()
							.getPRSequent();
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
	public IPRFile getRodinInput() {
		if (prFile == null) {
			FileEditorInput editorInput = (FileEditorInput) this
					.getEditorInput();

			IFile inputFile = editorInput.getFile();

			prFile = (IPRFile) RodinCore.create(inputFile);
		}
		return prFile;
	}

	/**
	 * Get the current PRSequent.
	 * <p>
	 * 
	 * @return the current PRSequent
	 */
	public IPRSequent getCurrentProverSequent() {
		ProofState ps = getUserSupport().getCurrentPO();
		if (ps != null)
			return ps.getPRSequent();
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
	 */
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
	public void proofStateChanged(IProofStateDelta delta) {

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				if (saving)
					return; // Ignore delta while saving
				if (userSupport.isOutOfDate()) {
					IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
					if (activePage.isPartVisible(ProverUI.this))
						updateUserSupport();
				}

				ProverUI.this.editorDirtyStateChanged();
				// syncObligationExplorer();
			}
		});
	}

	private void updateUserSupport() {
		// For each changed POs, check if the proof tree in the memory is replayable
		// if YES, then replay
//		IPRSequent sequent = null;
//		IProofTree tree = null;
//		IProofTree newTree;
//		try {
//			newTree = sequent.makeFreshProofTree();
//			if (Lib.proofReusable(tree.getProofDependencies(), newTree.getRoot().getSequent())) {
//				
//				(BasicTactics.pasteTac(tree.getRoot())).apply(newTree.getRoot());
//			}
//		} catch (RodinDBException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
			
		
		MessageDialog
				.openInformation(this.getActivePageInstance().getSite()
						.getShell(), "Out of Date",
						"The Proof Obligation is Out of Date and need to be reloeaded.");
		try {
			doSave(null);
			UserSupportManager.setInput(userSupport, this.getRodinInput());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;

	}

}
