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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
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
import org.eventb.core.IContextFile;
import org.eventb.internal.ui.RodinElementTableLabelProvider;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class AbstractContextsSection<F extends IRodinFile> extends
		SectionPart implements IElementChangedListener,
		ISelectionChangedListener {

	protected IEventBEditor<F> editor;

	private Button removeButton;

	private Button addButton;

	TableViewer viewer;

	Combo contextCombo;

	protected F rodinFile;

	public AbstractContextsSection(IEventBEditor<F> editor,
			FormToolkit toolkit, Composite parent) {

		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		rodinFile = editor.getRodinInput();
		createClient(getSection(), toolkit);
		RodinCore.addElementChangedListener(this);
	}

	/**
	 * Add the clause for the context with the given name. This method will be
	 * run in a workspace runnable.
	 * 
	 * @param contextName
	 *            name of the context to add
	 */
	protected abstract void addClause(String contextName)
			throws RodinDBException;

	// This is the method we use in this class, not the one to be overriden by
	// clients.
	protected final void addClauseInRunnable(final String contextName) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws RodinDBException {
					addClause(contextName);
				}
			}, null);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when creating a clause");
		}
	}

	/**
	 * Creates the content of the section.
	 * <p>
	 * 
	 * @param section
	 *            the parent section
	 * @param toolkit
	 *            the FormToolkit used to create the content
	 */
	public final void createClient(Section section, FormToolkit toolkit) {
		section.setText(getTitle());
		section.setDescription(getDescription());
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
			public void widgetSelected(SelectionEvent event) {
				ISelection sel = viewer.getSelection();
				if (sel instanceof IStructuredSelection) {
					try {
						deleteElements((IStructuredSelection) sel, null);
					} catch (RodinDBException e) {
						UIUtils.log(e, "when deleting selected elements");
					}
				}
			}
		});

		viewer = new TableViewer(comp);
		viewer.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {
				// TODO Empty default
			}

			public Object[] getElements(Object inputElement) {
				return getClauses();
			}

			public void inputChanged(Viewer viewer1, Object oldInput,
					Object newInput) {
				// TODO Empty default
			}
		});
		viewer.setLabelProvider(new RodinElementTableLabelProvider(viewer));
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

			public void widgetDefaultSelected(SelectionEvent e) {
				addClauseInRunnable(contextCombo.getText());
			}

			public void widgetSelected(SelectionEvent e) {
				int index = contextCombo.getSelectionIndex();
				if (index != -1) {
					addClauseInRunnable(contextCombo.getItems()[index]);
				}
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
	 * Delete all internal elements that are in the given structured selection.
	 * 
	 * @param ssel
	 *            structured selection in viewer
	 * @throws RodinDBException
	 */
	final void deleteElements(IStructuredSelection ssel, IProgressMonitor pm)
			throws RodinDBException {

		final ArrayList<IInternalElement> elementList = new ArrayList<IInternalElement>();
		final Iterator<?> it = ssel.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof IInternalElement) {
				elementList.add((IInternalElement) obj);
			}
		}

		final int size = elementList.size();
		if (size == 0) {
			return;
		}

		final IInternalElement[] elements = elementList
				.toArray(new IInternalElement[size]);
		rodinFile.getRodinDB().delete(elements, true, pm);
	}

	@Override
	public final void dispose() {
		RodinCore.removeElementChangedListener(this);
		viewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	public final void elementChanged(ElementChangedEvent event) {
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

	/**
	 * Returns all clauses of this file. They will be displayed in the viewer.
	 * 
	 * @return all clauses of this file
	 */
	protected abstract IInternalElement[] getClauses();

	/**
	 * Returns the descriptive text of this section
	 * 
	 * @return the descriptive text of this section
	 */
	protected abstract String getDescription();

	/**
	 * Returns the title of this section
	 * 
	 * @return the title of this section
	 */
	protected abstract String getTitle();

	/**
	 * Returns the set of all context names which are considered as already
	 * used, so that they won't be displayed in the combo.
	 * 
	 * @return the set of all used context names
	 */
	protected abstract Set<String> getUsedContextNames();

	/**
	 * Handle the Add/Create action when the corresponding button is clicked.
	 */
	public final void handleAdd() {
		String contextName = contextCombo.getText();
		addClauseInRunnable(contextName);
	}

	final void initContextCombo() {
		contextCombo.removeAll();
		final IRodinProject project = rodinFile.getRodinProject();
		final IContextFile[] contexts;
		try {
			contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of " + project);
			return;
		}
		final Set<String> usedNames = getUsedContextNames();
		for (IContextFile context : contexts) {
			final String bareName = context.getComponentName();
			if (!usedNames.contains(bareName)) {
				contextCombo.add(bareName);
			}
		}
	}

	public final void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}

	final void updateButtons() {
		removeButton.setEnabled(!viewer.getSelection().isEmpty());
		final String text = contextCombo.getText();
		if (text.equals("")) {
			addButton.setEnabled(false);
		} else {
			addButton.setEnabled(!getUsedContextNames().contains(text));
		}
	}

}