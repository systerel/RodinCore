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

import static org.eventb.internal.ui.preferences.tactics.TacticPreferenceUtils.packAll;

import org.eclipse.swt.widgets.Composite;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;

/**
 * @author Nicolas Beauger
 * TODO split into simple parameterized combined
 */
public class TacticDetailsProvider implements IDetailsProvider {

	private final TacticsProfilesCache cache;
	private Composite parent;

	private final ParamTacticViewer paramViewer = new ParamTacticViewer();
	private final CombinedTacticViewer combViewer = new CombinedTacticViewer();
	private IPrefMapEntry<ITacticDescriptor> currentProfile = null;
	
	public TacticDetailsProvider(TacticsProfilesCache cache) {
		this.cache = cache;
	}

	@Override
	public void setParentComposite(Composite parent) {
		if (parent == this.parent) {
			return;
		}
		this.parent = parent;
		disposeAll();
		paramViewer.createContents(parent);
		combViewer.createContents(parent);
	}
	
	private void disposeAll() {
		paramViewer.dispose();
		combViewer.dispose();
	}

	public ITacticDescriptor getEditResult() {
		if (currentProfile == null) {
			// not editing
			return null;
		}
		// only parameterized tactics can be edited through details
		if (paramViewer.getInput() != currentProfile.getValue()) {
			// not editing a parameterized tactic
			return null;
		}
		return paramViewer.getEditResult();
	}
	
	@Override
	public void putDetails(String element) {
		currentProfile = cache.getEntry(element);
		if (currentProfile == null) return;
		final ITacticDescriptor desc = currentProfile.getValue();
		hideAll();
		if (desc instanceof IParamTacticDescriptor) {
			paramViewer.setInput((IParamTacticDescriptor) desc);
			paramViewer.show();
			packAll(paramViewer.getControl());
		} else {
			combViewer.setInput(desc);
			combViewer.show();
			packAll(combViewer.getControl());
		}
	}

	@Override
	public boolean hasChanges() {
		final ITacticDescriptor currentEditResult = getEditResult();
		return (currentEditResult != null && currentEditResult != currentProfile
				.getValue());
	}
	
	@Override
	public void save() {
		if (currentProfile == null) {
			return;
		}
		final ITacticDescriptor currentEditResult = getEditResult();
		currentProfile.setValue(currentEditResult);
	}
	
	private void hideAll()  {
		paramViewer.hide();
		combViewer.hide();
	}
	
	@Override
	public void clear() {
		hideAll();
	}

}
