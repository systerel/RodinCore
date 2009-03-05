/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import java.util.ArrayList;
import java.util.Iterator;

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
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

/**
 * DetailsPage for sequents. It is used within the proof skeleton
 * MasterDetailsBlock as the page to display for all proof tree nodes.
 * 
 * @author Nicolas Beauger
 * 
 */
public class SequentDetailsPage implements IDetailsPage {

	private static final Font EVENTB_FONT = JFaceResources
			.getFont(PreferenceConstants.EVENTB_MATH_FONT);

	private ListViewer viewer;

	private static SequentDetailsPage instance;
	
	private static final IStructuredContentProvider sequentContentProvider =
		new IStructuredContentProvider() {
		
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof IProverSequent) {
				return sequentToStrings((IProverSequent) inputElement);
			} else {
				return new Object[0];
			}
		}

		public void dispose() {
			// Do nothing
		}

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
	
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new ListViewer(parent);
		viewer.getControl().setFont(EVENTB_FONT);
		viewer.setContentProvider(sequentContentProvider);
		addPopUpMenu();
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

	public void commit(boolean onSave) {
		// Do nothing
	}

	public void dispose() {
		viewer.getList().dispose();
		viewer.getControl().dispose();
	}

	public void initialize(IManagedForm form) {
		// Do nothing
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		viewer.refresh();
	}

	public void setFocus() {
		// Do nothing
	}

	public boolean setFormInput(Object input) {
		if (input instanceof IProofTreeNode) {
			viewer.setInput(((IProofTreeNode) input).getSequent());
		} else {
			viewer.setInput(null);
		}
		return true;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		if (selection.isEmpty())
			return;

		if (selection instanceof ITreeSelection) {
			final Object selectedNode = ((ITreeSelection) selection).getPaths()[0]
					.getLastSegment();
			if (selectedNode != null && selectedNode instanceof IProofTreeNode) {
				viewer.setInput(((IProofTreeNode) selectedNode).getSequent());
			}
		}
	}

	static String[] sequentToStrings(IProverSequent sequent) {
		ArrayList<String> seqElements = new ArrayList<String>();

		Iterator<Predicate> iter = sequent.hypIterable().iterator();
		while (iter.hasNext()) {
			seqElements.add(iter.next().toString());
		}
		seqElements.add("\u22A2");
		seqElements.add(sequent.goal().toString());
		return seqElements.toArray(new String[seqElements.size()]);
	}

}
