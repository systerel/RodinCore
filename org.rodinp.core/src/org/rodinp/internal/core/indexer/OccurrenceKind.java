/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.indexer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.rodinp.core.indexer.IOccurrenceKind;

/**
 * @author Laurent Voisin
 * 
 */
public class OccurrenceKind implements IOccurrenceKind {

	private static final Map<String, OccurrenceKind> instances = new HashMap<String, OccurrenceKind>();

	public static synchronized IOccurrenceKind newOccurrenceKind(String id,
			String name) {
		if (valueOf(id) != null) {
			throw new IllegalArgumentException("Duplicate id " + id);
		}
		final OccurrenceKind result = new OccurrenceKind(id, name);
		instances.put(id, result);
		return result;
	}

	public static IOccurrenceKind valueOf(String id) {
		return instances.get(id);
	}

	private final String id;
	private final String name;

	private OccurrenceKind(String id, String name) {
		Assert.isNotNull(id);
		Assert.isNotNull(name);
		this.id = id;
		this.name = name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
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
