/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added getFreeIndex method to factor several methods
 *     Systerel - added methods indicateUser() and showUnexpectedError()
 *     Systerel - separation of file and root element
 *     Systerel - take into account implicit children when computing a free index
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IParameter;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.eventbeditor.editpage.ActionLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.AxiomLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.CarrierSetIdentifierAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.ConstantIdentifierAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.EventLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.GuardLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.IdentifierAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.InvariantLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.LabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.ParameterIdentifierAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.TheoremLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.VariableIdentifierAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.WitnessLabelAttributeFactory;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.projectexplorer.ExplorerUtilities;
import org.eventb.ui.projectexplorer.TreeNode;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class contains some utility static methods that are used in this
 *         Event-B User interface plug-in.
 */
public class UIUtils {
	
	public static abstract class MaxFinder {

		/**
		 * Inserts a number given in string format.
		 * 
		 * @param number
		 *            a string of decimal digits.
		 */
		public abstract void insert(String number);

		/**
		 * Returns a number which was not inserted previously as a string of
		 * decimal digits. Currently, returns the maximum of the inserted
		 * numbers plus one (or one if no number was inserted).
		 * 
		 * @return an available number
		 */
		public abstract String getAvailable();
	}

	public static class BigMaxFinder extends MaxFinder {

		BigInteger max = BigInteger.ZERO;

		@Override
		public void insert(String number) {
			final BigInteger n = new BigInteger(number);
			if (max.compareTo(n) < 0) {
				max = n;
			}
		}

		@Override
		public String getAvailable() {
			return max.add(BigInteger.ONE).toString();
		}

	}

	/**
	 * The debug flag. This is set by the option when the platform is launched.
	 * Client should not try to reset this flag.
	 */
	public static boolean DEBUG = false;

	/**
	 * Returns a non already used String index intended to be concatenated to
	 * the name or the attribute of an element.
	 * 
	 * @param parent
	 *            the parent node to be looked for
	 * @param type
	 *            the type of the element to be looked for
	 * @param attributeType
	 *            the type of the attribute to be looked for set it to null if
	 *            the index is intended to the name of the element
	 * @param prefix
	 *            the prefix of the element to be looked for
	 * @return a non already used integer index intended to be concatenated to
	 *         the name or the attribute of an element (if no element of that
	 *         type already exists, returns beginIndex)
	 * @throws RodinDBException
	 */
	public static String getFreePrefixIndex(IInternalParent parent,
			IInternalElementType<?> type, IAttributeType.String attributeType,
			String prefix) throws RodinDBException, IllegalStateException {

		final String regex = Pattern.quote(prefix) + "(\\d+)"; //$NON-NLS-1$
		final Pattern prefixDigits = Pattern.compile(regex);
		final MaxFinder maxFinder = new BigMaxFinder();
		for (IInternalElement element : getVisibleChildrenOfType(parent, type)) {
			final String elementString;
			if (attributeType == null) {
				// name research
				elementString = element.getElementName();
			} else if (element.hasAttribute(attributeType)) {
				// attribute research
				elementString = element.getAttributeValue(attributeType);
			} else {
				elementString = null;
			}

			if (elementString != null) {
				final Matcher matcher = prefixDigits.matcher(elementString);
				if (matcher.matches()) {
					maxFinder.insert(matcher.group(1));
				}
			}
		}
		return maxFinder.getAvailable();
	}

	private static List<IInternalElement> getVisibleChildrenOfType(
			IInternalParent parent, IInternalElementType<?> type)
			throws RodinDBException {
		final List<IInternalElement> result = new ArrayList<IInternalElement>();
		addImplicitChildrenOfType(result, parent, type);
		result.addAll(Arrays.asList(parent.getChildrenOfType(type)));
		return result;
	}

