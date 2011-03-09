/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - got factory from repository
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.VariantInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineVariantModule extends ExpressionModule<IVariant> {

	private static final int CVG_CODE = IConvergenceElement.Convergence.CONVERGENT
			.getCode();

	public static final IModuleType<MachineVariantModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineVariantModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static String VARIANT_NAME_PREFIX = "VAR";

	VariantInfo variantInfo;
	FormulaFactory factory;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		variantInfo = new VariantInfo();
		factory = repository.getFormulaFactory();
		repository.setState(variantInfo);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		checkForRedundantVariant(repository);

		variantInfo = null;
		factory = null;
		super.endModule(element, repository, monitor);
	}

	private void checkForRedundantVariant(ISCStateRepository repository)
			throws CoreException, RodinDBException {
		IConcreteEventTable concreteEventTable = (IConcreteEventTable) repository
				.getState(IConcreteEventTable.STATE_TYPE);

		boolean noCvgEvent = true;
		for (IConcreteEventInfo info : concreteEventTable) {
			ILabelSymbolInfo symbolInfo = info.getSymbolInfo();
			if (symbolInfo.hasError())
				continue;
			else {
				int cvg = symbolInfo
						.getAttributeValue(EventBAttributes.CONVERGENCE_ATTRIBUTE);
				if (cvg == CVG_CODE) {
					List<IAbstractEventInfo> infoList = info
							.getAbstractEventInfos();
					boolean nc = true;
					if (infoList.size() != 0) {
						for (IAbstractEventInfo absInfo : infoList) {
							nc &= absInfo.getConvergence() != IConvergenceElement.Convergence.CONVERGENT;
						}
					}
					if (nc) {
						noCvgEvent = false;
						break;
					}
				}
			}
		}
		if (noCvgEvent && variantInfo.getExpression() != null) {
			createProblemMarker(formulaElements[0],
					EventBAttributes.EXPRESSION_ATTRIBUTE,
					GraphProblem.NoConvergentEventButVariantWarning);
		}
	}

	@Override
	protected ILabelSymbolInfo fetchLabel(IInternalElement internalElement,
			String component, IProgressMonitor monitor) throws CoreException {
		ILabelSymbolInfo symbolInfo = SymbolFactory.getInstance()
				.makeLocalVariant("VARIANT", true, internalElement, component);
		symbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
				internalElement);
		return symbolInfo;
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
			variantInfo.makeImmutable();
			return;
		}

		if (formulaElements.length > 1) {
			for (int k = 1; k < formulaElements.length; k++)
				createProblemMarker(formulaElements[k],
						EventBAttributes.EXPRESSION_ATTRIBUTE,
						GraphProblem.TooManyVariantsError);
		}

		monitor.subTask(Messages.bind(Messages.progress_MachineVariant));

		checkAndType(element.getElementName(), repository, monitor);

		variantInfo.setExpression(formulas[0]);
		variantInfo.makeImmutable();

		createSCExpressions(target, VARIANT_NAME_PREFIX, 0, monitor);

	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		// this method is never called because fetchLabel() is overriden
		return null;
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
			boolean ok = type.equals(factory.makeIntegerType())
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
		throw new UnsupportedOperationException();
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
