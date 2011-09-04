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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.ui.preferences.AbstractEventBPreferencePage;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.CombinatorNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.ITacticNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.ProfileNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.SimpleNode;
import org.eventb.internal.ui.preferences.tactics.CombinedTacticViewer.TacticNodeLabelProvider;

/**
 * @author Nicolas Beauger
 *
 */
public class CombinedTacticEditor extends AbstractTacticViewer<ITacticDescriptor> {
	// TODO make a 'trash' composite where user can drop tactic nodes 
	// (destroys ? stores with a viewer ?)

	private final TacticsProfilesCache cache;
	private final CombinedTacticViewer combViewer = new CombinedTacticViewer();
	private Composite composite;
	private ListViewer simpleList;
	private ListViewer combList;
	private ListViewer refList;

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
		
		final TacticNodeLabelProvider labelProvider = new TacticNodeLabelProvider();
		
		final Group simpleGroup = makeGroup(composite);
		simpleGroup.setText("Tactics");
		simpleGroup.setLayout(new GridLayout());
		simpleList = new ListViewer(simpleGroup, SWT.SINGLE);
		simpleList.setLabelProvider(labelProvider);
		
		combViewer.createContents(composite);
		combViewer.addEditSupport();
		// TODO add a remove action (key+right click)
		
		final Composite combAndRef = makeGrid(composite, 1);
		final Group combGroup = makeGroup(combAndRef);
		combGroup.setText("Combinators");
		combGroup.setLayout(new GridLayout());
		combList = new ListViewer(combGroup, SWT.SINGLE);
		combList.setLabelProvider(labelProvider);
		
		final Group refGroup = makeGroup(combAndRef);
		refGroup.setText("Profiles");
		refGroup.setLayout(new GridLayout());
		refList = new ListViewer(refGroup, SWT.SINGLE);
		refList.setLabelProvider(labelProvider);
	}

	private static Group makeGroup(final Composite parent) {
		final int style =  SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		return new Group(parent, style);
	}

	private static Composite makeGrid(Composite parent, int numColumns) {
		final Composite composite = new Composite(parent, SWT.FILL
				| SWT.NO_FOCUS | SWT.V_SCROLL);
		final GridLayout compLayout = new GridLayout();
		compLayout.numColumns = numColumns;
		composite.setLayout(compLayout);
		AbstractEventBPreferencePage.setFillParent(composite);
		return composite;
	}

	@Override
	public void setInput(ITacticDescriptor desc) {
		initAutoTactics();
		combViewer.setInput(desc);
		initCombinators();
		initProfiles();
		
		simpleList.getControl().pack();
		combViewer.getControl().pack();
		combList.getControl().pack();
		refList.getControl().pack();
		TacticPreferenceUtils.packAll(composite, 8);
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
	public ITacticDescriptor getInput() {
		final Object input = combViewer.getInput();
		if (!(input instanceof ITacticDescriptor)) {
			return null;
		}
		return (ITacticDescriptor) input;
	}

	@Override
	public ITacticDescriptor getEditResult() {
		return combViewer.getEditResult();
	}

	public boolean isResultValid() {
		return combViewer.isResultValid();
	}
	
}
