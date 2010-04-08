/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofpurger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;

/**
 * Dialog for the selection of proofs to be removed by the proof purger.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofPurgerSelectionDialog extends CheckedTreeSelectionDialog {

	private final ITreeContentProvider contentProvider;
	private final List<IPRProof> selectedProofs;
	private final List<IPRRoot> selectedFiles;
	
	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            The shell to parent from.
	 */
	ProofPurgerSelectionDialog(Shell parentShell, ITreeContentProvider contentProvider) {
		super(parentShell, new ProofPurgerLabelProvider(), contentProvider);
		this.contentProvider = contentProvider;
		this.selectedProofs = new ArrayList<IPRProof>();
		this.selectedFiles = new ArrayList<IPRRoot>();
		this.setInput(EventBUIPlugin.getRodinDatabase());
		this.setMessage(Messages.proofpurgerselectiondialog_selectproofstodelete);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.proofpurgerselectiondialog_proofpurgerselection);
	}

	@Override
	public void create() {
		setContainerMode(true);
		super.create();
		getOkButton().setText(Messages.proofpurgerselectiondialog_delete);
		
		expandAndCheckAll();
	}

	private void expandAndCheckAll() {
		final CheckboxTreeViewer treeViewer = getTreeViewer();
		treeViewer.expandAll();
		
		final Object[] items = treeViewer.getExpandedElements();
		treeViewer.setCheckedElements(items);
	}

    @Override
	protected void computeResult() {
        final List<Object> result = new ArrayList<Object>();
    	
    	final CheckboxTreeViewer treeViewer = getTreeViewer();

    	// filter selected leafs (proofs or files actually)
		for (Object o : treeViewer.getCheckedElements()) {
			if (!contentProvider.hasChildren(o)) {
				result.add(o);
				addToSpecificList(o);
			}
		}
		setResult(result);
    }

	private void addToSpecificList(Object o) {
		if (o instanceof IPRProof) {
			selectedProofs.add((IPRProof) o);
		} else if (o instanceof IPRRoot) {
			selectedFiles.add((IPRRoot) o);
		}
	}

	
	/**
	 * Extract the proofs from the selected elements of the tree.
	 * 
	 * @return The selected proofs.
	 */
	public List<IPRProof> getSelectedProofs() {
		return new ArrayList<IPRProof>(selectedProofs);
	}

	/**
	 * Extract the files from the selected elements of the tree.
	 * 
	 * @return The selected proofs.
	 */
	public List<IPRRoot> getSelectedFiles() {
		return new ArrayList<IPRRoot>(selectedFiles);
	}
}
