/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GlobalHypothesesModule extends UtilityModule {

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		index = 0;
		
		typeEnvironment = repository.getTypeEnvironment();
	}
	
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

	private static String PRD_NAME_PREFIX = "PRD";
	protected ITypeEnvironment typeEnvironment;
	protected int index;

	protected void copyContexts(
			IPOPredicateSet rootSet, 
			ISCInternalContext[] contexts, 
			IProgressMonitor monitor) throws RodinDBException {
		
		for (ISCInternalContext context : contexts) {
			
			fetchCarriersSetsAndConstants(context, rootSet, monitor);
			
			for (ISCAxiom axiom : context.getSCAxioms()) {
				savePOPredicate(rootSet, axiom, monitor);
			}
		}
		
	}

	protected void fetchCarriersSetsAndConstants(
			ISCContext context, 
			IPOPredicateSet rootSet, 
			IProgressMonitor monitor) throws RodinDBException {
		for (ISCCarrierSet set : context.getSCCarrierSets()) {
			FreeIdentifier identifier = fetchIdentifier(set);
			createIdentifier(rootSet, identifier, monitor);
		}
		for (ISCConstant constant : context.getSCConstants()) {
			FreeIdentifier identifier = fetchIdentifier(constant);
			createIdentifier(rootSet, identifier, monitor);
		}
	}

	protected void createIdentifier(
			IPOPredicateSet predSet, 
			FreeIdentifier identifier, 
			IProgressMonitor monitor) throws RodinDBException {
		String idName = identifier.getName();
		Type type = identifier.getType();
		IPOIdentifier poIdentifier = predSet.getIdentifier(idName);
		poIdentifier.create(null, monitor);
		poIdentifier.setType(type, monitor);
	}

	protected FreeIdentifier fetchIdentifier(ISCIdentifierElement ident) throws RodinDBException {
		FreeIdentifier identifier = ident.getIdentifier(factory);
		typeEnvironment.add(identifier);
		return identifier;
	}

	protected void savePOPredicate(IPOPredicateSet rootSet, ISCPredicateElement element, IProgressMonitor monitor) throws RodinDBException {
		IPOPredicate predicate = rootSet.getPredicate(PRD_NAME_PREFIX + index++);
		predicate.create(null, monitor);
		predicate.setPredicateString(element.getPredicateString(), monitor);
		predicate.setSource(((ITraceableElement) element).getSource(), monitor);
	}

	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository,
			IProgressMonitor monitor)
		throws CoreException {
		
		// all is done in the initialisation part

	}

}
