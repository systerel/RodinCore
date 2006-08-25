/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	// internal messages
	
	// Static checker error
	public static String sctool_UninitializedStateError;
	
	// Symbol table errors
	public static String symtab_SymbolConflict;

	public static String symtab_ImmutableSymbolViolation;
	public static String symtab_StackUnderflow;
	
    // error messages of the static checker addressed to the user
	
	public static String scuser_OnlyOneEventRefinesClauseProblem;
	
	public static String scuser_InvalidIdentifierName;
	
    public static String scuser_AbstractContextNotFound;
    public static String scuser_SeenContextNotFound;
    public static String scuser_AbstractMachineNotFound;
    public static String scuser_AbstractEventNotFound;
    public static String scuser_CarrierSetNameImportConflict;
    public static String scuser_CarrierSetNameConflict;
    public static String scuser_ConstantNameImportConflict;
    public static String scuser_ConstantNameConflict;
    public static String scuser_VariableNameImportConflict;
    public static String scuser_VariableNameConflict;
	public static String scuser_UntypedCarrierSetError;
    public static String scuser_UntypedConstantError;
    public static String scuser_UntypedVariableError;
    public static String scuser_UndeclaredFreeIdentifierError;
    
    public static String scuser_AxiomLabelConflict;
    public static String scuser_TheoremLabelConflict;
    public static String scuser_InvariantLabelConflict;
    public static String scuser_EventLabelConflict;
    public static String scuser_GuardLabelConflict;
    public static String scuser_ActionLabelConflict;
    
    public static String scuser_WitnessLabelConflict;
    public static String scuser_WitnessLabelMissing;
    public static String scuser_WitnessLabelNeedLess;
   
    public static String scuser_ContextOnlyPresentInAbstractMachine;
    
    public static String scuser_AbstractEventLabelProblem;
    public static String scuser_ObsoleteEventLabelProblem;
    public static String scuser_ObsoleteVariableNameProblem;
    public static String scuser_VariableHasDisappearedError;
    public static String scuser_VariableIsLocalInAbstractMachine;
    public static String scuser_AssignedIdentifierNotVariable;
    public static String scuser_LocalVariableChangedTypeError;
    public static String scuser_AssignmentToLocalVariable;
  
    public static String scuser_BoundIdentifierIsAlreadyBound;
    public static String scuser_Circularity;
    public static String scuser_FreeIdentifierHasBoundOccurences;
    public static String scuser_InternalError;
    public static String scuser_LexerError;
    public static String scuser_SyntaxError;
    public static String scuser_TypeCheckFailure;
    public static String scuser_TypesDoNotMatch;
    public static String scuser_TypeUnknown;
    
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