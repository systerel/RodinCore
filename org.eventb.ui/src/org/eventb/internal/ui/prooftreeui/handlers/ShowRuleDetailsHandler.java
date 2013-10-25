/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import static org.eventb.internal.ui.UIUtils.showView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eventb.internal.ui.prooftreeui.RuleDetailsView;

/**
 * Handler class for the "org.eventb.ui.showRuleDetails" command.
 */
public class ShowRuleDetailsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		showView(RuleDetailsView.VIEW_ID);
		return null;
	}

}
