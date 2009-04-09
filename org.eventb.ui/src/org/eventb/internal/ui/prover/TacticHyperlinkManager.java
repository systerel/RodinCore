/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - fixed menu bug
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.ast.IPosition;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.TacticPositionUI;

public abstract class TacticHyperlinkManager {

	final Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);

	final Cursor arrowCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);

	final Cursor handCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);

	StyledText text;

	Collection<TacticPositionUI> links;

	Menu tipMenu;

	Point currentLink;

	public TacticHyperlinkManager(StyledText text) {
		this.text = text;
		links = new ArrayList<TacticPositionUI>();
		currentLink = null;
	}

	public void setHyperlinks(Collection<TacticPositionUI> links) {
		this.links = links;
	}

	public void updateHyperlinks(int index, int offset) {
		for (TacticPositionUI link : links) {
			// IMPORTANT: Do NOT add/remove the map while iterating through it
			// IMPORTANT: Modified the key is NOT allowed either
			Point point = link.getPoint();
			if (point.x > index) {
				link.setPoint(new Point(point.x + offset, point.y + offset));
			}
		}

		if (currentLink != null && currentLink.x > index) {
			setCurrentLink(new Point(currentLink.x + offset, currentLink.y
					+ offset));
		}
	}

	public void enableCurrentLink() {
		if (currentLink == null)
			return;
		
		StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = RED;
		style.underline = true;
		text.setStyleRange(style);
	}

	public void setHyperlinkStyle() {
		for (TacticPositionUI link : links) {
			Point point = link.getPoint();
			StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.foreground = RED;
			text.setStyleRange(style);
		}
	}

	public void activateHyperlink(Point link, Point widgetPosition) {
		TacticPositionUI tacticPositionUI = getTacticPositionUI(link);
		List<Pair<String, IPosition>> tacticPositions = tacticPositionUI
				.getTacticPositions();
		if (tacticPositions.size() == 1) {
			// Apply the only rule.
			Pair<String, IPosition> tacticPosition = tacticPositions
					.get(0);
			applyTactic(tacticPosition.getFirst(), tacticPosition
					.getSecond());
		} else {
			showToolTip(tacticPositionUI, widgetPosition);
		}
	}

	private TacticPositionUI getTacticPositionUI(Point link) {
		for (TacticPositionUI tacticPosition : links) {
			if (link.equals(tacticPosition.getPoint())) {
				return tacticPosition;
			}
		}
		return null;
	}

	protected abstract void applyTactic(String tacticID, IPosition position);

	public Point getLink(Point location) {
		int offset = getCharacterOffset(location);
		if (offset == -1)
			return null;
		for (TacticPositionUI link : links) {
			Point index = link.getPoint();
			if (index.x <= offset && offset < index.y)
				return index;
		}
		return null;
	}

	void showToolTip(TacticPositionUI tacticPositionUI, Point widgetPosition) {
		List<Pair<String, IPosition>> tacticPositions = tacticPositionUI
				.getTacticPositions();

		if (tipMenu != null && !tipMenu.isDisposed())
			tipMenu.dispose();

		tipMenu = new Menu(text.getShell(), SWT.POP_UP);

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		for (Pair<String, IPosition> tacticPosition : tacticPositions) {
			final String tacticID = tacticPosition.getFirst();
			final IPosition position = tacticPosition.getSecond();

			MenuItem item = new MenuItem(tipMenu, SWT.PUSH);
			item.setText(tacticUIRegistry.getTip(tacticID));
			item.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

				public void widgetSelected(SelectionEvent se) {
					applyTactic(tacticID, position);
					enableListeners();
				}

			});
		}

		Point tipPosition = text.toDisplay(widgetPosition);
		setMenuLocation(tipMenu, tipPosition);
		disableCurrentLink();
		text.setCursor(arrowCursor);
		tipMenu.addMenuListener(new MenuListener() {

			public void menuHidden(MenuEvent e) {
				enableListeners();
			}

			public void menuShown(MenuEvent e) {
				disableListeners();
			}
			
		});
		tipMenu.setVisible(true);
	}

	public void showToolTip(Point widgetPosition) {
		if (currentLink == null)
			return;
		TacticPositionUI tacticPositionUI = getTacticPositionUI(currentLink);
		showToolTip(tacticPositionUI, widgetPosition);
	}

	protected abstract void disableListeners();
	
	protected abstract void enableListeners();

	// Display the shell 16 pixels below the place where the user clicked
	// so that the hyperlink source is not hidden by the menu
	void setMenuLocation(Menu menu, Point position) {
		Point belowCursor = new Point(position.x, position.y + 16);
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
		StyleRange style = new StyleRange();
		style.start = currentLink.x;
		style.length = currentLink.y - currentLink.x;
		style.foreground = RED;
		style.underline = false;
		text.setStyleRange(style);
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
			Point index = getLink(location);
			if (index != null) {
				if (!index.equals(currentLink)) {
					setCurrentLink(index);
					text.setCursor(handCursor);
				}
			} else {
				if (currentLink != null) {
					text.setCursor(arrowCursor);
					setCurrentLink(null);
				}
			}
		} catch (IllegalArgumentException exception) {
			// if (ProverUIUtils.DEBUG)
			// ProverUIUtils.debug("Invalid");
		}
		return;
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
		Point location = text.getLocationAtOffset(offset);

		// From the caret offset to the character offset.
		if (pt.x < location.x)
			offset = offset - 1;
		return offset;
	}

	public void mouseDown(Point location) {
		disposeMenu();
		Point link = getLink(location);

		if (link != null) {
			setCurrentLink(link);
			activateHyperlink(link, location);
		}
		return;
	}

}
