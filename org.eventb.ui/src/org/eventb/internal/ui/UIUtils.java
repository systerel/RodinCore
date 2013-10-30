/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *     Systerel - added boolean to linkToProverUI() and a showQuestion() method
 *     Systerel - added method to remove MouseWheel Listener of CCombo
 *     Systerel - used ElementDescRegistry
 *     Systerel - update combo list on focus gain
 *     Systerel - added dialog opening methods
 *     Systerel - made showView return the shown view
 *     Systerel - refactored to support the new preference mechanism
 *******************************************************************************/
package org.eventb.internal.ui;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eventb.ui.EventBUIPlugin.PLUGIN_ID;

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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.utils.LegacyCommandAction;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
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
	
	public static final String COMBO_VALUE_UNDEFINED = "--undef--";
	
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
	 *            the type of the attribute to be looked for. Set to
	 *            <code>null</code> if the element name should be considered
	 *            rather than, an attribute value
	 * @param prefix
	 *            the prefix of the element to be looked for
	 * @return a non already used integer index intended to be concatenated to
	 *         the name or the attribute of an element (if no element of that
	 *         type already exists, returns beginIndex)
	 * @throws RodinDBException
	 */
	public static String getFreePrefixIndex(IInternalElement parent,
			IInternalElementType<?> type, IAttributeType.String attributeType,
			String prefix) throws RodinDBException {

		return getFreePrefixIndex(getVisibleChildrenOfType(parent, type),
				attributeType, prefix);
	}

	/**
	 * Returns a non already used String index intended to be concatenated to
	 * the name or the attribute of an element.
	 * 
	 * @param elements
	 *            the elements to be looked for
	 * @param attributeType
	 *            the type of the attribute to be looked for. Set to
	 *            <code>null</code> if the element name should be considered
	 *            rather than, an attribute value
	 * @param prefix
	 *            the prefix of the element to be looked for
	 * @return a non already used integer index intended to be concatenated to
	 *         the name or the attribute of an element (if no element of that
	 *         type already exists, returns beginIndex)
	 * @throws RodinDBException
	 */
	public static String getFreePrefixIndex(
			List<? extends IInternalElement> elements,
			IAttributeType.String attributeType, String prefix)
			throws RodinDBException {

		final String regex = Pattern.quote(prefix) + "(\\d+)"; //$NON-NLS-1$
		final Pattern prefixDigits = Pattern.compile(regex);
		final MaxFinder maxFinder = new BigMaxFinder();
		for (IInternalElement element : elements) {
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
	
	public static <T extends IInternalElement> List<T> getImplicitChildrenOfType(
			IInternalElement parent, IInternalElementType<T> type)
			throws RodinDBException {
		final List<T> result = new ArrayList<T>();
		addImplicitChildrenOfType(result, parent, type);
		return result;
	}
	
	public static <T extends IInternalElement> List<T> getVisibleChildrenOfType(
			IInternalElement parent, IInternalElementType<T> type)
			throws RodinDBException {
		final List<T> result = new ArrayList<T>();
		addImplicitChildrenOfType(result, parent, type);
		result.addAll(Arrays.asList(parent.getChildrenOfType(type)));
		return result;
	}

	public static <T extends IInternalElement> void addImplicitChildrenOfType(
			List<T> result, IInternalElement parent,
			IInternalElementType<T> type) throws RodinDBException {
		if (parent instanceof IEvent) {
			final IInternalElement[] implicitChildren = EventBUtils
					.getImplicitChildren((IEvent) parent);
			for (IInternalElement child : implicitChildren) {
				final T typedChild = asOfType(child, type);
				if (typedChild != null) {
					result.add(typedChild);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends IInternalElement> T asOfType(
			IInternalElement element, IInternalElementType<T> type) {
		if (element.getElementType() == type) {
			return (T) element;
		}
		return null;
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
		log(status);
	}

	/**
	 * Logs the given status to the Event-B UI plug-in log.
	 */
	public static void log(IStatus status) {
		EventBUIPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Method to return the openable for an object (IRodinElement).
	 * <p>
	 * 
	 * @param element
	 *            A Rodin Element
	 * @return The IRodinFile corresponding to the input object
	 */
	public static IOpenable getOpenable(Object element) {
		if (element instanceof IRodinElement) {
			return ((IRodinElement) element).getOpenable();
		} else {
			return null;
		}
	}

	/**
	 * Link the current object to a Prover UI editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. a proof obligation or a Rodin file)
	 */
	public static void linkToProverUI(final Object obj) {
		linkToProverUI(obj, true);
	}
	
	/**
	 * Link the current object to a Prover UI editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. a proof obligation or a Rodin file)
	 * @param activateEditor
	 *            Set to true when the Prover UI editor has to get activated.
	 *            When this linking is just intended for background overall
	 *            coherence, setting to false reduces selection listeners
	 *            pollution.
	 */
	public static void linkToProverUI(final Object obj, boolean activateEditor) {
		final IRodinFile psFile = getPSFileFor(obj);
		if (psFile == null) {
			// Not a PS file element
			// TODO log error here ?
			return;
		}
		try {
			IEditorInput fileInput = new FileEditorInput(psFile.getResource());
			final ProverUI editor = (ProverUI) EventBUIPlugin.getActivePage()
					.openEditor(fileInput, ProverUI.EDITOR_ID, activateEditor);
			if (obj instanceof IPSStatus)
				UIUtils.runWithProgressDialog(editor.getSite().getShell(),
						new IRunnableWithProgress() {
							@Override
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
		final IRodinFile component = asRodinFile(obj);
		if (component == null)
			return;
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
				.getDefaultEditor(component.getCorrespondingResource().getName());
		linkToEditor(component, obj, desc);
	}
	
	/**
	 * Link the current object to the preferred editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */
	public static void linkToPreferredEditor(Object obj) {
		final IRodinFile component = asRodinFile(obj);
		if (component == null)
			return;
		IEditorDescriptor desc = IDE.getDefaultEditor(component.getResource());
		linkToEditor(component, obj, desc);
	}
	
	private static IRodinFile asRodinFile(Object obj) {
		if (obj instanceof IRodinProject)
			return null;
		return (IRodinFile) UIUtils.getOpenable(obj);
	}
	
	/**
	 * Link the current object to the specified editor.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 * @param desc
	 *            the editor descriptor
	 */
	private static void linkToEditor(IRodinFile component, Object obj, IEditorDescriptor desc) {
		try {
			IEditorPart editor = EventBUIPlugin.getActivePage().openEditor(
					new FileEditorInput(component.getResource()), desc.getId());
			if (editor == null) {
				// External editor
				return;
			}
			final ISelectionProvider sp = editor.getSite().getSelectionProvider();
			if (sp == null) {
				return;
			}
			sp.setSelection(new StructuredSelection(obj));
		} catch (PartInitException e) {
			String errorMsg = "Error opening Editor";
			MessageDialog.openError(null, null, errorMsg);
			log(e, "while trying to open editor for " + component);
		}
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
	 * 
	 * @param view_ID
	 *            the ID of the view which will be activate
	 * @return the IViewPart shown or null in case of failure
	 */
	public static IViewPart showView(String view_ID) {
		try {
			return EventBUIPlugin.getActivePage().showView(view_ID);
		} catch (PartInitException e) {
			log(e, "while trying to show view " + view_ID);
		}
		return null;
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
			@Override
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
			@Override
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

	/**
	 * Returns a free element label for an element described by its parent and
	 * element type.
	 * 
	 * @param parent
	 *            the parent of element for which a free label is wanted
	 * @param type
	 *            the type of the element we want a label for
	 * @return a non already used label for an element
	 */
	public static String getFreeElementLabel(IInternalElement parent,
			IInternalElementType<?> type) {
		final String prefix = PreferenceUtils.getAutoNamePrefix(parent, type);
		return prefix + getFreeElementLabelIndex(parent, type, prefix);
	}


	/**
	 * Returns an integer that can be used as a label index for a newly created
	 * element.
	 * 
	 * @param parent
	 *            the parent node to be looked for
	 * @param type
	 *            the type of the element to be looked for
	 * @param prefix
	 *            the prefix of the element to be looked for
	 * @return a non already used integer identifier intended for a new element
	 *         of that type (if no element of that type already exists, returns
	 *         beginIndex)
	 */
	public static String getFreeElementLabelIndex(IInternalElement parent,
			IInternalElementType<?> type, String prefix) {
		try {
			return getFreePrefixIndex(parent, type,
					EventBAttributes.LABEL_ATTRIBUTE, prefix);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return "1";
		}
	}

	/**
	 * Returns an integer that can be used as a label index for a newly created
	 * element.
	 * 
	 * @param elements
	 *            the elements to be looked for
	 * @param prefix
	 *            the prefix of the element to be looked for
	 * @return a non already used integer identifier intended for a new element
	 *         of that type (if no element of that type already exists, returns
	 *         beginIndex)
	 */
	public static String getFreeElementLabelIndex(
			List<? extends IInternalElement> elements, String prefix) {
		try {
			return getFreePrefixIndex(elements,
					EventBAttributes.LABEL_ATTRIBUTE, prefix);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return "1";
		}
	}

	public static String getFreeElementIdentifier(IInternalElement parent,
			IInternalElementType<? extends IInternalElement> type) {
		final String prefix = PreferenceUtils.getAutoNamePrefix(parent, type);
		try {
			return prefix + getFreeElementIdentifierIndex(parent, type, prefix);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return prefix;
		}
	}

	public static String getFreeElementIdentifierIndex(IInternalElement parent,
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

	public static void setStringAttribute(
			IInternalElement element, IAttributeManipulation factory, String value,
			IProgressMonitor monitor) {
		if (!element.exists())
			return;

		try {
			if (attributeHasChanged(element, factory, value, monitor)) {
				final IRodinFile rodinFile = element.getRodinFile();
				History.getInstance().addOperation(
						OperationFactory.changeAttribute(rodinFile, factory,
								element, value));
			}
		} catch (RodinDBException e) {
			log(e, "Error changing attribute for element "
					+ element.getElementName());
			if (UIUtils.DEBUG)
				e.printStackTrace();
		}
	}

	private static boolean attributeHasChanged(IInternalElement element,
			IAttributeManipulation manipulation, String value,
			IProgressMonitor monitor) throws RodinDBException {
		final String newValue = (value == null) ? "" : value;
		final String oldValue = !manipulation.hasValue(element, monitor) ? ""
				: manipulation.getValue(element, monitor);
		return !(newValue.equals(oldValue));
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
			// Restore the interruption status
			Thread.currentThread().interrupt();
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (UIUtils.DEBUG)
				System.out.println("Interrupt"); //$NON-NLS-1$
			realException.printStackTrace();
			final String message = getExceptionMessage(realException);
			MessageDialog.openError(shell, Messages.uiUtils_unexpectedError,
					message);
			return;
		}
	}

	private static String getExceptionMessage(Throwable e) {
		final String msg = e.getLocalizedMessage();
		if (msg != null) {
			return msg;
		}
		return e.getClass().getName();
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
	public static <T> String toCommaSeparatedList(List<T> objects) {
		return flatten(objects, ",");
	}

	/**
	 * Returns a string representation of a list of input objects. The objects
	 * are separated by a given character.
	 * 
	 * @param objects
	 *            a list of objects
	 * @param separator
	 *            the character to use to separate the objects
	 * @return the string representation of input objects
	 */
	public static <T> String flatten(List<T> objects, String separator) {
		final StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (T item : objects) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
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
		for (String item : objects) {
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
		return parseString(stringList, ",");
	}

	/**
	 * Parse a character separated string to a list of string.
	 * 
	 * @param stringList
	 *            the comma separated string.
	 * @param c
	 *            the character separates the string
	 * @return an array of strings that make up the character separated input
	 *         string.
	 */
	public static String[] parseString(String stringList, String c) {
		StringTokenizer st = new StringTokenizer(stringList, c);//$NON-NLS-1$
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
						Messages.uiUtils_unexpectedError, status);
			}
		});
	}

	static Shell getShell() {
		return getWorkbench().getModalDialogShellProvider().getShell();
	}
	
	private static void syncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(runnable);
	}

	/*
	 * Fix for bug 2417413: CCombo has a child Text, and an arrow Button. When
	 * Text or Button receives a MouseWheel event, CCombo sends a MouseWheel
	 * event to its listeners. To fix it we remove actions on MouseWheel event.
	 */
	public static void disableMouseWheel(CCombo cc) {
		cc.addListener(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.doit = false;
			}
		});
	}
	
	public static void resetCComboValues(CCombo combo,
			IAttributeManipulation manipulation, IInternalElement element,
			boolean required) {
		final String[] values = getValues(manipulation, element, required);
		if (Arrays.equals(combo.getItems(), values))
			return;
		setCComboValues(combo, values);
	}

	private static String[] getValues(IAttributeManipulation manipulation,
			IInternalElement element, boolean required) {
		final String[] values = manipulation.getPossibleValues(element, null);
		try {
			//see bug #3005230
			if (required && manipulation.hasValue(element, null)) {
				return values;
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		final String[] temp = new String[values.length + 1];
		temp[0] = COMBO_VALUE_UNDEFINED;
		System.arraycopy(values, 0, temp, 1, values.length);
		return temp;
	}

	private static void setCComboValues(CCombo combo, String[] values) {
		final String currentText = combo.getText();
		combo.removeAll();
		for (String value : values) {
			combo.add(value);
		}
		combo.setText(currentText);
	}

	/**
	 * Indicates that a global action is to be implemented by a command.
	 * <p>
	 * This method must be used only if the global action is not already
	 * retrofitted to use commands, which is the case for instance for navigate
	 * actions. If the global action has already been retrofitted (e.g.,
	 * <code>org.eclipse.ui.file.new</code>), there is nothing to do.
	 * </p>
	 * <p>
	 * <b>Note</b>: Clients must eventually call
	 * <code>bars.updateActionBars()</code> for this change to be taken into
	 * account.
	 * </p>
	 * 
	 * @param site
	 *            site for which the command should be registered (editor, view,
	 *            view page, etc.)
	 * @param bars
	 *            action bars for the given site
	 * @param globalAction
	 *            global action from {@link ActionFactory}
	 *            <code>http://www.eclipse.org/forums/index.php/mv/msg/107499/329140/#msg_329140</code>
	 * @see <a
	 *      href="http://www.eclipse.org/forums/index.php/mv/msg/107499/329140/#msg_329140"
	 *      >http://www.eclipse.org/forums/index.php/mv/msg/107499/329140/#msg_329140</a>
	 */
	public static void addGlobalActionHandler(final IWorkbenchSite site,
			final IActionBars bars, final ActionFactory globalAction) {
		final String commandId = globalAction.getCommandId();
		final String actionId = globalAction.getId();
		final IWorkbenchWindow window = site.getWorkbenchWindow();
		final IAction bridge = new LegacyCommandAction(window, commandId);
		bars.setGlobalActionHandler(actionId, bridge);
	}

}
