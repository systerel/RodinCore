/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.navigator.contentProviders;

import org.eventb.core.IVariable;


/**
 * The content provider for Variable elements
 * @author Maria Husmann
 *
 */
public class VariableContentProvider extends ElementsContentProvider {
	public VariableContentProvider(){
		this_class = IVariable.class;
	}

}
