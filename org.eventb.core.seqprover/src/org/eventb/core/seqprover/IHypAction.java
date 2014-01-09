/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.internal.core.seqprover.IInternalProverSequent;

/**
 * Interface describing the information stored in a {@link IHypAction} object.
 * 
 * <p>
 * An object of this type is typically attached to a rule antecedent. They describe
 * operations that *may* be performed on the hypotheses of a sequent in case the stated 
 * hypotheses exist.
 * </p>
 *
 * <p>
 * The action type denotes the type of action to be performed:
 * <li>
 * <ul> A selection action alters the selection information in the set of hypotheses 
 * (i.e. select, deselect, hide, show)
 * <ul> A forward inference action performs a forward inference on a set of hypotheses. 
 * </li>
 * </p>
 *
 * <p>
 * This interface is not intended to be implemented by clients. Objects of this type 
 * are typically generated inside reasoners by calling a factory method.
 * </p>
 * 
 * @see IAntecedent
 * @see ProverFactory
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public interface IHypAction {
	
	/**
	 * Returns the action type of the hypothesis action.
	 * 
	 * This may be one of the action types defined in this file.
	 * 
	 * @return
	 * 		The action type of this hypothesis action
	 */
	String getActionType();
	
	/**
	 * Interface implemented by hypothesis actions that change the selection 
	 * information of hypotheses in a sequent.
	 * 
	 * <p>
	 * Possible action types for objects implementing such a hypothesis action are
	 * defined in this interface.
	 * </p>
	 * 
	 * @see IHypAction
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public interface ISelectionHypAction extends IHypAction{
		
		/**
		 * Action type for a select hypotheses action
		 */
		static String SELECT_ACTION_TYPE = "SELECT";

		/**
		 * Action type for a deselect hypotheses action
		 */
		static String DESELECT_ACTION_TYPE = "DESELECT";
		
		/**
		 * Action type for a hide hypotheses action
		 */
		static String HIDE_ACTION_TYPE = "HIDE";
		
		/**
		 * Action type for a show hypotheses action
		 */
		static String SHOW_ACTION_TYPE = "SHOW";
		
		
		/**
		 * Returns the set of hypotheses on which the hypothesis
		 * action does its job.
		 * <p>
		 * The action is performed on all the hypotheses that are also 
		 * present in the sequent. The exact description of what is performed
		 * can be found in the javadoc for {@link IInternalProverSequent}
		 * </p>
		 * @return
		 * 		the set of hypotheses to change the selection of 
		 */
		Collection<Predicate> getHyps();
	
	}
		
	/**
	 * Interface implemented by hypothesis actions that perform forward inference
	 * on the hypotheses in a sequent.
	 * 
	 * 
	 * @see IHypAction
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public interface IForwardInfHypAction extends IHypAction{
		
		/**
		 * The only legal action type for forward inference hypothesis actions.
		 */
		String ACTION_TYPE = "FORWARD_INF";
		
		
		/**
		 * A set of hypotheses that are required in order to perform the forward
		 * inference.
		 * 
		 * <p>
		 * The inference can only be performed if all these hypotheses are present
		 * in the hypotheses of a sequent.The exact description of what is performed
		 * can be found in the javadoc for {@link IInternalProverSequent}
		 * </p>
		 * 
		 * @return
		 * 		The required hypotheses for the forward inference 
		 */
		Collection<Predicate> getHyps();
		
		
		/**
		 * Returns an array of free identifiers introduced by the forward inference.
		 * 		 
		 * <p>
		 * The inference can only be performed if all these free identifiers are
		 * fresh (i.e. not present in the type environment) of a sequent.
		 * </p>
		 * <p>
		 * In case this inferrence can be performed on a sequent, these free identifiers
		 * will be added to its type environment. The exact description of what is performed
		 * can be found in the javadoc for {@link IInternalProverSequent}
		 * </p>
		 * 
		 * @return
		 * 	an array of free identifiers introduced by the forward inference
		 */
		FreeIdentifier[] getAddedFreeIdents();
		
		/**
		 * Returns the set of hypotheses inferred by this forward inferrence
		 * 
		 * <p>
		 * In case this inferrence can be performed on a sequent, these hypotheses
		 * will be added to its set of hypotheses. The exact description of what is performed
		 * can be found in the javadoc for {@link IInternalProverSequent}.
		 * </p>
		 * 
		 * @return
		 * 	the set of hypotheses inferred by this forward inferrence
		 */
		Collection<Predicate> getInferredHyps();
	
	}

	/**
	 * Translates this hyp action using the given formula factory.
	 * 
	 * @param factory
	 *            the destination formula factory
	 * @return the translated hyp action
	 * @throws IllegalArgumentException
	 *             if the translation fails
	 * @since 3.0
	 */
	IHypAction translate(FormulaFactory factory);

}
