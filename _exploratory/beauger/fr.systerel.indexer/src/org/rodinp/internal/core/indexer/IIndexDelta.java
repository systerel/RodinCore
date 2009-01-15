/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import org.rodinp.core.IRodinElement;

/**
 * @author Nicolas Beauger
 * 
 */
public interface IIndexDelta {

	public enum Kind {
		FILE_CHANGED, PROJECT_OPENED, PROJECT_CLOSED, PROJECT_CREATED,
		PROJECT_DELETED, PROJECT_CLEANED
	}

	IRodinElement getElement();

	Kind getKind();

}