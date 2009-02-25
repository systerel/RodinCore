/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" and "font color" options
 *     Systerel - added new options
 *     Systerel - added messages for UIUtils
 *     Systerel - replaced inherited by extended
 *******************************************************************************/
package org.eventb.internal.ui.utils;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public class Messages {
	private static final String BUNDLE_NAME = "org.eventb.internal.ui.utils.messages"; //$NON-NLS-1$

	public static String editorPage_axiomPage_title;
	public static String editorPage_axiomPage_tabTitle;
	public static String editorPage_eventPage_title;
	public static String editorPage_eventPage_tabTitle;
	public static String editorPage_constantPage_title;
	public static String editorPage_constantPage_tabTitle;
	public static String editorPage_variablePage_title;
	public static String editorPage_variablePage_tabTitle;
	public static String editorPage_theoremPage_title;
	public static String editorPage_theoremPage_tabTitle;
	public static String editorPage_carrierSetPage_title;
	public static String editorPage_carrierSetPage_tabTitle;
	public static String editorPage_dependencyPage_title;
	public static String editorPage_dependencyPage_tabTitle;
	public static String editorPage_invariantPage_title;
	public static String editorPage_invariantPage_tabTitle;
	public static String editorPage_prettyPrint_title;
	public static String editorPage_prettyPrint_tabTitle;
	public static String editorPage_html_title;
	public static String editorPage_html_tabTitle;
	public static String editorPage_edit_title;
	public static String editorPage_edit_tabTitle;
	public static String editorPage_synthethicViewPage_title;
	public static String editorPage_synthethicViewPage_tabTitle;
	
	public static String editorAction_paste_title;
	public static String editorAction_paste_toolTip;
	public static String editorAction_copy_title;
	public static String editorAction_copy_toolTip;
	
	// Cached hypotheses view
	public static String cachedHypothesis_defaultMessage;
	public static String cachedHypothesis_toolItem_add_toolTipText;
	public static String cachedHypothesis_toolItem_remove_toolTipText;
	public static String cachedHypothesis_toolItem_selectAll_toolTipText;
	public static String cachedHypothesis_toolItem_inverseSelection_toolTipText;
	public static String cachedHypothesis_toolItem_selectNone_toolTipText;
	
	// Searched hypotheses view
	public static String searchedHypothesis_defaultMessage;
	public static String searchedHypothesis_toolItem_add_toolTipText;
	public static String searchedHypothesis_toolItem_remove_toolTipText;
	public static String searchedHypothesis_toolItem_selectAll_toolTipText;
	public static String searchedHypothesis_toolItem_inverseSelection_toolTipText;
	public static String searchedHypothesis_toolItem_selectNone_toolTipText;
	
	// Selected hypotheses
	public static String selectedHypothesis_toolItem_remove_toolTipText;
	public static String selectedHypothesis_toolItem_selectAll_toolTipText;
	public static String selectedHypothesis_toolItem_inverseSelection_toolTipText;
	public static String selectedHypothesis_toolItem_selectNone_toolTipText;

	// Preference pages
	public static String preferencepage_contexteditor_description;
	public static String preferencepage_contexteditor_editorpagedescription;
	public static String preferencepage_machineeditor_description;
	public static String preferencepage_machineeditor_editorpagedescription;
	public static String preferencepage_editorpage_tab_title;
	public static String preferencepage_modellingui_description;
	public static String preferencepage_pomtactic_description;
	public static String preferencepage_pomtactic_enablementdescription;
	public static String preferencepage_pomtactic_selectedtacticsdescription;
	public static String preferencepage_posttactic_description;
	public static String preferencepage_posttactic_enablementdescription;
	public static String preferencepage_posttactic_selectedtacticsdescription;
	public static String preferencepage_eventb_description;
	public static String preferencepage_provingui_description;
	public static String preferencepage_seqprover_description;
	public static String preferencepage_twolistselectioneditor_availablelabel;
	public static String preferencepage_twolistselectioneditor_selectedlabel;
	public static String preferencepage_appearance_description;
	public static String preferencepage_colorsandfonts_description;
	public static String preferencepage_colorsandfonts_showborders;
	public static String preferencepage_colorsandfonts_textForeground;
	public static String preferencepage_colorsandfonts_commentForeground;
	public static String preferencepage_colorsandfonts_requiredfieldbackground;
	public static String preferencepage_colorsandfonts_dirtystatecolor;
	public static String preferencepage_colorsandfonts_boxbordercolor;
	

	// Attribute Manipulation
	public static String attributeManipulation_extended_true;
	public static String attributeManipulation_extended_false;

	// UIUtils
	public static String uiUtils_errorOpeningProvingEditor;
	public static String uiUtils_unexpectedError;

	// Proof Purger
	public static String filepurgeaction_rodindberror;
	public static String filepurgeaction_invalidselection;
	public static String filepurgeaction_noproofstopurge;
	public static String filepurgeaction_usedproofs;
	public static String filepurgeaction_runningpurgeroperation;
	public static String proofpurger_computingunusedproofs;
	public static String proofpurger_deleting;
	public static String proofpurger_deletingselectedproofs;
	public static String proofpurger_extractingprooffiles;
	public static String proofpurger_extractingunusedproofs;
	public static String proofpurger_savingchanges;
	public static String proofpurger_tryingtodeleteusedproofs;
	public static String proofpurger_verifyingselectedproofs;
	public static String proofpurgerselectiondialog_delete;
	public static String proofpurgerselectiondialog_proofpurgerselection;
	public static String proofpurgerselectiondialog_selectproofstodelete;
	public static String proofpurger_tryingtodeleteusedfiles;
	
	// Proof Skeleton
	public static String proofskeleton_noproof;
	public static String proofskeleton_cantdisplayproof;
	public static String proofskeleton_pendingnode;
	public static String proofskeleton_turnstile;
	public static String proofskeleton_copy_title;
	public static String proofskeleton_copy_toolTip;


	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Bind the given message's substitution locations with the given string
	 * values.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param bindings
	 *            An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}
	
	private Messages() {
		// Do not instantiate
	}

}
