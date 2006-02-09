/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.protopog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class Cache<F extends IRodinFile> {
	
	protected final F file;
	
	private final FormulaFactory factory;
	
	protected final Predicate BTRUE;
	protected final Predicate BFALSE;

	public Cache(F file) {
		this.file = file;
		this.factory = FormulaFactory.getDefault();
		BTRUE = factory.makeLiteralPredicate(Formula.BTRUE, null);
		BFALSE = factory.makeLiteralPredicate(Formula.BFALSE, null);
	}

	/**
	 * @return Returns the formula factory.
	 */
	public FormulaFactory getFactory() {
		return factory;
	}
	
	protected ITypeEnvironment getTypeEnvironment(IInternalElement[] identifiers, IProgressMonitor monitor) throws RodinDBException {
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		for(IInternalElement identifier : identifiers) {
			String name = identifier.getElementName();
			String type = identifier.getContents(monitor);
			IParseResult result = factory.parseType(type);
			assert result.isSuccess();
			typeEnvironment.addName(name, result.getParsedType());
		}
		return typeEnvironment;
	}

}
