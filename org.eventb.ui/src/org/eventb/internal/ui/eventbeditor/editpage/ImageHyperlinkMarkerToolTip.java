package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eventb.internal.ui.markers.MarkerUIRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinElement;

public class ImageHyperlinkMarkerToolTip {
	Shell parentShell;

	Menu tipMenu;

	Widget tipWidget; // widget this tooltip is hovering over

	protected Point tipPosition; // the position being hovered over on the

	protected Point widgetPosition; // the position hovered over in the Widget;

	IEventBEditor<?> editor;
	
	/**
	 * Creates a new tooltip handler
	 * 
	 * @param parent
	 *            the parent Shell
	 */
	public ImageHyperlinkMarkerToolTip(IEventBEditor<?> editor, Shell parent) {
		this.editor = editor;
		this.parentShell = parent;
	}

	protected void createMenu(IRodinElement element) {
		IMarker[] markers;
		try {
			markers = MarkerUIRegistry.getDefault().getMarkers(element);
		} catch (CoreException e) {
			return;
		}
		if (tipMenu != null)
			tipMenu.dispose();
		
		tipMenu = new Menu(parentShell);
		
		for (final IMarker marker : markers) {
			MenuItem item = new MenuItem(tipMenu, SWT.PUSH);
			item.setText(marker.getAttribute(IMarker.MESSAGE, ""));
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					editor.gotoMarker(marker);
				}
				
			});
		}

	} 
	
	/**
	 * Enables customized hover help for a specified control
	 * 
	 * @control the control on which to enable hoverhelp
	 */
	public void activateHoverHelp(final Control control) {
		/*
		 * Get out of the way if we attempt to activate the control underneath
		 * the tooltip
		 */
		control.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (tipMenu != null && tipMenu.isVisible())
					tipMenu.setVisible(false);
			}
		});

		/*
		 * Trap hover events to pop-up tooltip
		 */
		control.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				if (tipMenu != null && tipMenu.isVisible())
					tipMenu.setVisible(false);
				tipWidget = null;
			}

			@Override
			public void mouseHover(MouseEvent event) {
				widgetPosition = new Point(event.x, event.y);
				Widget widget = event.widget;
				if (widget == null) {
					if (tipMenu != null && tipMenu.isVisible())
						tipMenu.setVisible(false);
					tipWidget = null;
					return;
				}
				if (widget == tipWidget)
					return;
				tipWidget = widget;
				if (tipWidget instanceof ImageHyperlink) {
					Object obj = tipWidget.getData();
					if (obj instanceof IRodinElement) {
						IRodinElement element = (IRodinElement) obj;
						createMenu(element);
					}
				}
				tipPosition = control.toDisplay(widgetPosition);
				setMenuLocation(tipMenu, tipPosition);
				tipMenu.setVisible(true);
			}
		});

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
}