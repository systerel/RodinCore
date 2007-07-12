/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.pog.IPOGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.IModuleFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ProofObligationGenerator implements IAutomaticTool, IExtractor {

	private static final String TMP_EXTENSION_SUFFIX = "_tmp";

	public static String PRD_NAME_PREFIX = "PRD";
	
	public static boolean DEBUG = false;
	public static boolean DEBUG_STATE = false;
	public static boolean DEBUG_MODULECONF = false;
	
	protected static final String DEFAULT_CONFIG = EventBPlugin.PLUGIN_ID + ".fwd";
	
	protected final IPOGStateRepository createRepository(
			IPOFile target, 
			IProgressMonitor monitor) throws CoreException {
		
		final POGStateRepository repository = new POGStateRepository(target);
		
		if (DEBUG_STATE)
			repository.debug();
		
		return repository;
	}
	
	protected IPOFile getTmpPOFile(IPOFile poFile) {
		final IRodinProject project = (IRodinProject) poFile.getParent();
		final String name = poFile.getElementName();
		return (IPOFile) project.getRodinFile(name + "_tmp");
	}

//	protected IPOFile getTmpPOFile(IPOFile poFile, IProgressMonitor monitor) throws RodinDBException {
//		final IRodinProject project = (IRodinProject) poFile.getParent();
//		final String name = poFile.getElementName();
//		String tmpName = name + TMP_EXTENSION_SUFFIX;
//		if (poFile.exists())
//			poFile.move(project, null, tmpName, true, monitor);
//		return (IPOFile) project.getRodinFile(tmpName);
//	}

	// Compare the temporary file with the current proof obligation file.
	// The comparison is done per predicate set and per sequent.
	// Changed parent predicate sets are taken into account in the
	// comparison: if a parent predicate set has changed, then the 
	// child pedicate set or sequent is considered changed too.
	//
	// TODO progress monitor
	protected boolean compareAndSave(
			IPOFile oldFile, 
			IPOFile newFile,
			IProgressMonitor monitor) throws CoreException {
		
		assert oldFile != null;
		assert newFile != null;
		
		long freshStamp;
		boolean oldExists = oldFile.exists();
		if (oldExists) {
			freshStamp = oldFile.getStamp();
			freshStamp++;
		} else
			freshStamp = IPOStampedElement.INIT_STAMP;
		
		IPOPredicateSet[] predSets = newFile.getPredicateSets();
		Set<IPOPredicateSet> chPrdSets = comparePredicateSets(oldFile, predSets, freshStamp, null);
		
		IPOSequent[] sequents = newFile.getSequents();
		boolean chSeqs = compareSequents(oldFile, sequents, chPrdSets, freshStamp, null);
		
		// the delta check is only valid if the PO file only contains predicate sets and sequents
		// and not any other element types. Predicate sets and sequents themselves can be extended though.
		if (newFile.getChildren().length != predSets.length + sequents.length) {
			throw Util.newCoreException(
					"PO files must only contains elements of type IPOPredicateSet or IPOSequent");
		}
		
		boolean changed = !oldExists;
		changed |= chSeqs;
		changed |= !chPrdSets.isEmpty();
		changed |= predSets.length != (oldExists ? oldFile.getPredicateSets().length : -1);
		changed |= sequents.length != (oldExists ? oldFile.getSequents().length : -1);
		
		if (changed) {
			newFile.setStamp(freshStamp, null);
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
			IPOFile oldFile, 
			IPOSequent[] sequents, 
			Set<IPOPredicateSet> chPrdSets,
			long freshStamp, 
			IProgressMonitor monitor) throws RodinDBException {
		boolean changed = false;
		
		for (IPOSequent sequent : sequents) {
			
			IPOPredicateSet hyp = sequent.getHypotheses()[0];
			if (chPrdSets.contains(hyp)) {
				sequent.setStamp(freshStamp, monitor);
				changed = true;
			} else {

				String name = sequent.getElementName();
				IPOSequent oldSequent = oldFile.getSequent(name);

				assert oldSequent != null;
				if (oldSequent.exists()) {
					long oldStamp = oldSequent.getStamp();
					sequent.setStamp(oldStamp, monitor);
					if (!sequent.hasSameAttributes(oldSequent)
							|| !sequent.hasSameChildren(oldSequent)) {
						sequent.setStamp(freshStamp, monitor);
						changed = true;
					}
				} else {
					sequent.setStamp(freshStamp, monitor);
					changed = true;
				}
			}
		}
		
		return changed;
	}

	private Set<IPOPredicateSet> comparePredicateSets(
			IPOFile oldFile, 
			IPOPredicateSet[] predSets, 
			long freshStamp,
			IProgressMonitor monitor) throws RodinDBException {
		HashSet<IPOPredicateSet> done = new HashSet<IPOPredicateSet>(predSets.length * 4 / 3 + 1);
		HashSet<IPOPredicateSet> chPrdSets = new HashSet<IPOPredicateSet>(predSets.length);

		// compute roots of changed and unchanged predicate sets
		for (IPOPredicateSet newSet : predSets) {
			String name = newSet.getElementName();
			IPOPredicateSet oldSet = oldFile.getPredicateSet(name);
			assert oldSet != null;
			if (oldSet.exists()) {
				long oldStamp = oldSet.getStamp();
				newSet.setStamp(oldStamp, monitor);
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
					
					newSet.setStamp(freshStamp, monitor);
					chPrdSets.add(newSet);
					done.add(newSet);
				}
			} else {
				newSet.setStamp(IPOStampedElement.INIT_STAMP, monitor);
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
					cSet.setStamp(freshStamp, monitor);
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
	public void clean(IFile source, IFile file, IProgressMonitor monitor)
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

	protected void runModules(
			IPOGProcessorModule rootModule,
			IRodinFile file, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		rootModule.initModule(file, repository, monitor);
		rootModule.process(file, repository, monitor);
		rootModule.endModule(file, repository, monitor);
		
	}

	protected void printModuleTree(IRodinFile file, IModuleFactory moduleFactory) {
		if (DEBUG_MODULECONF) {
			System.out.println("+++ PROOF OBLIGATION GENERATOR MODULES +++");
			System.out.println("INPUT " + file.getPath());
			System.out.println("      " + file.getElementType());
			System.out.println("CONFIG " + DEFAULT_CONFIG);
			System.out.print(moduleFactory
					.printModuleTree(file.getElementType()));
			System.out.println("++++++++++++++++++++++++++++++++++++++");
		}
	}

}
