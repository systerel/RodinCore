/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protopog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.protosc.MachineSC;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

public class MachinePOG implements IAutomaticTool, IExtractor {
	
	private IProgressMonitor monitor;

	private ISCMachineFile scMachine;
	private IPOFile poFile;
	
	private MachineRuleBase ruleBase;
	
	private SCMachineCache machineCache;
	
	private List<ProofObligation> poList;

	public void init(
			@SuppressWarnings("hiding") ISCMachineFile scMachine, 
			@SuppressWarnings("hiding") IPOFile poFile, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) {
		this.monitor = monitor;
		this.scMachine = scMachine;
		this.poFile = poFile;
		this.ruleBase = new MachineRuleBase();
		this.poList = new LinkedList<ProofObligation>();
	}
	
	public void run() throws CoreException {
		
		machineCache = new SCMachineCache(scMachine, monitor);
		
		for(IPOGMachineRule rule : ruleBase.getRules()) {
			poList.addAll(rule.get(machineCache));
		}
		
		// Create the resulting PO file atomically.
		RodinCore.run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor saveMonitor) throws CoreException {
						createPOFile();
					}
				}, monitor);
	}
	
	public boolean run(IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {

		IPOFile newPOFile = (IPOFile) RodinCore.create(file);
		ISCMachineFile machineIn = newPOFile.getSCMachine();

		if (! machineIn.exists())
			MachineSC.makeError("Source SC context does not exist.");
		
		init(machineIn, newPOFile, monitor);
		run();
		return true;
	}

	public void clean(IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		ISCMachineFile in = (ISCMachineFile) RodinCore.create(file);
		IPOFile target = in.getMachineFile().getPOFile();
		
		IPath inPath = in.getPath();
		IPath targetPath = target.getPath();
		
		graph.addNode(targetPath, POGCore.MACHINE_POG_TOOL_ID);
		graph.putToolDependency(inPath, targetPath, POGCore.MACHINE_POG_TOOL_ID, true);
		graph.updateGraph();
	}

	void createPOFile() throws CoreException {
		IRodinProject project = poFile.getRodinProject();
		project.createRodinFile(poFile.getElementName(), true, null);

		createGlobalTypeEnvironment();
		createHypSets();
		createProofObligations();
		
		poFile.save(monitor, true);
	}
	
	private void createHypSets() throws CoreException {
		
		FormulaFactory factory = FormulaFactory.getDefault();
		
		IInternalElement element = poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, machineCache.getOldHypSetName(), null, monitor);
		int nameIdx = 0;
		for(ISCAxiom axiom : machineCache.getOldAxioms()) {
			final String name = "p" + nameIdx++;
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			predicate.setContents(axiom.getContents(), monitor);
		}
		for(ISCTheorem theorem : machineCache.getOldTheorems()) {
			final String name = "p" + nameIdx++;
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			predicate.setContents(theorem.getContents(), monitor);
		}
		for(ISCCarrierSet carrierSet : machineCache.getSCCarrierSets()) {
			final String name = "p" + nameIdx++;
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			Predicate pp = factory.makeRelationalPredicate(
					Formula.NOTEQUAL, 
					carrierSet.getIdentifier(factory), 
					factory.makeAtomicExpression(Formula.EMPTYSET, null), 
					null);
			predicate.setContents(pp.toString(), monitor);
		}
		// TODO: this is actually wrong! there must be a separate hyp set for context hypotheses
		// The problem doesn't show in the preliminary tool because we don not have refinement! 
//		for(IInvariant invariant : machineCache.getOldInvariants()) {
//			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
//			predicate.setContents(invariant.getContents(), monitor);
//		}
		ISCInvariant[] invariants = machineCache.getNewInvariants();
		for(int i=0; i<invariants.length-1; i++) {
			final String name = invariants[i].getElementName();
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, machineCache.getHypSetName(invariants[i+1].getElementName()), null, monitor);
			predicateSet.setContents(machineCache.getHypSetName(name), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			predicate.setContents(invariants[i].getContents(monitor), monitor);
		}
		ISCTheorem[] theorems = machineCache.getNewTheorems();
		if(invariants.length > 0) {
			final String name = invariants[invariants.length-1].getElementName();
			if(theorems.length == 0) {
				IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, machineCache.getNewHypsetName(), null, monitor);
				predicateSet.setContents(machineCache.getHypSetName(name), monitor);
				IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
				predicate.setContents(invariants[invariants.length-1].getContents(monitor), monitor);
			} else if(theorems.length > 0) {
				IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, machineCache.getHypSetName(theorems[0].getElementName()), null, monitor);
				predicateSet.setContents(machineCache.getHypSetName(name), monitor);
				IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
				predicate.setContents(invariants[invariants.length-1].getContents(monitor), monitor);
			}
		}
		for(int i=0; i<theorems.length-1; i++) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, machineCache.getHypSetName(theorems[i+1].getElementName()), null, monitor);
			final String name = theorems[i].getElementName();
			predicateSet.setContents(machineCache.getHypSetName(name), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			predicate.setContents(theorems[i].getContents(monitor), monitor);
		}
		if(theorems.length > 0) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, machineCache.getNewHypsetName(), null, monitor);
			final String name = theorems[theorems.length-1].getElementName();
			predicateSet.setContents(machineCache.getHypSetName(name), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			predicate.setContents(theorems[theorems.length-1].getContents(monitor), monitor);
		}
	}
	
	private void createGlobalTypeEnvironment() throws CoreException {
		for(ISCCarrierSet identifier : machineCache.getSCCarrierSets()) {
			IPOIdentifier newIdentifier = (IPOIdentifier) poFile.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identifier.getElementName(), null, monitor);
			newIdentifier.setContents(identifier.getContents(), monitor);
		}
		for(ISCConstant identifier : machineCache.getSCConstants()) {
			IPOIdentifier newIdentifier = (IPOIdentifier) poFile.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identifier.getElementName(), null, monitor);
			newIdentifier.setContents(identifier.getContents(), monitor);
		}
		for(ISCVariable identifier : machineCache.getSCVariables()) {
			IPOIdentifier newIdentifier = (IPOIdentifier) poFile.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identifier.getElementName(), null, monitor);
			newIdentifier.setContents(identifier.getContents(), monitor);
			IPOIdentifier primedIdentifier = (IPOIdentifier) poFile.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identifier.getElementName() + "'", null, monitor);
			primedIdentifier.setContents(identifier.getContents(), monitor);
		}
	}
	
	private void createProofObligations() throws CoreException {
		for(ProofObligation obligation : poList) {
			obligation.put(poFile, monitor);
		}
	}

}
