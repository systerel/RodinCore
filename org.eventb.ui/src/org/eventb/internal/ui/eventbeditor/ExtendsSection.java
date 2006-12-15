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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixExtendsContextName;
import org.eventb.ui.ElementLabelProvider;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
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
	private IEventBEditor editor;

	// Buttons.
	private Button removeButton;

	// private Button chooseButton;

	private Button addButton;

	// The table viewer
	TableViewer viewer;

	Combo contextCombo;

	// The seen internal element.
	// private IInternalElement seen;

	IContextFile rodinFile;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The Form editor contains this section
	 * @param parent
	 *            The composite parent
	 */
	public ExtendsSection(IEventBEditor<IContextFile> editor, FormToolkit toolkit,
			Composite parent) {

		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		rodinFile = editor.getRodinInput();

		createClient(getSection(), toolkit);
		RodinCore.addElementChangedListener(this);
	}

	class ExtendedContextContentProvider implements IStructuredContentProvider {

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
			// TODO Empty default
		}

		public void inputChanged(Viewer viewer1, Object oldInput,
				Object newInput) {
			// TODO Empty default
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
			@Override
			public void widgetSelected(SelectionEvent e) {
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
		viewer.setLabelProvider(new ElementLabelProvider(viewer));
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
			@Override
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
	 * @param contextName
	 *            name of the context
	 */
	void addExtendedContext(String contextName) {
		try {
			final String elementName = UIUtils.getFreeElementName(editor,
					rodinFile, IExtendsContext.ELEMENT_TYPE,
					PrefixExtendsContextName.QUALIFIED_NAME,
					PrefixExtendsContextName.DEFAULT_PREFIX);
			IExtendsContext extended = rodinFile.getExtendsClause(elementName);
			extended.create(null, null);
			extended.setAbstractContextName(contextName, null);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when adding an extends clause.");
		}
	}

	/**
	 * Handle the Add/Create action when the corresponding button is clicked.
	 */
	public void handleAdd() {
		String contextName = contextCombo.getText();
		addExtendedContext(contextName);
	}

	public void elementChanged(ElementChangedEvent event) {
		Control control = viewer.getControl();
		if (control.isDisposed())
			return;
		Display display = control.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				viewer.setInput(rodinFile);
				initContextCombo();
				updateButtons();
			}

		});
	}

	void initContextCombo() {
		contextCombo.removeAll();
		final IRodinProject project = rodinFile.getRodinProject();
		final IContextFile[] contexts;
		try {
			contexts = (IContextFile[]) project
					.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of " + project);
			return;
		}
		final Set<String> usedNames = getUsedContextNames();
		for (IContextFile context : contexts) {
			final String elementName = context.getElementName();
			final String bareName = EventBPlugin.getComponentName(elementName);
			if (!usedNames.contains(bareName)) {
				contextCombo.add(bareName);
			}
		}
	}

	private Set<String> getUsedContextNames() {
		Set<String> usedNames = new HashSet<String>();

		// First add myself
		final String elementName = rodinFile.getElementName();
		final String bareName = EventBPlugin.getComponentName(elementName);
		usedNames.add(bareName);

		// Then, all contexts already extended
		final IExtendsContext[] extendsClauses;
		try {
			extendsClauses = rodinFile.getExtendsClauses();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the extends clause of " + rodinFile);
			return usedNames;
		}
		for (IExtendsContext extend : extendsClauses) {
			try {
				usedNames.add(extend.getAbstractContextName());
			} catch (RodinDBException e) {
				UIUtils.log(e, "when reading the extends clause " + extend);
			}
		}
		return usedNames;
	}
	
	
	void updateButtons() {
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
				for (IRodinElement extendedContext : extendedContexts) {
					if (((IExtendsContext) extendedContext)
							.getAbstractContextName()
							.equals(text)) {
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