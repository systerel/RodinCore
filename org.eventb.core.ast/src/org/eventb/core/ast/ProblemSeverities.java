/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * This contains all possible problem severities.
 * <p>
 * If a problem added to a result is an error, it should cause the corresponding
 * result to indicate a failure.
 * 
 * @author François Terrier
 *
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ProblemSeverities {
	
	/**
	 * Problem is ignored
	 */
	int Ignore = -1;
	/**
	 * Problem is a warning
	 */
	int Warning = 0;
	/**
	 * Problem is an error
	 */
	int Error = 1;
}
