/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is a class which store utility static method that can be used in
 *         the development
 */
public class UIUtils {

	public static boolean DEBUG = false;

	public static void log(Throwable exc, String message) {
		Throwable nestedException;
		if (exc instanceof RodinDBException
				&& (nestedException = ((RodinDBException) exc).getException()) != null) {
			exc = nestedException;
		}
		IStatus status = new Status(IStatus.ERROR, EventBUIPlugin.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		EventBUIPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Method to return the openable for an object (IRodinElement or TreeNode).
	 * <p>
	 * 
	 * @param node
	 *            A Rodin Element or a tree node
	 * @return The IRodinFile corresponding to the input object
	 */
	public static IOpenable getOpenable(Object node) {
		if (node instanceof TreeNode)
			return ((IRodinElement) ((TreeNode) node).getParent())
					.getOpenable();
		else if (node instanceof IRodinElement)
			return ((IRodinElement) node).getOpenable();

		return null;
	}

	/**
	 * Link the current object to an Prover UI editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. a proof obligation or a Rodin file)
	 */
	public static void linkToProverUI(Object obj) {
		String editorId = ProverUI.EDITOR_ID;

		IPSFile component = null;
		if (obj instanceof IRodinProject)
			return;

		if (!(obj instanceof IRodinElement))
			return;

		IRodinElement element = (IRodinElement) obj;

		IOpenable file = element.getOpenable();

		if (file instanceof IPSFile) {
			component = (IPSFile) file;
		} else if (file instanceof IMachineFile) {
			component = ((IMachineFile) file).getPSFile();
		} else if (file instanceof IContextFile) {
			component = ((IContextFile) file).getPSFile();
		} else {
			return; // Not recorgnised
		}

		Assert
				.isTrue(component != null,
						"Component must be initialised by now");
		try {
			IEditorInput fileInput = new FileEditorInput(component
					.getResource());

			ProverUI editor = (ProverUI) EventBUIPlugin.getActivePage()
					.openEditor(fileInput, editorId);
			if (obj instanceof IPSStatus)
				editor.setCurrentPO((IPSStatus) obj, new NullProgressMonitor());
		} catch (PartInitException e) {
			MessageDialog
					.openError(null, null, "Error open the proving editor");
			e.printStackTrace();
			// TODO EventBImage.logException(e);
		}
		return;
	}

	/**
	 * Link the current object to an Event-B editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */
	public static void linkToEventBEditor(Object obj) {

		IRodinFile component;

		if (!(obj instanceof IRodinProject)) {
			component = (IRodinFile) UIUtils.getOpenable(obj);
			try {
				IEditorInput fileInput = new FileEditorInput(component
						.getResource());
				String editorId = "";
				if (component instanceof IMachineFile) {
					editorId = EventBMachineEditor.EDITOR_ID;
				} else if (component instanceof IContextFile) {
					editorId = EventBContextEditor.EDITOR_ID;
				}
				IEventBEditor editor = (IEventBEditor) EventBUIPlugin
						.getActivePage().openEditor(fileInput, editorId);
				editor.edit(obj);
			} catch (PartInitException e) {
				MessageDialog.openError(null, null,
						"Error open the Event-B Editor");
				e.printStackTrace();
				// TODO EventBUIPlugin.logException(e);
			}
		}
		return;
	}

	/**
	 * Convert a string input to XML format by replacing special characters (&, <,
	 * >).
	 * <p>
	 * 
	 * @param input
	 *            the input string
	 * @return a string corresponding to the input in XML format
	 */
	public static String XMLWrapUp(String input) {
		String output = input;
		output = output.replaceAll("&", "&amp;");
		output = output.replaceAll("<", "&lt;");
		output = output.replaceAll(">", "&gt;");
		return output;
	}

	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * 
	 * @param link
	 *            a String
	 * @return XML formatted string represents the link
	 */
	public static String makeHyperlink(String link) {
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">"
				+ UIUtils.XMLWrapUp(link) + "</a>";
	}

	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * 
	 * @param link
	 *            a String
	 * @return XML formatted string represents the link
	 */
	public static String makeHyperlink(String link, String text) {
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">"
				+ UIUtils.XMLWrapUp(text) + "</a>";
	}

	/**
	 * Activate a particular view given the ID.
	 * <p>
	 * 
	 * @param View_ID
	 *            the ID of the view which will be activate
	 */
	public static void activateView(String View_ID) {
		IViewPart aView = EventBUIPlugin.getActivePage().findView(View_ID);
		if (aView != null) {
			EventBUIPlugin.getActivePage().activate(aView);
		}
		return;
	}

	/**
	 * Activate a particular view given the ID.
	 * <p>
	 * 
	 * @param view_ID
	 *            the ID of the view which will be activate
	 */
	public static void showView(String view_ID) {
		try {
			EventBUIPlugin.getActivePage().showView(view_ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Running a runable asynchronously.
	 * <p>
	 * 
	 * @param r
	 *            the runnable
	 * @param ctrl
	 *            the control that the runnable attached to
	 */
	public static void asyncPostRunnable(final Runnable r, Control ctrl) {
		final Runnable trackedRunnable = new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					// removePendingChange();
				}
			}
		};
		if (ctrl != null && !ctrl.isDisposed()) {
			try {
				ctrl.getDisplay().asyncExec(trackedRunnable);
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e;
			}
		}
	}

	/**
	 * Running a runable synchronously.
	 * <p>
	 * 
	 * @param r
	 *            the runnable
	 * @param ctrl
	 *            the control that the runnable attached to
	 */
	public static void syncPostRunnable(final Runnable r, Control ctrl) {
		final Runnable trackedRunnable = new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					// removePendingChange();
				}
			}
		};
		if (ctrl != null && !ctrl.isDisposed()) {
			try {
				ctrl.getDisplay().syncExec(trackedRunnable);
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e;
			}
		}
	}

	public static String getNamePrefix(IEventBEditor editor,
			IInternalElementType<?> type, String defaultPrefix) {
		return "internal_" + getPrefix(editor, type, defaultPrefix);
	}

	public static String getPrefix(IEventBEditor editor,
			IInternalElementType<?> type, String defaultPrefix) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					getQualifiedName(type));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (prefix == null)
			prefix = defaultPrefix;
		return prefix;
	}

	public static <T extends IInternalElement> String getFreeElementName(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type,
			String defaultPrefix) throws RodinDBException {
		String prefix = getNamePrefix(editor, type, defaultPrefix);
		return prefix + getFreeElementNameIndex(editor, parent, type, prefix);
	}

	public static <T extends IInternalElement> int getFreeElementNameIndex(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String prefix)
			throws RodinDBException {
		return getFreeElementNameIndex(parent, type, prefix, 1);
	}

	public static <T extends IInternalElement> int getFreeElementNameIndex(
			IInternalParent parent, IInternalElementType<T> type,
			String prefix, int beginIndex) throws RodinDBException {

		T[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			T element = parent.getInternalElement(type, prefix + i);
			if (!element.exists())
				break;
		}
		return i;
	}

	public static <T extends ILabeledElement> String getFreeElementLabel(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String defaultPrefix) throws RodinDBException {
		String prefix = getPrefix(editor, type, defaultPrefix);
		return prefix + getFreeElementLabelIndex(editor, parent, type, prefix);
	}

	public static <T extends ILabeledElement> int getFreeElementLabelIndex(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String prefix)
			throws RodinDBException {
		return getFreeElementLabelIndex(editor, parent, type, prefix, 1);
	}

	public static <T extends ILabeledElement> int getFreeElementLabelIndex(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String prefix, int beginIndex)
			throws RodinDBException {

		T[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			boolean exists = false;
			for (T element : elements) {
				if (element.hasAttribute(EventBAttributes.LABEL_ATTRIBUTE)
						&& element.getLabel().equals(prefix + i)) {
					exists = true;
					break;
				}
			}
			if (!exists)
				break;
		}
		return i;
	}

	public static <T extends IIdentifierElement> String getFreeElementIdentifier(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String defaultPrefix) throws RodinDBException {
		String prefix = getPrefix(editor, type, defaultPrefix);
		return prefix
				+ getFreeElementIdentifierIndex(editor, parent, type, prefix);
	}

	public static <T extends IIdentifierElement> int getFreeElementIdentifierIndex(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String prefix)
			throws RodinDBException {
		return getFreeElementIdentifierIndex(editor, parent, type, prefix, 1);
	}

	public static <T extends IIdentifierElement> int getFreeElementIdentifierIndex(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type, String prefix, int beginIndex)
			throws RodinDBException {

		T[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			boolean exists = false;
			for (T element : elements) {
				if (element.hasAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE)
						&& element.getIdentifierString().equals(prefix + i)) {
					exists = true;
					break;
				}
			}
			if (!exists)
				break;
		}
		return i;
	}

	public static <T extends ILabeledElement> T getFirstChildOfTypeWithLabel(
			IParent parent, IInternalElementType<T> type, String label)
			throws RodinDBException {

		for (T child : parent.getChildrenOfType(type)) {
			if (child.hasAttribute(EventBAttributes.LABEL_ATTRIBUTE)
					&& label.equals(child.getLabel()))
				return child;
		}
		return null;
	}

	public static Collection<IRodinElement> addToTreeSet(
			Collection<IRodinElement> set, IRodinElement element) {
		Collection<IRodinElement> delete = new ArrayList<IRodinElement>();
		for (IRodinElement member : set) {
			if (element.isAncestorOf(member))
				return set;
			else if (member.isAncestorOf(element)) {
				delete.add(member);
			}
		}
		set.removeAll(delete);
		set.add(element);
		return set;
	}

	/**
	 * Sets the given string attribute of the given Rodin element to the given
	 * new value, if it is not already set. In case the new value is empty, the
	 * attribute is removed.
	 * 
	 * @param element
	 * @param attrType
	 * @param newValue
	 * @param pm
	 */
	public static void setStringAttribute(IInternalElement element,
			IAttributeType.String attrType, String newValue, IProgressMonitor pm) {
		try {
			if (newValue.length() == 0) {
				if (element.hasAttribute(attrType)) {
					element.removeAttribute(attrType, pm);
				}
			} else if (!element.hasAttribute(attrType)
					|| !newValue.equals(element.getAttributeValue(attrType))) {
				element.setAttributeValue(attrType, newValue, pm);
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "Error changing attribute " + attrType.getId()
					+ " for element " + element.getElementName());
			if (UIUtils.DEBUG)
				e.printStackTrace();
		}
	}

	public static <T extends IInternalElement> String getFreeChildName(
			IEventBEditor editor, IInternalParent parent,
			IInternalElementType<T> type) throws RodinDBException {
		String defaultPrefix = "element"; // TODO Get this from extentions
		String prefix = getNamePrefix(editor, type, defaultPrefix);
		return prefix + getFreeElementNameIndex(editor, parent, type, prefix);
	}

	public static QualifiedName getQualifiedName(
			IInternalElementType type) {
		return new QualifiedName(EventBUIPlugin.PLUGIN_ID, type.getId());
	}

}
