/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelMachine;

/**
 * The content provider for Variable elements
 */
public class VariableContentProvider extends AbstractContentProvider {

	public VariableContentProvider() {
		super(IVariable.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {
		// there is no ModelElement for variables.
		if (element instanceof IVariable) {
			IVariable var = (IVariable) element;
			IMachineRoot mach = (IMachineRoot) var.getRoot();
			ModelMachine machine = ModelController.getMachine(mach);
			if (machine != null) {
				return machine.variable_node;
			}
		}
		if (element instanceof IElementNode) {
			return ((IElementNode) element).getParent();
		}
		return null;
	}

}
