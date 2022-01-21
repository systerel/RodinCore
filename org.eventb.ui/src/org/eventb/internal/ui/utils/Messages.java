/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
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
 *     Systerel - added messages for Proof Simplification
 *     Systerel - added messages for Dialogs
 *     Systerel - added messages for Proof Skeleton
 *     Systerel - added message for Tactics
 *     Systerel - added "expand section" preference
 *     Systerel - added messages for dialogs
 *     Systerel - added messages for the new prefix preference mechanism
 *     Systerel - added message for the simplify proof preference
 *******************************************************************************/
package org.eventb.internal.ui.utils;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;
import org.eventb.core.IPRProof;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;

public final class Messages {

	private static final String BUNDLE_NAME = "org.eventb.internal.ui.utils.messages"; //$NON-NLS-1$

	public static String error_unsupported_action;
	public static String error_cannot_save_as_message;
	
	public static String editorPage_axiomPage_title;
	public static String editorPage_axiomPage_tabTitle;
	public static String editorPage_eventPage_title;
	public static String editorPage_eventPage_tabTitle;
	public static String editorPage_constantPage_title;
	public static String editorPage_constantPage_tabTitle;
	public static String editorPage_variablePage_title;
	public static String editorPage_variablePage_tabTitle;
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
	public static String searchedHypothesis_toolItem_search_toolTipText;
	public static String searchedHypothesis_toolItem_refresh_toolTipText;
	public static String searchedHypothesis_toolItem_preferences;
	
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
	public static String preferencepage_modellingui_description;
	public static String preferencepage_modellingui_expandSections;
	public static String preferencepage_modellingui_showborders;
	
	public static String preferencepage_pomtactic_title;
	public static String preferencepage_pomtactic_enablementdescription;
	public static String preferencepage_pomtactic_selectedtacticprofiledescription;
	public static String preferencepage_posttactic_title;
	public static String preferencepage_posttactic_enablementdescription;
	public static String preferencepage_posttactic_selectedtacticprofiledescription;
	public static String preferencepage_postautotactic_description;
	public static String preferencepage_postautotactic_tab_autoposttactics;
	public static String preferencepage_postautotactic_tab_profiles;
	public static String preferencepage_postautotactic_tacticdetails_header;
	public static String preferencepage_postautotactic_newbutton;
	public static String preferencepage_postautotactic_editbutton;
	public static String preferencepage_postautotactic_removebutton;
	public static String preferencepage_postautotactic_duplicatebutton;
	public static String preferencepage_postautotactic_import_ws_profiles;
	public static String preferencepage_postautotactic_export_ws_profiles;
	
	public static String preferencepage_eventb_description;
	public static String preferencepage_provingui_description;
	public static String preferencepage_provingui_considerHiddenHypotheses;
	public static String preferencepage_seqprover_description;
	public static String preferencepage_seqprover_simplify_proofs;
	public static String preferencepage_twolistselectioneditor_availablelabel;
	public static String preferencepage_twolistselectioneditor_selectedlabel;
	public static String preferencepage_prefixSettings_description;
	public static String preferencepage_configureLink;
	public static String preferencepage_prefixSettings_propIDTitle;
	public static String preferencepage_enableProjectSpecifixSettings;
	
	// Wizard
	public static String wizard_editprofil_title;
	public static String wizard_editprofil_comb_description;
	public static String wizard_editprofil_comb_help;
	public static String wizard_editprofil_param_description;
	public static String wizard_editprofil_param_help;
	public static String wizard_editprofil_nameheader;
	public static String wizard_editprofil_profileexists;
	public static String wizard_editprofil_profilemustbespecified;
	public static String wizard_editprofile_shouldexist;
	public static String wizard_editprofile_choice_combined;
	public static String wizard_editprofile_choice_parameterized;
	public static String wizard_editprofile_error_cyclicrefs;
	public static String wizard_editprofile_error_invalidtactic;
	public static String wizard_editprofile_error_unresolvedrefs;
	public static String wizard_editprofile_page_choice_message;
	public static String wizard_editprofile_page_choice_title;
	public static String wizard_editprofile_page_choiceparam;
	public static String wizard_editprofile_page_choiceparam_message;
	public static String wizard_editprofile_page_choiceparam_nonefound;
	public static String wizard_editprofile_page_choiceparam_title;
	public static String wizard_editprofile_combedit_list_combinators;
	public static String wizard_editprofile_combedit_list_profiles;
	public static String wizard_editprofile_combedit_list_tactics;
	public static String wizard_editprofile_combedit_noselectedtactic;
	public static String tacticviewer_combined_action_delete;
	public static String tacticviewer_combined_unboundarity;
	public static String tacticlist_currentunsaved;

