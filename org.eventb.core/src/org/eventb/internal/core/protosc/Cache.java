/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.HashMap;
import java.util.HashSet;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;

/**
 * @author halstefa
 *
 */
public abstract class Cache<F extends IRodinFile> {
	
	protected final F file;
	
	private FormulaFactory factory;
	
	private ITypeEnvironment typeEnvironment;

	public Cache(F file) {
		factory = FormulaFactory.getDefault();
		typeEnvironment = factory.makeTypeEnvironment();
		this.file = file;
	}

	/**
	 * @return Returns the oldTypeEnvironment.
	 */
	public ITypeEnvironment getTypeEnvironment() {
		return typeEnvironment;
	}

	/**
	 * @param typeEnvironment The typeEnvironment to set.
	 */
	public void setTypeEnvironment(ITypeEnvironment typeEnvironment) {
		this.typeEnvironment = typeEnvironment;
	}

	protected void parseNames(IInternalElement[] elements, 
			HashMap<String, String> identMap, 
			HashSet<String> conflictSet,
			ISCProblemList problemList) {
		
		for(IInternalElement element : elements) {
			IParseResult result = factory.parseExpression(element.getElementName());
			if(result.isSuccess()) {
				Expression expr = result.getParsedExpression();
				if (expr instanceof FreeIdentifier) {
					
					FreeIdentifier identifier = (FreeIdentifier) expr;
					String name = element.getElementName();	
					
					// if(isValidName(element.getElementName())) {
					
					if(identifier.getName().equals(name)) {
						
						if(identMap.containsKey(name))
							conflictSet.add(name);
						else
							identMap.put(name, identifier.getName());
						
					} else {
						problemList.addProblem(element, "Invalid Identifier.", SCProblem.SEVERITY_ERROR);
					}
				} else {
					// TODO: use error message from parser ?
					problemList.addProblem(element, "Invalid Identifier.", SCProblem.SEVERITY_ERROR);
				}
			} else {
				problemList.addProblem(element, "Invalid Identifier (has leading or trailing spaces).", SCProblem.SEVERITY_ERROR);
			}
		}
	}
	
//	private boolean isValidName(String name) {
//		boolean frontOK = !(name.charAt(0) == ' ');
//		boolean tailOK = !(name.charAt(name.length() - 1) == ' ');
//		return frontOK && tailOK;
//	}

	/**
	 * @return Returns the factory.
	 */
	public FormulaFactory getFactory() {
		return factory;
	}

}
