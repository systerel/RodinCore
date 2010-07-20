/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

/**
 * @author Nicolas Beauger
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface ICompatibilityMediator {

	/**
	 * Adds a compatibility between operators of given ids.
	 * <p>
	 * N.B: Compatibility is 'oriented', thus compatibility from left to right
	 * does not imply compatibility from right to left.
	 * </p>
	 * <p>
	 * If a priority is set between left and right, compatibility is assumed.
	 * </p>
	 * <p>
	 * If there is no priority and no compatibility set between left and right,
	 * operators are considered incompatible.
	 * </p>
	 * 
	 * @param leftOpId
	 *            an operator id
	 * @param rightOpId
	 *            an operator id
	 */
	void addCompatibility(String leftOpId, String rightOpId);

	/**
	 * Adds self compatibility for the given operator and records it as
	 * associative.
	 * <p>
	 * Associative operators have special treatments in various places:
	 * <li>parsing: "a op b op c" is allowed without parentheses; a, b and c are
	 * direct children of op</li>
	 * <li>printing: a formula "(a op b) op c" is printed with these parentheses
	 * </li>
	 * <li>rewriting: flattening becomes possible</li>
	 * </p>
	 * <p>
	 * It is different from calling <code>addCompatibility(opId, opId)</code>,
	 * which has the following consequences:
	 * <li>parsing: "a op b op c" is allowed without parentheses and produces
	 * either "(a op b) op c" (if the extension is declared binary) or
	 * "a op b op c" with a, b and c as direct children of op (if the extension
	 * is declared multary)</li>
	 * <li>printing: a formula "(a op b) op c" will be printed without
	 * parentheses</li>
	 * <li>rewriting: flattening is not performed</li>
	 * </p>
	 * 
	 * @param opId
	 *            an operator id
	 */
	void addAssociativity(String opId);
}
