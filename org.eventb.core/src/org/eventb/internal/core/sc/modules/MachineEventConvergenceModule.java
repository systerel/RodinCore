/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.CONVERGENT;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;
import static org.eventb.core.sc.GraphProblem.ConvergenceUndefError;
import static org.eventb.core.sc.GraphProblem.ConvergentEventNoVariantWarning;
import static org.eventb.core.sc.GraphProblem.FaultyAbstractConvergenceAnticipatedWarning;
import static org.eventb.core.sc.GraphProblem.FaultyAbstractConvergenceOrdinaryWarning;
import static org.eventb.core.sc.GraphProblem.FaultyAbstractConvergenceUnchangedWarning;
import static org.eventb.core.sc.GraphProblem.InitialisationNotOrdinaryWarning;
import static org.eventb.core.sc.GraphProblem.OrdinaryFaultyConvergenceWarning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.IVariantPresentInfo;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventConvergenceModule extends SCFilterModule {

	public static final IModuleType<MachineEventConvergenceModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventConvergenceModule"); //$NON-NLS-1$

	private IVariantPresentInfo variantPresent;
	private ILabelSymbolTable labelSymbolTable;
	private IConcreteEventTable concreteEventTable;

	private Convergence concreteCvg;
	private Convergence abstractCvg;

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ISCFilterModule#accept(org.rodinp.core.IRodinElement,
	 * org.eventb.core.sc.state.ISCStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IEvent event = (IEvent) element;

		if (event.hasConvergence()) {

			String eventLabel = event.getLabel();

			ILabelSymbolInfo eventSymbolInfo = labelSymbolTable
					.getSymbolInfo(eventLabel);
			IConcreteEventInfo eventInfo = concreteEventTable
					.getConcreteEventInfo(eventLabel);

			checkConvergence(eventInfo, eventSymbolInfo);
			return true;

		} else {
			createProblemMarker(event, CONVERGENCE_ATTRIBUTE,
					ConvergenceUndefError);
			return false;
		}
	}

	public void checkConvergence(IConcreteEventInfo concreteEventInfo,
			ILabelSymbolInfo eventSymbolInfo) throws CoreException {

		abstractCvg = null;
		concreteCvg = concreteEventInfo.getEvent().getConvergence();
		Convergence origConcreteCvg = concreteCvg;

		if (concreteEventInfo.isInitialisation()) {
			if (concreteCvg != ORDINARY) {
				concreteCvg = ORDINARY;
				createProblemMarker(concreteEventInfo.getEvent(),
						CONVERGENCE_ATTRIBUTE,
						InitialisationNotOrdinaryWarning);
			}
		} else {

			List<IAbstractEventInfo> abstractEventInfos = concreteEventInfo
					.getAbstractEventInfos();

			if (abstractEventInfos.size() != 0) { // not a new event

				checkAbstractConvergence(concreteEventInfo, abstractEventInfos);

			}
			checkVariantConvergence(concreteEventInfo);
		}

		if (concreteCvg != origConcreteCvg)
			concreteEventInfo.setNotAccurate();

		eventSymbolInfo.setAttributeValue(CONVERGENCE_ATTRIBUTE,
				concreteCvg.getCode());

	}

	private void checkAbstractConvergence(IConcreteEventInfo concreteEventInfo,
			List<IAbstractEventInfo> abstractEventInfos) throws CoreException {

		getAbstractConvergence(concreteEventInfo.getRefinesClauses(),
				abstractEventInfos);

		if (abstractCvg == ORDINARY && concreteCvg != ORDINARY) {
			createProblemMarker(concreteEventInfo.getEvent(),
					CONVERGENCE_ATTRIBUTE,
					OrdinaryFaultyConvergenceWarning,
					concreteEventInfo.getEventLabel());
			concreteCvg = ORDINARY;
		}
	}

	private void getAbstractConvergence(List<IRefinesEvent> refinesClauses,
			List<IAbstractEventInfo> abstractEventInfos)
			throws RodinDBException {

		List<Convergence> convergences = new ArrayList<>(3);

		for (IAbstractEventInfo abstractEventInfo : abstractEventInfos) {

			if (convergences.contains(abstractEventInfo.getConvergence()))
				continue;
			convergences.add(abstractEventInfo.getConvergence());

		}

		abstractCvg = Collections.min(convergences);

		if (convergences.size() > 1) {
			for (IRefinesEvent refinesEvent : refinesClauses) {
				String label = refinesEvent.getAbstractEventLabel();
				for (IAbstractEventInfo abstractEventInfo : abstractEventInfos) {
					if (abstractEventInfo.getEventLabel().equals(label)) {
						Convergence cvg = abstractEventInfo.getConvergence();
						GraphProblem problem = FaultyAbstractConvergenceUnchangedWarning;
						if (abstractCvg != cvg) {
							if (abstractCvg == ANTICIPATED)
								problem = FaultyAbstractConvergenceAnticipatedWarning;
							else if (abstractCvg == ORDINARY)
								problem = FaultyAbstractConvergenceOrdinaryWarning;
						}
						createProblemMarker(
								refinesEvent,
								TARGET_ATTRIBUTE,
								problem,
								label);
					}
				}
			}
		}
	}

	private void checkVariantConvergence(IConcreteEventInfo concreteEventInfo)
			throws CoreException {

		if (variantPresent.isTrue()) {
			return;
		}

		if (concreteCvg != CONVERGENT || abstractCvg == CONVERGENT) {
			return;
		}

		createProblemMarker(concreteEventInfo.getEvent(),
				CONVERGENCE_ATTRIBUTE,
				ConvergentEventNoVariantWarning,
				concreteEventInfo.getEventLabel());
		concreteCvg = ORDINARY;
	}

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		variantPresent = repository.getState(IVariantPresentInfo.STATE_TYPE);
		labelSymbolTable = repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
		concreteEventTable = repository.getState(IConcreteEventTable.STATE_TYPE);
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		variantPresent = null;
		super.endModule(repository, monitor);
	}

}
