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
package fr.systerel.eventb.proofpurger.popup.actions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eventb.core.IPRProof;
import org.eventb.ui.EventBUIPlugin;

/**
 * Dialog for the selection of proofs to be removed by the proof purger.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofPurgerSelectionDialog extends CheckedTreeSelectionDialog {

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            The shell to parent from.
	 * @param proofs
	 *            The proofs from which the user will make his selection.
	 */
	ProofPurgerSelectionDialog(Shell parentShell, IPRProof[] proofs) {
		super(parentShell, new ProofPurgerLabelProvider(), new ProofPurgerContentProvider(
				proofs));
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
		getTreeViewer().expandAll();
		getTreeViewer().setAllChecked(true);
	}

	/**
	 * Extract the proofs from the selected elements of the tree.
	 * 
	 * @return The selected proofs.
	 */
	public IPRProof[] getSelectedProofs() {
		Set<IPRProof> result = new LinkedHashSet<IPRProof>();
		for (Object o: this.getResult()) {
			if (o instanceof IPRProof) {
				result.add((IPRProof) o);
			}
		}
		return result.toArray(new IPRProof[result.size()]);
	}
}
