/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" and "font color" options
 *     Systerel - added new options
 *     Systerel - added constants for preference page ids
 *     Systerel - added constant for expand section preference
 *     Systerel - added constants to handle new prefix preferences
 *     Systerel - added constants to handle auto/post-tactics profiles preferences
 *     Systerel - moved auto/post-tactics preferences to EventB Core
 *******************************************************************************/
package org.eventb.internal.ui.preferences;


/**
 * @author htson
 *         <p>
 *         Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	/**
	 * Unique identifier of the preference page for event-B
	 */
	public static final String EVENTB_PAGE_ID = "org.eventb.ui.preferences.eventB";

	/**
	 * Unique identifier of the preference page for the proving GUI.
	 */
	public static final String PROVING_UI_PAGE_ID = "org.eventb.ui.preferences.provingUI";

	/**
	 * Unique identifier of the preference page for appearance.
	 */
	public static final String APPEARANCE_PAGE_ID = "org.eventb.ui.preferences.appearance";

	/**
	 * Unique identifier of the preference page for colors and fonts.
	 */
	public static final String COLORS_AND_FONTS_PAGE_ID = "org.eventb.ui.colorsAndFonts";

	/**
	 * Unique identifier of the preference page for the sequent prover.
	 */
	public static final String SEQUENT_PROVER_PAGE_ID = "org.eventb.ui.preferences.seqProver";

	/**
	 * Unique identifier of the preference page for the Auto/Post Tactic settings.
	 */
	public static final String AUTO_POST_TACTIC_PREFERENCE_PAGE_ID = "org.eventb.ui.preferences.autoPostTactic";

	/**
	 * Unique identifier of the preference page for the modeling GUI.
	 */
	public static final String MODELLING_UI_PAGE_ID = "org.eventb.ui.preferences.modellingUI";

	/**
	 * Unique identifier of the preference page for the machine editor.
	 */
	public static final String MACHINE_EDITOR_PAGE_ID = "org.eventb.ui.preferences.machineEditor";

	/**
	 * Unique identifier of the preference page for the context editor.
	 */
	public static final String CONTEXT_EDITOR_PAGE_ID = "org.eventb.ui.preferences.contextEditor";
	
	/**
	 * Unique identifier of the preference page for the prefix settings.
	 */
	public static final String PREFIX_PREFERENCE_PAGE_ID = "org.eventb.ui.preferences.prefixPage";
	
	/**
	 * Preference key for the list of machine editor pages.
	 */
	public static final String P_MACHINE_EDITOR_PAGE = "Machine editor pages"; //$NON-NLS-1$

	/**
	 * Preference key for the list of context editor pages.
	 */
	public static final String P_CONTEXT_EDITOR_PAGE = "Context editor pages"; //$NON-NLS-1$

	/**
	 * Preference key for the enablement of border drawing.
	 */
	public static final String P_BORDER_ENABLE = "Border enable"; //$NON-NLS-1$

	/**
	 * Preference key for the choice of font color.
	 */
	public static final String P_TEXT_FOREGROUND = "Text foreground"; //$NON-NLS-1$
	
	/**
	 * Preference key for the choice of font color for comment.
	 */
	public static final String P_COMMENT_FOREGROUND = "Comment foreground"; //$NON-NLS-1$
	
	/**
	 * Preference key for the choice of required field background color
	 */
	public static final String P_REQUIRED_FIELD_BACKGROUND = "Required field background";

	/**
	 * Preference key for the choice of dirty state color
	 */
	public static final String P_DIRTY_STATE_COLOR = "Dirty state color";
	
	/**
	 * Preference key for the choice of box border color
	 */
	public static final String P_BOX_BORDER_COLOR = "Box border color";

	/**
	 * Preference key for considering hidden hypotheses in search
	 */
	public static final String P_CONSIDER_HIDDEN_HYPOTHESES = "Consider hidden hypotheses in search";
	
	/**
	 * Preference key enabling/disabling the highlight for selected text
	 */
	public static final String P_HIGHLIGHT_IN_PROVERUI = "Highlight the selection in prover views";
	
	/**
	 * Preference key for recursive expand
	 */
	public static final String P_EXPAND_SECTIONS = "Expand sections when unfolding an element";
	
	/**
	 * Preference key for axiom auto naming prefix
	 */
	public static final String P_PREFIX = "p_prefix_";
	
	/**
	 * Preference key indicating if some value is project specific
	 */
	public static final String P_SPECIFIC = "project_specific";
}
