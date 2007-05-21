package org.eventb.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.RodinDBException;

public class EventBUIExceptionHandler {

	/**
	 * The enumerated type <code>UserAwareness</code> specifies the awareness
	 * level of user in handling exceptions. The order of the awareness levels
	 * in the declaration is in relevant.
	 */
	public enum UserAwareness {
		IGNORE(0), INFORM(1);

		private final int code;

		UserAwareness(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

	}

	private static void handleException(Exception e, String msg,
			UserAwareness level) {
		if (e instanceof RodinDBException) {
			handleRodinDBException((RodinDBException) e, msg, level);
			return;
		}

		if (e instanceof CoreException) {
			handleCoreException((CoreException) e, msg, level);
			return;
		}

		if (UIUtils.DEBUG) {
			System.out.println(msg);
			e.printStackTrace();
		}
		UIUtils.log(e, msg);
	}

	private static void handleCoreException(CoreException exception,
			String msg, UserAwareness level) {
		IStatus status = exception.getStatus();
		if (level == UserAwareness.INFORM) {
			int severity = status.getSeverity();
			switch (severity) {
			case IStatus.ERROR:
				MessageDialog.openError(null, msg, status.getMessage());
				break;
			case IStatus.WARNING:
				MessageDialog.openWarning(null, msg, status.getMessage());
				break;
			case IStatus.INFO:
				MessageDialog.openInformation(null, msg, status.getMessage());
				break;
			}
		}
		if (UIUtils.DEBUG) {
			System.out.println(msg);
			exception.printStackTrace();
		}
		UIUtils.log(exception, msg);
	}

	private static void handleRodinDBException(RodinDBException exception,
			String msg, UserAwareness level) {
		IRodinDBStatus rodinDBStatus = exception.getRodinDBStatus();
		if (level == UserAwareness.INFORM) {
			int severity = rodinDBStatus.getSeverity();
			switch (severity) {
			case IStatus.ERROR:
				MessageDialog.openError(null, msg, rodinDBStatus.getMessage());
				break;
			case IStatus.WARNING:
				MessageDialog
						.openWarning(null, msg, rodinDBStatus.getMessage());
				break;
			case IStatus.INFO:
				MessageDialog.openInformation(null, msg, rodinDBStatus
						.getMessage());
				break;
			}
		}
		if (UIUtils.DEBUG) {
			System.out.println(msg);
			exception.printStackTrace();
		}
		UIUtils.log(exception, msg);
	}

	public static void handleCreateElementException(Exception e) {
		handleException(e, "Exception throws when creating a new element",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	public static void handleDeleteElementException(Exception e) {
		handleException(e, "Exception throws when deleting an element",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	public static void handleSetAttributeException(Exception e) {
		handleException(e, "Exception throws when setting an attribute",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	public static void handleGetPersistentPropertyException(Exception e) {
		handleException(e, "Exception throws when getting persistent property",
				EventBUIExceptionHandler.UserAwareness.IGNORE);
	}

	public static void handleSetPersistentPropertyException(Exception e) {
		handleException(e, "Exception throws when setting persistent property",
				EventBUIExceptionHandler.UserAwareness.IGNORE);
	}

	public static void handleGetChildrenException(Exception e) {
		handleException(e, "Exception throws when getting child elements",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

	public static void handleRemoveAttribteException(Exception e) {
		handleException(e, "Exception throws when removing element attribute",
				EventBUIExceptionHandler.UserAwareness.INFORM);
	}

}
