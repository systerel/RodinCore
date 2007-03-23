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
	AbstractEventNotRefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventNotRefined),
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
	InitialisationIncompleteWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationIncomplete),
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
	EventVariableNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_EventVariableNameConflict),
	EventVariableNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_EventVariableNameConflict),
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
	ConvergentFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentFaultyConvergence),
	OrdinaryFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OrdinaryFaultyConvergence),
	AnticipatedFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AnticipatedFaultyConvergence),
	NoConvergentEventButVariantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoConvergentEventButVariant),
	ConvergentEventNoVariantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentEventNoVariant),
	InitialisationNotOrdinaryWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationNotOrdinary),
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
	WitnessLabelNotWellFormedError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelNotWellFormed),
	ContextOnlyInAbstractMachineWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ContextOnlyPresentInAbstractMachine),
	WasAbstractEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WasAbstractEventLabelProblem),
	InconsistentEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InconsistentEventLabelProblem),
	VariableHasDisappearedError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableHasDisappearedError),
	VariableIsLocalInAbstractMachineError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableIsLocalInAbstractMachine),
	AssignedIdentifierNotVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignedIdentifierNotVariable),
	LocalVariableChangedTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_LocalVariableChangedTypeError),
	AssignmentToLocalVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentToLocalVariable);
	
	private final String errorCode;
	
	private final String message;
	
	private final int severity;
	
	private int arity;

	private GraphProblem(int severity, String message) {
		this.severity = severity;
		this.message = message;
		this.errorCode = EventBPlugin.PLUGIN_ID + "." + name();
		arity = -1;
	}
	
	public static GraphProblem valueOfErrorCode(String errorCode) {
		String instName = errorCode.substring(errorCode.lastIndexOf('.')+1);
		return valueOf(instName);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getSeverity()
	 */
	public int getSeverity() {
		return severity;
	}
	
	/**
	 * Returns the number of parameters needed by the message of this problem,
	 * i.e. the length of the object array to be passed to 
	 * <code>getLocalizedMessage()</code>.
	 * 
	 * @return the number of parameters needed by the message of this problem
	 */
	public int getArity() {
		if (arity == -1) {
			MessageFormat mf = new MessageFormat(message);
		    arity = mf.getFormatsByArgumentIndex().length;
		}
		return arity;
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
