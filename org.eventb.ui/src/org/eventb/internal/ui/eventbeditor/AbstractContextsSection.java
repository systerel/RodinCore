/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - used IAttributeFactory
 *     Systerel - prevented from editing generated elements
 *     Systerel - used eclipse decorator mechanism
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.internal.ui.RodinElementTableLabelProvider;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractContextManipulation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class AbstractContextsSection<R extends IInternalElement, E extends IInternalElement>
		extends SectionPart implements IElementChangedListener,
		ISelectionChangedListener {

	protected IEventBEditor<R> editor;

	private Button removeButton;

	private Button addButton;

	TableViewer viewer;

	Combo contextCombo;

	protected R rodinRoot;

	public AbstractContextsSection(IEventBEditor<R> editor,
			FormToolkit toolkit, Composite parent) {

		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		rodinRoot = editor.getRodinInput();
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
				@Override
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
			@Override
			public void dispose() {
				// TODO Empty default
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return getClauses();
			}

			@Override
			public void inputChanged(Viewer viewer1, Object oldInput,
					Object newInput) {
				// TODO Empty default
			}
		});
		final ILabelDecorator decorator = PlatformUI.getWorkbench()
				.getDecoratorManager().getLabelDecorator();
		viewer.setLabelProvider(new DecoratingLabelProvider(
				new RodinElementTableLabelProvider(viewer), decorator));
		viewer.setInput(rodinRoot);
		viewer.addSelectionChangedListener(this);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.heightHint = 150;
		viewer.getTable().setLayoutData(gd);

		contextCombo = new Combo(comp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		contextCombo.setLayoutData(gd);

		contextCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addClauseInRunnable(contextCombo.getText());
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = contextCombo.getSelectionIndex();
				if (index != -1) {
					addClauseInRunnable(contextCombo.getItems()[index]);
				}
			}

		});

		contextCombo.addModifyListener(new ModifyListener() {

			@Override
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
		History.getInstance().addOperation(OperationFactory.deleteElement(elements, true));
	}

	@Override
	public final void dispose() {
		RodinCore.removeElementChangedListener(this);
		viewer.removeSelectionChangedListener(this);
		super.dispose();
	}

	@Override
	public final void elementChanged(ElementChangedEvent event) {
		Control control = viewer.getControl();
		if (control.isDisposed())
			return;
		Display display = control.getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				viewer.setInput(rodinRoot);
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
	protected IInternalElement[] getClauses() {
		try {
			return getManipulation().getClauses(getFreeElementContext());
		} catch (RodinDBException e) {
			UIUtils.log(e, "when getting free child name for " + rodinRoot);
			return new IInternalElement[0];
		}
	}

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
	private Set<String> getUsedContextNames() {
		try {
			return getManipulation().getUsedContextNames(getFreeElementContext());
		} catch (RodinDBException e) {
			UIUtils.log(e, "when getting used context names");
			return new HashSet<String>();
		}
	}

	/**
	 * Handle the Add/Create action when the corresponding button is clicked.
	 */
	public final void handleAdd() {
		String contextName = contextCombo.getText();
		addClauseInRunnable(contextName);
	}

	final void initContextCombo() {
		contextCombo.removeAll();
		try {
			for (String context : getContext())
				contextCombo.add(context);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of "
					+ rodinRoot.getRodinProject());
		}
		contextCombo.setEnabled(!isReadOnly(rodinRoot));
	}

	/**
	 * Returns the array of all context names to be displayed in the combo.
	 * 
	 * @return an array of all context names to display
	 */
	private String[] getContext() throws RodinDBException {
		return getManipulation().getPossibleValues(getFreeElementContext(), null);
	}

	@Override
	public final void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}

	final void updateButtons() {
		if (isReadOnly(rodinRoot)) {
			removeButton.setEnabled(false);
			addButton.setEnabled(false);
		} else {
			setRemoveEnabled();
			final String text = contextCombo.getText();
			if (text.equals("")) {
				addButton.setEnabled(false);
			} else {
				addButton.setEnabled(!getUsedContextNames().contains(text));
			}
		}
	}

	private void setRemoveEnabled() {
		final ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			final IStructuredSelection sSelection = (IStructuredSelection) selection;
			boolean ro = false;
			for (Object o : sSelection.toList()) {
				if (o instanceof IInternalElement) {
					ro = isReadOnly((IInternalElement) o);
					if (ro)
						break;
				}
			}
			removeButton.setEnabled(!ro);
		} else {
			removeButton.setEnabled(!selection.isEmpty());
		}
	}

	/**
	 * Returns an handle of a free element
	 */
	protected abstract E getFreeElementContext() throws RodinDBException;

	protected abstract AbstractContextManipulation<E> getManipulation();

}