	// Attribute Manipulation
	public static String attributeManipulation_extended_true;
	public static String attributeManipulation_extended_false;
	public static String attributeManipulation_theorem_true;
	public static String attributeManipulation_theorem_false;

	// UIUtils
	public static String uiUtils_errorOpeningProvingEditor;
	public static String uiUtils_errorBPSAlreadyOpen;
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
	public static String proofskeleton_copy_title;
	public static String proofskeleton_copy_toolTip;
	public static String proofskeleton_buildfailed;
	public static String proofskeleton_proofdoesnotexist;

	// Proof Simplification
	public static String proofSimplification_fetchingProofs;
	public static String proofSimplification_invalidSelection;
	public static String proofSimplification_couldNotRun;
	public static String proofSimplification_noProofsToSimplify;
	public static String proofSimplification_symplifyingProofs;
	
	// Dialogs
	public static String dialogs_invalidIdentifiers;
	public static String dialogs_duplicateNames;
	public static String dialogs_readOnlyElement;
	public static String dialogs_nothingToPaste;
	public static String dialogs_pasteNotAllowed;
	public static String dialogs_canNotGetChildren;
	public static String dialogs_elementDoesNotExist;
	public static String dialogs_po_Deleted;
	public static String dialogs_prover_error_creating_page;
	public static String dialogs_cancel_renaming;
	public static String dialogs_projectSelection_title;
	public static String dialogs_projectSelection_description;
	public static String dialogs_new_component_title;
	
	//Dialog titles
	public static String title_error;
	public static String title_unexpectedError;
	public static String title_nothingToPaste;
	public static String title_canNotPaste;
	public static String title_po_Deleted;
	public static String title_prover_editor;
	
	// Tactics
	public static String tactics_replaceWith;
	
	// Rename action
	public static String rename_renaming_root;
	public static String rename_task_wait_for_indexer;
	public static String rename_task_perform_renaming;

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
	
	public static String dialogs_canNotGetChildren(IParent parent) {
		return bind(dialogs_canNotGetChildren, parent);
	}
	
	public static String dialogs_nothingToPaste(IRodinElement element) {
		return bind(dialogs_nothingToPaste, element);
	}
	
	public static String dialogs_pasteNotAllowed(String name1, String name2) {
		return bind(dialogs_pasteNotAllowed, name1, name2);
	}
	
	public static String dialogs_elementDoesNotExist(IRodinElement element) {
		return bind(dialogs_elementDoesNotExist, element);
	}
	
	public static String dialogs_readOnlyElement(String str) {
		return bind(dialogs_readOnlyElement, str);
	}
	
	public static String dialogs_cancelRenaming(String s1){
		return bind(dialogs_cancel_renaming, s1);
	}

	public static String tactics_replaceWith(String s1, String s2){
		return bind(tactics_replaceWith, s1, s2);
	}

	public static String proofSkeleton_cantDisplay(IPRProof proof, String reason) {
		return bind(proofskeleton_cantdisplayproof, proof.getElementName(),
				reason);
	}
	
	public static String preferencepage_prefix_propertyPageTitle(String projectName){
		return bind(preferencepage_prefixSettings_propIDTitle, projectName);
	}
	
	public static String renameElement(String rootName, String newBareName) {
		return bind(rename_renaming_root, rootName, newBareName);
	}

	private Messages() {
		// Do not instantiate
	}

}
