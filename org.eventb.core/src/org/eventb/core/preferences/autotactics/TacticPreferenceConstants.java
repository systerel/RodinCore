/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.core.preferences.autotactics;

/**
 * Constants used to retrieve eclipse preferences for tactics.
 * @since 2.1
 */
public class TacticPreferenceConstants {

	/**
	 * The preference identifier for post-tactics enablement.
	 */
	public static final String P_POSTTACTIC_ENABLE = "Post-Tactic enable";
	
	/**
	 * The preference for post-tactics selected profile.
	 */
	public static final String P_POSTTACTIC_CHOICE = "Post-Tactics Profil";

	/**
	 * The preference identifier for auto-tactics enablement.
	 */
	public static final String P_AUTOTACTIC_ENABLE = "Auto-Tactic enable";
	
	/**
	 * The preference for auto-tactics selected profile.
	 */
	public static final String P_AUTOTACTIC_CHOICE = "Auto-Tactics Profil";

	/**
	 * The preference containing all the tactic profiles.
	 */
	public static final String P_TACTICSPROFILES = "Tactics Map";
	
	/**
	 * Preference key enabling/disabling proof simplification
	 * @since 2.4
	 */
	public static final String P_SIMPLIFY_PROOFS = "Simplify Proofs";
	
}
