/*******************************************************************************
 * Copyright (c) 2024 UPEC and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     UPEC - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pm;

import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Interface for an object that can run some tactics.
 *
 * @author Guillaume Verdier
 * @since 3.7
 */
@FunctionalInterface
public interface ITacticRunner {

	/**
	 * Run the provided tactic.
	 *
	 * The tactic can be executed directly (e.g., {@code tactic.accept(newMonitor)})
	 * or through a worker thread, as long as the runner waits for the worker to
	 * finish before returning.
	 *
	 * @param tactic tactic to run
	 */
	public void run(Consumer<IProgressMonitor> tactic);

}
