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
