/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added scuser_LoadingRootModuleError
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.internal.core.sc;

/**
 * @author Stefan Hallerstede
 *
 */
import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	private static final String BUNDLE_NAME = "org.eventb.internal.core.sc.messages";//$NON-NLS-1$

	// build
	public static String build_cleaning;
	public static String build_runningSC;
	public static String build_extracting;
	
	// progress messages
	public static String progress_ContextCarrierSets;
	public static String progress_ContextConstants;
	public static String progress_ContextAxioms;
	public static String progress_ContextExtends;
	
	public static String progress_MachineVariables;
	public static String progress_MachineInvariants;
	public static String progress_MachineEvents;
	public static String progress_MachineVariant;
	public static String progress_MachineRefines;
	public static String progress_MachineSees;
	
    // error messages of the static checker addressed to the user

	public static String scuser_LoadingRootModuleError;
	public static String scuser_ConfigurationMissing;
	public static String scuser_IdentifierUndef;
	public static String scuser_PredicateUndef;
	public static String scuser_ExpressionUndef;
	public static String scuser_AssignmentUndef;
	public static String scuser_ConvergenceUndef;
	public static String scuser_ExtendedUndef;
	public static String scuser_DerivedPredUndef;
	public static String scuser_DerivedPredIgnored;
	public static String scuser_InvalidIdentifierName;
	public static String scuser_InvalidIdentifierContainsSpaces;
	public static String scuser_LabelUndef;
	public static String scuser_EmptyLabel;
	public static String scuser_AbstractContextNameUndef;
    public static String scuser_AbstractContextNotFound;
    public static String scuser_AbstractContextRedundant;
    public static String scuser_SeenContextRedundant;
    public static String scuser_SeenContextNameUndef;
    public static String scuser_SeenContextNotFound;
    public static String scuser_SeenContextWithoutConfiguration;
    public static String scuser_AbstractMachineNameUndef;
    public static String scuser_OnlyOneAbstractMachine;
	public static String scuser_AbstractMachineWithoutConfiguration;
	public static String scuser_AbstractContextWithoutConfiguration;
	public static String scuser_AbstractMachineNotFound;
    public static String scuser_AbstractEventLabelUndef;
    public static String scuser_AbstractEventNotFound;
    @Deprecated
    public static String scuser_AbstractEventNotRefined;
    public static String scuser_AbstractEventLabelConflict;
    @Deprecated
    public static String scuser_EventMergeSplitConflict;
    @Deprecated
    public static String scuser_EventMergeMergeConflict;
    @Deprecated
    public static String scuser_EventInheritedMergeSplitConflict;
	public static String scuser_EventExtendedUnrefined;
	public static String scuser_EventExtendedMerge;
    public static String scuser_EventMergeParameterTypeConflict;
    public static String scuser_EventMergeActionConflict;
    public static String scuser_EventMergeLabelConflict;
    public static String scuser_EventRefinementError;
    public static String scuser_EventInitLabelMisspelling;
    public static String scuser_MachineWithoutInitialisation;
	public static String scuser_InitialisationRefinesEventError;
	public static String scuser_InitialisationRefinedError;
    public static String scuser_InitialisationVariableError;
    public static String scuser_InitialisationGuardError;
    public static String scuser_InitialisationActionRHSError;
    public static String scuser_InitialisationIncomplete;
    public static String scuser_CarrierSetNameImportConflict;
    public static String scuser_CarrierSetNameConflict;
    public static String scuser_ConstantNameImportConflict;
    public static String scuser_ConstantNameConflict;
    public static String scuser_VariableNameImportConflict;
    public static String scuser_VariableNameConflict;
    public static String scuser_ParameterNameConflict;
	public static String scuser_ParameterNameImportConflift;
	public static String scuser_UntypedCarrierSetError;
    public static String scuser_UntypedConstantError;
    public static String scuser_UntypedVariableError;
    public static String scuser_UntypedParameterError;
    public static String scuser_UntypedIdentifierError;
    public static String scuser_UndeclaredFreeIdentifierError;
    public static String scuser_FreeIdentifierFaultyDeclError;
    public static String scuser_VariantFreeIdentifierError;
    public static String scuser_AxiomFreeIdentifierError;
    @Deprecated
    public static String scuser_TheoremFreeIdentifierError;
    public static String scuser_InvariantFreeIdentifierError;
    public static String scuser_GuardFreeIdentifierError;
    public static String scuser_ActionFreeIdentifierError;
    public static String scuser_ActionDisjointLHSProblem;
    public static String scuser_WitnessFreeIdentifierError;
    public static String scuser_InvalidVariantTypeError;
    public static String scuser_TooManyVariants;
    public static String scuser_ConvergentFaultyConvergence;
    public static String scuser_OrdinaryFaultyConvergence;
    public static String scuser_AnticipatedFaultyConvergence;
	public static String scuser_AbstractConvergenceUnchanged;
	public static String scuser_AbstractConvergenceOrdinary;
	public static String scuser_AbstractConvergenceAnticipated;
    public static String scuser_NoConvergentEventButVariant;
    public static String scuser_ConvergentEventNoVariant;
    public static String scuser_InitialisationNotOrdinary;

    public static String scuser_AxiomLabelConflict;
    @Deprecated
    public static String scuser_TheoremLabelConflict;
    public static String scuser_InvariantLabelConflict;
    public static String scuser_EventLabelConflict;
    public static String scuser_GuardLabelConflict;
    public static String scuser_ActionLabelConflict;
    
    public static String scuser_WitnessLabelConflict;
    public static String scuser_WitnessLabelMissing;
    public static String scuser_WitnessLabelNeedLess;
    public static String scuser_WitnessLabelNotPermissible;
    @Deprecated
    public static String scuser_ContextOnlyPresentInAbstractMachine;
    
    public static String scuser_WasAbstractEventLabelProblem;
    public static String scuser_InconsistentEventLabelProblem;
    public static String scuser_VariableHasDisappearedError;
    public static String scuser_DisappearedVariableRedeclaredError;
    public static String scuser_VariableIsParameterInAbstractMachine;
    public static String scuser_AssignedIdentifierNotVariable;
    public static String scuser_ParameterChangedTypeError;
    public static String scuser_AssignmentToParameter;
    public static String scuser_AssignmentToCarrierSet;
    public static String scuser_AssignmentToConstant;

    public static String scuser_BoundIdentifierIsAlreadyBound;
    public static String scuser_Circularity;
    public static String scuser_FreeIdentifierHasBoundOccurences;
    public static String scuser_InternalError;
    public static String scuser_LexerError;
    public static String scuser_SyntaxError;
    public static String scuser_TypeCheckFailure;
    public static String scuser_TypesDoNotMatch;
    public static String scuser_TypeUnknown;
    public static String scuser_MinusAppliedToSet;
	public static String scuser_MulAppliedToSet;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}
	
	private Messages() {
		// Do not instantiate
	}
	
}