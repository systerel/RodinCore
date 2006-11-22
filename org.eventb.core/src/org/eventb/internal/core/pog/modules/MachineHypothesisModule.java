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
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.Module;
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
import org.eventb.internal.core.pog.TypingState;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineHypothesisModule extends Module {

	public static String PRD_NAME_PREFIX = "PRD";
	
	IMachineHypothesisManager hypothesisManager;
	ITypeEnvironment typeEnvironment;
	FormulaFactory factory;
	IMachineInvariantTable invariantTable;
	IMachineTheoremTable theoremTable;
	
	private int index;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		index = 0;
		
		factory = repository.getFormulaFactory();
		
		typeEnvironment = factory.makeTypeEnvironment();
		
		repository.setState(new TypingState(typeEnvironment));
		
		ISCMachineFile scMachineFile = (ISCMachineFile) element;
		
		createContextHypSet(scMachineFile, target, monitor);
		
		IPOPredicateSet rootSet = target.getPredicateSet(MachineHypothesisManager.ABS_HYP_NAME);
		rootSet.create(null, monitor);
		rootSet.setParentPredicateSet(target.getPredicateSet(MachineHypothesisManager.CTX_HYP_NAME), monitor);
		
		fetchVariables(scMachineFile.getSCVariables(), rootSet, repository, monitor);
		
		ISCInvariant[] invariants = scMachineFile.getSCInvariants();
		ISCTheorem[] theorems = scMachineFile.getSCTheorems();
		
		invariantTable = new MachineInvariantTable();
		theoremTable = new MachineTheoremTable();
		
		repository.setState(invariantTable);
		repository.setState(theoremTable);
		
		String bag = scMachineFile.getMachineFile().getElementName();
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		fetchPredicates(predicates, invariantTable, rootSet, invariants, bag, monitor);
		fetchPredicates(predicates, theoremTable, rootSet, theorems, bag, monitor);
		
		invariantTable.trim();
		theoremTable.trim();
		
		ISCPredicateElement[] predicateElements = new ISCPredicateElement[predicates.size()];		
		predicates.toArray(predicateElements);
		
		hypothesisManager = 
			new MachineHypothesisManager(scMachineFile, predicateElements, monitor);
		
		repository.setState(hypothesisManager);
		
	}

	private void createContextHypSet(ISCMachineFile scMachineFile, IPOFile target, IProgressMonitor monitor) throws RodinDBException {
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
	
	private void copyContexts(IPOPredicateSet rootSet, 
			ISCInternalContext[] contexts, 
			IProgressMonitor monitor) throws RodinDBException {
		
		for (ISCInternalContext context : contexts) {
			
			for (ISCCarrierSet set : context.getSCCarrierSets()) {
				FreeIdentifier identifier = fetchIdentifier(set);
				createIdentifier(rootSet, identifier, monitor);
			}
			for (ISCConstant constant : context.getSCConstants()) {
				FreeIdentifier identifier = fetchIdentifier(constant);
				createIdentifier(rootSet, identifier, monitor);
			}
			for (ISCAxiom axiom : context.getSCAxioms()) {
				savePOPredicate(rootSet, axiom, monitor);
			}
			for (ISCTheorem theorem : context.getSCTheorems()) {
				savePOPredicate(rootSet, theorem, monitor);
			}
		}
		
	}

	private void createIdentifier(IPOPredicateSet predSet, FreeIdentifier identifier, IProgressMonitor monitor) throws RodinDBException {
		String idName = identifier.getName();
		Type type = identifier.getType();
		IPOIdentifier poIdentifier = predSet.getIdentifier(idName);
		poIdentifier.create(null, monitor);
		poIdentifier.setType(type, monitor);
	}

	private FreeIdentifier fetchIdentifier(ISCIdentifierElement ident) throws RodinDBException {
		FreeIdentifier identifier = ident.getIdentifier(factory);
		typeEnvironment.add(identifier);
		return identifier;
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

	private void savePOPredicate(
			IPOPredicateSet rootSet, 
			ISCPredicateElement element, 
			IProgressMonitor monitor) throws RodinDBException {
		IPOPredicate predicate = rootSet.getPredicate(PRD_NAME_PREFIX + index++);
		predicate.create(null, monitor);
		predicate.setPredicateString(element.getPredicateString(), monitor);
		predicate.setSource(((ITraceableElement) element).getSource(), monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor)
		throws CoreException {
		
		// all is done in the initialisation part

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOFile target,
			IStateRepository<IStatePOG> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(target, monitor);
		typeEnvironment = null;
		factory = null;
		
		super.endModule(element, target, repository, monitor);
	}

}
