package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.ast.IPosition;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.TacticPositionUI;

public abstract class TacticHyperlinkManager {

	final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);

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

		tipMenu = new Menu(text);

		for (Pair<String, IPosition> tacticPosition : tacticPositions) {
			final String tacticID = tacticPosition.getFirst();
			final IPosition position = tacticPosition.getSecond();

			MenuItem item = new MenuItem(tipMenu, SWT.DEFAULT);
			TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
			item.setText(tacticUIRegistry.getTip(tacticID));
			item.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

				public void widgetSelected(SelectionEvent se) {
					applyTactic(tacticID, position);
				}

			});
		}

		Point tipPosition = text.toDisplay(widgetPosition);
		setMenuLocation(tipMenu, tipPosition);
		disableCurrentLink();
		text.setCursor(arrowCursor);
		tipMenu.setVisible(true);
	}

	public void showToolTip(Point widgetPosition) {
		if (currentLink == null)
			return;
		TacticPositionUI tacticPositionUI = getTacticPositionUI(currentLink);
		showToolTip(tacticPositionUI, widgetPosition);
	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param shell
	 *            the object that is to hover
	 * @param position
	 *            the position of a widget to hover over
	 */
	void setHoverLocation(Shell shell, Point position) {
		Rectangle displayBounds = shell.getDisplay().getBounds();
		Rectangle shellBounds = shell.getBounds();
		shellBounds.x = Math.max(Math.min(position.x, displayBounds.width
				- shellBounds.width), 0);
		shellBounds.y = Math.max(Math.min(position.y + 16, displayBounds.height
				- shellBounds.height), 0);
		shell.setBounds(shellBounds);
	}

	/**
	 * Sets the location for a hovering shell
	 * 
	 * @param menu
	 *            the menu
	 * @param position
	 *            the position of a widget to hover over
	 */
	void setMenuLocation(Menu menu, Point position) {
		Rectangle displayBounds = menu.getDisplay().getBounds();

		int x = Math.max(Math.min(position.x, displayBounds.width), 0);
		int y = Math.max(Math.min(position.y + 16, displayBounds.height), 0);
		menu.setLocation(new Point(x, y));
	}

	public void disposeMenu() {
		if (tipMenu != null && !tipMenu.isDisposed())
			tipMenu.dispose();
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
			// if (ProverUIUtils.DEBUG)
			// ProverUIUtils.debug("Move Offset " + offset);
		} catch (IllegalArgumentException exception) {
			// if (ProverUIUtils.DEBUG)
			// ProverUIUtils.debug("Invalid");
		}
		return;
	}

	int getCharacterOffset(Point pt) {
		int offset = text.getOffsetAtLocation(pt);
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
