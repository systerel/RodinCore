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
package org.eventb.core.pm;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Common protocol for tactic profiles contributed through extension point
 * <code>org.eventb.core.tacticProfiles</code>.
 * 
 * @author beauger
 * @since 3.0
 */
public interface ITacticProfileContribution extends IExecutableExtensionFactory {

	@Override
	ITacticDescriptor create() throws CoreException;
}
