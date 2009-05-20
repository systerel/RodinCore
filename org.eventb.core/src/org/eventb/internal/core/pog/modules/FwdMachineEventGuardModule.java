/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IAbstractEventGuardList;
import org.eventb.core.pog.state.IAbstractEventGuardTable;
import org.eventb.core.pog.state.IConcreteEventGuardTable;
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.pog.state.IHypothesisManager;
import org.eventb.core.pog.state.IMachineHypothesisManager;
import org.eventb.core.pog.state.IMachineInfo;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventGuardModule extends PredicateModule<ISCGuard> {

	public static final IModuleType<FwdMachineEventGuardModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventGuardModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.process(element, repository, monitor);
	}

	public static final String MACHINE_EVENT_GUARD_MODULE = 
		EventBPlugin.PLUGIN_ID + ".machineEventGuardModule";

	protected String eventLabel;
	protected IAbstractEventGuardList abstractEventGuardList;
	protected IMachineInfo machineInfo;
	protected IMachineHypothesisManager machineHypothesisManager;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		ISCEvent event = (ISCEvent) element;
		eventLabel = event.getLabel();
		machineInfo = (IMachineInfo) repository.getState(IMachineInfo.STATE_TYPE);
		abstractEventGuardList = 
			(IAbstractEventGuardList) repository.getState(IAbstractEventGuardList.STATE_TYPE);
		machineHypothesisManager =
			(IMachineHypothesisManager) repository.getState(IMachineHypothesisManager.STATE_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pog.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.IPOFile, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		eventLabel = null;
		machineInfo = null;
		abstractEventGuardList = null;
		machineHypothesisManager = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected IHypothesisManager getHypothesisManager(IPOGStateRepository repository) throws CoreException {
		return (IEventHypothesisManager) repository.getState(IEventHypothesisManager.STATE_TYPE);
	}

	@Override
	protected IPredicateTable<ISCGuard> getPredicateTable(IPOGStateRepository repository) throws CoreException {
		return (IConcreteEventGuardTable) repository.getState(IConcreteEventGuardTable.STATE_TYPE);
	}

	@Override
	protected void createWDProofObligation(
			IPORoot target, 
			String poPrefix, 
			ISCGuard predicateElement, 
			Predicate predicate, 
			int index,
			boolean isTheorem,
			IProgressMonitor monitor) throws CoreException {
		
		if (isRedundantWDProofObligation(predicate, index))
			return;
		
		super.createWDProofObligation(target, poPrefix, predicateElement,
				predicate, index, isTheorem, monitor);
	}
	
	private boolean isRedundantWDProofObligation(Predicate predicate, int index) {
		
		List<IAbstractEventGuardTable> abstractEventGuardTables = 
			abstractEventGuardList.getAbstractEventGuardTables();
		
		for (IAbstractEventGuardTable abstractEventGuardTable : abstractEventGuardTables) {
		
			if (isFreshPOForAbstractGuard(predicate, index, abstractEventGuardTable))
				continue;
			
			return true;
		}
		return false;
	}

	private boolean isFreshPOForAbstractGuard(Predicate predicate, int index, IAbstractEventGuardTable abstractEventGuardTable) {
		int absIndex = abstractEventGuardTable.indexOfPredicate(predicate);

		if (absIndex == -1)
			return true;

		for (int k=0; k<absIndex; k++) {
		
			int indexOfConcrete = abstractEventGuardTable.getIndexOfCorrespondingConcrete(k);
		
			if (indexOfConcrete != -1 && indexOfConcrete < index)
				continue;
		
			return true;
		}
		return false;
	}

	@Override
	protected String getWDProofObligationDescription(boolean isTheorem) {
		if (isTheorem) {
			return "Well-definedness of Theorem";
		} else {
			return "Well-definedness of Guard";
		}
	}

	@Override
	protected boolean isAccurate() {
		return ((IEventHypothesisManager) hypothesisManager).eventIsAccurate()
			&& machineHypothesisManager.machineIsAccurate();
	}

	@Override
	protected String getProofObligationPrefix(ISCGuard predicateElement)
			throws RodinDBException {
		return eventLabel + "/" + predicateElement.getLabel();
	}

}
