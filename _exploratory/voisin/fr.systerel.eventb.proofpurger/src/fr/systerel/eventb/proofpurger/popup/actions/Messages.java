package fr.systerel.eventb.proofpurger.popup.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "fr.systerel.eventb.proofpurger.popup.actions.messages"; //$NON-NLS-1$
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
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}
}
