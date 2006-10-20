/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.Module;
import org.eventb.core.pog.POGHint;
import org.eventb.core.pog.POGPredicate;
import org.eventb.core.sc.IStateRepository;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class UtilityModule extends Module {

	protected Predicate btrue;
	protected List<POGPredicate> emptyPredicates;
	protected POGHint[] emptyHints;
	protected FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		
		factory = repository.getFormulaFactory();
		btrue = factory.makeLiteralPredicate(Formula.BTRUE, null);
		emptyPredicates = new ArrayList<POGPredicate>(0);
		emptyHints = new POGHint[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		factory = null;
		btrue = null;
		emptyPredicates = null;
		emptyHints = null;
		super.endModule(element, target, repository, monitor);
	}

}
