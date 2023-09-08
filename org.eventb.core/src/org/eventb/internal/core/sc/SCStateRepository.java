/*******************************************************************************
 * Copyright (c) 2006, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.tool.state.StateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class SCStateRepository extends StateRepository<ISCState> implements ISCStateRepository {

	public SCStateRepository(IEventBRoot root) throws CoreException {
		super(root);
	}

}
