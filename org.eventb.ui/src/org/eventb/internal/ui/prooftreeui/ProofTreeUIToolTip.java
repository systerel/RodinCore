package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.IEventBInputText;

public class ProofTreeUIToolTip {
	private Shell parentShell;

	private Shell tipShell;

	private Label tipLabel;

	private Label labelF2;

	private Widget tipWidget; // widget this tooltip is hovering over

	protected Point tipPosition; // the position being hovered over on the

	protected Point widgetPosition; // the position hovered over in the Widget;

	private UserSupport userSupport;

	private Shell helpShell;

	private final static int MAX_WIDTH = 300;
	
	private final static int MAX_HEIGHT = 120;

	/**
	 * Creates a new tooltip handler
	 * 
	 * @param parent
	 *            the parent Shell
	 */
	public ProofTreeUIToolTip(Shell parent, UserSupport US) {
		final Display display = parent.getDisplay();
		this.parentShell = parent;
		this.userSupport = US;

		// Tip shell
		tipShell = new Shell(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		tipShell.setLayout(gridLayout);
		tipShell.setBackground(display
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		tipShell.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// Do nothing
			}

			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.F2) {
					IProofTreeNode node = null;
					if (tipWidget == null)
						return;
					if (tipWidget instanceof TreeItem) {
						Object obj = tipWidget.getData();
						if (obj instanceof IProofTreeNode) {
							node = (IProofTreeNode) obj;
						}
					}
					if (tipShell.isVisible()) {
						tipShell.setVisible(false);
						Display display = parentShell.getDisplay();
						helpShell = new Shell(parentShell, SWT.NONE);
						helpShell.setLayout(new FillLayout());
						helpShell.setSize(300, 100);
						Text text = new Text(helpShell, SWT.MULTI
								| SWT.WRAP | SWT.V_SCROLL);

						text.setText(node.getComment());
						text.setSize(200, 100);
						text.setForeground(display
								.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
						text.setBackground(display
								.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						text.setLayoutData(new GridData(
								GridData.FILL_HORIZONTAL
										| GridData.VERTICAL_ALIGN_CENTER));

						TextListener listener = new TextListener(new EventBMath(text), node);
						text.addListener(SWT.FocusOut, listener);
						text.addListener(SWT.Traverse, listener);
						text.addModifyListener(listener);

						setHoverLocation(helpShell, tipPosition);
						helpShell.open();
					}

				}
			}

		});

		// Tip label
		tipLabel = new Label(tipShell, SWT.LEFT | SWT.WRAP);
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

		// Create a separator
		Label separator = new Label(tipShell, SWT.SEPARATOR);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER);
		gd.heightHint = 1;
		separator.setLayoutData(gd);

		// Create the F2 label
		labelF2 = new Label(tipShell, SWT.RIGHT);
		labelF2
				.setForeground(display
						.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		labelF2
				.setBackground(display
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		labelF2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		labelF2.setText("Press 'F2' to edit.");

	}

	/**
	 * @author htson
	 *         <p>
	 *         This class handles the different changes to the Text.
	 */
	private class TextListener implements Listener, ModifyListener {

		private IProofTreeNode node;

		private IEventBInputText text;

		String original;

		private int lastModify;

		/**
		 * @author htson
		 *         <p>
		 *         This class implements the auto commit behaviour of the text.
		 */
		private class TimeRunnable implements Runnable {
			private int time;

			TimeRunnable(int time) {
				this.time = time;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				if (lastModify == time) {
					Text textWidget = text.getTextWidget();
					if (!textWidget.isDisposed()) {
						userSupport.setComment(textWidget.getText(), node);
					}
				}
			}
		}

		public TextListener(IEventBInputText text, IProofTreeNode node) {
			this.text = text;
			this.node = node;
			original = node.getComment();
		}

		public void handleEvent(Event event) {
			// TODO Auto-generated method stub
			switch (event.type) {
			case SWT.FocusOut:
				userSupport.setComment(text.getTextWidget().getText(), node);
				text.dispose();
				helpShell.dispose();
				break;
			case SWT.Traverse:
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					userSupport.setComment(original, node);
					text.dispose();
					helpShell.dispose();
					break;
				}
			}
		}

		public void modifyText(ModifyEvent e) {
			lastModify = e.time;
			text.getTextWidget().getDisplay().timerExec(1000, new TimeRunnable(e.time));
		}
	}

	protected String getToolTipText(IProofTreeNode node) {
		String comments = node.getComment();
		int i = comments.indexOf('.');
		int j = comments.indexOf('\n');
		if (i == -1) {
			if (j == -1)
				return comments; // One line comments
			else {
				return comments.substring(0, j); // Return the first line
			}
		} else {
			i++;
			if (j == -1)
				return comments.substring(0, i); // Return the first
			// sentence
			else {
				int k = i < j ? i : j; // k is the minimum
				return comments.substring(0, k);
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
			public void mouseDown(MouseEvent e) {
				if (tipShell.isVisible())
					tipShell.setVisible(false);
			}
		});

		/*
		 * Trap hover events to pop-up tooltip
		 */
		control.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseExit(MouseEvent e) {
				if (tipShell.isVisible())
					tipShell.setVisible(false);
				tipWidget = null;
			}

			public void mouseHover(MouseEvent event) {
				widgetPosition = new Point(event.x, event.y);
				Widget widget = event.widget;
				// if (widget instanceof ToolBar) {
				// ToolBar w = (ToolBar) widget;
				// widget = w.getItem(widgetPosition);
				// }
				// if (widget instanceof Table) {
				// Table w = (Table) widget;
				// widget = w.getItem(widgetPosition);
				// }
				if (widget instanceof Tree) {
					Tree w = (Tree) widget;
					widget = w.getItem(widgetPosition);
				}
				if (widget == null) {
					tipShell.setVisible(false);
					tipWidget = null;
					return;
				}
				if (widget == tipWidget)
					return;
				tipWidget = widget;
				if (tipWidget instanceof TreeItem) {
					Object obj = tipWidget.getData();
					if (obj instanceof IProofTreeNode) {
						IProofTreeNode node = (IProofTreeNode) obj;
						String text = getToolTipText(node);
						tipLabel.setText(text);
					}
				}
				tipPosition = control.toDisplay(widgetPosition);
				tipShell.pack();
				Point shellSize = tipShell.getSize();
				int width = MAX_WIDTH < shellSize.x ? MAX_WIDTH
						: shellSize.x;
				Point pt = tipShell.computeSize(width, SWT.DEFAULT);
				tipLabel.setSize(width, 40);
				int height = MAX_HEIGHT < pt.y ? MAX_HEIGHT : pt.y;
				tipShell.setSize(width, height);
				setHoverLocation(tipShell, tipPosition);
				tipShell.setVisible(true);
				tipShell.setFocus(); // Focus on the shell
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
	 * @return the top-left location for a hovering box
	 */
	private void setHoverLocation(Shell shell, Point position) {
		Rectangle displayBounds = shell.getDisplay().getBounds();
		Rectangle shellBounds = shell.getBounds();
		shellBounds.x = Math.max(Math.min(position.x, displayBounds.width
				- shellBounds.width), 0);
		shellBounds.y = Math.max(Math.min(position.y + 16, displayBounds.height
				- shellBounds.height), 0);
		shell.setBounds(shellBounds);
	}

}