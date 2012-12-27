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
package org.eventb.core.pog;

import org.eventb.internal.core.pog.POGNatureFactory;

/**
 * Common protocol for the nature of a Proof Obligation.
 * <p>
 * Instances of this interface are guaranteed to be unique for a given
 * description. As a consequence, natures can be compared using the
 * <code>==</code> equality.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. Instead, use
 * {@link POGProcessorModule#makeNature(String)} to create a Nature.
 * </p>
 * 
 * @see POGProcessorModule
 * @author A. Gilles
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 1.3
 */
public interface IPOGNature {

	public static final IPOGNature ACTION_FEASIBILITY = POGNatureFactory
			.getInstance().getNature("Feasibility of action");

	public static final IPOGNature ACTION_SIMULATION = POGNatureFactory
			.getInstance().getNature("Action simulation");

	public static final IPOGNature ACTION_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of action");

	public static final IPOGNature AXIOM_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of Axiom");

	public static final IPOGNature COMMON_VARIABLE_EQUALITY = POGNatureFactory
			.getInstance().getNature("Equality of common variables");

	public static final IPOGNature EVENT_VARIANT = POGNatureFactory
			.getInstance().getNature("Variant of event");

	public static final IPOGNature EVENT_VARIANT_IN_NAT = POGNatureFactory
			.getInstance().getNature("Natural number variant of event");

	public static final IPOGNature GUARD_STRENGTHENING_MERGE = POGNatureFactory
			.getInstance().getNature("Guard strengthening (merge)");

	public static final IPOGNature GUARD_STRENGTHENING_SPLIT = POGNatureFactory
			.getInstance().getNature("Guard strengthening (split)");

	public static final IPOGNature GUARD_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of Guard");

	public static final IPOGNature INVARIANT_ESTABLISHMENT = POGNatureFactory
			.getInstance().getNature("Invariant  establishment");

	public static final IPOGNature INVARIANT_PRESERVATION = POGNatureFactory
			.getInstance().getNature("Invariant  preservation");

	public static final IPOGNature INVARIANT_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of Invariant");

	public static final IPOGNature THEOREM = POGNatureFactory.getInstance()
			.getNature("Theorem");

	public static final IPOGNature THEOREM_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of Theorem");

	public static final IPOGNature VARIANT_FINITENESS = POGNatureFactory
			.getInstance().getNature("Finiteness of variant");

	public static final IPOGNature VARIANT_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of variant");

	public static final IPOGNature WITNESS_FEASIBILITY = POGNatureFactory
			.getInstance().getNature("Feasibility of witness");

	public static final IPOGNature WITNESS_WELL_DEFINEDNESS = POGNatureFactory
			.getInstance().getNature("Well-definedness of witness");

	/**
	 * Returns a more descriptive name of this nature.
	 * 
	 * @return a String description
	 */
	public String getDescription();

}
