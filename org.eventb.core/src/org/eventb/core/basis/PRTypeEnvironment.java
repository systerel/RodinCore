/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.IPair;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.Lib;
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
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	public ITypeEnvironment getTypeEnvironment() throws RodinDBException {
		ITypeEnvironment typEnv = FormulaFactory.getDefault().makeTypeEnvironment();
		for (IRodinElement pair : this.getChildrenOfType(IPair.ELEMENT_TYPE)) {
			Type type = Lib.parseType(((IPair)pair).getContents());
			assert type != null;
			typEnv.addName(pair.getElementName(),type);
		}
		return typEnv;
	}
	
	public FreeIdentifier[] getFreeIdentifiers() throws RodinDBException {
		IRodinElement[] children = this.getChildrenOfType(IPair.ELEMENT_TYPE);
		FreeIdentifier[] freeIdents = new FreeIdentifier[children.length];
		for (int i = 0; i < freeIdents.length; i++) {
			String name = ((IPair)children[i]).getElementName();
			Type type = Lib.parseType(((IPair)children[i]).getContents());
			assert type != null;
			freeIdents[i] = FormulaFactory.getDefault().makeFreeIdentifier(name,null,type);
			assert freeIdents[i].isTypeChecked();
		}
		return freeIdents;
	}

	public void setTypeEnvironment(ITypeEnvironment typeEnv) throws RodinDBException {
		//	delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the type environment
		Set<String> names = typeEnv.getNames();
		
		for (String name : names) {
			this.createInternalElement(IPair.ELEMENT_TYPE,name,null,null)
			.setContents(typeEnv.getType(name).toString());
		}
		
	}
	
	public void setTypeEnvironment(FreeIdentifier[] freeIdents) throws RodinDBException {
		//	delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		for (int i = 0; i < freeIdents.length; i++) {
			this.createInternalElement(
					IPair.ELEMENT_TYPE,
					freeIdents[i].getName(),
					null,null)
			.setContents(freeIdents[i].getType().toString());
		}
	}

}
