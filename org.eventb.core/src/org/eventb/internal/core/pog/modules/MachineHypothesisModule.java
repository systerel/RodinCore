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
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCBaggedElement;
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
import org.eventb.core.pog.IHypothesisManager;
import org.eventb.core.pog.IMachineInvariantTable;
import org.eventb.core.pog.IMachineTheoremTable;
import org.eventb.core.pog.IMachineVariableTable;
import org.eventb.core.pog.IPredicateTable;
import org.eventb.core.pog.Module;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.pog.IdentifierTable;
import org.eventb.internal.core.pog.MachineHypothesisManager;
import org.eventb.internal.core.pog.MachineInvariantTable;
import org.eventb.internal.core.pog.MachineTheoremTable;
import org.eventb.internal.core.pog.MachineVariableTable;
import org.eventb.internal.core.sc.TypingState;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineHypothesisModule extends Module {

	public static String PRD_NAME_PREFIX = "PRD";
	
	IHypothesisManager hypothesisManager;
	ITypeEnvironment typeEnvironment;
	FormulaFactory factory;
	IMachineInvariantTable invariantTable;
	IMachineTheoremTable theoremTable;
	IdentifierTable identifierTable;
	
	private int index;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOFile target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, target, repository, monitor);
		index = 0;
		
		factory = repository.getFormulaFactory();
		
		typeEnvironment = factory.makeTypeEnvironment();
		
		repository.setState(new TypingState(typeEnvironment));
		
		identifierTable = new IdentifierTable();
		
		ISCMachineFile scMachineFile = (ISCMachineFile) element;
		
		IPOPredicateSet rootSet =
			(IPOPredicateSet) target.createInternalElement(
					IPOPredicateSet.ELEMENT_TYPE, 
					MachineHypothesisManager.CTX_HYP_NAME, null, monitor);
		
		ISCInternalContext[] contexts = scMachineFile.getSCInternalContexts();
		
		copyContexts(rootSet, contexts, monitor);
		
		rootSet =
			(IPOPredicateSet) target.createInternalElement(
					IPOPredicateSet.ELEMENT_TYPE, 
					MachineHypothesisManager.ABS_HYP_NAME, null, monitor);
		rootSet.setParentPredicateSet(MachineHypothesisManager.CTX_HYP_NAME, monitor);
		
		String bag = scMachineFile.getElementName();
		
		List<ISCPredicateElement> predicates = new LinkedList<ISCPredicateElement>();
		
		fetchVariables(scMachineFile.getSCVariables(), repository, monitor);
		
		identifierTable.save(target, monitor);
		
		ISCInvariant[] invariants = scMachineFile.getSCInvariants();
		ISCTheorem[] theorems = scMachineFile.getSCTheorems();
		
		invariantTable = new MachineInvariantTable();
		theoremTable = new MachineTheoremTable();
		
		repository.setState(invariantTable);
		repository.setState(theoremTable);
		
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
	
	private void fetchVariables(
			ISCVariable[] variables, 
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		IMachineVariableTable variableTable =
			new MachineVariableTable(variables.length);
		repository.setState(variableTable);
		
		for(ISCVariable variable : variables) {
			FreeIdentifier identifier = fetchIdentifier(variable);
			FreeIdentifier primedIdent = identifier.withPrime(factory);
			typeEnvironment.addName(primedIdent.getName(), primedIdent.getType());
			if (variable.isForbidden(monitor))
				continue;
			variableTable.add(identifier, variable.isPreserved(monitor));
		}
		variableTable.trimToSize();
	}
	
	private void copyContexts(IPOPredicateSet rootSet, 
			ISCInternalContext[] contexts, 
			IProgressMonitor monitor) throws RodinDBException {
		
		for (ISCInternalContext context : contexts) {
			
			for (ISCCarrierSet set : context.getSCCarrierSets()) {
				fetchIdentifier(set);
			}
			for (ISCConstant constant : context.getSCConstants()) {
				fetchIdentifier(constant);
			}
			for (ISCAxiom axiom : context.getSCAxioms()) {
				savePOPredicate(rootSet, axiom, monitor);
			}
			for (ISCTheorem theorem : context.getSCTheorems()) {
				savePOPredicate(rootSet, theorem, monitor);
			}
		}
		
	}

	private FreeIdentifier fetchIdentifier(ISCIdentifierElement ident) throws RodinDBException {
		FreeIdentifier identifier = 
			factory.makeFreeIdentifier(ident.getIdentifierName(), null, ident.getType(factory));
		typeEnvironment.addName(identifier.getName(), identifier.getType());
		identifierTable.addIdentifier(identifier);
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
			ISCBaggedElement baggedElement = (ISCBaggedElement) element;
			if (bag.equals(baggedElement.getBag(monitor))) {
				predicates.add(element);
				predicateTable.addElement(element, typeEnvironment, factory);
			} else {
				savePOPredicate(rootSet, element, monitor); 
			}
		}
	}

	private void savePOPredicate(IPOPredicateSet rootSet, ISCPredicateElement element, IProgressMonitor monitor) throws RodinDBException {
		IPOPredicate predicate =
			(IPOPredicate) rootSet.createInternalElement(
					IPOPredicate.ELEMENT_TYPE, PRD_NAME_PREFIX + index++, null, monitor);
		predicate.setPredicateString(element.getPredicateString(), monitor);
		predicate.setSource(((ITraceableElement) element).getSource(monitor), monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IPOFile target,
			IStateRepository repository, 
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
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		hypothesisManager.createHypotheses(target, monitor);
		typeEnvironment = null;
		factory = null;
		
		super.endModule(element, target, repository, monitor);
	}

}
