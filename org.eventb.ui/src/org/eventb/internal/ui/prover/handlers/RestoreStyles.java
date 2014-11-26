/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getActiveEditorChecked;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.prover.ProverUI;

/**
 * Handler able to restore the hypotheses and goal styledTexts in a state
 * without any highlighting.
 * 
 * @author "Thomas Muller"
 */
public class RestoreStyles extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart activeEditor = getActiveEditorChecked(event);
		if (activeEditor instanceof ProverUI) {
			final ProverUI pu = ((ProverUI) activeEditor);
			pu.getHighlighter().removeHightlight(true);
		}
		return null;
	}

}
