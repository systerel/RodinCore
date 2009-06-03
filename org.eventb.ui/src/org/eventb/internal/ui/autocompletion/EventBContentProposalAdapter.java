/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public class EventBContentProposalAdapter {

	private final ContentProposalAdapter adapter;

	private boolean proposalClosed;

	public EventBContentProposalAdapter(Control control,
			IControlContentAdapter controlContentAdapter,
			IContentProposalProvider proposalProvider, KeyStroke keyStroke,
			char[] autoActivationCharacters) {
		adapter = new ContentProposalAdapter(control, controlContentAdapter,
				proposalProvider, keyStroke, autoActivationCharacters);
		proposalClosed = true;
		adapter.addContentProposalListener(new IContentProposalListener2() {
			@SuppressWarnings("synthetic-access")
			public void proposalPopupClosed(ContentProposalAdapter a) {
				proposalClosed = true;
			}

			@SuppressWarnings("synthetic-access")
			public void proposalPopupOpened(ContentProposalAdapter a) {
				proposalClosed = false;
			}

		});
	}

	/**
	 * Returns whether the content proposal popup is closed.
	 * 
	 * @return <code>true</code> if the proposal popup is closed,
	 *         <code>false</code> if it is opened.
	 */
	public boolean IsProposalClosed() {
		return proposalClosed;
	}

	public void addContentProposalListener(IContentProposalListener listener) {
		adapter.addContentProposalListener(listener);
	}

	public void addContentProposalListener(IContentProposalListener2 listener) {
		adapter.addContentProposalListener(listener);
	}

	@Override
	public boolean equals(Object obj) {
		return adapter.equals(obj);
	}

	public char[] getAutoActivationCharacters() {
		return adapter.getAutoActivationCharacters();
	}

	public int getAutoActivationDelay() {
		return adapter.getAutoActivationDelay();
	}

	public IContentProposalProvider getContentProposalProvider() {
		return adapter.getContentProposalProvider();
	}

	public Control getControl() {
		return adapter.getControl();
	}

	public IControlContentAdapter getControlContentAdapter() {
		return adapter.getControlContentAdapter();
	}

	public int getFilterStyle() {
		return adapter.getFilterStyle();
	}

	public ILabelProvider getLabelProvider() {
		return adapter.getLabelProvider();
	}

	public Point getPopupSize() {
		return adapter.getPopupSize();
	}

	public boolean getPropagateKeys() {
		return adapter.getPropagateKeys();
	}

	public int getProposalAcceptanceStyle() {
		return adapter.getProposalAcceptanceStyle();
	}

	@Override
	public int hashCode() {
		return adapter.hashCode();
	}

	public boolean hasProposalPopupFocus() {
		return adapter.hasProposalPopupFocus();
	}

	public boolean isEnabled() {
		return adapter.isEnabled();
	}

	public void removeContentProposalListener(IContentProposalListener listener) {
		adapter.removeContentProposalListener(listener);
	}

	public void removeContentProposalListener(IContentProposalListener2 listener) {
		adapter.removeContentProposalListener(listener);
	}

	public void setAutoActivationCharacters(char[] autoActivationCharacters) {
		adapter.setAutoActivationCharacters(autoActivationCharacters);
	}

	public void setAutoActivationDelay(int delay) {
		adapter.setAutoActivationDelay(delay);
	}

	public void setContentProposalProvider(
			IContentProposalProvider proposalProvider) {
		adapter.setContentProposalProvider(proposalProvider);
	}

	public void setEnabled(boolean enabled) {
		adapter.setEnabled(enabled);
	}

	public void setFilterStyle(int filterStyle) {
		adapter.setFilterStyle(filterStyle);
	}

	public void setLabelProvider(ILabelProvider labelProvider) {
		adapter.setLabelProvider(labelProvider);
	}

	public void setPopupSize(Point size) {
		adapter.setPopupSize(size);
	}

	public void setPropagateKeys(boolean propagateKeys) {
		adapter.setPropagateKeys(propagateKeys);
	}

	public void setProposalAcceptanceStyle(int acceptance) {
		adapter.setProposalAcceptanceStyle(acceptance);
	}

	@Override
	public String toString() {
		return adapter.toString();
	}
}
