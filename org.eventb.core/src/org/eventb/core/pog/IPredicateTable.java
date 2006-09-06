/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import java.util.List;

import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.IState;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IPredicateTable extends IState {

	List<ISCPredicateElement> getElements();
	
	List<Predicate> getPredicates();
	
	void addElement(
			ISCPredicateElement element, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws RodinDBException;
	
	void trim();
	
}
