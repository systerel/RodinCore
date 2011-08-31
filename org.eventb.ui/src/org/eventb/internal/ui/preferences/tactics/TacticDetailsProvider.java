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
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;

/**
 * @author Nicolas Beauger
 *
 */
public class TacticDetailsProvider implements IDetailsProvider {

	private final TacticsProfilesCache cache;
	private Composite parent;

	private final SimpleTacticViewer simpleViewer = new SimpleTacticViewer();
	private final ParamTacticViewer paramViewer = new ParamTacticViewer();
	private final CombinedTacticViewer combViewer = new CombinedTacticViewer();
	
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
		simpleViewer.createContents(parent);
		paramViewer.createContents(parent);
		combViewer.createContents(parent);
	}
	
	private void disposeAll() {
		paramViewer.dispose();
		combViewer.dispose();
	}

	@Override
	public void putDetails(String element) {
		final IPrefMapEntry<ITacticDescriptor> profile = cache
				.getEntry(element);
		final ITacticDescriptor desc = profile.getValue();
		hideAll();
		if (desc instanceof ICombinedTacticDescriptor) {
			combViewer.setInput((ICombinedTacticDescriptor) desc);
			combViewer.show();
		} else if (desc instanceof IParamTacticDescriptor) {
			paramViewer.setInput((IParamTacticDescriptor) desc);
			paramViewer.show();
		} else if (desc != null) {
			simpleViewer.setInput(desc);
			simpleViewer.show();
		}
		packAll(parent, 12);
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
