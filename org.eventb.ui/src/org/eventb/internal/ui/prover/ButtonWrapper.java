/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Wraps a button to add cursor modifier on it.
 *
 * @author Thomas Muller
 */
public class ButtonWrapper implements DisposeListener {
	
	protected final Button button;
	protected final CursorModifier cursorModifier;
	
	public ButtonWrapper(Composite parent) {
		this.button = new Button(parent, SWT.CHECK);
		this.cursorModifier = new CursorModifier(button);
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		button.removeMouseTrackListener(cursorModifier);
		button.removeDisposeListener(this);
	}

	/**
	 * Returns the wrapped <code>button</code>.
	 */
	public Control getButton() {
		return button;
	}

	/**
	 * Adds a mouse track listener to allow cursor modification when
	 * appropriate, and registers this wrapper as a dispose listener to remove
	 * it.
	 */
	public void configure() {
		button.addMouseTrackListener(cursorModifier);
		button.addDisposeListener(this);
	}

}
