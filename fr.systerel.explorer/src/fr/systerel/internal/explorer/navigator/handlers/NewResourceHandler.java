/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import org.eclipse.ui.INewWizard;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;

public class NewResourceHandler {

	private NewResourceHandler() {
		// no instance
	}

	public static class NewComponentHandler extends AbstractNewComponentHandler {
		@Override
		protected INewWizard createWizard() {
			return new NewComponentWizard();
		}
	}

	public static class NewProjectHandler extends AbstractNewComponentHandler {
		@Override
		protected INewWizard createWizard() {
			return new NewProjectWizard();
		}
	}

}
