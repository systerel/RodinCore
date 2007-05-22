/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.MachineHypothesisManager;
import org.eventb.internal.core.pog.MachineInvariantTable;
import org.eventb.internal.core.pog.MachineTheoremTable;
import org.eventb.internal.core.pog.MachineVariableTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineHypothesesModule extends GlobalHypothesesModule {

	public static final IModuleType<FwdMachineHypothesesModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineHypothesesModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	MachineHypothesisManager hypothesisManager;
	MachineInvariantTable invariantTable;
	MachineTheoremTable theoremTable;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		ISCMachineFile scMachineFile = (ISCMachineFile) element;
		
		IPOFile target = repository.getTarget();
		
		createContextHypSet(scMachineFile, target, monitor);
		
		IPOPredicateSet rootSet = target.getPredicateSet(MachineHypothesisManager.ABS_HYP_NAME);
		rootSet.create(null, monitor);
		rootSet.setParentPredicateSet(
				target.getPredicateSet(MachineHypothesisManager.CTX_HYP_NAME), monitor);
		
		fetchVariables(scMachineFile.getSCVariables(), rootSet, repository, monitor);
		
		ISCInvariant[] invariants = scMachineFile.getSCInvariants();
		ISCTheorem[] theorems = scMachineFile.getSCTheorems();
		
		String bag = scMachineFile.getMachineFile().getElementName();
		
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		List<ISCInvariant> invPreds = 
			fetchPredicates(predicates, rootSet, invariants, bag, monitor);
		List<ISCTheorem> thmPreds = 
			fetchPredicates(predicates, rootSet, theorems, bag, monitor);
		
		invariantTable = new MachineInvariantTable(
				invPreds.toArray(new ISCInvariant[invPreds.size()]),
				typeEnvironment,
				factory);
		theoremTable = new MachineTheoremTable(
				thmPreds.toArray(new ISCTheorem[thmPreds.size()]),
				typeEnvironment,
				factory);
		
		repository.setState(invariantTable);
		repository.setState(theoremTable);
		
		invariantTable.makeImmutable();
		theoremTable.makeImmutable();
		
		ISCPredicateElement[] predicateElements = new ISCPredicateElement[predicates.size()];		
		predicates.toArray(predicateElements);
		
		boolean accuracy = scMachineFile.isAccurate();
		
		hypothesisManager = 
			new MachineHypothesisManager(scMachineFile, target, predicateElements, accuracy);
		
		repository.setState(hypothesisManager);
		
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(monitor);
		factory = null;
		
		super.endModule(element, repository, monitor);
	}

	private void createContextHypSet(
			ISCMachineFile scMachineFile, 
			IPOFile target, 
			IProgressMonitor monitor) throws RodinDBException {
		IPOPredicateSet ctxRootSet = target.getPredicateSet(MachineHypothesisManager.CTX_HYP_NAME);
		ctxRootSet.create(null, monitor);
		
		ISCInternalContext[] contexts = scMachineFile.getSCSeenContexts();
		
		copyContexts(ctxRootSet, contexts, monitor);
	}
	
	private void fetchVariables(
			ISCVariable[] variables, 
			IPOPredicateSet predSet,
			IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		MachineVariableTable variableTable =
			new MachineVariableTable(variables.length);
		repository.setState(variableTable);
		
		for(ISCVariable variable : variables) {
			FreeIdentifier identifier = fetchIdentifier(variable);
			createIdentifier(predSet, identifier, monitor);
			if (variable.isForbidden())
				continue;
			variableTable.add(identifier, variable.isPreserved());
		}
		variableTable.makeImmutable();
	}
	
	private <PE extends ISCPredicateElement> List<PE> fetchPredicates(
			List<ISCPredicateElement> predicates,
			IPOPredicateSet rootSet, 
			PE[] predicateElements,
			String bag,
			IProgressMonitor monitor) throws RodinDBException {
		
		List<PE> localPreds = new LinkedList<PE>();
		
		for(PE element : predicateElements) {
			ITraceableElement baggedElement = (ITraceableElement) element;
			String elementBag = 
				((IInternalElement) baggedElement.getSource()).getRodinFile().getElementName();
			if (bag.equals(elementBag)) {
				predicates.add(element);
				localPreds.add(element);
			} else {
				savePOPredicate(rootSet, element, monitor); 
			}
		}
		return localPreds;
	}

}
