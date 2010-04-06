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
 *     Systerel - added PO nature
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.IPOGHint;
import org.eventb.core.pog.IPOGNature;
import org.eventb.core.pog.IPOGPredicate;
import org.eventb.core.pog.IPOGSource;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IMachineVariableTable;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class FwdMachineEventActionFrameSimModule extends MachineEventRefinementModule {

	public static final IModuleType<FwdMachineEventActionFrameSimModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".fwdMachineEventActionFrameSimModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected ISCEvent abstractEvent;
	protected IMachineVariableTable machineVariableTable;
	
	public void process(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		createFrameSimProofObligations(
				repository.getTarget(), 
				monitor);
	
	}
	
	private void createFrameSimProofObligations(
			IPORoot target, 
			IProgressMonitor monitor) throws CoreException {
		
		if (machineInfo.isInitialMachine())
			return;
		
		ArrayList<IPOGPredicate> hyp = 
			new ArrayList<IPOGPredicate>(1);
		
		List<Assignment> nondetAssignments = 
			concreteEventActionTable.getNondetAssignments();
		List<ISCAction> nondetActions = 
			concreteEventActionTable.getNondetActions();
		List<BecomesEqualTo> detAssignments = 
			concreteEventActionTable.getDetAssignments();
		List<BecomesEqualTo> primedDetAssignments = 
			concreteEventActionTable.getPrimedDetAssignments();
		List<ISCAction> detActions = 
			concreteEventActionTable.getDetActions();
		
		for (FreeIdentifier variable : machineVariableTable.getPreservedVariables()) {
			
			if(abstractEventActionTable.containsAssignedVariable(variable))
				continue;
			
			hyp.clear();
			
			Predicate predicate =
				factory.makeRelationalPredicate(Formula.EQUAL, 
						variable.withPrime(factory), 
						variable, null);
			
			ISCAction action = null;
			
			int pos = findIndex(variable, nondetAssignments);
			
			if (pos >= 0) {
				hyp.add(
						makePredicate(nondetAssignments.get(pos).getBAPredicate(factory),
								nondetActions.get(pos).getSource()));
				action = nondetActions.get(pos);
			} else {
				pos = findIndex(variable, detAssignments);
				
				if (pos >= 0) {
					predicate = predicate.applyAssignment(primedDetAssignments.get(pos), factory);
					action = detActions.get(pos);
				} else
					continue;
			}
			
			// TODO should the abstract machine be shown when there is no abstract event?
			IPOGSource[] sources = abstractEvent == null ?
					new IPOGSource[] {
						makeSource(IPOSource.CONCRETE_ROLE, concreteEvent.getSource())
					}:
					new IPOGSource[] {
						makeSource(IPOSource.ABSTRACT_ROLE, abstractEvent.getSource()),
						makeSource(IPOSource.CONCRETE_ROLE, concreteEvent.getSource())
					};
			
			String sequentName = concreteEventLabel + "/" + variable.getName() + "/EQL";
			createPO(
					target, 
					sequentName, 
					IPOGNature.COMMON_VARIABLE_EQUALITY,
					fullHypothesis,
					hyp,
					makePredicate(predicate, action.getSource()),
					sources,
					new IPOGHint[] {
						getLocalHypothesisSelectionHint(target, sequentName)
					},
					accurate,
					monitor);

		}
	}

	private int findIndex(FreeIdentifier variable, List<? extends Assignment> assignments) {
		int pos = -1;
		
		for (int i=0; i<assignments.size(); i++) {
			Assignment assignment = assignments.get(i);
			for (FreeIdentifier ident : assignment.getAssignedIdentifiers()) {
				if (variable.equals(ident)) {
					pos = i;
					break;
				}
			}
		}
		return pos;
	}
	

	@Override
	public void initModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		abstractEvent = abstractEventGuardList.getFirstAbstractEvent();
		machineVariableTable =
			(IMachineVariableTable) repository.getState(IMachineVariableTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IPOGStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		abstractEvent = null;
		machineVariableTable = null;
		super.endModule(element, repository, monitor);
	}

}
