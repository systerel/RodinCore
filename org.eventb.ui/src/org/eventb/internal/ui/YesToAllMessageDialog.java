/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public class YesToAllMessageDialog extends MessageDialog {

	public static int YES = 0;
	public static int NO = 1;
	public static int YES_TO_ALL = 2;
	public static int NO_TO_ALL = 3;
	
	public YesToAllMessageDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
	}

	public static int openYesNoToAllQuestion(Shell parent, String title,
			String message) {
		MessageDialog dialog = new MessageDialog(parent, title, null, // accept
				// the
				// default
				// window
				// icon
				message, QUESTION, new String[] { IDialogConstants.YES_LABEL,
						IDialogConstants.NO_LABEL,
						IDialogConstants.YES_TO_ALL_LABEL,
						IDialogConstants.NO_TO_ALL_LABEL }, 0); // yes is the
																// default
		return dialog.open();
	}

}
