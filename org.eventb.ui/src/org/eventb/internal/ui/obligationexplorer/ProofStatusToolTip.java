package org.eventb.internal.ui.obligationexplorer;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPSFile;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

public class ProofStatusToolTip {
	Shell parentShell;

	Shell tipShell;

	Label tipLabel;

	Widget tipWidget; // widget this tooltip is hovering over

	protected Point tipPosition; // the position being hovered over on the

	protected Point widgetPosition; // the position hovered over in the Widget;

	Shell helpShell;

	private final static int MAX_WIDTH = 300;

	private final static int MAX_HEIGHT = 150;

	Display display;

	Listener labelListener;

	/**
	 * Creates a new tooltip handler
	 * 
	 * @param parent
	 *            the parent Shell
	 */
	public ProofStatusToolTip(Shell parent) {
		display = parent.getDisplay();
		this.parentShell = parent;

		// Implement a "fake" tooltip
		labelListener = new Listener() {
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

	protected String getToolTipText(IPSFile psFile) {
		ProofStatus proofStatus = new ProofStatus(psFile, true);
		return proofStatus.toString();
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
				if (tipShell != null) {
					tipShell.dispose();
					tipShell = null;
					tipLabel = null;
				}
			}
		});

		control.addMouseMoveListener(new MouseMoveListener() {

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
				 if (tipShell != null) {
					tipShell.dispose();
					tipShell = null;
				}
				tipWidget = null;
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
				tipShell.setBackground(display
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

				// FillLayout layout = new FillLayout();
				// layout.marginWidth = 2;
				// tipShell.setLayout(layout);
				tipLabel = new Label(tipShell, SWT.NONE);
				tipLabel.setForeground(display
						.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				tipLabel.setBackground(display
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				tipLabel.setLayoutData(new GridData(GridData.FILL_BOTH
						| GridData.VERTICAL_ALIGN_CENTER));
				// Create a new font for this label
				Font font = JFaceResources
						.getFont(PreferenceConstants.EVENTB_MATH_FONT);
				tipLabel.setFont(font);

				// tipLabel.setData ("_TABLEITEM", item);
				if (tipWidget instanceof TreeItem) {
					Object obj = tipWidget.getData();
					if (obj instanceof IMachineFile) {
						IPSFile psFile = ((IMachineFile) obj).getPSFile();
						tipLabel.setText(getToolTipText(psFile));
					}
					else if (obj instanceof IContextFile) {
						IPSFile psFile = ((IContextFile) obj).getPSFile();
						tipLabel.setText(getToolTipText(psFile));
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

				tipPosition = control.toDisplay(widgetPosition);
				Point shellSize = tipShell
						.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = MAX_WIDTH < shellSize.x ? MAX_WIDTH : shellSize.x;
				Point pt = tipShell.computeSize(width, SWT.DEFAULT);
				int height = MAX_HEIGHT < pt.y ? MAX_HEIGHT : pt.y;
				tipLabel.setSize(width, height);
				tipShell.setSize(width, height);
				// tipLabel.setSize(200, 40);
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

//	public void openEditing() {
//		ICommentedElement element = null;
//		if (tipWidget == null)
//			return;
//		if (tipWidget instanceof TreeItem) {
//			Object obj = tipWidget.getData();
//			if (obj instanceof ICommentedElement) {
//				element = (ICommentedElement) obj;
//			}
//		}
//		if (tipShell != null) {
//			tipShell.setVisible(false);
//
//			if (helpShell != null)
//				helpShell.dispose();
//
//			helpShell = new Shell(parentShell, SWT.NONE);
//			helpShell.setLayout(new FillLayout());
//			helpShell.setSize(400, 200);
//			Text text = new Text(helpShell, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
//
//			try {
//				if (element.hasComment()) {
//					text.setText(element.getComment());
//				} else {
//					text.setText("");
//				}
//			} catch (RodinDBException e1) {
//				text.setText("");
//			}
//			text.setSize(400, 200);
//			text.setForeground(display
//					.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
//			text.setBackground(display
//					.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
//			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
//					| GridData.VERTICAL_ALIGN_CENTER));
//
//			setHoverLocation(helpShell, tipPosition);
//			helpShell.open();
//		}
//	}

}