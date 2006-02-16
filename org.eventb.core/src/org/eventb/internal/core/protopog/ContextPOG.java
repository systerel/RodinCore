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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ITheorem;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.protosc.ContextSC;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;
import org.rodinp.core.builder.IInterrupt;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextPOG implements IAutomaticTool, IExtractor {

	@SuppressWarnings("unused")
	private IInterrupt interrupt;
	private IProgressMonitor monitor;

	private ISCContext scContext;
	private IPOFile poFile;
	
	private ContextRuleBase ruleBase;
	
	private SCContextCache contextCache;
	
	private List<ProofObligation> poList;

	public void init(
			@SuppressWarnings("hiding") ISCContext scContext, 
			@SuppressWarnings("hiding") IPOFile poFile, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) {
		this.interrupt = interrupt;
		this.monitor = monitor;
		this.scContext = scContext;
		this.poFile = poFile;
		this.ruleBase = new ContextRuleBase();
		this.poList = new LinkedList<ProofObligation>();
	}
	
	public void run() throws CoreException {
		
		contextCache = new SCContextCache(scContext, monitor);
		
		for(IPOGContextRule rule : ruleBase.getRules()) {
			poList.addAll(rule.get(contextCache));
		}
		
		createPOFile();
	}
	
	public boolean run(IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
				
		IPOFile newPOFile = (IPOFile) RodinCore.create(file);
		newPOFile.getRodinProject().createRodinFile(newPOFile.getElementName(), true, null);
		
		// TODO: the explicit file extension should be replaced by a request to the content type manager
		IFile contextFile = workspace.getRoot().getFile(file.getFullPath().removeFileExtension().addFileExtension("bcc"));
		
		ISCContext contextIn = (ISCContext) RodinCore.create(contextFile);
		if(!contextIn.exists())
			ContextSC.makeError("Source SC context does not exist.");
		contextIn.open(monitor);
		
		
		init(contextIn, newPOFile, interrupt, monitor);
		
		run();
		
		contextIn.close();
		newPOFile.close();
		
		return true;
	}

	public void clean(IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);
	}

	public void extract(IFile file, IGraph graph) throws CoreException {
		// TODO Auto-generated method stub
		IPath target = file.getFullPath().removeFileExtension().addFileExtension("bpo");
		graph.addNode(target, POGCore.CONTEXT_POG_TOOL_ID);
		IPath[] paths = graph.getDependencies(target, POGCore.CONTEXT_POG_TOOL_ID);
		if(paths.length == 1 && paths[0].equals(target))
			return;
		else {
			graph.removeDependencies(target, POGCore.CONTEXT_POG_TOOL_ID);
			graph.addToolDependency(file.getFullPath(), target, POGCore.CONTEXT_POG_TOOL_ID, true);
		}
	}

	private void createPOFile() throws CoreException {
		createGlobalTypeEnvironment();
		createHypSets();
		createProofObligations();
		
		poFile.save(monitor, true);
	}
	
	private void createHypSets() throws CoreException {
		
		FormulaFactory factory = FormulaFactory.getDefault();
		
		IInternalElement element = poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getOldHypSetName(), null, monitor);
		for(IAxiom axiom : contextCache.getOldAxioms()) {
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			predicate.setContents(axiom.getContents(), monitor);
		}
		for(ITheorem theorem : contextCache.getOldTheorems()) {
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			predicate.setContents(theorem.getContents(), monitor);
		}
		for(ISCCarrierSet carrierSet : contextCache.getSCCarrierSets()) {
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			Predicate pp = factory.makeRelationalPredicate(
					Formula.NOTEQUAL, 
					factory.makeFreeIdentifier(carrierSet.getName(), null), 
					factory.makeAtomicExpression(Formula.EMPTYSET, null), 
					null);
			predicate.setContents(pp.toString(), monitor);
		}
		IAxiom[] axioms = contextCache.getNewAxioms();
		for(int i=0; i<axioms.length-1; i++) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getHypSetName(axioms[i+1].getElementName()), null, monitor);
			predicateSet.setContents(contextCache.getHypSetName(axioms[i].getElementName()), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			predicate.setContents(axioms[i].getContents(monitor), monitor);
		}
		ITheorem[] theorems = contextCache.getNewTheorems();
		if(axioms.length > 0 && theorems.length > 0) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getHypSetName(theorems[0].getElementName()), null, monitor);
			predicateSet.setContents(contextCache.getHypSetName(axioms[axioms.length-1].getElementName()), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			predicate.setContents(axioms[axioms.length-1].getContents(monitor), monitor);
		}
		for(int i=0; i<theorems.length-1; i++) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getHypSetName(theorems[i+1].getElementName()), null, monitor);
			predicateSet.setContents(contextCache.getHypSetName(theorems[i].getElementName()), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
			predicate.setContents(theorems[i].getContents(monitor), monitor);
		}
	}
	
	private void createGlobalTypeEnvironment() throws CoreException {
		for(ISCConstant identifier : contextCache.getSCConstants()) {
			IPOIdentifier newIdentifier = (IPOIdentifier) poFile.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identifier.getElementName(), null, monitor);
			newIdentifier.setContents(identifier.getContents(), monitor);
		}
		for(ISCCarrierSet identifier : contextCache.getSCCarrierSets()) {
			IPOIdentifier newIdentifier = (IPOIdentifier) poFile.createInternalElement(IPOIdentifier.ELEMENT_TYPE, identifier.getElementName(), null, monitor);
			newIdentifier.setContents(identifier.getContents(), monitor);
		}
	}
	
	private void createProofObligations() throws CoreException {
		for(ProofObligation obligation : poList) {
			obligation.put(poFile, monitor);
		}
	}
	
}
