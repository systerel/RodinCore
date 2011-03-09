/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is intended to trace externally specified prover rules into
 * source code.
 * <p>
 * The idea is to annotate each method that implements a given set of rules with
 * the names of these rules.
 * </p>
 * <p>
 * Examples:
 * <pre>
 * @ProverRule("FALSE_HYP")
 * public void applyFalseHyp() {
 *    ...
 * }
 * 
 * @ProverRule( { "TRUE_GOAL", "SIMPLIFY_NOTEQUAL", "AUTO_MH" })
 * public void applySeveralRules() {
 *    ...
 * }
 * </pre>
 * </p>
 * 
 * @author "Nicolas Beauger"
 * @since 1.0
 * 
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ProverRule {

	/**
	 * Returns the names of proof rules implemented by the annotated method.
	 * 
	 * @return an array of proof rule names
	 */
	String[] value();

}
