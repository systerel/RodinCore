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
package org.eventb.internal.ui.propertiesView;

import org.eclipse.jface.viewers.IFilter;
import org.eventb.core.IDerivedPredicateElement;
import org.eventb.core.IGuard;

// TODO Remove when theorem in Guard is available.
public class DerivedPredicateNoGuardFilter implements IFilter {

	public boolean select(Object toTest) {
		return (toTest instanceof IDerivedPredicateElement)
				&& !(toTest instanceof IGuard);
	}
}
