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
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.protosc.ContextSC;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextPOG implements IAutomaticTool, IExtractor {

//	private IInterrupt interrupt;
	private IProgressMonitor monitor;

	private ISCContextFile scContext;
	private IPOFile poFile;
	
	private ContextRuleBase ruleBase;
	
	private SCContextCache contextCache;
	
	private List<ProofObligation> poList;

	public void init(
			@SuppressWarnings("hiding") ISCContextFile scContext, 
			@SuppressWarnings("hiding") IPOFile poFile, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) {
//		this.interrupt = interrupt;
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
		ISCContextFile contextIn = newPOFile.getSCContext();
		
		if (! contextIn.exists())
			ContextSC.makeError("Source SC context does not exist.");
		
		init(contextIn, newPOFile, monitor);
		run();
		return true;
	}

	public void clean(IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		
		ISCContextFile in = (ISCContextFile) RodinCore.create(file);
		IPOFile target = in.getContextFile().getPOFile();
		
		graph.openGraph();
		graph.addNode(target.getResource(), POGCore.CONTEXT_POG_TOOL_ID);
		graph.putToolDependency(in.getResource(), target.getResource(), POGCore.CONTEXT_POG_TOOL_ID, true);
		graph.closeGraph();
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
		
		IInternalElement element = poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getOldHypSetName(), null, monitor);
//		for(IAxiom axiom : contextCache.getOldAxioms()) {
//			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
//			predicate.setContents(axiom.getContents(), monitor);
//		}
//		for(ITheorem theorem : contextCache.getOldTheorems()) {
//			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, null, null, monitor);
//			predicate.setContents(theorem.getContents(), monitor);
//		}
		int nameIdx = 1;
		for(ISCCarrierSet carrierSet : contextCache.getSCCarrierSets()) {
			String name = "hypCS" + nameIdx++;
			IPOPredicate predicate = (IPOPredicate) element.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, monitor);
			Predicate pp = factory.makeRelationalPredicate(
					Formula.NOTEQUAL, 
					carrierSet.getIdentifier(factory), 
					factory.makeAtomicExpression(Formula.EMPTYSET, null), 
					null);
			predicate.setContents(pp.toString(), monitor);
		}
		ISCAxiom[] axioms = contextCache.getNewAxioms();
		for(int i=0; i<axioms.length-1; i++) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getHypSetName(axioms[i+1].getElementName()), null, monitor);
			predicateSet.setContents(contextCache.getHypSetName(axioms[i].getElementName()), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, "p1", null, monitor);
			predicate.setContents(axioms[i].getContents(monitor), monitor);
		}
		ISCTheorem[] theorems = contextCache.getNewTheorems();
		if(axioms.length > 0 && theorems.length > 0) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getHypSetName(theorems[0].getElementName()), null, monitor);
			predicateSet.setContents(contextCache.getHypSetName(axioms[axioms.length-1].getElementName()), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, "p1", null, monitor);
			predicate.setContents(axioms[axioms.length-1].getContents(monitor), monitor);
		}
		for(int i=0; i<theorems.length-1; i++) {
			IPOPredicateSet predicateSet = (IPOPredicateSet) poFile.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, contextCache.getHypSetName(theorems[i+1].getElementName()), null, monitor);
			predicateSet.setContents(contextCache.getHypSetName(theorems[i].getElementName()), monitor);
			IPOPredicate predicate = (IPOPredicate) predicateSet.createInternalElement(IPOPredicate.ELEMENT_TYPE, "p1", null, monitor);
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

	public void remove(IFile file, IFile origin, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
}
