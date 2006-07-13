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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.pm.ProofState;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBFormText;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an sub-class of Hypotheses Section to show the set of
 *         search hypotheses in Prover UI editor.
 */
public class SearchHypothesesSection extends HypothesesSection {

	// Title and description
	private static final String SECTION_TITLE = "Searched Hypotheses";

	private static final String SECTION_DESCRIPTION = "The set of searched hypotheses";

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	private class CachedHyperlinkAdapter extends HyperlinkAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkActivated(HyperlinkEvent e) {
			if (e.getLabel().equals("sl")) {
				Set<Hypothesis> selected = new HashSet<Hypothesis>();
				for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
					HypothesisRow hr = it.next();
					if (hr.isSelected()) {
						selected.add(hr.getHypothesis());
					}
				}
				if (selected.isEmpty())
					return;

				ProverUI editor = (ProverUI) page.getEditor();
				ITactic t = Tactics.mngHyp(ActionType.SELECT, selected);
				try {
					editor.getUserSupport()
							.applyTacticToHypotheses(t, selected);
				} catch (RodinDBException exception) {
					exception.printStackTrace();
				}

				TreeViewer viewer = editor.getProofTreeUI().getViewer();
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof IProofTreeNode) {
					IProofTreeNode proofTree = (IProofTreeNode) obj;
					editor.getProofTreeUI().refresh(proofTree);
					// Expand the node
					viewer.expandToLevel(proofTree,
							AbstractTreeViewer.ALL_LEVELS);
					ProofState ps = editor.getUserSupport().getCurrentPO();
					IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
					if (pt != null)
						editor.getProofTreeUI().getViewer().setSelection(
								new StructuredSelection(pt));
				}
			}

			else if (e.getLabel().equals("ds")) {
				Set<Hypothesis> deselected = new HashSet<Hypothesis>();
				for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
					HypothesisRow hr = it.next();
					if (hr.isSelected()) {
						deselected.add(hr.getHypothesis());
					}
				}
				if (deselected.isEmpty())
					return;
				ProverUI editor = (ProverUI) page.getEditor();
				editor.getUserSupport().removeSearchedHypotheses(deselected);
			}

		}

	}

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param page
	 *            The page that contain this section
	 * @param parent
	 *            the composite parent of the section
	 * @param style
	 *            style to create this section
	 */
	public SearchHypothesesSection(ProofsPage page, Composite parent, int style) {
		super(page, parent, style, SECTION_TITLE, SECTION_DESCRIPTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.SectionPart#expansionStateChanging(boolean)
	 */
	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
			gd.minimumHeight = 100;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		} else {
			GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesesSection#createTopFormText()
	 */
	@Override
	protected void createTopFormText(FormToolkit toolkit, Composite comp) {
		GridData gd;
		formText = new EventBFormText(toolkit.createFormText(
				comp, true));
		gd = new GridData();
		gd.widthHint = 50;
		gd.horizontalAlignment = SWT.LEFT;
		FormText ft = formText.getFormText();
		ft.setLayoutData(gd);
		ft.addHyperlinkListener(new CachedHyperlinkAdapter());
		String string = "<form><li style=\"text\" value=\"\" bindent=\"-20\"><a href=\"sl\">sl</a> <a href=\"ds\">ds</a></li></form>";
		ft.setText(string, true, false);
	}

}