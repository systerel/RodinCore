/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.preferences.autotactics.ITacticProfileCache;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.IDynamicTacticRef;
import org.eventb.core.seqprover.ITacticDescriptor;
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
	
	private static enum TacticCategories {
		DISCHARGE("Discharge"), MIXED("Mixed"), SIMPLIFY("Simplify"), SPLIT(
				"Split"), OTHER("");

		private final String key;

		private TacticCategories(String key) {
			this.key = key;
		}

		public static int getCategory(String text) {
			for (TacticCategories tacCat : values()) {
				if (text.contains(tacCat.key)) {
					return tacCat.ordinal();
				}
			}
			return OTHER.ordinal();
		}
	}
	
	private static final class TacticNodeComparator extends ViewerComparator {

		public TacticNodeComparator() {
			// avoid synthetic access
		}
	
		@Override
		public int category(Object element) {
			if (!(element instanceof ITacticNode)) {
				return -1;
			}
			final ITacticNode node = (ITacticNode) element;
			// FIXME poor hack, not developer-friendly
			// works only with core autotactics
			// support categorization in autotactic extension point instead
			final String text = node.getText();
			return TacticCategories.getCategory(text);
		}
	}

	// TODO make a 'trash' composite where user can drop tactic nodes 
	// (destroys ? stores with a viewer ?)

	private final ITacticProfileCache cache;
	private final CombinedTacticViewer combViewer = new CombinedTacticViewer();
	private Composite composite;
	private ListViewer simpleList;
	private ListViewer combList;
	private ListViewer refList;
	private Text descrText;

	private TacSelListener tacSelListener;

	public CombinedTacticEditor(ITacticProfileCache profiles) {
		this.cache = profiles;
	}
	
	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public void createContents(Composite parent) {
		composite = makeGrid(parent, 3);

		simpleList = makeListViewer(composite,
				wizard_editprofile_combedit_list_tactics, true);
		simpleList.setComparator(new TacticNodeComparator());

		combViewer.createContents(composite);
		combViewer.addEditSupport();

		final Composite combRefDescr = makeRightRow(composite);
		combList = makeListViewer(combRefDescr,
				wizard_editprofile_combedit_list_combinators, false);

		refList = makeListViewer(combRefDescr,
				wizard_editprofile_combedit_list_profiles, false);

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

	private static ListViewer makeListViewer(Composite parent, String text,
			boolean withFilter) {
		final TacticNodeLabelProvider labelProvider = new TacticNodeLabelProvider();
		final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer
				.getTransfer() };

		final Group group = makeGroup(parent, text);
		Text filter = null;
		if (withFilter) {
			filter = createFilter(group);
		}
		final ListViewer viewer = new ListViewer(group);
		viewer.getList().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, true));
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(labelProvider);
		
		viewer.setComparator(new ViewerComparator());
		
		final ViewerSelectionDragEffect simpleDrag = new ViewerSelectionDragEffect(
				viewer);
		viewer.addDragSupport(DND.DROP_MOVE, transferTypes, simpleDrag);
		
		if (withFilter) {
			setupFilter(filter, viewer);
		}
		return viewer;
	}
	
	private static Text createFilter(Composite parent) {
		final Text filterText = new Text(parent, SWT.BORDER | SWT.SEARCH
				| SWT.ICON_SEARCH | SWT.CANCEL | SWT.ICON_CANCEL);
		filterText
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return filterText;
	}

	private static void setupFilter(final Text filter, final ListViewer viewer) {
		filter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				viewer.refresh();
			}
		});
		viewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer v, Object parentElement, Object element) {
				final String text = ((ILabelProvider) ((StructuredViewer) v)
						.getLabelProvider()).getText(element);
				if (text == null) {
					return false;
				}
				final String filterText = filter.getText();
				return text.toUpperCase().contains(filterText.toUpperCase());
			}
		});
	}
	
	private static Group makeGroup(Composite parent, String text) {
		final Group group = new Group(parent, SWT.NO_FOCUS);
		group.setText(text);
		group.setLayout(new GridLayout());
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
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
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL,
				true, true);
		composite.setLayoutData(gridData);
		return composite;
	}

	private static Composite makeRightRow(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NO_FOCUS);
		final GridLayout compLayout = new GridLayout(1, false);
		composite.setLayout(compLayout);
		final GridData gridData = new GridData(SWT.END, SWT.FILL,
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
		final IDynamicTacticRef[] dynTacticRefs = reg.getDynTacticRefs();
		final SimpleNode[] simpleNodes = new SimpleNode[autoTacs.length
				+ dynTacticRefs.length];
		for (int i = 0; i < autoTacs.length; i++) {
			final String tacticId = autoTacs[i];
			final ITacticDescriptor desc = reg.getTacticDescriptor(tacticId);
			simpleNodes[i] = new SimpleNode(desc);
		}
		for (int i = 0; i < dynTacticRefs.length; i++) {
			simpleNodes[autoTacs.length + i] = new SimpleNode(dynTacticRefs[i]);
		}
		simpleList.setInput(simpleNodes);
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
		combList.setInput(combNodes);
	}

	private void initProfiles() {
		refList.getList().removeAll();
		final List<IPrefMapEntry<ITacticDescriptor>> profiles = cache.getEntries();
		final List<ITacticNode> profileNodes = new ArrayList<ITacticNode>(profiles.size());
		for (IPrefMapEntry<ITacticDescriptor> profile : profiles) {
			final ProfileNode profileNode = new ProfileNode(profile);
			profileNodes.add(profileNode);
		}
		refList.setInput(profileNodes.toArray(new ITacticNode[profileNodes.size()]));
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
