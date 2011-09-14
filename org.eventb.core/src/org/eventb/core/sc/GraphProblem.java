/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added LoadingRootModuleError
 *     University of Dusseldorf - added theorem attribute
 *     Systerel - added UnknownConfigurationWarning
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
 * @since 1.0
 */
public enum GraphProblem implements IRodinProblem {

	LoadingRootModuleError(IMarker.SEVERITY_ERROR, Messages.scuser_LoadingRootModuleError),
	ConfigurationMissingError(IMarker.SEVERITY_ERROR, Messages.scuser_ConfigurationMissing),
	UnknownConfigurationWarning(IMarker.SEVERITY_WARNING, Messages.scuser_UnknownConfiguration),
	IdentifierUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_IdentifierUndef),
	PredicateUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_PredicateUndef),
	ExpressionUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ExpressionUndef),
	AssignmentUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentUndef),
	ConvergenceUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ConvergenceUndef),
	ExtendedUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_ExtendedUndef),
	DerivedPredUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_DerivedPredUndef),
	DerivedPredIgnoredWarning(IMarker.SEVERITY_WARNING, Messages.scuser_DerivedPredIgnored),
	InvalidIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentifierName),
	InvalidIdentifierSpacesError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidIdentifierContainsSpaces),
	LabelUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_LabelUndef),
	EmptyLabelError(IMarker.SEVERITY_ERROR, Messages.scuser_EmptyLabel),
	AbstractContextNameUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextNameUndef),
	AbstractContextNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextNotFound),
	AbstractContextRedundantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractContextRedundant),
	SeenContextRedundantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_SeenContextRedundant),
	SeenContextNameUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextNameUndef),
	SeenContextNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextNotFound),
	SeenContextWithoutConfigurationError(IMarker.SEVERITY_ERROR, Messages.scuser_SeenContextWithoutConfiguration),
	AbstractMachineNameUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineNameUndef),
	TooManyAbstractMachinesError(IMarker.SEVERITY_ERROR, Messages.scuser_OnlyOneAbstractMachine),
	AbstractMachineWithoutConfigurationError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineWithoutConfiguration),
	AbstractContextWithoutConfigurationError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractContextWithoutConfiguration),
	AbstractMachineNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractMachineNotFound),
	AbstractEventLabelUndefError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventLabelUndef),
	AbstractEventNotFoundError(IMarker.SEVERITY_ERROR, Messages.scuser_AbstractEventNotFound),
	/**
	 * @since 2.0
	 */
	AbstractEventNotRefinedWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractEventNotRefined),
	AbstractEventLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractEventLabelConflict),
	@SuppressWarnings("deprecation")
	EventMergeSplitError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeSplitConflict),
	@SuppressWarnings("deprecation")
	EventMergeMergeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeMergeConflict),
	@SuppressWarnings("deprecation")
	EventInheritedMergeSplitError(IMarker.SEVERITY_ERROR, Messages.scuser_EventInheritedMergeSplitConflict),
	EventExtendedUnrefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_EventExtendedUnrefined),
	EventExtendedMergeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventExtendedMerge),
	EventMergeVariableTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeParameterTypeConflict),
	EventMergeActionError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeActionConflict),
	EventMergeLabelError(IMarker.SEVERITY_ERROR, Messages.scuser_EventMergeLabelConflict),
	EventRefinementError(IMarker.SEVERITY_ERROR, Messages.scuser_EventRefinementError),
	MachineWithoutInitialisationWarning(IMarker.SEVERITY_WARNING, Messages.scuser_MachineWithoutInitialisation),
	InitialisationRefinedError(IMarker.SEVERITY_ERROR, Messages.scuser_InitialisationRefinedError),
	InitialisationRefinesEventWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationRefinesEventError),
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
	ParameterNameImportConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ParameterNameImportConflift),
	ParameterNameImportConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ParameterNameImportConflift),
	ParameterNameConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_ParameterNameConflict),
	ParameterNameConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ParameterNameConflict),
	UntypedCarrierSetError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedCarrierSetError),
	UntypedConstantError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedConstantError),
	UntypedVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedVariableError),
	UntypedParameterError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedParameterError),
	UntypedIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_UntypedIdentifierError),
	UndeclaredFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_UndeclaredFreeIdentifierError),
	FreeIdentifierFaultyDeclError(IMarker.SEVERITY_ERROR, Messages.scuser_FreeIdentifierFaultyDeclError),
	VariantFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_VariantFreeIdentifierError),
	AxiomFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomFreeIdentifierError),
	@Deprecated
	TheoremFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremFreeIdentifierError),
	InvariantFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_InvariantFreeIdentifierError),
	GuardFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_GuardFreeIdentifierError),
	ActionFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionFreeIdentifierError),
	ActionDisjointLHSError(IMarker.SEVERITY_ERROR, Messages.scuser_ActionDisjointLHSProblem),
	ActionDisjointLHSWarining(IMarker.SEVERITY_WARNING, Messages.scuser_ActionDisjointLHSProblem),
	WitnessFreeIdentifierError(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessFreeIdentifierError),
	InvalidVariantTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_InvalidVariantTypeError),
	TooManyVariantsError(IMarker.SEVERITY_ERROR, Messages.scuser_TooManyVariants),
	FaultyAbstractConvergenceUnchangedWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractConvergenceUnchanged),
	FaultyAbstractConvergenceOrdinaryWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractConvergenceOrdinary),
	FaultyAbstractConvergenceAnticipatedWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AbstractConvergenceAnticipated),
	ConvergentFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentFaultyConvergence),
	OrdinaryFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_OrdinaryFaultyConvergence),
	AnticipatedFaultyConvergenceWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AnticipatedFaultyConvergence),
	NoConvergentEventButVariantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_NoConvergentEventButVariant),
	ConvergentEventNoVariantWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ConvergentEventNoVariant),
	InitialisationNotOrdinaryWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InitialisationNotOrdinary),
	AxiomLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_AxiomLabelConflict),
	AxiomLabelConflictWarning(IMarker.SEVERITY_WARNING, Messages.scuser_AxiomLabelConflict),
	@Deprecated
	TheoremLabelConflictError(IMarker.SEVERITY_ERROR, Messages.scuser_TheoremLabelConflict),
	@Deprecated
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
	WitnessLabelNotPermissible(IMarker.SEVERITY_ERROR, Messages.scuser_WitnessLabelNotPermissible),
	@SuppressWarnings("deprecation")
	ContextOnlyInAbstractMachineWarning(IMarker.SEVERITY_WARNING, Messages.scuser_ContextOnlyPresentInAbstractMachine),
	WasAbstractEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_WasAbstractEventLabelProblem),
	InconsistentEventLabelWarning(IMarker.SEVERITY_WARNING, Messages.scuser_InconsistentEventLabelProblem),
	VariableHasDisappearedError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableHasDisappearedError),
	DisappearedVariableRedeclaredError(IMarker.SEVERITY_ERROR, Messages.scuser_DisappearedVariableRedeclaredError),
	VariableIsParameterInAbstractMachineError(IMarker.SEVERITY_ERROR, Messages.scuser_VariableIsParameterInAbstractMachine),
	AssignedIdentifierNotVariableError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignedIdentifierNotVariable),
	ParameterChangedTypeError(IMarker.SEVERITY_ERROR, Messages.scuser_ParameterChangedTypeError),
	AssignmentToParameterError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentToParameter),
	AssignmentToCarrierSetError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentToCarrierSet),
	AssignmentToConstantError(IMarker.SEVERITY_ERROR, Messages.scuser_AssignmentToConstant),
	/**
	 * @since 2.0
	 */
	EventInitLabelMisspellingWarning(IMarker.SEVERITY_WARNING, Messages.scuser_EventInitLabelMisspelling);
	
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
	@Override
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
	@Override
	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinProblem#getLocalizedMessage(java.lang.Object[])
	 */
	@Override
	public String getLocalizedMessage(Object[] args) {
		return MessageFormat.format(message, args);
	}

}
