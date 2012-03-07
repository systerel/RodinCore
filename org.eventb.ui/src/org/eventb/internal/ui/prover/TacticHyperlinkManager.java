/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - fixed menu bug
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.prover.CursorModifier.HAND_CURSOR;
import static org.eventb.internal.ui.prover.ProverUIUtils.SOFT_BG_COLOR;
import static org.eventb.internal.ui.prover.ProverUIUtils.getHyperlinkLabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.prover.MouseHyperlinkListener.MouseDownListener;
import org.eventb.internal.ui.prover.MouseHyperlinkListener.MouseEnterListener;
import org.eventb.internal.ui.prover.MouseHyperlinkListener.MouseExitListener;
import org.eventb.internal.ui.prover.MouseHyperlinkListener.MouseHoverListener;
import org.eventb.internal.ui.prover.MouseHyperlinkListener.MouseMoveListener;
import org.eventb.internal.ui.prover.registry.PositionApplicationProxy;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

public class TacticHyperlinkManager {

	final static Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);

	private final StyledText text;
	private final StringBuilder toAppend;
	private int stringBuilderOffset;

	private final Map<Point, List<PositionApplicationProxy>> links = new HashMap<Point, List<PositionApplicationProxy>>();
	private final Map<Point, PredicateRow> hypAppli = new HashMap<Point, PredicateRow>();
	private final Set<PaintObjectListener> paintListeners = new HashSet<PaintObjectListener>();
	private final Set<BackgroundPainter> backgroundPainters = new HashSet<BackgroundPainter>();

	private Menu tipMenu;
	private Point currentLink;
	
	private MouseDownListener mouseDownListener = new MouseDownListener(this);
	private MouseMoveListener mouseMoveListener = new MouseMoveListener(this);
	private MouseHoverListener mouseHoverListener = new MouseHoverListener(this);
	private MouseEnterListener mouseEnterListener = new MouseEnterListener(this);	
	private MouseExitListener mouseExitListener = new MouseExitListener(this);

	private final Cursor defaultCursor;
	
	public TacticHyperlinkManager(StyledText text) {
		this.text = text;
		this.defaultCursor = text.getCursor();
		currentLink = null;
		this.toAppend = new StringBuilder();
		this.stringBuilderOffset = 0;
	}
	
	public StyledText getText() {
		return text;
	}
	
	public void putAssociation(Set<Point> points, PredicateRow row) {
		for (Point pt : points) {
			hypAppli.put(pt, row);			
		}
	}
	
	public void setHyperlinks(Map<Point, List<PositionApplicationProxy>> toAdd,
			PredicateRow hypRow) {
		links.putAll(toAdd);
	}

	public void dispose() {
		disableListeners();
		disposeMenu();
		if (text != null && !text.isDisposed()) {
			for (PaintObjectListener p : paintListeners) {
				text.removePaintObjectListener(p);
			}
		}
		links.clear();
		hypAppli.clear();
		paintListeners.clear();
		backgroundPainters.clear();
	}

	public void enableCurrentLink() {
		if (currentLink == null)
			return;
		final StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = RED;
		style.underline = true;
		text.setStyleRange(style);
	}

	public List<StyleRange> getHyperlinkStyles() {
		final List<StyleRange> ranges = new ArrayList<StyleRange>();
		for (Point point : links.keySet()) {
			final StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.foreground = RED;
			ranges.add(style);
		}
		return ranges;
	}
	
	public void addPaintObjectListener(PaintObjectListener listener) {
		paintListeners.add(listener);
	}

	public void activateHyperlink(Point link, Point widgetPosition) {
		final List<PositionApplicationProxy> tacticPositions = links.get(link);
		if (tacticPositions.size() == 1) {
			// Apply the only rule.
			final ITacticApplication tacticUIInfo = tacticPositions.get(0);
			applyTactic(link, tacticUIInfo);
		} else {
			showToolTip(tacticPositions, widgetPosition, link);
		}
	}

	protected void applyTactic(Point link, ITacticApplication tacticPosition) {
		final boolean skipPostTactic = TacticUIRegistry.getDefault()
				.isSkipPostTactic(tacticPosition.getTacticID());

		final PredicateRow hypothesisRow = hypAppli.get(link);
		if (hypothesisRow != null)
			hypothesisRow.apply(tacticPosition, skipPostTactic);
	}

	public Point getLink(Point location) {
		int offset = getCharacterOffset(location);
		if (offset == -1)
			return null;
		for (Point index : links.keySet()) {
			if (index.x <= offset && offset < index.y)
				return index;
		}
		return null;
	}

	void showToolTip(List<PositionApplicationProxy> tacticPositions, Point widgetPosition, final Point link) {

		if (tipMenu != null && !tipMenu.isDisposed()) {
			tipMenu.dispose();
		}

		tipMenu = new Menu(text.getShell(), SWT.POP_UP);

		for (final ITacticApplication tacticPosition : tacticPositions) {
			if (tacticPosition instanceof IPositionApplication) {
				final MenuItem item = new MenuItem(tipMenu, SWT.PUSH);

				final String linkLabel = getHyperlinkLabel((IPositionApplication) tacticPosition);
				
				item.setText(linkLabel);
				item.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(SelectionEvent se) {
						widgetSelected(se);
					}

					@Override
					public void widgetSelected(SelectionEvent se) {
						applyTactic(link, tacticPosition);
						enableListeners(true);
					}

				});
			}
		}

		final Point tipPosition = text.toDisplay(widgetPosition);
		setMenuLocation(tipMenu, tipPosition);
		disableCurrentLink();
		tipMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuHidden(MenuEvent e) {
				enableListeners(true);
			}

			@Override
			public void menuShown(MenuEvent e) {
				enableListeners(false);
			}
			
		});
		tipMenu.setVisible(true);
	}

	public void showToolTip(Point widgetPosition) {
		if (currentLink == null)
			return;
		final List<PositionApplicationProxy> tacticApplis = links.get(currentLink);
		if (tacticApplis == null)
			return;
		showToolTip(tacticApplis, widgetPosition, currentLink);
	}

	private void disableListeners() {
		if (text != null && !text.isDisposed()) {
			text.removeListener(SWT.MouseDown, mouseDownListener);
			text.removeListener(SWT.MouseMove, mouseMoveListener);
			text.removeListener(SWT.MouseHover, mouseHoverListener);
			text.removeListener(SWT.MouseExit, mouseExitListener);
			text.removeListener(SWT.MouseEnter, mouseEnterListener);
		}
	}
	
	private void enableListeners() {
		if (!text.isDisposed()) {
			text.addListener(SWT.MouseDown, mouseDownListener);
			text.addListener(SWT.MouseMove, mouseMoveListener);
			text.addListener(SWT.MouseHover, mouseHoverListener);
			text.addListener(SWT.MouseExit, mouseExitListener);
			text.addListener(SWT.MouseEnter, mouseEnterListener);
		}
	}
	
	public void enableListeners(boolean enabled) {
		if (enabled) {
			enableListeners();
		} else {
			disableListeners();
		}
	}
	
	// Display the shell 16 pixels below the place where the user clicked
	// so that the hyperlink source is not hidden by the menu
	void setMenuLocation(Menu menu, Point position) {
		Point belowCursor = new Point(position.x + 5, position.y + 10);
		menu.setLocation(belowCursor);
	}

	public void disposeMenu() {
		if (tipMenu != null && !tipMenu.isDisposed()) {
			tipMenu.dispose();
		}
	}
	public void hideMenu() {
		if (tipMenu != null && !tipMenu.isDisposed() && tipMenu.isVisible()) {
			tipMenu.setVisible(false);
		}
	}

	public void disableCurrentLink() {
		if (currentLink == null)
			return;
		final StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = RED;
		style.underline = false;
		text.setStyleRange(style);
		text.setCursor(defaultCursor);
		currentLink = null;
	}

	public void setCurrentLink(Point link) {
		disableCurrentLink();
		currentLink = link;
		enableCurrentLink();
	}

	public Point getCurrentLink() {
		return currentLink;
	}

	public void setMousePosition(Point location) {
		try {
			final Point index = getLink(location);
			if (index != null) {
				if (!index.equals(currentLink)) {
					setCurrentLink(index);
					text.setCursor(HAND_CURSOR);
				}
			} else {
				if (currentLink != null) {
					disableCurrentLink();
				}
			}
		} catch (IllegalArgumentException exception) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Invalid mouse position");
		}
	}

	int getCharacterOffset(Point pt) {
		if (text.isDisposed())
			return -1;
		int offset;
		try {
			offset = text.getOffsetAtLocation(pt);
		}
		catch (IllegalArgumentException e) {
			return -1;
		}
		final Point location = text.getLocationAtOffset(offset);
		// From the caret offset to the character offset.
		if (pt.x < location.x)
			offset = offset - 1;
		return offset;
	}

	public void mouseDown(Point location) {
		disposeMenu();
		final Point link = getLink(location);
		if (link != null) {
			setCurrentLink(link);
			activateHyperlink(link, location);
		}
	}

	public void appendText(String str) {
		stringBuilderOffset += str.length();
		toAppend.append(str);
	}

	public void setContents() {	
		text.setText(toAppend.toString());
		final List<StyleRange> hyperlinkStyles = getHyperlinkStyles();
		Collections.sort(hyperlinkStyles, new Comparator<StyleRange>(){
			@Override
			public int compare(StyleRange o1, StyleRange o2) {
				return o1.start - o2.start;
			}
		});
		final StyleRange[] ranges = hyperlinkStyles
				.toArray(new StyleRange[hyperlinkStyles.size()]);
		text.setStyleRanges(ranges);
		for (PaintObjectListener l : paintListeners){
			text.addPaintObjectListener(l);
		}
	}

	public int getCurrentOffset() {
		return stringBuilderOffset;
	}

	public void addBackgroundPainter(boolean odd, final int startOffset, final int endOffset) {
		if (odd) {
			backgroundPainters.add(new BackgroundPainter(text, startOffset, endOffset, SOFT_BG_COLOR));
		}
	}
	
	public void activateBackgroundColoration() {
		for (BackgroundPainter p : backgroundPainters){
			p.draw();
		}
	}
	
	private static class BackgroundPainter {
		
		private int start;
		private int end;
		private StyledText text;
		private Color color;

		public BackgroundPainter(StyledText text, int start, int end, Color color) {
			this.start = start;
			this.end = end;
			this.text = text;
			this.color = color;
		}
		
		public void draw() {
			final int startLine = text.getLineAtOffset(start);
			final int endLine = text.getLineAtOffset(end);
			final int length = endLine - startLine;
			text.setLineBackground(startLine, length, color);
		}
	}
	
}
