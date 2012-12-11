/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added theorem attribute
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCParameter;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventGuardModule extends PredicateWithTypingModule<IGuard> {

	public static final IModuleType<MachineEventGuardModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventGuardModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		if (formulaElements.length == 0)
			return;

		if (!checkInitialisation(element, monitor))
			return;
		
		checkAndType(element.getElementName(), repository, monitor);

		if (target != null) {
			ISCEvent targetEvent = (ISCEvent) target;
			createSCPredicates(targetEvent, monitor);
		}
	}

	private boolean checkInitialisation(IRodinElement event,
			IProgressMonitor monitor) throws RodinDBException {
		if (((ILabeledElement) event).getLabel().equals(IEvent.INITIALISATION))
			if (formulaElements.length > 0) {
				for (IGuard guard : formulaElements)
					createProblemMarker(guard, getFormulaAttributeType(),
							GraphProblem.InitialisationGuardError);
				return false;
			}
		return true;
	}

	protected IConcreteEventInfo refinedEventTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.LabeledFormulaModule#typeCheckFormula
	 * (int, org.rodinp.core.IInternalElement[], org.eventb.core.ast.Formula[],
	 * org.eventb.core.ast.ITypeEnvironment)
	 */
	@Override
	protected ITypeEnvironment typeCheckFormula(IGuard formulaElement,
			Predicate formula, ITypeEnvironment typeEnvironment)
			throws CoreException {

		// local variables must not change their type from an abstract machine
		// to a concrete machine

		ITypeEnvironment inferredTypeEnvironment = super.typeCheckFormula(
				formulaElement, formula, typeEnvironment);

		if (inferredTypeEnvironment == null)
			return null;

		boolean ok = true;

		ITypeEnvironment.IIterator iterator = inferredTypeEnvironment
				.getIterator();

		List<IAbstractEventInfo> eventInfos = refinedEventTable
				.getAbstractEventInfos();
		IAbstractEventInfo eventInfo = eventInfos.size() > 0 ? refinedEventTable
				.getAbstractEventInfos().get(0)
				: null;

		while (iterator.hasNext()) {
			iterator.advance();
			String name = iterator.getName();
			Type type = iterator.getType();

			if (eventInfo != null) {

				FreeIdentifier identifier = eventInfo.getParameter(name);

				if (identifier == null || identifier.getType().equals(type))
					continue;

				ok = false;

				createProblemMarker(formulaElement, getFormulaAttributeType(),
						GraphProblem.ParameterChangedTypeError, name, type
								.toString(), identifier.getType().toString());
			}
		}

		return ok ? inferredTypeEnvironment : null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		refinedEventTable = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		refinedEventTable = null;
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		// no progress inside event
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eventb.internal.core.sc.modules.LabeledElementModule#
	 * getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(IEventLabelSymbolTable.STATE_TYPE);
	}

	private boolean allParameters(ITypeEnvironment typeEnvironment,
			IInternalElement formulaElement) throws CoreException {
		boolean ok = true;
		ITypeEnvironment.IIterator iterator = typeEnvironment.getIterator();
		while (iterator.hasNext()) {
			iterator.advance();
			IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
					.getSymbolInfoFromTop(iterator.getName());
			if (symbolInfo.getSymbolType() == ISCParameter.ELEMENT_TYPE) {
				continue;
			}
			ok = false;
			createProblemMarker(formulaElement, getFormulaAttributeType(),
					GraphProblem.UntypedIdentifierError, iterator.getName());
		}
		return ok;
	}

	@Override
	protected boolean updateIdentifierSymbolTable(
			IInternalElement formulaElement,
			ITypeEnvironment inferredEnvironment,
			ITypeEnvironmentBuilder typeEnvironment) throws CoreException {

		if (allParameters(inferredEnvironment, formulaElement))
			return super.updateIdentifierSymbolTable(formulaElement,
					inferredEnvironment, typeEnvironment);
		else
			return false;

	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		ILabelSymbolInfo info = SymbolFactory.getInstance().makeLocalGuard(symbol, true, element,
				component);
		info.setAttributeValue(EventBAttributes.THEOREM_ATTRIBUTE, false);
		return info;
	}

	@Override
	protected IGuard[] getFormulaElements(IRodinElement element)
			throws CoreException {
		IEvent event = (IEvent) element;
		return event.getGuards();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository)
			throws CoreException {
		return (IAccuracyInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
	}

}
