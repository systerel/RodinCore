/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eventb.internal.ui.IEventBInputText;

public class ProviderModifyListener implements ModifyListener {

	private final Set<WizardProposalProvider> providers;
	private final Set<IEventBInputText> inputs;

	public ProviderModifyListener() {
		providers = new HashSet<WizardProposalProvider>();
		inputs = new HashSet<IEventBInputText>();
	}

	public void addInputText(IEventBInputText text) {
		text.getTextWidget().addModifyListener(this);
		inputs.add(text);
	}

	public void addProvider(WizardProposalProvider provider) {
		providers.add(provider);
		final List<String> ids = getIds();
		provider.setIdentifiers(ids);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		final List<String> ids = getIds();
		for (WizardProposalProvider provider : providers) {
			provider.setIdentifiers(ids);
		}
	}

	private List<String> getIds() {
		final List<String> ids = new ArrayList<String>();
		for (IEventBInputText text : inputs) {
			final String id = text.getTextWidget().getText();
			if (!id.isEmpty()) {
				ids.add(id);
			}
		}
		return ids;
	}
}
