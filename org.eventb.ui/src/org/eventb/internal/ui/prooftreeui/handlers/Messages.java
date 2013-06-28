/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import org.eclipse.osgi.util.NLS;
import org.eventb.core.seqprover.IProofSkeleton;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eventb.internal.ui.prooftreeui.handlers.messages";

	// Abstract Proof Tree Command Handler
	public static String proofTreeHandler_commandNotExecutedTitle;
	public static String proofTreeHandler_editCommentTitle;
	public static String proofTreeHandler_commandNotEnabledInfo;
	public static String proofTreeHandler_moreThanOneElementSelected;
	public static String proofTreeHandler_elementIsNotProofTreeNode;
	public static String proofTreeHandler_copyBufferIsEmpty;
	public static String proofTreeHandler_copyBufferNotAProofSkeleton;
	public static String proofTreeHandler_commandError;
	public static String proofTreeHandler_noActiveProofTreeUIError;
	public static String proofTreeHandler_noActiveUserSupportError;
	public static String proofTreeHandler_invalidSelectionError;
	public static String proofTreeHandler_selectionNotProofTreeNodeError;
	
	public static String proofTreeHandler_pruneSuccessMessage;
	public static String proofTreeHandler_pasteSuccessMessage;
	public static String proofTreeHandler_copySuccessMessage;
	public static String proofTreeHandler_editSuccessMessage;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// no instantiation
	}

	public static String commandNotEnabledInfo(String commandID, String cause) {
		return bind(proofTreeHandler_commandNotEnabledInfo, commandID, cause);
	}

	public static String commandError(String commandID, String cause) {
		return bind(proofTreeHandler_commandError, commandID, cause);
	}

	public static String pasteSuccessMessage(IProofSkeleton node) {
		return bind(proofTreeHandler_pasteSuccessMessage, node);
	}

	public static String copySuccessMessage(Object node) {
		return bind(proofTreeHandler_copySuccessMessage, node);
	}
	
}
