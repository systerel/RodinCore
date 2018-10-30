/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added PO nature
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import static org.eventb.internal.core.pog.modules.FwdMachineVariantModule.FiniteTypeMatcher.isFinite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineVariantInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.MachineVariantInfo;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineVariantModule extends UtilityModule {
	
	public static final IModuleType<FwdMachineVariantModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineVariantModule"); //$NON-NLS-1$
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		if (!variantInfo.machineHasVariant())
			return;
		
		IPORoot target = repository.getTarget();
		
		IRodinElement source = variantInfo.getVariant().getSource();
		Expression expression = variantInfo.getExpression();

		Predicate wdPredicate = expression.getWDPredicate();
		IPOGSource[] sources = new IPOGSource[] {
				makeSource(IPOSource.DEFAULT_ROLE, source)
		};
		if (!isTrivial(wdPredicate)) {
			createPO(
					target, 
					"VWD", 
					IPOGNature.VARIANT_WELL_DEFINEDNESS, 
					machineHypothesisManager.getFullHypothesis(), 
					emptyPredicates, 
					makePredicate(wdPredicate, source), 
					sources, 
					NO_HINTS, 
					machineHypothesisManager.machineIsAccurate(),
					monitor);
		} else {
			if (DEBUG_TRIVIAL)
				debugTraceTrivial("VWD");
		}
		
		if (mustProveFinite(expression)) {
			Predicate finPredicate = 
				factory.makeSimplePredicate(Formula.KFINITE, expression, null);
			createPO(
					target, 
					"FIN", 
					IPOGNature.VARIANT_FINITENESS, 
					machineHypothesisManager.getFullHypothesis(), 
					emptyPredicates, 
					makePredicate(finPredicate, source), 
					sources, 
					NO_HINTS, 
					machineHypothesisManager.machineIsAccurate(),
					monitor);
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
			repository.getState(IMachineHypothesisManager.STATE_TYPE);
		
		IRodinFile machineFile = (IRodinFile) element;
		ISCMachineRoot machineRoot = (ISCMachineRoot) machineFile.getRoot();
		
		ISCVariant[] variants = machineRoot.getSCVariants();
		if (variants.length == 0) {
			variantInfo = new MachineVariantInfo(null, null);
		} else {
			ISCVariant variant = variants[0];
			Expression expression = variant.getExpression(typeEnvironment);
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
	
	private boolean mustProveFinite(Expression expression) {
		Type type = expression.getType();
		return !(type instanceof IntegerType) && !isFinite(type);
	}

	// Recognize that a type is finite by construction.
	static class FiniteTypeMatcher implements ITypeVisitor {

		public static boolean isFinite(Type type) {
			FiniteTypeMatcher matcher = new FiniteTypeMatcher();
			type.accept(matcher);
			return matcher.result;
		}

		// Can only change to false during traversal
		private boolean result = true;

		@Override
		public void visit(BooleanType type) {
			// OK
		}

		@Override
		public void visit(GivenType type) {
			// Can be infinite
			result = false;
		}

		@Override
		public void visit(IntegerType type) {
			// Is infinite
			result = false;
		}

		@Override
		public void visit(ParametricType type) {
			// Can be infinite, e.g. List(BOOL)
			result = false;
		}

		@Override
		public void visit(PowerSetType type) {
			if (result)
				type.getBaseType().accept(this);
		}

		@Override
		public void visit(ProductType type) {
			if (result)
				type.getLeft().accept(this);
			if (result)
				type.getRight().accept(this);
		}
	}

}
