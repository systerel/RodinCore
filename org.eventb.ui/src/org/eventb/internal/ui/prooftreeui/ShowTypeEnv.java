/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;

/**
 * Action that shows the type environment view.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ShowTypeEnv extends AbstractProofTreeAction {

	public ShowTypeEnv() {
		super(true);
	}

	@Override
	public void run(IAction action) {
		showView(TypeEnvView.VIEW_ID);
	}

	@Override
	protected boolean isEnabled(IAction action) {
		// do not take open/close node condition into account
		return true;
	}

}
