/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.refine;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.rodinp.core.IInternalElement;

/**
 * @author Nicolas Beauger
 * 
 */
public class RefineHandler extends AbstractHandler {

	private IInternalElement currentRoot;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (currentRoot == null) {
			throw new IllegalStateException(
					"I have no clue which component to refine !");
		}
		
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		final EvaluationContext eval = (EvaluationContext) evaluationContext;
		final Object defaultVariable = eval.getDefaultVariable();
		final List<?> selection = (List<?>) defaultVariable;
		final boolean enabled = computeEnablement(selection);

		if (enabled) {
			this.currentRoot = (IInternalElement) selection.get(0);
		} else {
			this.currentRoot = null;
		}
		setBaseEnabled(enabled);
		// FIXME set popup menu label, (use dynamic menu contribution ?)
	}

	private boolean computeEnablement(List<?> selection) {
		if (selection.size() != 1)
			return false;
		final Object x = selection.get(0);
		if (!(x instanceof IInternalElement))
			return false;
		final IInternalElement e = (IInternalElement) x;
		return RefinementUIRegistry.getDefault().hasRefinement(
				e.getElementType());
	}

}
