/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core.builder;

import java.io.Serializable;

/**
 * @author Stefan Hallerstede
 *
 */
public class Link implements Serializable {

	private static final long serialVersionUID = 7775979088227183917L;

	public enum Provider { USER, TOOL }
	public enum Priority { LOW, HIGH }

	protected final Provider prov; // tells wether this link was provided by a tool or by a user
	protected final Priority prio; // priority of this link
	protected final String id;     // links can be managed by their ids and their sources
	                	  		   // ids can be used to group sources
	protected final Node source; // source of link (target is the node that owns the link)
	protected final Node origin; // this is the nonderived resource to which error messages must be directed
                                 // if this link is part of a cycle
	
	
	public Link(Provider prov, Priority prio, String id, Node source, Node origin) {
		this.prov = prov;
		this.prio = prio;
		this.id = id;
		this.source = source;
		this.origin = origin;
	}
	
	@Override
	public String toString() {
		return source + " [" + id + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
