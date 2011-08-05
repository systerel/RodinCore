/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - added history support
 *     ETH Zurich - adapted to org.rodinp.keyboard
 *     Systerel - prevented from editing generated elements
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eventb.core.ICommentedElement;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.TimerText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.CommentAttributeManipulation;
import org.rodinp.core.RodinDBException;
import org.rodinp.keyboard.preferences.PreferenceConstants;

public class CommentToolTip {
	Shell parentShell;

	Shell tipShell;

	Label tipLabel;

	Label labelF2;

	Widget tipWidget; // widget this tooltip is hovering over

	protected Point tipPosition; // the position being hovered over on the

	protected Point widgetPosition; // the position hovered over in the Widget;

	Shell helpShell;

	private final static int MAX_WIDTH = 300;

	private final static int MAX_HEIGHT = 120;

	Listener labelListener;

	static final CommentAttributeManipulation FACTORY = new CommentAttributeManipulation();

	/**
	 * Creates a new tooltip handler
	 * 
	 * @param parent
	 *            the parent Shell
	 */
	public CommentToolTip(Shell parent) {
		this.parentShell = parent;

		// Implement a "fake" tooltip
		labelListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					shell.dispose();
					parentShell.setFocus();
					break;
				case SWT.MouseExit:
					shell.dispose();
					break;
				}
			}
		};
	}

	/**
	 * @author htson
	 *         <p>
	 *         This class handles the different changes to the Text.
	 */
	private class TextListener extends TimerText implements Listener,
			ModifyListener {

		private ICommentedElement element;

		private IEventBInputText inputText;

		String original;

		public TextListener(IEventBInputText text, int delay,
				ICommentedElement element) {
			super(text.getTextWidget(), delay);
			this.inputText = text;
			this.element = element;
			try {
				this.original = FACTORY.getValue(element, null);
			} catch (RodinDBException e) {
				this.original = "";
			}
		}

		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.FocusOut:
				response();
				inputText.dispose();
				helpShell.dispose();
				break;
			case SWT.Traverse:
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					UIUtils.setStringAttribute(element, FACTORY, original, null);
					inputText.dispose();
					helpShell.dispose();
					break;
				}
			}
		}

		@Override
		protected void response() {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Set comment for "
						+ element.getElementName());
			final String value = inputText.getTextWidget().getText();
			UIUtils.setStringAttribute(element, FACTORY, value, null);
		}

	}

	
	protected String getToolTipText(ICommentedElement element) {
		String comment;
		try {
			if (! element.hasComment()) {
				return "";
			}
			comment = element.getComment();
		} catch (RodinDBException e) {
			comment = "";
		}
		int i = comment.indexOf('.');
		int j = comment.indexOf('\n');
		if (i == -1) {
			if (j == -1)
				return comment; // One line comment
			else {
				return comment.substring(0, j); // Return the first line
			}
		} else {
			i++;
			if (j == -1)
				return comment.substring(0, i); // Return the first
			// sentence
			else {
				int k = i < j ? i : j; // k is the minimum
				return comment.substring(0, k);
			}
		}

	} // protected Image getToolTipImage(Object object) {

	// if (object instanceof Control) {
	// return (Image) ((Control) object).getData("TIP_IMAGE");
	// }
	// return null;
	// }

	// protected String getToolTipHelp(Object object) {
	// return "Long help for " + object.toString();
	// }

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
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Mouse Down");
				if (tipShell != null) {
					tipShell.dispose();
					tipShell = null;
					tipLabel = null;
				}
			}
		});

		control.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				if (tipShell != null) {
					tipShell.dispose();
					tipShell = null;
				}
				if (helpShell != null) {
					helpShell.dispose();
					helpShell.dispose();
				}
				tipLabel = null;
				return;
			}

		});
		/*
		 * Trap hover events to pop-up tooltip
		 */
		control.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Mouse Exit");
				// if (tipShell != null) {
				// tipShell.dispose();
				// tipShell = null;
				// }
				// tipWidget = null;
			}

			@Override
			public void mouseHover(MouseEvent event) {
				widgetPosition = new Point(event.x, event.y);
				Widget widget = event.widget;
				if (widget instanceof ToolBar) {
					ToolBar w = (ToolBar) widget;
					widget = w.getItem(widgetPosition);
				}
				if (widget instanceof Table) {
					Table w = (Table) widget;
					widget = w.getItem(widgetPosition);
				}
				if (widget instanceof Tree) {
					Tree w = (Tree) widget;
					widget = w.getItem(widgetPosition);
				}
				if (widget == null) {
					// tipShell.setVisible(false);
					tipWidget = null;
					return;
				}
				if (widget == tipWidget)
					return;
				tipWidget = widget;

				if (tipShell != null && !tipShell.isDisposed())
					tipShell.dispose();
				tipShell = new Shell(parentShell, SWT.ON_TOP | SWT.NO_FOCUS
						| SWT.TOOL);
				GridLayout gridLayout = new GridLayout();
				// gridLayout.numColumns = 1;
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				tipShell.setLayout(gridLayout);
				tipShell.setBackground(EventBSharedColor
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

				// FillLayout layout = new FillLayout();
				// layout.marginWidth = 2;
				// tipShell.setLayout(layout);
				tipLabel = new Label(tipShell, SWT.NONE);
				tipLabel.setForeground(EventBSharedColor
						.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				tipLabel.setBackground(EventBSharedColor
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				tipLabel.setLayoutData(new GridData(GridData.FILL_BOTH
						| GridData.VERTICAL_ALIGN_CENTER));
				// Create a new font for this label
				Font font = JFaceResources
						.getFont(PreferenceConstants.RODIN_MATH_FONT);
				tipLabel.setFont(font);

				// tipLabel.setData ("_TABLEITEM", item);
				if (tipWidget instanceof TreeItem) {
					Object obj = tipWidget.getData();
					if (obj instanceof ICommentedElement) {
						ICommentedElement element = (ICommentedElement) obj;
						tipLabel.setText(getToolTipText(element));
					}
				}
				// tipLabel.setText("Test");
				tipLabel.addListener(SWT.MouseExit, labelListener);
				tipLabel.addListener(SWT.MouseDown, labelListener);

				// Create a separator
				Label separator = new Label(tipShell, SWT.SEPARATOR);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_CENTER);
				gd.heightHint = 1;
				separator.setLayoutData(gd);

				// Create the F2 label
				labelF2 = new Label(tipShell, SWT.RIGHT);
				labelF2.setForeground(EventBSharedColor
						.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				labelF2.setBackground(EventBSharedColor
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				labelF2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_CENTER));
				labelF2.setText("Press 'F2' to edit.");

				tipPosition = control.toDisplay(widgetPosition);
				Point shellSize = tipShell
						.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = MAX_WIDTH < shellSize.x ? MAX_WIDTH : shellSize.x;
				Point pt = tipShell.computeSize(width, SWT.DEFAULT);
				int height = MAX_HEIGHT < pt.y ? MAX_HEIGHT : pt.y;
				tipLabel.setSize(width, height);
				tipShell.setSize(width, height);
				// tipLabel.setSize(200, 40);
				if (EventBEditorUtils.DEBUG) {
					EventBEditorUtils.debug("Widget: " + tipWidget);
					EventBEditorUtils
							.debug("WidgetPosition: " + widgetPosition);
					EventBEditorUtils.debug("TipPosition: " + tipPosition);
					EventBEditorUtils.debug("Size: " + width + ", " + height);
				}
				setHoverLocation(tipShell, tipPosition);
				tipShell.setVisible(true);

			}
		});

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

	public void openEditing() {
		ICommentedElement element = null;
		if (tipWidget == null)
			return;
		if (tipWidget instanceof TreeItem) {
			Object obj = tipWidget.getData();
			if (obj instanceof ICommentedElement) {
				element = (ICommentedElement) obj;
			}
		}
		if (tipShell != null && element != null) {
			tipShell.setVisible(false);

			if (helpShell != null)
				helpShell.dispose();

			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Creat editing shell");
			}
			helpShell = new Shell(parentShell, SWT.NONE);
			helpShell.setLayout(new FillLayout());
			helpShell.setSize(400, 200);
			Text text = new Text(helpShell, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			text.setEditable(!isReadOnly(element));
			try {
				if (element.hasComment()) {
					text.setText(element.getComment());
				} else {
					text.setText("");
				}
			} catch (RodinDBException e1) {
				text.setText("");
			}
			text.setSize(400, 200);
			text.setForeground(EventBSharedColor
					.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			text.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
					| GridData.VERTICAL_ALIGN_CENTER));

			TextListener listener = new TextListener(new EventBText(text),
					1000, element);
			text.addListener(SWT.FocusOut, listener);
			text.addListener(SWT.Traverse, listener);
			text.addModifyListener(listener);

			setHoverLocation(helpShell, tipPosition);
			helpShell.open();
		}
	}

}