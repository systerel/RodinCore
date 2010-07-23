/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.htmlpage;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBControl;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBFormText;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class HTMLPage extends EventBEditorPage implements
		IElementChangedListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".htmlpage"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_html_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_html_tabTitle;

	// The scrolled form
	private ScrolledForm form;

	// The form text
	private IEventBFormText formText;

	private boolean needsUpdate;

	private AstConverter astConverter;

	private Browser browser;

	private EventBControl eventBBrowser;

	boolean haveUpdate = false;
	
	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public HTMLPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
		astConverter = new Ast2HtmlConverter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
		RodinCore.addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayout(new FillLayout());

		try {
			browser = new Browser(body, SWT.NONE);
		} catch (SWTError e) {
			/* The Browser widget throws an SWTError if it fails to
			 * instantiate properly. Application code should catch
			 * this SWTError and disable any feature requiring the
			 * Browser widget.
			 * Platform requirements for the SWT Browser widget are available
			 * from the SWT FAQ website. 
			 */
			browser = null;
		}
		if (browser != null) {
			/* The Browser widget can be used */
			eventBBrowser = new EventBControl(browser);
			setFormText(new NullProgressMonitor());
		}
		else {
			FormText widget = managedForm.getToolkit().createFormText(body,
					true);
			widget
					.setText(
							"<form>Your platform does not support SWT Browser widget. Platform requirements for the widget are available from the SWT FAQ website: http://www.eclipse.org/swt/faq.php#howusemozilla</form>",
							true, false);
			formText = new EventBFormText(widget);
			widget.setWhitespaceNormalized(false);
		}

	}

	/**
	 * This private helper method is use to set the content string of the form
	 * text. The content string is set according to the type of the rodin input
	 * file and the content of that file
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 */
	void setFormText(IProgressMonitor monitor) {
		IInternalElement root = getEventBEditor().getRodinInput();
		String text = astConverter.getText(monitor, root);
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug(text);
		browser.setText(text);
		form.reflow(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (browser != null) {
			eventBBrowser.dispose();
			browser.dispose();
		}
		if (formText != null)
			formText.dispose();
		RodinCore.removeElementChangedListener(this);
		super.dispose();
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		refresh();
	}

	/**
	 * Refresh the page contents. Currently, not incremental, everything is
	 * recomputed anew.
	 */
	private void refresh() {
		if (form == null)
			return;
		if (form.getContent().isDisposed())
			return;
		if (isActive() && needsUpdate) {
			// We are switching to this page - refresh it
			// if needed.
			final Display display = this.getEditorSite().getShell()
					.getDisplay();
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					// Reset the content string of the form text
					setFormText(new NullProgressMonitor());
				}
			});
			needsUpdate = false;
		}
	}

	private IMachineRoot getAbstractMachine(IMachineRoot mchRoot) {
		if (!mchRoot.exists()) {
			return null;
		}
		try {
			final IRefinesMachine[] refines = mchRoot.getRefinesClauses();
			if (refines.length > 0) {
				return (IMachineRoot) refines[0].getAbstractMachine().getRoot();
			}
		} catch (RodinDBException e) {
			// ignore
		}
		return null;
	}

	// Returns true if rf is a machine and parent is one of its abstractions
	private boolean isAbstractMachine(IRodinFile rf, IRodinFile parent) {
		final IInternalElement root = rf.getRoot();
		if (root.getElementType() != IMachineRoot.ELEMENT_TYPE)
			return false;
		final IMachineRoot mchRoot = (IMachineRoot) root;
		final IInternalElement parentRoot = parent.getRoot();

		// detects cycles
		final Set<IInternalElement> absRoots = new LinkedHashSet<IInternalElement>();
		absRoots.add(root);
		
		IMachineRoot abstractRoot = getAbstractMachine(mchRoot);
		while (abstractRoot != null) {
			if (abstractRoot.equals(parentRoot)) {
				return true;
			}
			final boolean added = absRoots.add(abstractRoot);
			if (!added) { // cyclic refinement !!!
				return false;
			}
			abstractRoot = getAbstractMachine(abstractRoot);
		}
		return false;
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
		refresh();
	}

	private void processDelta(IRodinElementDelta delta) {
		final IInternalElement root = ((IEventBEditor<?>) getEditor())
				.getRodinInput();
		final IRodinElement element = delta.getElement();
		final IRodinProject rodinProject = root.getRodinProject();

		if (element.equals(root.getRodinDB())) {
			for (IRodinElementDelta child : delta.getAffectedChildren()) {
				if (child.getElement().equals(rodinProject)) {
					processDelta(child);
				}
			}
		} else if (element.equals(rodinProject)) {
			final IRodinFile rodinFile = root.getRodinFile();
			for (IRodinElementDelta child : delta.getAffectedChildren()) {
				final IRodinFile childElement = (IRodinFile) child.getElement();
				if (childElement.equals(rodinFile)
						|| isAbstractMachine(rodinFile, childElement)) {
					needsUpdate = true;
					return;
				}
			}
		}
	}
	
	@Override
	public void setFocus() {
		// Super method try to focus on the first children which is the first
		// link, that cause the page to scroll automatic to the top.
		// Do nothing
	}

}
