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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.pm.ProofState;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.HypothesisRow;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         This is the based class for creating different hypothesis sections
 *         (cached, searched, selected).
 */
public abstract class HypothesesSection extends SectionPart {

	// The page contains the section.
	protected ProofsPage page;

	private Composite comp;

	private ScrolledForm scrolledForm;

	// Title and description
	private String title;

	private String description;

	protected Collection<HypothesisRow> rows;

	protected IEventBFormText formText;

	// private boolean compact;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param page
	 *            page contains the section
	 * @param parent
	 *            composite parent of the section
	 * @param style
	 *            style to creat the section
	 * @param title
	 *            title of the section
	 * @param description
	 *            description of the section
	 */
	public HypothesesSection(ProofsPage page, Composite parent, int style,
			String title, String description) {
		super(parent, page.getManagedForm().getToolkit(), style);
		// compact = (style & Section.COMPACT) != 0 ? true : false;
		this.page = page;
		this.title = title;
		this.description = description;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
		rows = new HashSet<HypothesisRow>();
	}

	/**
	 * Create the client of the section.
	 * <p>
	 * 
	 * @param section
	 *            the section
	 * @param toolkit
	 *            the FormToolkit used to create the client.
	 */
	public void createClient(Section section, FormToolkit toolkit) {
		section.setText(title);
		section.setDescription(description);

		scrolledForm = toolkit.createScrolledForm(section);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		comp.setLayout(layout);

		section.setClient(scrolledForm);

		createTextClient(section, toolkit);
		updateTextClientStatus();
	}

	private void updateTextClientStatus() {
		ProofState ps = ((ProverUI) page.getEditor()).getUserSupport()
				.getCurrentPO();

		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null && node.isOpen())
				enable = true;
		}

		updateTextClientStatus(enable);
	}

	protected abstract void updateTextClientStatus(boolean enable);

	protected abstract void createTextClient(Section section,
			FormToolkit toolkit);

	public void init(Collection<Hypothesis> hyps, boolean enable) {
		// Remove everything
		for (HypothesisRow row : rows) {
			row.dispose();
		}
		rows.clear();

		// Add new hyps
		int i = 0;
		for (Hypothesis hyp : hyps) {
			UIUtils.debugEventBEditor("Add to " + this.title + " hyp: "
					+ hyp.getPredicate());
			HypothesisRow row = new HypothesisRow(this.getManagedForm().getToolkit(), comp, hyp,
					((ProverUI) page.getEditor()).getUserSupport(),
					(i % 2) == 0, enable);
			rows.add(row);
			i++;
		}

		updateTextClientStatus();
		scrolledForm.reflow(true);
	}

	@Override
	public void dispose() {
		if (formText != null)
			formText.dispose();
		super.dispose();
	}

	// protected void expansionStateChanged(boolean expanding) {
	// if (expanding)
	// compact = false;
	// else
	// compact = true;
	// page.layout();
	// }

	// public boolean isCompact() {
	// return compact;
	// }

	public ScrolledForm getScrolledForm() {
		return scrolledForm;
	}

	public Collection<HypothesisRow> getRows() {
		return rows;
	}
}