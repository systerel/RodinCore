/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.Iterator;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editting Sees
 *         clause.
 */
public class ExtendsSection extends SectionPart implements
		IElementChangedListener, ISelectionChangedListener {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Abstract Contexts";

	private static final String SECTION_DESCRIPTION = "Select abstract contexts of this context";

	// The Form editor contains this section.
	private FormEditor editor;

	// Buttons.
	private Button removeButton;

	// private Button chooseButton;

	private Button addButton;

	// The table viewer
	private TableViewer viewer;

	private Combo contextCombo;

	// The seen internal element.
	// private IInternalElement seen;

	private IRodinFile rodinFile;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The Form editor contains this section
	 * @param page
	 *            The Dependencies page contains this section
	 * @param parent
	 *            The composite parent
	 */
	public ExtendsSection(FormEditor editor, FormToolkit toolkit,
			Composite parent) {
		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		rodinFile = ((EventBEditor) editor).getRodinInput();

		createClient(getSection(), toolkit);
		RodinCore.addElementChangedListener(this);
	}

	private class ExtendedContextContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			try {
				return rodinFile
						.getChildrenOfType(IExtendsContext.ELEMENT_TYPE);
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
			return new Object[0];
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

	/**
	 * Creat the content of the section.
	 * <p>
	 * 
	 * @param section
	 *            the parent section
	 * @param toolkit
	 *            the FormToolkit used to create the content
	 */
	public void createClient(Section section, FormToolkit toolkit) {
		section.setText(SECTION_TITLE);
		section.setDescription(SECTION_DESCRIPTION);
		Composite comp = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);

		removeButton = toolkit.createButton(comp, "Remove", SWT.PUSH);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		removeButton.setLayoutData(gd);

		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UIUtils.debugEventBEditor("Here");
				ISelection sel = viewer.getSelection();
				if (sel instanceof IStructuredSelection) {
					final IStructuredSelection ssel = (IStructuredSelection) sel;
					try {
						RodinCore.run(new IWorkspaceRunnable() {
							public void run(IProgressMonitor monitor)
									throws CoreException {
								for (Iterator it = ssel.iterator(); it
										.hasNext();) {
									Object obj = it.next();
									if (obj instanceof IInternalElement) {
										((IInternalElement) obj).delete(true,
												null);
									}
								}
							}

						}, null);
					} catch (CoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		viewer = new TableViewer(comp);
		viewer.setContentProvider(new ExtendedContextContentProvider());
		viewer.setLabelProvider(new UIUtils.ElementLabelProvider());
		viewer.setInput(rodinFile);
		viewer.addSelectionChangedListener(this);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.heightHint = 150;
		viewer.getTable().setLayoutData(gd);

		contextCombo = new Combo(comp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		contextCombo.setLayoutData(gd);

		contextCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int index = contextCombo.getSelectionIndex();
				if (index != -1) {
					addExtendedContext(contextCombo.getItems()[index]);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				addExtendedContext(contextCombo.getText());
			}

		});

		contextCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateButtons();
			}

		});

		addButton = new Button(comp, SWT.PUSH);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			}
		});

		toolkit.paintBordersFor(comp);
		section.setClient(comp);

		initContextCombo();
		updateButtons();
	}

	/**
	 * Set the seen context with the given name.
	 * <p>
	 * 
	 * @param context
	 *            name of the context
	 */
	private void addExtendedContext(String context) {
		try {
			IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
			IInternalElement extended = rodinFile.createInternalElement(
					IExtendsContext.ELEMENT_TYPE, context, null, null);
			extended.setContents(context);
			// markDirty();
		} catch (RodinDBException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Handle the Add/Create action when the corresponding button is clicked.
	 */
	public void handleAdd() {
		String context = contextCombo.getText();
		try {
			IInternalElement extended = rodinFile.createInternalElement(
					IExtendsContext.ELEMENT_TYPE, context, null, null);
			extended.setContents(context);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		IRodinProject project = (IRodinProject) rodinFile.getParent();
//		String contextFileName = EventBPlugin.getContextFileName(context);
//		IRodinFile contextFile = project.getRodinFile(contextFileName);
//		if (!contextFile.exists()) {
//			boolean answer = MessageDialog
//					.openQuestion(
//							this.getSection().getShell(),
//							"Create Context",
//							"Context "
//									+ contextFileName
//									+ " does not exist. Do you want to create new extended context?");
//
//			if (!answer)
//				return;
//
//			try {
//				contextFile = project.createRodinFile(contextFileName, true,
//						null);
//			} catch (RodinDBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		UIUtils.linkToEventBEditor(contextFile);

		return;
	}

	public void elementChanged(ElementChangedEvent event) {
		viewer.setInput(rodinFile);
		initContextCombo();
		updateButtons();
	}

	private void initContextCombo() {
		contextCombo.removeAll();
		try {
			IRodinElement[] contexts = ((IParent) rodinFile.getParent())
					.getChildrenOfType(IContextFile.ELEMENT_TYPE);
			IRodinElement[] extendedContexts = rodinFile
					.getChildrenOfType(IExtendsContext.ELEMENT_TYPE);

			for (IRodinElement context : contexts) {

				if (context.equals(rodinFile))
					continue;
				boolean found = false;
				for (IRodinElement extendContext : extendedContexts) {
					if (EventBPlugin.getComponentName(context.getElementName())
							.equals(
									((IInternalElement) extendContext)
											.getContents())) {
						found = true;
						break;
					}
				}

				if (!found)
					contextCombo.add(EventBPlugin.getComponentName(context
							.getElementName()));
			}
		} catch (RodinDBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void updateButtons() {
		removeButton.setEnabled(!viewer.getSelection().isEmpty());
		String text = contextCombo.getText();
		if (text.equals("")) {
			addButton.setEnabled(false);
		} else if (text.equals(EventBPlugin.getComponentName(rodinFile
				.getElementName()))) {
			addButton.setEnabled(false);
		} else {
			try {
				IRodinElement[] extendedContexts = rodinFile
						.getChildrenOfType(IExtendsContext.ELEMENT_TYPE);
				for (IRodinElement seenContext : extendedContexts) {
					if (((IInternalElement) seenContext).getContents().equals(
							text)) {
						addButton.setEnabled(false);
						return;
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			addButton.setEnabled(true);
		}
	}

	@Override
	public void dispose() {
		RodinCore.removeElementChangedListener(this);
		viewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}

}