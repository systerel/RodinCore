/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class used to register controls in a StyledText. This class is able to paint
 * and place the control that it holds in the parent styled text.
 * 
 * @author "Thomas Muller"
 */
public class ControlHolder {

	protected static final Color RED = EventBSharedColor
			.getSystemColor(SWT.COLOR_RED);

	private static final int MARGIN = 2; // px
	private final StyledText text;

	protected final int offset;
	protected final ControlMaker maker;
	protected final boolean drawBoxAround;
	private final PredicateRow row;

	private Set<EventListener> listeners;
	private Color bgColor;
	protected Control control;


	public ControlHolder(PredicateRow row, ControlMaker maker, int offset,
			boolean drawBoxAround) {
		this.row = row;
		this.maker = maker;
		this.offset = offset;
		this.drawBoxAround = drawBoxAround;
		this.text = (StyledText) maker.getParent();
		this.listeners = new HashSet<EventListener>();
	}

	public ControlHolder(PredicateRow row, ControlMaker maker, int offset,
			boolean drawBoxAround, Color bgColor) {
		this(row, maker, offset, drawBoxAround);
		this.bgColor = bgColor;
	}

	public void attach(boolean lazy) {
		setStyleRange(lazy);
		row.getControlPainter().registerControlHolder(this);
	}

	protected void setStyleRange(boolean lazy) {
		final StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		final Rectangle rect;
		if (lazy) {
			rect = maker.getBounds(this);
		} else {
			control.pack();
			rect = control.getBounds();
		}
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		text.setStyleRange(style);
	}

	protected void paintAndPlace(PaintObjectEvent event) {
		final Rectangle cBounds;
		if (control == null) {
			cBounds = maker.getBounds(this);
		} else {
			cBounds = control.getBounds();
		}
		final int x = event.x + MARGIN;
		final int lineHeight = event.ascent + event.descent;
		final int y;
		if (isWindows())
			y = event.y + MARGIN;
		else
			y = event.y + MARGIN + (lineHeight - cBounds.height) / 2;
		if (control == null) {
			setControl(maker.getControl(this));
		}
		control.setVisible(true);
		control.setEnabled(row.isEnabled());
		control.setBounds(x, y, cBounds.width, cBounds.height);
	}

	private static boolean isWindows(){
		final String os = System.getProperty("os.name");
		return os.toLowerCase().contains("win");
	}

	public void setControl(Control c) {
		this.control = c;
		setBackgroundColor();
		for (EventListener lr : listeners){
			addListenerToControl(c, lr);
		}
	}

	public void addListener(EventListener alistener) {
		listeners.add(alistener);
	}

	public void remove() {
		if (control != null && !control.isDisposed()) {
			for(EventListener lr : listeners){
				removeListenersFromControl(control, lr);
			}
			control.dispose();
		}
	}

	private static void addListenerToControl(Control c, EventListener listener) {
		if (c instanceof Button && listener instanceof SelectionListener) {
			((Button) c).addSelectionListener((SelectionListener) listener);
		}
		if (listener instanceof MouseListener) {
			c.addMouseListener((MouseListener) listener);
		}
		if (listener instanceof KeyListener) {
			c.addKeyListener((KeyListener) listener);
		}
		if (listener instanceof FocusListener) {
			c.addFocusListener((FocusListener) listener);
		}
	}

	private static void removeListenersFromControl(Control c, EventListener listener) {
		if (c instanceof Button && listener instanceof SelectionListener) {
			((Button) c).removeSelectionListener((SelectionListener) listener);
		}
		if (listener instanceof MouseListener) {
			c.removeMouseListener((MouseListener) listener);
		}
		if (listener instanceof KeyListener) {
			c.removeKeyListener((KeyListener) listener);
		}
		if (listener instanceof FocusListener) {
			c.removeFocusListener((FocusListener) listener);
		}
	}

	public void render() {
		if (control == null) {
			control = maker.getControl(this);
		}
	}

	public void setBackgroundColor() {
		if (bgColor != null)
			control.setBackground(bgColor);
	}

	public Control getControl() {
		return control;
	}

	public PredicateRow getRow() {
		return row;
	}

	public StyledText getText() {
		return text;
	}

}
