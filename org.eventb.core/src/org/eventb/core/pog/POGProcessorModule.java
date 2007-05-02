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
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModule;
import org.eventb.core.tool.IProcessorModule;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author Stefan Hallerstede
 *
 */
//TODO javadoc
public abstract class POGProcessorModule extends POGModule implements IPOGProcessorModule {
	
	@Override
	protected final IFilterModule[] getFilterModules() {
		return super.getFilterModules();
	}

	@Override
	protected final IProcessorModule[] getProcessorModules() {
		return super.getProcessorModules();
	}

	private static final String SEQ_HYP_NAME = "SEQHYP";
	private static final String PRD_NAME_PREFIX = "PRD";
	private static final String SRC_NAME_PREFIX = "SRC";
	private static final String HINT_NAME_PREFIX = "HINT";
	private static final String GOAL_NAME = "GOAL";
	
	protected final void createPO(
			IPOFile file, 
			String name,
			String desc,
			IPOPredicateSet globalHypothesis,
			List<IPOGPredicate> localHypothesis,
			IPOGPredicate goal,
			IPOGSource[] sources,
			IPOGHint[] hints,
			IProgressMonitor monitor) throws RodinDBException {
		
		IPOSequent sequent = file.getSequent(name);
		sequent.create(null, monitor);
		
		IPOPredicateSet hypothesis = sequent.getHypothesis(SEQ_HYP_NAME);
		hypothesis.create(null, monitor);
		hypothesis.setParentPredicateSet(globalHypothesis, monitor);
		
		putPOGPredicates(hypothesis, localHypothesis, monitor);
		
		IPOPredicate goalPredicate = sequent.getGoal(GOAL_NAME);
		putPredicate(goalPredicate, goal, monitor);
		
		sequent.setDescription(desc, monitor);
		
		putPOGSources(sequent, sources, monitor);
		
		putPOGHints(sequent, hints, monitor);
	}

	private void putPredicate(
			IPOPredicate predicate, 
			IPOGPredicate pogPredicate, 
			IProgressMonitor monitor) throws RodinDBException {
		predicate.create(null, monitor);
		predicate.setPredicate(pogPredicate.getPredicate(), monitor);
		predicate.setSource(pogPredicate.getSource(), monitor);
	}
	
	private void putPOGHints(
			IPOSequent sequent, 
			IPOGHint[] hints, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (hints == null)
			return;
		
		for (int idx=0; idx < hints.length; idx++) {
			
			hints[idx].create(sequent, HINT_NAME_PREFIX + idx, monitor);

		}

	}
	
	private void putPOGSources(
			IPOSequent sequent, 
			IPOGSource[] sources, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (sources == null)
			return;
		
		for (int idx=0; idx < sources.length; idx++) {
			
			IPOSource source = sequent.getSource(SRC_NAME_PREFIX + idx);
			source.create(null, monitor);
			source.setSource(sources[idx].getSource(), monitor);
			source.setRole(sources[idx].getRole(), monitor);
		}

	}
	
	private void putPOGPredicates(
			IPOPredicateSet hypothesis, 
			List<IPOGPredicate> localHypothesis, 
			IProgressMonitor monitor) throws RodinDBException {
		
		if (localHypothesis == null)
			return;
		
		int index = 0;
		
		for (IPOGPredicate predicate : localHypothesis) {
			
			IPOPredicate poPredicate = hypothesis.getPredicate(PRD_NAME_PREFIX + index++);
			putPredicate(poPredicate, predicate, monitor);
		}

	}

	private static final String PROCESSOR = "PROCESSOR";
//	private static final String FILTER = "FILTER";
	private static final String INI = "INI";
	private static final String RUN = "RUN";
	private static final String END = "END";

	private <M extends IModule> void traceModule(M module, String op, String kind) {
		System.out.println("POG MOD" + op + ": " + module.getModuleType() + " " + kind);
	}

	protected final void initModules(
			IRodinElement element,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, INI, PROCESSOR);
			IPOGProcessorModule pogModule = (IPOGProcessorModule) module;
			pogModule.initModule(element, repository, monitor);
		}
	}
	
	protected final void processModules(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, PROCESSOR);
			IPOGProcessorModule pogModule = (IPOGProcessorModule) module;
			pogModule.process(element, repository, monitor);
		}
	}
	
	protected final void endModules(
			IRodinElement element,
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, END, PROCESSOR);
			IPOGProcessorModule pogModule = (IPOGProcessorModule) module;
			pogModule.endModule(element, repository, monitor);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {

			// nothing to do
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {

		// nothing to do
		
	}
	
	/**
	 * Creates a POG predicate with an associated source element reference to be stored in a PO file.
	 * 
	 * @param predicate a predicate
	 * @param source an associated source element
	 * 
	 * @see IPOPredicate
	 */
	protected static final IPOGPredicate makePredicate(final Predicate predicate, final IRodinElement source) {
		return new POGPredicate(predicate, source);
	}
	
	/**
	 * Creates a POG source with an associated source element reference to be stored in a PO file.
	 * 
	 * @param role the role of the source
	 * @param source the actual source element
	 * 
	 * @see IPOSource
	 */
	protected static final IPOGSource makeSource(final String role, final IRodinElement source) 
	throws RodinDBException {
		return new POGSource(role, source);
	}

	/**
	 * Creates a predicate selection hint for <code>predicate</code>.
	 * 
	 * @param predicate the predicate to select
	 */
	protected static final IPOGHint makePredicateSelectionHint(final IPOPredicate predicate) {
		return new POGPredicateSelectionHint(predicate);
	}
	
	/**
	 * Creates an interval selection hint.
	 * 
	 * @param start the predicate set immediately preceding the interval 
	 * @param end the last predicate set in the interval
	 */
	protected static final IPOGHint makeIntervalSelectionHint(
			final IPOPredicateSet start, final IPOPredicateSet end) {
		return new POGIntervalSelectionHint(start, end);
	}

	protected static final IPOPredicateSet getSequentHypothesis(IPOFile file, String sequentName) 
	throws RodinDBException {
		return file.getSequent(sequentName).getHypothesis(SEQ_HYP_NAME);
	}

	protected static final IPOGSource[] sources(IPOGSource... sources) {
		return sources;
	}
	
	protected static final IPOGHint[] hints(IPOGHint... hints) {
		return hints;
	}

}
