/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContext;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ITheorem;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.protosc.SCCore;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;
import org.rodinp.core.builder.IInterrupt;

/**
 * @author halstefa
 *
 */
public class ContextSC extends CommonSC implements IAutomaticTool, IExtractor {
	
	@SuppressWarnings("unused")
	private IInterrupt interrupt;
	private IProgressMonitor monitor;

	@SuppressWarnings("unused")
	private IContext context;
	private ISCContext scContext;
	
	private ContextRuleBase ruleBase;

	private ContextCache contextCache;
	private HashMap<String, Predicate> axiomPredicateMap;
	private HashMap<String, Predicate> theoremPredicateMap;
	
	/**
	 * @param scContext TODO
	 * 
	 */
	public void init(
			@SuppressWarnings("hiding") IContext context, 
			@SuppressWarnings("hiding") ISCContext scContext, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws RodinDBException {
		this.interrupt = interrupt;
		this.monitor = monitor;
		this.context = context;
		this.scContext = scContext;
		this.ruleBase = new ContextRuleBase();
		contextCache = new ContextCache(context, this);
		axiomPredicateMap = new HashMap<String, Predicate>(contextCache.getAxioms().length * 4 / 3 + 1);
		theoremPredicateMap = new HashMap<String, Predicate>(contextCache.getTheorems().length * 4 / 3 + 1);
		problems.clear();
	}
	
	public boolean run(
			IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt,
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		
		if(DEBUG)
			System.out.println(getClass().getName() + " running.");
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
//		file.delete(true, monitor); // dangerous ?
		
		ISCContext newSCContext = (ISCContext) RodinCore.create(file);
//		newSCContext.open(monitor);
		
		// TODO: the explicit file extension should be replaced by a request to the content type manager
		IFile contextFile = workspace.getRoot().getFile(file.getFullPath().removeFileExtension().addFileExtension("buc"));
		
		IContext contextIn = (IContext) RodinCore.create(contextFile);
		if(!contextIn.exists())
			ContextSC.makeError("Source context does not exist.");
		contextIn.open(monitor);
		
		
		init(contextIn, newSCContext, interrupt, monitor);
		
		runSC();
		
		contextIn.close();
		newSCContext.close();
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IProducer#clean(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IInterrupt, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(
			IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt,
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);
	}
	
	public void extract(IFile file, IGraph graph) throws CoreException {
		// the prototype does not have refinements
		IPath target = file.getFullPath().removeFileExtension().addFileExtension("bcc");
		graph.addNode(target, SCCore.CONTEXT_SC_TOOL_ID);
		IPath[] paths = graph.getDependencies(target, SCCore.CONTEXT_SC_TOOL_ID);
		if(paths.length == 1 && paths[0].equals(target))
			return;
		else {
			graph.removeDependencies(target, SCCore.CONTEXT_SC_TOOL_ID);
			graph.addToolDependency(file.getFullPath(), target, SCCore.CONTEXT_SC_TOOL_ID, true);
		}
		
	}

	public void runSC() throws CoreException {
		
		commitCarrierSets();
		commitConstants();
		commitAxioms();
		retractUntypedConstants();
		commitTheorems();
		
//		createCheckedContext();
		
		issueProblems(context);
	}
	
