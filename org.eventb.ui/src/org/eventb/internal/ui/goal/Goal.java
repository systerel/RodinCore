/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.goal;

import org.eclipse.ui.IWorkbenchPart;
import org.eventb.internal.ui.prover.ProverContentOutline;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         Implementation of the Goal View.
 */
public class Goal extends ProverContentOutline {

	/**
	 * The identifier of the Cache Hypothesis View (value
	 * <code>"org.eventb.ui.views.Goal"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.Goal";

	public Goal() {
		super("Goal is not available");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a Search Hypothesis Page.
		Object obj = part.getAdapter(IGoalPage.class);
		if (obj instanceof IGoalPage) {
			final IGoalPage page = (IGoalPage) obj;
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		// There is no content outline
		return null;
	}

}