/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class used to register controls in a StyledText.
 *  
 * @author "Thomas Muller"
 */
public class ControlHolder {
	
	protected static final Color RED = EventBSharedColor
			.getSystemColor(SWT.COLOR_RED);
	
	private static final int MARGIN = 1; //px
	private static final int LENGTH = 1; //offset
	
	private final StyledText text;
	private final ControlPainter painter;
	
	protected final int offset;
	protected final boolean drawBoxAround;
	
	private SelectionListener listener;
	private Color bgColor;

	protected ControlMaker maker;
	protected Control control;
	
	public ControlHolder(ControlMaker maker, int offset, boolean drawBoxAround) {
		this.maker = maker;
		this.offset = offset;
		this.drawBoxAround = drawBoxAround;
		this.text = (StyledText) maker.getParent();
		this.painter = new ControlPainter(this);
	}
	
	public ControlHolder(ControlMaker maker, int offset, boolean drawBoxAround,
			Color bgColor) {
		this(maker, offset, drawBoxAround);
		this.bgColor = bgColor;
	}

	public ControlHolder(Control control, int offset, boolean drawBoxAround) {
		this.control = control;
		this.offset = offset;
		this.drawBoxAround = drawBoxAround;
		this.text = (StyledText) control.getParent();
		this.painter = new ControlPainter(this);
	}

	public ControlHolder(Control control, int offset, boolean drawBoxAround,
			Color bgColor) {
		this(control, offset, drawBoxAround);
		this.bgColor = bgColor;
		control.setBackground(bgColor);
	}
		
	public void attach(boolean lazy) {
		setStyleRange(lazy);
		registerPainter();
	}
	
	public void addSelectionListener(SelectionListener l) {
			listener = l;
	}
	
	public void remove() {
		if (!text.isDisposed() && painter != null)
			text.removePaintObjectListener(painter);
		if (control != null && !control.isDisposed()) {
			if (listener != null && control instanceof Button) {
				((Button) control).removeSelectionListener(listener);
			}
			control.dispose();
		}
	}
	
	protected void setStyleRange(boolean lazy) {
		final StyleRange style = new StyleRange();
		style.start = offset;
		style.length = LENGTH;
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

	private void registerPainter() {
		text.addPaintObjectListener(painter);
	}
	
	public void setControl(Control c) {
		this.control = c;
		setBackgroundColor();
		if (listener != null && c instanceof Button) {
			((Button)c).addSelectionListener(listener);
		}
	}
	
	private static class ControlPainter implements
			PaintObjectListener {

		private final ControlHolder holder;
		private ControlMaker maker;
		

		public ControlPainter(ControlHolder holder) {
			this.holder = holder;
			this.maker = holder.maker;
		}

		@Override
		public void paintObject(PaintObjectEvent event) {
			if (maker != null && maker.isPainting())
				return;
			try {
				if (maker != null){
					maker.setPainting(true);
				}
				final StyleRange style = event.style;
				int start = style.start;
				if (start != holder.offset) {
					return;
				}
				holder.paintAndPlace(event.x, event.y, event.ascent, event.descent);
				drawBoxAround(event.gc, holder.drawBoxAround);
			} finally {
				if (maker != null) {
					maker.setPainting(false);
				}
			}
		}

		private void drawBoxAround(GC gc, final boolean drawBoxAround) {
			if (!drawBoxAround)
				return;
			final Rectangle bounds = holder.control.getBounds();
			final Color savedBgColor = gc.getForeground();
			gc.setForeground(RED);
			gc.drawRectangle(bounds.x - 1, bounds.y - 1, bounds.width + 1,
					bounds.height + 1);
			gc.setForeground(savedBgColor);
		}

	}
	
	protected void paintAndPlace(int ex, int ey, int ascent, int descent) {
		final Rectangle controlBounds;
		if (control == null) {
			controlBounds = maker.getBounds(this);
		} else {
			controlBounds = control.getBounds();
		}
		final int x = ex + MARGIN;
		final int lineHeight = ascent + descent;
		final int y = ey + MARGIN + (lineHeight - controlBounds.height) / 2;
		if (control == null) {
			setControl(maker.getControl());
		}
		control.setVisible(true);
		control.setLocation(x, y);
	}
	
	public void render() {
		if (control == null) {
			control = maker.getControl();
		}
	}
	
	public void setBackgroundColor() {
		if (bgColor != null)
			control.setBackground(bgColor);
	}

	public Control getControl() {
		return control;
	}
	
}
