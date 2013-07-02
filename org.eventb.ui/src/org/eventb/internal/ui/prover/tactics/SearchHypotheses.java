/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added input string passing to SearchHypothesis view
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IViewPart;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesis;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;
import org.rodinp.keyboard.ui.RodinKeyboardUIPlugin;

public class SearchHypotheses implements IProofCommand {

	@Override
	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		// TODO should have been fixed at the root (the text widget) rather than here
		final String input = RodinKeyboardUIPlugin.getDefault().translate(
				inputs[0]);
		final String searched = input.replaceAll("\r", "").replaceAll("\n", "");
		us.searchHyps(searched);
		// Trying to show the Search View
		final IViewPart view = UIUtils.showView(SearchHypothesis.VIEW_ID);
		if (view instanceof SearchHypothesis){
			((SearchHypothesis) view).setSearchedHyp(searched);
		}
	}

	@Override
	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		return (us.getCurrentPO() != null && us.getCurrentPO().getCurrentNode() != null);
	}

}
