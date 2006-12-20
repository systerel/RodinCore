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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.HypothesisRow;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;

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

	private ImageHyperlink ds;

	private ImageHyperlink sl;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	private class SearchedHyperlinkAdapter extends HyperlinkAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkActivated(HyperlinkEvent e) {
			Widget widget = e.widget;
			if (widget.equals(sl)) {
				Set<Predicate> selected = new HashSet<Predicate>();
				for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
					HypothesisRow hr = it.next();
					if (hr.isSelected()) {
						selected.add(hr.getHypothesis());
					}
				}
				if (selected.isEmpty())
					return;

				ProverUI editor = (ProverUI) page.getEditor();
				ITactic t = Tactics.mngHyp(ProverFactory.makeSelectHypAction(selected));
				editor.getUserSupport().applyTacticToHypotheses(t, selected, new NullProgressMonitor());
				// TreeViewer viewer = editor.getProofTreeUI().getViewer();
				// ISelection selection = viewer.getSelection();
				// Object obj = ((IStructuredSelection) selection)
				// .getFirstElement();
				// if (obj instanceof IProofTreeNode) {
				// IProofTreeNode proofTree = (IProofTreeNode) obj;
				// editor.getProofTreeUI().refresh(proofTree);
				// // Expand the node
				// viewer.expandToLevel(proofTree,
				// AbstractTreeViewer.ALL_LEVELS);
				// ProofState ps = editor.getUserSupport().getCurrentPO();
				// IProofTreeNode pt = ps.getNextPendingSubgoal(proofTree);
				// if (pt != null)
				// editor.getProofTreeUI().getViewer().setSelection(
				// new StructuredSelection(pt));
				// }
			}

			else if (widget.equals(sl)) {
				Set<Predicate> deselected = new HashSet<Predicate>();
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

	protected void createTextClient(Section section, FormToolkit toolkit) {
		Composite composite = new Composite(section, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		toolkit.adapt(composite, true, true);
		composite.setBackground(section.getTitleBarGradientBackground());

		ds = new ImageHyperlink(composite, SWT.CENTER);
		toolkit.adapt(ds, true, true);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		ds.setImage(registry.get(IEventBSharedImages.IMG_PENDING));
		ds.addHyperlinkListener(new SearchedHyperlinkAdapter());
		ds.setBackground(section.getTitleBarGradientBackground());
		ds.setToolTipText("Deselect checked hypotheses");
		
		sl = new ImageHyperlink(composite, SWT.CENTER);
		toolkit.adapt(sl, true, true);
		sl.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		sl.addHyperlinkListener(new SearchedHyperlinkAdapter());
		sl.setBackground(section.getTitleBarGradientBackground());
		sl.setToolTipText("Select checked hypotheses");
		composite.pack();

		section.setTextClient(composite);
	}

	@Override
	protected void updateTextClientStatus(boolean enable) {
		ds.setEnabled(enable);
		sl.setEnabled(enable);
	}

}