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
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.util.POGSource;
import org.eventb.core.pog.util.POGTraceablePredicate;
import org.eventb.internal.core.pog.MachineVariantInfo;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantModule extends UtilityModule {
	
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		if (!variantInfo.machineHasVariant())
			return;
		
		IPOFile target = repository.getTarget();
		
		Predicate wdPredicate = variantInfo.getExpression().getWDPredicate(factory);
		POGSource[] sources = sources(new POGSource(IPOSource.DEFAULT_ROLE, variantInfo.getVariant()));
		if (!goalIsTrivial(wdPredicate)) {
			createPO(
					target, 
					"VWD", 
					"Well-definedness of variant", 
					machineHypothesisManager.getFullHypothesis(), 
					emptyPredicates, 
					new POGTraceablePredicate(wdPredicate, variantInfo.getVariant()), 
					sources, 
					emptyHints, monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial("VWD");
		}
		
		if (mustProveFinite()) {
			Predicate finPredicate = 
				factory.makeSimplePredicate(Formula.KFINITE, variantInfo.getExpression(), null);
			createPO(
					target, 
					"FIN", 
					"Finiteness of variant", 
					machineHypothesisManager.getFullHypothesis(), 
					emptyPredicates, 
					new POGTraceablePredicate(finPredicate, variantInfo.getVariant()), 
					sources, 
					emptyHints, monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial("FIN");
		}
		
	}
	
	protected IMachineVariantInfo variantInfo;
	protected ITypeEnvironment typeEnvironment;
	protected IMachineHypothesisManager machineHypothesisManager;
	
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		typeEnvironment = repository.getTypeEnvironment();
		
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
		
		ISCMachineFile machineFile = (ISCMachineFile) element;
		ISCVariant[] variants = machineFile.getSCVariants();
		if (variants.length == 0) {
			variantInfo = new MachineVariantInfo(null, null);
		} else {
			ISCVariant variant = variants[0];
			Expression expression = variant.getExpression(factory, typeEnvironment);
			variantInfo = new MachineVariantInfo(expression, variant);
		}
		repository.setState(variantInfo);
	}
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		variantInfo = null;
		typeEnvironment = null;
		machineHypothesisManager = null;
		super.endModule(element, repository, monitor);
	}
	
	private boolean mustProveFinite() {
		Type type = variantInfo.getExpression().getType();
		if (type.equals(factory.makeIntegerType()))
			return false;
		if (derivedFromBoolean(type))
			return false;
		return true;
	}

	private boolean derivedFromBoolean(Type type) {
		if (type.equals(factory.makeBooleanType()))
			return true;
		Type baseType = type.getBaseType();
		if (baseType != null)
			return derivedFromBoolean(baseType);
		if (type instanceof ProductType) {
			ProductType productType = (ProductType) type;
			return derivedFromBoolean(productType.getLeft()) 
				&& derivedFromBoolean(productType.getRight());
		}
		return false;
	}
	
}
