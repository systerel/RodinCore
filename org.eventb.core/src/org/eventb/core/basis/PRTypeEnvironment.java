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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRTypeEnvironment extends InternalElement implements IPRTypeEnvironment {

	public PRTypeEnvironment(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] children = this.getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
		ITypeEnvironment typEnv = factory.makeTypeEnvironment();
		for (int i = 0; i < children.length; i++) {
			IPRIdentifier prIdent = (IPRIdentifier) children[i];
			typEnv.addName(prIdent.getElementName(),prIdent.getType(factory, monitor));
		}
		return typEnv;
	}
	
	public FreeIdentifier[] getFreeIdentifiers(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] children = this.getChildrenOfType(IPRIdentifier.ELEMENT_TYPE);
		FreeIdentifier[] freeIdents = new FreeIdentifier[children.length];
		for (int i = 0; i < freeIdents.length; i++) {
			freeIdents[i] = ((IPRIdentifier)children[i]).getIdentifier(factory, monitor);			
		}
		return freeIdents;
	}

	public void setTypeEnvironment(ITypeEnvironment typeEnv, IProgressMonitor monitor) throws RodinDBException {
		//	delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the type environment
		Set<String> names = typeEnv.getNames();
		
		for (String name : names) {			
			IPRIdentifier prIdent = (IPRIdentifier) 
			this.createInternalElement(
					IPRIdentifier.ELEMENT_TYPE,
					name,
					null,monitor);
			prIdent.setType(typeEnv.getType(name), monitor);			
		}
		
	}
	
	public void setTypeEnvironment(FreeIdentifier[] freeIdents, IProgressMonitor monitor) throws RodinDBException {
		//	delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		for (int i = 0; i < freeIdents.length; i++) {
			IPRIdentifier prIdent = (IPRIdentifier) 
			this.createInternalElement(
					IPRIdentifier.ELEMENT_TYPE,
					freeIdents[i].getName(),
					null,monitor);
			prIdent.setType(freeIdents[i].getType(), monitor);
		}
	}

}
