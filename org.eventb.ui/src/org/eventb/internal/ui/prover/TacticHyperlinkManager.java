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
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.prover.ProverUIUtils.getHyperlinkLabel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eventb.internal.provisional.ui.prover.IPositionApplication;
import org.eventb.internal.provisional.ui.prover.ITacticApplication;
import org.eventb.internal.ui.EventBSharedColor;

public abstract class TacticHyperlinkManager {

	final Color RED = EventBSharedColor.getSystemColor(SWT.COLOR_RED);

	final Cursor arrowCursor = new Cursor(Display.getDefault(), SWT.CURSOR_ARROW);

	final Cursor handCursor = new Cursor(Display.getDefault(), SWT.CURSOR_HAND);

	StyledText text;

	Map<Point, List<ITacticApplication>> links;

	Menu tipMenu;

	Point currentLink;

	public TacticHyperlinkManager(StyledText text) {
		this.text = text;
		links = new HashMap<Point, List<ITacticApplication>>();
		currentLink = null;
	}

	public void setHyperlinks(Map<Point, List<ITacticApplication>> links) {
		this.links = links;
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
		for (Point point : links.keySet()) {
			StyleRange style = new StyleRange();
			style.start = point.x;
			style.length = point.y - point.x;
			style.foreground = RED;
			text.setStyleRange(style);
		}
	}

	public void activateHyperlink(Point link, Point widgetPosition) {
		List<ITacticApplication> tacticPositions = links.get(link);
		if (tacticPositions.size() == 1) {
			// Apply the only rule.
			ITacticApplication tacticUIInfo = tacticPositions.get(0);
			applyTactic(tacticUIInfo);
		} else {
			showToolTip(tacticPositions, widgetPosition);
		}
	}

	protected abstract void applyTactic(ITacticApplication tacticPosition);

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

	void showToolTip(List<ITacticApplication> tacticPositions, Point widgetPosition) {

		if (tipMenu != null && !tipMenu.isDisposed())
			tipMenu.dispose();

		tipMenu = new Menu(text.getShell(), SWT.POP_UP);

		for (final ITacticApplication tacticPosition : tacticPositions) {
			if (tacticPosition instanceof IPositionApplication) {
				final MenuItem item = new MenuItem(tipMenu, SWT.PUSH);

				final String linkLabel = getHyperlinkLabel((IPositionApplication) tacticPosition);
				
				item.setText(linkLabel);
				item.addSelectionListener(new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent se) {
						widgetSelected(se);
					}

					public void widgetSelected(SelectionEvent se) {
						applyTactic(tacticPosition);
						enableListeners();
					}

				});
			}
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
		List<ITacticApplication> tacticPositionUI = links.get(currentLink);
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
	}

}
