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
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineInvariantTable;
import org.eventb.core.pog.state.IMachineTheoremTable;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.pog.state.IStatePOG;
import org.eventb.core.state.IStateRepository;
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
public class MachineHypothesisModule extends GlobalHypothesisModule {

	IMachineHypothesisManager hypothesisManager;
	IMachineInvariantTable invariantTable;
	IMachineTheoremTable theoremTable;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		
		ISCMachineFile scMachineFile = (ISCMachineFile) element;
		
		createContextHypSet(scMachineFile, target, monitor);
		
		IPOPredicateSet rootSet = target.getPredicateSet(MachineHypothesisManager.ABS_HYP_NAME);
		rootSet.create(null, monitor);
		rootSet.setParentPredicateSet(target.getPredicateSet(MachineHypothesisManager.CTX_HYP_NAME), monitor);
		
		fetchVariables(scMachineFile.getSCVariables(), rootSet, repository, monitor);
		
		ISCInvariant[] invariants = scMachineFile.getSCInvariants();
		ISCTheorem[] theorems = scMachineFile.getSCTheorems();
		
		String bag = scMachineFile.getMachineFile().getElementName();
		
		invariantTable = new MachineInvariantTable();
		theoremTable = new MachineTheoremTable();
		
		repository.setState(invariantTable);
		repository.setState(theoremTable);
		
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		fetchPredicates(predicates, invariantTable, rootSet, invariants, bag, monitor);
		fetchPredicates(predicates, theoremTable, rootSet, theorems, bag, monitor);
		
		invariantTable.trim();
		theoremTable.trim();
		
		ISCPredicateElement[] predicateElements = new ISCPredicateElement[predicates.size()];		
		predicates.toArray(predicateElements);
		
		hypothesisManager = 
			new MachineHypothesisManager(scMachineFile, predicateElements);
		
		repository.setState(hypothesisManager);
		
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(target, monitor);
		factory = null;
		
		super.endModule(element, target, repository, monitor);
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
			IStateRepository<IStatePOG> repository,
			IProgressMonitor monitor) throws CoreException {
		
		IMachineVariableTable variableTable =
			new MachineVariableTable(variables.length);
		repository.setState(variableTable);
		
		for(ISCVariable variable : variables) {
			FreeIdentifier identifier = fetchIdentifier(variable);
			createIdentifier(predSet, identifier, monitor);
			if (variable.isForbidden())
				continue;
			variableTable.add(identifier, variable.isPreserved());
		}
		variableTable.trimToSize();
	}
	
	private void fetchPredicates(
			List<ISCPredicateElement> predicates,
			IPredicateTable predicateTable,
			IPOPredicateSet rootSet, 
			ISCPredicateElement[] predicateElements,
			String bag,
			IProgressMonitor monitor) throws RodinDBException {
		
		for(ISCPredicateElement element : predicateElements) {
			ITraceableElement baggedElement = (ITraceableElement) element;
			String elementBag = 
				((IInternalElement) baggedElement.getSource()).getRodinFile().getElementName();
			if (bag.equals(elementBag)) {
				predicates.add(element);
				predicateTable.addElement(element, typeEnvironment, factory);
			} else {
				savePOPredicate(rootSet, element, monitor); 
			}
		}
	}

}
