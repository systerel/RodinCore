/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core;

/**
 * Interface for a level controller.
 *
 * @author François Terrier
 */
public interface ILevelController {
	
	/**
	 * Returns the current level.
	 * 
	 * @return the current level
	 */
	public Level getCurrentLevel();

	/**
	 * Advances the level controller to the next level.
	 * <p>
	 * The next level is defined as follows. If the left branch of the
	 * current level has not been closed yet, calling this method sets the
	 * current level to the left branch of the current level. If the left
	 * branch of the current level has already been closed, the current level
	 * becomes the right branch of the current level.
	 */
	public void nextLevel();

}
