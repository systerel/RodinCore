/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - create a marker when no configuration for file (no exception)
 *     Systerel - added config in message for problem LoadingRootModuleError
 *     Systerel - added removal of temporary file
 *******************************************************************************/
package org.eventb.core.pog;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.sc.GraphProblem;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.pog.Messages;
import org.eventb.internal.core.pog.POGStateRepository;
import org.eventb.internal.core.pog.POGUtil;
import org.eventb.internal.core.sc.SCUtil;
import org.eventb.internal.core.tool.IModuleFactory;
import org.eventb.internal.core.tool.POGModuleManager;
import org.eventb.internal.core.tool.types.IPOGProcessorModule;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class ProofObligationGenerator implements IAutomaticTool, IExtractor {

	private static final String TMP_EXTENSION_SUFFIX = "_tmp";

	public static String PRD_NAME_PREFIX = "PRD";
	
	private IPOGStateRepository createRepository(
			IEventBRoot source,
			IPORoot target,
			IProgressMonitor monitor) throws CoreException {
		
		final POGStateRepository repository = new POGStateRepository(source, target);
		
		if (POGUtil.DEBUG_STATE)
			repository.debug();
		
		return repository;
	}
	
	private IRodinFile getTmpPOFile(IRodinFile poFile) {
		final IRodinProject project = (IRodinProject) poFile.getParent();
		final String name = poFile.getElementName();
		return project.getRodinFile(name + "_tmp");
	}

	// Compare the temporary file with the current proof obligation file.
	// The comparison is done per predicate set and per sequent.
	// Changed parent predicate sets are taken into account in the
	// comparison: if a parent predicate set has changed, then the 
	// child predicate set or sequent is considered changed too.
	//
	// TODO progress monitor
	private boolean compareAndSave(
			IRodinFile oldFile, 
			IRodinFile newFile,
			IProgressMonitor monitor) throws CoreException {
		
		assert oldFile != null;
		assert newFile != null;

		final IPORoot oldRoot = (IPORoot) oldFile.getRoot();
		final IPORoot newRoot = (IPORoot) newFile.getRoot();
		final long freshStamp;
		final boolean oldExists = oldFile.exists();
		if (oldExists) {
			freshStamp = oldRoot.getPOStamp() + 1;
		} else {
			freshStamp = IPOStampedElement.INIT_STAMP;
		}

		IPOPredicateSet[] predSets = newRoot.getPredicateSets();
		Set<IPOPredicateSet> chPrdSets = comparePredicateSets(oldRoot, predSets, freshStamp, null);
		
		IPOSequent[] sequents = newRoot.getSequents();
		boolean chSeqs = compareSequents(oldRoot, sequents, chPrdSets, freshStamp, null);
		
		// the delta check is only valid if the PO file only contains predicate sets and sequents
		// and not any other element types. Predicate sets and sequents themselves can be extended though.
		if (newRoot.getChildren().length != predSets.length + sequents.length) {
			throw Util.newCoreException(
					"PO files must only contains elements of type IPOPredicateSet or IPOSequent");
		}
		
		boolean changed = !oldExists;
		changed |= chSeqs;
		changed |= !chPrdSets.isEmpty();
		changed |= predSets.length != (oldExists ? oldRoot.getPredicateSets().length : -1);
		changed |= sequents.length != (oldExists ? oldRoot.getSequents().length : -1);
		
		if (changed) {
			newRoot.setPOStamp(freshStamp, null);
			newFile.save(new SubProgressMonitor(monitor, 1), true, false);
			final IRodinElement project = oldFile.getParent();
			final String name = oldFile.getElementName();
			final SubProgressMonitor subPM = new SubProgressMonitor(monitor, 1);
			newFile.move(project, null, name, true, subPM);
			return true;			
		} else {
			newFile.delete(true, new SubProgressMonitor(monitor, 1));
			return false;
		}
	}
	
	private boolean compareSequents(
			IPORoot oldRoot, 
			IPOSequent[] sequents, 
			Set<IPOPredicateSet> chPrdSets,
			long freshStamp, 
			IProgressMonitor monitor) throws CoreException {
		boolean changed = false;
		
		for (IPOSequent sequent : sequents) {
			
			IPOPredicateSet hyp = sequent.getHypotheses()[0];
			if (chPrdSets.contains(hyp.getParentPredicateSet())) {
				sequent.setPOStamp(freshStamp, monitor);
				changed = true;
			} else {

				String name = sequent.getElementName();
				IPOSequent oldSequent = oldRoot.getSequent(name);

				assert oldSequent != null;
				if (oldSequent.exists()) {
					long oldStamp = oldSequent.getPOStamp();
					sequent.setPOStamp(oldStamp, monitor);
					if (!sequent.hasSameAttributes(oldSequent)
							|| !sequent.hasSameChildren(oldSequent)) {
						sequent.setPOStamp(freshStamp, monitor);
						changed = true;
					}
				} else {
					sequent.setPOStamp(freshStamp, monitor);
					changed = true;
				}
			}
		}
		
		return changed;
	}

	private Set<IPOPredicateSet> comparePredicateSets(
			IPORoot oldRoot, 
			IPOPredicateSet[] predSets, 
			long freshStamp,
			IProgressMonitor monitor) throws CoreException {
		HashSet<IPOPredicateSet> done = new HashSet<IPOPredicateSet>(predSets.length * 4 / 3 + 1);
		HashSet<IPOPredicateSet> chPrdSets = new HashSet<IPOPredicateSet>(predSets.length);

		// compute roots of changed and unchanged predicate sets
		for (IPOPredicateSet newSet : predSets) {
			String name = newSet.getElementName();
			IPOPredicateSet oldSet = oldRoot.getPredicateSet(name);
			assert oldSet != null;
			if (oldSet.exists()) {
				long oldStamp = oldSet.getPOStamp();
				newSet.setPOStamp(oldStamp, monitor);
				if (newSet.hasSameAttributes(oldSet) 
						&& newSet.hasSameChildren(oldSet)) {
					IPOPredicateSet parentSet = newSet.getParentPredicateSet();
					if (parentSet == null) {
						done.add(newSet);
					}
				} else {
					
					// The following assertion could theoretically fail;
					// For this to happen, the proof obligation generator
					// would have to run for many years without ever
					// cleaning the workspace
					assert oldStamp != freshStamp;
					
					newSet.setPOStamp(freshStamp, monitor);
					chPrdSets.add(newSet);
					done.add(newSet);
				}
			} else {
				newSet.setPOStamp(IPOStampedElement.INIT_STAMP, monitor);
				done.add(newSet);
			}
		}
		
		// update all dependent predicate sets
		for (IPOPredicateSet set : predSets) {
			if (done.contains(set))
				continue;
			
			List<IPOPredicateSet> list = new LinkedList<IPOPredicateSet>();
			list.add(set);
			IPOPredicateSet pSet = set.getParentPredicateSet();
			while (!done.contains(pSet)) {
				list.add(pSet);
				pSet = pSet.getParentPredicateSet();
			}
			if (chPrdSets.contains(pSet)) {
				for (IPOPredicateSet cSet : list) {
					cSet.setPOStamp(freshStamp, monitor);
					chPrdSets.add(cSet);
					done.add(cSet);
				}
			} else {
				for (IPOPredicateSet uSet : list) {
					done.add(uSet);
				}
			}
			
		}
		
		return chPrdSets;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public final void clean(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		try {
		
			monitor.beginTask(Messages.bind(Messages.build_cleaning, file.getName()), 1);
			
			if (file.exists())
				file.delete(true, monitor);
			
			IPath fullPath = file.getFullPath();
			IPath tmpPath = fullPath.removeFileExtension();
			tmpPath = tmpPath.addFileExtension(fullPath.getFileExtension() + TMP_EXTENSION_SUFFIX);
			
			IFile tmpFile = ResourcesPlugin.getWorkspace().getRoot().getFile(tmpPath);
			
			if (tmpFile.exists())
				tmpFile.delete(true, monitor);
			
			monitor.worked(1);
			
		} finally {
			monitor.done();
		}

	}

	private void runModules(
			IPOGProcessorModule rootModule,
			IRodinFile file, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		rootModule.initModule(file, repository, monitor);
		rootModule.process(file, repository, monitor);
		rootModule.endModule(file, repository, monitor);
		
	}

	private void printModuleTree(String config, IRodinFile file, IModuleFactory moduleFactory) {
		if (POGUtil.DEBUG_MODULECONF) {
			System.out.println("+++ PROOF OBLIGATION GENERATOR MODULES +++");
			System.out.println("INPUT " + file.getPath());
			System.out.println("      " + file.getRoot().getElementType());
			System.out.println("CONFIG " + config);
			System.out.print(moduleFactory
					.printModuleTree(file.getRoot().getElementType()));
			System.out.println("++++++++++++++++++++++++++++++++++++++");
		}
	}

	private IPOGProcessorModule getRootModule(IRodinFile rodinFile) throws CoreException {
	
		IConfigurationElement confElement = (IConfigurationElement) rodinFile.getRoot();
		
		if (confElement.hasConfiguration()) {
	
			final String config = confElement.getConfiguration();
	
			IModuleFactory moduleFactory = POGModuleManager.getInstance()
					.getModuleFactory(config);
	
			printModuleTree(config, rodinFile, moduleFactory);
	
			IPOGProcessorModule rootModule = (IPOGProcessorModule) moduleFactory
					.getRootModule(rodinFile.getRoot().getElementType());
			if (rootModule == null) {
				SCUtil.createProblemMarker(rodinFile, 
						GraphProblem.LoadingRootModuleError, config);
			}
			return rootModule;
	
		} else {
			SCUtil.createProblemMarker(confElement, 
					GraphProblem.ConfigurationMissingError, 
					rodinFile.getBareName());
			return null;
		}
	}

	@Override
	public final boolean run(IFile source, IFile target, IProgressMonitor monitor)
			throws CoreException {
				
				IRodinFile poFile = RodinCore.valueOf(target).getMutableCopy();
				IRodinFile srcRodinFile = RodinCore.valueOf(source).getSnapshot();
				final IRodinFile poTmpFile = getTmpPOFile(poFile);
				IPORoot poRoot = (IPORoot) poFile.getRoot();
				
				// TODO progress monitor
				try {
				
					monitor.beginTask(
							Messages.bind(
									Messages.build_runningPO, 
									poRoot.getComponentName()),
							1);
					
					poTmpFile.create(true, monitor);

					final IPOGStateRepository repository = createRepository(
					(IEventBRoot) srcRodinFile.getRoot(),
					(IPORoot) poTmpFile.getRoot(), monitor);

					IPOGProcessorModule rootModule = getRootModule(srcRodinFile);
				
					if (rootModule != null) {
						runModules(
							rootModule,
							srcRodinFile, 
							repository,
							monitor);
					}
					
					return compareAndSave(poFile, poTmpFile, monitor);
				} finally {
					// Ensure that the temporary file gets deleted
					if (poTmpFile.exists()) {
						poTmpFile.delete(true, new SubProgressMonitor(monitor, 1));
					}
					monitor.done();
					poFile.makeConsistent(null);
				}
			}

}
