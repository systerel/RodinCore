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
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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

	private SelectionListener listener;
	private Color bgColor;
	private int lastPaintTime;
	protected Control control;


	public ControlHolder(PredicateRow row, ControlMaker maker, int offset,
			boolean drawBoxAround) {
		this.row = row;
		this.maker = maker;
		this.offset = offset;
		this.drawBoxAround = drawBoxAround;
		this.text = (StyledText) maker.getParent();
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

	public void addSelectionListener(SelectionListener alistener) {
		listener = alistener;
	}

	public void remove() {
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

	public void setControl(Control c) {
		this.control = c;
		setBackgroundColor();
		if (listener != null && c instanceof Button) {
			((Button) c).addSelectionListener(listener);
		}
	}

	protected void paintAndPlace(PaintObjectEvent event) {
		lastPaintTime = event.time;
		final Rectangle cBounds;
		if (control == null) {
			cBounds = maker.getBounds(this);
		} else {
			cBounds = control.getBounds();
		}
		final int x = event.x + MARGIN;
		final int lineHeight = event.ascent + event.descent;
		final int y = event.y + MARGIN + (lineHeight - cBounds.height) / 2;
		if (control == null) {
			setControl(maker.getControl(this));
		}
		control.setVisible(true);
		control.setEnabled(row.isEnabled());
		control.setBounds(x, y, cBounds.width, cBounds.height);
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

	public int getLastPaintTime() {
		return lastPaintTime;
	}

}
