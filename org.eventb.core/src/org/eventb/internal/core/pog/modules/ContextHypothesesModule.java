/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.ContextAxiomTable;
import org.eventb.internal.core.pog.ContextHypothesisManager;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 *
 */
public class ContextHypothesesModule extends GlobalHypothesesModule {

	public static final IModuleType<ContextHypothesesModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextHypothesesModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	ContextHypothesisManager hypothesisManager;
	ContextAxiomTable axiomTable;
	IPORoot target;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		IRodinFile scContextFile = (IRodinFile) element;
		ISCContextRoot scContextRoot = (ISCContextRoot) scContextFile.getRoot();
		
		
		target = repository.getTarget();
		
		IPOPredicateSet rootSet = target.getPredicateSet(ContextHypothesisManager.ABS_HYP_NAME);
		rootSet.create(null, monitor);
		
		ISCInternalContext[] contexts = scContextRoot.getAbstractSCContexts();
		
		copyContexts(rootSet, contexts, monitor);
				
		fetchCarriersSetsAndConstants(scContextRoot, rootSet, monitor);
		
		ISCAxiom[] axioms = scContextRoot.getSCAxioms();
		
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		fetchPredicates(predicates, axioms);
		
		axiomTable = new ContextAxiomTable(axioms, typeEnvironment, factory);
		
		repository.setState(axiomTable);
		
		axiomTable.makeImmutable();
		
		ISCPredicateElement[] predicateElements = new ISCPredicateElement[predicates.size()];		
		predicates.toArray(predicateElements);
		
		boolean accuracy = scContextRoot.isAccurate();
		
		hypothesisManager = 
			new ContextHypothesisManager(scContextFile, target, predicateElements, accuracy);
		
		repository.setState(hypothesisManager);
		
	}
	
	private void fetchPredicates(
			List<ISCPredicateElement> predicates,
			ISCPredicateElement[] predicateElements) throws RodinDBException {
		
		for(ISCPredicateElement element : predicateElements) {
			predicates.add(element);
		}
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(monitor);
		target = null;
		hypothesisManager = null;
		axiomTable = null;
		
		super.endModule(element, repository, monitor);
	}

}
