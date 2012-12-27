/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCGuard;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventGuardTable extends PredicateTable<ISCGuard> {

	public EventGuardTable(
			ISCGuard[] guards, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws RodinDBException {
		super(guards, typeEnvironment, factory);
		
	}
	
}
