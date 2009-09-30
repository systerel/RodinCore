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

package org.eventb.core.ast;

/**
 * Common protocol for reporting results of upgrading a mathematical formula to
 * a new language version.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface IUpgradeResult<T extends Formula<T>> extends IResult {

	/**
	 * Tells whether any action must be taken to ensure that the input string
	 * given to the upgrader is parseable in the target version of the
	 * mathematical language.
	 * 
	 * @return <code>true</code> iff the input formula would not be parsed
	 *         correctly in the target language version
	 */
	boolean upgradeNeeded();

	/**
	 * Returns the upgraded formula, that is a formula in the target language
	 * version which has the same meaning as the input string parsed with the
	 * language version just before the target one. When it is not possible to
	 * compute such an equivalent formula, <code>null</code> is returned.
	 * <p>
	 * In particular, if some error was encountered during upgrade (see
	 * {@link #hasProblem()}, <code>null</code> will be returned.
	 * </p>
	 * 
	 * @return the upgraded formula or <code>null</code> if not upgradable
	 * @see #hasProblem()
	 */
	T getUpgradedFormula();

}
