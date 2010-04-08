/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.prover;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * @author htson
 *         <p>
 *         This is the common interface fo providing tactics to the proving user
 *         interface.
 * @since 1.0
 */
public interface ITacticProvider {

	/**
	 * Return the list of applicable positions in the predicate.
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node.
	 * @param hyp
	 *            the hypothesis or <code>null</code> if the goal is in
	 *            consideration.
	 * @param input
	 *            the input for the tactic (taken from the input text in the
	 *            Proof Control View) in case of global tactic.
	 * @return a list of applicable positions or null if this is not applicable.
	 */
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input);

	/**
	 * Return the actual tactic provides by this class.
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node.
	 * @param hyp
	 *            the hypothesis or <code>null</code> if the goal is in
	 *            consideration.
	 * @param position
	 *            the position to apply this tactic
	 * @param inputs
	 *            the inputs of the Goal/Hypothesis tactic which are an array of
	 *            strings. This inputs only applied to instantiate quantified
	 *            predicates. This is the list of string used to instantiate
	 *            taken from the Proving User Interface. <br>
	 *            For global tactic, clients must use
	 *            {@link #getTactic(IProofTreeNode, Predicate, IPosition, String[], String)}.
	 * @return the actual {@link org.eventb.core.seqprover.ITactic} that
	 *         provides by this class
	 * @deprecated use
	 *             {@link #getTactic(IProofTreeNode, Predicate, IPosition, String[], String)}
	 *             instead.
	 */
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs);

	/**
	 * Return the actual tactic provides by this class.
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node.
	 * @param hyp
	 *            the hypothesis or <code>null</code> if the goal is in
	 *            consideration.
	 * @param position
	 *            the position to apply this tactic
	 * @param inputs
	 *            the inputs of the Goal/Hypothesis tactic which are an array of
	 *            strings. This inputs only applied to instantiate quantified
	 *            predicates. This is the list of string used to instantiate
	 *            taken from the Proving User Interface. <br>
	 * @param globalInput
	 *            The input is single string taken from the input area in the 
	 *            Proof Control View. 
	 * @return the actual {@link org.eventb.core.seqprover.ITactic} that
	 *         provides by this class
	 */
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs, String globalInput);
	
	
	/**
	 * @deprecated Use
	 *             {@link #getApplicablePositions(IProofTreeNode,Predicate,String)}
	 *             instead
	 */
	@Deprecated
	public boolean isApplicable(IProofTreeNode node, Predicate hyp, String input);

	/**
	 * Return the source location where the hyperlink will be placed in the
	 * predicate for a particular position.
	 * <p>
	 * 
	 * @param predicate
	 *            the predicate (either goal/hypothesis).
	 * @param predStr
	 *            the source string of the predicate.
	 * @param position
	 *            the actual position to find in the predicate.
	 * @return the source location corresponding to the position where the
	 *         hyperlink is placed in the predicate.
	 */
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position);

}
