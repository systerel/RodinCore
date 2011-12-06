/*******************************************************************************
 * Copyright (c) 2011 Systerel and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;

public class ShowRuleDetails extends AbstractProofTreeAction {

	public ShowRuleDetails() {
		super(false);
	}

	@Override
	public void run(IAction action) {
		showView(RuleDetailsView.VIEW_ID);
	}
}
