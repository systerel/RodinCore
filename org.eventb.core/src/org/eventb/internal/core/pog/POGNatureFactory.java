/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.pog.IPOGNature;

/**
 * A singleton class to create unique instances of Proof Obligation Nature.
 * <p>
 * Use {@link POGNatureFactory#getNature(String)} to guarantee that two
 * {@link IPOGNature} with the same description are the same instance.
 * </p>
 * 
 * @author A. Gilles
 */
public class POGNatureFactory {

	private static final POGNatureFactory INSTANCE = new POGNatureFactory();

	private final Map<String, IPOGNature> natures;

	private POGNatureFactory() {
		natures = new HashMap<String, IPOGNature>();
	}

	public static POGNatureFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * Creates a POG Nature for <code>description</code>.
	 * 
	 * @param description
	 *            the description of the nature
	 * @return the unique POG Nature corresponding to the given description
	 * @see IPOGNature
	 * @since 1.3
	 */
	public synchronized IPOGNature getNature(String description) {
		IPOGNature nat = natures.get(description);
		if (nat == null) {
			nat = new POGNature(description);
			natures.put(description, nat);
		}
		return nat;
	}

}
