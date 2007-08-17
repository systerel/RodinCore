/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements.terms;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Sort;

/**
 * This class is used to get new instances of {@link Variable},
 * {@link Constant}, {@link LocalVariable} and {@link IntegerConstant}. It
 * plays the role of a factory for all subclasses of {@link SimpleTerm}. 
 *
 * @author François Terrier
 *
 */
public class VariableContext implements IVariableContext {

	private int currentLocalVariableID = 0;
	private int currentGlobalVariableID = 0;
	private int currentConstantID = 0;
	
	public VariableContext() {
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.elements.IVariableContext#getNextVariable(org.eventb.internal.pp.core.elements.Sort)
	 */
	public Variable getNextVariable(Sort sort) {
		assert sort != null;
		return new Variable(currentGlobalVariableID++,sort);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.IVariableContext#getNextLocalVariable(boolean, org.eventb.internal.pp.core.elements.Sort)
	 */
	public LocalVariable getNextLocalVariable(boolean isForall, Sort sort) {
		assert sort != null;
		return new LocalVariable(currentLocalVariableID++, isForall, sort);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.IVariableContext#getNextFreshConstant(org.eventb.internal.pp.core.elements.Sort)
	 */
	public Constant getNextFreshConstant(Sort sort) {
		assert sort != null;
		return new Constant(Integer.toString(currentConstantID++),sort);
	}
	
}
