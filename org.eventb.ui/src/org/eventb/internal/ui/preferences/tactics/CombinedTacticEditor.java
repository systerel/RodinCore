/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static org.eventb.internal.ui.utils.Messages.wizard_editprofile_combedit_list_combinators;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofile_combedit_list_profiles;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofile_combedit_list_tactics;
import static org.eventb.internal.ui.utils.Messages.wizard_editprofile_combedit_noselectedtactic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.CombinatorNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.ITacticNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.ITacticRefreshListener;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.ProfileNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.SimpleNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.TacticNodeLabelProvider;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.ViewerSelectionDragEffect;

/**
 * @author Nicolas Beauger
 *
 */
public class CombinedTacticEditor extends AbstractTacticViewer<ITacticDescriptor> {
	
	private static class TacSelListener implements ISelectionChangedListener {
		
		private final Text descr;
		private Viewer currentSource = null;
		
		public TacSelListener(Text text) {
			this.descr = text;
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			updateDescr(event);
			
			emptyCurrentSelection(event);
		}

		private void updateDescr(SelectionChangedEvent event) {
			if (descr == null || descr.isDisposed()) {
				return;
			}
			final ISelection selection = event.getSelection();
			if (selection == null) {
				descr.setText(wizard_editprofile_combedit_noselectedtactic);
				return;
			}
			if (!(selection instanceof IStructuredSelection) || selection.isEmpty()) {
				return;
			}
			final IStructuredSelection sel = (IStructuredSelection) selection;
			final Object first = sel.getFirstElement();
			if (!(first instanceof ITacticNode)) {
				return;
			}
			final ITacticNode node = (ITacticNode) first;
			final String description = node.getDescription();
			descr.setText(description);
		}

		private void emptyCurrentSelection(SelectionChangedEvent event) {
			final Object newSource = event.getSource();
			if (newSource instanceof Viewer && newSource != currentSource) {
				if (currentSource != null) {
					currentSource.setSelection(StructuredSelection.EMPTY);
				}
				currentSource = (Viewer) newSource;
			}
		}

	}
	
	// TODO make a 'trash' composite where user can drop tactic nodes 
	// (destroys ? stores with a viewer ?)

	private final TacticsProfilesCache cache;
	private final CombinedTacticViewer combViewer = new CombinedTacticViewer();
	private Composite composite;
	private ListViewer simpleList;
	private ListViewer combList;
	private ListViewer refList;
	private Text descrText;

	private TacSelListener tacSelListener;

	public CombinedTacticEditor(TacticsProfilesCache profiles) {
		this.cache = profiles;
	}
	
	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public void createContents(Composite parent) {
		composite = makeGrid(parent, 3);
		
		simpleList = makeListViewer(composite, wizard_editprofile_combedit_list_tactics);
		
		combViewer.createContents(composite);
		combViewer.addEditSupport();
		
		final Composite combRefDescr = makeRightRow(composite);
		combList = makeListViewer(combRefDescr, wizard_editprofile_combedit_list_combinators);
		
		refList = makeListViewer(combRefDescr, wizard_editprofile_combedit_list_profiles);
		
		final Group descrGroup = makeGroup(combRefDescr, "Description");
		descrText = makeDescription(descrGroup);
		tacSelListener = new TacSelListener(descrText);
		addDescriptionListener();
	}

