/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     ETH Zurich - adapted to org.rodinp.keyboard.ui
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;

/**
 * DetailsPage for sequents. It is used within the proof skeleton
 * MasterDetailsBlock as the page to display for all proof tree nodes.
 * 
 * @author Nicolas Beauger
 * 
 */
public class SequentDetailsPage implements IDetailsPage {

	private ListViewer viewer;

	private static SequentDetailsPage instance;
	
	private static final IStructuredContentProvider sequentContentProvider =
		new IStructuredContentProvider() {
		
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IProverSequent) {
				return sequentToStrings((IProverSequent) inputElement);
			} else {
				return new Object[0];
			}
		}

		@Override
		public void dispose() {
			// Do nothing
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// Do nothing
		}
	};

	private SequentDetailsPage() {
		// Singleton : private constructor
	}

	public static SequentDetailsPage getDefault() {
		if (instance == null) {
			instance = new SequentDetailsPage();
		}
		return instance;
	}
	
	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new ListViewer(parent);
		setFont(JFaceResources.getFont(RODIN_MATH_FONT));
		viewer.setContentProvider(sequentContentProvider);
		addPopUpMenu();
	}

	public void setFont(Font font) {
		if (viewer == null || viewer.getControl().isDisposed())
			return;
		viewer.getControl().setFont(font);
	}

	private void addPopUpMenu() {
		final MenuManager popupMenu = new MenuManager();
		final Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
				.getDisplay());
		final IAction copyAction = new CopyAction(viewer, clipboard);
		popupMenu.add(copyAction);
		Menu menu = popupMenu.createContextMenu(viewer.getList());
		viewer.getList().setMenu(menu);
	}

	@Override
	public void commit(boolean onSave) {
		// Do nothing
	}

	@Override
	public void dispose() {
		viewer.getList().dispose();
		viewer.getControl().dispose();
	}

	@Override
	public void initialize(IManagedForm form) {
		// Do nothing
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		viewer.refresh();
	}

	@Override
	public void setFocus() {
		// Do nothing
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof IProofTreeNode) {
			viewer.setInput(((IProofTreeNode) input).getSequent());
		} else {
			viewer.setInput(null);
		}
		return true;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (selection.isEmpty())
			return;

		if (selection instanceof ITreeSelection) {
			final Object selectedNode = ((ITreeSelection) selection).getPaths()[0]
					.getLastSegment();
			if (selectedNode instanceof IProofTreeNode) {
				viewer.setInput(((IProofTreeNode) selectedNode).getSequent());
			}
		}
	}

	static String[] sequentToStrings(IProverSequent sequent) {
		ArrayList<String> seqElements = new ArrayList<String>();

		for (Predicate predicate : sequent.hypIterable()) {
			seqElements.add(predicate.toString());
		}
		seqElements.add("\u22A2");
		seqElements.add(sequent.goal().toString());
		return seqElements.toArray(new String[seqElements.size()]);
	}

}
