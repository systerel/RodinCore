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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.EventBSharedColor;

/**
 * Class used to register controls in a StyledText.
 *  
 * @author "Thomas Muller"
 */
public class ControlHolder<U extends Control> {
	
	private static final int MARGIN = 1; //px
	private static final int LENGTH = 1; //offset
	
	private final StyledText text;
	private final ControlPainter<U> painter;
	
	protected final int offset;
	protected final U control;
	protected final boolean drawBoxAround;
	
	public ControlHolder(U control, int offset, boolean drawBoxAround) {
		this.text = (StyledText) control.getParent();
		this.control = control;
		this.offset = offset;
		this.drawBoxAround = drawBoxAround;
		this.painter = new ControlPainter<U>(this);
	}
	
	public void attach() {
		addControl();
		registerPainter();
	}
	
	public void remove() {
		if (!text.isDisposed() && painter != null)
			text.removePaintObjectListener(painter);
		if (!control.isDisposed())
			control.dispose();
	}
	
	public U getControl() {
		return control;
	}
	
	private void addControl() {
		final StyleRange style = new StyleRange();
		style.start = offset;
		style.length = LENGTH;
		control.pack();
		final Rectangle rect = control.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		final Point loc = text.getLocationAtOffset(offset);
		loc.x += MARGIN;
		loc.y += MARGIN;
		control.setLocation(loc);
		text.setStyleRange(style);
	}

	private void registerPainter() {
		text.addPaintObjectListener(painter);
	}
	
	private static class ControlPainter<U extends Control> implements
			PaintObjectListener {

		private static final Color RED = EventBSharedColor
				.getSystemColor(SWT.COLOR_RED);
		private final ControlHolder<U> holder;
		private boolean painting = false;

		public ControlPainter(ControlHolder<U> holder) {
			this.holder = holder;
		}

		@Override
		public void paintObject(PaintObjectEvent event) {
			if (painting)
				return;
			try {
				painting = true;
				final StyleRange style = event.style;
				int start = style.start;
				if (start != holder.offset) {
					return;
				}
				paintAndPlace(event.x, event.y, event.ascent, event.descent);
				drawBoxAround(event.gc, holder.drawBoxAround);
			} finally {
				painting = false;
			}
		}
		
		private void paintAndPlace(int ex, int ey, int ascent, int descent) {
			final Rectangle controlBounds = holder.control.getBounds();
			final int x = ex + MARGIN;
			final int lineHeight = ascent + descent;
			final int y = ey + MARGIN + (lineHeight - controlBounds.height) / 2;
			holder.control.setLocation(x, y);
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
		final Rectangle controlBounds = control.getBounds();
		final int x = ex + MARGIN;
		final int lineHeight = ascent + descent;
		final int y = ey + MARGIN + (lineHeight - controlBounds.height) / 2;
		control.setLocation(x, y);
	}
	
}
