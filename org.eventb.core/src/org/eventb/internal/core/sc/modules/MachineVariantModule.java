/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCVariant;
import org.eventb.core.IVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.ILabelSymbolTable;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.IVariantInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.VariantInfo;
import org.eventb.internal.core.sc.symbolTable.VariantSymbolInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantModule extends ExpressionModule {

	public static final String MACHINE_VARIANT_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineVariantAcceptor";

	private final IAcceptorModule[] modules;

	public MachineVariantModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = 
			manager.getAcceptorModules(MACHINE_VARIANT_ACCEPTOR);
	}

	private static String VARIANT_NAME_PREFIX = "VAR";

	IVariantInfo variantInfo;
	FormulaFactory factory;
	
	@Override
	public void initModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		variantInfo = new VariantInfo();
		factory = repository.getFormulaFactory();
		repository.setState(variantInfo);
	}

	@Override
	public void endModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		variantInfo = null;
		factory = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected ILabelSymbolInfo fetchLabel(
			IInternalElement internalElement, 
			String component, 
			IProgressMonitor monitor) throws CoreException {
		return new VariantSymbolInfo("VARIANT", internalElement, component);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(IRodinElement element, IInternalParent target,
			IStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;
		
		IVariant variant = machineFile.getVariant();
		
		if (variant == null)
			return;
		
		IVariant[] variants = new IVariant[] { 
				variant
		};
		
		Expression[] expressions = new Expression[1];

		monitor.subTask(Messages.bind(Messages.progress_MachineVariant));
		
		checkAndType(
				variants, 
				target,
				expressions,
				modules,
				machineFile.getElementName(),
				repository,
				monitor);
		
		saveVariant(target, variants[0], expressions[0], null);

	}

	private void saveVariant(
			IInternalParent parent, 
			IVariant variant, 
			Expression expression,
			IProgressMonitor monitor) throws RodinDBException {
		
		if (expression == null)
			return;
		
		variantInfo.setExpression(expression);
		
		ISCVariant scAxiom = 
			(ISCVariant) parent.createInternalElement(
					ISCVariant.ELEMENT_TYPE, 
					VARIANT_NAME_PREFIX, 
					null, 
					monitor);
		scAxiom.setExpression(expression);
		scAxiom.setSource(variant, monitor);
	}
	
	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IStateRepository repository) throws CoreException {
		// this method is never called because fetchLabel() is overriden
		return null;
	}

	@Override
	protected ITypeEnvironment typeCheckFormula(
			IInternalElement formulaElement, 
			Formula formula, 
			ITypeEnvironment typeEnvironment) throws CoreException {
		ITypeEnvironment inferredEnvironment =
			super.typeCheckFormula(formulaElement, formula, typeEnvironment);
		if (inferredEnvironment == null)
			return null;
		else {
			Expression expression = (Expression) formula;
			Type type = expression.getType();
			boolean ok = type.equals(factory.makeIntegerType()) || type.getBaseType() != null;
			if (!ok) {
				createProblemMarker(
						formulaElement, 
						getFormulaAttributeId(), 
						GraphProblem.InvalidVariantTypeError, 
						type.toString());
				return null;
			} else
				return inferredEnvironment;
		}
	}

}
