/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.rubin.tests;

import org.eventb.rubin.Sequent;

/**
 * Represents a simple problem, that is a sequent whose status is known.
 * 
 * @author Laurent Voisin.
 */
public interface IProblem {

	/**
	 * @return the name of this problem
	 */
	String name();

	/**
	 * @return the sequent of this problem
	 */
	Sequent sequent();

	/**
	 * @return the expected status of this problem
	 */
	ProblemStatus status();

	/**
	 * @return another problem derived from this problem but which should be
	 *         invalid
	 */
	IProblem invalidVariant();

}