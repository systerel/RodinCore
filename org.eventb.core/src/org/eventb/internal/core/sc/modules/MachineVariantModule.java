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
 *     Systerel - got factory from repository
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static java.util.Arrays.stream;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.NoConvergentEventButVariantWarning;

import java.util.Objects;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.IVariantPresentInfo;
import org.eventb.core.sc.state.IVariantUsedInfo;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.VariantPresentInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineVariantModule extends ExpressionModule<IVariant> {

	public static final IModuleType<MachineVariantModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineVariantModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	IVariantPresentInfo variantPresent;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		variantPresent = new VariantPresentInfo();
		repository.setState(variantPresent);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		checkForRedundantVariant(repository);

		variantPresent = null;
		super.endModule(element, repository, monitor);
	}

	private void checkForRedundantVariant(ISCStateRepository repository)
			throws CoreException {

		if (!variantPresent.isTrue()) {
			return;
		}
		
		IVariantUsedInfo variantUsed = repository.getState(IVariantUsedInfo.STATE_TYPE);
		if (variantUsed.isTrue()) {
			return;
		}

		for (int i = 0; i < formulaElements.length; ++i) {
			if (formulas[i] != null) {
				createProblemMarker(formulaElements[i], EXPRESSION_ATTRIBUTE, //
						NoConvergentEventButVariantWarning);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement
	 * , org.rodinp.core.IInternalElement, org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		if (formulaElements.length == 0) {
			variantPresent.set(false);
			variantPresent.makeImmutable();
			return;
		}

		monitor.subTask(Messages.bind(Messages.progress_MachineVariant));

		checkAndType(element.getElementName(), repository, monitor);

		variantPresent.set(stream(formulas).anyMatch(Objects::nonNull));
		variantPresent.makeImmutable();

		createSCExpressions(target, monitor);
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ITypeEnvironment typeCheckFormula(IVariant formulaElement,
			Expression formula, ITypeEnvironment typeEnvironment)
			throws CoreException {
		ITypeEnvironment inferredEnvironment = super.typeCheckFormula(
				formulaElement, formula, typeEnvironment);
		if (inferredEnvironment == null)
			return null;
		else {
			Expression expression = formula;
			Type type = expression.getType();
			boolean ok = type instanceof IntegerType
					|| type.getBaseType() != null;
			if (!ok) {
				createProblemMarker(formulaElement, getFormulaAttributeType(),
						GraphProblem.InvalidVariantTypeError, type.toString());
				return null;
			} else
				return inferredEnvironment;
		}
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		SymbolFactory sf = SymbolFactory.getInstance();
		return sf.makeLocalVariant(symbol, true, element, component);
	}

	@Override
	protected IVariant[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IRodinFile machineFile = (IRodinFile) element;
		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();
		return machineRoot.getVariants();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return null;
	}

}