	private static void addImplicitChildrenOfType(
			List<IInternalElement> result, IInternalParent parent,
			IInternalElementType<?> type) throws RodinDBException {
		if (parent instanceof IEvent) {
			final IInternalElement[] implicitChildren = EventBUtils
					.getImplicitChildren((IEvent) parent);
			for (IInternalElement child : implicitChildren) {
				if (child.getElementType() == type) {
					result.add(child);
				}
			}
		}
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
			return ((IRodinElement) ((TreeNode<?>) node).getParent())
					.getOpenable();
		else if (node instanceof IRodinElement)
			return ((IRodinElement) node).getOpenable();

		return null;
	}

	/**
	 * Link the current object to a Prover UI editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. a proof obligation or a Rodin file)
	 */
	public static void linkToProverUI(final Object obj) {
		final IRodinFile psFile = getPSFileFor(obj);
		if (psFile == null) {
			// Not a PS file element
			// TODO log error here ?
			return;
		}
		try {
			IEditorInput fileInput = new FileEditorInput(psFile.getResource());
			final ProverUI editor = (ProverUI) EventBUIPlugin.getActivePage()
					.openEditor(fileInput, ProverUI.EDITOR_ID);
			if (obj instanceof IPSStatus)
				UIUtils.runWithProgressDialog(editor.getSite().getShell(),
						new IRunnableWithProgress() {
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								editor.setCurrentPO((IPSStatus) obj, monitor);
							}

						});
		} catch (PartInitException e) {
			MessageDialog.openError(null, null,
					Messages.uiUtils_errorOpeningProvingEditor);
			e.printStackTrace();
			// TODO EventBImage.logException(e);
		}
	}

	private static IRodinFile getPSFileFor(Object obj) {
		if (!(obj instanceof IRodinElement)) {
			return null;
		}

		final IOpenable file = ((IRodinElement) obj).getOpenable();
		if (file instanceof IRodinFile) {
			IInternalElement root = ((IRodinFile) file).getRoot();
			if(root instanceof IPSRoot){
				return root.getRodinFile();
			} else if(root instanceof IEventBRoot){
				return ((IEventBRoot) root).getPSRoot().getRodinFile();
			}				
		}
		return null;
	}

	/**
	 * Link the current object to an Event-B editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */
	public static void linkToEventBEditor(Object obj) {
		ExplorerUtilities.linkToEditor(obj);
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
		output = output.replaceAll("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		output = output.replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		output = output.replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
		return output;
	}

	/**
	 * Convert a string input to HTML format by replacing special characters (&, <, >,
	 * space, tab).
	 * <p>
	 * 
	 * @param input
	 *            the input string
	 * @return a string corresponding to the input in XML format
	 */
	public static String HTMLWrapUp(String input) {
		String output = input;
		output = output.replaceAll("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		output = output.replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		output = output.replaceAll(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
		output = output.replaceAll(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
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
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">" //$NON-NLS-1$ //$NON-NLS-2$
				+ UIUtils.XMLWrapUp(link) + "</a>"; //$NON-NLS-1$
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
		return "<a href=\"" + UIUtils.XMLWrapUp(link) + "\">" //$NON-NLS-1$ //$NON-NLS-2$
				+ UIUtils.XMLWrapUp(text) + "</a>"; //$NON-NLS-1$
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
	 * Running a runnable asynchronously.
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
	 * Running a runnable synchronously.
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

	public static String getNamePrefix(IRodinFile rodinFile,
			IInternalElementType<?> type, String defaultPrefix) {
		return "internal_" + getPrefix(rodinFile, type, defaultPrefix); //$NON-NLS-1$
	}

	public static String getNamePrefix(IInternalElement root,
			IInternalElementType<?> type, String defaultPrefix) {
		return getNamePrefix(root.getRodinFile(), type, defaultPrefix);
	}

	public static String getPrefix(IRodinFile inputFile,
			IInternalElementType<?> type, String defaultPrefix) {
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
	
	public static String getPrefix(IInternalElement root,
			IInternalElementType<?> type, String defaultPrefix) {
		return getPrefix(root.getRodinFile(), type, defaultPrefix);
	}

	public static <T extends IInternalElement> String getFreeElementName(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> type, String defaultPrefix)
			throws RodinDBException {
		String prefix = getNamePrefix(editor.getRodinInput(), type, defaultPrefix);
		return prefix + EventBUtils.getFreeChildNameIndex(parent, type, prefix);
	}

	public static String getFreeElementLabel(IEventBEditor<?> editor,
			IInternalParent parent,
			IInternalElementType<? extends IInternalElement> type,
			String defaultPrefix) throws RodinDBException {
		String prefix = getPrefix(editor.getRodinInput(), type, defaultPrefix);
		return prefix + getFreeElementLabelIndex(editor, parent, type, prefix);
	}


	/**
	 * Returns an integer that can be used as a label index for a newly created
	 * element.
	 * 
	 * @param editor
	 *            unused parameter
	 * @param parent
	 *            the parent node to be looked for
	 * @param type
	 *            the type of the element to be looked for
	 * @param prefix
	 *            the prefix of the element to be looked for
	 * @return a non already used integer identifier intended for a new element
	 *         of that type (if no element of that type already exists, returns
	 *         beginIndex)
	 * @throws RodinDBException
	 */
	public static String getFreeElementLabelIndex(IEventBEditor<?> editor,
			IInternalParent parent,
			IInternalElementType<? extends IInternalElement> type,
			String prefix) throws RodinDBException {
		return getFreePrefixIndex(parent, type,
				EventBAttributes.LABEL_ATTRIBUTE, prefix);
	}

	public static String getFreeElementIdentifier(IEventBEditor<?> editor,
			IInternalParent parent,
			IInternalElementType<? extends IInternalElement> type,
			String defaultPrefix) throws RodinDBException {
		String prefix = getPrefix(editor.getRodinInput(), type, defaultPrefix);
		return prefix + getFreeElementIdentifierIndex(parent, type, prefix);
	}

	public static String getFreeElementIdentifierIndex(IInternalParent parent,
			IInternalElementType<?> type, String prefix)
			throws RodinDBException {
		return getFreePrefixIndex(parent, type, 
				EventBAttributes.IDENTIFIER_ATTRIBUTE, prefix);
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

	public static IdentifierAttributeFactory getIdentifierAttributeFactory(
			IAttributedElement element) {
		if (element instanceof ICarrierSet){
			return new CarrierSetIdentifierAttributeFactory();
		}else if(element instanceof IConstant){
			return new ConstantIdentifierAttributeFactory();
		}else if(element instanceof IParameter){
			return new ParameterIdentifierAttributeFactory();
		}else if(element instanceof IVariable){
			return new VariableIdentifierAttributeFactory();
		}else{
			return null;
		}
	}
	

	public static IdentifierAttributeFactory getIdentifierAttributeFactory(
			IInternalElementType<?> type) {
		if (type == ICarrierSet.ELEMENT_TYPE) {
			return new CarrierSetIdentifierAttributeFactory();
		} else if (type == IConstant.ELEMENT_TYPE) {
			return new ConstantIdentifierAttributeFactory();
		} else if (type == IParameter.ELEMENT_TYPE) {
			return new ParameterIdentifierAttributeFactory();
		} else if (type == IVariable.ELEMENT_TYPE) {
			return new VariableIdentifierAttributeFactory();
		} else {
			return null;
		}
	}
	
	public static LabelAttributeFactory getLabelAttributeFactory(
			IAttributedElement element) {
		if (element instanceof IAction) {
			return new ActionLabelAttributeFactory();
		}else if(element instanceof IAxiom){
			return new AxiomLabelAttributeFactory();
		}else if(element instanceof IEvent){
			return new EventLabelAttributeFactory();
		}else if(element instanceof IGuard){
			return new GuardLabelAttributeFactory();
		}else if(element instanceof IInvariant){
			return new InvariantLabelAttributeFactory();
		}else if(element instanceof ITheorem){
			return new TheoremLabelAttributeFactory();
		}else if(element instanceof IWitness){
			return new WitnessLabelAttributeFactory();
		}else{
			return null;
		}
	}

	public static LabelAttributeFactory getLabelAttributeFactory(
			IInternalElementType<?> type) {
		if (type == IAction.ELEMENT_TYPE) {
			return new ActionLabelAttributeFactory();
		} else if (type == IAxiom.ELEMENT_TYPE) {
			return new AxiomLabelAttributeFactory();
		} else if (type == IEvent.ELEMENT_TYPE) {
			return new EventLabelAttributeFactory();
		} else if (type == IGuard.ELEMENT_TYPE) {
			return new GuardLabelAttributeFactory();
		} else if (type == IInvariant.ELEMENT_TYPE) {
			return new InvariantLabelAttributeFactory();
		} else if (type == ITheorem.ELEMENT_TYPE) {
			return new TheoremLabelAttributeFactory();
		} else if (type == IWitness.ELEMENT_TYPE) {
			return new WitnessLabelAttributeFactory();
		} else {
			return null;
		}
	}

	public static <E extends IAttributedElement> void setStringAttribute(
			E element, IAttributeFactory<E> factory, String value,
			IProgressMonitor monitor) {
		try {
			if (attributeHasChanged(element, factory, value, monitor)) {
				final IRodinFile rodinFile = ((IInternalElement) element)
						.getRodinFile();
				History.getInstance().addOperation(
						OperationFactory.changeAttribute(rodinFile, factory,
								element, value));
			}
		} catch (RodinDBException e) {
			UIUtils.log(e, "Error changing attribute for element "
					+ element.getElementName());
			if (UIUtils.DEBUG)
				e.printStackTrace();
		}
	}

	private static <E extends IAttributedElement> boolean attributeHasChanged(
			E element, IAttributeFactory<E> factory, String value,
			IProgressMonitor monitor) throws RodinDBException {
		if (value == null) {
			return factory.hasValue(element, monitor);
		}
		if (!factory.hasValue(element, monitor))
			return true;
		return !value.equals(factory.getValue(element, monitor));
	}

	public static <T extends IInternalElement> String getFreeChildName(
			IRodinFile file, IInternalParent parent,
			IInternalElementType<T> type) throws RodinDBException {
		String defaultPrefix = "element"; // TODO Get this from extensions //$NON-NLS-1$
		String prefix = getNamePrefix(file, type, defaultPrefix);
		return prefix + EventBUtils.getFreeChildNameIndex(parent, type, prefix);
	}

	public static <T extends IInternalElement> String getFreeChildName(
			IInternalElement root, IInternalParent parent,
			IInternalElementType<T> type) throws RodinDBException {
		return getFreeChildName(root.getRodinFile(), parent, type);
	}
	
	public static QualifiedName getQualifiedName(IInternalElementType<?> type) {
		return new QualifiedName(EventBUIPlugin.PLUGIN_ID, type.getId());
	}

	public static void runWithProgressDialog(Shell shell,
			final IRunnableWithProgress op) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (UIUtils.DEBUG)
				System.out.println("Interrupt"); //$NON-NLS-1$
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (UIUtils.DEBUG)
				System.out.println("Interrupt"); //$NON-NLS-1$
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, Messages.uiUtils_unexpectedError,
					message);
			return;
		}
	}

	/**
	 * Print the debug message in multiple line, each line with the same given
	 * prefix.
	 * 
	 * @param debugPrefix
	 *            the default prefix for the debug message
	 * @param message
	 *            the debug message
	 */
	public static void printDebugMessage(String debugPrefix, String message) {
		StringTokenizer tokenizer = new StringTokenizer(message, "\n"); //$NON-NLS-1$
		while (tokenizer.hasMoreElements()) {
			System.out.println(debugPrefix + tokenizer.nextToken());
		}
	}

	/**
	 * Return a comma separated representation of a list of input objects.
	 * 
	 * @param objects
	 *            a list of objects.
	 * @return the comma separated string representation of input objects.
	 */
	public static String toCommaSeparatedList(ArrayList<Object> objects) {
		StringBuffer buffer = new StringBuffer();
		boolean sep = false;
		for (Object item : objects) {
			if (sep) {
				sep = true;
			} else {
				buffer.append(","); //$NON-NLS-1$
			}
			buffer.append(item);
		}
		return buffer.toString();
	}

	/**
	 * Return a comma separated representation of an array of input objects.
	 * 
	 * @param objects
	 *            an array of objects.
	 * @return the comma separated string representation of input objects.
	 */
	public static String toCommaSeparatedList(String[] objects) {
		StringBuffer buffer = new StringBuffer();
		boolean sep = false;
		for (Object item : objects) {
			if (sep) {
				sep = true;
			} else {
				buffer.append(","); //$NON-NLS-1$
			}
			buffer.append(item);
		}
		return buffer.toString();
	}

	/**
	 * Parse a comma separated string to a list of string.
	 * 
	 * @param stringList
	 *            the comma separated string.
	 * @return an array of strings that make up the comma separted input string.
	 */
	public static String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, ",");//$NON-NLS-1$
		ArrayList<String> result = new ArrayList<String>();
		while (st.hasMoreElements()) {
			result.add((String) st.nextElement());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Opens an information dialog to the user displaying the given message.
	 *  
	 * @param message The dialog message.
	 */
	public static void showInfo(final String message) {
		final String pluginName = getPluginName();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final IWorkbenchWindow activeWorkbenchWindow = EventBUIPlugin
						.getActiveWorkbenchWindow();
				MessageDialog.openInformation(activeWorkbenchWindow.getShell(),
						pluginName, message);
			}
		});

	}

	/**
	 * Opens an error dialog to the user showing the given unexpected error.
	 * 
	 * @param e The unexpected error.
	 */
	public static void showUnexpectedError(final CoreException e) {
		final String pluginName = getPluginName();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final IStatus status = new Status(IStatus.ERROR,
						EventBUIPlugin.PLUGIN_ID, IStatus.ERROR, e.getStatus()
								.getMessage(), null);
				ErrorDialog.openError(EventBUIPlugin.getActiveWorkbenchWindow()
						.getShell(), pluginName,
						Messages.uiUtils_unexpectedError, status);
			}
		});

	}
	
	private static String getPluginName() {
		final Bundle bundle = EventBUIPlugin.getDefault().getBundle();
		return (String) bundle.getHeaders().get(Constants.BUNDLE_NAME);
	}

	
	public static IMachineRoot[] getMachineRootChildren(IRodinProject project)
			throws RodinDBException {
		ArrayList<IMachineRoot> result = new ArrayList<IMachineRoot>();
		for (IRodinElement element : project.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element)
						.getRoot();
				if (root instanceof IMachineRoot)
					result.add((IMachineRoot) root);
			}
		}
		return result.toArray(new IMachineRoot[result.size()]);
	}
	
	
	public static IContextRoot[] getContextRootChildren(IRodinProject project)
			throws RodinDBException {
		ArrayList<IContextRoot> result = new ArrayList<IContextRoot>();
		for (IRodinElement element : project.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element)
						.getRoot();
				if (root instanceof IContextRoot)
					result.add((IContextRoot) root);
			}
		}
		return result.toArray(new IContextRoot[result.size()]);
	}

	public static IPSRoot[] getPSRootChildren(IRodinProject project)
			throws RodinDBException {
		ArrayList<IPSRoot> result = new ArrayList<IPSRoot>();
		for (IRodinElement element : project.getChildren()) {
			if (element instanceof IRodinFile) {
				IInternalElement root = ((IRodinFile) element)
						.getRoot();
				if (root instanceof IPSRoot)
					result.add((IPSRoot) root);
			}
		}
		return result.toArray(new IPSRoot[result.size()]);
}
	
}
