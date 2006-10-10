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
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.HypothesesManagement.ActionType;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.HypothesisRow;

/**
 * @author htson
 *         <p>
 *         This class is an sub-class of Hypotheses Section to show the set of
 *         cache hypotheses in Prover UI editor.
 */
public class CacheHypothesesSection extends HypothesesSection {

	// Title and description
	private static final String SECTION_TITLE = "Cached Hypotheses";

	private static final String SECTION_DESCRIPTION = "The set of cached hypotheses";

	private ImageHyperlink ds;

	private ImageHyperlink sl;

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
			Widget widget = e.widget;
			if (widget.equals(sl)) {
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
				editor.getUserSupport().applyTacticToHypotheses(t, selected, new NullProgressMonitor());
			}

			else if (widget.equals(ds)) {
				Set<Hypothesis> deselected = new HashSet<Hypothesis>();
				for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
					HypothesisRow hr = it.next();
					if (hr.isSelected())
						deselected.add(hr.getHypothesis());
				}
				if (deselected.isEmpty())
					return;
				ProverUI editor = (ProverUI) page.getEditor();
				editor.getUserSupport().removeCachedHypotheses(deselected);
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
	public CacheHypothesesSection(ProofsPage page, Composite parent, int style) {
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
		ds.setImage(registry.get(EventBImage.IMG_PENDING));
		ds.addHyperlinkListener(new CachedHyperlinkAdapter());
		ds.setBackground(section.getTitleBarGradientBackground());
		ds.setToolTipText("Deselect checked hypotheses");
		
		sl = new ImageHyperlink(composite, SWT.CENTER);
		toolkit.adapt(sl, true, true);
		sl.setImage(registry.get(EventBImage.IMG_DISCHARGED));
		sl.addHyperlinkListener(new CachedHyperlinkAdapter());
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