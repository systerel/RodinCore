/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.RodinDBException;

import fr.systerel.editor.EditorPlugin;

/**
 * Utility methods for the Rodin Editor.
 */
public class RodinEditorUtils {
	
	public static void debug(String message) {
		System.out.println(EditorPlugin.DEBUG_PREFIX + message);
	}
	
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, EditorPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		EditorPlugin.getDefault().getLog().log(status);
	}
	

	/**
	 * Opens an information dialog to the user displaying the given message.
	 *  
	 * @param message The dialog message.
	 */
	public static void showInfo(final String message) {
		showInfo(null, message);
	}

	/**
	 * Opens an information dialog to the user displaying the given message.
	 * 
	 * @param title
	 *            The title of the dialog
	 * @param message
	 *            The dialog message
	 */
	public static void showInfo(final String title, final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(getShell(), title, message);
			}
		});

	}

	/**
	 * Opens an information dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message.
	 */
	public static boolean showQuestion(final String message) {
		class Question implements Runnable {
			private boolean response;

			@Override
			public void run() {
				response = MessageDialog
						.openQuestion(getShell(), null, message);
			}

			public boolean getResponse() {
				return response;
			}
		}
		final Question question = new Question();
		syncExec(question);
		return question.getResponse();
	}
	
	/**
	 * Opens an error dialog to the user displaying the given message.
	 * 
	 * @param message
	 *            The dialog message displayed
	 * @param title 
	 */
	public static void showError(final String title, final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openError(getShell(), title, message);
			}
		});
	}
	
	/**
	 * Opens a warning dialog to the user displaying the given message.
	 * 
	 * @param title
	 *            The title of the dialog window
	 * @param message
	 *            The dialog message displayed
	 * 
	 */
	public static void showWarning(final String title, final String message) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(getShell(), title, message);
			}
		});
	}
	
	/**
	 * Opens an error dialog to the user showing the given unexpected error.
	 * 
	 * @param exc
	 *            The unexpected error.
	 * @param errorMessage
	 *            error message for logging
	 */
	public static void showUnexpectedError(final Throwable exc,
			final String errorMessage) {
		log(exc, errorMessage);
		final IStatus status;
		if (exc instanceof CoreException) {
			IStatus s = ((CoreException) exc).getStatus();
			status = new Status(s.getSeverity(), s.getPlugin(), s.getMessage()
					+ "\n" + errorMessage, s.getException());
		} else {
			final String msg = "Internal error " + errorMessage;
			status = new Status(IStatus.ERROR, PLUGIN_ID, msg, exc);
		}
		syncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(getShell(), null,
						"Unexpected error. See log for details.", status);
			}
		});
	}

	public static Shell getShell() {
		return EditorPlugin.getActiveWorkbenchWindow().getShell();
	}


	public static void syncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(runnable);
	}


	public static void asyncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(runnable);
	}

}