	private static Text makeDescription(final Composite parent) {
		final Text text = new Text(parent, SWT.WRAP | SWT.V_SCROLL
				| SWT.READ_ONLY);
		final GridData layoutData = new GridData();
		layoutData.exclude = true; // avoid packing
		text.setLayoutData(layoutData);
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				text.setBounds(parent.getClientArea());
			}
		});
		return text;
	}

	private void addDescriptionListener() {
		simpleList.addSelectionChangedListener(tacSelListener);
		combList.addSelectionChangedListener(tacSelListener);
		refList.addSelectionChangedListener(tacSelListener);
	}

	private void removeDescriptionListener() {
		simpleList.removeSelectionChangedListener(tacSelListener);
		combList.removeSelectionChangedListener(tacSelListener);
		refList.removeSelectionChangedListener(tacSelListener);
	}

	private static ListViewer makeListViewer(Composite parent, String text) {
		final TacticNodeLabelProvider labelProvider = new TacticNodeLabelProvider();
		final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer
				.getTransfer() };

		final Group group = makeGroup(parent, text);
		final ListViewer viewer = new ListViewer(group);
		viewer.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		
		viewer.setLabelProvider(labelProvider);
		
		viewer.setComparator(new ViewerComparator());
		
		final ViewerSelectionDragEffect simpleDrag = new ViewerSelectionDragEffect(
				viewer);
		viewer.addDragSupport(DND.DROP_MOVE, transferTypes, simpleDrag);
		return viewer;
	}
	
	private static Group makeGroup(Composite parent, String text) {
		final Group group = new Group(parent, SWT.NO_FOCUS);
		group.setText(text);
		group.setLayout(new GridLayout());
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumWidth = 200;
		layoutData.minimumHeight = 200;
		group.setLayoutData(layoutData);
		return group;
	}

	private static Composite makeGrid(Composite parent, int numColumns) {
		final Composite composite = new Composite(parent, SWT.NO_FOCUS);
		final GridLayout compLayout = new GridLayout();
		compLayout.numColumns = numColumns;
		composite.setLayout(compLayout);
		final GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING,
				true, true);
		composite.setLayoutData(gridData);
		return composite;
	}

	private static Composite makeRightRow(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NO_FOCUS);
		final GridLayout compLayout = new GridLayout(1, false);
		composite.setLayout(compLayout);
		final GridData gridData = new GridData(SWT.END, SWT.BEGINNING,
				true, true);
		composite.setLayoutData(gridData);
		return composite;
	}

	@Override
	public void setInput(ITacticDescriptor desc) {
		initAutoTactics();
		combViewer.setInput(desc);
		initCombinators();
		initProfiles();
	}

	private void initAutoTactics() {
		simpleList.getList().removeAll();
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final String[] autoTacs = reg.getRegisteredIDs();
		final SimpleNode[] combNodes = new SimpleNode[autoTacs.length];
		for (int i = 0; i < autoTacs.length; i++) {
			final String tacticId = autoTacs[i];
			final ITacticDescriptor desc = reg.getTacticDescriptor(tacticId);
			combNodes[i] = new SimpleNode(desc);
		}
		simpleList.add(combNodes);
	}

	private void initCombinators() {
		combList.getList().removeAll();
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final ICombinatorDescriptor[] combinators = reg
				.getCombinatorDescriptors();
		final CombinatorNode[] combNodes = new CombinatorNode[combinators.length];
		for (int i = 0; i < combinators.length; i++) {
			combNodes[i] = new CombinatorNode(null, combinators[i]);
		}
		combList.add(combNodes);
	}

	private void initProfiles() {
		refList.getList().removeAll();
		final List<IPrefMapEntry<ITacticDescriptor>> profiles = cache.getEntries();
		final List<ITacticNode> profileNodes = new ArrayList<ITacticNode>(profiles.size());
		for (IPrefMapEntry<ITacticDescriptor> profile : profiles) {
			final ProfileNode profileNode = new ProfileNode(profile);
			profileNodes.add(profileNode);
		}
		refList.add(profileNodes.toArray(new ITacticNode[profileNodes.size()]));
	}

	@Override
	public Object getInput() {
		return combViewer.getInput();
	}

	@Override
	public ITacticDescriptor getEditResult() {
		return combViewer.getEditResult();
	}

	public boolean isResultValid() {
		return combViewer.isResultValid();
	}

	@Override
	public ISelection getSelection() {
		return combViewer.getSelection();
	}

	@Override
	public void refresh() {
		combViewer.refresh();
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		combViewer.setSelection(selection, reveal);
	}
	
	public void addTacticRefreshListener(ITacticRefreshListener listener) {
		combViewer.addTacticRefreshListener(listener);
	}
	
	public void removedTacticRefreshListener(ITacticRefreshListener listener) {
		combViewer.removedTacticRefreshListener(listener);
	}

	@Override
	public void dispose() {
		removeDescriptionListener();
		super.dispose();
	}
	
}
