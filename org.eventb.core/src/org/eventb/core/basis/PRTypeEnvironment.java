/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRIdentifier;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public class PRTypeEnvironment extends EventBProofElement implements IPRTypeEnvironment {

	public PRTypeEnvironment(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) throws RodinDBException {
		ITypeEnvironment typEnv = factory.makeTypeEnvironment();
		typEnv.addAll(getFreeIdents(factory));
		return typEnv;
	}
	


	public void setTypeEnvironment(ITypeEnvironment typeEnv, IProgressMonitor monitor) throws RodinDBException {
		
		// write out the type environment
		Set<String> names = typeEnv.getNames();
		
		for (String name : names) {			
			IPRIdentifier prIdent = (IPRIdentifier) getInternalElement(
					IPRIdentifier.ELEMENT_TYPE, name);
			prIdent.create(null,monitor);
			prIdent.setType(typeEnv.getType(name), monitor);			
		}
		
	}
	
}
