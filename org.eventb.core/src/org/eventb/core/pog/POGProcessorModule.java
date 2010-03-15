/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.core.pog;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.pog.POGNatureFactory;
import org.eventb.internal.core.tool.types.IFilterModule;
import org.eventb.internal.core.tool.types.IModule;
import org.eventb.internal.core.tool.types.IPOGFilterModule;
import org.eventb.internal.core.tool.types.IPOGProcessorModule;
import org.eventb.internal.core.tool.types.IProcessorModule;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Default implementation of a proof obligation generator processor module. 
 * 
 * @see IPOGProcessorModule
 * @see POGModule
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
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
	
	/**
	 * Create a proof obligation in the specified file.
	 * 
	 * @param target
	 *            the target file
	 * @param name
	 *            the name of the proof obligation
	 * @param desc
	 *            a description of the proof obligation
	 * @param globalHypotheses
	 *            the global hypotheses (shared between proof obligations)
	 * @param localHypotheses
	 *            the local hypotheses (<b>not</b> shared between proof
	 *            obligations)
	 * @param goal
	 *            the goal to be proved
	 * @param sources
	 *            references to source elements from which the proof obligation
	 *            was derived
	 * @param hints
	 *            hints for a theorem prover
	 * @param accurate
	 *            the accuracy of the PO sequent
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws CoreException
	 *             if there has been any problem creating the proof obligation
	 * @deprecated use the method with a IPOGNature instead
	 */
	@Deprecated
	protected final void createPO(
			IPORoot target, 
			String name,
			String desc,
			IPOPredicateSet globalHypotheses,
			List<IPOGPredicate> localHypotheses,
			IPOGPredicate goal,
			IPOGSource[] sources,
			IPOGHint[] hints,
			boolean accurate,
			IProgressMonitor monitor) throws CoreException {
		createPO(target, name, makeNature(desc), globalHypotheses,
				localHypotheses, goal, sources, hints, accurate, monitor);
	}

	/**
	 * Create a proof obligation in the specified file.
	 * 
	 * @param target
	 *            the target file
	 * @param name
	 *            the name of the proof obligation
	 * @param nature
	 *            the nature of the proof obligation
	 * @param globalHypotheses
	 *            the global hypotheses (shared between proof obligations)
	 * @param localHypotheses
	 *            the local hypotheses (<b>not</b> shared between proof
	 *            obligations)
	 * @param goal
	 *            the goal to be proved
	 * @param sources
	 *            references to source elements from which the proof obligation
	 *            was derived
	 * @param hints
	 *            hints for a theorem prover
	 * @param accurate
	 *            the accuracy of the PO sequent
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws CoreException
	 *             if there has been any problem creating the proof obligation
	 * @since 1.3
	 */
	protected final void createPO(
			IPORoot target, 
			String name,
			IPOGNature nature,
			IPOPredicateSet globalHypotheses,
			List<IPOGPredicate> localHypotheses,
			IPOGPredicate goal,
			IPOGSource[] sources,
			IPOGHint[] hints,
			boolean accurate,
			IProgressMonitor monitor) throws CoreException {
		
		if (acceptPO(name, monitor)) {

			IPOSequent sequent = target.getSequent(name);
			sequent.create(null, monitor);

			IPOPredicateSet hypothesis = sequent.getHypothesis(SEQ_HYP_NAME);
			hypothesis.create(null, monitor);
			hypothesis.setParentPredicateSet(globalHypotheses, monitor);

			putPOGPredicates(hypothesis, localHypotheses, monitor);

			IPOPredicate goalPredicate = sequent.getGoal(GOAL_NAME);
			putPredicate(goalPredicate, goal, monitor);

			sequent.setPOGNature(nature, monitor);

			putPOGSources(sequent, sources, monitor);

			putPOGHints(sequent, hints, monitor);

			sequent.setAccuracy(accurate, monitor);

		}
	}

	private boolean acceptPO(final String name, final IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, FILTER);
			IPOGFilterModule pogModule = (IPOGFilterModule) module;
			if (pogModule.accept(name, monitor))
				continue;
			else
				return false;
		}
		return true;
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
	private static final String FILTER = "FILTER";
	private static final String INI = "INI";
	private static final String RUN = "RUN";
	private static final String END = "END";

	private <M extends IModule> void traceModule(M module, String op, String kind) {
		System.out.println("POG MOD" + op + ": " + module.getModuleType() + " " + kind);
	}

	/**
	 * Initialise filter modules in the order returned by
	 * <code>getFilterModules()</code>.
	 * 
	 * @param repository
	 *            the state repository to pass to all modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during the initialisation of one of
	 *             the modules
	 */
	private final void initFilterModules(
			POGProcessorModule pogModule,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : pogModule.getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, INI, FILTER);
			IPOGFilterModule pogFModule = (IPOGFilterModule) module;
			pogFModule.initModule(repository, monitor);
		}
	}

	private final void endFilterModules(
			POGProcessorModule pogModule,
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		for (IFilterModule module : pogModule.getFilterModules()) {
			if (DEBUG_MODULE)
				traceModule(module, END, FILTER);
			IPOGFilterModule pogFModule = (IPOGFilterModule) module;
			pogFModule.endModule(repository, monitor);
		}
	}
	
	/**
	 * Initialise processor modules in the order returned by
	 * <code>getProcessorModules()</code>.
	 * 
	 * @param repository
	 *            the state repository to pass to all processor modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during the initialisation of one of
	 *             the modules
	 */
	protected final void initProcessorModules(
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
	
	/**
	 * Process an element using the child processor modules in the order
	 * returned by <code>getProcessorModules()</code>.
	 * 
	 * @param element
	 *            the element to process
	 * @param repository
	 *            the state repository to pass to all processor modules
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException
	 *             if there was a problem during processing
	 */
	protected final void processModules(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		for (IProcessorModule module : getProcessorModules()) {
			if (DEBUG_MODULE)
				traceModule(module, RUN, PROCESSOR);
			POGProcessorModule pogModule = (POGProcessorModule) module;
			
			initFilterModules(pogModule, repository, monitor);
			pogModule.process(element, repository, monitor);
			endFilterModules(pogModule, repository, monitor);
		}
	}
	
	protected final void endProcessorModules(
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

	/**
	 * Each proof obligation has its own sequent hypothesis. This is an {@link IPOPredicateSet}
	 * associated with that sequent. There is only one per sequent.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param target the target proof obligation file
	 * @param sequentName the name of the sequent
	 * @return a handle to the predicate set that represents the hypothesis
	 */
	protected static final IPOPredicateSet getSequentHypothesis(IPORoot target, String sequentName) {
		return target.getSequent(sequentName).getHypothesis(SEQ_HYP_NAME);
	}

	/**
	 * Creates a POG Nature for <code>description</code>.
	 * 
	 * @param description
	 *            the description of the nature
	 * @return the unique POG Nature corresponding to the given description
	 * @see IPOGNature
	 * @since 1.3
	 */
	protected static final IPOGNature makeNature(String description) {
		return POGNatureFactory.getInstance().getNature(description);
	}

}
