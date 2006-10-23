/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPRExpression;
import org.eventb.core.IPair;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */

public class PRExpression extends InternalElement implements IPRExpression {

	public PRExpression(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
//	public String getName() {
//		return getElementName();
//	}

	public Expression getExpression() throws RodinDBException {
		// read in the type environment
		
		// TODO : refactor code with PRTypeEnv
		ITypeEnvironment typEnv = FormulaFactory.getDefault().makeTypeEnvironment();
		// this.getChildrenOfType(IPair.ELEMENT_TYPE);
		for (IRodinElement pair : this.getChildrenOfType(IPair.ELEMENT_TYPE)) {
			Type type = ASTLib.parseType(((IPair)pair).getContents());
			assert type != null;
			typEnv.addName(pair.getElementName(),type);
		}
		Expression expr = ASTLib.parseExpression(this.getContents());
		assert expr != null;
		// attn : wellTyped does type checking!
		boolean wellTyped = ASTLib.typeCheckClosed(expr,typEnv);
		assert wellTyped;
		return expr;
	}

	public void setExpression(Expression e) throws RodinDBException {
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		
		// write out the type environment
		FreeIdentifier[] freeIdents = e.getFreeIdentifiers();
		// ITypeEnvironment typEnv = FormulaFactory.getDefault().makeTypeEnvironment();
		for (FreeIdentifier identifier : freeIdents) {
			this.createInternalElement(IPair.ELEMENT_TYPE,identifier.getName(),null,null)
			.setContents(identifier.getType().toString());
			// typEnv.addName(identifier.getName(),identifier.getType().toString());
		}
		// write out the expression
		this.setContents(e.toStringWithTypes());
		return;
	}


}