	private void commitCarrierSets() throws RodinDBException {
		List<IContextRule> rules = ruleBase.getCarrierSetRules();
		for(ICarrierSet carrierSet : contextCache.getCarrierSets()) {
			boolean verified = true;
			for(IContextRule rule : rules) {
				verified = rule.verify(carrierSet, contextCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				// TODO: adapt to new type management
				String elementName = carrierSet.getElementName();
				contextCache.getNewCarrierSets().put(elementName, carrierSet);				
				contextCache.getTypeEnvironment().addGivenSet(elementName);	
			}
		}
	}
	
	private void commitConstants() throws RodinDBException {
		List<IContextRule> rules = ruleBase.getConstantRules();
		for(IConstant constant : contextCache.getConstants()) {
			boolean verified = true;
			for(IContextRule rule : rules) {
				verified = rule.verify(constant, contextCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				String elementName = constant.getElementName();
				contextCache.getNewConstants().put(elementName, constant);	
			}
		}
	}
	
	private void commitAxioms() throws RodinDBException {
		List<IContextRule> rules = ruleBase.getAxiomRules();
		for(IAxiom axiom : contextCache.getAxioms()) {
			boolean verified = true;
			for(IContextRule rule : rules) {
				verified = rule.verify(axiom, contextCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				SCParser parser = parseAndVerifyPredicate(axiom);
				if(parser != null) {
					contextCache.getNewAxioms().add(axiom);	
					contextCache.setTypeEnvironment(parser.getTypeEnvironment());
					axiomPredicateMap.put(axiom.getElementName(), parser.getPredicate());
				}
			}
		}
	}
	
	private void retractUntypedConstants() {
		String[] constants = new String[contextCache.newConstants.size()];
		contextCache.newConstants.keySet().toArray(constants);
		for(String name : constants) {
			if(!contextCache.getTypeEnvironment().contains(contextCache.getConstantIdentMap().get(name))) {
				addProblem(contextCache.newConstants.get(name), "Constant does not have a type.", SCProblem.SEVERITY_ERROR);
				contextCache.newConstants.remove(name);
			}
		}
	}
	
	private void commitTheorems() throws RodinDBException {
		List<IContextRule> rules = ruleBase.getTheoremRules();
		for(ITheorem theorem : contextCache.getTheorems()) {
			boolean verified = true;
			for(IContextRule rule : rules) {
				verified = rule.verify(theorem, contextCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				SCParser parser = parseAndVerifyPredicate(theorem);
				if(parser != null) {
					contextCache.getNewTheorems().add(theorem);
					theoremPredicateMap.put(theorem.getElementName(), parser.getPredicate());
				}
			}
		}
	}
	
	protected SCParser parseAndVerifyPredicate(IInternalElement element) {
		Collection<FreeIdentifier> declaredIdentifiers = new HashSet<FreeIdentifier>();
		declaredIdentifiers.addAll(makeIdentifiers(contextCache.getOldCarrierSets().keySet(), contextCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(contextCache.getOldConstants().keySet(), contextCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(contextCache.getNewCarrierSets().keySet(), contextCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(contextCache.getNewConstants().keySet(), contextCache.getFactory()));
		try {
			SCParser parser = new SCParser(contextCache.getTypeEnvironment(), declaredIdentifiers, contextCache.getFactory());
			if(parser.parsePredicate(element, this)) {
				
				FreeIdentifier[] freeIdentifiers = parser.getPredicate().getFreeIdentifiers();
				
				ArrayList<String> unboundList = new ArrayList<String>(freeIdentifiers.length);
				
				boolean allContained = true;
				for(FreeIdentifier identifier : freeIdentifiers) {
					boolean contained = false;
					String name = identifier.getName();
					contained |= contextCache.getOldCarrierSets().containsKey(name);
					contained |= contextCache.getNewCarrierSets().containsKey(name);
					contained |= contextCache.getOldConstants().containsKey(name);
					contained |= contextCache.getNewConstants().containsKey(name);
					allContained &= contained;
					if(!contained)
						unboundList.add(name);
				}
				if(allContained) {
					return parser;
				} else {
					assert unboundList.size() > 0;
					String result = unboundList.get(0);
					for(int i=1; i< unboundList.size(); i++) {
						result += "," + unboundList.get(i); //$NON-NLS-1$
					}
					addProblem(element, "Undeclared identifiers in predicate:" + result, SCProblem.SEVERITY_ERROR);
				}
			}
		} catch (RodinDBException e) {
			logMessage(e, "Cannot access contents of element" + element.getElementName());
		}
		// in this case we cannot accept the axiom as well-formed
		return null;
	}
	
	private void createCheckedContext() throws RodinDBException {
		
		createDeclarations(contextCache.getOldCarrierSets().values(), contextCache.getCarrierSetIdentMap());
		createDeclarations(contextCache.getNewCarrierSets().values(), contextCache.getCarrierSetIdentMap());
		createDeclarations(contextCache.getOldConstants().values(), contextCache.getConstantIdentMap());
		createDeclarations(contextCache.getNewConstants().values(), contextCache.getConstantIdentMap());
		
		ISCAxiomSet axiomSet = (ISCAxiomSet) scContext.createInternalElement(ISCAxiomSet.ELEMENT_TYPE, "CONTEXT", null, monitor);
		createFormulas(axiomSet, contextCache.getOldAxioms());
		
		ISCTheoremSet theoremSet = (ISCTheoremSet) scContext.createInternalElement(ISCTheoremSet.ELEMENT_TYPE, "CONTEXT", null, monitor);
		createFormulas(theoremSet, contextCache.getOldTheorems());
		
		createFormulas(scContext, contextCache.getNewAxioms());
		createFormulas(scContext, contextCache.getNewTheorems());
		
	}
	
	private void createFormulas(IInternalParent parent, Collection<? extends IInternalElement> elements) throws RodinDBException {
		for(IInternalElement element : elements) {
			IInternalElement newElement = parent.createInternalElement(element.getElementType(), element.getElementName(), null, monitor);
			String newContents = (element.getElementType().equals(IAxiom.ELEMENT_TYPE)) ? 
					axiomPredicateMap.get(element.getElementName()).toString() :
					theoremPredicateMap.get(element.getElementName()).toString();
			newElement.setContents(newContents);
			// TODO: set origin attribute of new element
		}
	}
	
	String getCorrespondingElementType(String type) {
		if(type.equals(IConstant.ELEMENT_TYPE))
			return ISCConstant.ELEMENT_TYPE;
		else if(type.equals(ICarrierSet.ELEMENT_TYPE))
			return ISCCarrierSet.ELEMENT_TYPE;
		else 
			return "?";
	}
	
	private void createDeclarations(Collection<? extends IInternalElement> elements, HashMap<String, String> identMap) throws RodinDBException {
		for(IInternalElement element : elements) {
			IInternalElement newElement = scContext.createInternalElement(getCorrespondingElementType(element.getElementType()), element.getElementName(), null, monitor);
			// TODO: set origin attribute of new element
			newElement.setContents(contextCache.getTypeEnvironment().getType(element.getElementName()).toString());
//			IPOIdentifier identifier = (IPOIdentifier) scContext.createInternalElement(IPOIdentifier.ELEMENT_TYPE, element.getElementName(), null, monitor);
//			identifier.setContents(contextCache.getTypeEnvironment().getType(element.getElementName()).toString());
		}
	}

}
