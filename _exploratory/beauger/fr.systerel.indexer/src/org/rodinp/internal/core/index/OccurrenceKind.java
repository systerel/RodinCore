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
package org.rodinp.internal.core.index;

import org.eclipse.core.runtime.Assert;
import org.rodinp.core.index.IOccurrenceKind;

/**
 * @author Laurent Voisin
 * 
 */
public class OccurrenceKind implements IOccurrenceKind {

	private final String id;
	private final String name;

	public OccurrenceKind(String id, String name) {
		Assert.isNotNull(id);
		Assert.isNotNull(name);
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return 31 + id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof OccurrenceKind))
			return false;
		final OccurrenceKind other = (OccurrenceKind) obj;
		return id.equals(other.id);
	}

}
