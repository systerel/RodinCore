/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.internal.ui.prooftreeui.services.IProofRuleSelectionListener;
import org.eventb.internal.ui.prooftreeui.services.ProofRuleSelectionService;
import org.eventb.ui.EventBUIPlugin;

/**
 * The rule details view provides information about the rule which was applied
 * on a given proof tree node.
 * 
 * @author "Thomas Muller"
 */
public class RuleDetailsView extends AbstractProofNodeView implements IProofRuleSelectionListener {

	/**
	 * The identifier of the Rule Details View (value
	 * <code>"org.eventb.ui.views.RuleDetails"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.RuleDetails";

	public static final int ZERO = 0;

	private ScrolledComposite sc = null;
	private RuleDetailsProvider rdp = null;

	@Override
	protected void initializeControl(final Composite parent, Font font) {
		final GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = ZERO;
		gl.marginWidth = ZERO;
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayout(gl);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		rdp = new RuleDetailsProvider(sc, font);
		
		final ProofRuleSelectionService ruleSelService = ProofRuleSelectionService.getInstance();
		ruleSelService.addListener(this);
		ruleChanged(ruleSelService.getCurrent());
	}

	@Override
	public void ruleChanged(IProofRule rule) {
		if (isDisposed()) {
			return;
		}
		if (rule == null) {
			sc.setVisible(false);
			return;
		}
		sc.setVisible(true);
		sc.setRedraw(false);
		final Control details = rdp.getRuleDetailsPresentation(rule);
		sc.setContent(details);
		sc.setMinSize(details.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setRedraw(true);
	}
	
	@Override
	protected void fontChanged(Font font) {
		rdp.setFont(font);
	}
	
	@Override
	public void dispose() {
		sc.dispose();
		super.dispose();
	}

}
