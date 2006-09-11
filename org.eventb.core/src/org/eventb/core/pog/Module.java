/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOHint;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.sc.IStateRepository;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class Module implements IModule {
	
	private static final String SEQ_HYP_NAME = "SEQHYP";
	private static final String PRD_NAME_PREFIX = "PRD";
	private static final String SRC_NAME_PREFIX = "SRC";
	private static final String HINT_NAME_PREFIX = "HINT";
	private static final String GOAL_NAME = "GOAL";
	
	public void createPO(
			IPOFile file, 
			String name,
			String desc,
			IIdentifierTable identifierTable,
			String globalHypothesis,
			List<POGPredicate> localHypothesis,
			POGPredicate goal,
			POGSource[] sources,
			POGHint[] hints,
			IProgressMonitor monitor) throws RodinDBException {
		
		IPOSequent sequent = 
			(IPOSequent) file.createInternalElement(
				IPOSequent.ELEMENT_TYPE, name, null, monitor);
		
		putTypeEnvironment(sequent, identifierTable, monitor);
		
		IPOPredicateSet hypothesis = 
			(IPOPredicateSet) sequent.createInternalElement(
					IPOPredicateSet.ELEMENT_TYPE, SEQ_HYP_NAME, null, monitor);
		
		hypothesis.setParentPredicateSet(globalHypothesis, monitor);
		
		putPOGPredicates(hypothesis, localHypothesis, monitor);
		
		putPOGPredicate(sequent, GOAL_NAME, goal, monitor);
		
		sequent.setDescription(desc, monitor);
		
		putPOGSources(sequent, sources, monitor);
		
		putPOGHints(sequent, hints, monitor);
	}
	
	private void putPOGHints(
			IPOSequent sequent, 
			POGHint[] hints, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (hints == null)
			return;
		
		for (int idx=0; idx < hints.length; idx++) {
			
			IPOHint hint =
				(IPOHint) sequent.createInternalElement(
						IPOHint.ELEMENT_TYPE, HINT_NAME_PREFIX + idx, null, monitor);
			hint.setValue(hints[idx].getValue(), monitor);

		}

	}
	
	private void putPOGSources(
			IPOSequent sequent, 
			POGSource[] sources, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (sources == null)
			return;
		
		for (int idx=0; idx < sources.length; idx++) {
			
			IPOSource source =
				(IPOSource) sequent.createInternalElement(
						IPOSource.ELEMENT_TYPE, SRC_NAME_PREFIX + idx, null, monitor);
			source.setSource(sources[idx].getSource().getSource(monitor), monitor);

		}

	}
	
	private void putPOGPredicates(
			IPOPredicateSet hypothesis, 
			List<POGPredicate> localHypothesis, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (localHypothesis == null)
			return;
		
		int index = 0;
		
		for (POGPredicate predicate : localHypothesis) {
			
			putPOGPredicate(hypothesis, PRD_NAME_PREFIX + index++, predicate, monitor);
		}

	}

	private void putPOGPredicate(
			IInternalElement element, 
			String name,
			POGPredicate predicate, 
			IProgressMonitor monitor) throws RodinDBException {
		IPOPredicate poPredicate = 
			(IPOPredicate) element.createInternalElement(
					IPOPredicate.ELEMENT_TYPE, name, null, monitor);
		poPredicate.setPredicate(predicate.getPredicate(), monitor);
		poPredicate.setSource(predicate.getSource(), monitor);
	}
	
	private void putTypeEnvironment(
			IPOSequent sequent, 
			IIdentifierTable identifierTable, 
			IProgressMonitor monitor)
		throws RodinDBException {
		
		identifierTable.save(sequent, monitor);

	}

	protected void initModules(
			IRodinElement element,
			IPOFile target,
			IModule[] modules,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IModule module : modules) {
			module.initModule(element, target, repository, monitor);
		}
	}
	
	protected void processModules(
			IModule[] modules, 
			IRodinElement element, 
			IPOFile target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IModule module : modules) {
			module.process(element, target, repository, monitor);
		}
	}
	
	protected void endModules(
			IRodinElement element,
			IPOFile target,
			IModule[] modules, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IModule module : modules) {
			module.endModule(element, target, repository, monitor);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void endModule(IRodinElement element, IPOFile target, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
	protected POGSource[] sources(POGSource... sources) {
		return sources;
	}
	
	protected POGPredicate[] hypotheses(POGPredicate... predicates) {
		return predicates;
	}

}
