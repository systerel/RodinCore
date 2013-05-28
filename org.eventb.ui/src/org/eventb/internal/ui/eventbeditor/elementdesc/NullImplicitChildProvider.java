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
package org.eventb.internal.ui.eventbeditor.elementdesc;

import java.util.Collections;
import java.util.List;

import org.eventb.ui.IImplicitChildProvider;
import org.rodinp.core.IInternalElement;

/**
 * A default implicit child provider which returns an empty list of implicit
 * children.
 */
public class NullImplicitChildProvider implements IImplicitChildProvider {

	@Override
	public List<? extends IInternalElement> getImplicitChildren(
			IInternalElement parent) {
		return Collections.emptyList();
	}

}
