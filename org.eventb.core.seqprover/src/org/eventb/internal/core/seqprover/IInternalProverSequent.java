/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added constraints about predicate variables
 *     Systerel - added unselected hypotheses
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.Collection;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;

public interface IInternalProverSequent extends IProverSequent{
	
	/**
	 * Method for non-destructive modification of a sequent.
	 * <p>
	 * The original sequent remains unmodified.
	 * 
	 * Returns a new sequent after adding the fresh free identifiers, adding and
	 * selecting the given hypotheses, and replacing the goal with the given new
	 * goal.
	 * </p>
	 * <p>
	 * This operation can only be performed if :
	 * <ul>
	 * <li>All the new free identifiers provided do not appear in the type
	 * environment of the sequent, and
	 * <li>The hypotheses and new goal provided do not contain predicate
	 * variables, and
	 * <li>The hypotheses and new goal provided can be successfully type checked
	 * using the type environment of the sequent enriched with the new free
	 * identifiers.
	 * </ul>
	 * These checks are done.
	 * </p>
	 * <p>
	 * In case this operation succeeds, but does not modify the sequent, a
	 * reference to the original sequent is returned. The case of
	 * non-modification can therefore be checked using <code>==</code>.
	 * </p>
	 * 
	 * @param freshFreeIdents
	 *            The fresh identifiers appearing in the given hypotheses and
	 *            not in the type environment of the sequent, or
	 *            <code>null</code> in case none.
	 * @param addedhyps
	 *            The hypotheses to add and select, or <code>null</code> in case
	 *            none.
	 * @param unselAddedHyps
	 *            the subset of addedhyps to add but not select, or
	 *            <code>null</code> if none
	 * @param newGoal
	 *            The new goal to replace with, or <code>null</code> in case no
	 *            goal replacement is to be done.
	 * @return A new sequent with the given free identifiers added to the type
	 *         environment, the given hypotheses added and selected, and the
	 *         given goal replaced, or <code>null</code> in case this operation
	 *         could not be performed.
	 * 
	 */
	IInternalProverSequent modify(FreeIdentifier[] freshFreeIdents,
			Collection<Predicate> addhyps,
			Collection<Predicate> unselAddedHyps, Predicate newGoal);

	/**
	 * Returns a new sequent after selecting the given hypotheses.
	 * <p>
	 * The original sequent remains unmodified.
	 * 
	 * The hypotheses that are actually selected are those that are present in the
	 * original sequent. Selecting a hidden hypothesis removes it from the hidden set.
	 * </p>
	 * <p>
	 * This operation is always successful. In case the sequent is not modified, 
	 * a reference to the original sequent is returned. The case of non-modification
	 * can therefore be checked using <code>==</code>.
	 * </p>
	 * @param toSelect
	 * 		Hypotheses to select, or <code>null</code> in case none.
	 * @return
	 * 		A sequent with the given hypotheses selected.
	 */
	IInternalProverSequent selectHypotheses(Collection<Predicate> toSelect);
	
	/**
	 * Returns a new sequent after deselecting the given hypotheses.
	 * <p>
	 * The original sequent remains unmodified.
	 * 
	 * The hypotheses that are actually deselected are those that are present 
	 * and selected in the original sequent.
	 * </p>
	 * <p>
	 * This operation is always successful. In case the sequent is not modified, 
	 * a reference to the original sequent is returned. The case of non-modification
	 * can therefore be checked using <code>==</code>.
	 * </p>
	 * @param toDeselect
	 * 		Hypotheses to deselect, or <code>null</code> in case none.
	 * @return
	 * 		A sequent with the given hypotheses deselected.
	 */
	IInternalProverSequent deselectHypotheses(Collection<Predicate> toDeselect);

	/**
	 * Returns a new sequent after hiding the given hypotheses.
	 * <p>
	 * The original sequent remains unmodified.
	 * 
	 * The hypotheses that are actually hidden are those that are present in the
	 * original sequent. Hiding a selected hypothesis removes it from the selected set.
	 * </p>
	 * <p>
	 * This operation is always successful. In case the sequent is not modified, 
	 * a reference to the original sequent is returned. The case of non-modification
	 * can therefore be checked using <code>==</code>.
	 * </p>
	 * @param toHide
	 * 		Hypotheses to hide, or <code>null</code> in case none.
	 * @return
	 * 		A sequent with the given hypotheses hidden.
	 */
	IInternalProverSequent hideHypotheses(Collection<Predicate> toHide);
	
	/**
	 * Returns a new sequent after showing (i.e. un-hiding) the given hypotheses.
	 * <p>
	 * The original sequent remains unmodified.
	 * 
	 * The hypotheses that are actually shown are those that are present 
	 * and hidden in the original sequent.
	 * </p>
	 * <p>
	 * This operation is always successful. In case the sequent is not modified, 
	 * a reference to the original sequent is returned. The case of non-modification
	 * can therefore be checked using <code>==</code>.
	 * <p>
	 * @param toShow
	 * 		Hypotheses to deselect, or <code>null</code> in case none.
	 * @return
	 * 		A sequent with the given hypotheses shown.
	 */
	IInternalProverSequent showHypotheses(Collection<Predicate> toShow);
	
	
	/**
	 * Returns a new sequent after performing the given forward inference on its
	 * hypotheses.
	 * 
	 * <p>
	 * The original sequent remains unmodified.
	 * 
	 * The forward inference is applicable if:
	 * <ul>
	 * <li> All <code>hyps</code> are present in the hypotheses of the
	 * sequent, and
	 * <li> All <code>addedIdents</code> do not occur in the type environment
	 * of the sequent, and
     * <li>All <code>inferredHyps</code> and <code>hyps</code>hypotheses do not
	 * contain predicate variables, and
	 * <li> All <code>inferredHyps</code> can be successfully type checked
	 * using the type environment of the sequent enriched with the
	 * <code>addedIdents</code>, and
	 * <li> All <code>inferredHyps</code> are not already contained in the
	 * hypotheses of the sequent.
	 * </ul>
	 * </p>
	 * <p>
	 * In case the forward inference is applicable, the <code>addedIdents</code>
	 * are added to the type environment, the <code>inferredHyps</code> that
	 * are not present are added as new hypotheses, and :
	 * <ul>
	 * <li> Selected iff at least one of the <code>hyps</code> is selected
	 * <li> Hidden iff all of the <code>hyps</code> are hidden
	 * </ul>
	 * <p>
	 * This operation is always successful, as soon as it type-checks, and does not
     * contain predicate variables. In case the sequent is not modified, a reference to
	 * the original sequent is returned.
     * The case of non-modification can therefore be checked using
	 * <code>==</code>. In case of type-check error, <code>null</code> is
	 * returned.
	 * </p>
	 * 
	 * @param hyps
	 *            Hypotheses needed to perform the forward inference, or
	 *            <code>null</code> if none.
	 * @param addedIdents
	 *            Free identifiers added by the forward inference, or
	 *            <code>null</code> if none.
	 * @param inferredHyps
	 *            Inferred hypotheses, or <code>null</code> if none.
	 * @return A sequent with the given forward inference performed on the
	 *         hypotheses or <code>null</code> in case of type-check error
	 */
	IInternalProverSequent performfwdInf(Collection<Predicate> hyps, FreeIdentifier[] addedIdents, Collection<Predicate> inferredHyps);
	
}
