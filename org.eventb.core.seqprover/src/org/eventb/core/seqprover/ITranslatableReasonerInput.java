/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * Common protocol for reasoner inputs that support formula factory translation.
 * 
 * <p>
 * This interface is intended to be implemented by clients who make their own
 * type of reasoner input and want to support proof translation for their
 * reasoners. This is required in order to work properly with mathematical
 * language extensions in proofs. Proofs using reasoners with a non translatable
 * input will not be fully reusable.
 * </p>
 * 
 * @author beauger
 * @since 3.0
 */
public interface ITranslatableReasonerInput {

	/**
	 * Translates the formulas inside this reasoner input using the given
	 * formula factory.
	 * 
	 * @param factory
	 *            the factory to use for translation
	 * @return the translated reasoner input
	 * @throws IllegalArgumentException
	 *             if the translation fails
	 */
	IReasonerInput translate(FormulaFactory factory);
	
	/**
	 * Computes the required type environment to use for parsing this reasoner
	 * input.
	 * 
	 * @param factory
	 *            the factory to use for making the type environment
	 * @return the required type environment of this reasoner input
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory);
}
