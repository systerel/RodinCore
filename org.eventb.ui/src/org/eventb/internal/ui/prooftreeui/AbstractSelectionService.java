/*******************************************************************************
 * Copyright (c) 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.core.runtime.ListenerList;

/**
 * Common abstraction for proof-related selection services.
 * 
 * @author beauger
 *
 * @param <T>
 *            the type of the selected object
 * @param <L>
 *            the type of the listeners
 */
public abstract class AbstractSelectionService<T, L> {

	private final ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

	private T current;

	/**
	 * Starts this selection service by listening to the desired providers.
	 */
	protected abstract void startListening();

	/**
	 * Stops this selection service by removing the listeners added in
	 * {@link #startListening()}.
	 */
	protected abstract void stopListening();

	/**
	 * Notifies the given listener that the current selection has changed to the
	 * given value.
	 * 
	 * @param listener
	 *            the listener to notify
	 * @param newValue
	 *            the new selected value
	 */
	protected abstract void notifyChange(L listener, T newValue);

	@SuppressWarnings("unchecked")
	private final void fireChange() {
		// following code recommendation from ListenerList
		final Object[] listenerArray = listeners.getListeners();
		for (int i = 0; i < listenerArray.length; i++) {
			notifyChange((L) listenerArray[i], current);
		}
	}

	/**
	 * Must be called whenever the current value changes.
	 * 
	 * @param newValue
	 *            the new value
	 */
	protected void currentChanged(T newValue) {
		if (newValue != current) {
			current = newValue;
			fireChange();
		}
	}

	/**
	 * Returns the currently selected value.
	 * 
	 * @return the current value, may be <code>null</code>
	 */
	public T getCurrent() {
		return current;
	}
	
	/**
	 * Registers the given listener so that it receives notifications when the
	 * current selection changes. This method has no effect if the same listener
	 * is already registered.
	 * 
	 * @param listener
	 *            a proof rule listener
	 */
	public synchronized void addListener(L listener) {
		if (listeners.isEmpty()) {
			startListening();
		}
		listeners.add(listener);
	}

	/**
	 * Removes the given listener. Has no effect if the same listener was not
	 * already registered.
	 * 
	 * @param listener
	 */
	public synchronized void removeListener(L listener) {
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			stopListening();
		}
	}

}