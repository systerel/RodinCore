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

import java.util.Collection;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.ElementAttributeInputDialog;
import org.eventb.internal.ui.eventbeditor.EventBContextEditor;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.eventb.internal.ui.eventbeditor.EventBMachineEditor;
import org.eventb.internal.ui.eventbeditor.NewEnumeratedSetInputDialog;
import org.eventb.internal.ui.eventbeditor.NewEventInputDialog;
import org.eventb.internal.ui.eventbeditor.actions.PrefixActName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixAxmName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixCstName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixEvtName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixGrdName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixSetName;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVarName;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.eventb.internal.ui.prover.ProverUI;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is a class which store utility static method that can be used in
 *         the development
 */
public class UIUtils {

	public static boolean DEBUG = false;

	/**
	 * Print out the message if the <code>DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debug(String message) {
		if (DEBUG)
			System.out.println(message);
	}

	/**
	 * Print out the message if the <code>EventBEditor.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugEventBEditor(String message) {
		if (EventBEditor.DEBUG)
			System.out.println("*** EventBEditor *** " + message);
	}

	/**
	 * Print out the message if the <code>ProjectExplorer.DEBUG</code> flag is
	 * <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugProjectExplorer(String message) {
		if (ProjectExplorer.DEBUG)
			System.out.println("*** Project Explorer *** " + message);
	}

	/**
	 * Print out the message if the <code>ObligationExplorer.DEBUG</code> flag
	 * is <code>true</code>.
	 * <p>
	 * 
	 * @param message
	 *            the messege to print out
	 */
	public static void debugObligationExplorer(String message) {
		if (ObligationExplorer.DEBUG)
			System.out.println(message);
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
	 * @author htson
	 *         <p>
	 *         Class which provide the label for Rodin elements.
	 */
	public static class ElementLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			try {
				if (obj instanceof IIdentifierElement)

					return ((IIdentifierElement) obj).getIdentifierString();

				if (obj instanceof ILabeledElement)
					return ((ILabeledElement) obj).getLabel(null);

				if (obj instanceof ISeesContext)
					return ((ISeesContext) obj).getSeenContextName();

				if (obj instanceof IRefinesMachine)
					return ((IRefinesMachine) obj).getAbstractMachineName();

				if (obj instanceof IExtendsContext)
					return ((IExtendsContext) obj).getAbstractContextName();
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
			return "";
		}

		public Image getImage(Object obj) {
			if (obj instanceof IRodinElement)
				return EventBImage.getRodinImage((IRodinElement) obj);
			return null;
		}
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

		IPRFile component = null;
		if (obj instanceof IRodinProject)
			return;
		if (obj instanceof IPRFile)
			component = (IPRFile) obj;
		else if (obj instanceof IRodinElement)
			component = (IPRFile) ((IRodinElement) obj).getParent();
		Assert
				.isTrue(component != null,
						"component must be initialised by now");
		try {
			UIUtils.debugObligationExplorer("Link to : " + obj);

			IEditorInput fileInput = new FileEditorInput(component
					.getResource());

			ProverUI editor = (ProverUI) EventBUIPlugin.getActivePage()
					.openEditor(fileInput, editorId);
			if (!(obj instanceof IPRFile))
				editor.setCurrentPO((IPRSequent) obj);
		} catch (PartInitException e) {
			MessageDialog.openError(null, null, "Error open the editor");
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
				EventBEditor editor = (EventBEditor) EventBUIPlugin
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
					// if (UIUtils.DEBUG) System.out.println("Runned");
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
					// if (UIUtils.DEBUG) System.out.println("Runned");
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

	/**
	 * Utility method to create a event with its local variables, guards and
	 * actions using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the event will be created in
	 */
	public static void newEvent(final EventBEditor editor,
			final IRodinFile rodinFile, IProgressMonitor monitor) {
		try {
			String evtName = getFreeElementLabel(editor, rodinFile,
					IEvent.ELEMENT_TYPE, PrefixEvtName.QUALIFIED_NAME,
					PrefixEvtName.DEFAULT_PREFIX);

			final NewEventInputDialog dialog = new NewEventInputDialog(Display
					.getCurrent().getActiveShell(), "New Events", evtName);

			dialog.open();
			final String name = dialog.getName();
			if (name != null) {

				RodinCore.run(new IWorkspaceRunnable() {

					public void run(IProgressMonitor monitor)
							throws CoreException {
						String[] varNames = dialog.getVariables();
						String[] grdNames = dialog.getGrdNames();
						String[] grdPredicates = dialog.getGrdPredicates();
						String[] actNames = dialog.getActNames();
						String[] actSubstitutions = dialog
								.getActSubstitutions();

						IEvent evt = (IEvent) rodinFile.createInternalElement(
								IEvent.ELEMENT_TYPE, getFreeElementName(editor,
										rodinFile, IEvent.ELEMENT_TYPE,
										PrefixEvtName.QUALIFIED_NAME,
										PrefixEvtName.DEFAULT_PREFIX), null,
								monitor);
						evt.setLabel(name, monitor);
						editor.addNewElement(evt);
						String varPrefix = getNamePrefix(editor,
								PrefixVarName.QUALIFIED_NAME,
								PrefixVarName.DEFAULT_PREFIX);
						int varIndex = getFreeElementNameIndex(editor, evt,
								IVariable.ELEMENT_TYPE, varPrefix);
						for (String varName : varNames) {
							IVariable var = (IVariable) evt
									.createInternalElement(
											IVariable.ELEMENT_TYPE, varPrefix
													+ varIndex, null, monitor);
							var.setIdentifierString(varName);
							editor.addNewElement(var);
							varIndex = getFreeElementNameIndex(editor, evt,
									IVariable.ELEMENT_TYPE, varPrefix,
									varIndex + 1);
						}

						String grdPrefix = getNamePrefix(editor,
								PrefixGrdName.QUALIFIED_NAME,
								PrefixGrdName.DEFAULT_PREFIX);
						int grdIndex = getFreeElementNameIndex(editor, evt,
								IGuard.ELEMENT_TYPE, grdPrefix);
						for (int i = 0; i < grdNames.length; i++) {
							IGuard grd = (IGuard) evt.createInternalElement(
									IGuard.ELEMENT_TYPE, grdPrefix + grdIndex,
									null, monitor);
							grd.setLabel(grdNames[i], monitor);
							grd.setPredicateString(grdPredicates[i]);
							editor.addNewElement(grd);
							grdIndex = getFreeElementNameIndex(editor, evt,
									IGuard.ELEMENT_TYPE, grdPrefix,
									grdIndex + 1);
						}

						String actPrefix = getNamePrefix(editor,
								PrefixActName.QUALIFIED_NAME,
								PrefixActName.DEFAULT_PREFIX);
						int actIndex = getFreeElementNameIndex(editor, evt,
								IAction.ELEMENT_TYPE, actPrefix);
						for (int i = 0; i < actNames.length; i++) {
							IAction act = (IAction) evt.createInternalElement(
									IAction.ELEMENT_TYPE, actPrefix + actIndex,
									null, monitor);
							act.setLabel(actNames[i], monitor);
							act.setAssignmentString(actSubstitutions[i]);
							editor.addNewElement(act);
							actIndex = getFreeElementNameIndex(editor, evt,
									IAction.ELEMENT_TYPE, actPrefix, grdIndex);
						}
					}

				}, monitor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new carrier sets will be created in
	 */
	public static void newCarrierSets(final EventBEditor editor,
			final IRodinFile rodinFile, IProgressMonitor monitor) {
		try {
			String identifier = getFreeElementIdentifier(editor, rodinFile,
					ICarrierSet.ELEMENT_TYPE, PrefixSetName.QUALIFIED_NAME,
					PrefixSetName.DEFAULT_PREFIX);
			ElementAttributeInputDialog dialog = new ElementAttributeInputDialog(
					Display.getCurrent().getActiveShell(), "New Carrier Sets",
					"Name", identifier);

			dialog.open();
			final Collection<String> names = dialog.getAttributes();
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					String setPrefix = getNamePrefix(editor,
							PrefixCstName.QUALIFIED_NAME,
							PrefixCstName.DEFAULT_PREFIX);
					int setIndex = getFreeElementNameIndex(editor, rodinFile,
							ICarrierSet.ELEMENT_TYPE, setPrefix);
					for (String name : names) {
						ICarrierSet set = (ICarrierSet) rodinFile
								.createInternalElement(
										ICarrierSet.ELEMENT_TYPE, setPrefix
												+ setIndex, null, monitor);
						set.setIdentifierString(name);
						editor.addNewElement(set);
						setIndex = getFreeElementNameIndex(editor, rodinFile,
								ICarrierSet.ELEMENT_TYPE, setPrefix,
								setIndex + 1);
					}
				}

			}, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Utility method to create new carrier sets using a modal dialog.
	 * <p>
	 * 
	 * @param editor
	 *            the editor that made the call to this method.
	 * @param rodinFile
	 *            the Rodin file that the new carrier sets will be created in
	 */
	public static void newEnumeratedSet(final EventBEditor editor,
			final IRodinFile rodinFile, IProgressMonitor monitor) {

		try {
			String identifier = getFreeElementIdentifier(editor, rodinFile,
					ICarrierSet.ELEMENT_TYPE, PrefixSetName.QUALIFIED_NAME,
					PrefixSetName.DEFAULT_PREFIX);
			final NewEnumeratedSetInputDialog dialog = new NewEnumeratedSetInputDialog(
					Display.getCurrent().getActiveShell(),
					"New Enumerated Set", identifier);

			dialog.open();
			final String name = dialog.getName();
			if (name == null)
				return;

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					Collection<String> elements = dialog.getElements();

					ICarrierSet set = (ICarrierSet) rodinFile
							.createInternalElement(ICarrierSet.ELEMENT_TYPE,
									getFreeElementName(editor, rodinFile,
											ICarrierSet.ELEMENT_TYPE,
											PrefixSetName.QUALIFIED_NAME,
											PrefixSetName.DEFAULT_PREFIX),
									null, monitor);
					set.setIdentifierString(name);
					editor.addNewElement(set);

					if (elements.size() == 0)
						return;

					String namePrefix = getNamePrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					int nameIndex = getFreeElementNameIndex(editor, rodinFile,
							IAxiom.ELEMENT_TYPE, namePrefix);

					String labelPrefix = getPrefix(editor,
							PrefixAxmName.QUALIFIED_NAME,
							PrefixAxmName.DEFAULT_PREFIX);
					int labelIndex = getFreeElementLabelIndex(editor,
							rodinFile, IAxiom.ELEMENT_TYPE, labelPrefix);
					// String axmName = namePrefix + nameIndex;

					IAxiom newAxm = (IAxiom) rodinFile.createInternalElement(
							IAxiom.ELEMENT_TYPE, namePrefix + nameIndex, null,
							null);
					newAxm.setLabel(labelPrefix + labelIndex, monitor);
					String axmPred = name + " = {";

					String cstPrefix = getNamePrefix(editor,
							PrefixCstName.QUALIFIED_NAME,
							PrefixCstName.DEFAULT_PREFIX);
					int cstIndex = getFreeElementNameIndex(editor, rodinFile,
							IConstant.ELEMENT_TYPE, cstPrefix);
					int counter = 0;
					for (String element : elements) {
						IConstant cst = (IConstant) rodinFile
								.createInternalElement(IConstant.ELEMENT_TYPE,
										cstPrefix + cstIndex, null, monitor);
						cst.setIdentifierString(element);
						editor.addNewElement(cst);
						cstIndex = getFreeElementNameIndex(editor, rodinFile,
								IConstant.ELEMENT_TYPE, cstPrefix, cstIndex + 1);

						nameIndex = getFreeElementNameIndex(editor, rodinFile,
								IAxiom.ELEMENT_TYPE, namePrefix, nameIndex);
						labelIndex = getFreeElementLabelIndex(editor,
								rodinFile, IAxiom.ELEMENT_TYPE, labelPrefix,
								labelIndex);
						IAxiom axm = (IAxiom) rodinFile.createInternalElement(
								IAxiom.ELEMENT_TYPE, namePrefix + nameIndex,
								null, monitor);
						axm.setLabel(labelPrefix + labelIndex, monitor);

						axm.setPredicateString(element + " \u2208 " + name);
						axmPred += element;
						counter++;
						if (counter != elements.size())
							axmPred += ", ";
					}
					axmPred += "}";
					newAxm.setPredicateString(axmPred);

					counter = 0;
					String[] elementsArray = elements
							.toArray(new String[elements.size()]);
					for (String element : elements) {
						counter++;
						for (int i = counter; i < elements.size(); i++) {
							String element2 = elementsArray[i];
							nameIndex = getFreeElementNameIndex(editor,
									rodinFile, IAxiom.ELEMENT_TYPE, namePrefix,
									nameIndex);
							labelIndex = getFreeElementLabelIndex(editor,
									rodinFile, IAxiom.ELEMENT_TYPE,
									labelPrefix, labelIndex);
							IAxiom axm = (IAxiom) rodinFile
									.createInternalElement(IAxiom.ELEMENT_TYPE,
											namePrefix + nameIndex, null,
											monitor);
							axm.setLabel(labelPrefix + labelIndex, monitor);
							axm.setPredicateString(element + " \u2260 "
									+ element2);
						}
					}

				}

			}, monitor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getNamePrefix(EventBEditor editor,
			QualifiedName qualifiedName, String defaultPrefix) {
		return "internal_" + getPrefix(editor, qualifiedName, defaultPrefix);
	}

	public static String getPrefix(EventBEditor editor,
			QualifiedName qualifiedName, String defaultPrefix) {
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					qualifiedName);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (prefix == null)
			prefix = defaultPrefix;
		return prefix;
	}

	public static String getFreeElementName(EventBEditor editor,
			IInternalParent parent, String type, QualifiedName qualifiedName,
			String defaultPrefix) throws RodinDBException {
		String prefix = getNamePrefix(editor, qualifiedName, defaultPrefix);
		return prefix + getFreeElementNameIndex(editor, parent, type, prefix);
	}

	public static int getFreeElementNameIndex(EventBEditor editor,
			IInternalParent parent, String type, String prefix)
			throws RodinDBException {
		return getFreeElementNameIndex(editor, parent, type, prefix, 1);
	}

	public static int getFreeElementNameIndex(EventBEditor editor,
			IInternalParent parent, String type, String prefix, int beginIndex)
			throws RodinDBException {

		IRodinElement[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			IInternalElement element = parent.getInternalElement(type, prefix
					+ i);
			if (!element.exists())
				break;
		}
		return i;
	}

	public static String getFreeElementLabel(EventBEditor editor,
			IInternalParent parent, String type, QualifiedName qualifiedName,
			String defaultPrefix) throws RodinDBException {
		String prefix = getPrefix(editor, qualifiedName, defaultPrefix);
		return prefix + getFreeElementLabelIndex(editor, parent, type, prefix);
	}

	public static int getFreeElementLabelIndex(EventBEditor editor,
			IInternalParent parent, String type, String prefix)
			throws RodinDBException {
		return getFreeElementLabelIndex(editor, parent, type, prefix, 1);
	}

	public static int getFreeElementLabelIndex(EventBEditor editor,
			IInternalParent parent, String type, String prefix, int beginIndex)
			throws RodinDBException {

		IRodinElement[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			boolean exists = false;
			for (IRodinElement element : elements) {
				if (((ILabeledElement) element).getLabel(null).equals(
						prefix + i)) {
					exists = true;
					break;
				}
			}
			if (!exists)
				break;
		}
		return i;
	}

	public static String getFreeElementIdentifier(EventBEditor editor,
			IInternalParent parent, String type, QualifiedName qualifiedName,
			String defaultPrefix) throws RodinDBException {
		String prefix = getPrefix(editor, qualifiedName, defaultPrefix);
		return prefix
				+ getFreeElementIdentifierIndex(editor, parent, type, prefix);
	}

	public static int getFreeElementIdentifierIndex(EventBEditor editor,
			IInternalParent parent, String type, String prefix)
			throws RodinDBException {
		return getFreeElementIdentifierIndex(editor, parent, type, prefix, 1);
	}

	public static int getFreeElementIdentifierIndex(EventBEditor editor,
			IInternalParent parent, String type, String prefix, int beginIndex)
			throws RodinDBException {

		IRodinElement[] elements = parent.getChildrenOfType(type);

		int i;
		for (i = beginIndex; i < elements.length + beginIndex; i++) {
			boolean exists = true;
			for (IRodinElement element : elements) {
				if (!((IIdentifierElement) element).getIdentifierString()
						.equals(prefix + i)) {
					exists = false;
					break;
				}
			}
			if (!exists)
				break;
		}
		return i;
	}

	public static IRodinElement getParentOfType(IRodinElement element,
			String type) {
		IRodinElement parent = element;
		while (parent != null && !parent.getElementType().equals(type)) {
			parent = parent.getParent();
		}
		return parent;
	}

	public static IRodinElement getFirstChildOfTypeWithLabel(IParent parent,
			String type, String label) throws RodinDBException {
		IRodinElement[] children = parent.getChildrenOfType(type);
		for (IRodinElement child : children) {
			if (child instanceof ILabeledElement) {
				String elementLabel = ((ILabeledElement) child).getLabel(null);
				if (elementLabel.equals(label))
					return child;
			}
		}
		return null;
	}
}
