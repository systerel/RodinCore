/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * Common protocol for deserializing a reasoner input object. Serialization is
 * done by registering strings, predicates and expressions with an instance of
 * this interface. Each one is associated to a key chosen by the client. When
 * deserializing, clients just retrieve the stored information using the same
 * key.
 * <p>
 * Additionaly, most information from the rule corresponding to the previous
 * application of the reasoner with the same input is also accessible.
 * </p>
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * 
 * @author Farhad Mehta
 * @noextend This interface is not intended to be extended by clients.
 * @since 1.0
 */
public interface IReasonerInputReader {

	
	/**
	 * Returns the formula factory for deserializing reasoner input objects.
	 * 
	 * @return the formula factory used by the current input reader
	 * @since 2.0
	 */
	FormulaFactory getFormulaFactory();
	
	/**
	 * Returns the predicates that were serialized with the given key.
	 * 
	 * @param key
	 *            key to use
	 * @throws SerializeException
	 */
	Predicate[] getPredicates(String key) throws SerializeException;

	/**
	 * Returns the expressions that were serialized with the given key.
	 * 
	 * @param key
	 *            key to use
	 * @throws SerializeException
	 */
	Expression[] getExpressions(String key) throws SerializeException;

	/**
	 * Returns the string that was serialized with the given key.
	 * 
	 * @param key
	 *            key to use
	 * @throws SerializeException
	 */
	String getString(String key) throws SerializeException;

	/**
	 * Returns the goal of the corresponding proof rule.
	 * 
	 * @return the goal of the corresponding proof rule
	 */
	Predicate getGoal();

	/**
	 * Returns the needed hypotheses of the corresponding proof rule.
	 * 
	 * @return the needed hypotheses of the corresponding proof rule
	 */
	Set<Predicate> getNeededHyps();

	/**
	 * Returns the confidence of the corresponding proof rule.
	 * 
	 * @return the confidence of the corresponding proof rule
	 */
	int getConfidence();

	/**
	 * Returns the display name of the corresponding proof rule.
	 * 
	 * @return the display name of the corresponding proof rule
	 */
	String getDisplayName();

	/**
	 * Returns the antecedents of the corresponding proof rule.
	 * 
	 * @return the antecedents of the corresponding proof rule
	 */
	IAntecedent[] getAntecedents();

}
