/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRPredicate;
import org.eventb.core.IPair;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */

public class PRPredicate extends InternalElement implements IPRPredicate {

	public PRPredicate(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	

	public Predicate getPredicate(FormulaFactory factory, ITypeEnvironment typEnv1, IProgressMonitor monitor) throws RodinDBException {
		// read in the type environment
		
		// TODO : refactor code with PRTypeEnv
		ITypeEnvironment typEnv = FormulaFactory.getDefault().makeTypeEnvironment();
		// this.getChildrenOfType(IPair.ELEMENT_TYPE);
		IRodinElement[] pairs = this.getChildrenOfType(IPair.ELEMENT_TYPE);
		for (IRodinElement pair : pairs) {
			Type type = ASTLib.parseType(((IPair)pair).getContents());
			assert type != null;
			typEnv.addName(pair.getElementName(),type);
		}
		Predicate pred = ASTLib.parsePredicate(this.getContents());
		assert pred != null;
		// attn : wellTyped does type checking!
		boolean wellTyped = ASTLib.typeCheckClosed(pred,typEnv);
		assert wellTyped;
		return pred;
	}

	public void setPredicate(Predicate p, IProgressMonitor monitor) throws RodinDBException {
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the type environment
		FreeIdentifier[] freeIdents = p.getFreeIdentifiers();
		// ITypeEnvironment typEnv = FormulaFactory.getDefault().makeTypeEnvironment();
		for (FreeIdentifier identifier : freeIdents) {
			this.createInternalElement(IPair.ELEMENT_TYPE,identifier.getName(),null,null)
			.setContents(identifier.getType().toString());
			// typEnv.addName(identifier.getName(),identifier.getType().toString());
		}
		// write out the predicate
		this.setContents(p.toStringWithTypes());
		return;
	}

}
