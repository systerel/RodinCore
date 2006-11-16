/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public enum GraphProblem implements IRodinProblem {

	InvalidIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentifierName),
	InvalidIdentifierSpacesError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentifierContainsSpaces),
	AbstractContextNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextNotFound),
	SeenContextNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextNotFound),
	TooManyAbstractMachinesError(IMarker.SEVERITY_ERROR, Messages.scuser_OnlyOneAbstractMachine),
	AbstractMachineNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineNotFound),
	AbstractEventNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventNotFound),
	AbstractEventNotRefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventNotFound),
	AbstractEventLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractEventLabelConflict),
	EventMergeSplitError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeSplitConflict),
	EventMergeMergeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeMergeConflict),
	EventInheritedMergeSplitError(IMarker.SEVERITY_ERROR, Messages.scuser_EventInheritedMergeSplitConflict),
	EventMergeVariableTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeVariableTypeConflict),
	EventMergeActionError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeActionConflict),
	EventRefinementError(IMarker.SEVERITY_ERROR, Messages.scuser_EventRefinementError),
	MachineWithoutInitialisationError(IMarker.SEVERITY_ERROR, Messages.scuser_MachineWithoutInitialisationError),
	InitialisationRefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationRefinedError),
	InitialisationVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationVariableError),
	InitialisationGuardError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationGuardError),
	InitialisationActionRHSError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationActionRHSError),
	InitialisationIncompleteWarning(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationIncomplete),
	CarrierSetNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_CarrierSetNameImportConflict),
	CarrierSetNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CarrierSetNameImportConflict),
	CarrierSetNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_CarrierSetNameConflict),
	CarrierSetNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_CarrierSetNameConflict),
	ConstantNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ConstantNameImportConflict),
	ConstantNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConstantNameImportConflict),
	ConstantNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ConstantNameConflict),
	ConstantNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConstantNameConflict),
	VariableNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableNameImportConflict),
	VariableNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_VariableNameImportConflict),
	VariableNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableNameConflict),
	VariableNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_VariableNameConflict),
	UntypedCarrierSetError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedCarrierSetError),
	UntypedConstantError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedConstantError),
	UntypedVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedVariableError),
	UntypedIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedIdentifierError),
	UndeclaredFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_UndeclaredFreeIdentifierError),
	VariantFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_VariantFreeIdentifierError),
	AxiomFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomFreeIdentifierError),
	TheoremFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremFreeIdentifierError),
	InvariantFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_InvariantFreeIdentifierError),
	GuardFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_GuardFreeIdentifierError),
	ActionFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionFreeIdentifierError),
	ActionDisjointLHSError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionDisjointLHSError),
	WitnessFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessFreeIdentifierError),
	InvalidVariantTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidVariantTypeError),
	TooManyVariantsError(IMarker.SEVERITY_ERROR, Messages.scuser_TooManyVariants),
	InconsistentAbstractConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InconsistentAbstractConvergence),
	ConvergentFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentFaultyConvergence),
	OrdinaryFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OrdinaryFaultyConvergence),
	AnticipatedFaultyConvergence(IMarker.SEVERITY_WARNING, Messages.scuser_AnticipatedFaultyConvergence),
	AnticipatedEventNoVariant(IMarker.SEVERITY_WARNING, Messages.scuser_AnticipatedEventNoVariant),
	ConvergentEventNoVariant(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentEventNoVariant),
	AxiomLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomLabelConflict),
	AxiomLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AxiomLabelConflict),
	TheoremLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremLabelConflict),
	TheoremLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_TheoremLabelConflict),
	InvariantLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_InvariantLabelConflict),
	InvariantLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InvariantLabelConflict),
	EventLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_EventLabelConflict),
	EventLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_EventLabelConflict),
	GuardLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_GuardLabelConflict),
	GuardLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_GuardLabelConflict),
	ActionLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionLabelConflict),
	ActionLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ActionLabelConflict),
	WitnessLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelConflict),
	WitnessLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WitnessLabelConflict),
	WitnessLabelMissingWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WitnessLabelMissing),
	WitnessLabelNeedLessError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelNeedLess),
	ContextOnlyInAbstractMachineWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ContextOnlyPresentInAbstractMachine),
	WasAbstractEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WasAbstractEventLabelProblem),
	ObsoleteEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ObsoleteEventLabelProblem),
	InconsistentEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InconsistentEventLabelProblem),
	VariableHasDisappearedError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableHasDisappearedError),
	VariableIsLocalInAbstractMachineError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableIsLocalInAbstractMachine),
	AssignedIdentifierNotVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignedIdentifierNotVariable),
	LocalVariableChangedTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_LocalVariableChangedTypeError),
	AssignmentToLocalVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentToLocalVariable);
	
	private final String errorCode;
	
	private final String message;
	
	private final int severity;

	private GraphProblem(int severity, String message) {
		this.severity = severity;
		this.message = message;
		this.errorCode = EventBPlugin.PLUGIN_ID + name();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getSeverity()
	 */
	public int getSeverity() {
		return severity;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getErrorCode()
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getLocalizedMessage(java.lang.Object[])
	 */
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

}
