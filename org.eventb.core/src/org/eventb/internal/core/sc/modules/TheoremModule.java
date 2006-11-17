/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class TheoremModule extends PredicateModule {

	private static String THEOREM_NAME_PREFIX = "THM";
	
	protected void checkAndSaveTheorems(
			IInternalParent target, 
			int offset,
			ITheorem[] theorems, 
			IFilterModule[] rules,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor) throws CoreException {
		
		Predicate[] predicates = new Predicate[theorems.length];

		checkAndType(
				theorems, 
				target,
				predicates,
				rules,
				target.getElementName(),
				repository,
				monitor);
		
		saveTheorems(target, offset, theorems, predicates, null);
	}
	
	protected abstract ISCTheorem getSCTheorem(IInternalParent target, String elementName);
	
	private void saveTheorems(
			IInternalParent parent, 
			int offset,
			ITheorem[] theorems, 
			Predicate[] predicates,
			IProgressMonitor monitor) throws RodinDBException {
		
		int index = offset;
		
		for (int i=0; i<theorems.length; i++) {
			if (predicates[i] == null)
				continue;
			ISCTheorem scTheorem = getSCTheorem(parent, THEOREM_NAME_PREFIX + index++);
			scTheorem.create(null, monitor);
			scTheorem.setLabel(theorems[i].getLabel(monitor), monitor);
			scTheorem.setPredicate(predicates[i], null);
			scTheorem.setSource(theorems[i], monitor);
		}
	}
	
}
