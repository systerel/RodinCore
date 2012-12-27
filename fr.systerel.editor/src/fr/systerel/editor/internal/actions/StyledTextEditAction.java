/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.StyledText;

/**
 * Wrapper type for text widget edit actions.
 *
 */
public class StyledTextEditAction extends Action {

	private StyledText fTextWidget;
	private int fAction;

	public StyledTextEditAction(StyledText textWidget, int action) {
		fTextWidget= textWidget;
		fAction= action;
	}
	
	@Override
	public void run() {
		fTextWidget.invokeAction(fAction);
	}
	
}
